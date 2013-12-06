package com.spidasoftware.schema.server

import org.apache.log4j.Logger
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Wraps a simple embedded web server
 * To add one to your project, you should usually put it in its own thread:
 * LocalServer localServer = new LocalServer();
 * // add servlets
 * // add file paths
 * new Thread( localServer).start();
 * @author mford
 */
public class LocalServer implements Runnable {

	private static Logger log = Logger.getLogger(LocalServer.class);
	public static int DEFAULT_PORT = 3491;
	private Server jetty;
	private ContextHandler context;
	private Properties properties = new Properties();
	private int port = DEFAULT_PORT;
	boolean running = false;
	private ServletHandler handler;
	public LocalServer() {
		this(DEFAULT_PORT);
	}

	public LocalServer(int port) {
		jetty = new Server(port);
		handler = new ServletHandler();
		jetty.setHandler(handler);
	}

	public void run() {
		running = true;
		try {
			jetty.start();
			jetty.join();
		} catch (Exception ex) {
			log.error(ex, ex);
		}
	}

	/**
	 * Stop and destroy server.
	 */
	public void stop() {
		try {
			jetty.stop()
		} catch (InterruptedException ex) {
			log.error(ex,ex);
		}
	}

	public void addServlet(String path, Servlet servlet) {
		// tjws.addServlet(path, servlet);
		ServletHolder holder = new ServletHolder(servlet)
		handler.addServletWithMapping(holder,  "/" + path);
	}

	public void addServlet(LocalServiceServlet servlet){
		def url = servlet.baseURL.endsWith("/") ? servlet.baseURL+"*" : servlet.baseURL+"/*"
		if(url.startsWith("/")){url = url.replaceFirst("/", "")}
		log.info "Adding server at $url"
		addServlet(url, servlet)
	}

	public int getPort() {
		return port;
	}

	// public void addPath(String alias, File path) {
	// 	aliases.put(alias, path);
	// }
}
