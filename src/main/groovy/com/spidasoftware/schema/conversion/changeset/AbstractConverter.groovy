package com.spidasoftware.schema.conversion.changeset

import groovy.transform.CompileStatic
import org.apache.log4j.Logger

/**
 * Converts JSON from one version to another
 *
 * Conversion process:
 *
 * 	-- each public release is a minor version number increment
 *  -- each change to the schema is a changeset
 *  -- conversion will bring data up or down to new version
 *  -- a list of changesets is kept separately per versioned root schema - calc project, for example
 *
 */
@CompileStatic
abstract class AbstractConverter implements Converter {

	int defaultVersion = 1
	protected TreeMap<Integer, List<ChangeSet>> versions = new TreeMap<>() // each list will be applied when going from N-1 to N

	abstract void applyChangeset(ChangeSet changeSet, Map json)
	abstract void revertChangeset(ChangeSet changeSet, Map json)

	int getCurrentVersion() {
		return versions.lastKey()
	}

	public void convert(Map json, int toVersion) throws ConversionException {
		int fromVersion = defaultVersion
		if (json.containsKey("version") && json.get('version')!=null) {
			fromVersion = json.get("version")
		}

		if (fromVersion == toVersion) {
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
				Logger.getLogger(AbstractConverter).info("Applying changest: ${changeSet.class.simpleName}")
				revertChangeset(changeSet, json)
			}
		}
		json.put("version", toVersion)
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
