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

import java.io.Serializable;

public class NumLines implements DocumentFeature, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7318479960116417762L;

	public NumLines() {
		// nothing
	}
	
	public String getName() {
		return "doc_num_lines";
	}

	public double getValue(Document document) {
		return document.getNumLines();
	}
}
