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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineRegExp extends LineFeatureMatchType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2396390302253664241L;
	private CaseType caseType;
	private Pattern pattern;  
	
	public LineRegExp(final String name, final MatchType matchType, 
			final TrimType trimType, final CaseType caseType,
			final String regExp) {
		super(name, matchType, trimType);
		
		this.caseType = caseType;
		
		if (caseType == CaseType.LOWER) {
			pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(regExp);
		}
	}
	
	@Override
	protected boolean beginsWith(final String text) {
		int pos = -1;
		
		final Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			pos = matcher.start();
		}
		
		return (pos == 0 ? true : false);
	}
	
	@Override
	protected boolean endsWith(final String text) {
		int endPos = -1;
		
		final Matcher matcher = pattern.matcher(text);
		
		while (matcher.find()) {
			endPos = matcher.end();
		}

		return (endPos == (text.length() - 1) ? true : false);
	}
	
	@Override
	protected boolean exists(final String text) {
		final Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}
	
	@Override
	protected int getCount(final String text) {
		int count = 0;

		final Matcher matcher = pattern.matcher(text);
		
		while (matcher.find()) {
			count++;
		}
		
		return count;
	}
	
	@Override
	protected int getFirstPosition(final String text) {
		int pos = -1;
		
		final Matcher matcher = pattern.matcher(text);
		
		if (matcher.find()) {
			pos = matcher.start();
		}
		
		return pos;
	}
	
	@Override
	protected int getLastPosition(final String text) {
		int pos = -1;
		
		final Matcher matcher = pattern.matcher(text);
		
		while (matcher.find()) {
			pos = matcher.start();
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
