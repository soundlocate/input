package de.sfn_kassel.sound_record.in;

import com.portaudio.BlockingStream;
import com.portaudio.DeviceInfo;
import com.portaudio.PortAudio;
import com.portaudio.StreamParameters;

/**
 * Created by robin on 12/23/15.
 */
public class AudioInput {
    private int numMics;
    private int blockSize;
    private BlockingStream inputStream;

    public AudioInput(int numMics, String deviceName, int samplerate, int blockSize) {
        PortAudio.initialize();

        this.blockSize = blockSize;

        int deviceCount = PortAudio.getDeviceCount();
        int deviceId = Integer.MAX_VALUE;
        StreamParameters inputParamters = new StreamParameters();

        inputParamters.channelCount = numMics;

        for (int i = 0; i < deviceCount; i++) {
            DeviceInfo deviceInfo = PortAudio.getDeviceInfo(i);

            System.out.println( "  deviceId    = " + i);
		    System.out.println( "  sampleRate  = " + deviceInfo.defaultSampleRate);
		    System.out.println( "  device name = " + deviceInfo.name );

            if(deviceInfo.name.contains(deviceName)) {
                inputParamters.device = i;

                if(PortAudio.isFormatSupported(inputParamters, null, samplerate) == 0) {
                    System.out.println("device found:)");
                    deviceId = i;
                    break;
                }
            }
        }

        if(deviceId == Integer.MAX_VALUE) {
            System.out.println("No suitable device found");
            System.exit(-1);
        }

        inputStream = PortAudio.openStream(inputParamters, null, samplerate, blockSize, 0);

        inputStream.start();

        this.numMics = numMics;
    }

    public double[] getSamples() {
        double[] samples = new double[blockSize * numMics];
        float[] buffer = new float[blockSize * numMics];

        inputStream.read(buffer, blockSize);

        for (int i = 0; i < buffer.length; i++) {
            samples[i] = buffer[i];
        }

        return samples;
    }
}
