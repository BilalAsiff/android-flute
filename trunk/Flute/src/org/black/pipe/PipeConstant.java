package org.black.pipe;

/**
 * Interface to declare Global constant values.
 * @author black
 *
 */
public interface PipeConstant {
    String APP_TAG = "org.black.Pipe";

    int[] NOTE_VALUES = { 72, 74, 76, 77 };

    int MIN_AUDIO_PRESSURE = 43;

    String SHARED_PERFERENCE = "pipe.sharedPreference";
    String INSTRUMENT_NUMBER = "Instrument_number";

    int[] MIDI_PIPE_INSTRUMENT_NUMBERT = { 74, 76, 79 };
    int DEFAULT_MIDI_PIPE_INSTRUMENT_NUMBERT = MIDI_PIPE_INSTRUMENT_NUMBERT[0];
    String[] MIDI_PIPE_INSTRUMENT_NAME = { "Flute", "Pan Flute",
            "Ocarina" };
    String DEFAULT_MIDI_PIPE_INSTRUMENT_NAME = MIDI_PIPE_INSTRUMENT_NAME[0];
}
