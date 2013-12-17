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
import gov.va.chir.tagline.beans.Line;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.AddValues;

public enum DatasetUtil {
	;
	
	public static final String DOC_ID = "doc_id";
	public static final String LINE_ID = "line_id";
	public static final String LABEL = "label";
	
	public static Instances createDataset(final Collection<Document> documents) {

		// Key = feature name | Value = number representing NUMERIC, NOMINAL, etc.
		final Map<String, Integer> featureType = new TreeMap<String, Integer>();
		
		// Key = feature name | Values = distinct values for NOMINAL values
		final Map<String, Set<String>> nominalFeatureMap = 
				new HashMap<String, Set<String>>();
	
		final Set<String> labels = new TreeSet<String>();
		final Set<String> docIds = new TreeSet<String>();
		
		// First scan -- determine attribute values
		for (Document document : documents) {
			processFeatures(document.getFeatures(), featureType, 
					nominalFeatureMap);
			docIds.add(document.getName());
			
			for (Line line : document.getLines()) {
				processFeatures(line.getFeatures(), featureType, 
						nominalFeatureMap);
				
				labels.add(line.getLabel());
			}
		}
		
		final ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		
		// Add Document and Line IDs as first two attributes
		//final Attribute docId = new Attribute(DOC_ID, (ArrayList<String>) null);
		final Attribute docId = new Attribute(DOC_ID, new ArrayList<String>(docIds));
		final Attribute lineId = new Attribute(LINE_ID);
				
		attributes.add(docId);
		attributes.add(lineId);
		
		// Build attributes
		for (String feature : featureType.keySet()) {
			final int type = featureType.get(feature);
			
			if (type == Attribute.NUMERIC) {
				attributes.add(new Attribute(feature));
			} else {
				if (nominalFeatureMap.containsKey(feature)) {
					attributes.add(new Attribute(feature, 
							new ArrayList<String>(nominalFeatureMap.get(feature))));
				}
			}
		}
		
		// Add class attribute
		Attribute classAttr = new Attribute(LABEL, new ArrayList<String>(labels));
		attributes.add(classAttr);
		
		final Instances instances = new Instances("train", attributes, documents.size());
		
		// Second scan -- add data
		for (Document document : documents) {
			final Map<String, Object> docFeatures = document.getFeatures();
			
			for (Line line : document.getLines()) {
				final Instance instance = new DenseInstance(attributes.size());
				
				final Map<String, Object> lineFeatures = line.getFeatures();
				lineFeatures.putAll(docFeatures);

				instance.setValue(docId, document.getName());
				instance.setValue(lineId, line.getLineId());
				instance.setValue(classAttr, line.getLabel());				
				
				for (Attribute attribute : attributes) {
					if (!attribute.equals(docId) && 
							!attribute.equals(lineId) &&
							!attribute.equals(classAttr)) {
						final String name = attribute.name();
						final Object obj = lineFeatures.get(name);
						
						if (obj instanceof Double) {
							instance.setValue(attribute, ((Double)obj).doubleValue());
						} else if (obj instanceof Integer) {
							instance.setValue(attribute, ((Integer)obj).doubleValue());
						} else {
							instance.setValue(attribute, obj.toString());
						}						
					}
				}
				
				instances.add(instance);
			}
		}
		
		// Set last attribute as class
		instances.setClassIndex(attributes.size() - 1);
		
		return instances;
	}
	
	public static Instances createDataset(final Document document) {
		final List<Document> documents = new ArrayList<Document>();
		documents.add(document);
		return createDataset(documents);
	}
	
