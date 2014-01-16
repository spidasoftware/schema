package com.spidasoftware.schema.client.rest

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 1/15/14
 * Time: 7:35 PM
 * To change this template use File | Settings | File Templates.
 */
class RestJSONClient {
    String baseUrl

    List<RestResource> resources

    RestJSONClient(String baseUrl) {
        this.baseUrl = baseUrl
    }



}
