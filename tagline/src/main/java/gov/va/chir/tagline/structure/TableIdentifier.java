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
package gov.va.chir.tagline.structure;

import gov.va.chir.tagline.beans.Annotation;
import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.beans.Line;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableIdentifier extends Identifier {

	
	@Override
	protected Set<Annotation> identifyStructures(Document document) {
		final Set<Annotation> annotations = new HashSet<Annotation>();
		//AnnotationType.TABLE
		// TODO Auto-generated method stub
		
		final List <Line> lines = document.getLines();
        boolean inTable = false;
		for (int i = 0; i < lines.size(); i++) {
		
			
			int tableStart = -1;
			int tableEnd = -1;
			String thisLineText;
			String thisWorkingText;
			String nextLineText;
			String nextWorkingText;
			String lastLineText;
			String lastWorkingText;
			final Line lastLine;
			final Line nextLine;
			
			final Line thisLine = lines.get(i);
			thisLineText = thisLine.getText();
			
		
			if (getLabel(thisLine).equalsIgnoreCase("THE")) {
				if (inTable == true){
					if (tableEnd > -1){
						annotations.add(new Annotation("Table", tableStart, tableEnd));
						thisWorkingText = thisLineText.trim();
						tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
						tableEnd = -1;
					}
				}
				else {
					inTable = true;
					thisWorkingText = thisLineText.trim();
					tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
					tableEnd = -1;
				}
			}
			else if (getLabel(thisLine).equalsIgnoreCase("CLA")){
				if (inTable == true){
					if (tableEnd > -1){
						annotations.add(new Annotation("Table", tableStart, tableEnd));
						thisWorkingText = thisLineText.trim();
						tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
						tableEnd = -1;
					}
				}
				
				else {
					inTable = true;
					if (i > 0){
						lastLine = lines.get(i-1);
						lastLineText = lastLine.getText();
						if (lastLine.getPredictedLabel().equalsIgnoreCase("SHE")){
							lastWorkingText = lastLineText.trim();
							tableStart = lastLine.getOffset() + lastLineText.indexOf(lastWorkingText);
							tableEnd = -1;
						}
					}
					else {
						thisWorkingText = thisLineText.trim();
						tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
						tableEnd = -1;
					}
				}
			}
			else if (getLabel(thisLine).equalsIgnoreCase("TBI")){
				if (inTable == true){
					thisWorkingText = thisLineText.trim();
					tableEnd = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText) + thisLineText.length();
				}
				else {
					if (i < lines.size()){
						nextLine = lines.get(i+1);
						nextLineText = nextLine.getText(); 
						if (nextLine.getPredictedLabel().equalsIgnoreCase("TBI")){
							inTable = true;
							if (i > 0){
								lastLine = lines.get(i-1);
								lastLineText = lastLine.getText();
							
								if (lastLine.getPredictedLabel().equalsIgnoreCase("SHE")){
									lastWorkingText = lastLineText.trim();
									tableStart = lastLine.getOffset() + lastLineText.indexOf(lastWorkingText);
									tableEnd = -1;
								}
							}
						}
						else {
							thisWorkingText = thisLineText.trim();
							tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
							tableEnd = -1;
						}
					}
				}
			}
			
			else {
				if (inTable == true){
					if (i < lines.size()){
						nextLine = lines.get(i+1);
						nextLineText = nextLine.getText();
						if (! nextLine.getPredictedLabel().equalsIgnoreCase("TBI")){
							annotations.add(new Annotation("Table", tableStart, tableEnd));
							inTable = false;
							tableStart = -1;
							tableEnd = -1;
						}
					}
				}
			}
	
		}
		
		return annotations;
		
	}

}
