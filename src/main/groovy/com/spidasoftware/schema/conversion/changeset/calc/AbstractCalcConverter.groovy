/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.AbstractConverter
import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.Converter
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
 * NOTE: Removed @CompileStatic because it was throwing an exception when
 *       called from MIN -> NoSuchMethodError: groovy.lang.IntRange.<init>(ZII)V
 */
abstract class AbstractCalcConverter extends AbstractConverter {

	private static final int VERSION_ALLOWED_IN_LOCATION_DESIGN = 4

	protected boolean isVersionAllowedInLocationAndDesign(int version) {
		return version >= VERSION_ALLOWED_IN_LOCATION_DESIGN
	}

}
