package com.spidasoftware.schema.server

import Acme.Serve.Serve;
import Acme.Serve.Serve.PathTreeDictionary
import java.io.File;
import org.apache.log4j.Logger;

import javax.servlet.Servlet;
import java.util.Properties;

//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//import org.eclipse.jetty.servlet.ServletHolder;

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
	class PublicServe extends Acme.Serve.Serve {
				// Overriding method for public access
				public void setMappingTable(PathTreeDictionary mappingtable) {
					super.setMappingTable(mappingtable);
				}
				// add the method below when .war deployment is needed
				public void addWarDeployer(String deployerFactory, String throttles) {
					super.addWarDeployer(deployerFactory, throttles);
				}
			};

			// setting aliases, for an optional file servlet
	Acme.Serve.Serve.PathTreeDictionary aliases = new Acme.Serve.Serve.PathTreeDictionary();
	private PublicServe tjws = new PublicServe();
//	Serve.PathTreeDictionary paths = new Serve.PathTreeDictionary();
//	private Server jetty;
//	private ServletContextHandler context;
	private Properties properties = new Properties();
	private int port = DEFAULT_PORT;
	boolean running = false;

	public LocalServer() {
		this(DEFAULT_PORT);
	}

	public LocalServer(int port) {
		this.port = port;
		properties.put("port", port);
		properties.setProperty(Acme.Serve.Serve.ARG_NOHUP, "nohup");
		properties.setProperty(Acme.Serve.Serve.ARG_BINDADDRESS, "127.0.0.1");
		tjws.arguments = properties;


//		jetty = new Server(port);
//		context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		context.setContextPath("/");
//		jetty.setHandler(context);
	}

	public void run() {
		running = true;
		if (aliases.keys() != null) {
			tjws.setMappingTable(aliases);
			tjws.addDefaultServlets(null);
		}
		try {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				public void run() {
					if (tjws != null) {
						tjws.notifyStop();
						tjws.destroyAllServlets();
					}
				}
			}));

			tjws.serve();
//			jetty.start();
		} catch (Exception ex) {
			log.error(ex, ex);
		}
	}

	/**
	 * Stop and destroy server.
	 */
	public void stop() {
		tjws.notifyStop();
		tjws.destroyAllServlets();
//		try {
//
//			jetty.join();
//		} catch (InterruptedException ex) {
//			log.error(ex,ex);
//		}
	}

	public void addServlet(String path, Servlet servlet) {
		tjws.addServlet(path, servlet);

//		context.addServlet(new ServletHolder(servlet), "/" + path);
	}

	public void addServlet(LocalServiceServlet servlet){
		def url = servlet.baseURL.endsWith("/") ? servlet.baseURL+"*" : servlet.baseURL+"/*"
		println "Adding server at $url"
		tjws.addServlet(url, servlet)
	}

	public int getPort() {
		return port;
	}

	public void addPath(String alias, File path) {
		aliases.put(alias, path);

	}
}
