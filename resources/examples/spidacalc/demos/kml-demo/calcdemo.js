
markers = [];
infos = [];


function addPlacemark(location) {
  var kmlString = '\n\t<Placemark>' +
                       '\n\t\t<name>' + location.label + '</name>' +
                       '\n\t\t<Point>\n\t\t\t<coordinates>' +
                       location.geographicCoordinate.coordinates[1] + "," + location.geographicCoordinate.coordinates[0] +
                        '</coordinates> \n\t\t</Point>\n\t</Placemark>';
  return kmlString;
}

function convertToKML(project) {
  console.log("convertToKML");

  // add kml namespace declaration
  var kmlString = '<?xml version="1.0" encoding="UTF-8"?>\n<kml xmlns="http://www.opengis.net/kml/2.2">';

  for (i=0; i<project.leads.length; i++) {
    var lead = project.leads[i];
    for (j=0; j<lead.locations.length; j++) {
      var location = lead.locations[j];
      kmlString = kmlString + addPlacemark(location);
    }
  }
  kmlString = kmlString + "\n</kml>";
  return kmlString;
}

function createMarkers(project) {

  for (i=0; i<project.leads.length; i++) {
    var lead = project.leads[i];
    for (j=0; j<lead.locations.length; j++) {
      var location = lead.locations[j];
      var point = new google.maps.LatLng(location.geographicCoordinate.coordinates[1], location.geographicCoordinate.coordinates[0]);
      console.log("adding marker at " + point);
      var marker = new google.maps.Marker({
        position: point,
        map: map,
        title: location.label
      });

      var resultObj = location.designs[0].analysis[0].results.find (function(element) {
      	return element.component == "Pole"
      })
      
      

      var info = new google.maps.InfoWindow({
           content: location.label + '<br>Pole Loading: ' + resultObj.actual.toFixed(2) + '%'
       });

      map.setCenter(point);

      info.open(map, marker);
      markers.push(marker);
      infos.push(info);
    }
  }
}

function processProject(jsonText) {
  var project = jQuery.parseJSON(jsonText);

    jsonDisplay.innerText = JSON.stringify(project, " ", 2);

    var kmlString = convertToKML(project);
    kmlDisplay.innerText = kmlString;

    createMarkers(project)
}

function loadProject() {

  console.log("Loading project.");
  var file = fileInput.files[0];

  var reader = new FileReader();
  var jsonText;
  reader.onload = function(e) {
    jsonText = reader.result;
    processProject(jsonText);
  }
  reader.readAsText(file);
}

window.onload = function() {
  fileInput = document.getElementById("projectFile");
  jsonDisplay = document.getElementById("json");
  kmlDisplay = document.getElementById("kml")

  fileInput.addEventListener("change", loadProject);

}
