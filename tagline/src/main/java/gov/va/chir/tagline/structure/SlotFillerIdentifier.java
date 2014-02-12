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
			gap = gap.substring(1);
			int occurences = 0;
			int index = text.indexOf(gap);
			while (index > 0) {
				
				index = text.indexOf(gap);
				occurences++;
				text = text.substring(index);
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
			int gapPosition = -1;
			int colonPosition = -1;
			int hyphenPosition = -1;
			int refPosition = -1;
			int headerStart = -1;
			int headerEnd = -1;
			String thisLineText = "";
			String thisWorkingText = "";
			String nextWorkingText = "";
			String nextLineText = "";
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
			
			
			
			final Line thisLine = lines.get(i);
			thisLineText = thisLine.getText();
			
			if (getLabel(thisLine).equalsIgnoreCase("SLV")) {
				colonPosition = thisLineText.indexOf(":");
				slot = thisLineText.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = thisLineText.substring(colonPosition, 0);
				filler = filler.trim();
				fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
				fillEnd = thisLine.getOffset() + fillStart + filler.length();
				annotations.add(new Annotation("Filler", fillStart, fillEnd));
				annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
			}
			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("SLT")) {
				colonPosition = thisLineText.indexOf(":");
				slot = thisLineText.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				
				if (i <= lines.size()){
					final Line nextLine = lines.get(i + 1);
					if (nextLine.getPredictedLabel().equalsIgnoreCase("FRT")) {
						nextLineText = nextLine.getText();
						fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
						fillEnd = thisLine.getOffset() + fillStart + filler.length();
						annotations.add(new Annotation("Slot", slotStart, slotEnd));
						annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
					}
						
				}
			}
			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("NSL")) {
				refPosition = thisLineText.indexOf(".");
				if (refPosition == -1){
					refPosition  = thisLineText.indexOf(")");
				}
				thisLineText = thisLineText.substring(refPosition);
				colonPosition = thisLineText.indexOf(":");
				slot = thisLineText.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
			}
			

			else if (thisLine.getPredictedLabel().equalsIgnoreCase("SHV")) {
				hyphenPosition = thisLineText.indexOf("-");
				slot = thisLineText.substring(0, hyphenPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = thisLineText.substring(hyphenPosition, 0);
				filler = filler.trim();
				fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
				fillEnd = thisLine.getOffset() + fillStart + filler.length();
				annotations.add(new Annotation("Filler", fillStart, fillEnd));
				annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				
			}
			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("HSV")) {
				colonPosition = thisLineText.indexOf(":");
				header = thisLineText.substring(0, colonPosition);
				header = header.trim();
				headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
				headerEnd = thisLine.getOffset() + headerStart + header.length();
				annotations.add(new Annotation("Filler", fillStart, fillEnd));
				thisLineText = thisLineText.substring(colonPosition);
				colonPosition = thisLineText.indexOf(":");
				slot = thisLineText.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = thisLineText.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
			}
			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("HDS")) {
				colonPosition = thisLineText.indexOf(":");
				header = thisLineText.substring(0, colonPosition);
				header = header.trim();
				headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
				headerEnd = thisLine.getOffset() + header.length();
				annotations.add(new Annotation("Filler", fillStart, fillEnd));
				thisWorkingText = thisLineText.substring(colonPosition);
				gap = getLargestGap(thisWorkingText, 1);
				gapPosition = thisWorkingText.indexOf(gap);
				slotFiller = thisWorkingText.substring(0, gapPosition);
				slotFiller2 = thisWorkingText.substring(gapPosition, 0);
				colonPosition = slotFiller.indexOf(":");
				slot = slotFiller.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = slotFiller.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
				colonPosition = slotFiller2.indexOf(":");
				slot = slotFiller2.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = slotFiller2.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
			}
			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("HDV")) {
				colonPosition = thisLineText.indexOf(":");
				header = thisLineText.substring(0, colonPosition);
				header = header.trim();
				headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
				headerEnd = thisLine.getOffset() + header.length();
				annotations.add(new Annotation("Filler", fillStart, fillEnd));
				thisWorkingText = thisLineText.substring(colonPosition);
				gapPosition = thisWorkingText.indexOf("  ");
				slotFiller = thisWorkingText.substring(0, gapPosition);
				slotFiller2 = thisWorkingText.substring(gapPosition, 0);
				colonPosition = slotFiller.indexOf(":");
				slot = slotFiller.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = slotFiller.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
				
				colonPosition = slotFiller2.indexOf(":");
				slot = slotFiller2.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = slotFiller2.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
			}
			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("LDS")) {
				gapPosition = thisLineText.indexOf("  ");
				header = thisLineText.substring(0, gapPosition);
				header = header.trim();
				headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
				headerEnd = thisLine.getOffset() + header.length();
				annotations.add(new Annotation("Filler", fillStart, fillEnd));
				thisWorkingText = thisLineText.substring(gapPosition);
				gapPosition = thisWorkingText.indexOf("  ");
				slotFiller = thisWorkingText.substring(0, gapPosition);
				slotFiller2 = thisWorkingText.substring(gapPosition, 0);
				colonPosition = slotFiller.indexOf(":");
				slot = slotFiller.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = slotFiller.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() +fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
				
				colonPosition = slotFiller2.indexOf(":");
				slot = slotFiller2.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = slotFiller2.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
			}
				
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("SVV")) {
				colonPosition = thisLineText.indexOf(":");
				header = thisLineText.substring(0, colonPosition);
				header = header.trim();
				headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
				headerEnd = thisLine.getOffset() + header.length();
				annotations.add(new Annotation("Filler", fillStart, fillEnd));
				thisLineText = thisLineText.substring(colonPosition);
				thisLineText = thisLineText.trim();
				gapPosition = thisLineText.indexOf("   ");
				slot = thisLineText.substring(0, gapPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = thisLineText.substring(gapPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
			}
			
			
			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("NSS")) {
				refPosition = thisLineText.indexOf(".");
				if (refPosition == -1){
					refPosition  = thisLineText.indexOf(")");
				}
				thisLineText = thisLineText.substring(refPosition);
				colonPosition = thisLineText.indexOf(":");
				slot = thisLineText.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				refPosition = thisLineText.indexOf(".");
				if (refPosition > 0){
					filler = thisLineText.substring(colonPosition, 0);
					filler = filler.trim();
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
				else{
					final Line nextLine = lines.get(i +1);
					nextLineText = nextLine.getText();
					refPosition = thisLineText.indexOf(".");
					filler = thisLineText.substring(colonPosition, 0);
					filler = filler.trim();
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
			}
				
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("NSV")) {
				refPosition = thisLineText.indexOf(".");
				if (refPosition == -1){
					refPosition  = thisLineText.indexOf(")");
				}
				if (refPosition == -1){
					refPosition  = thisLineText.indexOf("]");
				}
				thisLineText = thisLineText.substring(refPosition);
				colonPosition = thisLineText.indexOf(":");
				slot = thisLineText.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				refPosition = thisLineText.indexOf(".");
				filler = thisLineText.substring(colonPosition, 0);
				filler = filler.trim();
				fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
				fillEnd = thisLine.getOffset() + fillStart + filler.length();
				annotations.add(new Annotation("Filler", fillStart, fillEnd));
				annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
			}
	

			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("DSL")) {
				colonPosition = thisLineText.indexOf(":");
				slot = thisLineText.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = thisLineText.substring(colonPosition, 0);
				filler = filler.trim();
				fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
				fillEnd = thisLine.getOffset() + fillStart + filler.length();
				annotations.add(new Annotation("Date", fillStart, fillEnd));
				annotations.add(new Annotation("SlotDate", slotStart, fillEnd));
			}
			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("DTH")) {
				colonPosition = thisLineText.indexOf(":");
				slot = thisLineText.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = thisLineText.substring(colonPosition, 0);
				filler = filler.trim();
				fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
				fillEnd = thisLine.getOffset() + fillStart + filler.length();
				annotations.add(new Annotation("Date", fillStart, fillEnd));
				annotations.add(new Annotation("SlotDate", slotStart, fillEnd));
			}
			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("DTV")) {
				gap = getLargestGap(thisLineText, 1);
				gapPosition = thisLineText.indexOf(gap);
				slot = thisLineText.substring(0, gapPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Date", slotStart, slotEnd));
				filler = thisLineText.substring(gapPosition, 0);
				filler = filler.trim();
				fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
				fillEnd = thisLine.getOffset() + fillStart + filler.length();
				annotations.add(new Annotation("Event", fillStart, fillEnd));
				annotations.add(new Annotation("SlotDate", slotStart, fillEnd));	
			}
				
			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("DKV")) {
				gap = getLargestGap(thisLineText, 1);
				gapPosition = thisLineText.indexOf(gap);
				header = thisLineText.substring(0, gapPosition);
				header = header.trim();
				headerStart = thisLine.getOffset() + thisLineText.indexOf(header);
				headerEnd = thisLine.getOffset() + header.length();
				annotations.add(new Annotation("Date", fillStart, fillEnd));
				thisLineText = thisLineText.substring(colonPosition);
				colonPosition = thisLineText.indexOf(":");
				slot = thisLineText.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = thisLineText.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("DateSlotFiller", headerStart, fillEnd));
				}
			}
			
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("DSV")) {
				thisWorkingText = thisLineText.trim();
				gap = getLargestGap(thisWorkingText, 1);
				gapPosition = thisLineText.indexOf(gap);
				slotFiller = thisLineText.substring(0, gapPosition);
				slotFiller2 = thisLineText.substring(gapPosition, 0);
				colonPosition = slotFiller.indexOf(":");
				slot = slotFiller.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = slotFiller.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
				colonPosition = slotFiller2.indexOf(":");
				slot = slotFiller2.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = slotFiller2.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));				
				}	
			}
				
			else if (thisLine.getPredictedLabel().equalsIgnoreCase("TSV")) {
				thisWorkingText = thisLineText.trim();
				gap = getLargestGap(thisWorkingText, 1);
				gapPosition = thisLineText.indexOf(gap);
				slotFiller = thisLineText.substring(0, gapPosition);
				thisWorkingText = thisLineText.substring(gapPosition, 0);
				colonPosition = slotFiller.indexOf(":");
				slot = slotFiller.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = slotFiller.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
				gapPosition = thisWorkingText.indexOf(gap);
				slotFiller2 = thisWorkingText.substring(0, gapPosition);
				slotFiller3 = thisWorkingText.substring(gapPosition, 0);
				colonPosition = slotFiller2.indexOf(":");
				slot = slotFiller2.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = slotFiller2.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
				colonPosition = slotFiller3.indexOf(":");
				slot = slotFiller3.substring(0, colonPosition);
				slot = slot.trim();
				slotStart = thisLine.getOffset() + thisLineText.indexOf(slot, gapPosition);
				slotEnd = thisLine.getOffset() + slotStart + slot.length();
				annotations.add(new Annotation("Slot", slotStart, slotEnd));
				filler = slotFiller3.substring(colonPosition, 0);
				filler = filler.trim();
				if (filler.length() > 0){
					fillStart = thisLine.getOffset() + thisLineText.indexOf(filler, gapPosition);
					fillEnd = thisLine.getOffset() + fillStart + filler.length();
					annotations.add(new Annotation("Filler", fillStart, fillEnd));
					annotations.add(new Annotation("SlotFiller", slotStart, fillEnd));
				}
					
			}
		                                                
		}
		

		return annotations;
	}

}
