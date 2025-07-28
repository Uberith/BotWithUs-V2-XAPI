package net.botwithus.xapi.util;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Comprehensive logging utility for BotWithUs XAPI.
 * Provides both static and instance-based logging with support for different log levels.
 * 
 * Usage:
 * - Static logging: Logger.logInfo("Message"), Logger.logDebug("Debug info"), etc.
 * - Instance logging: Logger logger = Logger.getLogger(MyClass.class); logger.info("Message");
 * - Context logging: Logger.withContext("scriptName", "MyScript").info("Message");
 * 
 * Log Levels (in order of severity):
 * - TRACE: Very detailed diagnostic information
 * - DEBUG: Debugging information for development
 * - INFO: General information about application flow
 * - WARN: Potentially harmful situations
 * - ERROR: Error events that might still allow the application to continue
 * 
 * Files are automatically created in the logs/ directory:
 * - botwithus-xapi.log: Main application log (INFO and above)
 * - debug.log: Debug information (DEBUG and TRACE only)
 * - errors.log: Error messages (ERROR only)
 */
public class Logger {
    
    // Static logger for general use
    private static final org.slf4j.Logger STATIC_LOGGER = LoggerFactory.getLogger(Logger.class);
    
    // Instance logger
    private final org.slf4j.Logger instanceLogger;
    
    /**
     * Private constructor for instance creation
     */
    private Logger(org.slf4j.Logger logger) {
        this.instanceLogger = logger;
    }
    
    /**
     * Creates a new Logger instance for the specified class
     * @param clazz The class to create a logger for
     * @return A new Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(LoggerFactory.getLogger(clazz));
    }
    
    /**
     * Creates a new Logger instance with the specified name
     * @param name The name of the logger
     * @return A new Logger instance
     */
    public static Logger getLogger(String name) {
        return new Logger(LoggerFactory.getLogger(name));
    }
    
    /**
     * Creates a temporary logger with additional context information
     * @param key The context key
     * @param value The context value
     * @return A ContextLogger for chaining
     */
    public static ContextLogger withContext(String key, String value) {
        return new ContextLogger().put(key, value);
    }
    
    // Static logging methods
    
    /**
     * Logs a TRACE level message (most verbose) - static version
     */
    public static void logTrace(String message) {
        STATIC_LOGGER.trace(message);
    }
    
    /**
     * Logs a TRACE level message with exception - static version
     */
    public static void logTrace(String message, Throwable throwable) {
        STATIC_LOGGER.trace(message, throwable);
    }
    
    /**
     * Logs a DEBUG level message - static version
     */
    public static void logDebug(String message) {
        STATIC_LOGGER.debug(message);
    }
    
    /**
     * Logs a DEBUG level message with exception - static version
     */
    public static void logDebug(String message, Throwable throwable) {
        STATIC_LOGGER.debug(message, throwable);
    }
    
    /**
     * Logs an INFO level message - static version
     */
    public static void logInfo(String message) {
        STATIC_LOGGER.info(message);
    }
    
    /**
     * Logs an INFO level message with exception - static version
     */
    public static void logInfo(String message, Throwable throwable) {
        STATIC_LOGGER.info(message, throwable);
    }
    
    /**
     * Logs a WARN level message - static version
     */
    public static void logWarn(String message) {
        STATIC_LOGGER.warn(message);
    }
    
    /**
     * Logs a WARN level message with exception - static version
     */
    public static void logWarn(String message, Throwable throwable) {
        STATIC_LOGGER.warn(message, throwable);
    }
    
    /**
     * Logs an ERROR level message - static version
     */
    public static void logError(String message) {
        STATIC_LOGGER.error(message);
    }
    
    /**
     * Logs an ERROR level message with exception - static version
     */
    public static void logError(String message, Throwable throwable) {
        STATIC_LOGGER.error(message, throwable);
    }
    
    // Instance logging methods
    
    /**
     * Instance TRACE logging
     */
    public void trace(String message) {
        instanceLogger.trace(message);
    }
    
    /**
     * Instance TRACE logging with exception
     */
    public void trace(String message, Throwable throwable) {
        instanceLogger.trace(message, throwable);
    }
    
    /**
     * Instance DEBUG logging
     */
    public void debug(String message) {
        instanceLogger.debug(message);
    }
    
    /**
     * Instance DEBUG logging with exception
     */
    public void debug(String message, Throwable throwable) {
        instanceLogger.debug(message, throwable);
    }
    
    /**
     * Instance INFO logging
     */
    public void info(String message) {
        instanceLogger.info(message);
    }
    
    /**
     * Instance INFO logging with exception
     */
    public void info(String message, Throwable throwable) {
        instanceLogger.info(message, throwable);
    }
    
    /**
     * Instance WARN logging
     */
    public void warn(String message) {
        instanceLogger.warn(message);
    }
    
    /**
     * Instance WARN logging with exception
     */
    public void warn(String message, Throwable throwable) {
        instanceLogger.warn(message, throwable);
    }
    
    /**
     * Instance ERROR logging
     */
    public void error(String message) {
        instanceLogger.error(message);
    }
    
    /**
     * Instance ERROR logging with exception
     */
    public void error(String message, Throwable throwable) {
        instanceLogger.error(message, throwable);
    }
    
    /**
     * Checks if DEBUG level is enabled for this logger
     */
    public boolean isDebugEnabled() {
        return instanceLogger.isDebugEnabled();
    }
    
    /**
     * Checks if TRACE level is enabled for this logger
     */
    public boolean isTraceEnabled() {
        return instanceLogger.isTraceEnabled();
    }
    
    /**
     * Helper class for adding context to log messages
     */
    public static class ContextLogger {
        
        public ContextLogger put(String key, String value) {
            MDC.put(key, value);
            return this;
        }
        
        public void trace(String message) {
            try {
                STATIC_LOGGER.trace(message);
            } finally {
                MDC.clear();
            }
        }
        
        public void debug(String message) {
            try {
                STATIC_LOGGER.debug(message);
            } finally {
                MDC.clear();
            }
        }
        
        public void info(String message) {
            try {
                STATIC_LOGGER.info(message);
            } finally {
                MDC.clear();
            }
        }
        
        public void warn(String message) {
            try {
                STATIC_LOGGER.warn(message);
            } finally {
                MDC.clear();
            }
        }
        
        public void error(String message) {
            try {
                STATIC_LOGGER.error(message);
            } finally {
                MDC.clear();
            }
        }
        
        public void error(String message, Throwable throwable) {
            try {
                STATIC_LOGGER.error(message, throwable);
            } finally {
                MDC.clear();
            }
        }
    }
} 