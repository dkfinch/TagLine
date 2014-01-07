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
package gov.va.chir.tagline.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Document {	
	private String name;
	private String text;
	private List<Line> lines;
	private Map<String, Object> features;
	private Set<Annotation> annotations;
	
	public Document(final String name, final List<Line> lines) {
		this.name = name;
		this.lines = lines;
		
		features = new HashMap<String, Object>();
		annotations = new HashSet<Annotation>();
		
		setOffsets();
		createText();
	}
	
	public Document(final String name, final String text) {
		this.name = name;
		this.text = text;
		
		features = new HashMap<String, Object>();
		annotations = new HashSet<Annotation>();
		
		lines = new ArrayList<Line>();
		createLines();
	}
	
	public void addAnnotation(final Annotation annotation) {
		annotations.add(annotation);
	}
	
	public void addFeature(final String feature, final Object result) {
		features.put(feature, result);
	}
	
	private void createLines() {
		if (text != null && text.length() > 0) {
			final String [] array = text.split("\\r?\\n");	
			
			// Need to determine number of characters used to end each line
			// (Assumes the same newline characters are used throughout the text)
			final int fullLength = text.length();
			final int reducedLength = text.replaceAll("\\r?\\n", "").length();
			final int nlChars = (fullLength - reducedLength) / array.length;		
			
			int offset = 0;
			
			for (int i = 0; i < array.length; i++) {
				final Line line = new Line(i, array[i]);
				line.setOffset(offset);
				line.setLineNum((i + 1));
				
				lines.add(line);
				
				// Add newline chars to offset
				offset += array[i].length() + nlChars;
			}
		}
	}
	
	private void createText() {
		final String newLine = System.getProperty("line.separator");
		
		final StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < lines.size(); i++) {
			sb.append(lines.get(i).getText());
			if (i < lines.size() - 1) {
				sb.append(newLine);
			}
		}
		
		text = sb.toString();
	}
	
	public Set<Annotation> getAnnotations() {
		return annotations;
	}
	
	public Map<String, Object> getFeatures() {
		return features;
	}
	
	public Line getLine(final int lineId) {
		Line line = null;
		
		int i = 0;
		while (line == null && i < lines.size()) {
			if (lines.get(i).getLineId() == lineId) {
				line = lines.get(i);
			}
			i++;
		}
		
		return line;
		
	}
	
	public List<Line> getLines() {
		return lines;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumLines() {
		return lines.size();
	}
	
	public String getText() {
		return text;
	}
	
	public boolean hasLabels() {
		boolean labels = true;
		
		int i = 0;
		
		while (labels && i < lines.size()) {
			if (lines.get(i).getLabel() == null) {
				labels = false;
			}
			i++;
		}
		
		return labels;
	}
	
	private void setOffsets() {
		int offset = 0;
		
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).setLineNum((i + 1));
			lines.get(i).setOffset(offset);
			offset += lines.get(i).getText().length();
		}
	}
}
