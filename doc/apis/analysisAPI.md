Analysis API 
===========

_note: this api is a draft specification and will be implemented in phases_

### OAUTH

OAUTH is used for authentication in CEE.  Current plans are for Google and our own CAS providers to be used. Click the login link at [https://cee.spidastudio.com](https://cee.spidastudio.com). Once authenticated, contact us so we can manually enabled you.  (This will eventually be automated.)

Jobs
----

The analysis can be a somewhat long running operation.  Anywhere from a few seconds to several minutes depending on the complexity of the analysis involved. An analysis job is submitted and placed in the queue.  Once it is ready to be processed it is started.  Upon completion the job results are POSTed to the specified call back url.


----------------------------------------------------------------------------------------------------------------------------------------------------------------


### CREATE

To start an analysis you will need to create and post a job.  A job includes an analysis payload.  This payload includes the structure, client data, and analysis case required to analyze the pole.
See the job schema [here](../../resources/schema/spidacalc/cee/job.schema).

#### URL

https://cee.spidastudio.com/job

#### Method

POST

#### Parameter

One [Job](../../resources/schema/spidacalc/cee/job.schema) object in the POST body
Or an array of [Job](../../resources/schema/spidacalc/cee/job.schema) objects in the POST body

#### Response

A list of objects.  Objects will include a `valid` boolean, an `id` if the job was valid, an array of `errors` if invalid.


----------------------------------------------------------------------------------------------------------------------------------------------------------------


### READ

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


### UPDATE

Update job(s) before they have been started.

#### URL

https://cee.spidastudio.com/job

#### Method

PUT

#### Parameter

One [Job](../../resources/schema/spidacalc/cee/job.schema) object in the POST body
Or an array of [Job](../../resources/schema/spidacalc/cee/job.schema) objects in the POST body

#### Response

A list of objects.  Objects will include a `valid` boolean, an `id` if the job was valid, an array of `errors` if invalid.


----------------------------------------------------------------------------------------------------------------------------------------------------------------


### DELETE

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

an array of objects with a `removed` boolean property


----------------------------------------------------------------------------------------------------------------------------------------------------------------


### VALIDATE

You can also just validate jobs without adding them to the queue.  This can be a helpful tool during development and/or debugging.

#### URL

https://cee.spidastudio.com/job/validate

#### Method

POST

#### Parameter

One [Job](../../resources/schema/spidacalc/cee/job.schema) object in the POST body
Or an array of [Job](../../resources/schema/spidacalc/cee/job.schema) objects in the POST body

#### Response

A list of objects.  Objects will include a `valid` boolean and an array of `errors` if invalid.