	@SuppressWarnings("unchecked")
	public static Instances createDataset(final Instances header, 
			final Collection<Document> documents) throws Exception {

		// Update header to include all docIDs from the passed in documents
		// (Weka requires all values for nominal features)
		final Set<String> docIds = new TreeSet<String>();
		
		for (Document document : documents) {
			docIds.add(document.getName());			
		}
		
		final AddValues avf = new AddValues();
		avf.setLabels(StringUtils.join(docIds, ","));
		
		// Have to add 1 because SingleIndex.setValue() has a bug, expecting
		// the passed in index to be 1-based rather than 0-based. Why? I have 
		// no idea.
		// Calling path: AddValues.setInputFormat() -->
		//               SingleIndex.setUpper() -->
		//               SingleIndex.setValue()
		avf.setAttributeIndex(String.valueOf(header.attribute(DOC_ID).index() + 1));
		
		avf.setInputFormat(header);
		final Instances newHeader = Filter.useFilter(header, avf);
		
		final Instances instances = new Instances(newHeader, documents.size());
		
		// Map attributes
		final Map<String, Attribute> attrMap = new HashMap<String, Attribute>();
		
		final Enumeration<Attribute> en = newHeader.enumerateAttributes();
		
		while (en.hasMoreElements()) {
			final Attribute attr = en.nextElement();
			
			attrMap.put(attr.name(), attr);
		}
	
		attrMap.put(newHeader.classAttribute().name(), newHeader.classAttribute());
		
		final Attribute docId = attrMap.get(DOC_ID);
		final Attribute lineId = attrMap.get(LINE_ID);
		final Attribute classAttr = attrMap.get(LABEL);
		
		// Add data
		for (Document document : documents) {
			final Map<String, Object> docFeatures = document.getFeatures();

System.out.println("-----------------------------------");
System.out.println("ORIG: " + document.getName());
System.out.println(docId.toString());
			
			for (Line line : document.getLines()) {
				final Instance instance = new DenseInstance(attrMap.size());
				
				final Map<String, Object> lineFeatures = line.getFeatures();
				lineFeatures.putAll(docFeatures);
				
				instance.setValue(docId, document.getName());
				instance.setValue(lineId, line.getLineId());

				if (line.getLabel() == null) {
					instance.setMissing(classAttr);
				} else {
					instance.setValue(classAttr, line.getLabel());
				}
					
				for (Attribute attribute : attrMap.values()) {
					if (!attribute.equals(docId) && 
							!attribute.equals(lineId) &&
							!attribute.equals(classAttr)) {
						final String name = attribute.name();
						final Object obj = lineFeatures.get(name);
						
						if (obj instanceof Double) {
							instance.setValue(attribute, ((Double)obj).doubleValue());
						} else if (obj instanceof Integer) {
							instance.setValue(attribute, ((Integer)obj).doubleValue());
						} else {
							instance.setValue(attribute, obj.toString());
						}						
					}
				}
				
				instances.add(instance);
			}
		}
		
		// Set last attribute as class
		instances.setClassIndex(attrMap.size() - 1);
		
		return instances;
	}
	
	public static Instances createDataset(final Instances header, 
			final Document document) throws Exception {
		final List<Document> documents = new ArrayList<Document>();
		documents.add(document);
		return createDataset(header, documents);
	}
	
	public static boolean hasLabels(final Collection<Document> documents) {
		boolean labels = true;
		
		final Iterator<Document> iter = documents.iterator();
		
		while (labels && iter.hasNext()) {
			labels = iter.next().hasLabels();
		}

		return labels;
	}	
	
	private static void processFeatures(final Map<String, Object> features, 
			final Map<String, Integer> featureType, 
			final Map<String, Set<String>> nominalFeatureMap) {
		
		for (String feature : features.keySet()) {
			final Object obj = features.get(feature);
			
			if (!featureType.containsKey(feature)) {
				// Determine feature type (simplistic determination)
				if (obj instanceof Integer || obj instanceof Double) {
					featureType.put(feature, Attribute.NUMERIC);
				} else {
					featureType.put(feature, Attribute.NOMINAL);
					nominalFeatureMap.put(feature, new TreeSet<String>());
				}
			}
			
			// Add distinct value for nominal attributes
			if (featureType.get(feature) == Attribute.NOMINAL) {
				nominalFeatureMap.get(feature).add(obj.toString());
			}
		}
	}
}
