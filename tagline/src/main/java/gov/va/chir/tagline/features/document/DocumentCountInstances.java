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
import gov.va.chir.tagline.beans.Line;
import gov.va.chir.tagline.features.DocumentFeature;
import gov.va.chir.tagline.features.LineFeature;

import java.io.Serializable;

public class DocumentCountInstances implements DocumentFeature, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7242956513844515408L;
	private LineFeature lineFeature;
	
	public DocumentCountInstances(final LineFeature lineFeature) {
		this.lineFeature = lineFeature;
	}
	
	public String getName() {
		return String.format("doc_cnt_%s", lineFeature.getName()).toLowerCase();
	}

	public double getValue(Document document) {
		int count = 0;
		
		for (Line line : document.getLines()) {
			// Assume all values must be positive
			// (negative values may indicate an item was not found)
			// @TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			count += lineFeature.getValue(line);
		}
		
		return count;
	}
}