# Analysis API 

_note: this api is a draft specification and will be implemented in phases_

### OAUTH

OAUTH is used for authentication in CEE.  Current plans are for Google and our own CAS providers to be used. Click the login link at [https://cee.spidastudio.com](https://cee.spidastudio.com). Once authenticated, contact us so we can manually enabled you.  (This will eventually be automated.)

### Job Queue

The analysis can be a somewhat long running operation.  It can take anywhere from a few seconds to several minutes depending on the complexity of the analysis involved.  When an analysis job is submitted it is placed in the queue and then analyzed.

### Callbacks

When CEE finishes one analysis job, a POST request is made to the `callbackUrl` with the complete job json in the body.  The analysis results can be retrieved from the job `output` property.

### API Usage Example

```
$ curl -H "Content-Type: application/json" --data @schema/resources/examples/spidacalc/cee/job.json http://localhost:8080/job
[{"success":true,"id":"5755ad4a3c55d07876c8ae8a"}]

$ curl -o job.json http://localhost:8080/job/5755ad4a3c55d07876c8ae8a

$ cat job.json
[{"callbackUrl":"https://post/job/here/when/done","engineVersion":"7.0.0.0-SNAPSHOT","payload":{...

$ curl --request PUT -H "Content-Type: application/json" --data @job.json http://localhost:8080/job
[{"success":true,"id":"5755ae963c55d07876c8ae8b"}]

$ curl --request DELETE http://localhost:8080/job/5755ae963c55d07876c8ae8b
[{"success":true}]

$ curl -H "Content-Type: application/json" --data @schema/resources/examples/spidacalc/cee/job.json http://localhost:8080/job/validate
[{"success":true,"errors":[]}]

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

One [Job](../../resources/schema/spidacalc/cee/job.schema) object in the POST body

Or an array of [Job](../../resources/schema/spidacalc/cee/job.schema) objects in the POST body

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


### Updating Jobs

Update job(s) before they have been started.

#### URL

https://cee.spidastudio.com/job

#### Method

PUT

#### Parameter

One [Job](../../resources/schema/spidacalc/cee/job.schema) object in the POST body

Or an array of [Job](../../resources/schema/spidacalc/cee/job.schema) objects in the POST body

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


### Validating Jobs

You can also just validate jobs without adding them to the queue.  This can be a helpful tool during development and/or debugging.

#### URL

https://cee.spidastudio.com/job/validate

#### Method

POST

#### Parameter

One [Job](../../resources/schema/spidacalc/cee/job.schema) object in the POST body

Or an array of [Job](../../resources/schema/spidacalc/cee/job.schema) objects in the POST body

#### Response

An array of [job-action-response](../../resources/schema/spidacalc/cee/job-action-response.schema) objects.


