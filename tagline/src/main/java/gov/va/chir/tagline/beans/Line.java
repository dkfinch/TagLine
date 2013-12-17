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

import java.util.HashMap;
import java.util.Map;

public class Line {
	private int lineId;
	private String text;
	private int offset;
	private String label;
	private int lineNum;
	private Map<String, Object> features;
	private String predictedLabel;
	private double predictedProbability;
	
	public Line(final int lineId, final String text) {
		this(lineId, text, null);
	}
	
	public Line(final int lineId, final String text, final String label) {
		this.lineId = lineId;
		this.text = text;
		this.label = label;
		
		features = new HashMap<String, Object>();
		lineNum = 0;
	}
	
	public void addFeature(final String feature, final Object result) {
		features.put(feature, result);
	}
	
	public Map<String, Object> getFeatures() {
		return features;
	}
	
	public String getLabel() {
		return label;
	}
	
	public int getLineId() {
		return lineId;
	}
	
	public int getLineNum() {
		return lineNum;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public String getPredictedLabel() {
		return predictedLabel;
	}
	
	public double getPredictedProbability() {
		return predictedProbability;
	}
	
	public String getText() {
		return text;
	}
	
	public void setLabel(final String label) {
		this.label = label;
	}

	public void setLineNum(final int lineNum) {
		this.lineNum = lineNum;
	}

	public void setOffset(final int offset) {
		this.offset = offset;
	}

	public void setPredictedLabel(final String predictedLabel) {
		this.predictedLabel = predictedLabel;
	}

	public void setPredictedProbability(final double predictedProbability) {
		this.predictedProbability = predictedProbability;
	}	
}
