package com.wingcopter.UAVSimulator.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPController {
    private static final Logger log = LoggerFactory.getLogger(UDPController.class);
    private String hostname;
    private String port;
    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;

    public UDPController(Properties properties) {
        try {
            hostname = properties.getProperty("udp.target.ip");
            port = properties.getProperty("udp.target.port");
            DatagramSocket socket = new DatagramSocket();
            pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream(pipedInputStream);
            int bufferSize = 65535; // buffer size

            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(() -> {
            try {
                byte[] buffer = new byte[bufferSize];
                while (!socket.isClosed()) {
                    int bytesRead = pipedInputStream.read(buffer);
                    DatagramPacket packet = new DatagramPacket(buffer, bytesRead, InetAddress.getByName(hostname), Integer.parseInt(port));
                    if (bytesRead != -1) {
                        socket.send(packet);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                    try {
                        pipedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                    if (!service.isShutdown()) {
                        service.shutdown();
                    }
                }
            });
        } catch (Exception e) {
            log.error("Error: " + e);
        }
    }

    public PipedInputStream getPipedInputStream() {
        return pipedInputStream;
    }


    public PipedOutputStream getPipedOutputStream() {
        return pipedOutputStream;
    }

}
