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

public class ListIdentifier extends Identifier{
	
	
	
	@Override
	protected Set<Annotation> identifyStructures(Document document) {
		final Set<Annotation> annotations = new HashSet<Annotation>();
		//AnnotationType.LIST
		
		
		final List <Line> lines = document.getLines();

		for (int i = 0; i < lines.size(); i++) {
			
			
			int start = -1;
			int end = -1;
			int linesOfFRT = 0;
			final int MAXFRT = 2;
			String thisLineText;
			String thisWorkingText;
			String nextLineText;
			String nextWorkingText;
			String lastLineText;
			String prevLineText;
			String lastWorkingText;
			String prevWorkingText;
			String listType = "none";
			final Line lastLine;
			final Line nextLine;
			final Line prevLine;
			
			
			final Line thisLine = lines.get(i);
			thisLineText = thisLine.getText();
	        boolean inList = false;
	        int listStart = -1;
			int listEnd = -1;
		
			if (getLabel(thisLine).equalsIgnoreCase("LHE")) {
				linesOfFRT = 0;
				System.out.println("Found a LHE");
				System.out.println("Reset linesOfFRT");
				if (inList == true){
					System.out.println("In A List");
					if (listEnd > -1){
						System.out.println("List End Set");
						System.out.println("Start = " + listStart + "  End = " + listEnd);
						annotations.add(new Annotation("List", listStart, listEnd));
						System.out.println("List Annotated");
						thisWorkingText = thisLineText.trim();
						listStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
						System.out.println("List Start Set.");
						listEnd = -1;
						System.out.println("List End reset.");
						System.out.println("Start = " + listStart + "  End = " + listEnd);
					}
				}
				else {
					inList = true;
					System.out.println("List Started");
					thisWorkingText = thisLineText.trim();
					listStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
					System.out.println("List Start Set");
					System.out.println("Start = " + listStart + "  End = " + listEnd);
				}
			}
			else if (getLabel(thisLine).equalsIgnoreCase("LLI")){
				System.out.println("Found a LLI");
				inList = true;
				System.out.println("List Started");
				linesOfFRT = 0;
				System.out.println("Reset linesOfFRT");
				thisWorkingText = thisLineText.trim();
				listStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
				System.out.println("List Start Set");
				listEnd = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText) + thisWorkingText.length();
				System.out.println("Set List End");
				System.out.println("Start = " + listStart + "  End = " + listEnd);
				
			}
			else if (getLabel(thisLine).equalsIgnoreCase("LIT")){
				linesOfFRT = 0;
				System.out.println("Found a LIT");
				System.out.println("Reset linesOfFRT");
				if (inList == true){
					System.out.println("In A List");
					thisWorkingText = thisLineText.trim();
					listEnd = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText) + thisWorkingText.length();
					System.out.println("Set List End");
					System.out.println("Start = " + listStart + "  End = " + listEnd);
				}
				else {
					if (i +1 < lines.size()){
						nextLine = lines.get(i+1);
						nextLineText = nextLine.getText();
						System.out.println("Going after nextline.");
						if (getLabel(nextLine).equalsIgnoreCase("LIT")){
							System.out.println("Nextline is a LIT");
							inList = true;
							System.out.println("List Started");
							if (i > 0){
								lastLine = lines.get(i-1);
								lastLineText = lastLine.getText();
								System.out.println("Going after lastline.");
								if (getLabel(lastLine).equalsIgnoreCase("SHE")){
									System.out.println("Its a Section Header.");
									lastWorkingText = lastLineText.trim();
									thisWorkingText = thisLineText.trim();
									listStart = lastLine.getOffset() + lastLineText.indexOf(lastWorkingText);
									System.out.println("List Start Set.");
									listEnd = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText) + thisWorkingText.length();
									System.out.println("List End Set.");
									System.out.println("Start = " + listStart + "  End = " + listEnd);
								}
								else if (i > 1){
									prevLine = lines.get(i-2);
									prevLineText = prevLine.getText();
									System.out.println("Going after prevline.");
									if (getLabel(prevLine).equalsIgnoreCase("SHE")){
										System.out.println("Its a Section Header.");
										prevWorkingText = prevLineText.trim();
										thisWorkingText = thisLineText.trim();
										listStart = prevLine.getOffset() + prevLineText.indexOf(prevWorkingText);
										System.out.println("List Start Set.");
										listEnd = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText) + thisWorkingText.length();
										System.out.println("List End Set.");
										System.out.println("Start = " + listStart + "  End = " + listEnd);
									}
									else {
										thisWorkingText = thisLineText.trim();
										listStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
										System.out.println("list Start Set.");
										System.out.println("Start = " + listStart + "  End = " + listEnd);
									}
								}
							}
							else {
								thisWorkingText = thisLineText.trim();
								listStart = thisLine.getOffset() + thisLineText.indexOf(thisWorkingText);
								System.out.println("List Start Set.");
								System.out.println("Start = " + listStart + "  End = " + listEnd);
							}
						}
					}
				}
			}
			else {
				linesOfFRT ++;
				System.out.println("Found something else.");
				System.out.println("lineOfFRT = " + linesOfFRT);
				if (inList == true){
					System.out.println("In A List");
					if (linesOfFRT > MAXFRT){
						if (listEnd > -1){
							System.out.println("List End Set.");
							System.out.println("Start = " + listStart + "  End = " + listEnd);
							annotations.add(new Annotation("List", listStart, listEnd));
							System.out.println("List Annotated");
							inList = false;
							System.out.println("Reset InList Status");
							listStart = -1;
							listEnd = -1;
							System.out.println("Start = " + listStart + "  End = " + listEnd);
						}
						else{
							System.out.println("List End Not Set.");
							System.out.println("Start = " + listStart + "  End = " + listEnd);
							inList = false;
							System.out.println("Reset InList Status");
							listStart = -1;
							listEnd = -1;
							System.out.println("Start = " + listStart + "  End = " + listEnd);
						}
					}
				}
			}
		}
		
		return annotations;
		
	}

}
