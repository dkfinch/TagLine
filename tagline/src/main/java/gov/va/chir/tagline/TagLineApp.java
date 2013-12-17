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

import gov.va.chir.tagline.beans.AnnotationType;
import gov.va.chir.tagline.beans.ClassifierType;
import gov.va.chir.tagline.beans.Configuration;
import gov.va.chir.tagline.beans.Document;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class TagLineApp {
	private static final boolean TO_CONSOLE = true;
	
	public static void main(String[] args) {
		String configFilename = "config.properties";
		
		if (args != null && args.length == 1) {
            configFilename = args[0];
        }
		
		final Configuration config = new Configuration(configFilename);
		
		final TagLineApp tla = new TagLineApp(config);
		try {			
			tla.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} 	
	}

	private static void writeLog(final File file, final String s) throws IOException {
        final String output = String.format("%s%s", 
                s, System.getProperty("line.separator"));
		FileUtils.writeStringToFile(file, output, true);
		
		if (TO_CONSOLE) {
			System.out.print(output);
		}
    }

	private Configuration config;	
	
	public TagLineApp(final Configuration config) {
		this.config = config;
	}
	
	private void createSyntheticDataset(final File log, final File dataset, 
			final int numDocs, final int maxLinesPerDoc, 
			final int maxLineLength, final int numClasses, 
			final long randomSeed) throws IOException {
		
		writeLog(log, "Creating synthetic dataset");
		
		if (dataset == null) {
			throw new IllegalArgumentException("Dataset file must not be null");
		}
		
		if (numDocs <= 0 || maxLinesPerDoc <= 0 || maxLineLength <= 0) {
			throw new IllegalArgumentException("Number of docs, maximum lines "
					+ "per doc, and maximum line length must all be positive "
					+ "integers greater than zero");
		}
		
		// numClasses == 0 is a special case
		if (numClasses != 0 && numClasses < 2) {
			throw new IllegalArgumentException("There must be at least two classes");
		}
		
		FileDao.createSyntheticTrainingDataset(dataset, numDocs, 
				maxLinesPerDoc, maxLineLength, numClasses, randomSeed);
		writeLog(log, "Finished");
	}
			
	private void evaluatePerformance(final File log, final File dataset, 
			final File performance) throws Exception {
		
		writeLog(log, "Evaluating performance");
		
		if (dataset == null || performance == null) {
			throw new IllegalArgumentException(
					"Input dataset and output performance files must not be null");
		}

		writeLog(log, "Loading input data");
		Collection<Document> docs = FileDao.loadTrainingLines(dataset,	true);
		
		writeLog(log, String.format("Loaded %,d docs", docs.size()));
		
		writeLog(log, "Training and evaluating models");
		final TagLineEvaluator tle = new TagLineEvaluator(docs);
		tle.evaluate(ClassifierType.J48);
		
		writeLog(log, "Saving performance");
		FileDao.savePerformance(performance, tle.getEvaluationSummary());
		
		writeLog(log, "Finished");
	}
	
	public void execute() throws Exception {
        final File log = config.getOutLog();
        log.delete();
        
        writeLog(log, "Configuration file contents:");
        writeLog(log, config.getConfigContents());        
        writeLog(log, "---------------------------------------------------------------");
        
        writeLog(log, "");
        writeLog(log, String.format("Starting: %s", new Date().toString()));
        writeLog(log, "");
        
        final TaskType taskType = config.getTaskType();
        
        if (taskType == TaskType.CREATE) {
        	createSyntheticDataset(log, 
        			config.getCreateFileOutputDataset(), 
        			config.getCreateNumDocs(), 
        			config.getCreateMaxLinesPerDoc(), 
        			config.getCreateMaxLineLength(), 
        			config.getCreateNumClasses(), 
        			config.getCreateRandomSeed());
        } else if (taskType == TaskType.EVALUATE) {
        	evaluatePerformance(log, 
        			config.getEvalFileInputDataset(), 
        			config.getEvalFileOutputPerformance());
        } else if (taskType == TaskType.SCORE) {
        	scoreData(log, 
        			config.getScoreFileInputDataset(),
        			config.getScoreFileInputModel(),
        			config.getScoreFileOutputDataset());
        } else if (taskType == TaskType.TRAIN) {
        	trainClassifier(log,
        			config.getTrainFileInputDataset(),
        			config.getTrainFileOutputModel(),
        			config.getTrainFileOutputDataset());
        } else {
        	throw new IllegalArgumentException(
        			String.format("Unsupported task type: %s", taskType));
        }
        
        writeLog(log, "");
        writeLog(log, String.format("Finishing: %s", new Date().toString()));
	}
	
	private void scoreData(final File log, final File dataset, 
			final File modelFile, final File outputDataset) throws Exception {

		writeLog(log, "Scoring data");
		
		if (dataset == null || modelFile == null || outputDataset == null) {
			throw new IllegalArgumentException(
					"Input and Output datasets, and model files must not be null");
		}

		writeLog(log, "Loading input data");
		Collection<Document> docs = FileDao.loadScoringLines(dataset, true);
		
		writeLog(log, String.format("\tLoaded %,d docs", docs.size()));

		if (docs == null || docs.isEmpty()) {
			throw new IllegalStateException("Must have at least one document to score");
		} else {
			writeLog(log, "Loading model");
			final TagLineModel model = FileDao.loadTagLineModel(modelFile);
			writeLog(log, String.format("\tLoaded %,d features", 
					model.getFeatures().size()));
					
			writeLog(log, "Loading classifier");
			final TagLineScorer tls = new TagLineScorer(model);
			final Annotator annotator = new Annotator(AnnotationType.values());
			
			writeLog(log, "Scoring documents");
			final DatasetSaver ds = new DatasetSaver(outputDataset);
						
			for (Document doc : docs) {
				tls.applyModel(doc);
				ds.saveRecord(doc);
				annotator.annotate(doc);
			}
		} 
		// @TODO - what about saving annotations?
		
		writeLog(log, "Finished");
	}
	
	private void trainClassifier(final File log, final File dataset, 
			final File modelFile, final File outputDataset) throws Exception {
		
		writeLog(log, "Training a classifier");
		
		if (dataset == null || modelFile == null) {
			throw new IllegalArgumentException(
					"Dataset and model files must not be null");
		}
		
		writeLog(log, "Loading input data");
		Collection<Document> docs = FileDao.loadTrainingLines(dataset, true);
		
		writeLog(log, String.format("Loaded %,d docs", docs.size()));
		
		if (docs == null || docs.isEmpty()) {
			throw new IllegalStateException("Must have at least one document to score");
		} else {
			writeLog(log, "Loading trainer");
			final TagLineTrainer tlt = new TagLineTrainer(ClassifierType.J48);
			tlt.train(docs);
			
			writeLog(log, "Finished training");
			
			// Save dataset (optional)
			if (outputDataset != null) {
				writeLog(log, "Saving dataset with features");
				
				final DatasetSaver ds = new DatasetSaver(outputDataset);
				
				ds.saveRecords(docs);
				
//				FileDao.saveDataset(outputDataset, tlt.getInstances());
			}
			
			// Save model
			writeLog(log, "Saving model");
			FileDao.saveTagLineModel(modelFile, tlt.getTagLineModel());			
		}
		
		writeLog(log, "Finished");
	}	
}
