package com.wingcopter.UAVSimulator.service;

import com.wingcopter.UAVSimulator.model.GnssData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GNSSDataProducerServiceTest {

    GnssData gnssData = new GnssData();

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void sendGNSSDataNoArgumentsTest() {
        GNSSDataProducerService gnssDataProducerService = new GNSSDataProducerService(gnssData);
        assertThrows(IllegalArgumentException.class,
                gnssDataProducerService::sendGNSSData);
    }

    @Test
    void sendGNSSDataWithArgumentsTest() {
        gnssData.setStartLatitude(0.00001);
        gnssData.setStartLongitude(0.00001);
        gnssData.setEndLatitude(0.00002);
        gnssData.setEndLongitude(0.00002);
        gnssData.setAltitude(0);
        gnssData.setSpeed(1);
        gnssData.setChangeAltMeterPerSecond(0);
        GNSSDataProducerService gnssDataProducerService = new GNSSDataProducerService(gnssData);
        gnssDataProducerService.sendGNSSData();
        assertNotNull(gnssDataProducerService);
    }
}