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
        int tableStart = -1;
		int tableEnd = -1;
		for (int i = 0; i < lines.size(); i++) {
		
			System.out.println("***********");
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
			
		
			if (getLabel(thisLine).equalsIgnoreCase("TBH")) {
				System.out.println("Found a TBH");
				if (inTable == true){
					System.out.println("In A Table");
					if (tableEnd > -1){
						System.out.println("Table End Set");
						System.out.println("Start = " + tableStart + "  End = " + tableEnd);
						annotations.add(new Annotation("Table", tableStart, tableEnd));
						System.out.println("Table Annotated");
						thisWorkingText = thisLineText.trim();
						tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
						System.out.println("Table Start Set.");
						tableEnd = -1;
						System.out.println("Table End reset.");
						System.out.println("Start = " + tableStart + "  End = " + tableEnd);
					}
				}
				else {
					inTable = true;
					System.out.println("Table Started");
					thisWorkingText = thisLineText.trim();
					tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
					System.out.println("Table Start Set");
					System.out.println("Start = " + tableStart + "  End = " + tableEnd);
				}
			}
			else if (getLabel(thisLine).equalsIgnoreCase("TDT")) {
				System.out.println("Found a TDT");
				if (inTable == true){
					System.out.println("In A Table");
					if (tableEnd > -1){
						System.out.println("Table End Set");
						System.out.println("Start = " + tableStart + "  End = " + tableEnd);
					}
					else{
						System.out.println("Table End Not Set");
						thisWorkingText = thisLineText.trim();
						tableEnd = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText) + thisWorkingText.length();
						System.out.println("Table End Set");
						System.out.println("Start = " + tableStart + "  End = " + tableEnd);
					}
				}
				else {
					inTable = true;
					System.out.println("Table Started");
					thisWorkingText = thisLineText.trim();
					tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
					System.out.println("Table Start Set");
					System.out.println("Start = " + tableStart + "  End = " + tableEnd);
				}
			}
			else if (getLabel(thisLine).equalsIgnoreCase("CLA")){
				System.out.println("Found a CLA");
				if (inTable == true){
					System.out.println("In A Table");
					if (tableEnd > -1){
						System.out.println("Table End Set.");
						System.out.println("Start = " + tableStart + "  End = " + tableEnd);
						annotations.add(new Annotation("Table", tableStart, tableEnd));
						System.out.println("Table Annotatedt.");
						thisWorkingText = thisLineText.trim();
						tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
						System.out.println("Table Start Set.");
						tableEnd = -1;
						System.out.println("Table End Reset.");
						System.out.println("Start = " + tableStart + "  End = " + tableEnd);
					}
				}
				
				else {
					inTable = true;
					System.out.println("Table Started");
					if (i > 0){
						lastLine = lines.get(i-1);
						System.out.println("Going after lastline.");
						lastLineText = lastLine.getText();
						if (getLabel(lastLine).equalsIgnoreCase("SHE")){
							System.out.println("Its a Section Header.");
							lastWorkingText = lastLineText.trim();
							tableStart = lastLine.getOffset() + lastLineText.indexOf(lastWorkingText);
							System.out.println("Set Table Start.");
							System.out.println("Start = " + tableStart + "  End = " + tableEnd);
						}
						else{
							System.out.println("Its not a Section Header.");
							thisWorkingText = thisLineText.trim();
							tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
							System.out.println("Set Table Start.");
							System.out.println("Start = " + tableStart + "  End = " + tableEnd);
						}
					}
					else {
						thisWorkingText = thisLineText.trim();
						tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
						System.out.println("Set Table Start.");
						System.out.println("Start = " + tableStart + "  End = " + tableEnd);
					}
				}
			}
			else if (getLabel(thisLine).equalsIgnoreCase("TBI")){
				System.out.println("Found a TBI");
				if (inTable == true){
					System.out.println("In A Table");
					thisWorkingText = thisLineText.trim();
					tableEnd = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText) + thisWorkingText.length();
					System.out.println("Set Table End");
					System.out.println("Start = " + tableStart + "  End = " + tableEnd);
				}
				else {
					if (i +1 < lines.size()){
						nextLine = lines.get(i+1);
						nextLineText = nextLine.getText();
						System.out.println("Going after nextline.");
						if (getLabel(nextLine).equalsIgnoreCase("TBI")){
							System.out.println("Nextline is a TBI");
							inTable = true;
							System.out.println("Table Started");
							if (i > 0){
								lastLine = lines.get(i-1);
								lastLineText = lastLine.getText();
								System.out.println("Going after lastline.");
								if (getLabel(lastLine).equalsIgnoreCase("SHE")){
									System.out.println("Its a Section Header.");
									lastWorkingText = lastLineText.trim();
									thisWorkingText = thisLineText.trim();
									tableStart = lastLine.getOffset() + lastLineText.indexOf(lastWorkingText);
									System.out.println("Table Start Set.");
									tableEnd = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText) + thisWorkingText.length();
									System.out.println("Table End Set.");
									System.out.println("Start = " + tableStart + "  End = " + tableEnd);
								}
								else {
									thisWorkingText = thisLineText.trim();
									tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
									System.out.println("Table Start Set.");
									System.out.println("Start = " + tableStart + "  End = " + tableEnd);
									}
							}
							else {
								thisWorkingText = thisLineText.trim();
								tableStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
								System.out.println("Table Start Set.");
								System.out.println("Start = " + tableStart + "  End = " + tableEnd);
							}
						}
					}
				}
			}
			
			else {
				System.out.println("Found something else.");
				if (inTable == true){
					System.out.println("In A Table");
					if (i+1 < lines.size()){
						System.out.println("Going after nextline.");
						nextLine = lines.get(i+1);
						nextLineText = nextLine.getText();
						if (! getLabel(nextLine).equalsIgnoreCase("TBI")){
							System.out.println("Nextline not a TBI");
							if (tableEnd > -1){
								System.out.println("Table End Set.");
								System.out.println("Start = " + tableStart + "  End = " + tableEnd);
								annotations.add(new Annotation("Table", tableStart, tableEnd));
								System.out.println("Table Annotated");
								inTable = false;
								System.out.println("Reset InTable Status");
								tableStart = -1;
								tableEnd = -1;
								System.out.println("Start = " + tableStart + "  End = " + tableEnd);
							}
							else{
								System.out.println("Table End Not Set.");
								System.out.println("Start = " + tableStart + "  End = " + tableEnd);
								inTable = false;
								System.out.println("Reset InTable Status");
								tableStart = -1;
								tableEnd = -1;
								System.out.println("Start = " + tableStart + "  End = " + tableEnd);
							}
						}
						else{System.out.println("Nextline is a TBI ...");}
					}
				}
			}
	
		}
		
		return annotations;
		
	}

}
