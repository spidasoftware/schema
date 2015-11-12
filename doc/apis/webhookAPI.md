# Webhook API

It contains methods for registering, unregistering, renewing, and viewing webhooks.  All requests must have a content-type of 'application/json'.  With the exception of the apiToken, all parameters must be passed in the request body as properties of a JSON object.  All response bodies will contain a JSON object.

#### URLs

`https://${HOST}/projectmanager/webhookAPI`

#### Registering
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

#### Unregister
Unregister a previous registered webhook with a POST request to `/productname/webhookAPI/unregister?apiToken=your-api-token`

The request body must be a JSON object containing exactly one of the following properties:
* url: Unregister all webhooks with this url
* hookId: Unregister the webhook with this hookId

The server will respond with a JSON object containing the following properties:
* hookIds: An array of the hookIds unregistered
* success: true/false
* message: error/success message

#### Renew
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

#### View
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
			<td>{user: <i>userEmail</i>, <br/>
			projectId: <i>project.id</i>, <br/>
			project: <i>project</i>}</td>
			<td>Sent when a project is created</td>
		</tr>
		<tr>
			<td>Project</td>
			<td>
				update:<i>source</i>:<i>projectName</i><br/>
				source is api or ui
			</td>
			<td>{user: <i>userEmail</i>, <br/>
			projectId: <i>project.id</i>, <br/>
			project: <i>project</i>}</td>
			<td>Sent when a project is updated</td>
		</tr>
		<tr>
			<td>Project</td>
			<td>stationsAdded:<i>projectName</i></td>
			<td>{user: <i>userEmail</i>, <br/>
			projectId: <i>project.id</i>, <br/>
			project: <i>project</i>, <br/>
			stations: <i>stationsAdded</i>}</td>
			<td>Sent when stations are added to a project</td>
		</tr>
		<tr>
			<td>Project</td>
			<td>updateAddress:<i>projectName</i></td>
			<td>{user: <i>userEmail</i>, <br/>
			projectId: <i>project.id</i>, <br/>
			project: <i>project</i>}</td>
			<td>Sent when a project's address is updated through the UI</td>
		</tr>
		<tr>
			<td>Project</td>
			<td>delete:<i>projectName</i></td>
			<td>{user: <i>userEmail</i>, <br/>
			projectId: <i>project.id</i>, <br/>
			project: <i>project</i>}</td>
			<td>Sent when a project's address is updated through the UI</td>
		</tr>
		<tr>
			<td>Status</td>
			<td>leaveStatus:<i>partType</i>:<i>eventName</i>:<i>stationName</i></td>
			<td>{previousEvent: <i>status</i>,  <br/>
			nextEvent: <i>status</i>,  <br/>
			part: <i>station or project</i>,  <br/>
			user: <i>userEmail</i>, <br/>
			projectId: <i>project.id</i>}</td>
			<td>Send when an Project, Station, or Asset leaves a status</td>
		</tr>
		<tr>
			<td>Status</td>
			<td>enterStatus:<i>partType</i>:<i>eventName</i>:<i>stationName</i></td>
			<td>{previousEvent: <i>status</i>,  <br/>
			nextEvent: <i>status</i>,  <br/>
			part: <i>station or project</i>,  <br/>
			user: <i>userEmail</i>, <br/>
			projectId: <i>project.id</i>}</td>
			<td>Send when an Project, Station, or Asset enters a status</td>
		</tr>
		<tr>
			<td>Form</td>
			<td>update:<i>source</i>:<i>ownerName</i>:<i>formName</i><br/>
				source is api or ui</td>
			<td>{user: <i>userEmail</i>, <br/>
			form: <i>form</i>, <br/>
			projectId: <i>project.id</i>, <br/>
			projectName: <i>projectName</i>, <br/>
			parentId: <i>parentId</i>, <br/>
			parentName: <i>parentName</i>}</td>
			<td>Sent when a form is updated by a user</td>
		</tr>
		<tr>
			<td>File</td>
			<td>upload:<i>fileParentName</i>:<i>fileName</i></td>
			<td>{user: <i>userEmail</i>, <br/>
			projectId: <i>project.id</i>, <br/>
			parentType: <i>parentType</i>, <br/>
			parentName: <i>parentName</i>,  <br/>
			fileName: <i>fileName</i>}</td>
			<td>Sent when a file is uploaded by a user</td>
		</tr>
		<tr>
			<td>Tag</td>
			<td>new:<i>tagType</i></td>
			<td>{user: <i>userEmail</i>, <br/>
			tagType: <i>tagType</i>, <br/>
			name: <i>tagName</i>, <br/>
			ids: <i>tagIds</i>}</td>
			<td>Sent when a tag is created</td>
		</tr>
		<tr>
			<td>Tag</td>
			<td>delete:<i>tagType</i>:<i>tagName<i></td>
			<td>{user: <i>userEmail</i>, <br/>
			tagType: <i>tagType</i>, <br/>
			name: <i>tagName</i>}</td>
			<td>Sent when a tag is deleted</td>
		</tr>
		<tr>
			<td>Action</td>
			<td>runStart:<i>actionName</i>:<i>actionFilePrefix</i>:<i>partName</i></td>
			<td>{name: <i>actionName</i>, <br/>
			filePrefix<i>actionFilePrefix</i>, <br/>
			parameters: <i>actionParameters</i>, <br/>
			part: <i>actionPart</i>, <br/>
			runInstance: <i>uniqueRunUUID</i>, <br/>
			source: <i>source</i>,  <br/>
			user: <i>userEmail</i>, <br/>
			projectId: <i>project.id</i>}</td>
			<td>Sent when an action begins running</td>
		</tr>
		<tr>
			<td>Action</td>
			<td>runEnd:<i>actionName</i>:<i>actionFilePrefix</i>:<i>success</i></td>
			<td>{name: <i>actionName</i>, <br/>
			filePrefix<i>actionFilePrefix</i>, <br/>
			runInstance: <i>uniqueRunUUID</i>, <br/>
			success: <i>success</i>, <br/>
			message: <i>message</i>, <br/>
			source: <i>source</i>,  <br/>
			user: <i>userEmail</i>, <br/>
			projectId: <i>project.id</i>}</td>
			<td>Sent when an action finishes</td>
		</tr>
	</tbody>
