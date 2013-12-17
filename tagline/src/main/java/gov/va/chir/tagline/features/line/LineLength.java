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
import gov.va.chir.tagline.features.LineFeature;
import gov.va.chir.tagline.features.TrimType;

import java.io.Serializable;

public class LineLength implements LineFeature, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7227896501046826719L;
	private TrimType trimType;
	
	public LineLength(final TrimType trimType) {
		this.trimType = trimType;
	}
	
	public String getName() {
		return String.format("line_len_%s", trimType).toLowerCase();
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
		
		return text.length();
	}
}
