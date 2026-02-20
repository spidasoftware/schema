/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset

import com.spidasoftware.schema.conversion.changeset.calc.*
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import com.spidasoftware.schema.conversion.changeset.client.ClientDataConverter
import com.spidasoftware.schema.conversion.changeset.v2.*
import com.spidasoftware.schema.conversion.changeset.v3.*
import com.spidasoftware.schema.conversion.changeset.v4.*
import com.spidasoftware.schema.conversion.changeset.v5.*
import com.spidasoftware.schema.conversion.changeset.v6.*
import com.spidasoftware.schema.conversion.changeset.v7.*
import com.spidasoftware.schema.conversion.changeset.v8.*
import com.spidasoftware.schema.conversion.changeset.v9.*
import com.spidasoftware.schema.conversion.changeset.v10.*
import com.spidasoftware.schema.conversion.changeset.v11.*
import com.spidasoftware.schema.conversion.changeset.v12.*
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils

@Slf4j
class ConverterUtils {

    static final int currentVersion = 13

    static {
        addCalcConverter(new CalcProjectConverter())
        addCalcConverter(new CalcLocationConverter())
        addCalcConverter(new CalcDesignConverter())
        addResultConverter(new CalcResultConverter())
        addClientDataConverter(new ClientDataConverter())
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
        converter.addChangeSet(7, new AnalysisDetailsChangeset())
        converter.addChangeSet(8, new RelativeElevationChangeSet())
        converter.addChangeSet(8, new MaxTensionGroupChangeset())
        converter.addChangeSet(8, new AdvancedWireChangeSet())
        converter.addChangeSet(8, new ResultsWireChangeSet())
        converter.addChangeSet(9, new InsulatorStrengthChangeSet())
        converter.addChangeSet(9, new EnvironmentClientDataChangeset())
        converter.addChangeSet(9, new EnvironmentDescriptionChangeset())
        converter.addChangeSet(9, new ExtremeWindLoadCaseChangeset())
        converter.addChangeSet(9, new ClearancesChangeset())
        converter.addChangeSet(10, new IceDensityChangeSet())
        converter.addChangeSet(10, new TerrainLayerChangeSet())
        converter.addChangeSet(10, new LoadCaseChangeSet())
        converter.addChangeSet(10, new LoadCaseNameChangeSet())
        converter.addChangeSet(10, new DecimalDirectionsChangeset())
        converter.addChangeSet(10, new TemperatureOverridesChangeSet())
        converter.addChangeSet(11, new ClientPoleSettingTypeChangeSet())
        converter.addChangeSet(11, new UpdateTempOverridesChangeSet())
        converter.addChangeSet(11, new WireStateLabelChangeSet())
        converter.addChangeSet(11, new PoleCutTopChangeSet())
        converter.addChangeSet(11, new WireMountedEquipmentChangeSet())
        converter.addChangeSet(11, new MomentAtHeightChangeSet())
        converter.addChangeSet(11, new TrussChangeSet())
        converter.addChangeSet(12, new CSAMaxWindTemperatureChangeset())
        converter.addChangeSet(12, new PushBraceHeightChangeSet())
        converter.addChangeSet(12, new TensionOverrideChangeset())
        converter.addChangeSet(12, new ComponentBraceChangeset())
        converter.addChangeSet(12, new SidedPoleChangeset())
        converter.addChangeSet(12, new CSAMaxWindLoadFactorsChangeSet())

        // delete this once there is a "real" changeset
        converter.addChangeSet(13, new AbstractClientDataChangeSet() {
            @Override
            boolean applyToClientData(Map clientDataJSON) throws ConversionException {
                return false
            }

            @Override
            boolean revertClientData(Map clientDataJSON) throws ConversionException {
                return false
            }
        })
        // add calc changesets above here

        converter.setCurrentVersion(currentVersion)
        converters.put(converter.schemaPath, converter)
    }

