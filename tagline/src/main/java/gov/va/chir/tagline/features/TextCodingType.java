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

public enum TextCodingType {
	ORIGINAL_TEXT {
		@Override
		public String code(final String text) {
			return text.replaceAll("[a-zA-Z]", "A")
					.replaceAll("[0-9]", "N")
					.replaceAll(" ", "*");
		}
	},
	CODED_TEXT {
		@Override
		public String code(final String text) {
			return text;
		}
	};
	
	public abstract String code(final String text);
}
