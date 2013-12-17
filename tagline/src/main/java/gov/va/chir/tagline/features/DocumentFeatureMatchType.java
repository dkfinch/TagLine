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
package gov.va.chir.tagline.features;

import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.beans.Line;

import java.io.Serializable;

public class DocumentFeatureMatchType extends FeatureMatchType implements DocumentFeature, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3409444175840017796L;
	private String name;
	private AnalysisLevel analysisLevel;
	private LineFeatureMatchType lfmt;
	
	public DocumentFeatureMatchType(final String name, 
			final AnalysisLevel analysisLevel,
			final LineFeatureMatchType lfmt) {
		super(name, lfmt.getMatchType(), lfmt.getTrimType());
	
		this.name = name;	// Will overwrite name
		this.analysisLevel = analysisLevel;
		this.lfmt = lfmt;
	}

	public String getName() {
		return String.format("doc_%s_%s", name, analysisLevel).toLowerCase();
	}
	
	public double getValue(final Document document) {
		double v = 0;
		double len = 0;
		
		if (getMatchType() == MatchType.FIRST_POSITION) {
			v = Double.MAX_VALUE;
		} else if (getMatchType() == MatchType.LAST_POSITION) {
			v = Double.MIN_VALUE;
		}
		
		for (Line line : document.getLines()) {
			String text = line.getText();
			
			if (getTrimType() == TrimType.TRIM) {
				text = text.trim();
			}
			
			text = lfmt.preprocess(text);
			
			switch(getMatchType()) {
				case ALL:
					v += (lfmt.getCount(text) == text.length() ? 1 : 0);
					break;
				case BEGINS:
					v += (lfmt.beginsWith(text) ? 1 : 0);
					break;
				case COUNT:
					v += lfmt.getCount(text);
					break;
				case ENDS:
					v += (lfmt.endsWith(text) ? 1 : 0);
					break;
				case EXISTS:
					v += (lfmt.exists(text) ? 1 : 0);
					break;
				case PERCENT:
					v += lfmt.getCount(text);
					len += text.length();
					break;
				case FIRST_POSITION:
					v = Math.min(v, lfmt.getFirstPosition(text));
					break;
				case LAST_POSITION:
					v = Math.max(v, lfmt.getLastPosition(text));
					break;
				default:
					throw new IllegalArgumentException(String.format(
							"MatchType %s not supported in %s", 
							getMatchType(), this.getClass().getName()));
			}
		}
		
		// Adjust values as needed for aggregation at document or line level.
		// LINE Analysis level -- how many lines this item occurred in
		// DOCUMENT Analysis level -- if this item occurred at all or total count 
		//                            in this document.
		// Analysis Level does not matter for COUNT, PERCENT, FIRST_POSITION, 
		// or LAST_POSITION.
		switch(getMatchType()) {
			case ALL:
				if (analysisLevel == AnalysisLevel.LINE) {
					// accept as is
				} else if (analysisLevel == AnalysisLevel.DOCUMENT) {
					v = (v == document.getNumLines() ? 1 : 0);	
				}
				break;
			case BEGINS:
				if (analysisLevel == AnalysisLevel.LINE) {
					// accept as is					
				} else if (analysisLevel == AnalysisLevel.DOCUMENT) {
					v = (v > 0 ? 1 : 0);
				}
				break;
			case COUNT:
				// accept as is
				break;
			case ENDS:
				if (analysisLevel == AnalysisLevel.LINE) {
					// accept as is					
				} else if (analysisLevel == AnalysisLevel.DOCUMENT) {
					v = (v > 0 ? 1 : 0);
				}
				break;
			case EXISTS:
				if (analysisLevel == AnalysisLevel.LINE) {
					// accept as is
				} else if (analysisLevel == AnalysisLevel.DOCUMENT) {
					v = (v > 0 ? 1 : 0);
				}
				break;
			case PERCENT:
				v = v / len;
				break;
			case FIRST_POSITION:
				if (v == Double.MAX_VALUE) {
					v = -1;
				}
				break;
			case LAST_POSITION:
				if (v == Double.MIN_VALUE) {
					v = -1;
				}
				break;
			default:
				throw new IllegalArgumentException(String.format(
						"MatchType %s not supported in %s", 
						getMatchType(), this.getClass().getName()));
		}
		
		return v;
	}
}
