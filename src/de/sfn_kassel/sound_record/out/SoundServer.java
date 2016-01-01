package de.sfn_kassel.sound_record.out;

import java.io.IOException;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Created by robin on 12/23/15.
 */
public class SoundServer {
    private int numMics;
    ServerSocket listener;
    ArrayList<OutputStream> clients = new ArrayList<>();

    public SoundServer(int port, int numMics) throws IOException {
        listener = new ServerSocket(port);

        new Thread(() -> {
            while (true) {
                try{
                    Socket newClient = listener.accept();
                    System.out.println("client " + newClient.getInetAddress() + ":" + newClient.getPort() + " connected");

                    OutputStream writer = newClient.getOutputStream();

                    writer.write(ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN).putInt(numMics).array());

                    writer.flush();

                    clients.add(writer);

                } catch (IOException e) {
                        e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendSamples(double[] samples) throws IOException {
        assert samples.length % numMics == 0;

        ByteBuffer buffer = ByteBuffer.allocate(samples.length * Double.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        for(double d : samples) {
            buffer.putDouble(d);
        }

        for(OutputStream client : clients) {
            client.write(buffer.array());
            client.flush();
        }
    }
}
