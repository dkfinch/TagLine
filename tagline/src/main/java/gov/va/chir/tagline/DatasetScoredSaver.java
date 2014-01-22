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

import org.apache.commons.io.FileUtils;

public class DatasetScoredSaver {
	private static final String PRED_LABEL = "predicted_label";
	private static final String TEXT = "text";
	
	private File file;
	private boolean headerWritten;
	
	public DatasetScoredSaver(final File file) throws IOException {
		this.file = file;
		
		headerWritten = false;		
	}
	
	private void saveHeader() throws IOException {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(DatasetUtil.DOC_ID);
		builder.append("\t");

		builder.append(DatasetUtil.LINE_ID);
		builder.append("\t");

		builder.append(PRED_LABEL);
		builder.append("\t");
		
		builder.append(TEXT);
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
			saveHeader();

			headerWritten = true;
		}
		
		final StringBuilder builder = new StringBuilder();
		
		for (Document document : documents) {			
			for (Line line : document.getLines()) {
				builder.append(document.getName());
				builder.append("\t");

				builder.append(line.getLineId());
				builder.append("\t");

				builder.append(line.getPredictedLabel());
				builder.append("\t");
				
				builder.append(line.getText());
				builder.append(System.getProperty("line.separator"));				
			}
		}
		
		FileUtils.writeStringToFile(file, builder.toString(), true);
	}	
}
