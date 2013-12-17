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
package gov.va.chir.tagline.features.line;

import gov.va.chir.tagline.beans.Line;
import gov.va.chir.tagline.features.CaseType;
import gov.va.chir.tagline.features.LineFeature;
import gov.va.chir.tagline.features.TrimType;

import java.io.Serializable;

public class LineAllSameChar implements LineFeature, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7072749990690002945L;
	private TrimType trimType;
	private CaseType caseType;
	
	public LineAllSameChar(final TrimType trimType, final CaseType caseType) {
		this.trimType = trimType;
		this.caseType = caseType;
	}
	
	public String getName() {
		return String.format("line_all_same_char_%s_%s", trimType, caseType).toLowerCase();
	}

	public double getValue(Line line) {
		boolean allSame = false;
		
		String text = null;
		
		if (trimType == TrimType.TRIM) {
			text = line.getText().trim();
		} else if (trimType == TrimType.NO_TRIM) {
			text = line.getText();
		} else {
			throw new IllegalArgumentException(String.format(
					"TrimType %s not supported in %s", 
					trimType, this.getClass().getName()));
		}
		
		if (caseType == CaseType.LOWER) {
			text = text.toLowerCase();
		}
		
		if (text.length() > 1) {
			final char[] array = text.toCharArray();
			final char firstChar = array[0];
			
			allSame = true;
			int i = 1;
			
			while (allSame && i < array.length) {
				if (array[i] != firstChar) {
					allSame = false;
				}
				i++;
			}
		}
		
		return (allSame ? 1.0 : 0.0);
	}

}
