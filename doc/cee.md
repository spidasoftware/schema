# SPIDAcee Job API

## Authentication

Authentication to SPIDAcee is done with an API Token.  This token must be included in all requests in the URL query string as `apiToken`.

## Headers

All requests to the SPIDAcee Job Submission API must contain a `User-Agent` header.  All POST requests must contain a `Content-Type` header of `application/json`.

## Job Submission

In order to submit a job to SPIDAcee, make a POST request to `https://cee.spidastudio.com/job?aptToken=<aptToken>`.  The body of the request must contain a JSON array of object that match the schema at [#/spidacalc/cee/job.schema](https://github.com/spidasoftware/schema/blob/master/resources/schema/spidacalc/cee/job.schema).  The request must contain a `User-Agent` header and a `Content-Type` header of `application/json`.

The server will respond with an array of JSON objects matching [#/spidacalc/cee/job-action-response.schema](https://github.com/spidasoftware/schema/blob/master/resources/schema/spidacalc/cee/job-action-response.schema).  Each returned object corresponds to a submitted job.  These objects contain a success field.  If success is true, the job was submitted to SPIDAcee successfully and the job response object will contain an id field with a job id.  If false, the job was not submitted to SPIDAcee successfully and the job response object will contain an errors field containing an array of errors.  NOTE: the job is only validated at a high level when submitted.  Successful submission does not mean successful analysis.  The payload of the job will be further validated once the job is scheduled for analysis.

## Job Status

Immediately after a job is submitted it will be in `WAITING` state.  Once a resource is available to analyze the job it will be transistioned to the `STARTED` state.  Then once analysis has completed (successfully or not), the job will be in the `FINISHED` state.  Once in the `FINISHED` state, the job can be retrieved to view analysis results or analysis failure message.

## Retrieving Job Status

SPIDAcee has 2 methods available for checking job status: long-polling and immediate retrieval.  

### Method 1: immediate retrieval

This method will immediately return the current status of queried jobs.  To query for job status send a GET request to `https://cee.spidastudio.com/job/status?ids=<JSON array of ids>&apiToken=<apiToken>`.  If status for only a single job is needed, an alternate form `https://cee.spidastudio.com/job/status/<id>?apiToken=<apiToken>` can be used.

SPIDAcee will respond with an array of JSON objects containing an id and status field.

When continuously polling using the above method.  Please limit requests to no more than 1 per minute and no more than 100 jobs per request.

### Method 2: long-polling

This method should be used when immediate notification of progress is required (in order to display progress to a user for example).  This method will respond once ANY of the passed jobs meet the given status.  To poll for job status send a GET request to `https://cee.spidastudio.com/job/poll?ids=<JSON array of ids>&status=<FINISHED or STARTED>&apiToken=<apiToken>`.  Once any of the passed jobs are in the given status SPIDAcee will respond with an array of JSON objects containing id and status fields.  This array will contain only jobs in the given status.

If polling for only one job, an alternate form can be used: `https://cee.spidastudio.com/job/poll/<job id>?status=<STATUS>&apiToken=<apiToken>`

Example pseudocode:

```psuedocode
let pendingJobs = [job ids of unfinished jobs]
while length(pendingJobs) > 0 {
	try {
		//This will block until at least one of the jobs in pendingJobs is finished
		finishedJobs = pollCee(pendingJobs, 'FINISHED')
	} catch {
		//Request may timeout in the case of slow jobs.
		finishedJobs = []
	}

	//Remove finished jobs so they are not polled again
	remove(pendingJobs, finishedJobs)

	//Get finished analysis results from SPIDAcee
	retrieveFromCee(finishedJobs)

	//Update any progress display for user
	updateUI(pendingJobs)
}
```

## Retrieving Jobs / Analysis Results

Jobs can be retrieved from SPIDAcee by job id.  To retrieve SPIDAcee jobs, make a GET request to `https://cee.spidastudio.com/job?ids=<JSON array of ids>&apiToken=<apiToken>`.  SPIDAcee will respond with a JSON array of objects matching [#/spidacalc/cee/job.schema](https://github.com/spidasoftware/schema/blob/master/resources/schema/spidacalc/cee/job.schema).  If a job has finished, the job will contain an output field.  If output.success is true, output will contain a results field with analysis results.  If output.success is false, output will contain message and engineOutput fields with error information.

If retrieving only one job an alternate form can be used: `https://cee.spidastudio.com/job/<job id>?apiToken=<apiToken>`
 
