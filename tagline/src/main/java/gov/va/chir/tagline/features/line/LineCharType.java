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

import gov.va.chir.tagline.features.LineFeatureMatchType;
import gov.va.chir.tagline.features.MatchType;
import gov.va.chir.tagline.features.TrimType;

import java.io.Serializable;

public class LineCharType extends LineFeatureMatchType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6264485063733263305L;
	private byte charType;

	public LineCharType(final String name, final MatchType matchType, 
			final TrimType trimType, final byte charType) {
		super(name, matchType, trimType);
		
		this.charType = charType;
	}
	
	@Override
	protected boolean beginsWith(final String text) {
		boolean found = false;

		final char[] array = text.toCharArray();
		
		if (array.length > 0 && 
				Character.getType(array[0]) == charType) {
			found = true;
		}
		
		return found;
	}
	
	@Override
	protected boolean endsWith(final String text) {
		boolean found = false;

		final char[] array = text.toCharArray();
		
		if (array.length > 0 && 
				Character.getType(array[array.length - 1]) == charType) {
			found = true;
		}
		
		return found;
	}
	
	@Override
	protected boolean exists(final String text) {
		boolean found = false;
		int i = 0;

		final char[] array = text.toCharArray();
		
		while (!found && i < text.length()) {
			if (Character.getType(array[i]) == charType) {
				found = true;
			}
			i++;
		}
		
		return found;
	}
	
	@Override
	protected int getCount(final String text) {
		int count = 0;

		final char[] array = text.toCharArray();
		
		for (int i = 0; i < array.length; i++) {
			if (Character.getType(array[i]) == charType) {
				count++;
			}
		}
		
		return count;
	}
	
	@Override
	protected int getFirstPosition(final String text) {
		int i = 0;
		int pos = -1;

		final char[] array = text.toCharArray();
		
		while (pos < 0 && i < text.length()) {
			if (Character.getType(array[i]) == charType) {
				pos = i;
			}
			i++;
		}
		
		return pos;
	}
	
	@Override
	protected int getLastPosition(final String text) {
		int pos = -1;

		final char[] array = text.toCharArray();

		for (int i = 0; i < array.length; i++) {
			if (Character.getType(array[i]) == charType) {
				pos = i;
			}
		}
		
		return pos;
	}	
}
