package org.black.pipe;

public interface PipeConstant {
    String APP_TAG = "org.black.Pipe";

    int[] NOTE_VALUES = { 72, 74, 76, 77 };

    int MIN_AUDIO_PRESSURE = 43;

    String SHARED_PERFERENCE = "pipe.sharedPreference";
    String INSTRUMENT_NUMBER = "Instrument_number";

    int[] MIDI_PIPE_INSTRUMENT_NUMBERT = { 72, 73, 74, 79 };
    int DEFAULT_MIDI_PIPE_INSTRUMENT_NUMBERT = MIDI_PIPE_INSTRUMENT_NUMBERT[1];
    String[] MIDI_PIPE_INSTRUMENT_NAME = { "Picclo", "Flute", "Recorder",
            "Ocarina" };
    String DEFAULT_MIDI_PIPE_INSTRUMENT_NAME = MIDI_PIPE_INSTRUMENT_NAME[1];
}
