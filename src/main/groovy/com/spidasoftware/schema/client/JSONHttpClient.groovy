package com.spidasoftware.schema.client

import com.spidasoftware.schema.server.JSONServletException
import groovy.util.logging.Log4j
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import org.apache.http.HttpResponse
import org.apache.http.client.HttpResponseException
import org.apache.http.util.EntityUtils

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/15/14
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Log4j
class JSONHttpClient {

    JSONObject executeMethod(String method, URI uri, Map<String, Object> parameters) {
        GenericHttpClient client = GenericHttpClient.getInstance()
        return client.executeMethod(method, uri, parameters){response ->
            String body = EntityUtils.toString(response.getEntity())
            response.getEntity().consureContent()
            try {
                def json = new JsonSlurper().parse(response.getEntity()?.getContent())
                response.getEntity().consumeContent()
                return json
            } catch (Exception ex) {
                log.error("Response is not valid JSON", ex)
                response.getEntity().consumeContent()
                throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().reasonPhrase)
            }
        }
    }
}
