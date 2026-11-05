'use strict';
// Functional check that "!pragma layout elk" works in the browser (TeaVM) engine.
//
// usage: node check-elk.js target=<dir-or-js>
//   target   a directory containing plantuml.js, elk.bundled.js and viz-global.js,
//            or a path to the engine .js file itself
//
// The browser build lays ELK diagrams out with elkjs (elk.bundled.js, the
// JavaScript build of the Eclipse Layout Kernel), through ElkEngineJs. Before,
// the pragma was ignored there: every diagram went through Viz.js or Smetana.
// The ELK graph code itself is checked on the JVM (ElkJsonGraphTest, and the
// SVG of the elkjs path is the same as the one of ELK Java); this pins the
// browser plumbing:
//
// - with the pragma, Graphviz-family diagrams are laid out by elkjs although
//   viz-global.js is on the page, and the drawing differs from the Graphviz one;
//   without it, elkjs is not called;
// - elk.bundled.js is fetched on demand: not by a page that never asks for ELK,
//   and once for all the ELK diagrams of a page;
// - when elk.bundled.js cannot be loaded, the diagram still renders, with the
//   default engine, and the console says why;
// - a host that provides elkjs itself (globalThis.PLANTUML_ELK) needs no
//   elk.bundled.js at all.
const fs = require('fs');
const path = require('path');
const { createCheckReporter, isErrorImage } = require('../lib/browser-check');
const { parseTargetArg } = require('../lib/browser-cli');
const { createMountedServer, startServer } = require('../lib/browser-http');
const { createModulePageHtml, loadPlaywright, makeRenderModuleBody, openReadyPage, renderOn } = require('../lib/browser-page');

const scriptName = path.basename(__filename, '.js');
const { dir, file } = parseTargetArg(process.argv, `node ${scriptName}.js target=<dir-or-js>`);
const pw = loadPlaywright();

for (const needed of ['viz-global.js', 'elk.bundled.js'])
  if (!fs.existsSync(path.join(dir, needed))) {
    console.error(needed + ' not found next to the engine in ' + dir);
    process.exit(2);
  }

// Counts WebAssembly instantiations: Viz.js is WebAssembly, elkjs is not.
const hook = `<script>
window.__wasm = 0;
['compile','instantiate','instantiateStreaming','compileStreaming'].forEach(function (k) {
  var o = WebAssembly[k];
  if (o) WebAssembly[k] = function () { window.__wasm++; return o.apply(WebAssembly, arguments); };
});
</script>`;

const pageHtml = extraHead => createModulePageHtml({
  headHtml: hook + (extraHead || ''),
  bodyHtml: '<script src="/viz-global.js"></script>',
  modulePath: `/${file}`,
  moduleBody: makeRenderModuleBody({ maxSvgSize: 98304 }),
});

// Every request path, to see which scripts the engine fetched
const requests = [];
const elkFetches = () => requests.filter(p => p.endsWith('/elk.bundled.js')).length;

const elkSource = fs.readFileSync(path.join(dir, 'elk.bundled.js'));
const server = createMountedServer({
  onRequest: requestPath => requests.push(requestPath),
  routes: {
    '/index.html': { contentType: 'text/html', body: pageHtml() },
    // elk.bundled.js resolved under a prefix where it does not exist
    '/index-no-elk.html': { contentType: 'text/html', body: pageHtml('<script>globalThis.PLANTUML_STDLIB_BASE = "/missing/";</script>') },
    // The host loads elkjs itself, from its own URL, and hands it over
    '/index-host-elk.html': {
      contentType: 'text/html',
      body: pageHtml('<script src="/host/elk.js"></script><script>globalThis.PLANTUML_ELK = ELK; delete window.ELK; globalThis.PLANTUML_STDLIB_BASE = "/missing/";</script>'),
    },
    '/host/elk.js': { contentType: 'application/javascript', body: elkSource },
  },
  mounts: [{ prefix: '/', dir }],
});

const { check, finish } = createCheckReporter();

const FAMILIES = [
  ['class', ['class Car {', '  +drive(): void', '}', 'class Engine', 'class Wheel', 'Car *-- "1" Engine : has >', 'Car o-- "4" Wheel']],
  ['component', ['[Web UI] --> [API Gateway] : REST', '[Mobile App] --> [API Gateway]', '[API Gateway] --> [Orders]']],
  ['state', ['[*] --> Idle', 'Idle --> Running : start', 'Running --> Idle : stop', 'Running --> [*]']],
  ['usecase', ['actor User', 'User --> (Login)', 'User --> (Browse)', '(Browse) --> (Checkout)']],
  // Clusters: edges contained in a cluster are relative to it, edges between
  // clusters to their common ancestor
  ['nested packages', ['package model {', '  class User', '  class Order', '  package detail {', '    class Line', '  }', '}',
    'class Main', 'Main --> Order : uses', 'Order *-- Line', 'Line ..> User', 'User --> Order']],
  ['composite state', ['[*] --> Working', 'state Working {', '  [*] --> Fetching', '  Fetching --> Parsing : done', '}', 'Working --> [*] : shutdown']],
  ['edge to a package', ['package P {', '  class A', '  class B', '}', 'class C', 'C --> P', 'A --> B']],
  // Ports: ElkPort in the JSON graph, edges from and to ports
  ['component ports', ['component Board {', '  port tx', '  port rx', '}', 'component Sensor {', '  port in', '}',
    'tx --> in', 'Sensor --> rx']],
];
const diagram = (body, pragma) => ['@startuml', ...(pragma ? ['!pragma layout elk'] : []), ...body, '@enduml'];

