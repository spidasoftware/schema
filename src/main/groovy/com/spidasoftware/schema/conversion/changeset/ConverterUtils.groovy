/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset

import com.spidasoftware.schema.conversion.changeset.calc.*
import com.spidasoftware.schema.conversion.changeset.client.ClientDataConverter
import com.spidasoftware.schema.conversion.changeset.v2.*
import com.spidasoftware.schema.conversion.changeset.v3.*
import com.spidasoftware.schema.conversion.changeset.v4.*
import com.spidasoftware.schema.conversion.changeset.v5.*
import com.spidasoftware.schema.conversion.changeset.v6.*
import groovy.util.logging.Log4j

@Log4j
class ConverterUtils {

    static final int CURRENT_CALC_VERSION = 7
    static final int CURRENT_CLIENT_DATA_VERSION = 1

    static {
        addCalcConverter(new CalcProjectConverter())
        addCalcConverter(new CalcLocationConverter())
        addCalcConverter(new CalcDesignConverter())
        addClientDataConverter()
    }

    static void addCalcConverter(AbstractCalcConverter converter) {
        converter.addChangeSet(2, new PoleLeanChangeSet())
        converter.addChangeSet(2, new FoundationChangeSet())
        converter.addChangeSet(3, new WEPEnvironmentChangeSet())
        converter.addChangeSet(4, new AnalysisTypeChangeSet())
        converter.addChangeSet(4, new SpanGuyTypeChangeSet())
        converter.addChangeSet(4, new PhotoDirectionChangeSet())
        converter.addChangeSet(4, new SupportTypeChangeSet())
        converter.addChangeSet(4, new InsulatorAttachHeightChangeSet())
        converter.addChangeSet(4, new ConnectivityChangeSet())
        converter.addChangeSet(4, new MapLocationChangeSet())
        converter.addChangeSet(4, new AssembliesChangeSet())
        converter.addChangeSet(4, new DetailedResultsChangeset())
        converter.addChangeSet(4, new ClientItemVersionChangeSet())
        converter.addChangeSet(4, new PoleTemperatureChangeset())
        converter.addChangeSet(4, new WEPInclinationChangeSet())
        converter.addChangeSet(4, new WireEndPointPlacementChangeSet())
        converter.addChangeSet(4, new RemoveSchemaAndVersionChangeSet())
        converter.addChangeSet(4, new PointLoadItemChangeSet())
        converter.addChangeSet(4, new GuyAttachPointChangeSet())
        converter.addChangeSet(4, new DesignLayerChangeSet())
        converter.addChangeSet(4, new DamageRsmChangeSet())
        converter.addChangeSet(5, new RemoveAdditionalPropertiesChangeset())
        converter.addChangeSet(5, new InputAssemblyDistanceDirectionChangeset())
        converter.addChangeSet(6, new RemoveDetailedResultsChangeset())
        converter.addChangeSet(6, new SummaryNoteObjectChangeset())
        converter.addChangeSet(6, new RevertBundleChangeset())
        converter.addChangeSet(6, new RemoveTensionResultsChangeset())
        // add calc changesets here

        converter.setCurrentVersion(CURRENT_CALC_VERSION)
        converters.put(converter.schemaPath, converter)
    }

    static void addClientDataConverter() {
        def converter = new ClientDataConverter()
        // add client data changesets here

        converter.setCurrentVersion(CURRENT_CLIENT_DATA_VERSION)
        converters.put(converter.schemaPath, converter)
    }

    static Converter getConverterInstance(String schemaPath) {
        String schema = getV1Root(schemaPath)
        return converters.get(schema)
    }

    protected static final Map<String, Converter> converters = [:]

    //example: [4, 3, 2]
    static LinkedHashSet<Integer> getPossibleCalcVersionsNewestToOldest(){
    	return CURRENT_CALC_VERSION..2 as LinkedHashSet<Integer>
    }

    static LinkedHashSet<Integer> getPossibleClientDataVersionsNewestToOldest(){
        return CURRENT_CALC_VERSION..1 as LinkedHashSet<Integer>
    }

    static void convertJSON(Map json, int toVersion) throws ConversionException {
        if (json.containsKey("schema")) {
            Converter converter = getConverterInstance(json.get("schema"))
            if (converter == null) {
                throw new ConversionException("No converter found for ${json.get("schema")}")
            }
            converter.convert(json, toVersion)
        } else {
            throw new ConversionException("Missing schema element.")
        }
    }

    /*
     ** Method is used inside calc
     */
    static void convertToCurrentCalcVersionJSON(Map json) throws ConversionException {
       convertJSON(json, CURRENT_CALC_VERSION)
    }

    /*
     ** Method is used inside calc
     */
    static void convertToCurrentClientDataVersionJSON(Map json) throws ConversionException {
        convertJSON(json, CURRENT_CALC_VERSION)
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
