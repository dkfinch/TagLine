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
package gov.va.chir.tagline.gate;

import gate.Annotation;
import gate.AnnotationSet;

import java.util.Iterator;

public enum Utils {
	;
	
	public static Annotation getAnnotation(final gate.Document document, 
			final String type, final long start, final long end) {
		Annotation annotation = null;
		
		final AnnotationSet as = document.getAnnotations().getCovering(
				type, start, end);
		
		if (as != null) {
			final Iterator<Annotation> iter = as.iterator();

			// Takes first annotation of the specified name and position
			// (Really we assume only one annotation of this type with this
			// offset exists.) 
			if (iter.hasNext()) {
				annotation = iter.next();
			}
		}
		
		return annotation;
	}
}
