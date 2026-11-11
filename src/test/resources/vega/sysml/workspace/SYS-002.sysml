package 'SYS-002' {
    private import 'TRK-STK'::*;

    requirement <'SYS-002'> 'Dry boil protection' {
        doc /* The kettle shall switch off within 5 s when it is empty. */
    }

    dependency from 'SYS-002' to 'STK-001';
}
