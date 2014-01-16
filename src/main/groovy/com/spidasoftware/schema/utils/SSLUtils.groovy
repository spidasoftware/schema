package com.spidasoftware.schema.utils

import groovy.util.logging.Log4j

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import java.security.cert.Certificate

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/15/14
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Log4j
class SSLUtils {

    static void setupSSL(URL url) throws IOException, UnknownHostException {
        SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory()
        int sslPort = url.getPort()
        if (sslPort < 0) {
            sslPort = 443
        }
        log.debug("Creating a SSL Socket For " + url.getHost() + " on port " + sslPort)
        SSLSocket socket = (SSLSocket) factory.createSocket(url.getHost(), sslPort)
        log.debug("Handshaking Started...")
        socket.startHandshake()
        log.debug("Handshaking Complete")

        Certificate[] serverCerts = socket.getSession().getPeerCertificates();
        log.debug("Retreived Server's Certificate Chain");

        for (int i = 0; i < serverCerts.length; i++) {
            Certificate myCert = serverCerts[i]
            log.debug("Certificate: " + (i+1) + " of " + serverCerts.length)
            log.debug("Public Key: " + myCert.getPublicKey())
            log.debug("Certificate Type: " + myCert.getType())
        }

        socket.close()
    }

}
