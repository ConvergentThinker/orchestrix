package testorbit.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Port Manager - Automatic Port Allocation
 * Prevents port conflicts in parallel execution
 * 
 * KEY CONCEPTS:
 * - Each Appium server needs unique port
 * - Android needs systemPort and chromedriverPort
 * - iOS needs wdaLocalPort
 * - Ports must not conflict with each other or other services
 */
public class PortManager {
    private static final Logger logger = LoggerFactory.getLogger(PortManager.class);
    private static PortManager instance;
    
    // Track allocated ports across all threads
    private final Set<Integer> allocatedPorts;
    
    // Port ranges for different services
    private static final int APPIUM_PORT_START = 4723;
    private static final int APPIUM_PORT_END = 4800;
    
    private static final int SYSTEM_PORT_START = 8200;
    private static final int SYSTEM_PORT_END = 8300;
    
    private static final int CHROMEDRIVER_PORT_START = 9515;
    private static final int CHROMEDRIVER_PORT_END = 9600;
    
    private static final int WDA_PORT_START = 8100;
    private static final int WDA_PORT_END = 8200;

    private PortManager() {
        this.allocatedPorts = ConcurrentHashMap.newKeySet();
        logger.info("Port Manager initialized");
    }

    public static synchronized PortManager getInstance() {
        if (instance == null) {
            instance = new PortManager();
        }
        return instance;
    }

    /**
     * Allocate Appium server port
     * @return Available port number
     */
    public synchronized int allocateAppiumPort() {
        return allocatePort(APPIUM_PORT_START, APPIUM_PORT_END, "Appium");
    }

    /**
     * Allocate Android system port
     * @return Available port number
     */
    public synchronized int allocateSystemPort() {
        return allocatePort(SYSTEM_PORT_START, SYSTEM_PORT_END, "SystemPort");
    }

    /**
     * Allocate ChromeDriver port
     * @return Available port number
     */
    public synchronized int allocateChromedriverPort() {
        return allocatePort(CHROMEDRIVER_PORT_START, CHROMEDRIVER_PORT_END, "ChromeDriver");
    }

    /**
     * Allocate iOS WDA port
     * @return Available port number
     */
    public synchronized int allocateWdaPort() {
        return allocatePort(WDA_PORT_START, WDA_PORT_END, "WDA");
    }

    /**
     * Generic port allocation
     */
    private int allocatePort(int startPort, int endPort, String portType) {
        for (int port = startPort; port < endPort; port++) {
            if (!allocatedPorts.contains(port) && isPortAvailable(port)) {
                allocatedPorts.add(port);
                logger.debug("✓ Allocated {} port: {}", portType, port);
                return port;
            }
        }
        
        throw new RuntimeException("No available " + portType + " ports in range " + 
            startPort + "-" + endPort);
    }

    /**
     * Check if port is available on the system
     */
    private boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Release a port back to the pool
     */
    public synchronized void releasePort(int port) {
        if (allocatedPorts.remove(port)) {
            logger.debug("✓ Released port: {}", port);
        }
    }

    /**
     * Release multiple ports
     */
    public synchronized void releasePorts(int... ports) {
        for (int port : ports) {
            releasePort(port);
        }
    }

    /**
     * Get statistics
     */
    public int getAllocatedPortCount() {
        return allocatedPorts.size();
    }
}
