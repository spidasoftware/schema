/*
 * ©2009-2020 SPIDAWEB LLC
 */

package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.ChangeSet

abstract class AbstractResultsChangeSet extends AbstractCalcDesignChangeset {

	abstract boolean applyToResults(Map resultsJSON) throws ConversionException
	abstract boolean revertResults(Map resultsJSON) throws ConversionException

}