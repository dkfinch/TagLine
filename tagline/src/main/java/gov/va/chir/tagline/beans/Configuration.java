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

import gov.va.chir.tagline.TaskType;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

public class Configuration {
				
	private static final String TASK_TYPE = "task.type";
	private static final String OUT_LOG = "file.output.log";

	private static final String TRAIN_FILE_IN_DATASET = "train.file.input.dataset";
	private static final String TRAIN_FILE_OUT_MODEL = "train.file.output.model";
	private static final String TRAIN_FILE_OUT_DATASET = "train.file.output.dataset";	
	private static final String TRAIN_CLASSIFIER_TYPE = "train.classifier.type";
	private static final String TRAIN_CLASSIFIER_OPTS = "train.classifier.options";
	
	private static final String EVAL_FILE_IN_DATASET = "eval.file.input.dataset";
	private static final String EVAL_FILE_OUT_PERF = "eval.file.output.performance";
	private static final String EVAL_CLASSIFIER_TYPE = "eval.classifier.type";
	private static final String EVAL_CLASSIFIER_OPTS = "eval.classifier.options";
	
	private static final String SCORE_FILE_IN_DATASET = "score.file.input.dataset";
	private static final String SCORE_FILE_IN_MODEL = "score.file.input.model";
	private static final String SCORE_FILE_OUT_DATASET = "score.file.output.dataset";
	
	private static final String CREATE_FILE_OUT_DATASET = "create.file.output.dataset";
	private static final String CREATE_CLASSES = "create.classes";
	private static final String CREATE_DOCS = "create.docs";
	private static final String CREATE_MAX_LINES_PER_DOC = "create.maxlinesperdoc";
	private static final String CREATE_MAX_LINE_LENGTH = "create.maxlinelength";
	private static final String CREATE_RANDOMSEED = "create.randomseed";

	private org.apache.commons.configuration.Configuration config;

	private String filename;

	public Configuration(final String filename) {
		this.filename = filename;
		
		// Load configuration file
		try {
			config = new PropertiesConfiguration(filename);
		} catch (ConfigurationException e) {
			config = null;
			e.printStackTrace();
		}				
	}
	
	public String getConfigContents() throws IOException {
		return FileUtils.readFileToString(new File(filename));
	}

	public File getCreateFileOutputDataset() {
		return new File(config.getString(CREATE_FILE_OUT_DATASET));
	}
	
	public int getCreateMaxLineLength() {
		return config.getInt(CREATE_MAX_LINE_LENGTH);
	}
	
	public int getCreateMaxLinesPerDoc() {
		return config.getInt(CREATE_MAX_LINES_PER_DOC);
	}
	
	public int getCreateNumClasses() {
		return config.getInt(CREATE_CLASSES);
	}

	public int getCreateNumDocs() {
		return config.getInt(CREATE_DOCS);
	}

	public int getCreateRandomSeed() {
		return config.getInt(CREATE_RANDOMSEED);
	}

	public String[] getEvalClassifierOptions() {
		return config.getStringArray(EVAL_CLASSIFIER_OPTS);
	}
	
	public ClassifierType getEvalClassifierType() {
		return ClassifierType.getClassifierType(config.getString(EVAL_CLASSIFIER_TYPE));
	}
	
	public File getEvalFileInputDataset() {
		return new File(config.getString(EVAL_FILE_IN_DATASET));
	}

	public File getEvalFileOutputPerformance() {
		return new File(config.getString(EVAL_FILE_OUT_PERF));
	}
	
	public File getOutLog() {
		return new File(config.getString(OUT_LOG));
	}
	
	public File getScoreFileInputDataset() {
		return new File(config.getString(SCORE_FILE_IN_DATASET));
	}

	public File getScoreFileInputModel() {
		return new File(config.getString(SCORE_FILE_IN_MODEL));
	}
	
	public File getScoreFileOutputDataset() {
		return new File(config.getString(SCORE_FILE_OUT_DATASET));
	}
	
	public TaskType getTaskType() {
		return TaskType.fromString(config.getString(TASK_TYPE));
	}

	public String[] getTrainClassifierOptions() {
		return config.getStringArray(TRAIN_CLASSIFIER_OPTS);
	}
	
	public ClassifierType getTrainClassifierType() {
		return ClassifierType.getClassifierType(config.getString(TRAIN_CLASSIFIER_TYPE));
	}
	
	public File getTrainFileInputDataset() {
		return new File(config.getString(TRAIN_FILE_IN_DATASET));
	}

	public File getTrainFileOutputDataset() {
		return new File(config.getString(TRAIN_FILE_OUT_DATASET));
	}	
		
	public File getTrainFileOutputModel() {
		return new File(config.getString(TRAIN_FILE_OUT_MODEL));
	}		
}
