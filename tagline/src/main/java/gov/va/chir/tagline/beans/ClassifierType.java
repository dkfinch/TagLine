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

public enum ClassifierType {
	J48,
	LMT,
	RandomForest,
	SVM;
	
	public static ClassifierType getClassifierType(final String classifierType) {
		for (ClassifierType ct : values()) {
			if (ct.name().equalsIgnoreCase(classifierType)) {
				return ct;
			}
		}
		return null;
	}
}
