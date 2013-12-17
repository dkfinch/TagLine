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
import gov.va.chir.tagline.features.CountType;
import gov.va.chir.tagline.features.LineFeature;
import gov.va.chir.tagline.features.TrimType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LineIndexOccurrence implements LineFeature, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4919119905225473395L;
	private TrimType trimType;
	private CaseType caseType;
	private String value;
	private int occurrence;
	
	public LineIndexOccurrence(final TrimType trimType, 
			final CaseType caseType, final String value, 
			final CountType occurrence) {
		this.trimType = trimType;
		this.caseType = caseType;
		this.value = value;
		this.occurrence = occurrence.getCount();
		
		if (this.occurrence <= 0) {
			throw new IllegalArgumentException(String.format(
					"Occurrence must be a positive integer greater than 0 "
					+ "[current value = %d]", occurrence));
		}
	}
	
	public String getName() {
		return String.format("line_idx_occur_%s_%s_%s_%d", trimType, 
				caseType, value, occurrence).toLowerCase();
	}

	public double getValue(Line line) {
		int index = -1;
		
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
		
		final List<Integer> indexes = getIndexes(text, value);
		
		if (indexes.size() >= occurrence) {
			index = indexes.get(occurrence - 1);
		}
		
		return index;
	}

	private List<Integer> getIndexes(final String text, final String value) {
		final List<Integer> indexes = new ArrayList<Integer>();
		
		int index = text.indexOf(value);
		
		while (index > -1) {
			indexes.add(index);
			
			index = text.indexOf(value, index + 1);
		}
		
		return indexes;
	}	
}
