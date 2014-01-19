Rest API
====================

This is meant to simplify interactions between desktop java applications and REST services.
The defaults allow it to work out-of-the-box with the REST JSON services of CalcDB. It provides a very fluent and simple syntax, while still allowing for fine-grained customization.


Basic Example
---------------------

The following code will show how to create a new RestAPI and use it to call a REST JSON service.

    import com.spidasoftware.schema.client.rest.RestAPI
    import com.spidasoftware.schema.client.GenericHttpClient
    import net.sf.json.*

    // Creates a new api, which uses the given HttpClientInterface to make it's calls
    RestAPI api = new RestAPI("http://www.bookstore.com/api", new GenericHttpClient())

    // use the api to get the resource specified by bookstore.com/api/books/12345
    def result = api.books.find("12345")

    assert result instanceof net.sf.json.JSONObject
    assert result.status == 200  //http response code gets added here by default
    // the parsed response body is stored by default as result.json

    // to save a new book...
    def book = new JSONObject()
    book.put("title", "Huckleberry Finn")
    book.put("author", "Mark Twain")

    result = api.books.save(book)
    // this will result in a POST request to bookstore.com/api/books and put your parameters in the request body
    assert result.status == 200

    // list, update, and delete all work essentially the same way

    api.authors.list(["orderBy":"userRating"])
    // this will result in a GET resuest to bookstore.com/api/authors?orderBy=userRating

    // to delete, find, or update, we need to know the id of the resource we want
    def deleteResult = api.authors.delete("321")

    def updateResult = api.books.update(book, "<id_of_book>")  //PUT to bookstore.com/api/books/<id_of_book> with the book in the request body


Basic Configuration
-----------------------------

The default configuration is suitable for many basic api's, but we have the ability to easily configure the api for our specific needs. Let's say we want to query for books but the url path for books is actually /api/v2/book

    api.books.path = "/api/v2/book" //this path will be used for all future requests for 'books'

    /*
    * we can also override the request headers used for books. The following would cause requests to books
    * (but not any other resources) to always get sent these headers. These are actually the defaults,
    * but you can change them to whatever you want.
    */
    api.books.headers = ["Accept":"application/json", "Content-Type":"application/json"]

    // if we wanted ALL the requests to bookstore.com to use a particular set of headers, we could just say:
    api.defaults.headers = ["Accept":"application/json", "Content-Type":"application/json", "Accept-Language":"en-US"]
    // this would cause all resources to use these headers unless they've specified their own
    // Resource settings will aways take precedence over defaults

    /*
     * another option we have is to set additionalParams. This is a map of parameters that gets merged with every request.
     * A common use case would be for an api token. You can set this for the defaults and/or a specific resource
     * like so:
     */
     api.defaults.additionalParams = [apiToken: "12345abcd"] //default for all bookstore.com api calls
     api.books.additionalParams = [apiToken: "abcd12345"]    // used just for the books resource, takes precedence over defaults


** a little more advanced**

To look at some of the more advanced options, let's setup our bookstore api to use xml instead of json
(Hint: it's actually pretty easy). We could set this as a default or just for a single resource, but we'll just do the defaults.
The first step will be to change the request headers.

    api.defaults.headers = ["Accept":"application/xml", "Content-Type":"application/xml"]

This may cause the server to respond with xml instead of json, but we still need to override the default response handler in order to return xml instead of json. This will show the simple event-based response handlers that are available and how they are executed.

    api.defaults.doWithResponse = {response ->

        assert response instanceof org.apache.http.HttpResponse
        // parse the response body into xml or whatever format you choose
        // you'll even have a further opportunity to handle specific request responses later.
        def xml = XmlParser.parse(response.getEntity().getContent())
        return xml
    }

Once a response is received from the server, the response object is passed to the doWithResponse closure. This closure is passed to the api's http client and get's executed by the client. The client then calls it's cleanupResponse and cleanupRequest closures (by default, these simply close the response entity's inputstream and release the request's connection). Then whatever was returned from doWithResponse is sent back to the RestAPI. At this point, it will be passed through one of five closures defined in the api settings:

    doWithFindResult = { it }
    doWithListResult = { it }
    doWithSaveResult = { it }
    doWithUpdateResult = { it }
    doWithDeleteResult = { it }

By default, these closures don't modify the data at all, but you can use them to unwrap the responses even further or do whatever you wish. Again, these can be defined in the api.defaults and/or in any of the resources themselves, with the resources always taking precedent over the defaults.


Externalizing Configuration
-----------------------------------

You can easily externalize the configuration of a RestAPI by specifying a configuration directory in the constructor. If you provide such a directory, the api will look for a file there called defaults.config to use for it's defaults. The external config file will actually be merged into the base defaults, so you only have to specify the settings you want to override. RestAPIResources will also be automatically configured, looking for a file named <resource_name>.config. These files are written using Groovy's ConfigSlurper syntax.


The HttpClientInterface
-----------------------------

When you create a new RestAPI, you must specify an HttpClientInterface for it to use. This is an area under active development, so please check up on the documentation for it. The important thing is that you should use a single http client instance for all of the RestAPIs if possible.











