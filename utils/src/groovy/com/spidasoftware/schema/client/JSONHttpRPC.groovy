package com.spidasoftware.schema.client

import com.spidasoftware.schema.server.*
import net.sf.json.JSONObject
import net.sf.json.JSONSerializer
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.apache.log4j.Logger

import java.io.IOException
import java.util.List

/**
 * Created with IntelliJ IDEA.
 * User: mford
 * Date: 9/20/12
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class JSONHttpRPC {
	static Logger log = Logger.getLogger(JSONHttpRPC.class)
	String server
	public JSONHttpRPC(String serverRoot) {
		this.server = serverRoot
	}

	public JSONHttpResponse callMethod(String methodName, List<NameValuePair> parameters) throws IOException {

		String methodUrl = server + "/" + methodName
		DefaultHttpClient client = new DefaultHttpClient()

		HttpPost post = new HttpPost(methodUrl)
		log.debug("Method url: "+methodUrl);
		post.addHeader("Content-Type", "application/x-www-form-urlencoded")
		post.setEntity(new UrlEncodedFormEntity(parameters))
		HttpResponse response = client.execute(post)
		String body = EntityUtils.toString(response.getEntity(), "UTF-8")
		log.debug("response body\n" + body);
		Object jsonResponse = JSONSerializer.toJSON(body);
		if (jsonResponse instanceof JSONObject) {
			return JSONHttpResponse.fromJSON((JSONObject) jsonResponse)
		}
		throw new IOException("Value returned from server was not a JSONHttpResponse")
	}
}
