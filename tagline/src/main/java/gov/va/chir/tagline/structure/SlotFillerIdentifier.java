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
import java.util.Set;
import java.util.List;

public class SlotFillerIdentifier extends Identifier {
	
	
	private String getLargestGap(String line, int count){
		String gap = "               ";
		
		for (int i = 0; i < 15; i++) {
			
			String text = line;
			text = text.trim();
			gap = gap.substring(1);
			int occurences = 0;
			int index = text.indexOf(gap);
			while (index > -1) {
				
				index = text.indexOf(gap);
				
				if (index > -1){
					occurences++;
					text = text.substring(index);
					text = text.trim();
				}
			}
			if (occurences >= count){
				break;
			}
		}
		return gap;
	}

	
	
	@Override
	protected Set<Annotation> identifyStructures(Document document) {
		final Set<Annotation> annotations = new HashSet<Annotation>();
		//AnnotationType.SLOT;
		//AnnotationType.FILLER;
		
		final List <Line> lines = document.getLines();
        
		for (int i = 0; i < lines.size(); i++) {
			int slotStart = -1;
			int slotEnd = -1;
			int fillStart = -1;
			int fillEnd = -1;
			//int gapPosition = -1;
			int refPosition = -1;
			//int hyphenPosition = -1;
			//int refPosition = -1;
			int headerStart = -1;
			int headerEnd = -1;
			//String thisLineText = "";
			String thisWorkingText = "";
			//String nextWorkingText = "";
			String nextLineText = "";
			//String lastLineText = "";
			Line nextLine;
			Line lastLine;
			String header = "";
			String gap = "";
			String slot = "";
			String filler = "";
			String slot2 = "";
			String filler2 = "";
			String slot3 = "";
			String filler3 = "";
			String slotFiller = "";
			String slotFiller2 = "";
			String slotFiller3 = "";
			String thisClass = "";
			String nextClass = "";
			String lastClass = "";
			
			
			
			
			final Line thisLine = lines.get(i);
			final String thisLineText = thisLine.getText();
			thisClass = getLabel(thisLine);
			
			/**if (i+1 < lines.size()){
				nextLine = lines.get(i+1);
				nextLineText = nextLine.getText();
				nextClass = getLabel(nextLine);
			//}
			
			if (i-1 >= 0){
				lastLine = lines.get(i-1);
				lastLineText = lastLine.getText();
				lastClass = getLabel(lastLine);
			}
			else{lastClass = "XXX";}
			*/
			System.out.println(thisClass + " " + thisLineText);
			
			
			if (thisClass.equalsIgnoreCase("SLV")) {
				refPosition = thisLineText.indexOf(":");
				if (refPosition > -1){
					slot = thisLineText.substring(0, refPosition +1);
					slot = slot.trim();
					slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
					slotEnd = slotStart + slot.length();
					annotations.add(new Annotation("Slot", slotStart, slotEnd));
					filler = thisLineText.substring(refPosition);
					filler = filler.trim();
					if (filler.length()> 0){
						fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
						fillEnd = fillStart + filler.length();
						annotations.add(new Annotation("Filler", fillStart, fillEnd));
						annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));}}
			}
			
			else if (thisClass.equalsIgnoreCase("SLT")) {
				refPosition = thisLineText.indexOf(":");
				if (refPosition > -1){
					slot = thisLineText.substring(0, refPosition +1);
					slot = slot.trim();
					slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
					slotEnd = slotStart + slot.length();
					annotations.add(new Annotation("Slot", slotStart, slotEnd));
					if (nextClass.equalsIgnoreCase("FRT")) {
						if (i+1 < lines.size()){
							nextLine = lines.get(i+1);
							nextLineText = nextLine.getText();
							filler = nextLineText.trim();
							fillStart = nextLine.getOffset() + nextLineText.indexOf(filler);
							fillEnd = fillStart + filler.length();
							annotations.add(new Annotation("Filler", fillStart, fillEnd));
							annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
						}
					}
				}
						
			}

			else if (thisClass.equalsIgnoreCase("NSL")) {
				refPosition = thisLineText.indexOf(".");
				if (refPosition < 0){
					refPosition  = thisLineText.indexOf(")");
				}
				if (refPosition > -1){
					thisWorkingText = thisLineText.substring(refPosition);
					refPosition = thisWorkingText.indexOf(":");
					slot = thisWorkingText.substring(0, refPosition +1);
					slot = slot.trim();
					slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
					slotEnd = slotStart + slot.length();
					annotations.add(new Annotation("Slot", slotStart, slotEnd));
				}
			}
			

