package org.hoverla.bibernate.util;

import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.exception.ExceptionMessages;
import org.hoverla.bibernate.exception.props.CannotLoadPropertiesFromFileException;
import org.hoverla.bibernate.exception.props.UnableCreatePropertiesUtilException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 A utility class for loading properties from a file.
 */
@Slf4j
public final class PropertiesUtil {

    /**

     Private constructor to prevent instantiation of this class.
     Throws {@link UnableCreatePropertiesUtilException} if this constructor is called.
     */
    private PropertiesUtil() {
        throw new UnableCreatePropertiesUtilException(ExceptionMessages.USE_STATIC_METHOD_TO_CREATE_PROPS);
    }
    /**

     The class loader to be used for loading the properties file.
     */
    private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    /**

     Loads properties from the specified file name.
     @param fileName the name of the properties file to load.
     @return the loaded properties.
     @throws CannotLoadPropertiesFromFileException if the properties cannot be loaded from the file.
     */
    public static Properties getPropertiesFrom(String fileName) throws CannotLoadPropertiesFromFileException {
        try (InputStream inputStream = loader.getResourceAsStream(fileName)) {
            Properties props = new Properties();
            props.load(inputStream);
            return props;
        } catch (IOException e) {
            log.error("Error while loading properties from file '{}'", fileName, e);
            throw new CannotLoadPropertiesFromFileException(ExceptionMessages.CANNOT_LOAD_PROPERTIES, e);
        }
    }
}