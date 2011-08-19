package org.black.pipe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class to create Midi file This class reference thw following resources:
 * http://www.omega-art.com/midi/mfiles.html
 * http://kevinboone.net/javamidi.html
 * http://dogsbodynet.com/fileformats/midi.html
 * 
 */
public class MidiMaker {
    // Midi file header, MThd widh chunk length, chunk length is always 6
    private final static byte[] HEADER = { 0x4d, 0x54, 0x68, 0x64, 0x00, 0x00,
            0x00, 0x06 };
    // Midi format
    private final static byte[] FORMAT = { 0x00, 0x00 };
    // track number, 1 track
    private final static byte[] TRACK = { 0x00, 0x01 };
    // quarter note, 96å
    private final static byte[] QUARTER_NOTE = { 0x00, (byte) 0x10 };
    // MTrk
    private final static byte[] MTRK = { 0x4d, 0x54, 0x72, 0x6b };
    // Footer
    private final static byte[] FOOTER = { 0x01, (byte) 0xff, 0x2f, 0x00 };

    private final static int NOTE_ON = 0x90;
    private final static int NOTE_OFF = 0x80;
    private final static int PROGRAM_CHANGE = 0xc0;

    private ByteArrayOutputStream events;

    public MidiMaker() {
        this.events = new ByteArrayOutputStream();
    }

    public void programChange(int instrument) {
        this.addByte(0);
        this.addByte(PROGRAM_CHANGE);
        this.addByte(instrument);
    }

    public void noteOn(int delta, int note, int velocity) {
        this.addByte(delta);
        this.addByte(NOTE_ON);
        this.addByte(note);
        this.addByte(velocity);
    }

    public void noteOff(int delta, int note) {
        this.addByte(delta);
        this.addByte(NOTE_OFF);
        this.addByte(note);
        this.addByte(0);
    }

    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(HEADER);
        outputStream.write(FORMAT);
        outputStream.write(TRACK);
        outputStream.write(QUARTER_NOTE);
        outputStream.write(MTRK);
        int size = this.events.size() + FOOTER.length;
        outputStream.write((byte) (size >>> 24));
        outputStream.write((byte) (size >>> 16));
        outputStream.write((byte) (size >>> 8));
        outputStream.write((byte) size);
        outputStream.write(this.events.toByteArray());
        outputStream.write(FOOTER);
    }

    private void addByte(int value) {
        this.events.write((byte) value);
    }
}
