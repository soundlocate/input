package de.sfn_kassel.sound_record;

import de.sfn_kassel.sound_record.in.AudioInput;
import de.sfn_kassel.sound_record.out.SoundServer;

import java.io.IOException;

/**
 * Created by robin on 12/23/15.
 */
public class SoundRecord {
    private SoundServer server;
    private AudioInput audioInput;
    private int blockSize = 9600;

    public SoundRecord(int port, int numMics) throws IOException {
        audioInput = new AudioInput(numMics, "pulse", 192000, 4);
        server = new SoundServer(port, numMics);
    }

    public void startRecording() throws IOException {
        while (true) {
            server.sendSamples(audioInput.getSamples());
        }
    }

    public static void main(String args[]) throws IOException {
        new SoundRecord(32121, 4).startRecording();
    }
}
