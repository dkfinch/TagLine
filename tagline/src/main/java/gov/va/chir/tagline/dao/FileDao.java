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
package gov.va.chir.tagline.dao;

import gov.va.chir.tagline.TagLineModel;
import gov.va.chir.tagline.beans.Document;
import gov.va.chir.tagline.beans.Line;
import gov.va.chir.tagline.features.Feature;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

public class FileDao {
	;

	private static String FILENAME_FEATURES = "features.ser";
	private static String FILENAME_MODEL = "model.mod";
	private static String FILENAME_HEADER = "header.arff";
	private static int BUFFER_SIZE = 1024;
	
	public static void createSyntheticTrainingDataset(
			final File file, final int nDocs, final int maxLinesPerDoc, 
			final int maxLineLength, final int nClasses) throws IOException {
		createSyntheticTrainingDataset(file, nDocs, maxLinesPerDoc, 
				maxLineLength, nClasses, System.currentTimeMillis());
	}
	
	public static void createSyntheticTrainingDataset(
			final File file, final int nDocs, final int maxLinesPerDoc, 
			final int maxLineLength, final int nClasses, final long seed) throws IOException {
		final String[] classes = new String[nClasses];
		
		for (int i = 0; i < classes.length; i++) {
			classes[i] = String.format("%s%d", "class", i);
		}
				
		final Random random = new Random(seed);
		final List<String> lines = new ArrayList<String>();
		lines.add((nClasses > 0 ? 
				"note_id\tline_id\tclass\ttext" : 
				"note_id\tline_id\ttext"));
		
		for (int i = 0; i < nDocs; i++) {
			// How many lines in this doc
			final int lineNum = random.nextInt(maxLinesPerDoc) + 1;
			
			for (int j = 0; j < lineNum; j++) {
				final String text = RandomStringUtils.randomAscii(
						random.nextInt(maxLineLength) + 1);
				
				if (nClasses > 0) {
					lines.add(String.format("%d\t%d\t%s\t%s",
							i, j,
							classes[random.nextInt(classes.length)],
							text.replace("\\", " ")));
				} else {
					lines.add(String.format("%d\t%d\t%s",
							i, j,
							text.replace("\\", " ")));					
				}
			}
		}
		
		FileUtils.writeLines(file, lines, false);
	}
	
	public static String[] getContents(final File file) throws IOException {
		final List<String> lines = new ArrayList<String>();
		
		FileReader fileReader = null;
		
		try {
			fileReader = new FileReader(file);
			
			BufferedReader br = null;
			
			 try {
				 br = new BufferedReader(fileReader);
				 
				 String line = null;
				 
				 while ((line = br.readLine()) != null) {
					 lines.add(line);
				 }
				 
			 } finally {
				 if (br != null) {
					 br.close();
				 }
			 }
			
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
		}

		return lines.toArray(new String[lines.size()]);
	}
	
