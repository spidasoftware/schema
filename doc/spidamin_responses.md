SPIDAstudio API Responses and Errors
====================================

This guide explains how to interpret what SPIDAstudio (formerly SPIDAmin) sends back from its HTTP APIs: the success envelopes, the error envelopes, which HTTP status codes to expect, and what the individual error messages mean. It applies to the web applications that make up SPIDAstudio (projectmanager, usersmaster, assetmaster, filefort, and spidadb). It does not cover the local SPIDAcalc API (see [calcAPI.md](apis/calcAPI.md)) or SPIDAcee (see [cee.md](cee.md)).

Read the [SPIDAmin API Overview](README.md) and [RPC conventions](rpc.md) first if you have not already.

Quick Reference
---------------

The five things most integrations get wrong:

1. **Send the `apiToken` as a request parameter** (query string or form body). It is not read from headers or cookies.
2. **Check the body for an `error` key even when the HTTP status is 200.** Most RPC validation errors are returned with HTTP 200.
3. **Treat any redirect (HTTP 302) as an authentication failure.** Do not let your HTTP client follow redirects silently; you will end up parsing an HTML login page.
4. **Not every response is JSON, and not every JSON response is wrapped in `result`.** A handful of endpoints return plain text, a zip file, or an unwrapped object. See [Unwrapped and non-JSON responses](#unwrapped-and-non-json-responses).
5. **SPIDAdb uses a different envelope** (`{"status":"ok"}` / `{"status":"error"}`) from the RPC services. See [SPIDAdb REST API](#spidadb-rest-api).

Which Application Answers Your Request
--------------------------------------

Each SPIDAstudio server hosts several web applications under one host name. The first path segment tells you which one you are talking to, and that determines the response conventions.

| Base path | Application | APIs | Response style |
|-----------|-------------|------|----------------|
| `/projectmanager/projectAPI/...` | projectmanager | [Project API](apis/projectAPI.md) | RPC envelope; several methods also set HTTP status codes |
| `/projectmanager/stationLinkerAPI/...` | projectmanager | [Station Linker API](apis/stationLinkerAPI.md) | RPC envelope |
| `/projectmanager/actionAPI/...` | projectmanager | [Action API](apis/actionAPI.md) | `{success, message}` with HTTP status codes |
| `/projectmanager/webhookAPI/...` | projectmanager | [Webhook API](apis/webhookAPI.md) | `{success, message, ...}` |
| `/projectmanager/rest/projectSearches/...` | projectmanager | [Project Searches](rest/projectSearches.md) | REST; errors are HTTP status codes with empty bodies |
| `/usersmaster/usersAPI/...`, `/usersmaster/companyAPI/...` | usersmaster | [Users API](apis/usersAPI.md), [Company API](apis/companyAPI.md) | RPC envelope |
| `/assetmaster/assetAPI/...`, `assetSearchAPI`, `assetCreationAPI` | assetmaster | [Asset API](apis/assetAPI.md), [Asset Search API](apis/assetSearchAPI.md), [Asset Creation API](apis/assetCreationAPI.md) | RPC envelope |
| `/filefort/assetAPI/...`, `assetFileAPI`, ... | filefort | [Asset API](apis/assetAPI.md), [Asset File API](apis/assetFileAPI.md) | RPC envelope |
| `/spidadb/projects`, `/spidadb/locations`, ... | spidadb | [SPIDAdb API](apis/spidadbAPI.md) | `{"status":"ok"|"error"}` envelope with HTTP status codes |

Each application keeps its own session. Authenticating against projectmanager does not authenticate you against usersmaster; send the `apiToken` with every request to every application.

Authentication and Authorization Responses
------------------------------------------

Every application shares the same security layer, so these responses look the same everywhere.

### How the token is read

The token is read from the `apiToken` request parameter only: either `?apiToken=...` on the URL or an `apiToken` field in an `application/x-www-form-urlencoded` or `multipart/form-data` body. An `apiToken` HTTP header is ignored.

Once a token has been accepted, the server sets a `JSESSIONID` cookie. If your HTTP client stores cookies, later requests may succeed *without* the token because they ride on that session. Do not rely on this: always send the token, and pass `expireSession=true` (see [Sessions](README.md#sessions)) so you do not leave sessions open on the server.

### Outcomes

| Situation | HTTP status | Body | What to do |
|-----------|-------------|------|------------|
| Token accepted | as documented for the method | as documented for the method | — |
| No `apiToken` parameter | `302` to the SPIDAstudio login page (HTML if followed) | none | Add the `apiToken` parameter. |
| `apiToken` does not match any user | `401` | `{"error":{"code":"PERMISSION_DENIED","message":"Unable to Authenticate with apiToken."}}` | Check the token value; the user may have regenerated it. |
| Token is valid but the user does not have an API role | `302` to the login page | none | The token is silently ignored and the request is treated as anonymous. Ask the SPIDAstudio administrator to grant the user the API user role (or an administrator role). SPIDAdb only accepts administrator tokens. |
| Token sent on a URL that is not an API endpoint (for example a UI page) | `302` to the login page | none | Token authentication is only accepted on the API paths documented in this repository. |
| A request on an existing token session reaches a non-API URL | `200` | `{"error":{"code":"PERMISSION_DENIED","message":"Path not allowed when using token authentication. PATH: [...] PATHS ALLOWED: [...]"}}` | The session is ended. Only call documented API paths. |
| Authenticated, but the method requires a higher role (for example `getAllProjectsInCompany` requires `ROLE_PM_ADMINISTRATOR`) | `403`, or a `302` to an HTML "access denied" page, depending on the application (SPIDAdb always returns `403`) | empty or HTML | Use a token for a user with the required role. |
| The user's account has been deactivated for inactivity | `200` | `{"error":{"code":"PERMISSION_DENIED","message":"Your account has been deactivated because your last activity was on M/d/yyyy. Contact your SPIDAMin Administrator so they can reactivate your account."}}` | Have an administrator reactivate the user. |
| `switchcompany` succeeded | `302` to the `targeturl` parameter, or to the application root | none | This is the only redirect that indicates success. |
| `switchcompany` failed | `401` | plain text `Switch user failed: user '<email>' is not authorized for company '<id>' or the user/company does not exist` | Check the `coselect` company id and the user's company memberships. |
| Wrong HTTP method (for example `GET` to a `POST`-only method) | `405` | HTML | Check the method's _Allowed Methods_ section. |
| Method name misspelled or not defined | `404` | HTML | Check the URL against the method's _URL_ section. |
| Unhandled server error | `500` | HTML, or JSON depending on the endpoint | Retry later; report the request and time to SPIDA support if it persists. |

Note that the License Agreement (EULA) redirect described in the [overview](README.md#license-agreement-eula) applies to interactive browser sessions. API requests authenticated with a token are not redirected to the agreement page.

RPC Services
------------

This section covers the RPC-style services: [Project](apis/projectAPI.md), [Station Linker](apis/stationLinkerAPI.md), [Users](apis/usersAPI.md), [Company](apis/companyAPI.md), [Asset](apis/assetAPI.md), [Asset Search](apis/assetSearchAPI.md), [Asset Creation](apis/assetCreationAPI.md), and [Asset File](apis/assetFileAPI.md).

### The envelope

Responses use the [method response](../resources/schema/general/method_response.schema) envelope. A success has a `result` key:

```json
{"result": {"id": 55485}}
```

A failure has an `error` key with a `code` and a human-readable `message`:

```json
{"error": {"code": "MISSING_REQUIRED_PARAM", "message": "Please provide the project_json parameter."}}
```

A response never contains both keys. The `Content-Type` is `application/json; charset=utf-8`.

### Error codes

| Code | Meaning | Typical cause |
|------|---------|---------------|
| `MISSING_REQUIRED_PARAM` | A required parameter was not sent. | Parameter name misspelled, sent in the wrong place (for example a header instead of a form field), or empty. The message names the parameter: `Please provide the <name> parameter.` |
| `INVALID_PARAM` | A parameter was sent but could not be used. | JSON that does not parse, JSON that fails schema validation, a number where a JSON array was expected, an id that does not exist, or a business rule violation. The generic message is `Please correct the <name> parameter.`; many methods give a more specific message. |
| `MISSING_RESOURCE` | The thing the parameters refer to does not exist, or is not visible to the current user. | Wrong id, or the user is logged in to a different company than the resource. Generic message: `The requested <name> was not found.` |
| `PERMISSION_DENIED` | Authenticated, but not allowed to do this. | Missing role, project locked, project belongs to another company, flow not shared with the user, or an API client restriction (see [API client restrictions](#api-client-restrictions)). |
| `INTERNAL_ERROR` | The server could not complete the request. | A workflow action failed during a status change, a downstream service (SPIDAdb, an asset service) did not respond, or an unexpected exception. |
| `UNSUPPORTED` | The implementing application does not support this method. | Calling `assetFileAPI` on assetmaster, or deleting by UUID on a service that does not support it. |
| `UNAUTHORIZED` | The asset services' permission code: the user may not act on a resource that belongs to another company. | Creating, updating, or modifying a station or asset for another company in assetmaster; filefort attachment operations across companies. |
| `SCHEMA_NOT_FOUND`, `MISSING_METHOD` | Reserved codes from the schema; you should not see them from SPIDAstudio. | — |

`UNSUPPORTED` and `UNAUTHORIZED` are only emitted by the asset services (assetmaster and filefort); the other applications use `PERMISSION_DENIED` for permission failures. New codes may be added to the schema over time, so treat any `error.code` you do not recognize as a failure rather than rejecting the response.

### HTTP 200 with an error body

The RPC controllers write the error envelope without changing the HTTP status, so **most RPC errors arrive as HTTP 200**. A client that only checks the status code will treat these as successes and then fail while reading `result`. Always test for the `error` key first.

The exceptions, where the Project API does set a meaningful status, are listed in [Project API](#project-api) below. Everything in the Users, Company, Asset, Asset Search, Asset Creation, Asset File, and Station Linker APIs is HTTP 200.

### Unwrapped and non-JSON responses

Not every method wraps its success value in `result`. The table below lists the Project API methods whose successful response is *not* an envelope, and the methods that can return plain text. Everything else in the RPC services returns `{"result": ...}`.

| Method | Successful response | Failure response |
|--------|---------------------|------------------|
| `isProjectNameValid` | `{"valid": true}` or `{"valid": false, "errors": [...]}` (errors only when `details=true`) | `200` envelope `MISSING_REQUIRED_PARAM` `Please provide name parameter.` |
| `getDBProjectByDBId` | The project JSON itself (`calc` or `referenced`), or a zip file for `format=exchange` (`Content-Type: application/zip`, `Content-Disposition: attachment; filename=<db_id>.exchange.spida`) | `200` envelope `Please provide db_id parameter.`; **`404` plain text** `unable to find a project from db with id <db_id>`; **`403` plain text** `you do not have permission to view this project` |
| `getDBLocationByDBId` | `{"location": {...}, "clientData": {...}}` | `200` envelope `Please provide db_id parameter.`; **`404` plain text** `unable to find a location from db with id <db_id>`; **`403` plain text** `you do not have permission to view this location` |
| `getLocationPhotosByDBId`, `getLocationThumbnailsByDBId` | A zip file | `200` envelope `Please provide db_id parameter.`; **`404` plain text** `unable to find a location from db with id <db_id>` or `unable to find photos in filefort with db_id <db_id>`; **`403` plain text** `you do not have permission to view this location` |
| `getLinkedDBLocations` | A JSON array of referenced locations | `200` envelope `Please provide station_id parameter.` |
| `findDBProjectsInDB` | A JSON array of projects | `200` envelope `Please provide label parameter.` |
| `getProjectsByDBId` | A JSON array of projects | `200` envelope `INVALID_PARAM` `Please correct the db_ids parameter.` (also when `db_ids` is missing) |
| `getProjectLogs` | A JSON array of log messages | `200` envelope `Please provide the project_id parameter.`; `200` envelope `MISSING_RESOURCE` `The requested project: <id> was not found.`; **`403` with an empty body** when the project exists but is not viewable |
| `findStationsToMatch` | `{"name": ..., "id": ..., "stations": [...]}` | **`404` plain text** `unable to find project <projectId> (or you don't have permission to see it)` |
| `maxVersion` | A bare integer, for example `12` | — |
| `purge` | `{"result": {"success": [...], "fail": [...]}}` | **`403` plain text** `Purge API is not enabled on this server.`; **`400` plain text** `Please provide project_ids parameter.` or `Please provide project_ids.` |

When you receive a non-200 status from one of these methods, read the body as text; do not assume it is JSON.

Project API
-----------

### createOrUpdate, createOrUpdateWithDB, and addLogMessage

These three methods are different from the rest of the RPC services: they map the error code to an HTTP status, and they can return plain-text errors.

| HTTP status | Body | Meaning |
|-------------|------|---------|
| `200` | `{"result": {"id": <projectId>}}` | Success. For `addLogMessage` the id is the project id that was passed in. |
| `200` | `{"error": {"code": "MISSING_REQUIRED_PARAM", ...}}` | `project_json` (or `project_id` / `log_message_json`) was not sent. |
| `200` | `{"error": {"code": "INVALID_PARAM", "message": "Please correct the project_json parameter."}}` | `project_json` is not valid JSON or does not validate against the [project schema](../resources/schema/spidamin/project/project.schema). Validate locally with the [schema validator](../README.md#tools) to get the field-level reason. |
| `200` | `{"error": {"code": "MISSING_RESOURCE", "message": "The requested project: <id> was not found."}}` | (`addLogMessage` only) No project with that id. |
| `200` | `{"error": {"code": "PERMISSION_DENIED", "message": "Project not allowed to be edited through the API by current client"}}` | See [API client restrictions](#api-client-restrictions). |
| `422` | `{"error": {"code": "INVALID_PARAM" \| "MISSING_RESOURCE", "message": "..."}}` | The JSON was valid but a business rule failed. Nothing was saved. See the message catalog below. |
| `403` | `{"error": {"code": "PERMISSION_DENIED", "message": "..."}}` | The user may not perform this change. Nothing was saved. |
| `500` | `{"error": {"code": "INTERNAL_ERROR", "message": "..."}}` | Usually a failed status transition; see [Status transitions](#status-transitions). |
| `500` | plain text | An error that was not classified. The text is the exception message; the most common ones are listed below. Nothing was saved. |

The `Content-Type` is `application/json; charset=utf-8` even for the plain-text bodies, so parse defensively.

#### Message catalog

Messages are shown as they appear in `error.message`; `<...>` marks a value substituted by the server.

**Name and flow (`422` unless noted)**

| Message | Meaning |
|---------|---------|
| `New projects must have a name.` | `name` is missing on a create (no `id` in `project_json`). |
| `New projects must have either a flow id or flow name.` | Provide `flowId` (preferred; get it from `getFlows`) or `flowName`. |
| `The project name '<name>' is not unique.` | Another project in the company has that name. Check with `isProjectNameValid` first. |
| `Flow id <id> is not available to current user <userId>` / `Flow named <name> is not available to current user <userId>` (**`403`**) | The flow does not exist, or is not usable by the user's company. `getFlows` lists the flows the user may create projects in. |

**Editing an existing project**

| Message | Status | Meaning |
|---------|--------|---------|
| `User does not have permission to edit this project.` | `403` | The user is not a manager/assignee with edit rights, or the project belongs to another company. Use `switchcompany` if the project is in a company the user has access to. |
| `Cannot update a closed project.` | `422` | The project has reached a finish or cancel status. |
| `Project is locked for editing.` | `403` | The project is locked in the SPIDAstudio UI. |
| `User is not allowed to change draft status.` | `403` | `draft` can only be changed while the project is on its start status, and not in the same request that moves it off the start status. |

**Status transitions** (see also [Status transitions](#status-transitions))

| Message | Status | Meaning |
|---------|--------|---------|
| `Invalid status: <status>.  These are the only possible next events: {<name>=<id>, ...} ` | `422` | `status.current` is not one of the statuses reachable from the project's current status. The message lists the valid next statuses. |
| `Status <status> not found!` | `500` | The flow definition is inconsistent; contact the SPIDAstudio administrator. |
| `Unable to change project status from '<current>' to '<requested>'` optionally followed by ` because <log messages>` | `500` | The transition was attempted but a workflow action or required form rejected it. The appended text is the action's log output. |

**Members (`422`)**

| Message | Meaning |
|---------|---------|
| `Could not find a user group named: <name>` | `members[].userGroupName` does not match a group in the company. |
| `Could not find a member with the id: <id>` | `members[].id` is not a member of this project. |
| `Could not find user associated with the member: <json>` | The member's user no longer exists. |
| `Could not find a user with the id: <id>` | `members[].userId` is not a user visible to the current user. |
| `Could not find a user with the email address: <email>` | `members[].email` did not match. |
| `Could not find a user with the external id <system> <value>` | `members[].externalId` did not match. |

**Project codes (`422`)**

| Message | Meaning |
|---------|---------|
| `Could not find a project code with the id: <id>` | `projectCodes[].id` does not belong to this project. |
| Project code type / validation messages (server generated) | The `type` is not one of the company's configured project code types, or the value failed the type's validation rule. |

**Stations**

| Message | Status | Meaning |
|---------|--------|---------|
| `Stations missing a source:<stations>` | `422` | Every non-spotted station needs a `source` (asset service name) with its `stationId`. |
| `Stations missing a stationId:<stations>` | `422` | Every non-spotted station needs a `stationId`. |
| `Invalid asset service: <source>, valid:<list>` | `422` | `source` is not one of the asset services configured on this server. The message lists the valid names. |
| `Permission denied to asset service: <name>, ...` | `403` | The user's company may not use that asset service. |
| `Asset service named <source> only returned <n> of <m> stations. missing=<ids>` | `422` (`MISSING_RESOURCE`) | One or more `stationId` values do not exist in that asset service. Use `findStationsToMatch` to resolve ids before creating the project. |
| `When station ID is set, spotted must be false. ...` | `422` | A station cannot be both `spotted` and linked to an asset service station. |
| `Geometry is required for spotted station with display <display>` | `422` | Spotted stations must include `geometry`. |
| `Station with id <id> not found` | `422` | `stations[].id` does not belong to this project. |
| `Cannot find station with stationId=<id> and source=<source>` | `500` plain text | The station could not be resolved while linking. |
| `Duplicate station display values in the project:[<displays>]` | `500` plain text | Two stations in the request (or the request plus the existing project) share a `display`. Displays must be unique within a project. |
| `Two stations linked to the same asset service station ...` | `500` plain text | Two stations in the request point at the same `source`/`stationId`. |

**Forms (`422` unless noted)**

| Message | Meaning |
|---------|---------|
| `No form with id: <template> found ...` | `dataForms[].template` does not match a form template available to the project. |
| `User doesn't have permission to edit form <form>` (**`403`**) | The form is restricted to another role or status. |
| Form field validation messages (server generated) | A form value failed the field's validation rule. The message is the rule's text. |

**Files and SPIDAdb (`createOrUpdateWithDB`)**

| Message | Status | Meaning |
|---------|--------|---------|
| `Project not successfully sent to SPIDAdb. project=<project>, file.originalFilename=<file>` | `500` plain text | SPIDAdb rejected the `spidaFile`. The most common reason is that the exchange file's `project.json` does not validate against the [calc project schema](../resources/schema/spidacalc/calc/project.schema) for the requested schema version, or a location is missing `geographicCoordinate`. Validate the exchange file locally, and try pushing it directly to the [SPIDAdb API](#spidadb-rest-api) to see SPIDAdb's own error message. When a *create* fails this way the projectmanager project and any SPIDAdb project that was created are rolled back. |

#### Status transitions

`status.current` in `project_json` requests a move to that status. The server checks it against the statuses reachable from the project's current status (the `possible` array returned by `getProjects`), then runs the flow's actions and required-form checks for the transition.

* If `status.current` is not reachable, the whole request fails with `422` and nothing is saved.
* If the transition is reachable but an action or required form rejects it, the response is `500 INTERNAL_ERROR` `Unable to change project status from ...`. **The other changes in the request (members, codes, stations, forms, files) are kept**, and the failure is recorded in the project log. Call `getProjectLogs` to see the action output, fix the data, and resend just the status change.
* On a create, an unknown `status.current` is not an error: the project is created on the flow's start status and the request succeeds.

#### Which changes are saved

Apart from the status-transition case above, the create/update methods are transactional: any `422`, `403`, or `500` response means no part of the request was saved. On a successful create, the project is created on the requested flow, the start status actions are run, and any files in the request are attached. On `createOrUpdateWithDB`, the projectmanager project and the SPIDAdb project are created together; if either fails, both are rolled back.

### API client restrictions

A SPIDAstudio project can be configured to refuse API edits from particular client applications. The client is identified by the `User-Agent` request header. If the header matches a client that the project excludes, `createOrUpdate`, `createOrUpdateWithDB`, `addLogMessage`, and `delete` respond with:

```json
{"error": {"code": "PERMISSION_DENIED", "message": "Project not allowed to be edited through the API by current client"}}
```

(HTTP 200 for the create/update methods; the message is included in the `INVALID_PARAM` text for `delete`.) SPIDAcalc identifies itself with a `User-Agent` beginning with `SPIDACalc/`, and this restriction is normally used to stop SPIDAcalc from overwriting a project. Set a `User-Agent` that identifies your own integration so it is not mistaken for another client, and so that the entries it writes to the project log (which include the `User-Agent`) are recognisable.

### Schema version

Methods that return SPIDAdb data (`getDBProjectByDBId`, `getDBLocationByDBId`, `findDBProjectsInDB`, `getLinkedDBLocations`) accept a `version` parameter naming the schema version to return. If `version` is omitted the server tries to infer it from the `User-Agent`, but it only recognises SPIDAcalc user agents; for any other client the data is returned in the server's current schema version. Pass `version` explicitly if your integration depends on a particular version, and use `maxVersion` to find the highest version the server supports.

### getProjects, getMyProjects, getAllProjectsInCompany

Success is `{"result": {"projects": [...]}}`. Failures are HTTP 200 envelopes:

| Message | Meaning |
|---------|---------|
| `Please provide project_ids or project_code_values or associated_station_ids parameter.` | `getProjects` needs at least one selector. |
| `Please correct the project_ids parameter.` (also `project_code_values`, `associated_station_ids`, `project_attributes`, `company_id`) | The parameter is not a JSON array (ids and values must be sent as JSON arrays, for example `project_ids=[1,2]`), or `project_attributes` contains an unsupported value. |

Projects the user cannot view are silently left out of the results rather than causing an error. An empty `projects` array with no `error` means nothing matched *that the user can see*; check the company the token is logged in to (see [Switching Companies](README.md#switching-companies-and-users)).

### delete

Success is `{"result": {"success": true}}`. Any failure is an HTTP 200 `INVALID_PARAM` envelope. Because `delete` processes each id independently, the `message` is a space-separated list of everything that went wrong, for example `Project 123 not found. You do not have permission to edit project 456.`; items not mentioned in the message were deleted. Messages include `Project <id> not found.`, `You do not have permission to edit project <id>.`, `Project is locked.  Unable to delete station <id>.`, and the API client restriction text above.

### getFlows

Success is `{"result": {"flows": [{"id": ..., "name": ..., "companyId": ..., "companyName": ..., "canBeStartedFromCalc": ...}]}}`. With `include_viewable=true` the list also includes flows the user can see but cannot start projects in; those flows will be rejected by `createOrUpdate` with the `Flow ... is not available to current user` message.

Station Linker API
------------------

All responses are HTTP 200. Success values are `{"result": {"stations": [...], "geometry": ...}}` (`getStations`), `{"result": {"links": [...]}}` (`getLinks`), `{"result": {"id": <linkerId>}}` (`linkStations`), and `{"result": [<linkerId>, ...]}` (`unlinkStations`). Error messages:

| Message | Code | Meaning |
|---------|------|---------|
| `station_ids or bounding_box not provided` | `MISSING_REQUIRED_PARAM` | `getStations` needs one of the two. |
| `Must provide station_id with it's source or linker_id alone` | `MISSING_REQUIRED_PARAM` | `getLinks` needs either `station_id` + `source`, or `linker_id`. |
| `Must provide station_ids with source or linker_ids alone` | `MISSING_REQUIRED_PARAM` / `INVALID_PARAM` | The link/unlink methods need either `station_ids` + `source`, or `linker_ids`. |
| `A parameter provided is not a vaild object param.` (sic) / `A parameter provided is not a valid object param.` | `INVALID_PARAM` | A parameter that should be a JSON array or object did not parse. |
| Any other message with code `INTERNAL_ERROR` | `INTERNAL_ERROR` | `linkStations`/`unlinkStations` failed; the message is the server exception text. |

Users API and Company API
-------------------------

All responses are HTTP 200 unless noted. The Users API requires an account-administrator token (only `getLoggedInUser` works for an ordinary API user); the Company API requires an administrator token. Common messages:

| Message | Code | Meaning |
|---------|------|---------|
| `Please correct the apiToken parameter.` | `INVALID_PARAM` | `usersAPI/createOrUpdate` requires the `apiToken` parameter on that request, and it must belong to the logged-in user. You will see this if you rely on the session cookie instead of sending the token. |
| `Please provide user_json parameter.` / `Please correct the user_json parameter.` | `MISSING_REQUIRED_PARAM` / `INVALID_PARAM` | `usersAPI/createOrUpdate` body missing or failing the [user schema](../resources/schema/spidamin/user/user.schema). |
| `The requested user id <id> was not found.` | `MISSING_RESOURCE` | `user_json.id` does not match an existing user. |
| `The requested user id:<id> was not found.`, `The requested user email:<email> was not found.`, `The requested user api_token was not found.` | `MISSING_RESOURCE` | No matching user visible to the caller. |
| `Please provide id or api_token or email parameter.` | `MISSING_REQUIRED_PARAM` | `getUser` needs one selector. |
| `Please provide system and value parameter.` | `MISSING_REQUIRED_PARAM` | External id lookups need both. |
| `Not allowed to access this user.` | `PERMISSION_DENIED` | The user is in a company the caller does not administer. |
| `Not allowed to create or update user in another company.` | `PERMISSION_DENIED` | `user_json.company` names a company the caller does not administer. |
| `Unable to save user.` | `INTERNAL_ERROR` | The user failed server-side validation (for example a duplicate email). |
| `Please provide the id parameter.`, `id parameter is not a long.`, `The requested company was not found.` | `MISSING_REQUIRED_PARAM` / `INVALID_PARAM` / `MISSING_RESOURCE` | `companyAPI/getCompany`. |
| `Unable to parse "params" values, please check your List syntax.` | `INVALID_PARAM` | `companyAPI` list parameters must be JSON arrays. |

Asset APIs (assetmaster and filefort)
-------------------------------------

All responses are HTTP 200. Success values are `{"result": {"stations": [...]}}` and `{"result": {"assets": [...]}}` for the Asset API, `{"result": {...}}` for the Asset File API methods, and a bare `{"keys": [...], "operators": [...]}` object for `assetSearchAPI/getAdvancedFindOptions`. Common messages:

| Message | Code | Meaning |
|---------|------|---------|
| `station_ids or bounding_box not provided` / `station_ids, tags, or bounding_box not provided` | `MISSING_REQUIRED_PARAM` | `getStations` needs a selector. |
| `A parameter provided is not valid.` / `bounding_box, company_ids, or station_ids invalid` | `INVALID_PARAM` | A selector did not parse as JSON. |
| `Please provide the asset_ids parameter.` / `Please correct the asset_ids parameter.` | `MISSING_REQUIRED_PARAM` / `INVALID_PARAM` | `getAssets`. |
| `Please provide the find_query parameter.` / `Please provide the find_param parameter.` | `MISSING_REQUIRED_PARAM` | Asset Search API. |
| `station or asset not provided` | `MISSING_REQUIRED_PARAM` | Asset Creation API. |
| `Deletion by UUID not supported`, `Operation not supported on this service`, `station or asset creation or updating not supported with this service` | `UNSUPPORTED` | The method exists in the interface but this application does not implement it (for example `assetFileAPI` on assetmaster, or asset creation on filefort). |
| `Attachment does not belong to the current user's company` | `UNAUTHORIZED` | filefort attachment in another company. |
| `You do not have permission to create or update stations for this company.`, `You do not have permission to modify this station.`, `You do not have permission to create or update assets for this company.`, `You do not have permission to modify this asset.` | `UNAUTHORIZED` | assetmaster Asset Creation API: the station or asset (or its `dataProviderId`/`companyId`) belongs to a company the user is not a member of. |
| `Please provide the uuid parameter.`, `Please correct the offset parameter.`, `The requested <uuid> was not found.`, `Unable to get <x> for <uuid>` | various | filefort `assetFileAPI`. `getRaw` returns the file bytes with `Content-Disposition: attachment; filename="..."` rather than JSON. |

Webhook API
-----------

The [Webhook API](apis/webhookAPI.md) takes a JSON request body and always responds with HTTP 200 and a JSON object containing `success` and `message` (plus `hookId` and `leaseEnd` for `register`, `hookIds` for `unregister`/`renew`, and `webhooks` for `view`). Check `success`; the messages are:

| Message | Meaning |
|---------|---------|
| `Missing required parameters: <names>` | `register` needs `url`, `channel`, and `leaseTime`. |
| `Lease time can not be greater than 1209600 seconds` | `leaseTime` is capped at two weeks; renew before it expires. |
| `Lease time must be given and not greater than than 1209600 seconds` | Same rule, from `renew`. |
| `Neither url or hookId was passed` | `unregister` needs one of the two. |
| `Unable to create webhook` | Server-side failure; check the `url` is reachable and well formed. |
| `Webhook registered successfully`, `Webhooks successfully unregistered`, `Webhooks successfully renewed` | Success. |

The Webhook API requires an administrator token; other users receive an HTTP 403 or the access-denied response described under [Authentication](#outcomes).

Action API
----------

The [Action API](apis/actionAPI.md#errors) documents its own status codes: 404 (unknown action), 400 (unknown project part), 403 (security level), and 500, all with empty bodies, plus HTTP 200 with `{"success": false, "message": "..."}` when the action ran but reported a failure.

Project Searches (REST)
-----------------------

The [Project Searches](rest/projectSearches.md) endpoints follow REST conventions. Successful responses are `{"projectSearches": [...]}`, `{"projects": [...]}`, `{"stations": [...]}`, `{"count": n}`, or a file attachment for exports. Failures are HTTP status codes with **empty bodies**: `404` when the search id does not exist or is not visible to the user, `422` when a search cannot be saved, and `500` for server errors.

SPIDAdb REST API
----------------

[SPIDAdb](apis/spidadbAPI.md) is a separate application with its own conventions.

* Every SPIDAdb endpoint requires an administrator token (`ROLE_MIN_ADMINISTRATOR`). Other tokens are ignored, so you get the `302` login redirect described above, not a `403`.
* Successful responses are `{"status": "ok", ...}` with the payload under a key named for the resource: `projects`/`project`, `locations`/`location`, `designs`/`design`, `photo`, `results`/`result`, `clientFiles`/`clientFile`, `version`. Save and update return `{"status":"ok","project":"<id>","locations":[...],"designs":[...]}`; delete returns `{"status":"ok"}`.
* Exceptions to the envelope: `POST /clientFiles` responds with HTTP **201**; `GET /projects/<id>.exchange` (and `format=exchange`) returns a zip file (`application/zip`); and `/status/promote` and `/status/demote` return bare maps (`{"locationsPromoted": {...}, "designsPromoted": {...}}` / `{"locationsDemoted": [...], "designsDemoted": [...]}`).
* Failures set the HTTP status **and** return a JSON body of the form:

```json
{"status": "error", "code": 400, "message": "format is invalid: valid values are calc, referenced, and exchange."}
```

| HTTP status | Message | Meaning |
|-------------|---------|---------|
| `400` | `Missing required parameter: <name>` | Usually `id`. |
| `400` | `<param> is invalid: <reason>.` | See the list of reasons below. |
| `403` | `You do not have permission to access this project` | The project belongs to a company the token's user does not administer. |
| `404` | `No resource could be found with id: <id>` | Wrong id, wrong resource type (for example a location id sent to `/projects`), or the resource has been deleted. |
| `500` | `Internal Error` | Unhandled server error. |

`<param> is invalid` reasons:

| Message | Meaning |
|---------|---------|
| `format is invalid: valid values are calc, referenced, and exchange.` | Unknown `.suffix` on the URL. |
| `version is invalid: Invalid version: <v>.` | `version` is not a schema version this server knows. |
| `version is invalid: Invalid version: <v> is greater than max version of <max>.` | Ask for `GET /versions/max` and downgrade your JSON with the [changesets](../README.md#changesets) if needed. |
| `project is invalid: project missing from request.` | Save/update needs a form field named `project` (or `exchangeFile` for `.exchange`); a raw JSON body is not accepted for projects. |
| `project is invalid: project json does not validate against the schema. <report>.` | The `<report>` lists the failing paths. Validate against the [calc project schema](../resources/schema/spidacalc/calc/project.schema) locally first. |
| `project is invalid: Error converting project json to current schema version.` | The `schema` field in the JSON names a version that cannot be converted; check the `version` value and the changesets. |
| `clientData is invalid: client data json with id <id> does not validate against the schema. <report>.` | A `clientFiles` upload failed validation against the [client data schema](../resources/schema/spidacalc/client/data.schema). |

Framework-level failures (unknown URL, wrong HTTP method, upload too large) come back as HTML, not the JSON envelope.

If you push projects through `projectAPI/createOrUpdateWithDB`, remember that projectmanager forwards your `spidaFile` to these same SPIDAdb endpoints. When that forwarding fails you only see `Project not successfully sent to SPIDAdb ...` from projectmanager; sending the same exchange file straight to `POST /spidadb/projects.exchange` will show you SPIDAdb's own `message`.

Client Checklist
----------------

* Send `apiToken` on every request as a query or form parameter; never in a header.
* Set `Content-Type: application/x-www-form-urlencoded; charset=UTF-8` for RPC `POST`s (or `multipart/form-data` when uploading files), and URL-encode JSON parameter values.
* Set a `User-Agent` that names your integration and its version.
* Disable automatic redirect following, and treat `302` as "not authenticated".
* On every response: check the HTTP status, then check `Content-Type`/parse the body, then check for `error` (RPC) or `status == "error"` (SPIDAdb) *before* reading the result. Keep `error.code` and `error.message` in your logs; the message is usually enough to diagnose the problem.
* Do not assume a non-200 body is JSON; the Project API's `403`/`404`/`500` bodies may be plain text.
* Validate `project_json` and exchange files against the schemas in this repository before sending them; the server's `INVALID_PARAM` response does not say which field failed.
* Look up ids instead of guessing: `getFlows` for `flowId`, `findStationsToMatch` for `stationId`/`source`, `isProjectNameValid` before creating.
* After a `500 INTERNAL_ERROR` on a status change, call `getProjectLogs` to read the action output.
* Pass `expireSession=true` on your last call to an application, or call `/j_spring_security_logout`, so sessions are not left open.
* Never log the `apiToken`, and redact it from any request URLs you record, since it is part of the query string.
