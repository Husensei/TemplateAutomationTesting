package utils;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v135.log.Log;
import org.openqa.selenium.devtools.v135.network.Network;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class BrowserLogUtils {

    private static final ThreadLocal<DevTools> devTools = new ThreadLocal<>();
    private static final ThreadLocal<BrowserMobProxy> proxyThread = new ThreadLocal<>();

    public static void enableCDPLogging(RemoteWebDriver driver, String scenarioName) {
        if (driver instanceof HasDevTools) {
            DevTools tools = ((HasDevTools) driver).getDevTools();
            tools.createSession();
            devTools.set(tools);

            tools.send(Log.enable());
            tools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

            tools.addListener(Log.entryAdded(), logEntry -> {
                saveToFile("console", scenarioName, logEntry.getText());
            });

            tools.addListener(Network.requestWillBeSent(), request -> {
                saveToFile("network", scenarioName, "[REQUEST] " + request.getRequest().getUrl());
            });

            tools.addListener(Network.responseReceived(), response -> {
                saveToFile("network", scenarioName, "[RESPONSE] " + response.getResponse().getUrl());
            });
        }
    }

    public static void startProxy() {
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);
        proxy.newHar("session");
        proxyThread.set(proxy);
    }

    public static void saveProxyHar(String scenarioName) {
        if (proxyThread.get() != null) {
            Har har = proxyThread.get().getHar();
            File file = new File("target/logs/network/" + scenarioName + ".har");
            try {
                har.writeTo(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            proxyThread.get().stop();
            proxyThread.remove();
        }
    }

    private static synchronized void saveToFile(String type, String scenarioName, String content) {
        try {
            File dir = new File("target/logs/" + type);
            if (!dir.exists()) dir.mkdirs();

            File logFile = new File(dir, scenarioName + ".log");
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(content + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
