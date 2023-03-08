package org.hoverla.bibernate.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    private PropertiesUtil() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();

    public static Properties getPropertiesFor(String fileName) {
        try (InputStream inputStream = loader.getResourceAsStream(fileName)) {
            Properties props = new Properties();
            props.load(inputStream);
            return props;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
