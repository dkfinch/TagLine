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
package gov.va.chir.tagline.features.linedocument;

import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.beans.Line;
import gov.va.chir.tagline.features.LineDocumentFeature;

import java.io.Serializable;

public class LinePosition implements LineDocumentFeature, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3926692127770697373L;

	public LinePosition() {
		// nothing
	}

	public double getValue(Line line, Document document) {
		return (double)line.getLineNum() / (double)document.getNumLines();
	}

	public String getName() {
		return "line-doc_line_pos";
	}
}
