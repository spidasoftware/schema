Sample simple json project files.

full_project.json
	An export of one of our projects. Shows most of possible uses of the data format.

minimal_project_no_designs.json
	Shows how you can import just GPS coordinates and project structure. A more likely use would be similar to this but with address and other location data filled in.

minimal_project_with_gps.json
	Shows the basic use of the design api to fill in a pole, with a gps point on the location.

minimal_project_with_forms.json
	Shows a minimal project with custom form data on a location.

minimal_project_damage-analysis.json
	Shows a pole with a damage and the resulting analysis.

connectivity_two_locations.json
	Shows two locations connected with connectionIds on wire end points, wires, span points, and span guys.

lidar_measured_project.json
	Shows a "Measured" design built from a lidar/point cloud extraction: measured pole, attachment heights, wire end points, and a measured sag. See doc/lidar_integration.md.

lidar_connected_locations_project.json
	Shows a three-pole lidar-derived run: connected locations (connectionIds), connected wires through the middle pole (connectedWire), and measured sags on every span.

lidar_end_to_end_project.json
	A semi-realistic five-pole lidar-derived line: connected locations, connected wires, field-measured sags for the tension data on every span, terrain points under every span (including a creek crossing), deadend guying, and a transformer.

lidar_terrain_layer_project.json
	A five-pole lidar-derived connected line that delivers ground as a project-level terrain layer (terrainLayers + appliedTerrainLayers assigned to the Measured Design layer) instead of individual terrainPoints along each span.

multiple_projects.json
	Shows the exchange format for sending more than one project at a time.

project-with-assemblies.json, project-with-input-assemblies.json, input-assembly.json, input_assembly_c5.json
	Show the use of assemblies on structures.