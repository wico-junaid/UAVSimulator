package com.wingcopter.UAVSimulator.service;

import com.wingcopter.UAVSimulator.config.PropertyConfiguration;
import com.wingcopter.UAVSimulator.model.GnssData;
import com.wingcopter.UAVSimulator.udp.UDPController;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.common.GlobalPositionInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Properties;
import java.util.Random;

public class GNSSDataProducerService {

    private static final Logger log = LoggerFactory.getLogger(GNSSDataProducerService.class);

    private GnssData gnssData;
    private final int systemId = 255;
    private final int componentId = 0;

    private DatagramSocket socket;

    public GNSSDataProducerService(GnssData gnssData) {
        try {
            this.gnssData = gnssData;
            this.socket = new DatagramSocket();

        } catch (Exception e) {
            log.error("Exception: " + e);
            //e.printStackTrace();
        }

    }

    public void sendGNSSData() {
        try {
            Properties properties = PropertyConfiguration.loadProperties();
            UDPController udpController = new UDPController(properties);

            log.debug("Starting the simulation..");
            MavlinkConnection connection = MavlinkConnection.create(udpController.getPipedInputStream(), udpController.getPipedOutputStream());

//            if (gnssData.getHeartbeat() == 0){
//                log.debug("Heartbeat message..");
//                Heartbeat heartbeat = Heartbeat.builder()
//                        .type(MavType.MAV_TYPE_GCS)
//                        .autopilot(MavAutopilot.MAV_AUTOPILOT_INVALID)
//                        .systemStatus(MavState.MAV_STATE_UNINIT)
//                        .mavlinkVersion(3)
//                        .build();
//
//                System.out.println(heartbeat);
//                connection.send2(systemId,componentId,heartbeat);
//            } else {
                //log.debug("Performing the simulation..");
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

                // Perform the flight simulation
                for (int i = 0; i < numSteps; i++) {

                    // Calculate the current altitude based on the time elapsed
                    double elapsedTime = (i + 1) * timeInterval;
                    double currentAltitude = gnssData.getAltitude() + (elapsedTime * gnssData.getChangeAltMeterPerSecond());

                    // Update the current coordinates based on the increments
                    currentLatitude += latIncrement;
                    currentLongitude += lonIncrement;

                    // calculate the current timestamp
                    long currentTimeMillis = System.currentTimeMillis();
                    Instant currentInstant = Instant.ofEpochMilli(currentTimeMillis);

                    // Create a GlobalPositionInt message
                    GlobalPositionInt globalPositionInt = GlobalPositionInt.builder()
                            .timeBootMs(currentInstant.getEpochSecond())
                            .lat((int) (gnssData.getStartLatitude() * 1E7)) // Convert latitude to degrees * 1E7
                            .lon((int) (gnssData.getStartLongitude() * 1E7)) // Convert longitude to degrees * 1E7
                            .alt((int) (gnssData.getAltitude() * 1E3)) // Convert altitude to meters
                            .build();

                    byte[] secretKey = MessageDigest.getInstance("SHA-256")
                            .digest(globalPositionInt.toString().getBytes(StandardCharsets.UTF_8));

                    System.out.println("Mavlink Packet: " + systemId + ", " + componentId + ", " + globalPositionInt);

                    connection.send2(systemId, componentId, globalPositionInt, 0, currentInstant.getEpochSecond(), secretKey);

                    // update the start coordinates
                    gnssData.setAltitude(currentAltitude);
                    gnssData.setStartLatitude(currentLatitude);
                    gnssData.setStartLongitude(currentLongitude);

                    // Wait for the specified time interval before the next position update
                    Thread.sleep((long) (timeInterval * 1000));
                }
            //}
        } catch (Exception ex) {
            log.error("Exception :" + ex);
        }
    }


    public static String modifyMessage(String input) {
        // Parse the latitude and longitude from the input string
        int latStartIndex = input.indexOf("lat=") + 4;
        int latEndIndex = input.indexOf(",", latStartIndex);
        int lonStartIndex = input.indexOf("lon=") + 4;
        int lonEndIndex = input.indexOf(",", lonStartIndex);
        String latStr = input.substring(latStartIndex, latEndIndex);
        String lonStr = input.substring(lonStartIndex, lonEndIndex);
        int lat = Integer.parseInt(latStr);
        int lon = Integer.parseInt(lonStr);
        // Add a random number between 10000000 and 90000000 to the latitude and longitude
        Random rand = new Random();
        int latOffset = rand.nextInt(80000) + 10000;
        int lonOffset = rand.nextInt(80000) + 10000;
        lat += latOffset;
        lon += lonOffset;
        // Build the modified string
        String latReplaceStr = "lat=" + lat + ",";
        String lonReplaceStr = "lon=" + lon + ",";
        String modified = input.replaceFirst("lat=[^,]*,", latReplaceStr);
        modified = modified.replaceFirst("lon=[^,]*,", lonReplaceStr);
        return modified;
    }

    // Calculate the distance between two points using the Haversine formula
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
