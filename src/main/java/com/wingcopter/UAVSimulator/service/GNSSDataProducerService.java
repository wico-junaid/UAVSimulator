package com.wingcopter.UAVSimulator.service;

import com.wingcopter.UAVSimulator.config.PropertyConfiguration;
import com.wingcopter.UAVSimulator.model.GnssData;
import com.wingcopter.UAVSimulator.udp.UDPController;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.common.GpsRawInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Properties;


/**
 * UAV simulated GNSS/GPS data producer service
 */
public class GNSSDataProducerService {

    private static final Logger log = LoggerFactory.getLogger(GNSSDataProducerService.class);

    private static GnssData gnssData;
    private static final int systemId = 255;
    private static final int componentId = 0;

    public GNSSDataProducerService(GnssData gnssData) {
        try {
            this.gnssData = gnssData;
        } catch (Exception e) {
            log.error("Exception: " + e);
        }

    }

    /**
     * sending the simulated GNSS/GPS data based on mavlink and UDP
     */
    public void sendGNSSData() {
        try {
            Properties properties = PropertyConfiguration.loadProperties();
            UDPController udpController = new UDPController(properties);

            // arguments check
            if (gnssData.isEmpty()){
                throw new IllegalArgumentException("Argument are null");
            }

            log.debug("Starting the simulation..");
            MavlinkConnection connection = MavlinkConnection.create(udpController.getPipedInputStream(), udpController.getPipedOutputStream());

            // Calculate the distance between start and end points
            double distance = calculateDistance(gnssData.getStartLatitude(), gnssData.getStartLongitude(), gnssData.getEndLatitude(), gnssData.getEndLongitude());

            // Calculate the number of steps based on the desired time interval (e.g., 1 second)
            double timeInterval = 1.0; // Time interval in seconds
            int numSteps = (int) Math.ceil(distance / (gnssData.getSpeed() * timeInterval));

            // Calculate the latitude and longitude increment per step
            double latIncrement = (gnssData.getEndLatitude() - gnssData.getStartLatitude()) / numSteps;
            double lonIncrement = (gnssData.getEndLongitude()- gnssData.getStartLongitude()) / numSteps;

            // Initialize the current coordinates to the start point
            double currentLatitude = gnssData.getStartLatitude();
            double currentLongitude = gnssData.getStartLongitude();

            log.debug("Simulation in progress..");
            // Perform the flight simulation
            for (int i = 0; i < numSteps; i++) {
                // Calculate the current altitude based on the time elapsed
                double elapsedTime = (i + 1) * timeInterval;

                // update the altitude based on changing altitude meter per second
                double currentAltitude = gnssData.getAltitude() + (elapsedTime * gnssData.getChangeAltMeterPerSecond());

                // Update the current coordinates based on the increments
                currentLatitude += latIncrement;
                currentLongitude += lonIncrement;

                // calculate the current timestamp
                long currentTimeMillis = System.currentTimeMillis();
                Instant currentInstant = Instant.ofEpochMilli(currentTimeMillis);

                // Create a gpsRawInt message
                BigInteger timeInt = BigInteger.valueOf(currentInstant.getEpochSecond());
                GpsRawInt gpsRawInt = GpsRawInt.builder()
                        .timeUsec(timeInt)
                        .lat((int) (gnssData.getStartLatitude() * 1E7)) // Convert latitude to degrees * 1E7
                        .lon((int) (gnssData.getStartLongitude() * 1E7)) // Convert longitude to degrees * 1E7
                        .alt((int) (gnssData.getAltitude() * 1E3)) // Convert altitude to meters
                        .build();

                // secret key based on payload
                byte[] secretKey = MessageDigest.getInstance("SHA-256")
                        .digest(gpsRawInt.toString().getBytes(StandardCharsets.UTF_8));

                // printing the messages
                System.out.println("Mavlink Packet: " + systemId + ", " + componentId + ", " + gpsRawInt);

                // prepare and sending the mavlink2 messages
                connection.send2(systemId, componentId, gpsRawInt, 0, currentInstant.getEpochSecond(), secretKey);

                // update the start coordinates
                gnssData.setAltitude(currentAltitude);
                gnssData.setStartLatitude(currentLatitude);
                gnssData.setStartLongitude(currentLongitude);

                // Wait for the specified time interval before the next position update
                Thread.sleep((long) (timeInterval * 1000));
            }
        } catch (IOException | InterruptedException | NoSuchAlgorithmException | IllegalArgumentException ex) {
            log.error("Exception :" + ex);
        }
    }

    /**
     * Calculate the distance between two points using the Haversine formula
     * @param lat1 latitude 1 of initial coordinates
     * @param lon1 longitude 1 of initial coordinates
     * @param lat2 latitude 2 of destination coordinates
     * @param lon2 longitude  2 of destination coordinates
     * @return calculated distance
     */
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        //log.debug("Calculating distance between two coordinates.");
        double R = 6371.0; // Earth's radius in kilometers

        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000; // Convert to meters
    }
}