    static void addClientDataConverter(ClientDataConverter converter) {
        converter.addChangeSet(8, new AdvancedWireChangeSet())
        converter.addChangeSet(9, new InsulatorStrengthChangeSet())
        converter.addChangeSet(9, new EnvironmentClientDataChangeset())
        converter.addChangeSet(9, new EnvironmentDescriptionChangeset())
        converter.addChangeSet(9, new ExtremeWindLoadCaseChangeset())
        converter.addChangeSet(9, new ClearancesChangeset())
        converter.addChangeSet(10, new IceDensityChangeSet())
        converter.addChangeSet(10, new LoadCaseChangeSet())
        converter.addChangeSet(10, new LoadCaseNameChangeSet())
        converter.addChangeSet(10, new DecimalDirectionsChangeset())
        converter.addChangeSet(10, new TemperatureOverridesChangeSet())
        converter.addChangeSet(11, new ClientPoleSettingTypeChangeSet())
        converter.addChangeSet(11, new MomentAtHeightChangeSet())
        converter.addChangeSet(11, new UpdateTempOverridesChangeSet())
        converter.addChangeSet(11, new WireStateLabelChangeSet())
        converter.addChangeSet(11, new WireMountedEquipmentChangeSet())
        converter.addChangeSet(11, new TrussChangeSet())
        converter.addChangeSet(12, new CSAMaxWindTemperatureChangeset())
        converter.addChangeSet(12, new TensionOverrideChangeset())
        converter.addChangeSet(12, new ComponentBraceChangeset())
        converter.addChangeSet(12, new SidedPoleChangeset())
        converter.addChangeSet(12, new CSAMaxWindLoadFactorsChangeSet())
        // add client data changesets above here

        converter.setCurrentVersion(currentVersion)
        converters.put(converter.schemaPath, converter)
    }

    static void addResultConverter(CalcResultConverter converter) {
        converter.addChangeSet(8, new AdvancedWireChangeSet())
        converter.addChangeSet(8, new ResultsWireChangeSet())
        converter.addChangeSet(9, new InsulatorStrengthChangeSet())
        converter.addChangeSet(9, new EnvironmentClientDataChangeset())
        converter.addChangeSet(9, new ExtremeWindLoadCaseChangeset())
        converter.addChangeSet(9, new ClearancesChangeset())
        converter.addChangeSet(10, new IceDensityChangeSet())
        converter.addChangeSet(10, new LoadCaseChangeSet())
        converter.addChangeSet(10, new LoadCaseNameChangeSet())
        converter.addChangeSet(10, new DecimalDirectionsChangeset())
        converter.addChangeSet(11, new ClientPoleSettingTypeChangeSet())
        converter.addChangeSet(11, new PoleCutTopChangeSet())
        converter.addChangeSet(11, new MomentAtHeightChangeSet())
        converter.addChangeSet(11, new WireStateLabelChangeSet())
        converter.addChangeSet(11, new WireMountedEquipmentChangeSet())
        converter.addChangeSet(11, new TrussChangeSet())
        converter.addChangeSet(12, new CSAMaxWindTemperatureChangeset())
        converter.addChangeSet(12, new PushBraceHeightChangeSet())
        converter.addChangeSet(12, new TensionOverrideChangeset())
        converter.addChangeSet(12, new DetailedResultsVersionChangeset())
        converter.addChangeSet(12, new ComponentBraceChangeset())
        converter.addChangeSet(12, new SidedPoleChangeset())
        converter.addChangeSet(12, new CSAMaxWindLoadFactorsChangeSet())
        // add result changesets above here

        converter.setCurrentVersion(currentVersion)
        converters.put(converter.schemaPath, converter)
    }

    static Converter getConverterInstance(String schemaPath) {
        String schema = getV1Root(schemaPath)
        return converters.get(schema)
    }

    protected static final Map<String, Converter> converters = [:]

    //example: [4, 3, 2]
    static LinkedHashSet<Integer> getPossibleVersionsNewestToOldest(){
    	return currentVersion..2 as LinkedHashSet<Integer>
    }

    /**
     * Returns the schema version for an engine version.
     * Provides a way to determine the schema version for a detailed results json.
     * @param engineVersion  The engine version as found in the detailed results json, or null if the version cannot be determined.
     */
    static Integer getSchemaVersion(String engineVersionStr) {
        if(engineVersionStr == null) {
            return null
        }
        BigDecimal engineVersion
        try {
            int indexOfSecondDot = StringUtils.ordinalIndexOf(engineVersionStr, ".", 2)
            String engineVersionMajorMinor = engineVersionStr.substring(0, indexOfSecondDot)
            engineVersion = new BigDecimal(engineVersionMajorMinor)
        } catch (Exception ex) {
            log.warn("unable to parse engine version ${engineVersionStr}", ex)
            return null
        }
        switch (engineVersion) {
            case 26.0:
                return 13
            case 25.0:
                return 12
            case 24.1:
                return 11
            case 24.0:
                return 10
            case 8.1:
                return 9
            case 8.0:
                return 9
            case 7.3:
                return 8
            case {  it < 7.3 }:
                return 7 // there are no results changesets before schema version 8
        }
        log.warn("unknown schema version for engine version ${engineVersionStr}")
        return null
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

    static void convertJSON(Map json) throws ConversionException {
       convertJSON(json, currentVersion)
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
