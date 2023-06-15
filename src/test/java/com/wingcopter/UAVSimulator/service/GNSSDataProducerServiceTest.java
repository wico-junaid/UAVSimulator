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
}