// How many requests ElkEngineJs has sent to elkjs on this page (its state lives
// on globalThis.__plantumlElk). WebAssembly counts cannot tell: Viz.js is only
// instantiated once per page, so later renders through it use none.
const elkCalls = page => page.evaluate(() => (globalThis.__plantumlElk ? globalThis.__plantumlElk.nextId : 0));
async function renderCounting(page, lines) {
  const before = await elkCalls(page);
  const r = await renderOn(page, lines, RENDER);
  r.elk = (await elkCalls(page)) - before;
  return r;
}

const renderOk = r => !r.thrown && !!r.svg && !isErrorImage(r.svg) && r.shapes > 0 && r.texts > 0;
const why = r => r.thrown || (!r.svg ? 'no svg: ' + r.text.slice(0, 160)
  : isErrorImage(r.svg) ? 'error image: ' + r.text.slice(0, 160)
  : 'svg but no drawn content (shapes=' + r.shapes + ' texts=' + r.texts + ')');
const RENDER = { includeWasmCount: true, includeShapeCounts: true, includeHash: true, maxTextLength: 400, timeoutMs: 60000 };

(async () => {
  const port = await startServer(server);
  const browser = await pw.chromium.launch({ headless: true });

  // Page 1: a page that never asks for ELK does not fetch elkjs
  {
    requests.length = 0;
    const { page, errors } = await openReadyPage(browser, `http://127.0.0.1:${port}/index.html`, { polling: 200 });
    const r = await renderCounting(page, diagram(FAMILIES[0][1], false));
    check('control: class diagram without the pragma renders through the Graphviz bridge', renderOk(r) && r.wasm > 0 && r.elk === 0,
      !renderOk(r) ? why(r) : r.elk !== 0 ? 'elkjs called' : 'no WebAssembly used (' + r.wasm + ')');
    check('a page that never asks for ELK does not fetch `elk.bundled.js`', elkFetches() === 0, elkFetches() + ' fetches');
    check('no unhandled page errors on the control page', errors.length === 0, errors.join(' | '));
    await page.close();
  }

  // Page 2: the pragma lays out with elkjs, which is fetched once
  {
    requests.length = 0;
    const warnings = [];
    const { page, errors } = await openReadyPage(browser, `http://127.0.0.1:${port}/index.html`, {
      polling: 200,
      onConsole: msg => { if (msg.type() === 'warning' || msg.type() === 'error') warnings.push(msg.text()); },
    });
    for (const [label, body] of FAMILIES) {
      const r = await renderCounting(page, diagram(body, true));
      check(`elk ${label} diagram is laid out by elkjs`, renderOk(r) && r.elk > 0,
        renderOk(r) ? 'elkjs not called' : why(r));
    }
    check('`elk.bundled.js` is fetched once for all the ELK diagrams of a page', elkFetches() === 1, elkFetches() + ' fetches');

    const viaElk = await renderCounting(page, diagram(FAMILIES[4][1], true));
    const viaViz = await renderCounting(page, diagram(FAMILIES[4][1], false));
    check('the ELK drawing differs from the Graphviz one', renderOk(viaElk) && renderOk(viaViz) && viaElk.sha !== viaViz.sha,
      viaElk.sha === viaViz.sha ? 'same svg: the pragma changed nothing' : why(renderOk(viaElk) ? viaViz : viaElk));
    check('without the pragma, the same page does not call elkjs', viaViz.elk === 0, viaViz.elk + ' calls');
    const again = await renderCounting(page, diagram(FAMILIES[4][1], true));
    check('rendering the same ELK diagram twice gives the same svg', again.sha === viaElk.sha, 'svg changed');

    check('no elkjs warning when elkjs is there', warnings.filter(w => /elk/i.test(w)).length === 0, warnings.join(' | '));
    check('no unhandled page errors on the ELK page', errors.length === 0, errors.join(' | '));
    await page.close();
  }

  // Page 3: elk.bundled.js cannot be loaded, the default engine takes over
  {
    requests.length = 0;
    const warnings = [];
    const { page, errors } = await openReadyPage(browser, `http://127.0.0.1:${port}/index-no-elk.html`, {
      polling: 200,
      onConsole: msg => { if (msg.type() === 'warning') warnings.push(msg.text()); },
    });
    const r = await renderCounting(page, diagram(FAMILIES[0][1], true));
    check('without elkjs, an ELK diagram still renders, with the default engine', renderOk(r) && r.wasm > 0 && r.elk === 0,
      !renderOk(r) ? why(r) : r.elk !== 0 ? 'elkjs called' : 'no WebAssembly used: not the default engine');
    check('without elkjs, the elkjs fetch was attempted', requests.includes('/missing/elk.bundled.js'), requests.join(', '));
    check('without elkjs, the console says why', warnings.some(w => /elk/i.test(w) && /default layout engine/.test(w)),
      warnings.join(' | ') || 'no warning');
    check('no unhandled page errors on the page without elkjs', errors.length === 0, errors.join(' | '));
    await page.close();
  }

  // Page 4: the host provides elkjs (PLANTUML_ELK), elk.bundled.js is not needed
  {
    requests.length = 0;
    const { page, errors } = await openReadyPage(browser, `http://127.0.0.1:${port}/index-host-elk.html`, { polling: 200 });
    const r = await renderCounting(page, diagram(FAMILIES[4][1], true));
    check('a host-provided `PLANTUML_ELK` lays ELK diagrams out', renderOk(r) && r.elk > 0,
      renderOk(r) ? 'elkjs not called' : why(r));
    check('with `PLANTUML_ELK`, `elk.bundled.js` is never fetched', elkFetches() === 0, requests.join(', '));
    check('no unhandled page errors on the host-provided page', errors.length === 0, errors.join(' | '));
    await page.close();
  }

  await browser.close();
  server.close();
  finish(scriptName, { uppercase: true });
})().catch(e => { console.error(e); process.exit(2); });
