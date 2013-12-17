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

import gov.va.chir.tagline.features.CaseType;
import gov.va.chir.tagline.features.LineFeatureMatchType;
import gov.va.chir.tagline.features.MatchType;
import gov.va.chir.tagline.features.TrimType;

import java.io.Serializable;

public class LineWordMatch extends LineFeatureMatchType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -734232436821262787L;

	private static final String SPLIT = "\\s+";
	
	private String search;
	private CaseType caseType;
	
	public LineWordMatch(final String name, final MatchType matchType, 
			final TrimType trimType, final CaseType caseType,
			final String search) {
		super(name, matchType, trimType);
		
		this.caseType = caseType;
		this.search = search;
	}
	
	@Override
	protected boolean beginsWith(final String text) {
		final String[] tokens = tokenize(text);
		
		if (tokens.length > 0) {
			return tokens[0].equals(search);
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean endsWith(final String text) {
		final String[] tokens = tokenize(text);
		
		if (tokens.length > 0) {
			return tokens[tokens.length - 1].equals(search);
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean exists(final String text) {
		boolean exists = false;
		
		final String[] tokens = tokenize(text);
		int i = 0;
		
		while (!exists && i < tokens.length) {
			if (tokens[i].equals(search)) {
				exists = true;
			}
			i++;
		}
			
		return exists;
	}
	
	@Override
	protected int getCount(final String text) {
		int count = 0;
		
		final String[] tokens = tokenize(text);
		
		for (String token : tokens) {
			if (token.equals(search)) {
				count++;
			}
		}
		
		return count;
	}
	
	@Override
	protected int getFirstPosition(final String text) {
		int pos = -1;
		
		final String[] tokens = tokenize(text);
		int i = 0;
		
		while (pos < 0 && i < tokens.length) {
			if (tokens[i].equals(search)) {
				pos = i;
			}
			i++;
		}
			
		return pos;
	}
	
	@Override
	protected int getLastPosition(final String text) {
		int pos = -1;
		
		final String[] tokens = tokenize(text);
		
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equals(search)) {
				pos = i;
			}
		}		
		
		return pos;
	}
	
	@Override
	public String getName() {
		return String.format("word_%s_%s", super.getName(), caseType).toLowerCase();
	}
	
	@Override
	protected String preprocess(String text) {
		if (caseType == CaseType.LOWER) {
			text = text.toLowerCase();
		}
		
		return text;
	}
	
	private String[] tokenize(final String text) {
		return text.split(SPLIT);
	}
}
