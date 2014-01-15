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
