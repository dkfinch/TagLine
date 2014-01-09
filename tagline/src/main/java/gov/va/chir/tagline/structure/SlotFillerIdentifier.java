package gov.va.chir.tagline.structure;

import gov.va.chir.tagline.beans.Annotation;
import gov.va.chir.tagline.beans.AnnotationType;
import gov.va.chir.tagline.beans.Document;

import java.util.Set;

public class SlotFillerIdentifier extends Identifier {

	@Override
	protected Set<Annotation> identifyStructures(Document document) {
		//AnnotationType.SLOT;
		//AnnotationType.FILLER;
		return null;
	}

}
