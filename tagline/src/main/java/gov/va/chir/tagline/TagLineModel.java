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

import gov.va.chir.tagline.features.Feature;

import java.util.Collection;

import weka.classifiers.Classifier;
import weka.core.Instances;

public class TagLineModel {
	private Collection<Feature> features;
	private Instances header;
	private Classifier model;
	
	public TagLineModel() {
		features = null;
		header = null;
		model = null;
	}
	
	public Collection<Feature> getFeatures() {
		return features;
	}
	
	public Instances getHeader() {
		return header;
	}
	
	public Classifier getModel() {
		return model;
	}
	
	public void setFeatures(final Collection<Feature> features) {
		this.features = features;
	}
	
	public void setHeader(final Instances header) {
		this.header = header;
	}
	
	public void setModel(final Classifier model) {
		this.model = model;
	}
}
