# SPIDAMin Webhook Design

## Webhook API
The Webhook API is located at `/productname/webhookAPI/`.  It contains methods for registering, unregistering, renewing, and viewing webhooks.  All requests must have a content-type of 'application/json'.  With the exception of the apiToken, all parameters must be passed in the request body as properties of a JSON object.  All response bodies will contain a JSON object.

### Registering
Register a webhook with a POST request to `/productname/webhookAPI/register?apiToken=your-api-token`

The request body must be a JSON object containing the following properties:
* url: The URL to post to
* channel: The channel to listen to (Form, Project, Station, etc)
* eventFilter: A filter on event name.  Must be a Java regular expression.  The API will post back only on events matching this event name.  Defaults to .\*  Note: the entire event name must match the regular expression.  I.E. $ and ^ surrounding the expression is implied.
* hookId: User defined id for this hook.  Will be echoed back to the user in callbacks originating from this request and can be used to unregister or renew.  If webhook already exists for this hookId, it will be removed first.
* leaseTime: Expire web hook after this many seconds.

The server will respond with a JSON object containing the following properties:
* hookId: The hookId registered
* success: true/false
* message: error/success message
* leaseEnd: The time this webhook will expire.  Represented as milliseconds since the unix epoch.

### Unregister
Unregister a previous registered webhook with a POST request to `/productname/webhookAPI/unregister?apiToken=your-api-token`

The request body must be a JSON object containing exactly one of the following properties:
* url: Unregister all webhooks with this url
* hookId: Unregister the webhook with this hookId

The server will respond with a JSON object containing the following properties:
* hookIds: An array of the hookIds unregistered
* success: true/false
* message: error/success message

### Renew
Renew previously registered, currently active webhooks with a POST request to `/productname/webhookAPI/renew?apiToken=your-api-token`

The request body must be a JSON object containing the following properties:
* url: Renew all webhooks with this url
* hookId: Renew the webhook with this hookId
* leaseTime: The number of seconds to after which the webhook(s) will expire.  All renewed webhooks with then expire at now + leaseTime, unless renewed again.
NOTE: Exactly one of url or hookId should be provided

The server will respond with a JSON object containing the following properties:
* hookIds: An array of the hookIds renewed
* success: true/false
* message: error/success message
* leaseEnd: The time the webhooks will expire.  Represented as milliseconds since the unix epoch.

### View
To view previously registered active webhooks create a POST request to
`/productname/webhookAPI/view?apiToken=your-api-token`

The request body must be a JSON object containing no more than one of the following properties:
* url: View all webhooks with this url
* hookId: View the webhook with this hookId
NOTE: If an empty JSON object is passed the server will respond with all registered webhooks

The server will respond with a JSON object containing the following properties:
* webhooks: An array of JSON objects containing the following properties:
	* url: The URL this webhook will post to
	* channel: The channel listened to by this webhook (Form, Project, Station, etc)
	* eventFilter: A filter on event name.  Will be a Java regular expression.
	* hookId: The user defined id for this hook.  
	* leaseEnd: The time this webhook will expire.  Represented as milliseconds since the unix epoch.

## Internal Webhook Service
In Min code, anywhere a change or update is made we will call:
```
webhookService.sendEvent(channel, eventName, JSONObject obj)
```
or
```
webhookService.sendEvent(channel, eventName) {
	new JSONObject(
		...
	)
}
```
The second form can be used if generating the JSONObject could possible impact performance.  It will not be evaluated unless there is a webhook which matches the event and in that case will be executed only one.

For performance reasons this will add the event to an async queue that another thread will listen to then handle sending callbacks to the registered urls.

This thread will POST back to the registered urls when the channel matches and eventFilter is a regex match on eventName.  To make this work we will come up with a naming standard per channel, i.e. for a project channel it may be eventType:name:company id. Where eventType may be something like 'update', 'reassign', 'statusChange', etc.

## Callbacks
When an event within the given channel and matching the given eventName happens on the Min server.  A callback to the webhook's registered url will be created.  This callback will be an HTTP POST request with content-type application/json.  The request body will be a JSON object containing the following properties:
* channel: The channel of the event
* eventName: The name of the event
* hookId: The hookId of the registered hook that triggered this callback
* timestamp: The time (in millis since the unix epoch) when this event occured
* payload: A JSON object describing the event

Anything in the response will be ignored by the server.

