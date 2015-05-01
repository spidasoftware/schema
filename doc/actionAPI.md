###Action API###

####URLs####
To run an action make a POST request to the Min server with a path similar to the below:

`/projectmanager/actionAPI/run/{actionName}?apiToken={apiToken}`

Where `{actionName}` is the name of the action to run and `{apiToken}` is the user's api token.

####Parameters####
Parameters to the action must be passed in the body of the request as JSON.
Each parameter will have a name and a value; the value for all parameters must be a string type.
Additionally the request must have a Content-Type of application/json.

Example:
```
{
"Parameter 1": "Value for parameter 1",
"The Second Parameter": "Value for the second parameter"
}
```

If the action requires a project part, the id for the project part must be
passed in the JSON as a parameter with the name `projectPartId`.

####Response####
If the action was able to be run, the response body will contain a JSON object with 2 properties:
- `success` -- True/False indicates if the action returned an error
- `message` -- A message describing either action success/failure


####Security####
Each action has a security level which can be one of 3 values:
- ADMIN -- (default) Action can only be run by users with role type `ROLE_PM_USER`. 
- USERWITHPART -- Action can be run only if a project part is passed to the action and the user specified by the api token is a manager or assignee of that part's parent project (or the part itself if the part is a project)
- USER -- Any user can run the action.

Each action should contain a method `getScriptSecurity`, that must return a ScriptSecurity enum with one of the 3 above values.  If the action does not contain this method, it will default to ADMIN.

####Errors####
Errors can be expressed in one of 2 ways:
1. Through the HTTP status code of the response
2. Through the message text in the JSON response body

If the HTTP status code is something other than 200, then the action was not able to be run.  Possible status codes are below:
- 400 -- A projectPartId was specified but a project part with that id did not exist on the system.
- 403 -- User with given api token does not have rights to run the specified action.
- 404 -- The action requested does not exist.
- 500 -- Internal Error

If the HTTP status code is 200, then the action was able to run.  But, may or may not have completed successfully.  The success property of the returned JSON object will indicate action success/failure.

####SCE Actions####
We will create 2 actions for SCE.
1. UpdateEquipmentIdAction -- This action will take 2 parameters:
- "FunctionalLocation" -- The functional location of the pole
- "EquipmentNumber" -- The new equipment number of the pole
2. NotificationStatusChangeAction -- This action will take 2 parameters:
- "NotificationNumber" -- The notification being updated
- "NotificationStatus" -- The status of the notification

So, URLs for SCE will be the following:
`
/projectmanager/actionAPI/run/UpdateEquipmentIdAction
/projectmanager/actionAPI/run/NotificationStatusChangeAction
`

For the UpdateEquipmentIdAction we expect something similar to the following:
```
{
   "FunctionalLocation": "ABCDEFG",
   "EquipmentID": "12345678"
}
```

For the NotificationStatusChangeAction we expect something similar to the following:
```
{
   "NotificationNumber": "12345678",
   "NotificationStatus": "NOCO"
}
```


