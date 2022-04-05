/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset

import groovy.util.logging.Slf4j

/**
 * Base converter for calc & client converter
 */
@Slf4j
abstract class AbstractConverter implements Converter {

	int defaultVersion = 1
	protected TreeMap<Integer, List<ChangeSet>> versions = new TreeMap<>() // each list will be applied when going from N-1 to N
	private int currentVersion

	abstract void applyChangeset(ChangeSet changeSet, Map json)
	abstract void revertChangeset(ChangeSet changeSet, Map json)

	int getCurrentVersion() {
		return currentVersion
	}

	void setCurrentVersion(int version) {
		currentVersion = version
	}

	void convert(Map json, int toVersion) throws ConversionException {
		int fromVersion = defaultVersion
		if (json.containsKey("version") && json.get('version')!=null) {
			fromVersion = json.get("version")
		}

		if (fromVersion == toVersion) {
			Logger.getLogger(AbstractConverter).debug("no conversion necessary")
			return // no conversion necessary
		}

		List<ChangeSet> toApply = []
		if (toVersion > fromVersion) {
			// all changesets to go TO fromVersion have already been applied to json
			((fromVersion + 1)..toVersion).each { int index ->
				if (versions.containsKey(index)) {
					toApply.addAll(versions.get(index))
				}
			}
			toApply.each { ChangeSet changeSet ->
				applyChangeset(changeSet, json)
			}
		} else {
			((toVersion + 1)..fromVersion).each { int index ->
				if (versions.containsKey(index)) {
					toApply.addAll(versions.get(index))
				}
			}
			// need to revert in reverse order
			toApply.reverseEach { ChangeSet changeSet ->
				log.info("Applying changest: ${changeSet.class.simpleName}")
				revertChangeset(changeSet, json)
			}
		}
		updateVersion(json, toVersion)
	}

	/**
	 * Changeset will be applied when upgrading to version
	 * Changeset will be reverted when downgrading from version
	 * @param version
	 * @param changeSet
	 */
	void addChangeSet(int version, ChangeSet changeSet) {
		if (!versions.containsKey(version)) {
			versions.put(version, [])
		}
		versions.get(version).add(changeSet)
	}
}
