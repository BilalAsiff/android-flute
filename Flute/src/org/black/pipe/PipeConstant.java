package org.black.pipe;

/**
 * Interface to declare Global constant values.
 * 
 * @author black
 * 
 */
public interface PipeConstant {
    final String APP_TAG = "org.black.Pipe";

    final int[] NOTE_VALUES = { 72, 74, 76, 77, 79, 81, 83, 84 };
    
    final int[] BLOW_PRESSURES = {83, 71, 61};
    final int DEFAULT_MIN_BLOW_PRESSURE = BLOW_PRESSURES[1];

    final String SHARED_PERFERENCE = "pipe.sharedPreference";
    final String INSTRUMENT_NUMBER = "Instrument_number";
    final String HOLE_NUMBER = "Hole_number";
    final String BLOW_PRESSURE_THRESHOLD = "BLOW_pressure_threshold";

    final int[] MIDI_PIPE_INSTRUMENT_NUMBERT = { 74, 76, 79 };
    final int DEFAULT_MIDI_PIPE_INSTRUMENT_NUMBERT = MIDI_PIPE_INSTRUMENT_NUMBERT[0];
    final String[] MIDI_PIPE_INSTRUMENT_NAME = { "Flute", "Pan Flute",
            "Ocarina" };
    final String DEFAULT_MIDI_PIPE_INSTRUMENT_NAME = MIDI_PIPE_INSTRUMENT_NAME[0];

    final Integer[] INSTRUMENT_HOLE_NUMBER = { 2, 3 };
    final int DEFAULT_INSTRUMENT_HOLE_NUMBER = INSTRUMENT_HOLE_NUMBER[0];
}
