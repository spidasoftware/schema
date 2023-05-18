/*
 * ©2009-2021 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v9

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet

import groovy.transform.CompileStatic

@CompileStatic
/**
 * Custom environments were introduced in version 8.0.
 *
 * When a client data is up converted, pre fill them with our old default environments.
 * Change all references of environments in the project to pretty print form of the original Enum.
 *
 * When a client data is down converted, check if name when converted to enum form, matches {@link Environment}.
 * If it does match an enum from {@link Environment}, then convert all references in the project to that enum.
 * If it does not match an enum from {@link Environment}, then convert all references in the project to NONE.
 */
class EnvironmentClientDataChangeset extends AbstractClientDataChangeSet {

	@Override
	boolean applyToClientData(Map clientDataJSON) throws ConversionException {
		List<Map> defaultEnvironmentMaps = createDefaultEnvironmentMaps(Environment.values().collect {it.toString()})
		clientDataJSON.put("environments", defaultEnvironmentMaps)
		return true
	}

	@Override
	void applyToProject(Map projectJSON) throws ConversionException {
		super.applyToProject(projectJSON)
		Set<String> projectEnvironments = []
		projectJSON.get("leads")?.each { Map leadJSON ->
			leadJSON.get("locations")?.each { Map locationJSON ->
				applyToLocation(locationJSON, projectEnvironments)
			}
		}
		if (projectJSON.containsKey("clientData")) {
			Map clientDataJSON = projectJSON.clientData as Map
			clientDataJSON.put("environments", createDefaultEnvironmentMaps(projectEnvironments))
		}
	}

	@Override
	void applyToLocation(Map locationJSON) throws ConversionException {
		applyToLocation(locationJSON, new HashSet<String>())
	}

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		applyToDesign(designJSON, new HashSet<String>())
	}

	@Override
	boolean applyToResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = false
		Set<String> environments = []
		if (resultsJSON.containsKey("analyzedStructure")) {
			environments = applyToStructure(resultsJSON.analyzedStructure as Map)
            anyChanged = true
		}
		if (resultsJSON.containsKey("clientData")) {
			Map clientDataJSON = resultsJSON.clientData as Map
			clientDataJSON.put("environments", createDefaultEnvironmentMaps(environments))
			clientDataJSON.remove("hash")
            anyChanged = true
		}
		return anyChanged
	}

	protected void applyToLocation(Map locationJSON, Set<String> projectEnvironments) throws ConversionException {
		locationJSON.get("designs")?.each { Map designJSON ->
			applyToDesign(designJSON, projectEnvironments)
		}
	}

	protected void applyToDesign(Map designJSON, Set<String> projectEnvironments) throws ConversionException {
		projectEnvironments.addAll(applyToStructure(designJSON.structure as Map))
		if (designJSON.containsKey("analysisDetails") && ((Map)designJSON.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJSON = ((Map)designJSON.analysisDetails).detailedResults as Map
			applyToResults(detailedResultsJSON)
		}
	}

	/**
	 * @return set of environments from structure
	 */
	protected Set<String> applyToStructure(Map structureJSON) {
		Set<String> designEnvironments = new HashSet<>()
		applyPrettyPrintToEnvironment([structureJSON?.pole] as List<Map>, designEnvironments)
		applyPrettyPrintToEnvironment(structureJSON?.spanPoints as List<Map>, designEnvironments)
		applyPrettyPrintToEnvironment(structureJSON?.wireEndPoints as List<Map>, designEnvironments)
		return designEnvironments
	}

	protected List<Map> createDefaultEnvironmentMaps(Collection<String> environments) {
		return environments
				.findAll{!Environment.isNoneEnvironment(it)}
				.sort({it})
				.collect ({ [name: it, description: "N/A"] as Map})
	}

	protected void applyPrettyPrintToEnvironment(List<Map> environmentItems, Set<String> environments) {
		environmentItems?.each { Map environmentItem ->
			if (environmentItem?.environment != null) {
				String newEnvironment = Environment.makeReadableString(environmentItem.environment as String)
				environmentItem.environment = newEnvironment
				environments.add(newEnvironment)
			}
		}
	}

	@Override
	boolean revertClientData(Map clientDataJSON) throws ConversionException {
		clientDataJSON.remove("environments")
		clientDataJSON.remove("defaultEnvironment")
		(clientDataJSON.assemblies as List<Map>).each {
			revertStructure(it.assemblyStructure as Map)
		}
		return true
	}

	@Override
	void revertProject(Map projectJSON) throws ConversionException {
		super.revertProject(projectJSON)
		projectJSON.get("leads")?.each { Map leadJSON ->
			leadJSON.get("locations")?.each { Map locationJSON ->
				revertLocation(locationJSON)
			}
		}
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		if (designJSON.containsKey("analysisDetails") && ((Map)designJSON.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJSON = ((Map)designJSON.analysisDetails).detailedResults as Map
			revertResults(detailedResultsJSON)
		}
		if (designJSON.containsKey("structure")) {
			revertStructure(designJSON.structure as Map)
		}
	}


	@Override
	boolean revertResults(Map resultsJSON) throws ConversionException {
		boolean anyChanged = false
		if (resultsJSON.containsKey("clientData")) {
			Map clientDataJSON = resultsJSON.clientData as Map
			if (revertClientData(clientDataJSON) && clientDataJSON.containsKey("hash")) {
				clientDataJSON.remove("hash")
                anyChanged = true
			}
			if (resultsJSON.containsKey("analyzedStructure")) {
				boolean structureChange = revertStructure(resultsJSON.analyzedStructure as Map)
				if (structureChange) {
					anyChanged = true
				}
			}
		}
		return anyChanged
	}

	protected boolean revertStructure(Map structureJSON) {
		boolean anyChanged = false
		if (structureJSON.pole != null) {
			boolean poleChanged = revertEnvironmentToEnumFormat(structureJSON.pole as Map)
			if (poleChanged) {
				anyChanged = true
			}
		}
		(structureJSON.spanPoints as List<Map>).each { Map spanPoint ->
			boolean spanPointChanged = revertEnvironmentToEnumFormat(spanPoint)
			if (spanPointChanged) {
				anyChanged = true
			}
		}
		(structureJSON.wireEndPoints as List<Map>).each { Map wireEndPoint ->
			boolean wepChanged = revertEnvironmentToEnumFormat(wireEndPoint)
			if (wepChanged) {
				anyChanged = true
			}
		}
		return anyChanged
	}

	protected boolean revertEnvironmentToEnumFormat(Map environmentItem) {
		boolean anyChanged = false
		Environment defaultEnvironment = Environment.getEnvironment(environmentItem.environment as String)
		if (defaultEnvironment != null) {
			environmentItem.put("environment", defaultEnvironment.name())
		} else {
			environmentItem.put("environment", Environment.NONE.name())
			anyChanged = true
		}
		return anyChanged
	}
}
