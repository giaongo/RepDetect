/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.poseexercise.posedetector.classification;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.example.poseexercise.data.PostureResult;
import com.google.common.base.Preconditions;
import com.google.mlkit.vision.pose.Pose;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Accepts a stream of {@link Pose} for classification and Rep counting.
 */
public class PoseClassifierProcessor {
    private static final String TAG = "PoseClassifierProcessor";

    // File names of all exercise
    private static final String SQUAT_FILE = "pose/squats.csv";
    private static final String PUSH_UP_FILE = "pose/pushups.csv";
    private static final String LUNGE_FILE = "pose/lunges.csv";
    private static final String NEUTRAL_STANDING_FILE = "pose/neutral_standing.csv";
    private static final String SIT_UP_FILE = "pose/situps.csv";
    private static final String CHEST_PRESS_FILE = "pose/chestpress.csv";
    private static final String DEAD_LIFT_FILE = "pose/deadlift.csv";
    private static final String SHOULDER_PRESS_FILE = "pose/shoulderpress.csv";
    private static final String TREE_YOGA_FILE = "pose/treeyoga.csv";
    private static final String WARRIOR_YOGA_FILE = "pose/warrioryoga.csv";
    //private static final String POSE_SAMPLES_FILE = "pose/fitness_four_exercise_two_yoga_v04.csv";



    // The class name for all the exercise
    public static final String PUSHUPS_CLASS = "pushups_down";
    public static final String SQUATS_CLASS = "squats";
    public static final String LUNGES_CLASS = "lunges";
    public static final String SITUP_UP_CLASS = "situp_up";
    public static final String CHEST_PRESS_CLASS = "chestpress_down";
    public static final String DEAD_LIFT_CLASS = "deadlift_down";
    public static final String SHOULDER_PRESS_CLASS = "shoulderpress_down";
    public static final String WARRIOR_CLASS = "warrior";
    public static final String YOGA_TREE_CLASS = "tree_pose";
    public static final String[] POSE_CLASSES = {
            PUSHUPS_CLASS, SQUATS_CLASS, LUNGES_CLASS, SITUP_UP_CLASS,CHEST_PRESS_CLASS,DEAD_LIFT_CLASS,SHOULDER_PRESS_CLASS, WARRIOR_CLASS, YOGA_TREE_CLASS
    };

    private final boolean isStreamMode;
    private EMASmoothing emaSmoothing;
    private List<RepetitionCounter> repCounters;
    private PoseClassifier poseClassifier;
    private final Map<String, PostureResult> postureResults = new HashMap<>();


    @WorkerThread
    public PoseClassifierProcessor(Context context, boolean isStreamMode, List<String> plan) {

        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper());
        this.isStreamMode = isStreamMode;
        if (isStreamMode) {
            emaSmoothing = new EMASmoothing();
            repCounters = new ArrayList<>();
        }

        if(plan != null){
            Log.d("pose_classifier_processor: ", plan.toString());
            Log.d("pose_classifier_processor: ", mapExercisesToFiles(plan).toString());
        }

