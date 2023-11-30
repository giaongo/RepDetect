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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Accepts a stream of {@link Pose} for classification and Rep counting.
 */
public class PoseClassifierProcessor {
    private static final String TAG = "PoseClassifierProcessor";

    private static final String POSE_SAMPLES_FILE = "pose/fitness_four_exercise_two_yoga_v04.csv";

    // The class name for the pushups
    public static final String PUSHUPS_CLASS = "pushups_down";


    // The class name for squat
    public static final String SQUATS_CLASS = "squats";

    //class name for lunges
    public static final String LUNGES_CLASS = "lunges";

    // The class name for the situp
    public static final String SITUP_UP_CLASS = "situp_up";

    // The class name for the yoga pose
    public static final String WARRIOR_CLASS = "warrior";

    // The class name for the yoga tree pose
    public static final String YOGA_TREE_CLASS = "tree_pose";

    public static final String[] POSE_CLASSES = {
            PUSHUPS_CLASS, SQUATS_CLASS, LUNGES_CLASS, SITUP_UP_CLASS, WARRIOR_CLASS, YOGA_TREE_CLASS

    };

    private final boolean isStreamMode;

    private EMASmoothing emaSmoothing;
    private List<RepetitionCounter> repCounters;
    private PoseClassifier poseClassifier;

    private final Map<String, PostureResult> postureResults = new HashMap<>();

    @WorkerThread
    public PoseClassifierProcessor(Context context, boolean isStreamMode) {
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper());
        this.isStreamMode = isStreamMode;
        if (isStreamMode) {
            emaSmoothing = new EMASmoothing();
            repCounters = new ArrayList<>();
        }
        loadPoseSamples(context);
    }

    private void loadPoseSamples(Context context) {
        List<PoseSample> poseSamples = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(POSE_SAMPLES_FILE)));
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