## List of webhook events
<table>
	<thead>
		<tr>
			<th>Channel</th>
			<th>Event Name</th>
			<th>Payload</th>
			<th>Description</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>Project</td>
			<td>
				new:<i>source</i>:<i>flowName</i>:<i>projectName</i><br/>
				source is api or ui
			</td>
			<td>{user: <i>userEmail</i>, <br/>project: <i>project</i>}</td>
			<td>Sent when a project is created</td>
		</tr>
		<tr>
			<td>Project</td>
			<td>
				update:<i>source</i>:<i>projectName</i><br/>
				source is api or ui
			</td>
			<td>{user: <i>userEmail</i>, <br/>project: <i>project</i>}</td>
			<td>Sent when a project is updated</td>
		</tr>
		<tr>
			<td>Project</td>
			<td>stationsAdded:<i>projectName</i></td>
			<td>{user: <i>userEmail</i>, <br/>project: <i>project</i>, <br/>stations: <i>stationsAdded</i>}</td>
			<td>Sent when stations are added to a project</td>
		</tr>
		<tr>
			<td>Project</td>
			<td>updateAddress:<i>projectName</i></td>
			<td>{user: <i>userEmail</i>, <br/>project: <i>project</i>}</td>
			<td>Sent when a project's address is updated through the UI</td>
		</tr>
		<tr>
			<td>Project</td>
			<td>delete:<i>projectName</i></td>
			<td>{user: <i>userEmail</i>, <br/>project: <i>project</i>}</td>
			<td>Sent when a project's address is updated through the UI</td>
		</tr>
		<tr>
			<td>Status</td>
			<td>leaveStatus:<i>partType</i>:<i>eventName</i>:<i>stationName</i></td>
			<td>Station</td>
			<td>Send when an Project, Station, or Asset leaves a status</td>
		</tr>
		<tr>
			<td>Status</td>
			<td>enterStatus:<i>partType</i>:<i>eventName</i>:<i>stationName</i></td>
			<td>Project</td>
			<td>Send when an Project, Station, or Asset enters a status</td>
		</tr>
		<tr>
			<td>Form</td>
			<td>update:<i>ownerName</i>:<i>formName</i></td>
			<td>{user: <i>userEmail</i>, <br/>form: <i>form</i>, <br/>parentName: <i>parentName</i>}</td>
			<td>Sent when a form is updated by a user</td>
		</tr>
		<tr>
			<td>Form</td>
			<td>delete:<i>ownerName</i>:<i>formName</i></td>
			<td>{user: <i>userEmail</i>, <br/>form: <i>form</i>, <br/>parentName: <i>parentName</i>}</td>
			<td>Sent when a form is deleted by a user</td>
		</tr>
		<tr>
			<td>File</td>
			<td>upload:<i>fileParentName</i>:<i>fileName</i></td>
			<td>{user: <i>userEmail</i>, <br/>parentType: <i>parentType</i>, <br/>parentName: <i>parentName</i>, fileName: <i>fileName</i>}</td>
			<td>Sent when a file is uploaded by a user</td>
		</tr>
		<tr>
			<td>Tag</td>
			<td>new:<i>tagType</i></td>
			<td>{user: <i>userEmail</i>, <br/>tagType: <i>tagType</i>, <br/>name: <i>tagName</i>, <br/>ids: <i>tagIds</i>}</td>
			<td>Sent when a tag is created</td>
		</tr>
		<tr>
			<td>Tag</td>
			<td>delete:<i>tagType</i>:<i>tagName<i></td>
			<td>{user: <i>userEmail</i>, <br/>tagType: <i>tagType</i>, <br/>name: <i>tagName</i>}</td>
			<td>Sent when a tag is deleted</td>
		</tr>
		<tr>
			<td>Action</td>
			<td>runStart:<i>actionName</i>:<i>actionFilePrefix</i>:<i>partName</i></td>
			<td>{name: <i>actionName</i>, <br/>filePrefix<i>actionFilePrefix</i>, <br/>parameters: <i>actionParameters</i>, <br/>part: <i>actionPart</i>, <br/>runInstance: <i>uniqueRunUUID</i>, <br/>source: <i>source</i>}</td>
			<td>Sent when an action begins running</td>
		</tr>
		<tr>
			<td>Action</td>
			<td>runEnd:<i>actionName</i>:<i>actionFilePrefix</i>:<i>success</i></td>
			<td>{name: <i>actionName</i>, <br/>filePrefix<i>actionFilePrefix</i>, <br/>runInstance: <i>uniqueRunUUID</i>, <br/>success: <i>success</i>, <br/>message: <i>message</i>, <br/>source: <i>source</i>}</td>
			<td>Sent when an action finishes</td>
		</tr>
	</tbody>
</table>