	public static String[] getContents(final String relativeFilename) throws IOException {
		final List<String> lines = new ArrayList<String>();
		
		InputStream inputStream = null;
		
		try {
			inputStream = FileDao.class.getResourceAsStream(relativeFilename);
			BufferedReader br = null;
			
			 try {
				 br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				 
				 String line = null;
				 
				 while ((line = br.readLine()) != null) {
					 lines.add(line);
				 }
				 
			 } finally {
				 if (br != null) {
					 br.close();
				 }
			 }
			
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		return lines.toArray(new String[lines.size()]);
	}
	
	private static Collection<Feature> loadFeatures(final File file) throws IOException, ClassNotFoundException {
		final Collection<Feature> features = new ArrayList<Feature>();
		final ObjectInputStream in = new ObjectInputStream(
				new FileInputStream(file));
		
		try {
			Object o = in.readObject();
			
			while (o != null) {
				if (o instanceof Feature) {
					features.add((Feature)o);
					
					o = in.readObject();
				}
			}			
		} catch(EOFException ex) {
			// ignore
		}
		
		in.close();
		
		return features;
	}
	
	private static Instances loadHeader(final File file) throws IOException {
		ArffLoader loader = new ArffLoader();
		loader.setFile(file);
		return loader.getStructure();
	}
	
	public static Collection<Document> loadLabeledLines(final File file, boolean hasHeader) throws IOException {
		final Map<String, Map<Integer, Line>> docMap = 
				new HashMap<String, Map<Integer, Line>>();
		
		// Assumes tab-delimited columns. May not be sorted.
		// Column labels may be on first line
		// Col 0 = NoteID
		// Col 1 = LineID
		// Col 2 = Class
		// Col 3 = Text
		final int POS_DOC_ID = 0;
		final int POS_LINE_ID = 1;
		final int POS_CLASS = 2;
		final int POS_TEXT = 3;
		
		final List<String> contents = FileUtils.readLines(file);
		
		for (int i = (hasHeader ? 1 : 0); i < contents.size(); i++) {
			final String l = contents.get(i);
			final String[] array = l.split("\t");
			
			if (!docMap.containsKey(array[POS_DOC_ID])) {
				docMap.put(array[POS_DOC_ID], new TreeMap<Integer, Line>());
			}
			
			int lineId = NumberUtils.toInt(array[POS_LINE_ID]);
			
			final Line line = new Line(lineId, 
					(array.length == POS_TEXT ? "" : array[POS_TEXT]), // blank lines have no content 
					array[POS_CLASS]);

			docMap.get(array[POS_DOC_ID]).put(lineId, line);
		}
		
		final Collection<Document> docs = new ArrayList<Document>();
		
		for (String docId : docMap.keySet()) {
			docs.add(new Document(docId, new ArrayList<Line>(
					docMap.get(docId).values())));
		}
		
		return docs;
	}

	private static Classifier loadModel(final File file) throws Exception {
		return (Classifier)SerializationHelper.read(file.getAbsolutePath());
	}
	
	public static Collection<Document> loadScoringDocs(final File dir) throws IOException {
		final Collection<Document> docs = new ArrayList<Document>();
		
		if (dir == null) {
			throw new IllegalArgumentException("Directory must not be null");
		}
		
		final Collection<File> files = FileUtils.listFiles(dir, null, false);
		
		for (File file : files) {
			docs.add(new Document(file.getName(), 
					FileUtils.readFileToString(file)));
		}
		
		return docs;
	}
	
	public static Collection<Document> loadScoringLines(final File file, boolean hasHeader) throws IOException {
		final Map<String, Map<Integer, Line>> docMap = 
				new HashMap<String, Map<Integer, Line>>();
		
		// Assumes tab-delimited columns. May not be sorted.
		// Column labels may be on first line
		// Col 0 = NoteID
		// Col 1 = LineID
		// Col 2 = Text
		final int POS_DOC_ID = 0;
		final int POS_LINE_ID = 1;
		final int POS_TEXT = 2;
		
		final List<String> contents = FileUtils.readLines(file);
		
		for (int i = (hasHeader ? 1 : 0); i < contents.size(); i++) {
			final String l = contents.get(i);
			final String[] array = l.split("\t");
			
			if (!docMap.containsKey(array[POS_DOC_ID])) {
				docMap.put(array[POS_DOC_ID], new TreeMap<Integer, Line>());
			}
			
			int lineId = NumberUtils.toInt(array[POS_LINE_ID]);
			final Line line = new Line(
					lineId,
					array[POS_TEXT]);
			
			docMap.get(array[POS_DOC_ID]).put(lineId, line);
		}
		
		final Collection<Document> docs = new ArrayList<Document>();
		
		for (String docId : docMap.keySet()) {
			docs.add(new Document(docId, new ArrayList<Line>(
					docMap.get(docId).values())));
		}
		
		return docs;
	}
	
	public static TagLineModel loadTagLineModel(final File file) throws Exception {
		final TagLineModel model = new TagLineModel();
		
		// Unzip each file to temp
		final File temp = new File(System.getProperty("java.io.tmpdir"));

		byte[] buffer = new byte[BUFFER_SIZE];
		
		final ZipInputStream zis = new ZipInputStream(
				new FileInputStream(file));
		
		ZipEntry entry = zis.getNextEntry();
		
		while (entry != null) {
			final String name = entry.getName();
			
			File tempFile = new File(temp, name);
			
			// Write out file
			final FileOutputStream fos = new FileOutputStream(tempFile);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			
			fos.close();
			
			// Determine which file was written
			if (name.equalsIgnoreCase(FILENAME_FEATURES)) {
				model.setFeatures(loadFeatures(tempFile));
			} else if (name.equalsIgnoreCase(FILENAME_HEADER)) {
				model.setHeader(loadHeader(tempFile));
			} else if (name.equalsIgnoreCase(FILENAME_MODEL)) {
				model.setModel(loadModel(tempFile));
			} else {
				throw new IllegalStateException(String.format(
						"Unknown file in TagLine model file (%s)", name));
			}
		
			// Delete temp file
			tempFile.delete();
			
			// Get next entry
			zis.closeEntry();
			entry = zis.getNextEntry();
		}
		
		zis.close();
		
		return model;
	}
		
	private static void saveFeatures(final File file, final Collection<Feature> features) throws IOException {
		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(file));
		
		for (Feature feature : features) {
			out.writeObject(feature);
		}
		
		out.close();
	}
	
	private static void saveHeader(final File file, final Instances header) throws IOException {
		ArffSaver saver = new ArffSaver();
		saver.setStructure(header);
		saver.setFile(file);
		saver.writeBatch();
	}
	
	private static void saveModel(final File file, final Classifier model) throws Exception {
		SerializationHelper.write(file.getAbsolutePath(), (Object)model);
	}
	
	public static void savePerformance(final File file, final String performance) throws IOException {
		FileUtils.writeStringToFile(file, performance);
	}
	
	public static void saveTagLineModel(final File file, final TagLineModel tagLineModel) throws Exception {
		// Save each individual file to temp and then zip into a "model" file.
		final File temp = new File(System.getProperty("java.io.tmpdir"));
		
		// Save features to temp file
		final File featureFile = new File(temp, FILENAME_FEATURES);
		saveFeatures(featureFile, tagLineModel.getFeatures());
		
		// Save header to temp file
		final File headerFile = new File(temp, FILENAME_HEADER);
		saveHeader(headerFile, tagLineModel.getHeader());
		
		// Save classification model to temp file
		final File modelFile = new File(temp, FILENAME_MODEL);
		saveModel(modelFile, tagLineModel.getModel());
		
		// Zip all files together
		saveZipFile(file, featureFile, headerFile, modelFile);		
		
		// Delete temp files
		featureFile.delete();
		headerFile.delete();
		modelFile.delete();
	}
	
	private static void saveZipFile(final File zipFile, final File... inputFiles) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		
		final ZipOutputStream zos = new ZipOutputStream(
				new FileOutputStream(zipFile));
		
		for (File file : inputFiles) {
			final ZipEntry entry = new ZipEntry(file.getName());
			zos.putNextEntry(entry);
			
			final FileInputStream in = new FileInputStream(file);
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			
			in.close();
			zos.closeEntry();
		}
		
		zos.close();
	}
}