        //loadPoseSamples(context);
        combineAndLoadPoseSamples(context, mapExercisesToFiles(plan));
    }

    private void combineAndLoadPoseSamples(Context context, List<String> mappedPlan) {
        // Ensure the combined file exists in internal storage
        String combinedFilePath = context.getFilesDir().getPath() + File.separator + "combined_poses.csv";

        createNewFileReplacingPrevious(combinedFilePath);

        // Combine separate CSV files into a new combined file
        combineCSVFiles(context, combinedFilePath, mappedPlan);

        // Now, load pose samples from the combined file
        loadPoseSamples(context, combinedFilePath);
    }

    private void createNewFileReplacingPrevious(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && !file.isDirectory()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Error creating file: " + filePath + "\n" + e);
        }
    }

    private void combineCSVFiles(Context context, String outputPath, List<String> inputFiles) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            for (String inputFile : inputFiles) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(inputFile)));
                String csvLine = reader.readLine();
                while (csvLine != null) {
                    writer.println(csvLine);
                    csvLine = reader.readLine();
                }
                // Add an empty line between files
                writer.println();

                reader.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error when combining CSV files.\n" + e);
        }
    }


    private static List<String> mapExercisesToFiles(List<String> exercises) {
        List<String> files = new ArrayList<>();
        Set<String> uniqueFileNames = new HashSet<>();

        if(exercises != null){
            for (String exercise : exercises) {
                switch (exercise) {
                    case "Squat":
                        addUniqueFile(files, uniqueFileNames, SQUAT_FILE);
                        addUniqueFile(files, uniqueFileNames, NEUTRAL_STANDING_FILE);
                        addUniqueFile(files, uniqueFileNames, LUNGE_FILE);
                        break;
                    case "Push up":
                        addUniqueFile(files, uniqueFileNames, PUSH_UP_FILE);
                        break;
                    case "Sit up":
                        addUniqueFile(files, uniqueFileNames, SIT_UP_FILE);
                        break;
                    case "Lunge":
                        addUniqueFile(files, uniqueFileNames, LUNGE_FILE);
                        addUniqueFile(files, uniqueFileNames, NEUTRAL_STANDING_FILE);
                        addUniqueFile(files, uniqueFileNames, SQUAT_FILE);
                        break;
                    case "Chest press":
                        addUniqueFile(files, uniqueFileNames, CHEST_PRESS_FILE);
                        break;
                    case "Dead lift":
                        addUniqueFile(files, uniqueFileNames, DEAD_LIFT_FILE);
                        break;
                    case "Shoulder press":
                        addUniqueFile(files, uniqueFileNames, SHOULDER_PRESS_FILE);
                        break;
                    // Add more cases for other exercises if needed
                    default:
                        break;
                }
            }
        }

        // Exercise by Default
        addUniqueFile(files, uniqueFileNames, LUNGE_FILE);
        addUniqueFile(files, uniqueFileNames, NEUTRAL_STANDING_FILE);
        addUniqueFile(files, uniqueFileNames, SQUAT_FILE);
        files.add(WARRIOR_YOGA_FILE);
        files.add(TREE_YOGA_FILE);

        return files;
    }

    private static void addUniqueFile(List<String> files, Set<String> uniqueFileNames, String fileName) {
        if (uniqueFileNames.add(fileName)) {
            // If the file name is unique, add it to the list
            files.add(fileName);
        }
    }


    private void loadPoseSamples(Context context, String filePath) {
        List<PoseSample> poseSamples = new ArrayList<>();
        try {

            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            String csvLine = reader.readLine();
            while (csvLine != null) {
                // If line is not a valid {@link PoseSample}, we'll get null and skip adding to the list.
                PoseSample poseSample = PoseSample.getPoseSample(csvLine, ",");
                if (poseSample != null) {
                    poseSamples.add(poseSample);
                }
                csvLine = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error when loading pose samples.\n" + e);
        }
        poseClassifier = new PoseClassifier(poseSamples);
        if (isStreamMode) {
            for (String className : POSE_CLASSES) {
                repCounters.add(new RepetitionCounter(className));
            }
        }
    }

    /**
     * Given a new {@link Pose} input, returns a list of formatted {@link String}s with Pose
     * classification results.
     *
     * <p>Currently it returns up to 2 strings as following:
     * 0: PoseClass : X reps
     * 1: PoseClass : [0.0-1.0] confidence
     */
    @WorkerThread
    public Map<String, PostureResult> getPoseResult(Pose pose) {
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper());

        ClassificationResult classification = poseClassifier.classify(pose);

        // Update {@link RepetitionCounter}s if {@code isStreamMode}.
        if (isStreamMode) {
            // Feed pose to smoothing even if no pose found.
            classification = emaSmoothing.getSmoothedResult(classification);

            // Return early without updating repCounter if no pose found.
            if (pose.getAllPoseLandmarks().isEmpty()) {
                return postureResults;
            }

            for (RepetitionCounter repCounter : repCounters) {
                int repsBefore = repCounter.getNumRepeats();
                int repsAfter = repCounter.addClassificationResult(classification);
                if (repsAfter > repsBefore) {
                    // Play a fun beep when rep counter updates.
                    ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);

                    // Add result to map
                    postureResults.put(repCounter.getClassName(), new PostureResult(repsAfter, 0, repCounter.getClassName()));
                    break;
                }
            }
        }

        // Add maxConfidence class of current frame to result if pose is found.
        if (!pose.getAllPoseLandmarks().isEmpty()) {
            String maxConfidenceClass = classification.getMaxConfidenceClass();

            // find the key from the map -> if it exists, update the confidence value, otherwise add a new entry
            if (postureResults.containsKey(maxConfidenceClass)) {
                Objects.requireNonNull(postureResults.get(maxConfidenceClass))
                        .setConfidence(classification.getClassConfidence(maxConfidenceClass) / poseClassifier.confidenceRange());
            } else {
                postureResults.put(maxConfidenceClass, new PostureResult(0, classification.getClassConfidence(maxConfidenceClass) / poseClassifier.confidenceRange(), maxConfidenceClass));
            }
        }

        return postureResults;
    }
}
