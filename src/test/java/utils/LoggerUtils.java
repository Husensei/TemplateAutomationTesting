package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {

    private LoggerUtils() {

    }

    public static Logger getLogger(Class<?> className) {
        return LoggerFactory.getLogger(className);
    }
}
