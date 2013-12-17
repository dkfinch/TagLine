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

import java.io.Serializable;

public abstract class FeatureMatchType implements Feature, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -218198930398289674L;
	private String name;
	private MatchType matchType;
	private TrimType trimType;
	
	public FeatureMatchType(final String name, final MatchType matchType, 
			final TrimType trimType) {
		this.name = name;
		this.matchType = matchType;
		this.trimType = trimType;
	}
	
	public MatchType getMatchType() {
		return matchType;
	}
	
	public String getName() {
		return String.format("%s_%s_%s", name, matchType, trimType).toLowerCase();
	}
	
	public TrimType getTrimType() {
		return trimType;
	}
}
