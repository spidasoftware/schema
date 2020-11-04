package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.util.logging.Log4j

@Log4j
class CalcResultConverter extends AbstractCalcConverter {

	@Override
	String getSchemaPath() {
		return "/schema/spidacalc/results/results.schema"
	}

	@Override
	void updateVersion(Map json, int version) {
		boolean versionAllowedInDetailedResults = isVersionAllowedInDetailedResults(version)
		if(versionAllowedInDetailedResults) {
			json.put("version", version)
			if (json.containsKey("clientData")) {
				json.clientData.put("version", version)
			}
		}
	}

	@Override
	void applyChangeset(ChangeSet changeSet, Map json) {
		changeSet.applyToResults(json)
	}

	@Override
	void revertChangeset(ChangeSet changeSet, Map json) {
		changeSet.revertResults(json)
	}
}
