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
	void applyChangeset(ChangeSet changeSet, Map json) {
		//noop: currently no change sets for results
	}

	@Override
	void revertChangeset(ChangeSet changeSet, Map json) {
		//noop: currently no change sets for results
	}
}
