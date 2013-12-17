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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Annotation {
	private AnnotationType type;
	private int start;
	private int end;
	
	public Annotation(final AnnotationType type, final int start, final int end) {
		this.type = type;
		this.start = start;
		this.end = end;
	}
	
	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof Annotation){
	        final Annotation other = (Annotation) obj;
	        return new EqualsBuilder()
	            .append(type, other.getType())
	            .append(start, other.getStart())
	            .append(end, other.getStart())
	            .isEquals();
	    } else{
	        return false;
	    }
	}
	
	public int getEnd() {
		return end;
	}
	
	public int getStart() {
		return start;
	}
	
	public AnnotationType getType() {
		return type;
	}

	@Override
	public int hashCode(){
	    return new HashCodeBuilder()
	        .append(type)
	        .append(start)
	        .append(end)
	        .toHashCode();
	}	
}
