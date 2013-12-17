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
import gov.va.chir.tagline.features.ComparisonType;
import gov.va.chir.tagline.features.CountType;
import gov.va.chir.tagline.features.LineFeature;
import gov.va.chir.tagline.features.TrimType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LineContiguousCharType implements LineFeature, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5506495719267894129L;
	private String name;
	private TrimType trimType;
	private ComparisonType comparisonType;
	private byte charType;
	private int contiguousLength;
	
	public LineContiguousCharType(final String name, 
			final TrimType trimType, final ComparisonType comparisonType,
			final byte charType, final CountType contiguousLength) {
		this.name = name;
		this.trimType = trimType;
		this.comparisonType = comparisonType;
		this.charType = charType;
		this.contiguousLength = contiguousLength.getCount();
	}
	
	public String getName() {
		return String.format("line_num_cont_%s_%s_%d_%s", name, comparisonType, 
				contiguousLength, trimType).toLowerCase();
	}

	private Map<Integer, Integer> getNumContiguousChars(final String text) {
		final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		final char[] array = text.trim().toCharArray();
		
		int contiguous = 0;
		
		for (char c : array) {
			if (Character.getType(c) == charType) {
				contiguous++;
			} else if (contiguous > 0) {
				if (map.containsKey(contiguous)) {
					map.put(contiguous, map.get(contiguous) + 1);
				} else {
					map.put(contiguous, 1);
				}
				
				contiguous = 0;
			}
		}
		
		return map;
	}
	
	public double getValue(Line line) {
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
		
		final Map<Integer, Integer> map = getNumContiguousChars(text);
		
		int count = 0;
		
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			if (comparisonType == ComparisonType.EQUAL) {
				if (entry.getKey() == contiguousLength) {
					count = entry.getValue();
				}
			} else if (comparisonType == ComparisonType.GREATER_THAN_EQUAL_TO) {
				if (entry.getKey() >= contiguousLength) {
					count += entry.getValue();
				}
			} else {
				throw new IllegalArgumentException(String.format(
						"ComparisonType %s not supported in %s", 
						comparisonType, this.getClass().getName()));
			}
		}
		
		return count;
	}
}