</table>

##Webhook Example
In these examples the SPIDAMin server is running at https://spidamin.example.com and the webhook callback server is running at https://callback.example.com.  An API token "API_TOKEN" has been created for an admin user.

###Register
The below example registers a new webhook that will create a callback request for all events on the "Project" channel.

Direction: Callback Server -> SPIDAMin  
Request to: https://spidamin.example.com/projectmanager/webhookAPI/register?apiToken=API_TOKEN  
Request JSON:
```json
{
	"url": "https://callback.example.com/callback",
	"channel": "Project",
	"eventFilter": ".*",
	"leaseTime": 1200
}
```

Response JSON:
```json
{
	"message": "Webhook registered successfully",
	"leaseEnd": 1437763293324,
	"hookId": "397b23c8-ff7d-49e0-83eb-79f98f415aa2",
	"success": true
}
```

###View
The server can be queried for registered webhooks.  Here all webhooks registered with the callback url: https://callback.example.com/callback are queried.

Direction: Callback Server -> SPIDAMin  
Request to: https://spidamin.example.com/projectmanager/webhookAPI/view?apiToken=API_TOKEN  
Request JSON:
```json
{
  "url": "https://callback.example.com/callback"
}
```

Response JSON:
```json
{
  "webhooks": [
    {
      "leaseEnd": 1437763293324,
      "hookId": "397b23c8-ff7d-49e0-83eb-79f98f415aa2",
      "channel": "Project",
      "url": "https://callback.example.com/callback",
      "eventFilter": ".*"
    }
  ]
}
```

###Callback
A new project is created with 3 new stations.  The SPIDAMin server then makes 2 calls to the callback URL.  The first call is for a stationsAdded event, when the stations are added to the new project.  It lists the user who added the stations, the stations added, and the project the stations where added to.

