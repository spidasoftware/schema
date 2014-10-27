# for more documentation on coffescript see coffeescript.org
# once npm and coffeescript are installed, this can run this script with "coffee example_RPC_client.coffee"
# for documentation on node.js, see nodejs.org
# deep knowledge of neither of these should be required to understand the point of this example, though
# which is how to call the Calc RPC methods
# Any languages HTTP library will work fine.
# Calc must be running and have a license before this example will work

queryString = require('queryString')
http = require('http')
fs = require('fs')
util = require('util')


# query client data for pole information. I am using "Demo.client" because it is included in teh calc release
# but that is something you would want to be configurable in your software

# for this method, the only parameter is the id of the client file
postParameters = queryString.stringify {
	'clientFile': 'Demo.client'
}

# set up our query for all client anchors
queryOptions =
	host: 'localhost',
	port: '4560',
	path: '/clientData/anchors',
	method: 'post',
	headers:
		'Content-Type': 'application/x-www-form-urlencoded',
		'Content-Length': postParameters.length


onData = (data) ->
	# load the response JSON
	response = JSON.parse(data)
	util.puts "Got response: #{data}"

	# check if the request threw an error
	if response.error isnt undefined
		util.puts("error #{result.error.code}: #{result.error.message}")
	else
		#print the result

		for anchor in response.result
			util.puts("#{anchor}\n")

onResponse = (response) ->
     response.on('data', onData)

 # create and execute the actual request
httpPost = http.request(queryOptions, onResponse)
httpPost.write(postParameters)
httpPost.end()

# to load a project into calc
projectData = fs.readFileSync('../projects/minimal_project_with_gps.json', 'UTF-8')
#util.puts("read in project file\n #{projectData}")
# we don't actually need to convert this to json... it's just going to go back to a string in the parameters
# but we need to set up new query options and parameters.


postParameters = queryString.stringify(
	'project': projectData
)

#util.puts "postParameters: #{postParameters}"

queryOptions =
	host: 'localhost',
	port: '4560',
	path: '/calc/openProject',
	method: 'post',
	headers:
		'Content-Type': 'application/x-www-form-urlencoded',
		'Content-Length': postParameters.length


onData = (data) ->
	# load the response JSON
	response = JSON.parse(data)
	util.puts "Got response"
	# check if the request threw an error
	if response.error isnt undefined
		util.puts("error #{response.error.code}: #{response.error.message}")
	else
		# calc should not be displaying our test project
		# the result from these doesn't really mean much... you should look at the error instead'

onResponse = (response) ->
	response.on('data', onData)

httpPost = http.request(queryOptions, onResponse)
httpPost.write(postParameters)
httpPost.end()




