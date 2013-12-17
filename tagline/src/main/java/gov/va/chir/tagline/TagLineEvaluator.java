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

import java.io.IOException;
import java.util.Collection;
import java.util.Random;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public class TagLineEvaluator {
	private static final int NUM_FOLDS = 10;
	private static final int RANDOM_SEED = 1234;
	
	private Instances instances;
	private FilteredClassifier fc;
	
	private int numFolds;
	private int randomSeed;
	private String evaluationSummary;
	
	public TagLineEvaluator(final Collection<Document> documents) throws IOException {
		this(documents, new Feature[]{});
	}
	
	public TagLineEvaluator(final Collection<Document> documents, final Feature... features) throws IOException {
		instances = null;
		fc = null;
		
		numFolds = NUM_FOLDS;
		randomSeed = RANDOM_SEED;
		
		setup(documents, features);
	}
	
	public void evaluate(final ClassifierType type) throws Exception {
		evaluate(type, new String[]{});
	}
		
	// The problem with the evaluate method below is the cross-fold validation
	// assumes the lines are independent of one another.  This is not true.
	// We should be splitting based on documents and then train / test 
	// lines from different documents.
	public void evaluate(final ClassifierType type, final String... options) throws Exception {
		Classifier model = null;
		
		if (type.equals(ClassifierType.J48)) {
			model = new J48();
		}
		
		if (model != null) {
			// Set classifier options
			if (options != null && options.length > 0) {
				if (model instanceof AbstractClassifier) {
					((AbstractClassifier)model).setOptions(options);
				}
			}

			fc.setClassifier(model);

		    // Perform cross-validation
		    final Evaluation eval = new Evaluation(instances);
		    eval.crossValidateModel(fc, instances, numFolds, new Random(randomSeed));

			evaluationSummary = eval.toSummaryString();
		}
	}
	
	public String getEvaluationSummary() {
		return evaluationSummary;
	}
	
	public int getNumFolds() {
		return numFolds;
	}
	
	public int getRandomSeed() {
		return randomSeed;
	}
	
	public void setNumFolds(final int numFolds) {
		this.numFolds = numFolds;
	}
	
	public void setRandomSeed(final int randomSeed) {
		this.randomSeed = randomSeed;
	}
	
	private void setup(final Collection<Document> documents, final Feature... features) throws IOException {
		if (!DatasetUtil.hasLabels(documents)) {
			throw new IllegalArgumentException(
					"All lines for training must have a label.");
		}
		
		// Setup extractor for feature calculation
		final Extractor extractor = new Extractor();
		
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
		
		fc = new FilteredClassifier();
		fc.setFilter(remove);		
	}
}
