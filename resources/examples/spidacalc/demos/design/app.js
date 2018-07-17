var request = require('request');
var fs = require('fs');
var path = require('path');

// get the current project
// this script will copy the first design in the project a bunch of times
request("http://localhost:4560/calc/getProject", function(error, res, body) {
 var project = JSON.parse(body).result
 var currentLocation = project.leads[0].locations[0];
 	var design = currentLocation.designs[0];
		
		// for distances from 150 to 300, copy whatever the design was and set the wire
		// end point distances to that value
		var distance
		for (var distance = 150; distance<=300; distance+=10) {
			console.log("Generating design at " + distance + "feet.")
			var copy = copyDesign(design, distance)
			currentLocation.designs.push(copy)
		}
		
		// save the file
		var file = 'project.json';
		fs.writeFile(file, JSON.stringify(project, null, '  '));
		var filePath = path.resolve(file);
		// open the file
		// you could pass the file to openProject directly instead, but this way you have the
		// json file to look at to troubleshoot. Should probably be using a temp file.
		request.post({
  		url:     'http://localhost:4560/calc/openProjectFile',
  		form:    { 'projectFile': filePath}
		}, function(error, response, body){
  		if (error) {
  			console.log(error)
  		} else {
  			console.log("Project modified successfully.")
  		}
		});
})

// method to copy a design
function copyDesign(design, distance) {
		// There are better ways to copy json, but this is easy.
		var copy = JSON.parse(JSON.stringify(design));
		// set a unique label on each design
		copy.label = distance + "ft";
		// set to a draft layer so it doesn't try to make on opening (or affect other locations)
		copy.layerType = "Draft";
		var wep;
		for (wep of copy.structure.wireEndPoints) {
			wep.distance = {"value": distance, "unit": "FOOT"};
		}
		return copy;
}
