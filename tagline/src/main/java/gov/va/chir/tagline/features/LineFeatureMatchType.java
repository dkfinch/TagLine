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

import gov.va.chir.tagline.beans.Line;

import java.io.Serializable;

public abstract class LineFeatureMatchType extends FeatureMatchType implements LineFeature, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4797228023510897237L;

	public LineFeatureMatchType(final String name, final MatchType matchType,
			final TrimType trimType) {
		super(String.format("line_%s", name), matchType, trimType);
	}

	protected abstract int getCount(final String text);
	protected abstract boolean beginsWith(final String text);
	protected abstract boolean endsWith(final String text);
	protected abstract boolean exists(final String text);
	protected abstract int getFirstPosition(final String text);
	protected abstract int getLastPosition(final String text);
	
	protected String preprocess(String text) {
		return text;
	}
	
	public double getValue(final Line line) {
		double v = 0;
		String text = line.getText();
		
		if (getTrimType() == TrimType.TRIM) {
			text = text.trim();
		}
		
		text = preprocess(text);
		
		switch(getMatchType()) {
			case ALL:
				v = (getCount(text) == text.length() ? 1 : 0);
				break;
			case BEGINS:
				v = (beginsWith(text) ? 1 : 0);
				break;
			case COUNT:
				v = getCount(text);
				break;
			case ENDS:
				v = (endsWith(text) ? 1 : 0);
				break;
			case EXISTS:
				v = (exists(text) ? 1 : 0);
				break;
			case PERCENT:
				v = (double)getCount(text) / (double)text.length();
				break;
			case FIRST_POSITION:
				v = getFirstPosition(text);
				break;
			case LAST_POSITION:
				v = getLastPosition(text);
				break;
			default:
				throw new IllegalArgumentException(String.format(
						"MatchType %s not supported in %s", 
						getMatchType(), this.getClass().getName()));
		}
		
		return v;
	}
}
