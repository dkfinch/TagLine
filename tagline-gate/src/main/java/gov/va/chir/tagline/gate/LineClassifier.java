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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
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
import gov.va.chir.tagline.TagLineModel;
import gov.va.chir.tagline.TagLineScorer;
import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.beans.Line;
import gov.va.chir.tagline.dao.FileDao;

@CreoleResource(name = "TagLine Line Classifier", comment = "Comment goes here")
public class LineClassifier extends AbstractLanguageAnalyser {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1585745141810718012L;
	private String annotationSetName;
	private URL tagLineModelURL;

	private TagLineScorer tls;

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
		
		try {
			tls.applyModel(tlDoc);
		} catch (Exception e) {
			throw new ExecutionException("Error applying model to document", e);
		}
		
		for (Line line : tlDoc.getLines()) {
			final long start = (long)line.getOffset();
			final long end = ((long)line.getOffset() + (long)line.getText().length()); 
			
			final Annotation annotation = Utils.getAnnotation(document,
					AnnotationType.LINE, start, end);
			
			final Map<String, Object> lineFeatures = new HashMap<String, Object>();
			lineFeatures.put(FeatureType.PRED_LABEL, line.getPredictedLabel());
			lineFeatures.put(FeatureType.PRED_PROB, line.getPredictedProbability());

			FeatureMap lineFeatureMap = null;
			
			if (annotation == null) {
				lineFeatureMap = Factory.newFeatureMap();
			} else {
				lineFeatureMap = annotation.getFeatures();
			}
			
			for (Map.Entry<String, Object> entry : lineFeatures.entrySet()) {
				lineFeatureMap.put(entry.getKey(), entry.getValue());
			}
			
			if (annotation == null) {
				try {
					document.getAnnotations().add(start, end, 
							AnnotationType.LINE, lineFeatureMap);
				} catch (InvalidOffsetException e) {
					throw new ExecutionException(e);
				}
			} else {
				annotation.setFeatures(lineFeatureMap);
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

	private File getFile(final URL url) {
		File file = null;
		
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			file = new File(url.getPath());
		}
		
		return file;
	}

	public URL getTagLineModelURL() {
		return tagLineModelURL;
	}
	
	@Override
	public Resource init() throws ResourceInstantiationException {
		super.init();
		
		if (tagLineModelURL == null) {
			throw new ResourceInstantiationException("TagLine Model URL cannot be null");
		}
		
		final File modelFile = getFile(tagLineModelURL);
		
		if (modelFile == null) {
			throw new ResourceInstantiationException("TagLine Model URL cannot be converted to a file");
		}
		
		try {
			final TagLineModel model = FileDao.loadTagLineModel(modelFile);
			tls = new TagLineScorer(model);
		} catch (Exception e) {
			throw new ResourceInstantiationException("Error loading TagLine model", e);
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

	@CreoleParameter(comment = "TagLine model file (tlmod)", 
			defaultValue = "model.tlmod")
	public void setTagLineModelURL(URL tagLineModelURL) {
		this.tagLineModelURL = tagLineModelURL;
	}
}
