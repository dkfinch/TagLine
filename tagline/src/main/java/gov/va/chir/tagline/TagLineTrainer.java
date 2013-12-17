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

import gov.va.chir.tagline.beans.ClassifierType;
import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.features.Extractor;
import gov.va.chir.tagline.features.Feature;

import java.util.Collection;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public class TagLineTrainer {
	private TagLineModel tagLineModel;
	
	private Instances instances;
	private Extractor extractor;
	
	public TagLineTrainer(final ClassifierType type) throws Exception {
		this(type, new String[]{});
	}
	
	public TagLineTrainer(final ClassifierType type, final String... options) throws Exception {
		Classifier model = null;
		
		if (type.equals(ClassifierType.J48)) {
			model = new J48();
		} else {
			throw new IllegalArgumentException(String.format(
					"Classifier type not supported (%s)", type));
		}
		
		// Set classifier options
		if (options != null && options.length > 0) {
			if (model instanceof AbstractClassifier) {
				((AbstractClassifier)model).setOptions(options);
			}
		}
		
		tagLineModel = new TagLineModel();
		tagLineModel.setModel(model);
		
		instances = null;
		extractor = null;
	}
		
	public TagLineModel getTagLineModel() {
		final Instances header = new Instances(instances);
		header.delete();
		
		tagLineModel.setHeader(header);
		
		tagLineModel.setFeatures(extractor.getFeatures());
		
		return tagLineModel;
	}
	
	public void train(final Collection<Document> documents) throws Exception {
		train(documents, new Feature[]{});
	}
	
	public void train(final Collection<Document> documents, final Feature... features) throws Exception {
		if (!DatasetUtil.hasLabels(documents)) {
			throw new IllegalArgumentException(
					"All lines for training must have a label.");
		}
		
		// Setup extractor for feature calculation
		extractor = new Extractor();
		
		if (features != null && features.length > 0) {
			extractor.addFeatures(features);
		} else {
			extractor.addFeatures(Extractor.getDefaultFeatures());
		}
		
		// Setup any features that require the entire corpus
		extractor.setupCorpusProcessors(documents);

		// Calculate features at both document and line level
		for (Document document : documents) {
			extractor.calculateFeatureValues(document);
		}
				
		// Create dataset
		instances = DatasetUtil.createDataset(documents);
		
		// Remove IDs from dataset
		final Remove remove = new Remove();
		
		remove.setAttributeIndicesArray(new int[]{
				instances.attribute(DatasetUtil.DOC_ID).index(), 
				instances.attribute(DatasetUtil.LINE_ID).index()});
		
		final FilteredClassifier fc = new FilteredClassifier();
		fc.setFilter(remove);
		fc.setClassifier(tagLineModel.getModel());
		
		// Train model
		fc.buildClassifier(instances);

		tagLineModel.setModel(fc.getClassifier());
	}
}