Direction: SPIDAMin -> Callback Server  
Request to: https://callback.example.com/callback  
Request JSON:
```json
{
  "timestamp": 1437763552852,
  "payload": {
    "project": {
      "stations": [
        {
          "id": 35061,
          "spotted": false,
          "source": "assetmasterAssetService",
          "stationId": "ff8081814b84506a014b8458c5d01130",
          "display": "110",
          "deleted": false,
          "geometry": {
            "type": "Point",
            "coordinates": [
              -82.85795593261719,
              40.00355911254883
            ]
          }
        },
        {
          "id": 35063,
          "spotted": false,
          "source": "assetmasterAssetService",
          "stationId": "ff8081814b84506a014b8458df771a4c",
          "display": "193",
          "deleted": false,
          "geometry": {
            "type": "Point",
            "coordinates": [
              -82.85726928710938,
              40.005088806152344
            ]
          }
        },
        {
          "id": 35067,
          "spotted": false,
          "source": "assetmasterAssetService",
          "stationId": "ff8081814b84506a014b8459530f5190",
          "display": "230",
          "deleted": false,
          "geometry": {
            "type": "Point",
            "coordinates": [
              -82.85816192626953,
              40.004947662353516
            ]
          }
        }
      ],
      "status": {
        "current": "Start"
      },
      "projectCodes": [
        {
          "id": 35060,
          "value": "Webhook Test Project",
          "companyId": 11,
          "type": "PROJECT"
        }
      ],
      "deleted": false,
      "id": 35057,
      "draft": false,
      "address": {},
      "name": "Webhook Test Project",
      "companyId": 11,
      "dataForms": [],
      "flowName": "New Flow",
      "members": [
        {
          "id": 35058,
          "userId": 13,
          "type": "CREATOR"
        },
        {
          "id": 35059,
          "userId": 13,
          "type": "ASSIGNEE"
        }
      ],
      "flowId": 28379
    },
    "stations": [
      {
        "servicePriority": 90,
        "assetTypes": [
          "POLE"
        ],
        "source": "assetmasterAssetService",
        "address": {
          "zip_code": "89412",
          "county": "County A",
          "street": "Easy Street",
          "state": "CA",
          "city": "Emerald City",
          "country": "USA"
        },
        "originalStationId": "ff8081814b84506a014b8458c5d01130",
        "linkerId": 34443,
        "stationAssets": [
          {
            "assetDetails": [
              {
                "name": "strength",
                "value": "0.07451032192038298"
              },
              {
                "name": "county",
                "value": "County B"
              },
              {
                "name": "construction",
                "value": "Construction 2"
              },
              {
                "name": "yearSet",
                "value": "1999"
              },
              {
                "name": "length",
                "value": "70"
              },
              {
                "name": "township",
                "value": "Red Township"
              },
              {
                "name": "poleClass",
                "value": "H3"
              },
              {
                "name": "treatment",
                "value": "Copper Azole"
              },
              {
                "name": "dateInspected",
                "value": "61147850985302"
              },
              {
                "name": "loading",
                "value": "12"
              }
            ],
            "assetId": "ff8081814b84506a014b8458c5d01131",
            "assetType": "POLE",
            "ownerId": 101,
            "assetAttachments": [],
            "primaryAsset": true,
            "assetTags": [
              {
                "name": "Primary Tag",
                "primary": true,
                "value": "110"
              }
            ]
          }
        ],
        "stationId": "s27801_ff8081814b84506a014b8458c5d01130",
        "primaryAssetOwnerId": 101,
        "dataProviderId": 101,
        "primaryAssetType": "POLE",
        "geometry": {
          "type": "Point",
          "coordinates": [
            -82.85795593261719,
            40.00355911254883
          ]
        }
      },
      {
        "servicePriority": 90,
        "assetTypes": [
          "POLE"
        ],
        "source": "assetmasterAssetService",
        "address": {
          "zip_code": "02138",
          "county": "County A",
          "street": "Elm Street",
          "state": "CA",
          "country": "Amurrica",
          "city": "Emerald City"
        },
        "originalStationId": "ff8081814b84506a014b8458df771a4c",
        "linkerId": 34448,
        "stationAssets": [
          {
            "assetDetails": [
              {
                "name": "strength",
                "value": "0.43478809120011097"
              },
              {
                "name": "county",
                "value": "County B"
              },
              {
                "name": "construction",
                "value": "Construction 2"
              },
              {
                "name": "yearSet",
                "value": "1997"
              },
              {
                "name": "length",
                "value": "65"
              },
              {
                "name": "township",
                "value": "Red Township"
              },
              {
                "name": "poleClass",
                "value": "H2"
              },
              {
                "name": "treatment",
                "value": "CCA"
              },
              {
                "name": "loading",
                "value": "24"
              },
              {
                "name": "dateInspected",
                "value": "60813918591864"
              }
            ],
            "assetId": "ff8081814b84506a014b8458df771a4d",
            "assetType": "POLE",
            "ownerId": 123,
            "assetAttachments": [],
            "primaryAsset": true,
            "assetTags": [
              {
                "name": "Primary Tag",
                "primary": true,
                "value": "193"
              }
            ]
          }
        ],
        "stationId": "s27801_ff8081814b84506a014b8458df771a4c",
        "primaryAssetOwnerId": 123,
        "dataProviderId": 123,
        "primaryAssetType": "POLE",
        "geometry": {
          "type": "Point",
          "coordinates": [
            -82.85726928710938,
            40.005088806152344
          ]
        }
      },
      {
        "servicePriority": 90,
        "assetTypes": [
          "POLE"
        ],
        "source": "assetmasterAssetService",
        "address": {
          "zip_code": "90210",
          "county": "County B",
          "street": "Easy Street",
          "state": "OH",
          "country": "Amurrica",
          "city": "Lud"
        },
        "originalStationId": "ff8081814b84506a014b8459530f5190",
        "linkerId": 34478,
        "stationAssets": [
          {
            "assetDetails": [
              {
                "name": "county",
                "value": "County B"
              },
              {
                "name": "construction",
                "value": "Construction 1"
              },
              {
                "name": "yearSet",
                "value": "2000"
              },
              {
                "name": "length",
                "value": "40"
              },
              {
                "name": "township",
                "value": "Red Township"
              },
              {
                "name": "poleClass",
                "value": "4"
              },
              {
                "name": "loading",
                "value": "71"
              },
              {
                "name": "dateInspected",
                "value": "61245396621455"
              },
              {
                "name": "strength",
                "value": "0.5075003126284767"
              },
              {
                "name": "treatment",
                "value": "Creosote"
              }
            ],
            "assetId": "ff8081814b84506a014b8459530f5191",
            "assetType": "POLE",
            "ownerId": 189,
            "assetAttachments": [],
            "primaryAsset": true,
            "assetTags": [
              {
                "name": "Primary Tag",
                "primary": true,
                "value": "230"
              }
            ]
          }
        ],
        "stationId": "s27801_ff8081814b84506a014b8459530f5190",
        "primaryAssetOwnerId": 189,
        "dataProviderId": 189,
        "primaryAssetType": "POLE",
        "geometry": {
          "type": "Point",
          "coordinates": [
            -82.85816192626953,
            40.004947662353516
          ]
        }
      }
    ],
    "user": "admin@spidasoftware.com"
  },
  "eventName": "stationsAdded:Webhook Test Project",
  "hookId": "397b23c8-ff7d-49e0-83eb-79f98f415aa2",
  "channel": "Project"
}
```

