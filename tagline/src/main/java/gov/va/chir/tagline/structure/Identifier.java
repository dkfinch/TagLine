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
package gov.va.chir.tagline.structure;

import java.util.List;
import java.util.Set;

import gov.va.chir.tagline.beans.Annotation;
import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.beans.Line;

public abstract class Identifier {

	public void processDocument(final Document document) {
		if (document == null) {
			throw new IllegalArgumentException("Document must not be null");
		}
		
		boolean hasLabels = true;
		int i = 0;
		final List<Line> lines = document.getLines();
		
		while (hasLabels && i < lines.size()) {
			if (lines.get(i).getPredictedLabel() == null) {
				hasLabels = false;
			}
			i++;
		}
		
		final Set<Annotation> annotations = identifyStructures(document);
		
		if (annotations != null) {
			document.addAnnotations(annotations);
		}
	}
	
	protected abstract Set<Annotation> identifyStructures(final Document document);
}
