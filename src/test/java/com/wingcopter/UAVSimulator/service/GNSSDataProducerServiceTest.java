package com.wingcopter.UAVSimulator.service;

import com.wingcopter.UAVSimulator.model.GnssData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GNSSDataProducerServiceTest {

    static GnssData gnssData = new GnssData();

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
                GNSSDataProducerService::sendGNSSData);
    }

    @Test
    void sendGnssDataWithArgumentsTest(){
        gnssData.setStartLatitude(37.7749);
        gnssData.setStartLongitude(-122.4194);
        gnssData.setEndLatitude(34.0522);
        gnssData.setEndLatitude(-118.2437);

        GNSSDataProducerService gnssDataProducerService = new GNSSDataProducerService(gnssData);
        gnssDataProducerService.sendGNSSData();

        assertNull(gnssDataProducerService);
    }
}