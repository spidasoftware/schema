package com.spidasoftware.schema.conversion.changeset

import com.spidasoftware.schema.conversion.changeset.v2.FoundationChangeSet
import com.spidasoftware.schema.conversion.changeset.v2.PoleLeanChangeSet
import com.spidasoftware.schema.conversion.changeset.v3.WEPEnvironmentChangeSet
import com.spidasoftware.schema.conversion.changeset.v4.AnalysisTypeChangeSet
import com.spidasoftware.schema.conversion.changeset.v4.AssembliesChangeSet
import com.spidasoftware.schema.conversion.changeset.v4.DetailsResultsChangeset
import com.spidasoftware.schema.conversion.changeset.v4.InsulatorAttachHeightChangeSet
import com.spidasoftware.schema.conversion.changeset.v4.LocationWepChangeSet
import com.spidasoftware.schema.conversion.changeset.v4.MapLocationChangeSet
import com.spidasoftware.schema.conversion.changeset.v4.PhotoDirectionChangeSet
import com.spidasoftware.schema.conversion.changeset.v4.SpanGuyTypeChangeSet
import com.spidasoftware.schema.conversion.changeset.v4.SupportTypeChangeSet
import com.spidasoftware.schema.conversion.changeset.v4.WireConnectionIdChangeSet
import groovy.util.logging.Log4j
import net.sf.json.JSONObject

@Log4j
class ConverterUtils {

    static {
        Closure addConverter = { AbstractConverter converter ->
            converter.addChangeSet(2, new PoleLeanChangeSet())
            converter.addChangeSet(2, new FoundationChangeSet())
            converter.addChangeSet(3, new WEPEnvironmentChangeSet())
            converter.addChangeSet(4, new AnalysisTypeChangeSet())
            converter.addChangeSet(4, new SpanGuyTypeChangeSet())
            converter.addChangeSet(4, new PhotoDirectionChangeSet())
            converter.addChangeSet(4, new SupportTypeChangeSet())
            converter.addChangeSet(4, new InsulatorAttachHeightChangeSet())
            converter.addChangeSet(4, new LocationWepChangeSet())
            converter.addChangeSet(4, new WireConnectionIdChangeSet())
            converter.addChangeSet(4, new MapLocationChangeSet())
            converter.addChangeSet(4, new AssembliesChangeSet())
            converter.addChangeSet(4, new DetailsResultsChangeset())
            // add calc changesets here

            converters.put(converter.schemaPath, converter)
        }

        addConverter(new ProjectConverter())
        addConverter(new LocationConverter())
        addConverter(new DesignConverter())
        

    }

    static AbstractConverter getConverterInstance(String schemaPath) {
        String schema = getV1Root(schemaPath)
        return converters.get(schema)
    }

    protected static final Map<String, AbstractConverter> converters = [:]




    static void convertJSON(JSONObject json, int toVersion) throws ConversionException {
        if (json.containsKey("schema")) {
            AbstractConverter converter = getConverterInstance(json.getString("schema"))
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
            AbstractConverter converter = getConverterInstance(json.getString("schema"))
            if (converter == null) {
                throw new ConversionException("No converter found for ${json.getString("schema")}")
            }
            converter.convert(json, converter.getCurrentVersion())
        } else {
            throw new ConversionException("Missing schema element.")
        }
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
