package gov.va.chir.tagline.gate;

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
import gov.va.chir.tagline.structure.Identifier;
import gov.va.chir.tagline.structure.SlotFillerIdentifier;
import gov.va.chir.tagline.structure.TableIdentifier;
import gov.va.chir.tagline.structure.UserDefinedIdentifier;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CreoleResource(name = "TagLine Structure Identifier", comment = "Comment goes here")
public class StructureIdentifier extends AbstractLanguageAnalyser {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8648793480493153086L;
	
	private String annotationSetName;
	private boolean identifySlotFillers;
	private boolean identifyTables;
	private HashSet<String> identifyUserDefined;
	
	private Set<Identifier> identifiers;

	@Override
	public void execute() throws ExecutionException {
		final long startTime = System.currentTimeMillis();

		if (document == null) {
			throw new ExecutionException("No document to process!");
		}

		fireStatusChanged(String.format("Running %s on %s", this.getName(),
				document.getName()));
		fireProgressChanged(0);

		final List<Annotation> annotations = gate.Utils.inDocumentOrder(
				document.getAnnotations());
		
		final List<Line> lines = new ArrayList<Line>();

		// Create dataset for identifying structures
		if (annotations != null) {
			for (Annotation annon : annotations) {
				if (annon.getType().equalsIgnoreCase(AnnotationType.LINE)) {
					try {
						final Line line = new Line(annon.getId(), 
								document.getContent().getContent(
										annon.getStartNode().getOffset(), 
										annon.getEndNode().getOffset()).toString());
						
						final FeatureMap fm = annon.getFeatures();
						
						if (fm != null) {
							final String label = (String) fm.get(FeatureType.PRED_LABEL);
							
							if (label == null) {
								throw new ExecutionException(String.format(
										"Could not find predicted label for line [%s]", 
										annon.toString()));
							}
							
							line.setPredictedLabel(label);
						}
						
						lines.add(line);
					} catch (InvalidOffsetException e) {
						throw new ExecutionException(e);
					}					
				}
			}
		}
		
		final Document tlDoc = new Document(document.getName(), lines);
		
		for (Identifier identifier : identifiers) {
			identifier.processDocument(tlDoc);
		}
		
		for (gov.va.chir.tagline.beans.Annotation annotation : tlDoc.getAnnotations()) {
			try {
				document.getAnnotations().add(
						(long)annotation.getStart(), 
						(long)annotation.getEnd(), 
						annotation.getType(),
						Factory.newFeatureMap());
			} catch (InvalidOffsetException e) {
				throw new ExecutionException(e);
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
	
	public Boolean getIdentifySlotFillers() {
		return identifySlotFillers;
	}
	
	public Boolean getIdentifyTables() {
		return identifyTables;
	}

	public HashSet<String> getIdentifyUserDefined() {
		return identifyUserDefined;
	}
	
	@Override
	public Resource init() throws ResourceInstantiationException {
		super.init();
		
		identifiers = new HashSet<Identifier>();

		if (identifySlotFillers) {
			identifiers.add(new SlotFillerIdentifier());
		}
		
		if (identifyTables) {
			identifiers.add(new TableIdentifier());
		}
		
		if (identifyUserDefined != null) {
			for (String iud : identifyUserDefined) {
				identifiers.add(new UserDefinedIdentifier(iud));
			}
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
	
	@CreoleParameter(comment = "identify slot-filler structures", 
			defaultValue = "true")
	public void setIdentifySlotFillers(Boolean identifySlotFillers) {
		this.identifySlotFillers = identifySlotFillers;
	}

	@CreoleParameter(comment = "identify table structures", 
			defaultValue = "true")
	public void setIdentifyTables(Boolean identifyTables) {
		this.identifyTables = identifyTables;
	}

	@CreoleParameter(comment = "identify user-defined structures")
	public void setIdentifyUserDefined(HashSet<String> identifyUserDefined) {
		this.identifyUserDefined = identifyUserDefined;
	}
}
