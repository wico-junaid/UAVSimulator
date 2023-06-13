package com.wingcopter.UAVSimulator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@ConfigurationProperties(prefix="udp.target")
@Configuration
public class PropertyConfiguration {
    public static InputStream inputStream;

    private static final Logger log = LoggerFactory.getLogger(PropertyConfiguration.class);

    public static Properties loadProperties() throws IOException {
        Properties configuration = new Properties();
        String propFileName = "application.properties";
        try {
            inputStream = PropertyConfiguration.class
                    .getClassLoader()
                    .getResourceAsStream(propFileName);
            if (inputStream != null) {
                configuration.load(inputStream);
                inputStream.close();
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (Exception e){
            log.error("Exception: " + e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return configuration;
    }
}
