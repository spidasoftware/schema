
markers = [];
infos = [];


function addPlacemark(location) {
  var kmlString = '\n\t<Placemark>' +
                       '\n\t\t<name>' + location.id + '</name>' +
                       '\n\t\t<Point>\n\t\t\t<coordinates>' +
                       location.geographicCoordinate.latitude + "," + location.geographicCoordinate.longitude +
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
      var point = new google.maps.LatLng(location.geographicCoordinate.latitude, location.geographicCoordinate.longitude);
      console.log("adding marker at " + point);
      var marker = new google.maps.Marker({
        position: point,
        map: map,
        title: location.id
      });

      var contentString = location.id;

      var info = new google.maps.InfoWindow({
           content: contentString
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