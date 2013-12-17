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
package gov.va.chir.tagline;

import gov.va.chir.tagline.beans.AnnotationType;
import gov.va.chir.tagline.beans.Document;

import java.util.Arrays;

public class Annotator {
	private AnnotationType[] types;
	
	public Annotator(final AnnotationType... types) {
		// Assumes all documents will be annotated for the same types.
		// Reason: May be there is some code that needs to be initialized for
		// an annotation type.
		// If we want this to be separate then we can just pass AnnotationType
		// in the annotate method.
		
		this.types = types;
		Arrays.sort(this.types);
	}
	
	public void annotate(final Document document) {
		if (annotate(AnnotationType.TABLE)) {
			annotateTables(document);
		} else if (annotate(AnnotationType.SLOT) ||
				annotate(AnnotationType.FILLER) ||
				annotate(AnnotationType.SLOT_FILLER) ||
				annotate(AnnotationType.HEADER)) {
			annotateSlotFiller(document);
			
			// DO WE WANT TO REMOVE ANY ANNOTATIONS THAT WERE NOT EXPLICITLY ASKED FOR? 
		}
	}
	
	private void annotateTables(final Document document) {
		// @TODO add annotations here
	}
	
	private void annotateSlotFiller(final Document document) {
		// @TODO add annotations here		
	}
	
	private boolean annotate(final AnnotationType type) {
		if (Arrays.binarySearch(types, type) >= 0) {
			return true;
		} else {
			return false;
		}
	}
}
