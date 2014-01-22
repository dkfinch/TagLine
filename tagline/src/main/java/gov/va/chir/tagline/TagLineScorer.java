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

import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.dao.DatasetUtil;
import gov.va.chir.tagline.features.Extractor;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public class TagLineScorer {	
	private TagLineModel tagLineModel;
	
	private Attribute classAttr;
	private Attribute lineIdAttr;
	
	private FilteredClassifier fc;
	private Extractor extractor;
	
	public TagLineScorer(final TagLineModel tagLineModel) throws Exception {
				
		this.tagLineModel = tagLineModel;
		
		// Setup extractor for feature calculation
		extractor = new Extractor();
		extractor.addFeatures(this.tagLineModel.getFeatures());
		
		// @TODO - check if this code is necessary AND if this means a classifier can only be used one (since we remove stuff)
		classAttr = tagLineModel.getHeader().attribute(DatasetUtil.LABEL);
		this.tagLineModel.getHeader().setClass(classAttr);
		lineIdAttr = this.tagLineModel.getHeader().attribute(DatasetUtil.LINE_ID);
		final Attribute docIdAttr = this.tagLineModel.getHeader().attribute(DatasetUtil.DOC_ID);
				
		// Remove IDs from dataset (match training)
		final Remove remove = new Remove();
		
		remove.setAttributeIndicesArray(new int[]{
				docIdAttr.index(), 
				lineIdAttr.index()});
		remove.setInputFormat(this.tagLineModel.getHeader());
		
		fc = new FilteredClassifier();
		fc.setFilter(remove);
		fc.setClassifier(this.tagLineModel.getModel());
	}
	
	public void applyModel(final Document document) throws Exception {
		// Calculate features at both document and line level		
		extractor.calculateFeatureValues(document);
			
		final Instances instances = DatasetUtil.createDataset(
				tagLineModel.getHeader(), document);

		for (int i = 0; i < instances.size(); i++) {
			final double[] probs = fc.distributionForInstance(instances.get(i));
			
			int maxPos = -1;
			double maxProb = 0.0;
			
			for (int j = 0; j < probs.length; j++) {
				if (probs[j] > maxProb) {
					maxProb = probs[j];
					maxPos = j;
				}
			}
			
			if (maxPos < 0) {
				throw new IllegalStateException(String.format(
						"Predicted label array index must not be negative (%d)", 
						maxPos));
			}
			
			// Set predicted label and probability label to correct line
			final int lineId = (int)instances.get(i).value(lineIdAttr);
			document.getLine(lineId).setPredictedLabel(classAttr.value(maxPos));
			document.getLine(lineId).setPredictedProbability(maxProb);
		}
	}
}
