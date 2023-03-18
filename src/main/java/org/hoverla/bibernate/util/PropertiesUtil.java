package org.hoverla.bibernate.util;

import org.hoverla.bibernate.exception.ExceptionMessages;
import org.hoverla.bibernate.exception.props.CannotLoadPropertiesFromFileException;
import org.hoverla.bibernate.exception.props.UnableCreatePropertiesUtilException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    private PropertiesUtil() {
        throw new UnableCreatePropertiesUtilException(ExceptionMessages.USE_STATIC_METHOD_TO_CREATE_PROPS);
    }

    private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();

    public static Properties getPropertiesFrom(String fileName) {
        try (InputStream inputStream = loader.getResourceAsStream(fileName)) {
            Properties props = new Properties();
            props.load(inputStream);
            return props;
        } catch (IOException e) {
            throw new CannotLoadPropertiesFromFileException(ExceptionMessages.CANNOT_LOAD_PROPERTIES, e);
        }
    }
}
