
window.onload = function() {

  console.log("onload");
  var postParameters = {
    clientFile: 'Demo.client'
  };

  var posting = $.post("http://localhost:4560/clientData/framingUnits", postParameters);
  posting.done(function(data) {
     console.log("post returned");
     console.log(data);

     var clientSelect = document.getElementById("client");
     for (i=0;i<data.result.length; i++) {
        var element= document.createElement("option");
        element.innerText = data.result[i];
        clientSelect.appendChild(element);
     }
   });

}

units = ["35-3", "A2.3", "G1.2.15kVA"];

function onAdd() {
     var clientSelect = document.getElementById("client");
     var option = clientSelect.options[clientSelect.selectedIndex]
     if (option) {
       var value = option.value;
//       units.push(value);
       var designSelect = document.getElementById("design");
       var element= document.createElement("option");
       element.innerText = value;
       designSelect.appendChild(element);
     }
}

function frameInCalc() {
   var basePlan = {
      customName: "frameWithoutPrimary",
      framingEndPoints: [
        {
          direction: 95,
          distance: {
            unit: "FOOT",
            value: 50
          },
          framingWireItems: [
            {
              code: "1/0 ACSR",
              owner: {
                name: "ACME POWER",
                industry: "UTILITY",
                uuid: "43e130a1-3c21-41c9-b420-0e5b966eb2f2"
              },
              usageGroup: "PRIMARY"
            },
            {
              code: "1/0 AAC Triplex",
              owner: {
                name: "ACME POWER",
                industry: "UTILITY",
                uuid: "43e130a1-3c21-41c9-b420-0e5b966eb2f2"
              },
              usageGroup: "NEUTRAL"
            }
          ]
        },
        {
          direction: 270,
          distance: {
            unit: "FOOT",
            value: 60
          },
          framingWireItems: [
          {
            code: "1/0 ACSR",
            owner: {
              name: "ACME POWER",
              industry: "UTILITY",
              uuid: "43e130a1-3c21-41c9-b420-0e5b966eb2f2"

            },
              usageGroup: "PRIMARY"
            },
            {
              code: "1/0 AAC Triplex",
              owner: {
                name: "ACME POWER",
                industry: "UTILITY",
                uuid: "43e130a1-3c21-41c9-b420-0e5b966eb2f2"

              },
              usageGroup: "NEUTRAL"
            }
          ]
        }
      ],
      framingUnitItems: units
    };

  var project = {
  "name": "project-1",
  "clientFile": "TrainingDemo_4.0.client",
  "leads": [
    {
      "locations": [
        {
          "designs": [
            {
              "framingPlan": basePlan
            }
          ],
          "geographicCoordinate": {
            "latitude": 40.609780700000002,
            "longitude": -81.3688492
          }
        }
      ]
    }
  ],
  "schema": "https://raw.github.com/spidasoftware/schema/master/resources/schema/spidacalc/calc/project.schema"
  }

   var parameters = {
    project: JSON.stringify(project)
   }

   console.log(JSON.stringify(project, " " , 2));

   var posting = $.post("http://localhost:4560/calc/openProject", parameters);
   posting.done(function(data) {
     console.log(data);
     analyze();
   });

}

function analyze() {

   var parameters = {
    loadCases: JSON.stringify(["Medium"])
   }
  var posting = $.post("http://localhost:4560/calc/analyzeCurrentProject", parameters);
   posting.done(function(data) {
     console.log(data);
     generateReport();
   });
}

function generateReport() {
  var parameters = {
    report: "Project Details Report",
    format: "HTML"
   }
  var posting = $.post("http://localhost:4560/calc/generateReport", parameters);
   posting.done(function(data) {
     console.log(data);
     console.log(data.result);
     var iframe = document.getElementById("results");
     iframe.setAttribute("src", data.result);
   });
}
