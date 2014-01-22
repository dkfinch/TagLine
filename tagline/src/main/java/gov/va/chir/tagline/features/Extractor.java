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

import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.beans.Line;
import gov.va.chir.tagline.dao.FileDao;
import gov.va.chir.tagline.features.document.AvgLineLength;
import gov.va.chir.tagline.features.document.DocumentCountInstances;
import gov.va.chir.tagline.features.document.DocumentLength;
import gov.va.chir.tagline.features.document.NumLines;
import gov.va.chir.tagline.features.line.LineAllSameChar;
import gov.va.chir.tagline.features.line.LineCharType;
import gov.va.chir.tagline.features.line.LineContiguousCharType;
import gov.va.chir.tagline.features.line.LineIndexOccurrence;
import gov.va.chir.tagline.features.line.LineLength;
import gov.va.chir.tagline.features.line.LineMatch;
import gov.va.chir.tagline.features.line.LineMatchMultiple;
import gov.va.chir.tagline.features.line.LineRegExp;
import gov.va.chir.tagline.features.line.LineWordMatchMultiple;
import gov.va.chir.tagline.features.linedocument.LinePosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Extractor {
	private static final String[] REL_FILES = {
		//"/gov/va/chir/tagline/data/abbreviations.txt",
		//"/gov/va/chir/tagline/data/acronyms.txt",
		//"/gov/va/chir/tagline/data/cprs.txt",
		//"/gov/va/chir/tagline/data/headers.txt",
		//"/gov/va/chir/tagline/data/medical_terms.txt",
		//"/gov/va/chir/tagline/data/rx.txt",
		//"/gov/va/chir/tagline/data/stoplist.txt"
	};
	
	private static final String[] REL_FILE_NAMES = {
		//"rf_abbr", "rf_acron", "rf_cprs", "rf_headers", "rf_medterms", 
		//"rf_rx", "rf_stoplist"
	};
	
	private static final int RF_ABBR = 0;
	private static final int RF_ACRON = 1;
	private static final int RF_CPRS = 2;
	private static final int RF_HEADERS = 3;
	private static final int RF_MEDTERMS = 4;
	private static final int RF_RX = 5;
	private static final int RF_STOPLIST = 6;
	
	private static final byte[] CHAR_TYPES = {
		Character.UPPERCASE_LETTER,	Character.LOWERCASE_LETTER,	
		Character.TITLECASE_LETTER,	Character.MODIFIER_LETTER, 
		Character.OTHER_LETTER,	Character.COMBINING_SPACING_MARK, 
		Character.ENCLOSING_MARK, Character.NON_SPACING_MARK, 
		Character.DECIMAL_DIGIT_NUMBER,	Character.LETTER_NUMBER, 
		Character.OTHER_NUMBER,	Character.CONNECTOR_PUNCTUATION, 
		Character.DASH_PUNCTUATION,	Character.START_PUNCTUATION, 
		Character.END_PUNCTUATION, Character.INITIAL_QUOTE_PUNCTUATION, 
		Character.FINAL_QUOTE_PUNCTUATION, Character.OTHER_PUNCTUATION, 
		Character.CURRENCY_SYMBOL, Character.MODIFIER_SYMBOL, 
		Character.MATH_SYMBOL, Character.OTHER_SYMBOL, 
		Character.SPACE_SEPARATOR, Character.LINE_SEPARATOR, 
		Character.PARAGRAPH_SEPARATOR
	};
	
	private static final String[] CHAR_TYPE_NAMES = {
		"char_lu", "char_ll", "char_lt", "char_lm", 
		"char_lo", "char_mc", "char_me", "char_mn", 
		"char_nd", "char_ni", "char_no", "char_pc", 
		"char_pd", "char_ps", "char_pe", "char_pi", 
		"char_pf", "char_po", "char_sc", "char_sk", 
		"char_sm", "char_so", "char_zs", "char_zl", 
		"char_zp"
	};

	private static final int CHAR_LU = 0;
	private static final int CHAR_DIGIT = 8;
	private static final int CHAR_SPACE = 22;
	
	private static final String[] REG_EXPS = {
		"ICD[-\\s]?9", 
		"DSM[-\\s]?4",
		"(?:^[0-9]{1,2}[-).]|[ ]{2}[0-9]{1,2}[-).])",
		"[0-9]+[ ]?[mMgG]{2}",
		"(?:1[0-2]|[1-9])[:][1-5]?[0-9][:.][1-5]?[0-9][ ]+(?:AM|PM)?|(?:[0-2][0-9][:][0-5][0-9])",
		"(?:[\\(]?[1-9][0-9]{2}[\\)\\-. ]{1}[0-9]{3}[\\-. ]{1}[0-9]{4})|(?:[ :][0-9]{3}[\\-. ]{1}[0-9]{4})",
		"[\\[(]{1}[0-9X+ .]+?[\\])]{1}",
		"[0-9]+[.]{1}[0-9]+"
	};
	
	private static final String[] REG_EXP_NAMES = {
		"re_icd9", "re_dsm4", "re_line_num", "re_mg", "re_time", "re_phone",
		"re_boxes", "re_decimals"
	};
	
	private static final int RE_ICD9 = 0;
	private static final int RE_DSM4 = 1;
	private static final int RE_LINE_NUM = 2;
	private static final int RE_MG = 3;
	private static final int RE_TIME = 4;
	private static final int RE_PHONE = 5;
	private static final int RE_BOXES = 6;
	private static final int RE_DECIMALS = 7;
	
	

	private static final String[] SINGLE_STRINGS = {"`", "~", "!", "@", "#", "$", "%", "^", 
			"&", "*", "(", ")", "-", "_", "=", "+", "[", "{", "]", "}", "\\", 
			"|", ";", ":", "\"", "'", "<", ",", ">", ".", "/", "?", 
			"allerg", "sig:", "screen", "X"};
	private static final String[] SINGLE_STRING_NAMES = {"str_grave", "str_tilde", 
			"str_exclamation", "str_at", "str_hash", "str_dollar", "str_percent", 
			"str_caret", "str_ampersand", "str_asterisk", "str_lparan", 
			"str_rparen", "str_dash", "str_underscore", "str_equal", "str_plus", 
			"str_lbracket", "str_lbrace", "str_rbracket", "str_rbrace", 
			"str_backslash", "str_pipe", "str_semicolon", "str_colon", "str_quote", 
			"str_tick", "str_less", "str_comma", "str_more", "str_period", 
			"str_forwardslash", "str_questionmark", 
			"str_allerg", "str_sig", "str_screen", "str_xes"};
	
	private static final int STR_EXCLAMATION = 2;
	private static final int STR_AT = 3;
	private static final int STR_HASH = 4;
	private static final int STR_DOLLAR = 5;
	private static final int STR_PERCENT = 6;
	private static final int STR_CARET = 7;
	private static final int STR_AMPERSAND = 8;
	private static final int STR_ASTERISK = 9;
	private static final int STR_DASH = 12;
	private static final int STR_UNDERSCORE = 13;
	private static final int STR_EQUAL = 14;
	private static final int STR_PLUS = 15;
	private static final int STR_BACK_SLASH = 20;
	private static final int STR_PIPE = 21;
	private static final int STR_COLON = 23;
	private static final int STR_QUOTE = 24;
	private static final int STR_TICK = 25;
	private static final int STR_COMMA = 27;
	private static final int STR_PERIOD = 29;
	private static final int STR_FORWARD_SLASH = 30;
	private static final int STR_QUES = 31;
	private static final int STR_ALLERG = 32;
	private static final int STR_SIG = 33;
	private static final int STR_SCREEN = 34;
	private static final int STR_X = 35;
	
	
	private static final String[][] MULTI_STRINGS = {
			{"-", "*", ">>", "->", "+", ">", "//"},
			{"? yes", "?  yes", "? no", "?  no"},
			{"<", ">"},
			{"!", "?", ",", ".", ":", ";", "'"},
			{"/", "-", "*", "(", ")", "[", "]", "@", "#", "$", "%", "&", "+", "'", "=", "<", ">"},
			{"(", ")"},
			{"[", "]"},
			{"{", "}"}
			
	};
	private static final String[] MULTI_STRINGS_NAMES = {"mstr_bullets", "mstr_yes_no", 
			"mstr_comparisons", "mstr_punctuation", "mstr_spec_chars",
			"mstr_parens", "mstr_brackets", "mstr_curly_braces"};
	
	private static final int MSTR_BULLETS = 0;
	private static final int MSTR_YES_NO = 1;
	private static final int MSTR_LESS_GREATER = 2;
	private static final int MSTR_PUNCTUATION = 3;
	private static final int MSTR_SPECIAL_CHARS = 4;
	private static final int MSTR_PARENS = 5;
	private static final int MSTR_BRACKETS = 6;
	private static final int MSTR_BRACES = 7;
	
	public static Collection<Feature> getDefaultFeatures() throws IOException {
		final Collection<Feature> feats = new ArrayList<Feature>();
		
		// Holds all line features that will 
		// have document level features that count the number
		// of instances in a document.  These will eventually
		// be added to the normal list of features.
		final List<LineFeature> cntFeats = new ArrayList<LineFeature>();
		
		
		// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%		
		// Add all line features
		// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		
		
		// -----------------------------------------
		// Contiguous Character Types
		// -----------------------------------------

		// getOneSp
		cntFeats.add(new LineContiguousCharType(
				CHAR_TYPE_NAMES[CHAR_SPACE],
				TrimType.TRIM,
				ComparisonType.EQUAL,
				CHAR_TYPES[CHAR_SPACE],
				CountType.ONE));
		
		// getTwoSp
		cntFeats.add(new LineContiguousCharType(
				CHAR_TYPE_NAMES[CHAR_SPACE],
				TrimType.TRIM,
				ComparisonType.EQUAL,
				CHAR_TYPES[CHAR_SPACE],
				CountType.TWO));
		
		// getGaps
		// In Python version this was == 3, not >= 3 
		cntFeats.add(new LineContiguousCharType(
				CHAR_TYPE_NAMES[CHAR_SPACE],
				TrimType.TRIM,
				ComparisonType.GREATER_THAN_EQUAL_TO,
				CHAR_TYPES[CHAR_SPACE],
				CountType.THREE));
				
		
		// -----------------------------------------
		// Character Types
		// -----------------------------------------

		// getDigs
		cntFeats.add(new LineCharType(
				CHAR_TYPE_NAMES[CHAR_DIGIT], 
				MatchType.COUNT, 
				TrimType.NO_TRIM, 
				CHAR_TYPES[CHAR_DIGIT]));
		
		// getPNum
		feats.add(new LineCharType(
				CHAR_TYPE_NAMES[CHAR_DIGIT], 
				MatchType.PERCENT, 
				TrimType.TRIM,
				CHAR_TYPES[CHAR_DIGIT]));
		
		// getSpace
		// getLSpc appears identical
		cntFeats.add(new LineCharType(
				CHAR_TYPE_NAMES[CHAR_SPACE], 
				MatchType.COUNT, 
				TrimType.TRIM, 
				CHAR_TYPES[CHAR_SPACE]));
		
		// getPSpc
		feats.add(new LineCharType(
				CHAR_TYPE_NAMES[CHAR_SPACE], 
				MatchType.PERCENT, 
				TrimType.TRIM, 
				CHAR_TYPES[CHAR_SPACE]));		
		
		// getAllCap
		// @TODO Determine if all characters are uppercase, not just letters.
		// Probably NOT what we want!
		feats.add(new LineCharType(
				CHAR_TYPE_NAMES[CHAR_LU], 
				MatchType.ALL, 
				TrimType.NO_TRIM, 
				CHAR_TYPES[CHAR_LU]));

		// getCaps
		cntFeats.add(new LineCharType(
				CHAR_TYPE_NAMES[CHAR_LU], 
				MatchType.COUNT, 
				TrimType.NO_TRIM, 
				CHAR_TYPES[CHAR_LU]));
		
		// getPCaps
		feats.add(new LineCharType(
				CHAR_TYPE_NAMES[CHAR_LU], 
				MatchType.PERCENT, 
				TrimType.TRIM,				// was NOT trimmed in Python version 
				CHAR_TYPES[CHAR_LU]));
			
				
		// -----------------------------------------
		// Single Matches
		// -----------------------------------------

		// getLerg
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_ALLERG], 
				MatchType.EXISTS, 
				TrimType.NO_TRIM, 
				CaseType.LOWER, 
				SINGLE_STRINGS[STR_ALLERG]));	

		// getAmp
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_AMPERSAND], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_AMPERSAND]));
		
		// getStars
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_ASTERISK], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_ASTERISK]));	
		
		// getAt
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_AT], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_AT]));		
		
		// getBSlas
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_BACK_SLASH], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_BACK_SLASH]));				

		// getCaret
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_CARET], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_CARET]));
		
		// getColons
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_COLON], 
				MatchType.COUNT, 
				TrimType.TRIM, 
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_COLON]));
		
		// getSlot
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_COLON], 
				MatchType.ENDS, 
				TrimType.TRIM, 
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_COLON]));

		// getComma
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_COMMA], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_COMMA]));						
		
		// getHyph
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_DASH], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_DASH]));				

		// getHyPos
		feats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_DASH], 
				MatchType.FIRST_POSITION, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_DASH]));				

		// getDS
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_DOLLAR], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_DOLLAR]));		
		
		// getEqual
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_EQUAL], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_EQUAL]));				
				
		// getBang
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_EXCLAMATION], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_EXCLAMATION]));		

		// getSlash
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_FORWARD_SLASH], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_FORWARD_SLASH]));				
		
		// getPound
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_HASH], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_HASH]));						
		
		// getPerc
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_PERCENT], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_PERCENT]));
		
		// getEndp
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_PERIOD], 
				MatchType.ENDS, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_PERIOD]));
		
		// getPipe
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_PIPE], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_PIPE]));	

		// getPlus
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_PLUS], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_PLUS]));
						
		// getQM
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_QUES], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_QUES]));		
		
		// getQuest
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_QUES], 
				MatchType.ENDS, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_QUES]));		

		// getQPos
		feats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_QUES], 
				MatchType.FIRST_POSITION, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_QUES]));
		
		// getQuot
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_QUOTE], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_QUOTE]));	
				
		// getScrn
		feats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_SCREEN], 
				MatchType.FIRST_POSITION, 
				TrimType.TRIM,
				CaseType.LOWER, 
				SINGLE_STRINGS[STR_SCREEN]));
		
		// getSig
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_SIG], 
				MatchType.COUNT, 
				TrimType.NO_TRIM, 
				CaseType.LOWER, 
				SINGLE_STRINGS[STR_SIG]));	
		
		// getApost
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_TICK], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_TICK]));		
		
		// getUnder
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_UNDERSCORE], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_UNDERSCORE]));
		
		// getXes
		cntFeats.add(new LineMatch(
				SINGLE_STRING_NAMES[STR_X], 
				MatchType.COUNT, 
				TrimType.TRIM,
				CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_X]));
		
		// -----------------------------------------
		// Multiple Matches
		// -----------------------------------------

		// getCBrac
		cntFeats.add(new LineMatchMultiple(
				MULTI_STRINGS_NAMES[MSTR_BRACES], 
				MatchType.COUNT, 
				TrimType.TRIM, 
				CaseType.ORIGINAL, 
				MULTI_STRINGS[MSTR_BRACES]));
		
		// getBrack
		cntFeats.add(new LineMatchMultiple(
				MULTI_STRINGS_NAMES[MSTR_BRACKETS], 
				MatchType.COUNT, 
				TrimType.TRIM, 
				CaseType.ORIGINAL, 
				MULTI_STRINGS[MSTR_BRACKETS]));
		
		// getBull
		cntFeats.add(new LineMatchMultiple(
				MULTI_STRINGS_NAMES[MSTR_BULLETS], 
				MatchType.BEGINS, 
				TrimType.TRIM, 
				CaseType.ORIGINAL, 
				MULTI_STRINGS[MSTR_BULLETS]));		

		// getComp
		cntFeats.add(new LineMatchMultiple(
				MULTI_STRINGS_NAMES[MSTR_LESS_GREATER], 
				MatchType.COUNT, 
				TrimType.TRIM, 
				CaseType.ORIGINAL, 
				MULTI_STRINGS[MSTR_LESS_GREATER]));		

		// getParen
		cntFeats.add(new LineMatchMultiple(
				MULTI_STRINGS_NAMES[MSTR_PARENS], 
				MatchType.COUNT, 
				TrimType.TRIM, 
				CaseType.ORIGINAL, 
				MULTI_STRINGS[MSTR_PARENS]));				
						
		// getPunc
		cntFeats.add(new LineMatchMultiple(
				MULTI_STRINGS_NAMES[MSTR_PUNCTUATION], 
				MatchType.COUNT, 
				TrimType.TRIM, 
				CaseType.ORIGINAL, 
				MULTI_STRINGS[MSTR_PUNCTUATION]));				

		// getSpChar
		cntFeats.add(new LineMatchMultiple(
				MULTI_STRINGS_NAMES[MSTR_SPECIAL_CHARS], 
				MatchType.COUNT, 
				TrimType.TRIM, 
				CaseType.ORIGINAL, 
				MULTI_STRINGS[MSTR_SPECIAL_CHARS]));				
				
		// getYesNo
		cntFeats.add(new LineMatchMultiple(
				MULTI_STRINGS_NAMES[MSTR_YES_NO], 
				MatchType.ENDS, 
				TrimType.TRIM, 
				CaseType.LOWER, 
				MULTI_STRINGS[MSTR_YES_NO]));		
		
		
		
		
		/*
		// getCBrac
		cntFeats.add(new LineWordMatchMultiple(
				REL_FILE_NAMES[RF_ABBR], 
				MatchType.COUNT, 
				TrimType.TRIM, 
				CaseType.LOWER, 
				FileDao.getContents(REL_FILES[RF_ABBR])));

		// getMed
		cntFeats.add(new LineWordMatchMultiple(
				REL_FILE_NAMES[RF_MEDTERMS], 
				MatchType.COUNT, 
				TrimType.TRIM, 
				CaseType.LOWER, 
				FileDao.getContents(REL_FILES[RF_MEDTERMS])));
		 */
	
		
		
		// -----------------------------------------
		// Regular Expressions 
		// -----------------------------------------

		// getBoxes
		cntFeats.add(new LineRegExp(
				REG_EXP_NAMES[RE_BOXES], 
				MatchType.COUNT,
				TrimType.TRIM, 
				CaseType.LOWER, 
				REG_EXPS[RE_BOXES]));	

		// getDecs
		cntFeats.add(new LineRegExp(
				REG_EXP_NAMES[RE_DECIMALS], 
				MatchType.COUNT,
				TrimType.TRIM, 
				CaseType.LOWER, 
				REG_EXPS[RE_DECIMALS]));	
		
		// getDSM
		cntFeats.add(new LineRegExp(
				REG_EXP_NAMES[RE_DSM4], 
				MatchType.COUNT,
				TrimType.TRIM, 
				CaseType.LOWER, 
				REG_EXPS[RE_DSM4]));
		
		// getICD
		cntFeats.add(new LineRegExp(
				REG_EXP_NAMES[RE_ICD9], 
				MatchType.COUNT,
				TrimType.TRIM, 
				CaseType.LOWER, 
				REG_EXPS[RE_ICD9]));
		
		// getLNum
		cntFeats.add(new LineRegExp(
				REG_EXP_NAMES[RE_LINE_NUM], 
				MatchType.EXISTS,
				TrimType.TRIM, 
				CaseType.LOWER, 
				REG_EXPS[RE_LINE_NUM]));
				
		// getMg
		feats.add(new LineRegExp(
				REG_EXP_NAMES[RE_MG], 
				MatchType.FIRST_POSITION,
				TrimType.TRIM, 
				CaseType.LOWER, 
				REG_EXPS[RE_MG]));				
		
		// getTime
		cntFeats.add(new LineRegExp(
				REG_EXP_NAMES[RE_TIME], 
				MatchType.COUNT,
				TrimType.TRIM, 
				CaseType.LOWER, 
				REG_EXPS[RE_TIME]));	

		// getFone
		cntFeats.add(new LineRegExp(
				REG_EXP_NAMES[RE_PHONE], 
				MatchType.COUNT,
				TrimType.TRIM, 
				CaseType.LOWER, 
				REG_EXPS[RE_PHONE]));	
				
		// -----------------------------------------
		// Others 
		// -----------------------------------------

		// getCol1
		feats.add(new LineIndexOccurrence(
				TrimType.TRIM, CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_COLON], CountType.ONE));

		// getCol2
		feats.add(new LineIndexOccurrence(
				TrimType.TRIM, CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_COLON], CountType.TWO));

		// getCol3
		feats.add(new LineIndexOccurrence(
				TrimType.TRIM, CaseType.ORIGINAL, 
				SINGLE_STRINGS[STR_COLON], CountType.THREE));		
		
		// getFBar
		cntFeats.add(new LineAllSameChar(TrimType.TRIM, CaseType.ORIGINAL));
				
		// getLLen
		feats.add(new LineLength(TrimType.NO_TRIM));
		
		// getTLen
		feats.add(new LineLength(TrimType.TRIM));
		
		
		
		
		
		
		// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%		
		// Add all document features
		// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%		

		// No corresponding method in Python-version
		// Document-level of getTLen
		feats.add(new DocumentLength(TrimType.TRIM));

		// getNAveLL
		feats.add(new AvgLineLength(TrimType.NO_TRIM));

		// getNLen
		feats.add(new DocumentLength(TrimType.NO_TRIM));

		// getNumLin
		feats.add(new NumLines());
		
		
		// Add all line features that should also be
		// counted at the document level.  
		// Also add the line features to the list
		// of all features.
		for (LineFeature feature : cntFeats) {
			feats.add(new DocumentCountInstances(feature));
			feats.add(feature);
		}
		
		// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%		
		// Add all line-document features
		// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		
		// getLinPos
		feats.add(new LinePosition());
		
		return feats;
	}
	
	private Collection<Feature> features;
	
	public Extractor() {
		features = new ArrayList<Feature>();
	}
	
	public void addFeature(final Feature feature) {
		features.add(feature);
	}
	
	public void addFeatures(final Collection<Feature> features) {
		this.features.addAll(features);
	}
	
	public void addFeatures(final Feature... features) {
		for (Feature feature : features) {
			addFeature(feature);
		}
	}
	
	public void calculateFeatureValues(final Document document) {
		for (Feature feature : features) {
			if (feature instanceof LineFeature) {
				final LineFeature lf = (LineFeature)feature;
				
				for (Line line : document.getLines()) {
					line.addFeature(lf.getName(), lf.getValue(line));
				}
			} else if (feature instanceof DocumentFeature) {
				document.addFeature(feature.getName(), 
						((DocumentFeature)feature).getValue(document));
			} else if (feature instanceof LineDocumentFeature) {
				final LineDocumentFeature ldf = (LineDocumentFeature)feature;
				
				for (Line line : document.getLines()) {
					line.addFeature(ldf.getName(), ldf.getValue(line, document)); 
				}				
			} else {
				throw new IllegalArgumentException(String.format(
						"Feature of unknown type: %s", 
						this.getClass().getName()));
			}
		}
	}
	
	public Collection<Feature> getFeatures() {
		return features;
	}
	
	public void removeDocumentFeatures() {
		final Iterator<Feature> iter = features.iterator();
		
		while (iter.hasNext()) {
			final Feature feature = iter.next();
			
			if (feature instanceof DocumentFeature) {
				iter.remove();
			}
		}
	}
	
	public void removeLineDocumentFeatures() {
		final Iterator<Feature> iter = features.iterator();
		
		while (iter.hasNext()) {
			final Feature feature = iter.next();
			
			if (feature instanceof LineDocumentFeature) {
				iter.remove();
			}
		}
	}
	
	public void removeLineFeatures() {
		final Iterator<Feature> iter = features.iterator();
		
		while (iter.hasNext()) {
			final Feature feature = iter.next();
			
			if (feature instanceof LineFeature) {
				iter.remove();
			}
		}
	}
		
	public void setupCorpusProcessors(final Collection<Document> documents) {
		for (Feature feature : features) {
			if (feature instanceof CorpusProcessor) {
				((CorpusProcessor)feature).processCorpus(documents);
			}
		}
	}
}
