package com.spidasoftware.schema.utils

import groovy.util.logging.Log4j
import net.sf.json.JSON
import net.sf.json.JSONObject
import net.sf.json.JSONSerializer
import org.apache.http.HttpEntity
import org.apache.http.ParseException
import org.apache.http.client.HttpResponseException
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext
import org.apache.http.util.EntityUtils
import org.apache.tika.detect.DefaultDetector
import org.apache.tika.detect.Detector
import org.apache.tika.io.TikaInputStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MimeTypes

import java.nio.file.Files

/**
 * Generic http client class for making most of the basic http calls. It is fairly customizable by overriding the
 * Closures that it uses to create and configure the client and cleanup the responses and connections. The
 * default implementation will automatically close the response output stream and release the connections after each call.
 * It will not automatically cleanup the httpClient, however. You must call the shutdown() method when you are done in order
 * avoid leaking connections.
 *
 * <br>
 * By default, the client will not follow any redirects.
 *
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/15/14
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Log4j
class MimeDetector {

	private static final Detector DETECTOR = new DefaultDetector(MimeTypes.getDefaultMimeTypes());

	public static String detectMimeType(final File file) throws IOException {
		TikaInputStream tikaIS = null;
		try {
			tikaIS = TikaInputStream.get(file);

			/*
			 * You might not want to provide the file's name. If you provide an Excel
			 * document with a .xls extension, it will get it correct right away; but
			 * if you provide an Excel document with .doc extension, it will guess it
			 * to be a Word document
			 */
			final Metadata metadata = new Metadata();
			// metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
			String detected = DETECTOR.detect(tikaIS, metadata).toString();
			if(detected=="application/zip"){
				def fileName = file.getName().toLowerCase()
				if(fileName.endsWith(".spida")){
					return "application/x-spidacalc"
				}else if(fileName.endsWith(".spida")){
					return "application/x-spidaclient"
				}
			}
			return detected
		} finally {
			if (tikaIS != null) {
				tikaIS.close();
			}
		}
	}

}