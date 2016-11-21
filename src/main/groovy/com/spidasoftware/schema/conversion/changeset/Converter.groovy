package com.spidasoftware.schema.conversion.changeset

import com.spidasoftware.schema.conversion.changeset.v2.*
import com.spidasoftware.schema.conversion.changeset.v3.*
import com.spidasoftware.schema.conversion.changeset.v4.*
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import net.sf.json.JSONObject

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
@Log4j
@CompileStatic
class Converter {

	String                                      schemaPath
	int                               			defaultVersion = 1
	protected TreeMap<Integer, List<ChangeSet>> versions = new TreeMap<>() // each list will be applied when going from N-1 to N

	int getCurrentVersion() {
		return versions.lastKey()
	}

	public void convert(JSONObject json, int toVersion) throws ConversionException {
		int fromVersion = defaultVersion
		if (json.containsKey("version")) {
			fromVersion = json.getInt("version")
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
				changeSet.apply(json)
			}
		} else {
			((toVersion + 1)..fromVersion).each { int index ->
				if (versions.containsKey(index)) {
					toApply.addAll(versions.get(index))
				}
			}
			// need to revert in reverse order
			toApply.reverseEach { ChangeSet changeSet ->
				changeSet.revert(json)
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
	protected void addChangeSet(int version, ChangeSet changeSet) {
		if (!versions.containsKey(version)) {
			versions.put(version, [])
		}
		versions.get(version).add(changeSet)
	}

	protected static final Map<String, Converter> converters = [:]


	static Converter getConverterInstance(String schemaPath) {
		String schema = getV1Root(schemaPath)
		return converters.get(schema)
	}

	static void convertJSON(JSONObject json, int toVersion) throws ConversionException {
		if (json.containsKey("schema")) {
			Converter converter = getConverterInstance(json.getString("schema"))
			if (converter == null) {
				throw new ConversionException("No converter found for ${json.getString("schema")}")
			}
			converter.convert(json, toVersion)
		} else {
			throw new ConversionException("Missing schema element.")
		}
	}

    /**
     * convert to the current version
     */
    static void convertJSON(JSONObject json) throws ConversionException {
        if (json.containsKey("schema")) {
            Converter converter = getConverterInstance(json.getString("schema"))
            if (converter == null) {
                throw new ConversionException("No converter found for ${json.getString("schema")}")
            }
            converter.convert(json, converter.getCurrentVersion())
        } else {
            throw new ConversionException("Missing schema element.")
        }
    }

	static {

		Converter calcProjectConverter = new Converter(schemaPath: "/schema/spidacalc/calc/project.schema")
		calcProjectConverter.addChangeSet(2, new PoleLeanChangeSet())
		calcProjectConverter.addChangeSet(2, new FoundationChangeSet())
		calcProjectConverter.addChangeSet(3, new WEPEnvironmentChangeSet())
		calcProjectConverter.addChangeSet(4, new AnalysisTypeChangeSet())
		calcProjectConverter.addChangeSet(4, new SpanGuyTypeChangeSet())
		calcProjectConverter.addChangeSet(4, new PhotoDirectionChangeSet())
		calcProjectConverter.addChangeSet(4, new SupportTypeChangeSet())
		calcProjectConverter.addChangeSet(4, new InsulatorAttachHeightChangeSet())
        calcProjectConverter.addChangeSet(4, new LocationWepChangeSet())
		// add calc project changesets here
		converters.put(calcProjectConverter.schemaPath, calcProjectConverter)
	}

	/**
	 * If using the full url, convert to a path relative to resources.
	 * @param string
	 * @return
	 */
	static protected String getV1Root(String string) {
		int index = string.indexOf("/schema")
		return string.substring(index)
	}


}