			else if (thisClass.equalsIgnoreCase("SHV")) {
				refPosition = thisLineText.indexOf("-");
				if (refPosition > -1){
					slot = thisLineText.substring(0, refPosition +1);
					slot = slot.trim();
					slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
					slotEnd = slotStart + slot.length();
					annotations.add(new Annotation("Slot", slotStart, slotEnd));
					filler = thisLineText.substring(refPosition);
					filler = filler.trim();
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
			}
			
			else if (thisClass.equalsIgnoreCase("HSV")) {
				refPosition = thisLineText.indexOf(":");
				if (refPosition > -1){
					header = thisLineText.substring(0, refPosition +1);
					header = header.trim();
					headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
					headerEnd = headerStart + header.length();
					annotations.add(new Annotation("Header", headerStart, headerEnd));
					thisWorkingText = thisLineText.substring(refPosition);
					thisWorkingText = thisWorkingText.trim();
					refPosition = thisWorkingText.indexOf(":", headerEnd +1);
					if (refPosition > -1){
						slot = thisWorkingText.substring(0, refPosition +1);
						slot = slot.trim();
						slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
						slotEnd = slotStart + slot.length();
						annotations.add(new Annotation("Slot", slotStart, slotEnd));
						filler = thisWorkingText.substring(refPosition);
						filler = filler.trim();
						if (filler.length() > 0){
							fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
							fillEnd = fillStart + filler.length();
							annotations.add(new Annotation("Filler", fillStart, fillEnd));
							annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
						}
					}
				}
			}
			
			else if (thisClass.equalsIgnoreCase("HDS")) {
				refPosition = thisLineText.indexOf(":");
				if (refPosition > -1){
					header = thisLineText.substring(0, refPosition +1);
					header = header.trim();
					headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
					headerEnd = headerStart + header.length();
					annotations.add(new Annotation("Header", headerStart, headerEnd));
					thisWorkingText = thisLineText.substring(refPosition);
					gap = getLargestGap(thisWorkingText, 1);
					refPosition = thisWorkingText.indexOf(gap, headerEnd +1);
					if (refPosition > -1){
						slotFiller = thisWorkingText.substring(0, refPosition);
						slotFiller2 = thisWorkingText.substring(refPosition);
						refPosition = slotFiller.indexOf(":");
						if (refPosition > -1){
							slot = slotFiller.substring(0, refPosition +1);
							slot = slot.trim();
							slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
							slotEnd = slotStart + slot.length();
							annotations.add(new Annotation("Slot", slotStart, slotEnd));
							filler = slotFiller.substring(refPosition);
							filler = filler.trim();
							if (filler.length() > 0){
								fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
								fillEnd = fillStart + filler.length();
								annotations.add(new Annotation("Filler", fillStart, fillEnd));
								annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
							}
							refPosition = slotFiller2.indexOf(":");
							if (refPosition > -1){
								slot = slotFiller2.substring(0, refPosition +1);
								slot = slot.trim();
								slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
								slotEnd = slotStart + slot.length();
								annotations.add(new Annotation("Slot", slotStart, slotEnd));
								filler = slotFiller2.substring(refPosition);
								filler = filler.trim();
								if (filler.length() > 0){
									fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
									fillEnd = fillStart + filler.length();
									annotations.add(new Annotation("Filler", fillStart, fillEnd));
									annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
								}
							}
						}
					}
				}
			}
			
			else if (thisClass.equalsIgnoreCase("HDV")) {
				refPosition = thisLineText.indexOf(":");
				if (refPosition > -1){
				header = thisLineText.substring(0, refPosition +1);
				header = header.trim();
				headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
				headerEnd = headerStart + header.length();
				annotations.add(new Annotation("Header", headerStart, headerEnd));
				thisWorkingText = thisLineText.substring(refPosition);
				thisWorkingText = thisLineText.trim();
				gap = getLargestGap(thisWorkingText, 1);
				refPosition = thisWorkingText.indexOf(gap);
				if (refPosition > -1){
					slotFiller = thisWorkingText.substring(0, refPosition);
					slotFiller2 = thisWorkingText.substring(refPosition);
					refPosition = slotFiller.indexOf(":");
					if (refPosition > -1){
						slot = slotFiller.substring(0, refPosition +1);
						slot = slot.trim();
						slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
						slotEnd = slotStart + slot.length();
						annotations.add(new Annotation("Slot", slotStart, slotEnd));
						filler = slotFiller.substring(refPosition);
						filler = filler.trim();}
						if (filler.length() > 0){
							fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
							fillEnd = fillStart + filler.length();
							annotations.add(new Annotation("Filler", fillStart, fillEnd));
							annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
						}
						refPosition = slotFiller2.indexOf(":");
						if (refPosition > -1){
							slot = slotFiller2.substring(0, refPosition +1);
							slot = slot.trim();
							slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
							slotEnd = slotStart + slot.length();
							annotations.add(new Annotation("Slot", slotStart, slotEnd));
							filler = slotFiller2.substring(refPosition);
							filler = filler.trim();
							if (filler.length() > 0){
								fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
								fillEnd = fillStart + filler.length();
								annotations.add(new Annotation("Filler", fillStart, fillEnd));
								annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
							}
						}
					}
				}
			}
			
			else if (thisClass.equalsIgnoreCase("LDS")) {
				thisWorkingText = thisLineText.trim();
				refPosition = thisWorkingText.indexOf("  ");
				if (refPosition > -1){
					header = thisWorkingText.substring(0, refPosition +1);
					header = header.trim();
					headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
					headerEnd = headerStart + header.length();
					annotations.add(new Annotation("Header", headerStart, headerEnd));
					thisWorkingText = thisWorkingText.substring(refPosition);
					thisWorkingText = thisWorkingText.trim();
					gap = getLargestGap(thisWorkingText, 1);
					refPosition = thisWorkingText.indexOf(gap);
					if (refPosition > -1){
						slotFiller = thisWorkingText.substring(0, refPosition);
						slotFiller2 = thisWorkingText.substring(refPosition);
						refPosition = slotFiller.indexOf(":");
						slot = slotFiller.substring(0, refPosition +1);
						slot = slot.trim();
						slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
						slotEnd = slotStart + slot.length();
						annotations.add(new Annotation("Slot", slotStart, slotEnd));
						filler = slotFiller.substring(refPosition);
						filler = filler.trim();
						if (filler.length() > 0){
							fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
							fillEnd = fillStart + filler.length();
							annotations.add(new Annotation("Filler", fillStart, fillEnd));
							annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
						}
						refPosition = slotFiller2.indexOf(":");
						if (refPosition > -1){
							slot = slotFiller2.substring(0, refPosition +1);
							slot = slot.trim();
							slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
							slotEnd = slotStart + slot.length();
							annotations.add(new Annotation("Slot", slotStart, slotEnd));
							filler = slotFiller2.substring(refPosition);
							filler = filler.trim();
							if (filler.length() > 0){
								fillStart = thisLine.getOffset() + thisLineText.lastIndexOf(filler);
								fillEnd = fillStart + filler.length();
								annotations.add(new Annotation("Filler", fillStart, fillEnd));
								annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
							}
						}
					}
				}
			}
				
			else if (thisClass.equalsIgnoreCase("SVV")) {
				refPosition = thisLineText.indexOf(":");
				if (refPosition > -1){
					header = thisLineText.substring(0, refPosition +1);
					header = header.trim();
					headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
					headerEnd = headerStart + header.length();
					annotations.add(new Annotation("Header", headerStart, headerEnd));
					thisWorkingText = thisLineText.substring(refPosition);
					thisWorkingText = thisWorkingText.trim();
					refPosition = thisWorkingText.indexOf("   ");
					if (refPosition > -1){
						slot = thisWorkingText.substring(0, refPosition);
						slot = slot.trim();
						slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
						slotEnd = slotStart + slot.length();
						annotations.add(new Annotation("Slot", slotStart, slotEnd));
						filler = thisWorkingText.substring(refPosition);
						filler = filler.trim();
						if (filler.length() > 0){
							fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
							fillEnd = fillStart + filler.length();
							annotations.add(new Annotation("Filler", fillStart, fillEnd));
							annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
						}
					}
				}
			}
			
			
			else if (thisClass.equalsIgnoreCase("NSS")) {
				refPosition = thisLineText.indexOf(".");
				if (refPosition == -1){
					refPosition  = thisLineText.indexOf(")");}
				if (refPosition > -1){
					thisWorkingText = thisLineText.substring(refPosition);
					refPosition = thisWorkingText.indexOf(":");
					if (refPosition > -1){
						slot = thisWorkingText.substring(0, refPosition +1);
						slot = slot.trim();
						slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
						slotEnd = slotStart + slot.length();
						annotations.add(new Annotation("Slot", slotStart, slotEnd));
						refPosition = thisWorkingText.indexOf(".");
						if (refPosition > 0){
							filler = thisWorkingText.substring(refPosition);
							filler = filler.trim();
							fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
							fillEnd = fillStart + filler.length();
							annotations.add(new Annotation("Filler", fillStart, fillEnd));
							annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
						}
						else{
							if (i+1 < lines.size()){
								nextLine = lines.get(i+1);
								nextLineText = nextLine.getText();
								nextClass = getLabel(nextLine);
								refPosition = nextLineText.indexOf(".");
								filler = thisWorkingText.substring(0, refPosition);
								filler = filler.trim();
								fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
								fillEnd = fillStart + filler.length();
								annotations.add(new Annotation("Filler", fillStart, fillEnd));
								annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
							}
						}	
					}
				}
			}
				
			else if (thisClass.equalsIgnoreCase("NSV")) {
				refPosition = thisLineText.indexOf(".");
				if (refPosition == -1){
					refPosition  = thisLineText.indexOf(")");}
				if (refPosition == -1){
					refPosition  = thisLineText.indexOf("]");}
				if (refPosition > -1){
					thisWorkingText = thisLineText.substring(refPosition);
					refPosition = thisWorkingText.indexOf(":");
					if (refPosition > -1){
						slot = thisWorkingText.substring(0, refPosition +1);
						slot = slot.trim();
						slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
						slotEnd = slotStart + slot.length();
						annotations.add(new Annotation("Slot", slotStart, slotEnd));
						filler = thisWorkingText.substring(refPosition);
						filler = filler.trim();	
						fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
						fillEnd = fillStart + filler.length();
						annotations.add(new Annotation("Filler", fillStart, fillEnd));
						annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
					}
				}
			}
	
			else if (thisClass.equalsIgnoreCase("DSL")) {
				refPosition = thisLineText.indexOf(":");
				if (refPosition > -1){
					slot = thisLineText.substring(0, refPosition +1);
					slot = slot.trim();
					slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
					slotEnd = thisLine.getOffset() + slotStart + slot.length();
					annotations.add(new Annotation("Slot", slotStart, slotEnd));
					filler = thisLineText.substring(refPosition);
					filler = filler.trim();
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = fillStart + filler.length();
					annotations.add(new Annotation("Date", fillStart, fillEnd));
					annotations.add(new Annotation("SlotDate", slotStart, fillEnd));
				}
			}
			
			else if (thisClass.equalsIgnoreCase("DTH")) {
				refPosition = thisLineText.indexOf(":");
				if (refPosition > -1){
					slot = thisLineText.substring(0, refPosition +1);
					slot = slot.trim();
					slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
					slotEnd = slotStart + slot.length();
					annotations.add(new Annotation("Slot", slotStart, slotEnd));
					filler = thisLineText.substring(refPosition);
					filler = filler.trim();
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = fillStart + filler.length();
					annotations.add(new Annotation("Date", fillStart, fillEnd));
					annotations.add(new Annotation("SlotDate", slotStart, fillEnd));
				}
			}
			
			else if (thisClass.equalsIgnoreCase("DTV")) {
				gap = getLargestGap(thisLineText, 1);
				refPosition = thisLineText.indexOf(gap);
				if (refPosition > -1){
					slot = thisLineText.substring(0, refPosition +1);
					slot = slot.trim();
					slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
					slotEnd = slotStart + slot.length();
					annotations.add(new Annotation("Date", slotStart, slotEnd));
					filler = thisLineText.substring(refPosition);
					filler = filler.trim();
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = fillStart + filler.length();
					annotations.add(new Annotation("Event", fillStart, fillEnd));
					annotations.add(new Annotation("SlotDate", slotStart, fillEnd));
				}
			}
				
			
			else if (thisClass.equalsIgnoreCase("DKV")) {
				gap = getLargestGap(thisLineText, 1);
				refPosition = thisLineText.indexOf(gap);
				if (refPosition > -1){
					header = thisLineText.substring(0, refPosition +1);
					header = header.trim();
					headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
					headerEnd = headerStart + header.length();
					annotations.add(new Annotation("Date", headerStart, headerEnd));
					thisWorkingText = thisLineText.substring(refPosition);
					refPosition = thisWorkingText.indexOf(":");
					if (refPosition > -1){
						slot = thisWorkingText.substring(0, refPosition +1);
						slot = slot.trim();
						slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
						slotEnd = slotStart + slot.length();
						annotations.add(new Annotation("Slot", slotStart, slotEnd));
						filler = thisLineText.substring(refPosition);
						filler = filler.trim();
						if (filler.length() > 0){
							fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
							fillEnd = fillStart + filler.length();
							annotations.add(new Annotation("Filler", fillStart, fillEnd));
							annotations.add(new Annotation("DateSlotFiller", headerStart, fillEnd));
						}
					}
				}
			}
			
			else if (thisClass.equalsIgnoreCase("DSV")) {
				thisWorkingText = thisLineText.trim();
				gap = getLargestGap(thisWorkingText, 1);
				refPosition = thisWorkingText.indexOf(gap);
				if (refPosition > -1){
					slotFiller = thisWorkingText.substring(0, refPosition);
					slotFiller2 = thisWorkingText.substring(refPosition);
					refPosition = slotFiller.indexOf(":");
					if (refPosition <0){
						refPosition = slotFiller.indexOf(". ");
					}
					if (refPosition > -1){
						slot = slotFiller.substring(0, refPosition +1);
						slot = slot.trim();
						slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
						slotEnd = slotStart + slot.length();
						annotations.add(new Annotation("Slot", slotStart, slotEnd));
						filler = slotFiller.substring(refPosition);
						filler = filler.trim();
						if (filler.length() > 0){
							fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
							fillEnd = fillStart + filler.length();
							annotations.add(new Annotation("Filler", fillStart, fillEnd));
							annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
						}
						refPosition = slotFiller2.indexOf(":");
						if (refPosition > -1){
							slot = slotFiller2.substring(0, refPosition +1);
							slot = slot.trim();
							slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
							slotEnd = slotStart + slot.length();
							annotations.add(new Annotation("Slot", slotStart, slotEnd));
							filler = slotFiller2.substring(refPosition);
							filler = filler.trim();
							if (filler.length() > 0){
								fillStart = thisLine.getOffset() + thisLineText.lastIndexOf(filler);
								fillEnd = fillStart + filler.length();
								annotations.add(new Annotation("Filler", fillStart, fillEnd));
								annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
							}
						}
					}
				}
			}
				
			else if (thisClass.equalsIgnoreCase("TSV")) {
				thisWorkingText = thisLineText.trim();
				gap = getLargestGap(thisWorkingText, 2);
				refPosition = thisWorkingText.indexOf(gap);
				if (refPosition > -1){
					slotFiller = thisWorkingText.substring(0, refPosition);
					thisWorkingText = thisWorkingText.substring(refPosition);
					thisWorkingText = thisWorkingText.trim();
					refPosition = slotFiller.indexOf(":");
					if (refPosition > -1){
						slot = slotFiller.substring(0, refPosition +1);
						slot = slot.trim();
						slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
						slotEnd = slotStart + slot.length();
						annotations.add(new Annotation("Slot", slotStart, slotEnd));
						filler = slotFiller.substring(refPosition);
						filler = filler.trim();
						if (filler.length() > 0){
							fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
							fillEnd = fillStart + filler.length();
							annotations.add(new Annotation("Filler", fillStart, fillEnd));
							annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
						}
							refPosition = thisWorkingText.indexOf(gap);
							if (refPosition > -1){
								slotFiller2 = thisWorkingText.substring(0, refPosition);
								slotFiller3 = thisWorkingText.substring(refPosition);
								refPosition = slotFiller2.indexOf(":");
								if (refPosition > -1){
									slot = slotFiller2.substring(0, refPosition +1);
									slot = slot.trim();
									slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
									slotEnd = slotStart + slot.length();
									annotations.add(new Annotation("Slot", slotStart, slotEnd));
									filler = slotFiller2.substring(refPosition);
									filler = filler.trim();
									if (filler.length() > 0){
										fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
										fillEnd = fillStart + filler.length();
										annotations.add(new Annotation("Filler", fillStart, fillEnd));
										annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
									}
								refPosition = slotFiller3.indexOf(":");
								if (refPosition > -1){
									slot = slotFiller3.substring(0, refPosition +1);
									slot = slot.trim();
									slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
									slotEnd = slotStart + slot.length();
									annotations.add(new Annotation("Slot", slotStart, slotEnd));
									filler = slotFiller3.substring(refPosition);
									filler = filler.trim();
									if (filler.length() > 0){
										fillStart = thisLine.getOffset() + thisLineText.lastIndexOf(filler);
										fillEnd = fillStart + filler.length();
										annotations.add(new Annotation("Filler", fillStart, fillEnd));
										annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
									}
								}
							}
						}
					}
				}
					
			}
		                                                
		}
		

		return annotations;
	}

}
