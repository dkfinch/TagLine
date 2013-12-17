/**
 * This file is part of TagLine.
 *
 * TagLine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TagLine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TagLine.  If not, see <http://www.gnu.org/licenses/>.
 */
package gov.va.chir.tagline.features.document;

import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.features.DocumentFeature;
import gov.va.chir.tagline.features.TrimType;

import java.io.Serializable;

public class AvgLineLength implements DocumentFeature, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2464466890227794191L;
	private TrimType trimType;
	
	public AvgLineLength(final TrimType trimType) {
		this.trimType = trimType;
	}
	
	public String getName() {
		return String.format("doc_avg_line_len_%s", trimType).toLowerCase();
	}

	public double getValue(Document document) {
		int length = 0;
		
		if (trimType == TrimType.TRIM) {
			length = document.getText().trim().length();
		} else if (trimType == TrimType.NO_TRIM) {
			length = document.getText().length();
		} else {
			throw new IllegalArgumentException(String.format(
					"TrimType %s not supported in %s", 
					trimType, this.getClass().getName()));
		}
		
		final int numLines = document.getNumLines();
		
		return (numLines > 0 ? ((double)length / (double)numLines) : 0);
	}
}
