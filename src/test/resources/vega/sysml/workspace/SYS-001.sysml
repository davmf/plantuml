package 'SYS-001' {
    private import 'TRK-STK'::*;

    requirement <'SYS-001'> 'Boil time' {
        doc /*
         * The kettle shall boil 1 litre in at most 180 s.
         *
         * .. image:: _assets/boil-curve.svg
         *    :alt: Temperature against time
         *
         * Measured at 230 V.
         */
        attribute boilTime;
        require constraint { boilTime <= 180 }
    }

    dependency from 'SYS-001' to 'STK-001';
}
