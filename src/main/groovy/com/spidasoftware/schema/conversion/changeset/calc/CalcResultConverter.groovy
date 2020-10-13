package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.ChangeSet

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
		}
	}

	@Override
	boolean applyChangeset(ChangeSet changeSet, Map json) {
		return false //noop: currently no change sets for results
	}

	@Override
	boolean revertChangeset(ChangeSet changeSet, Map json) {
		return false //noop: currently no change sets for results
	}
}
