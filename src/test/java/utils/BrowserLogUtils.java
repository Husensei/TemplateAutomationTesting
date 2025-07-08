package utils;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v138.log.Log;
import org.openqa.selenium.devtools.v138.network.Network;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

/**
 * Utility class for managing browser logs and network traffic capture.
 * <p>
 * Supports Chrome DevTools Protocol (CDP) for Chrome/Edge browsers and BrowserMob Proxy
 * for capturing HTTP Archive (HAR) files on browsers without CDP support (e.g., Firefox, Safari).
 * <p>
 * Provides methods to start logging, save logs to files, and sanitize scenario names for safe file storage.
 */
public class BrowserLogUtils {

    private static final Logger logger = LoggerFactory.getLogger(BrowserLogUtils.class);
    private static final ThreadLocal<DevTools> devTools = new ThreadLocal<>();
    private static final ThreadLocal<BrowserMobProxy> proxyThread = new ThreadLocal<>();

    /**
     * Enables Chrome DevTools Protocol (CDP) logging for console messages and network events.
     * <p>
     * Starts a DevTools session on the given {@link RemoteWebDriver}, listens for console log entries,
     * network requests, and responses, and writes them asynchronously to log files.
     *
     * @param driver       the RemoteWebDriver instance; must implement {@link HasDevTools}
     * @param scenarioName the sanitized scenario name used for naming log files
     */
    public static void enableCDPLogging(RemoteWebDriver driver, String scenarioName) {
        if (driver instanceof HasDevTools) {
            try {
                DevTools tools = ((HasDevTools) driver).getDevTools();
                tools.createSession();
                devTools.set(tools);

                tools.send(Log.enable());
                tools.send(Network.enable(
                        Optional.<Integer>empty(),
                        Optional.<Integer>empty(),
                        Optional.<Integer>empty(),
                        Optional.<Boolean>empty()
                ));

                tools.addListener(Log.entryAdded(), logEntry ->
                        saveToFile("console", scenarioName, logEntry.getText())
                );

                tools.addListener(Network.requestWillBeSent(), request ->
                        saveToFile("network", scenarioName, "[REQUEST] " + request.getRequest().getUrl())
                );

                tools.addListener(Network.responseReceived(), response ->
                        saveToFile("network", scenarioName, "[RESPONSE] " + response.getResponse().getUrl())
                );

                logger.info("✅ CDP logging enabled for scenario: {}", scenarioName);
            } catch (Exception e) {
                logger.error("❌ Failed to enable CDP logging: {}", e.getMessage(), e);
            }
        } else {
            logger.warn("⚠️ DevTools not supported for this WebDriver instance.");
        }
    }

    /**
     * Starts a BrowserMob Proxy server to capture network traffic and begins recording
     * a new HTTP Archive (HAR) session.
     * <p>
     * This is intended for browsers that do not support CDP logging.
     */
    public static void startProxy() {
        try {
            BrowserMobProxy proxy = new BrowserMobProxyServer();
            proxy.start(0);
            proxy.newHar("session");
            proxyThread.set(proxy);
            logger.info("🌐 BrowserMob Proxy started and HAR recording initialized.");
        } catch (Exception e) {
            logger.error("❌ Failed to start BrowserMob Proxy: {}", e.getMessage(), e);
        }
    }

    /**
     * Saves the HAR (HTTP Archive) file captured by BrowserMob Proxy for the current scenario
     * and stops the proxy server.
     *
     * @param scenarioName the sanitized scenario name to use for the HAR file name
     */
    public static void saveProxyHar(String scenarioName) {
        BrowserMobProxy proxy = proxyThread.get();
        if (proxy != null) {
            try {
                Har har = proxy.getHar();
                File file = new File("target/logs/network/" + sanitize(scenarioName) + ".har");
                file.getParentFile().mkdirs();
                har.writeTo(file);
                logger.info("📄 HAR file saved for scenario: {}", scenarioName);
            } catch (IOException e) {
                logger.error("❌ Failed to save HAR file: {}", e.getMessage(), e);
            } finally {
                proxy.stop();
                proxyThread.remove();
            }
        }
    }

    /**
     * Writes a single log line to a text file corresponding to the log type and scenario.
     * <p>
     * Ensures the directory exists and appends to the log file in a thread-safe manner.
     *
     * @param type         the log type ("console" or "network")
     * @param scenarioName the sanitized scenario name used for file naming
     * @param content      the text content to write to the log file
     */
    private static synchronized void saveToFile(String type, String scenarioName, String content) {
        try {
            File dir = new File("target/logs/" + type);
            if (!dir.exists()) dir.mkdirs();

            File logFile = new File(dir, sanitize(scenarioName) + ".log");
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(content + "\n");
            }
        } catch (IOException e) {
            logger.error("❌ Failed to write log to file: {}", e.getMessage(), e);
        }
    }

    /**
     * Sanitizes a string by replacing all characters not suitable for filenames
     * (anything other than letters, digits, dash, underscore, or dot) with an underscore.
     *
     * @param name the raw scenario name or string to sanitize
     * @return a sanitized string safe for file naming
     */
    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }
}
