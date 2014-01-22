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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

public class DatasetFeatureSaver {
	private static final String PRED_LABEL = "predicted_label";
	private static final String PRED_PROB = "predicted_probability";
	
	private File file;
	private Set<String> docFeatures;
	private Set<String> lineFeatures;
	private boolean headerWritten;
	
	public DatasetFeatureSaver(final File file) throws IOException {
		this.file = file;
		
		docFeatures = new TreeSet<String>();
		lineFeatures = new TreeSet<String>();
		
		headerWritten = false;		
	}
	
	private void saveHeader() throws IOException {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(DatasetUtil.DOC_ID);
		builder.append("\t");

		builder.append(DatasetUtil.LINE_ID);
		builder.append("\t");

		for (String feature : docFeatures) {
			builder.append(feature);
			builder.append("\t");
		}

		for (String feature : lineFeatures) {
			builder.append(feature);
			builder.append("\t");
		}

		builder.append(DatasetUtil.LABEL);
		builder.append("\t");

		builder.append(PRED_PROB);
		builder.append("\t");

		builder.append(PRED_LABEL);
		builder.append(System.getProperty("line.separator"));
		
		FileUtils.writeStringToFile(file, builder.toString(), false);
	}
	
	public void saveRecord(final Document document) throws IOException {
		final Collection<Document> documents = new ArrayList<Document>();
		documents.add(document);
		saveRecords(documents);
	}
	
	public void saveRecords(final Collection<Document> documents) throws IOException {
		if (documents == null || documents.isEmpty()) {
			throw new IllegalArgumentException("There must be at least one document.");
		}

		// Write first line (if needed)
		if (!headerWritten) {
			setupFeatures(documents.iterator().next());
			saveHeader();

			headerWritten = true;
		}
		
		final StringBuilder builder = new StringBuilder();
		
		for (Document document : documents) {			
			final Map<String, Object> docValues = document.getFeatures();
			
			for (Line line : document.getLines()) {
				builder.append(document.getName());
				builder.append("\t");

				builder.append(line.getLineId());
				builder.append("\t");

				for (String feature : docFeatures) {
					builder.append(docValues.get(feature));
					builder.append("\t");
				}
			
				final Map<String, Object> lineValues = line.getFeatures();
				
				for (String feature : lineFeatures) {
					builder.append(lineValues.get(feature));
					builder.append("\t");
				}
				
				builder.append(line.getLabel());
				builder.append("\t");

				builder.append(line.getPredictedProbability());
				builder.append("\t");

				builder.append(line.getPredictedLabel());
				builder.append(System.getProperty("line.separator"));
			}
		}
		
		FileUtils.writeStringToFile(file, builder.toString(), true);
	}
	
	private void setupFeatures(final Document document) {
		docFeatures.addAll(document.getFeatures().keySet());
		
		if (document.getNumLines() > 0) {
			lineFeatures.addAll(document.getLine(0).getFeatures().keySet());
		}
	}	
}
