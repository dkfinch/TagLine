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
package gov.va.chir.tagline.features.line;

import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.beans.Hasher;
import gov.va.chir.tagline.beans.Line;
import gov.va.chir.tagline.features.CaseType;
import gov.va.chir.tagline.features.CorpusProcessor;
import gov.va.chir.tagline.features.LineFeature;
import gov.va.chir.tagline.features.TextCodingType;
import gov.va.chir.tagline.features.TrimType;

import java.io.File;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;

public class LineDuplicateCount implements LineFeature, CorpusProcessor, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4766921862131988956L;
	private File file;
	private TrimType trimType;
	private CaseType caseType;
	private TextCodingType textCodingType;
	
	private Hasher hasher;
	private Map<String, Integer> lineMap;
	
	public LineDuplicateCount(final TrimType trimType, 
			final CaseType caseType, final TextCodingType textCodingType,
			final File file) throws NoSuchAlgorithmException {
		
		this.file = file;
		this.trimType = trimType;
		this.caseType = caseType;
		this.textCodingType = textCodingType;
		
		lineMap = null;
		
		loadFile(file);
	}
	
	// NEED TO SAVE THE FILE SOMEWHERE
	
	private void loadFile(final File file) throws NoSuchAlgorithmException {
		if (!file.exists()) {
			hasher = new Hasher();
		} else {
			// load file -- seed will be first value
			//FileDao.load...
		}
	}
	
	public String getName() {
		return String.format("line_dupl_cnt_%s_%s", trimType, caseType).toLowerCase();
	}

	public void processCorpus(Collection<Document> documents) {
		
		for (Document document : documents) {
			for (Line line : document.getLines()) {
				final String text = preprocess(line.getText());
			}
		}
		
		// Write each line to a temp file
		
		
		// Sort temp file (external sort)
		
		
		// Build map for all lines that occur at least twice in the corpus 
		//new HashMap<String, Integer>()
		
		// Save map to file for future usage
		
	}

	public double getValue(Line line) {
		final Integer value = lineMap.get(preprocess(line.getText()));
		
		return (value == null ? 0 : value.intValue());
	}
	
	private String preprocess(final String text) {
		String t = null;
		
		if (trimType == TrimType.TRIM) {
			t = text.trim();
		} else if (trimType == TrimType.NO_TRIM) {
			t = text;
		} else {
			throw new IllegalArgumentException(String.format(
					"TrimType %s not supported in %s", 
					trimType, this.getClass().getName()));
		}
		
		if (caseType == CaseType.LOWER) {
			t = text.toLowerCase();
		}	
		
		t = textCodingType.code(t);
		
		if (textCodingType == TextCodingType.ORIGINAL_TEXT) {
			// Text is being hashed because it is unknown if the text
			// is sensitive or not. The hashes (with counts) will be 
			// released with any models.
			t = hasher.getHash(t);
		}
		
		return t;
	}	
}
