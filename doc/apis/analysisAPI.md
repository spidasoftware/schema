Analysis API 
===========

* note: this api is a draft specification and will be implemented in phases *

### OAUTH

OAUTH is used for authentication in CEE.  Current plans are for Google and our own CAS providers to be used. Once you authenticate, we currently will have to enable your user.

Jobs
----

The analysis can be a somewhat long running operation.  Anywhere from a few seconds to several minutes depending on the complexity of the analysis involved.  An analysis job is submitted and placed in the queue.  Once it is ready to be processed it is started.  Upon completion the job results are POSTed to the specified call back url.  

### CREATE

To start an analysis you will create a job by submitting a complete Analysis payload.

#### URL

    https://cee.spidastudio.com/job

#### Method

    POST

#### Parameter

An [Analysis](../../resources/schema/spidacalc/cee/analysis.schema) object in the POST body

#### Response

A [Job](../../resources/schema/spidacalc/cee/job.schema) object

### GET

Getting the job will tell you its updated status and position.

#### URL

    https://cee.spidastudio.com/job/${id}

#### Method

    GET

#### Parameter

  id: the id of the job that was returned upon creation

#### Response

  A [Job](../../resources/schema/spidacalc/cee/job.schema) object

### DELETE

To cancel any job before it has been started, you can delete the job with the following method.

#### URL

    https://cee.spidastudio.com/job/${id}

#### Method

    DELETE

#### Parameter

id: the id of the job that was returned upon creation

#### Response

none
