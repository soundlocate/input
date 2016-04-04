package de.sfn_kassel.sound_record;

import de.sfn_kassel.sound_record.in.AudioInput;
import de.sfn_kassel.sound_record.out.SoundServer;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by robin on 12/23/15.
 * the main Class
 */
public class SoundRecord {
    private SoundServer server;
    private AudioInput audioInput;
    private static Options options;

    public SoundRecord(int port, int numMics, int samplerate, String devicename) throws IOException {
        audioInput = new AudioInput(numMics, devicename, samplerate, 4);
        server = new SoundServer(port, numMics);
    }

    public void startRecording() throws IOException {
        while (true) {
            server.sendSamples(audioInput.getSamples());
        }
    }

    public static void main(String args[]) throws IOException {
        options = new Options();
        options.addOption("s", "samplerate", true, "the Samplerate");
        options.addOption("d", "device", true, "the device to use");
        options.addOption("n", "numMics", true, "the number of channels to record");
        options.addOption("o", "outputPort", true, "the Output Port");

        int numMics = 0, samplerate = 0, port = 0;
        String device = "";

        try {
            CommandLine cmd = new DefaultParser().parse(options, Arrays.copyOfRange(args, 1, args.length));
            if (cmd.hasOption("help")) {
                printHelp();
                System.exit(0);
            } else if (cmd.hasOption('s') && cmd.hasOption('d') && cmd.hasOption('n')) {
                numMics = Integer.parseInt(cmd.getOptionValue('n'));
                samplerate = Integer.parseInt(cmd.getOptionValue('s'));
                device = cmd.getOptionValue('d');
                port = Integer.parseInt(cmd.getOptionValue('o'));
            } else {
                System.err.println("please specify device, number of microphones and samplerate");
                printHelp();
                System.exit(-1);
            }
        } catch (Exception e) { //laziness
            System.err.println("failed to parse Arguments!");
            e.printStackTrace();
            printHelp();
            System.exit(-1);
        }
        //argument parsing stage finished

        new SoundRecord(port, numMics, samplerate, device).startRecording();
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("configServer", options);
    }
}
