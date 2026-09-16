Calc API
=======

Endpoints that allow you to control your locally installed SPIDAcalc program.  Located at [http://localhost:4560/calc/](http://localhost:4560/calc/).

## Implementing Apps

1. SPIDAcalc

## Responses

The Calc service only accepts requests from the local machine. Every response has a `Content-Type` of `application/json` and is wrapped in the generic [method response](../../resources/schema/general/method_response.schema) envelope. A successful call puts the return value under `result`:

    {"result": true}

    {"result": {"reportPath": "C:\\reports\\Project Summary Report.pdf"}}

The value under `result` is what each method's _Returns_ section below describes. Methods with no meaningful return value respond with `{"result": true}`. `getProject` returns the project object nested under `result`, not at the top level, so unwrap `result` before validating against the project schema.

A failed call returns an `error` object instead of `result`, and the HTTP status is set to match the error code:

    {"error": {"code": "BAD_REQUEST", "message": "The report 'Missing Report' does not exist."}}

| Error code           | HTTP status | Meaning |
|----------------------|-------------|---------|
| `BAD_REQUEST`        | 400 | A required parameter is missing or invalid, the file does not exist, or the project JSON failed schema validation. The `message` contains the details. |
| `FORBIDDEN`          | 403 | The request was not permitted. |
| `MISSING_RESOURCE`   | 404 | The requested resource (such as the service descriptor) could not be found. |
| `MISSING_METHOD`     | 405 | The method name in the URL is not defined for this service. |
| any other code       | 500 | An internal error such as `INTERNAL_ERROR`, a project that could not be opened, or a user cancelling an action in the SPIDAcalc UI. |

Methods
======

Open Project
-----

Opens a project with the given object

#### URL

`http://localhost:4560/calc/openProject`

#### Allowed Methods

`POST`

#### Parameters

1. `project`: a _required_ [project object](../../resources/schema/spidacalc/calc/project.schema).

#### Returns

`{"result": true}` once the project has been opened.

Open Project File
-----

Opens a project with the given file path

#### URL

`http://localhost:4560/calc/openProjectFile`

#### Allowed Methods

`POST`

#### Parameters

1. `projectFile`: a _required_ absolute `string` file path.

#### Returns

`{"result": true}` once the project has been opened.

Save Current Project
-----

Save the currently opened project

#### URL

`http://localhost:4560/calc/saveCurrentProject`

#### Allowed Methods

`POST`

#### Parameters

1. `projectFile`: a url `string` of the location to save to.

#### Returns

`{"result": true}` once the project has been saved.

Get Project
-----

Get the currently opened project as JSON

#### URL

`http://localhost:4560/calc/getProject`

#### Allowed Methods

`POST`

#### Parameters

none

#### Returns

`{"result": { ... }}` where the value of `result` is a complete [project object](../../resources/schema/spidacalc/calc/project.schema) with results. The project is nested under `result`; the response body is not itself a project object.

Exit
-----

Exit SPIDAcalc. This will shutdown control services.

#### URL

`http://localhost:4560/calc/exit`

#### Allowed Methods

`POST`

#### Parameters

none

#### Returns

`{"result": true}` if SPIDAcalc is shutting down, or `{"result": false}` if the application could not be closed (for example, the user cancelled a prompt to save unsaved changes).

Generate a report
-----

Generate a report for the current project. Returns file location of report.
#### URL

`http://localhost:4560/calc/generateReport`

#### Allowed Methods

`POST`

#### Parameters

1. `outputDirectory`: a `string` of location to save the report.
1. `reportName`: a `string` for the preset custom report to generate. You can also call pre-existing `Project Summary Report` and `Project Details Report` by using that for your report name.
1. `format`: a `string` of either PDF or HTML.

#### Returns

`{"result": {"reportPath": "/pathToReport/report.pdf"}}`


Generate an Excel report
-----

Generate an Excel report for the current project. Returns the location of the report file.
#### URL

`http://localhost:4560/calc/generateExcelReport`
#### Parameters

1. `outputDirectory`: a `string` of location to save the report.

#### Returns

`{"result": {"reportPath": "/pathToReport/report.xlsx"}}`


Run Script
-----

Run a script on the current project.

#### URL

`http://localhost:4560/calc/runScript`

#### Allowed Methods

`POST`

#### Parameters

1. `scriptName`: a _required_ `string` of the script to run.

#### Returns

`{"result": true}` once the script has been started.
