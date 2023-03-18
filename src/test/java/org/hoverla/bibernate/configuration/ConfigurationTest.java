package org.hoverla.bibernate.configuration;

import org.hoverla.bibernate.util.PropertiesUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

    private static final String PROPERTIES_FILE = "application.properties";

    private static final String EXPECTED_PROPS_JDBC_URL = "jdbc:h2:~/test";

    @Test
    void testLoadPropertiesFile() {
        assertDoesNotThrow(() -> PropertiesUtil.getPropertiesFrom(PROPERTIES_FILE));
    }

    @Test
    void configurationTest() {
        Configuration configuration = new PropertiesConfiguration(PROPERTIES_FILE);
        String actualUrl = configuration.getUrl();
        assertEquals(EXPECTED_PROPS_JDBC_URL, actualUrl);
    }
}
