package com.wingcopter.UAVSimulator;

import com.wingcopter.UAVSimulator.model.GnssData;
import com.wingcopter.UAVSimulator.service.GNSSDataProducerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UavSimulatorApplication {

	public static void main(String[] args) {
		double speed = 10.0;
		float altitude = 125.5f;
		float changeAltMeterPerSecond = 0.2f;
		GnssData gnssData = new GnssData();

		SpringApplication.run(UavSimulatorApplication.class, args);

		// Arguments based parsing i.e; 2 alternative inputs
		// i. four arguments only related to start and end coordinate with constant other parameters
		// ii. six arguments with speed, altitude and changeAltMeterPerSec
		if ((args.length == 4) || (args.length == 7)) {
			if (args.length == 4) {
				gnssData.setStartLatitude(Double.parseDouble(args[0]));  // Starting latitude in degrees
				gnssData.setStartLongitude(Double.parseDouble(args[1])); // Starting longitude in degrees
				gnssData.setEndLatitude(Double.parseDouble(args[2])); // Ending latitude in degrees
				gnssData.setEndLongitude(Double.parseDouble(args[3])); // Ending longitude in degrees
				gnssData.setSpeed(speed); // Movement speed in meters per second
				gnssData.setAltitude(altitude); // Altitude above mean sea level
				gnssData.setChangeAltMeterPerSecond(changeAltMeterPerSecond); // Change of altitude with time per seconds
			} else {
				gnssData.setStartLatitude(Double.parseDouble(args[0]));
				gnssData.setStartLongitude(Double.parseDouble(args[1]));
				gnssData.setEndLatitude(Double.parseDouble(args[2]));
				gnssData.setEndLongitude(Double.parseDouble(args[3]));
				gnssData.setAltitude(Float.parseFloat(args[4]));
				gnssData.setSpeed(Double.parseDouble(args[5]));
				gnssData.setChangeAltMeterPerSecond(Float.parseFloat(args[6]));
			}
		}
		// Instantiate the service for preparing and sending UDP Mavlink message
		GNSSDataProducerService gnssDataProducer = new GNSSDataProducerService(gnssData);
		gnssDataProducer.sendGNSSData();

	}

}
