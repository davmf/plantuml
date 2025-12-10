/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 *
 * If you like this project or if you find it useful, you can support us at:
 *
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 * Contributor:      David Fyfe
 *
 */
package net.sourceforge.plantuml.tim.builtin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.FileSystem;
import net.sourceforge.plantuml.FileUtils;
import net.sourceforge.plantuml.json.JsonArray;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.security.SURL;
import net.sourceforge.plantuml.text.StringLocated;
import net.sourceforge.plantuml.tim.EaterException;
import net.sourceforge.plantuml.tim.TContext;
import net.sourceforge.plantuml.tim.TFunctionSignature;
import net.sourceforge.plantuml.tim.TMemory;
import net.sourceforge.plantuml.tim.expression.TValue;

/**
 * Loads CSV data from file or URL source and provides two processing modes.
 * <p>
 * Mode 1 - Columns (%false() or default): Returns data organized by columns
 * <pre>
 * {
 *   "column1": [value1, value2, ...],
 *   "column2": [value1, value2, ...]
 * }
 * </pre>
 * <p>
 * Mode 2 - Pairs (%true()): Returns data as coordinate pairs using first column as X
 * <pre>
 * {
 *   "column2": [(x1, y1), (x2, y2), ...],
 *   "column3": [(x1, z1), (x2, z2), ...]
 * }
 * </pre>
 * <p>
 * Examples:<br/>
 *
 * <pre>
 *     &#64;startuml
 *     ' Load CSV in columns mode (default)
 *     !$data = %load_csv("data.csv")
 *     !$time = $data.time_ms
 *     !$voltage = $data.voltage_mV
 *
 *     ' Load CSV in pairs mode for chart use
 *     !$data_pairs = %load_csv("data.csv", %true())
 *     line "Voltage" $data_pairs.voltage_mV #3498db
 *
 *     ' Load CSV with specific charset
 *     !$data_utf16 = %load_csv("data.csv", %false(), "UTF-16")
 *     &#64;enduml
 * </pre>
 *
 * @author David Fyfe
 */
public class LoadCsv extends SimpleReturnFunction {

	private static final TFunctionSignature SIGNATURE = new TFunctionSignature("%load_csv", 3);

	private static final String VALUE_CHARSET_DEFAULT = "UTF-8";

	public TFunctionSignature getSignature() {
		return SIGNATURE;
	}

	@Override
	public boolean canCover(int nbArg, Set<String> namedArgument) {
		return nbArg == 1 || nbArg == 2 || nbArg == 3;
	}

	@Override
	public TValue executeReturnFunction(TContext context, TMemory memory, StringLocated location, List<TValue> values,
			Map<String, TValue> named) throws EaterException {
		final String path = values.get(0).toString();
		final boolean pairsMode = values.size() > 1 && values.get(1).toBoolean();
		final String charset = getCharset(values);

		try {
			String data = loadStringData(path, charset);
			if (data == null || data.isEmpty())
				return TValue.fromJson(new JsonObject());

			return parseCsvData(data, pairsMode, location);
		} catch (UnsupportedEncodingException e) {
			Logme.error(e);
			throw new EaterException("CSV encoding issue in source " + path + ": " + e.getMessage(), location);
		} catch (Exception e) {
			Logme.error(e);
			throw new EaterException("CSV parse error in source " + path + ": " + e.getMessage(), location);
		}
	}

	/**
	 * Parse CSV data based on the specified mode
	 */
	private TValue parseCsvData(String data, boolean pairsMode, StringLocated location) throws EaterException {
		String[] lines = data.split("\r?\n");
		if (lines.length == 0)
			return TValue.fromJson(new JsonObject());

		// Parse header line
		String[] headers = parseCSVLine(lines[0]);
		if (headers.length == 0)
			return TValue.fromJson(new JsonObject());

		// Initialize data structures
		List<List<JsonValue>> columns = new ArrayList<>();
		for (int i = 0; i < headers.length; i++) {
			columns.add(new ArrayList<JsonValue>());
		}

		// Parse data rows
		for (int lineNum = 1; lineNum < lines.length; lineNum++) {
			String line = lines[lineNum].trim();
			if (line.isEmpty())
				continue;

			String[] fields = parseCSVLine(line);
			for (int col = 0; col < fields.length && col < headers.length; col++) {
				JsonValue value = parseValue(fields[col].trim());
				columns.get(col).add(value);
			}

			// Fill missing columns with null
			for (int col = fields.length; col < headers.length; col++) {
				columns.get(col).add(JsonValue.NULL);
			}
		}

		// Build result based on mode
		if (pairsMode) {
			return buildPairsResult(headers, columns);
		} else {
			return buildColumnsResult(headers, columns);
		}
	}

