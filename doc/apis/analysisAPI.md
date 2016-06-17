# Analysis API 

_note: this api is a draft specification and will be implemented in phases_

### Job Queue

The analysis can be a somewhat long running operation.  It can take anywhere from a few seconds to several minutes depending on the complexity of the analysis involved.  When an analysis job is submitted it is placed in the queue and then analyzed.

### Callbacks

When CEE finishes one analysis job, if a `callbackUrl` property is provided, then a POST request is made to the `callbackUrl` with the complete job json in the body.  The analysis results can be retrieved from the job `output` property.

### Authentication

OAUTH is used for authentication in CEE.  Current plans are for Google and our own CAS providers to be used. 

Click the login link at [https://cee.spidastudio.com](https://cee.spidastudio.com). When you login the first time, SPIDA will receive an email and enable you manually.  Then you will receive an email when we enable you.  Now, you can submit jobs.  

To make an HTTP request you will need a client id and client secret.  Get these values from [https://cee.spidastudio.com/profile](https://cee.spidastudio.com/profile)

### Authorization

#### User Role

Users can only see their own profile and jobs that they have submitted.

#### Account Admin Role

Each account should have at least one administrator.  SPIDA will assign the account admin upon request.  The account administrator will be in charge of receiving and approving new user requests for their own account.  Also, account administrators can see all jobs submitted in their own account.

#### Admin Role

SPIDA employees can have an admin role.

### API Usage Examples with curl

```bash
#Get client id and client secret from https://cee.spidastudio.com/user/profile
oauthParams="grant_type=client_credentials&client_id=...&client_secret=..."
oauthJsonResponse=`curl -X POST --data "$oauthParams" https://cee.spidastudio.com/oauth/token`

#Get jq from https://stedolan.github.io/jq/
accessToken=`echo $oauthJsonResponse | jq -r '.access_token'`

#Create a job (from an example in this repository)
curl --request POST -H "Authorization: Bearer $accessToken" -H "Content-Type: application/json" --data @schema/resources/examples/spidacalc/cee/job.json https://cee.spidastudio.com/job
#[{"success":true,"id":"5755ad4a3c55d07876c8ae8a"}]

#Get that job back (output to a file so we can use it below)
curl --request GET -o job.json -H "Authorization: Bearer $accessToken" https://cee.spidastudio.com/job/5755ad4a3c55d07876c8ae8a
cat job.json
#[{"callbackUrl":"https://post/job/here/when/done","engineVersion":"7.0.0.0-SNAPSHOT","payload":{...

#Get that job status back
curl --request GET -H "Authorization: Bearer $accessToken" https://cee.spidastudio.com/job/status/5755ad4a3c55d07876c8ae8a
#[{"id":"5755ad4a3c55d07876c8ae8a","status":"WAITING"}]

#Update job (using file generated above)
curl --request PUT -H "Authorization: Bearer $accessToken" -H "Content-Type: application/json" --data @job.json https://cee.spidastudio.com/job
#[{"id":"5755ad4a3c55d07876c8ae8a","success":true}]

#Delete that updated job
curl --request DELETE -H "Authorization: Bearer $accessToken" https://cee.spidastudio.com/job/5755ad4a3c55d07876c8ae8a
#[{"id":"5755ad4a3c55d07876c8ae8a","success":true}]

```

[Example Job](../../resources/examples/spidacalc/cee/job.json)

----------------------------------------------------------------------------------------------------------------------------------------------------------------


### Creating Jobs

To start an analysis you will need to create and post a [job](../../resources/schema/spidacalc/cee/job.schema).  A [job](../../resources/schema/spidacalc/cee/job.schema) includes an analysis payload.  This payload includes the structure, client data, and analysis case required to analyze the pole.

#### URL

https://cee.spidastudio.com/job

#### Method

POST

#### Parameter

One [Job](../../resources/schema/spidacalc/cee/job.schema) object in the POST body (currently limited to 50MB)

Or an array of [Job](../../resources/schema/spidacalc/cee/job.schema) objects in the POST body (currently limited to 50MB)

#### Response

An array of [job-action-response](../../resources/schema/spidacalc/cee/job-action-response.schema) objects.


----------------------------------------------------------------------------------------------------------------------------------------------------------------


### Getting Jobs

Getting the job(s) will tell you status and position in queue.  If the job has been finished, then the job will include the output of the analysis.

#### URL

https://cee.spidastudio.com/job/${id}

or 

https://cee.spidastudio.com/job?ids=["${id}","${id}"]

#### Method

GET

#### Parameter

id: the id of the job that was returned upon creation

or 

ids: a json array of ids passed as a query parameter

#### Response

An array of [Job](../../resources/schema/spidacalc/cee/job.schema) objects


----------------------------------------------------------------------------------------------------------------------------------------------------------------


### Getting Job Statuses

Get each job(s) status in the queue.  This can be useful if you are not providing a `callbackUrl` and you just want to periodically check the status of jobs until they are all done.  Then you could request all the jobs and get the full objects back.

To see a list of possible statuses, see the [job-status-response](../../resources/schema/spidacalc/cee/job-status-response.schema)

#### URL

https://cee.spidastudio.com/job/status/${id}

or 

https://cee.spidastudio.com/job/status?ids=["${id}","${id}"]

#### Method

GET

#### Parameter

id: the id of the job that was returned upon creation

or 

ids: a json array of ids passed as a query parameter

#### Response

An array of [job-status-response](../../resources/schema/spidacalc/cee/job-status-response.schema) objects


----------------------------------------------------------------------------------------------------------------------------------------------------------------


### Updating Jobs

Update job(s) before they have been started.

#### URL

https://cee.spidastudio.com/job

#### Method

PUT

#### Parameter

One [Job](../../resources/schema/spidacalc/cee/job.schema) object in the POST body (currently limited to 50MB)

Or an array of [Job](../../resources/schema/spidacalc/cee/job.schema) objects in the POST body (currently limited to 50MB)

#### Response

An array of [job-action-response](../../resources/schema/spidacalc/cee/job-action-response.schema) objects.


----------------------------------------------------------------------------------------------------------------------------------------------------------------


### Deleting Jobs

Cancel job(s) before they have been started.

#### URL

https://cee.spidastudio.com/job/${id}

or 

https://cee.spidastudio.com/job?ids=["${id}","${id}"]

#### Method

DELETE

#### Parameter

id: the id of the job that was returned upon creation

or 

ids: a json array of ids passed as a query parameter

#### Response

An array of [job-action-response](../../resources/schema/spidacalc/cee/job-action-response.schema) objects.


----------------------------------------------------------------------------------------------------------------------------------------------------------------

