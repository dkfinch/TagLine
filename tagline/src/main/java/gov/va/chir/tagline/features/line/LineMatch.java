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

public class LineMatch extends LineFeatureMatchType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8513144138047852296L;
	private String search;
	private CaseType caseType;
	
	public LineMatch(final String name, final MatchType matchType, 
			final TrimType trimType, final CaseType caseType,
			final String search) {
		super(name, matchType, trimType);
		
		this.caseType = caseType;
		this.search = search;
	}
	
	@Override
	protected boolean beginsWith(final String text) {
		return (text.indexOf(search) == 0 ? true : false);
	}
	
	@Override
	protected boolean endsWith(final String text) {
		boolean ends = false;
		
		final int index = text.lastIndexOf(search);

		if (index >= 0) {
			final int len = search.length();
			
			if ((index + len) == (text.length() - 1)) {
				ends = true;
			}
		}

		return ends;
	}
	
	@Override
	protected boolean exists(final String text) {
		return (text.indexOf(search) >= 0 ? true : false);
	}
	
	@Override
	protected int getCount(final String text) {
		int count = 0;

		int index = text.indexOf(search);
		
		while (index >= 0) {
			count++;
			index = text.indexOf(search, index + 1);
		}
		
		return count;
	}
	
	@Override
	public String getName() {
		return String.format("%s_%s", super.getName(), caseType).toLowerCase();
	}
	
	@Override
	protected int getFirstPosition(final String text) {
		return text.indexOf(search);
	}
	
	@Override
	protected int getLastPosition(final String text) {
		return text.lastIndexOf(search);
	}
	
	@Override
	protected String preprocess(String text) {
		if (caseType == CaseType.LOWER) {
			text = text.toLowerCase();
		}
		
		return text;
	}
}