The server receiving the callback responds with a 200 OK status.  Callback response content is ignored by SPIDAMin.

The second callback is for the new event.

Direction: SPIDAMin -> Callback Server  
Request to: https://callback.example.com/callback  
Request JSON:
```json
{
  "timestamp": 1437763554001,
  "payload": {
    "stations": [
      {
        "id": 35061,
        "spotted": false,
        "source": "assetmasterAssetService",
        "stationId": "ff8081814b84506a014b8458c5d01130",
        "display": "110",
        "deleted": false,
        "geometry": {
          "type": "Point",
          "coordinates": [
            -82.85795593261719,
            40.00355911254883
          ]
        }
      },
      {
        "id": 35063,
        "spotted": false,
        "source": "assetmasterAssetService",
        "stationId": "ff8081814b84506a014b8458df771a4c",
        "display": "193",
        "deleted": false,
        "geometry": {
          "type": "Point",
          "coordinates": [
            -82.85726928710938,
            40.005088806152344
          ]
        }
      },
      {
        "id": 35067,
        "spotted": false,
        "source": "assetmasterAssetService",
        "stationId": "ff8081814b84506a014b8459530f5190",
        "display": "230",
        "deleted": false,
        "geometry": {
          "type": "Point",
          "coordinates": [
            -82.85816192626953,
            40.004947662353516
          ]
        }
      }
    ],
    "status": {
      "current": "Start"
    },
    "projectCodes": [
      {
        "id": 35060,
        "value": "Webhook Test Project",
        "companyId": 11,
        "type": "PROJECT"
      }
    ],
    "deleted": false,
    "id": 35057,
    "draft": false,
    "address": {},
    "name": "Webhook Test Project",
    "companyId": 11,
    "dataForms": [],
    "flowName": "New Flow",
    "members": [
      {
        "id": 35058,
        "userId": 13,
        "type": "CREATOR"
      },
      {
        "id": 35059,
        "userId": 13,
        "type": "ASSIGNEE"
      }
    ],
    "flowId": 28379
  },
  "eventName": "new:api:New Flow:Webhook Test Project",
  "hookId": "397b23c8-ff7d-49e0-83eb-79f98f415aa2",
  "channel": "Project"
}
```

The server receiving the callback again responds with a 200 OK status.  Callback response content is ignored by SPIDAMin.

###Renew
Before the webhook expires it must be renewed.  This is required to keep SPIDAMin from sending callbacks that are no longer needed.

Direction: Callback Server -> SPIDAMin  
Request to: https://spidamin.example.com/projectmanager/webhookAPI/renew?apiToken=API_TOKEN  
Request JSON:
```json
{
  "hookId": "397b23c8-ff7d-49e0-83eb-79f98f415aa2",
  "leaseTime": 1200
}
```

Response JSON:
```json
{
  "message": "Webhooks successfully renewed",
  "hookIds": [
    "397b23c8-ff7d-49e0-83eb-79f98f415aa2"
  ],
  "leaseEnd": 1437766625725,
  "success": true
}
```

###Unregister
When the callback is now longer required.  The requesting application can either wait for the webhook to expire, or unregister the webhook.

Direction: Callback Server -> SPIDAMin  
Request to: https://spidamin.example.com/projectmanager/webhookAPI/unregister?apiToken=API_TOKEN  
Request JSON:
```json
{
  "hookId": "397b23c8-ff7d-49e0-83eb-79f98f415aa2"
}
```

Response JSON:
```json
{
  "message": "Webhooks successfully unregistered",
  "hookIds": ["397b23c8-ff7d-49e0-83eb-79f98f415aa2"],
  "success": true
}
```
