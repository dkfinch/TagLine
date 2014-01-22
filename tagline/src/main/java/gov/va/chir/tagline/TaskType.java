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
package gov.va.chir.tagline;

public enum TaskType {
	TRAIN("t"),
	EVALUATE("e"),
	SCORE("s"),
	CREATE("c"),
	IDENTIFY("i");
	
	public static TaskType fromString(String abbr) {
        if (abbr != null) {
            for (TaskType opt : TaskType.values()) {
                if (abbr.equalsIgnoreCase(opt.abbr)) {
                    return opt;
                }
            }
        }
        throw new IllegalArgumentException(String.format(
                "No constant with value %s found", abbr));
    }
	
	private String abbr;
	
	private TaskType(final String abbr) {
		this.abbr = abbr;
	}

    public String getAbbreviation() {
        return this.abbr;
    }	
}
