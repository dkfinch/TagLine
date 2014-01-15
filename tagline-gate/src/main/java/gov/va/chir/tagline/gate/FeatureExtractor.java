package gov.va.chir.tagline.gate;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Map;

import gate.Annotation;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;
import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.beans.Line;
import gov.va.chir.tagline.features.Extractor;

@CreoleResource(name = "TagLine Feature Extractor", comment = "Comment goes here")
public class FeatureExtractor extends AbstractLanguageAnalyser {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8032413768595725210L;
	
	private String annotationSetName;
	private boolean lineFeatures;
	private boolean documentFeatures;
	private boolean lineDocumentFeatures;

	private Extractor extractor;

	@Override
	public void execute() throws ExecutionException {
		final long startTime = System.currentTimeMillis();

		if (document == null) {
			throw new ExecutionException("No document to process!");
		}

		fireStatusChanged(String.format("Running %s on %s", this.getName(),
				document.getName()));
		fireProgressChanged(0);

		final Document tlDoc = new Document(document.getName(), 
				document.getContent().toString());
		
		extractor.calculateFeatureValues(tlDoc);

		final long docStart = 0L;
		final long docEnd = document.getContent().size();
		
		final Annotation docAnnon = Utils.getAnnotation(document, 
				AnnotationType.DOC, docStart, docEnd);
		
		final Map<String, Object> docFeatures = tlDoc.getFeatures();
		
		FeatureMap docFeatureMap = null;
		
		if (docAnnon == null) {
			docFeatureMap = Factory.newFeatureMap();	
		} else {
			docFeatureMap = docAnnon.getFeatures();
		}
		
		for (Map.Entry<String, Object> entry : docFeatures.entrySet()) {
			docFeatureMap.put(entry.getKey(), entry.getValue());
		}

		if (docAnnon == null) {
			try {
				document.getAnnotations().add(0L, document.getContent().size(), 
						AnnotationType.DOC, docFeatureMap);
			} catch (InvalidOffsetException e) {
				throw new ExecutionException(e);
			}			
		} else {
			docAnnon.setFeatures(docFeatureMap);
		}
		
		for (Line line : tlDoc.getLines()) {
			final long lineStart = (long)line.getOffset();
			final long lineEnd = ((long)line.getOffset() + (long)line.getText().length()); 
			
			final Annotation lineAnnon = Utils.getAnnotation(document,
					AnnotationType.LINE, lineStart, lineEnd);
			
			final Map<String, Object> lineFeatures = line.getFeatures();
			
			FeatureMap lineFeatureMap = null;
			
			if (lineAnnon == null) {
				lineFeatureMap = Factory.newFeatureMap();
			} else {
				lineFeatureMap = lineAnnon.getFeatures();
			}
			
			for (Map.Entry<String, Object> entry : lineFeatures.entrySet()) {
				lineFeatureMap.put(entry.getKey(), entry.getValue());
			}
			
			if (lineAnnon == null) {
				try {
					document.getAnnotations().add((long)line.getOffset(), 
							((long)line.getOffset() + (long)line.getText().length()), 
							AnnotationType.LINE, lineFeatureMap);
				} catch (InvalidOffsetException e) {
					throw new ExecutionException(e);
				}
			} else {
				lineAnnon.setFeatures(lineFeatureMap);
			}
		}
		
		fireProcessFinished();
		fireStatusChanged(String
				.format("Finished %s on %s in %s seconds",
						this.getName(),
						document.getName(),
						NumberFormat
								.getInstance()
								.format((double) 
										(System.currentTimeMillis() - startTime) / 1000)));
	}

	public String getAnnotationSetName() {
		return annotationSetName;
	}

	public Boolean getDocumentFeatures() {
		return documentFeatures;
	}

	public Boolean getLineDocumentFeatures() {
		return lineDocumentFeatures;
	}

	public Boolean getLineFeatures() {
		return lineFeatures;
	}

	@Override
	public Resource init() throws ResourceInstantiationException {
		super.init();
		
		extractor = new Extractor();

		try {
			extractor.addFeatures(Extractor.getDefaultFeatures());
		} catch (IOException e) {
			throw new ResourceInstantiationException(e);
		}

		if (!lineFeatures) {
			extractor.removeLineFeatures();
		}

		if (!documentFeatures) {
			extractor.removeDocumentFeatures();
		}

		if (!lineDocumentFeatures) {
			extractor.removeLineDocumentFeatures();
		}

		return this;
	}

	@Override
	public void reInit() throws ResourceInstantiationException {
		init();
	}

	@Optional
	@RunTime
	@CreoleParameter(comment = "name of the annotation set used for output")
	public void setAnnotationSetName(String annotationSetName) {
		this.annotationSetName = annotationSetName;
	}

	@CreoleParameter(comment = "calculate document-based features", 
			defaultValue = "true")
	public void setDocumentFeatures(Boolean documentFeatures) {
		this.documentFeatures = documentFeatures;
	}

	@CreoleParameter(comment = "calculate line-document-based features", 
			defaultValue = "true")
	public void setLineDocumentFeatures(Boolean lineDocumentFeatures) {
		this.lineDocumentFeatures = lineDocumentFeatures;
	}

	@CreoleParameter(comment = "calculate line-based features", 
			defaultValue = "true")
	public void setLineFeatures(Boolean lineFeatures) {
		this.lineFeatures = lineFeatures;
	}
}
