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