	/**
	 * Build result in columns mode
	 */
	private TValue buildColumnsResult(String[] headers, List<List<JsonValue>> columns) {
		JsonObject result = new JsonObject();

		for (int i = 0; i < headers.length; i++) {
			JsonArray columnArray = new JsonArray();
			for (JsonValue value : columns.get(i)) {
				columnArray.add(value);
			}
			result.add(headers[i].trim(), columnArray);
		}

		return TValue.fromJson(result);
	}

	/**
	 * Build result in pairs mode (first column as X coordinate)
	 * Format output for direct use in PlantUML charts
	 */
	private TValue buildPairsResult(String[] headers, List<List<JsonValue>> columns) {
		JsonObject result = new JsonObject();

		if (columns.size() < 2) {
			return TValue.fromJson(result);
		}

		List<JsonValue> xValues = columns.get(0);

		// For each column after the first, create coordinate pairs
		for (int col = 1; col < headers.length; col++) {
			JsonArray pairsArray = new JsonArray();
			List<JsonValue> yValues = columns.get(col);

			// Build the array as a string that matches PlantUML chart format
			StringBuilder chartData = new StringBuilder("[");
			for (int row = 0; row < xValues.size(); row++) {
				if (row > 0) {
					chartData.append(",");
				}
				chartData.append("(");
				chartData.append(formatNumber(xValues.get(row)));
				chartData.append(",");
				if (row < yValues.size()) {
					chartData.append(formatNumber(yValues.get(row)));
				} else {
					chartData.append("0");
				}
				chartData.append(")");
			}
			chartData.append("]");

			// Store as a single string that will expand correctly in charts
			result.add(headers[col].trim(), chartData.toString());
		}

		return TValue.fromJson(result);
	}

	/**
	 * Format a JsonValue as a number string for chart compatibility
	 */
	private String formatNumber(JsonValue value) {
		if (value.isNull()) {
			return "0";
		}
		if (value.isNumber()) {
			// Format number without scientific notation
			double d = value.asDouble();
			if (d == (long) d) {
				return String.valueOf((long) d);
			} else {
				return String.valueOf(d);
			}
		}
		return value.asString();
	}

	/**
	 * Parse a CSV line handling quoted fields
	 */
	private String[] parseCSVLine(String line) {
		List<String> fields = new ArrayList<>();
		StringBuilder currentField = new StringBuilder();
		boolean inQuotes = false;
		boolean fieldStarted = false;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			char next = i + 1 < line.length() ? line.charAt(i + 1) : '\0';

			if (!fieldStarted) {
				fieldStarted = true;
				if (c == '"') {
					inQuotes = true;
					continue;
				}
			}

			if (inQuotes) {
				if (c == '"') {
					if (next == '"') {
						// Escaped quote
						currentField.append('"');
						i++; // Skip next quote
					} else {
						// End of quoted field
						inQuotes = false;
					}
				} else {
					currentField.append(c);
				}
			} else {
				if (c == ',') {
					fields.add(currentField.toString());
					currentField.setLength(0);
					fieldStarted = false;
				} else if (c != '"') {
					currentField.append(c);
				}
			}
		}

		// Add the last field
		fields.add(currentField.toString());

		return fields.toArray(new String[0]);
	}

	/**
	 * Parse a value as number if possible, otherwise as string
	 */
	private JsonValue parseValue(String value) {
		if (value == null || value.isEmpty() || "null".equalsIgnoreCase(value)) {
			return JsonValue.NULL;
		}

		// Try to parse as number
		try {
			if (value.contains(".")) {
				double d = Double.parseDouble(value);
				return JsonValue.valueOf(d);
			} else {
				long l = Long.parseLong(value);
				return JsonValue.valueOf(l);
			}
		} catch (NumberFormatException e) {
			// Not a number, treat as string
			return JsonValue.valueOf(value);
		}
	}

	/**
	 * Returns the charset name (if set)
	 */
	private String getCharset(List<TValue> values) {
		if (values.size() >= 3) {
			return values.get(2).toString();
		}
		return VALUE_CHARSET_DEFAULT;
	}

	/**
	 * Loads String data from a data source (file or URL)
	 */
	private String loadStringData(String path, String charset) throws EaterException, UnsupportedEncodingException {
		byte[] byteData = null;

		if (path.startsWith("http://") || path.startsWith("https://")) {
			final SURL url = SURL.create(path);
			if (url != null) {
				byteData = url.getBytes();
			}
		} else {
			try {
				final SFile file = FileSystem.getInstance().getFile(path);
				if (file != null && file.exists() && file.canRead() && !file.isDirectory()) {
					final ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 8);
					FileUtils.copyToStream(file, out);
					byteData = out.toByteArray();
				}
			} catch (IOException e) {
				Logme.error(e);
			}
		}

		if (byteData == null || byteData.length == 0) {
			return null;
		}

		return new String(byteData, charset);
	}
}