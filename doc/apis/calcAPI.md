Calc API
=======

Endpoints that allow you to control your locally installed SPIDACalc program.

## Implementing Apps

1. calc

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

none

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

none

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

none

Get Project
-----

Save the currently opened project

#### URL

`http://localhost:4560/calc/getProject`

#### Allowed Methods

`POST`

#### Parameters

none

#### Returns

a complete [project object](../../resources/schema/spidacalc/calc/project.schema) with results.

Exit
-----

Exit SPIDACalc. This will shutdown control services.

#### URL

`http://localhost:4560/calc/exit`

#### Allowed Methods

`POST`

#### Parameters

none

#### Returns

a complete [project object](../../resources/schema/spidacalc/calc/project.schema) with results.

Analyze Current Project
-----

Analyze the currently opened project

#### URL

`http://localhost:4560/calc/analyzeCurrentProject`

#### Allowed Methods

`POST`

#### Parameters

1. `loadCases`: an `array` of load case names to filter on.
1. `strengthCase`: a `string` strength case to include in the analysis. If not included it will use the existing strength case.

#### Returns

none

Generate a report
-----

Generate a report for the current project. Returns file location of report.
#### URL

`http://localhost:4560/calc/generateReport`

#### Allowed Methods

`POST`

#### Parameters

1. `outputDirectory`: an `string` of location to save the report.
1. `reportName`: a `string` for the preset custom report to generate. You can also call pre-existing `Project Summary Report` and `Project Details Report` by using that for your report name.
1. `format`: an `string` of either PDF or HTML.

#### Returns

A `string` to the file location

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

none
