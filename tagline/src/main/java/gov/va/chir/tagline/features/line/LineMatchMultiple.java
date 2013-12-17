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

public class LineMatchMultiple extends LineFeatureMatchType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 409007310588656424L;
	private CaseType caseType;
	private LineMatch[] lmcs;
	
	public LineMatchMultiple(final String name, final MatchType matchType, 
			final TrimType trimType, final CaseType caseType,
			final String[] search) {
		super(name, matchType, trimType);
		
		this.caseType = caseType;
		
		lmcs = new LineMatch[search.length];
		
		for (int i = 0; i < lmcs.length; i++) {
			lmcs[i] = new LineMatch("", matchType, trimType, caseType, search[i]);
		}
	}
	
	@Override
	protected boolean beginsWith(final String text) {
		boolean begins = false;
		int i = 0;
		
		while (!begins && i < lmcs.length) {
			begins = lmcs[i].beginsWith(text);
			i++;
		}
		
		return begins;
	}
	
	@Override
	protected boolean endsWith(final String text) {
		boolean ends = false;
		int i = 0;
		
		while (!ends && i < lmcs.length) {
			ends = lmcs[i].endsWith(text);
			i++;
		}
		
		return ends;
	}
	
	@Override
	protected boolean exists(final String text) {
		boolean exists = false;
		int i = 0;
		
		while (!exists && i < lmcs.length) {
			exists = lmcs[i].exists(text);
			i++;
		}
		
		return exists;
	}
	
	@Override
	protected int getCount(final String text) {
		int count = 0;
		
		for (LineMatch lmc : lmcs) {
			count += lmc.getCount(text);
		}

		return count;
	}
	
	@Override
	protected int getFirstPosition(final String text) {
		int pos = Integer.MAX_VALUE;
		
		for (LineMatch lmc : lmcs) {
			int index = lmc.getFirstPosition(text);
			
			if (index < pos) {
				pos = index;
			}
		}
		
		return pos;
	}
	
	@Override
	protected int getLastPosition(final String text) {
		int pos = -1;
		
		for (LineMatch lmc : lmcs) {
			int index = lmc.getLastPosition(text);
			
			if (index > pos) {
				pos = index;
			}
		}
		
		return pos;
	}

	@Override
	public String getName() {
		return String.format("%s_%s", super.getName(), caseType).toLowerCase();
	}	
	
	@Override
	protected String preprocess(String text) {
		if (caseType == CaseType.LOWER) {
			text = text.toLowerCase();
		}
		
		return text;
	}
}
