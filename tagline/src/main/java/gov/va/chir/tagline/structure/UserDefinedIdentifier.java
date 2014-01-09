package gov.va.chir.tagline.structure;

import gov.va.chir.tagline.beans.Annotation;
import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.beans.Line;

import java.util.HashSet;
import java.util.Set;

public class UserDefinedIdentifier extends Identifier {
	private String className;
	
	public UserDefinedIdentifier(final String className) {
		this.className = className;
	}
	
	@Override
	protected Set<Annotation> identifyStructures(Document document) {
		final Set<Annotation> annotations = new HashSet<Annotation>();
		
		int start = -1;
		int end = -1;
		
		for (Line line : document.getLines()) {			
			if (line.getPredictedLabel().equalsIgnoreCase(className)) {
				if (start == -1) {
					start = line.getOffset();
				}

				// Must be contiguous, thus use end position of this line
				end = line.getOffset() + line.getText().length();
			} else if (start != -1 && end != -1) {
				annotations.add(new Annotation(className, start, end));
				start = -1;
				end = -1;
			}
		}

		if (start != -1 && end != -1) {
			annotations.add(new Annotation(className, start, end));
		}

		return annotations;
	}
}
