package com.example.poseexercise.views.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.poseexercise.R
import com.example.poseexercise.adapters.WorkoutAdapter
import com.example.poseexercise.data.plan.ExerciseLog
import com.example.poseexercise.data.plan.ExercisePlan
import com.example.poseexercise.data.results.WorkoutResult
import com.example.poseexercise.posedetector.PoseDetectorProcessor
import com.example.poseexercise.util.MyApplication
import com.example.poseexercise.util.MyUtils.Companion.convertTimeStringToMinutes
import com.example.poseexercise.util.MyUtils.Companion.databaseNameToClassification
import com.example.poseexercise.util.MyUtils.Companion.exerciseNameToDisplay
import com.example.poseexercise.util.VisionImageProcessor
import com.example.poseexercise.viewmodels.CameraXViewModel
import com.example.poseexercise.viewmodels.HomeViewModel
import com.example.poseexercise.viewmodels.ResultViewModel
import com.example.poseexercise.views.activity.MainActivity
import com.example.poseexercise.views.fragment.preference.PreferenceUtils
import com.example.poseexercise.views.graphic.GraphicOverlay
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.common.MlKitException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class WorkOutFragment : Fragment() {
    private lateinit var resultViewModel: ResultViewModel
    private var screenOn = false
    private var previewView: PreviewView? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var selectedModel = POSE_DETECTION
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var cameraSelector: CameraSelector? = null
    private lateinit var startButton: Button
    private lateinit var buttonCompleteExercise: Button
    private lateinit var buttonCancelExercise: Button
    private lateinit var cameraFlipFAB: FloatingActionButton
    private lateinit var confIndicatorView: ImageView
    private lateinit var currentExerciseTextView: TextView
    private lateinit var currentRepetitionTextView: TextView

    private lateinit var cameraViewModel: CameraXViewModel

    private var mRecTimer: Timer? = null
    private var mRecSeconds = 0
    private var mRecMinute = 0
    private var mRecHours = 0
    private lateinit var timerTextView: TextView
    private lateinit var timerRecordIcon: ImageView
    private lateinit var ttf: TextToSpeech
    private lateinit var workoutRecyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var homeViewModel: HomeViewModel
    private var today : String = DateFormat.format("EEEE" , Date()) as String
    private var runOnce: Boolean = false
    private lateinit var loadingTV: TextView
    private lateinit var loadProgress: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!allRuntimePermissionsGranted()) {
            getRuntimePermissions()
        }
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        cameraViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().application)
        )[CameraXViewModel::class.java]
        resultViewModel = ResultViewModel(MyApplication.getInstance())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_workout, container, false)

        // Linking all button and controls
        cameraFlipFAB = view.findViewById(R.id.facing_switch)
        startButton = view.findViewById(R.id.button_start_exercise)
        buttonCompleteExercise = view.findViewById(R.id.button_complete_exercise)
        buttonCancelExercise = view.findViewById(R.id.button_cancel_exercise)
        timerTextView = view.findViewById(R.id.timerTV)
        timerRecordIcon = view.findViewById(R.id.timerRecIcon)
        confIndicatorView = view.findViewById(R.id.confidenceIndicatorView)
        currentExerciseTextView = view.findViewById(R.id.currentExerciseText)
        currentRepetitionTextView = view.findViewById(R.id.currentRepetitionText)
        confIndicatorView.visibility = View.INVISIBLE

        loadingTV = view.findViewById(R.id.loadingStatus)
        loadProgress = view.findViewById(R.id.loadingProgress)

        workoutRecyclerView = view.findViewById(R.id.workoutRecycleViewArea)
        workoutRecyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById(R.id.preview_view)
        graphicOverlay = view.findViewById(R.id.graphic_overlay)
        cameraFlipFAB.visibility = View.VISIBLE


        // start exercise button
        startButton.setOnClickListener {

            // showing loading AI pose detection Model inforamtion to user
            loadingTV.visibility = View.VISIBLE
            loadProgress.visibility = View.VISIBLE

            // Set the screenOn flag to true, preventing the screen from turning off
            screenOn = true

            // Add the FLAG_KEEP_SCREEN_ON flag to the activity's window, keeping the screen on
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            cameraFlipFAB.visibility = View.GONE
            buttonCancelExercise.visibility = View.VISIBLE
            buttonCompleteExercise.visibility = View.VISIBLE
            startButton.visibility = View.GONE

            // To disable screen timeout
            //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


            cameraViewModel.triggerClassification.value = true
        }


        // Cancel the exercise
        buttonCancelExercise.setOnClickListener {
            textToSpeech("Workout Cancelled")
            stopMediaTimer()
            Navigation.findNavController(view)
                .navigate(R.id.action_workoutFragment_to_cancelFragment)
            // Set the screenOn flag to false, allowing the screen to turn off
            screenOn = false
            // Clear the FLAG_KEEP_SCREEN_ON flag to allow the screen to turn off
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            // stop triggering classification process
            cameraViewModel.triggerClassification.value = false
        }

        // 10 reps =  3.2 for push up -> 1 reps = 3.2/10
        // Complete the exercise
        val sitUp = Postures.sitUp
        val pushUps = Postures.pushups
        val lunges = Postures.lunges
        val squats = Postures.squats
        buttonCompleteExercise.setOnClickListener {
            textToSpeech("Workout Complete")
            cameraViewModel.postureLiveData.value?.let {
                //val builder = StringBuilder()
                for ((_, value) in it) {
                    if (value.repetition != 0) {
                        lifecycleScope.launch {
                            val calorie = when (value.postureType) {
                                sitUp.type -> sitUp.value / 10
                                pushUps.type -> pushUps.value / 10
                                lunges.type -> lunges.value / 10
                                squats.type -> squats.value / 10
                                else -> 0.0
                            }
                            val workoutTime = convertTimeStringToMinutes(timerTextView.text.toString())

                           val workOutResult = WorkoutResult(
                                0,
                                value.postureType,
                                value.repetition,
                                value.confidence,
                                System.currentTimeMillis(),
                                calorie * value.repetition,
                               workoutTime
                            )
                            resultViewModel.insert(workOutResult)
                        }
                    }
                }
            }

            // update the workoutTimer in MainActivity
            val currentTimer = timerTextView.text.toString()
            MainActivity.workoutTimer = currentTimer

            stopMediaTimer()

            // Set the screenOn flag to false, allowing the screen to turn off
            screenOn = false

            // Clear the FLAG_KEEP_SCREEN_ON flag to allow the screen to turn off
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            // update MainActivity static postureResultData based on the postureLiveData
            Log.d("WorkoutFragment", "complete button clicked")

            // update the workoutResultData in MainActivity
            cameraViewModel.postureLiveData.value?.let {
                val builder = StringBuilder()
                for ((_, value) in it) {
                    if (value.repetition != 0) {
                        builder.append("${transformText(value.postureType)}: ${value.repetition}\n")
                    }
                }
                if (builder.toString().isNotEmpty()) MainActivity.workoutResultData =
                    builder.toString()
            }

            // stop triggering classification process
            cameraViewModel.triggerClassification.value = false

            // Navigation to complete fragment
            Navigation.findNavController(view)
                .navigate(R.id.action_workoutFragment_to_completedFragment)
        }


        if (previewView == null) {
            Log.d(TAG, "Preview is null")
        }
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }

        cameraViewModel.processCameraProvider.observe(viewLifecycleOwner) { provider: ProcessCameraProvider? ->
            cameraProvider = provider
            bindAllCameraUseCases()
        }

        cameraFlipFAB.setOnClickListener {
            toggleCameraLens()
        }

        // initialize the list of plan exercise to be filled from database
        val databaseExercisePlan = mutableListOf<ExercisePlan>()

        // Initialize Exercise Log
        val exerciseLog = ExerciseLog()

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // get the list of plans from database
        lifecycleScope.launch (Dispatchers.IO) {

            // get not completed exercise from database using coroutine
            val notCompletedExercise = withContext(Dispatchers.IO) { homeViewModel.getNotCompletePlans(today)}

            notCompletedExercise?.forEach {item ->
                val exercisePlan = ExercisePlan(databaseNameToClassification(item.exercise), item.repeatCount)

                val existingExercisePlan = databaseExercisePlan.find { it.exerciseName == databaseNameToClassification(item.exercise) }

                if (existingExercisePlan != null) {
                    // Update repetitions if ExercisePlan with the same exerciseName already exists
                    existingExercisePlan.repetitions += item.repeatCount
                } else {
                    // Add a new ExercisePlan if not already in the databaseExercisePlan
                    databaseExercisePlan.add(exercisePlan)
                }
            }

            // Push the planned exercise name in exercise Log
            databaseExercisePlan.forEach { exerciseLog.addExercise(it.exerciseName,0,0f,false) }

        }

        //Declare all the only pose exercise
        val onlyExercise = listOf("squats", "pushups_down", "lunges", "situp_up")
        val onlyPose = listOf("yoga")



        cameraViewModel.postureLiveData.observe(viewLifecycleOwner) { mapResult ->

            for ((key, value) in mapResult) {

                // Visualize the repetition exercise data
                if (key in onlyExercise) {

                    // make confidence marker invisible
                    confIndicatorView.visibility = View.INVISIBLE

                    // get the data from exercise log of specific exercise
                    val data = exerciseLog.getExerciseData(key)

                    if (data == null) {
                        // Adding exercise for the first time
                        exerciseLog.addExercise(key, value.repetition, value.confidence, false)

                    } else if (value.repetition == data.repetitions.plus(1)) {

                        // check if the exercise target is complete
                        var repetition: Int? = databaseExercisePlan.find { it.exerciseName.equals(key, ignoreCase = true) }?.repetitions
                        if (repetition==null || repetition==0){
                            repetition = HighCount
                        }
                        if (!data.isComplete && (value.repetition >= repetition)) {
                            // Adding data only when the increment happen
                            exerciseLog.addExercise(key, value.repetition, value.confidence, true)

                            // inform the user about completion only once
                            textToSpeech(exerciseNameToDisplay(key) + " exercise Complete")

                        } else if (data.isComplete) {
                            // Adding data only when the increment happen
                            exerciseLog.addExercise(key, value.repetition, value.confidence, true)
                        } else {
                            // Adding data only when the increment happen
                            exerciseLog.addExercise(key, value.repetition, value.confidence, false)
                        }


                        // display Current result when the increment happen
                        displayResult(key, exerciseLog)

                        // update the display list of all exercise progress when the increment happen
                        val exerciseList = exerciseLog.getExerciseDataList()
                        workoutAdapter = WorkoutAdapter(exerciseList, databaseExercisePlan)
                        workoutRecyclerView.adapter = workoutAdapter
                    }
                } else if (key in onlyPose){
                    // Implementation of pose confidence
                    confIndicatorView.visibility = View.VISIBLE
                    displayConfidence(value.confidence)
                } else{
                    confIndicatorView.visibility = View.INVISIBLE
                }
            }

            // Visualize list of all exercise result for the first time, to show the target exercise
            if(!runOnce){
                val exerciseList = exerciseLog.getExerciseDataList()
                workoutAdapter = WorkoutAdapter(exerciseList, databaseExercisePlan)
                workoutRecyclerView.adapter = workoutAdapter
                runOnce = true
                loadingTV.visibility = View.GONE
                loadProgress.visibility = View.GONE
                textToSpeech("Workout Started")
            }
        }
    }


    @Suppress("DEPRECATION")
    private fun textToSpeech(name: String) {
        ttf = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                ttf.language = Locale.US
                ttf.setSpeechRate(1.0f)
                ttf.speak(name, TextToSpeech.QUEUE_ADD, null)
            }
        }
        if(name == "Workout Started"){
            startMediaTimer()
            timerTextView.visibility = View.VISIBLE
            timerRecordIcon.visibility = View.VISIBLE
        }
    }


    @SuppressLint("SetTextI18n")
    private fun displayResult(key: String, exerciseLog: ExerciseLog) {
        currentExerciseTextView.visibility = View.VISIBLE
        currentRepetitionTextView.visibility = View.VISIBLE
        val pushUpData = exerciseLog.getExerciseData(key)
        currentExerciseTextView.text = exerciseNameToDisplay(key)
        currentRepetitionTextView.text = "count: " + pushUpData?.repetitions.toString()
    }


    private fun displayConfidence(confidence: Float) {
        if (confidence < 0.5) {
            confIndicatorView.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red)
        } else if (confidence > 0.5 && confidence <= 0.6) {
            confIndicatorView.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.orange)
        } else if (confidence > 0.6 && confidence <= 0.75) {
            confIndicatorView.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.yellow)
        } else if (confidence > 0.75 && confidence <= 0.85) {
            confIndicatorView.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.lightGreen)
        } else {
            confIndicatorView.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.green)
        }
    }


    private fun bindAllCameraUseCases() {
        bindPreviewUseCase()
        cameraViewModel.triggerClassification.observe(viewLifecycleOwner) { pressed ->
            bindAnalysisUseCase(pressed)
        }
    }

    /**
     * bind preview use case
     */
    @Suppress("DEPRECATION")
    private fun bindPreviewUseCase() {
        if (!PreferenceUtils.isCameraLiveViewportEnabled(requireContext())) {
            return
        }
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }
        val builder = Preview.Builder()
        val targetResolution =
            PreferenceUtils.getCameraXTargetResolution(requireContext(), lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        camera = cameraProvider!!.bindToLifecycle(this, cameraSelector!!, previewUseCase)
    }

    /**
     * bind analysis use case
     */
    private fun bindAnalysisUseCase(runClassification: Boolean) {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider?.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor?.stop()
        }
        imageProcessor = try {
            when (selectedModel) {
                POSE_DETECTION -> {
                    // get all the setting preferences for camera x live preview
                    val poseDetectorOptions =
                        PreferenceUtils.getPoseDetectorOptionsForLivePreview(requireContext())
                    val shouldShowInFrameLikelihood =
                        PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(
                            requireContext()
                        )
                    val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(requireContext())
                    val rescaleZ =
                        PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(requireContext())

                    //PreferenceUtils.shouldPoseDetectionRunClassification(requireContext())

                    // Build Pose Detector Processor based on the settings/preferences
                    PoseDetectorProcessor(
                        requireContext(),
                        poseDetectorOptions,
                        shouldShowInFrameLikelihood,
                        visualizeZ,
                        rescaleZ,
                        runClassification,
                        true,
                        cameraViewModel
                    )
                }

                else -> throw IllegalStateException("Invalid model name")
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Can not create image processor: $selectedModel",
                e
            )
            Toast.makeText(
                requireContext(),
                "Can not create image processor: " + e.localizedMessage,
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val builder = ImageAnalysis.Builder()
        analysisUseCase = builder.build()
        needUpdateGraphicOverlayImageSourceInfo = true
        analysisUseCase?.setAnalyzer(
            ContextCompat.getMainExecutor(requireContext())
        ) { imageProxy: ImageProxy ->
            if (needUpdateGraphicOverlayImageSourceInfo) {
                val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                if (rotationDegrees == 0 || rotationDegrees == 180) {
                    graphicOverlay!!.setImageSourceInfo(
                        imageProxy.width,
                        imageProxy.height,
                        isImageFlipped
                    )
                } else {
                    graphicOverlay!!.setImageSourceInfo(
                        imageProxy.height,
                        imageProxy.width,
                        isImageFlipped
                    )
                }
                needUpdateGraphicOverlayImageSourceInfo = false
            }

            try {
                imageProcessor!!.processImageProxy(imageProxy, graphicOverlay)
            } catch (e: MlKitException) {
                Log.e(
                    TAG,
                    "Failed to process image. Error: " + e.localizedMessage
                )
                Toast.makeText(
                    requireContext(),
                    e.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        cameraProvider?.bindToLifecycle(this, cameraSelector!!, analysisUseCase)
    }


    private fun allRuntimePermissionsGranted(): Boolean {
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission.let {
                if (!isPermissionGranted(requireContext(), it)) {
                    return false
                }
            }
        }
        return true
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }

    private fun getRuntimePermissions() {
        val permissionsToRequest = java.util.ArrayList<String>()
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission.let {
                if (!isPermissionGranted(requireContext(), it)) {
                    permissionsToRequest.add(permission)
                }
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUESTS
            )
        }
    }

    /**
     *
     */
    private fun toggleCameraLens() {
        if (cameraProvider == null) {
            Log.d(TAG, "Camera provider is null")
            return
        }
        val newLensFacing =
            if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
        val newCameraSelector = CameraSelector.Builder().requireLensFacing(newLensFacing).build()

        try {
            if (cameraProvider!!.hasCamera(newCameraSelector)) {
                Log.d(TAG, "Set facing to $newLensFacing")
                lensFacing = newLensFacing
                cameraSelector = newCameraSelector
                bindAllCameraUseCases()
                return
            }
        } catch (e: CameraInfoUnavailableException) {
            Log.e(TAG, "Failed to get camera info", e)
        }
    }

    /**
     * timer handling coroutine
     */
    private val mMainHandler: Handler by lazy {
        Handler(Looper.getMainLooper()) {
            when (it.what) {
                WHAT_START_TIMER -> {
                    if (mRecSeconds % 2 != 0) {
                        timerRecordIcon.visibility = View.VISIBLE
                    } else {
                        timerRecordIcon.visibility = View.INVISIBLE
                    }
                    timerTextView.text = calculateTime(mRecSeconds, mRecMinute)
                }

                WHAT_STOP_TIMER -> {
                    timerTextView.text = calculateTime(0, 0)
                    timerRecordIcon.visibility = View.GONE
                    timerTextView.visibility = View.GONE
                }
            }
            true
        }
    }


    /**
     * Start timer functionality
     */
    private fun startMediaTimer() {
        val pushTask: TimerTask = object : TimerTask() {
            override fun run() {

                mRecSeconds++

                if (mRecSeconds >= 60) {
                    mRecSeconds = 0
                    mRecMinute++
                }

                if (mRecMinute >= 60) {
                    mRecMinute = 0
                    mRecHours++
                    if (mRecHours >= 24) {
                        mRecHours = 0
                        mRecMinute = 0
                        mRecSeconds = 0
                    }
                }
                mMainHandler.sendEmptyMessage(WHAT_START_TIMER)
            }
        }
        if (mRecTimer != null) {
            stopMediaTimer()
        }
        mRecTimer = Timer()

        mRecTimer?.schedule(pushTask, 1000, 1000)
    }


    /**
     * Stop timer functionality
     */
    private fun stopMediaTimer() {
        if (mRecTimer != null) {
            mRecTimer?.cancel()
            mRecTimer = null
        }
        mRecHours = 0
        mRecMinute = 0
        mRecSeconds = 0
        mMainHandler.sendEmptyMessage(WHAT_STOP_TIMER)
    }

    /**
     * Calculate the time and return string
     */
    private fun calculateTime(seconds: Int, minute: Int, hour: Int? = null): String {
        val mBuilder = java.lang.StringBuilder()

        if (hour != null) {
            if (hour < 10) {
                mBuilder.append("0")
                mBuilder.append(hour)
            } else {
                mBuilder.append(hour)
            }
            mBuilder.append(":")
        }

        if (minute < 10) {
            mBuilder.append("0")
            mBuilder.append(minute)
        } else {
            mBuilder.append(minute)
        }

        mBuilder.append(":")
        if (seconds < 10) {
            mBuilder.append("0")
            mBuilder.append(seconds)
        } else {
            mBuilder.append(seconds)
        }
        return mBuilder.toString()
    }

    /**
     * Transform the posture result text to be displayed in the CompletedFragment
     */
    private fun transformText(input: String): String {
        val regex = Regex("_")
        if (regex.containsMatchIn(input)) {
            return regex.replace(input.lowercase(), " ")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        } else {
            print("No match found")
        }
        return input.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

    companion object {
        private const val TAG = "CameraXLivePreview"
        private const val POSE_DETECTION = "Pose Detection"
        private const val PERMISSION_REQUESTS = 1

        private const val WHAT_START_TIMER = 0x00
        private const val WHAT_STOP_TIMER = 0x01
        private const val HighCount = 9999999

        private val REQUIRED_RUNTIME_PERMISSIONS =
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
    }

    class TypedConstant(val type: String, val value: Double)
    object Postures {
        val pushups = TypedConstant("pushups_down", 3.2)
        val lunges = TypedConstant("lunges", 3.0)
        val squats = TypedConstant("squats", 3.8)
        val sitUp = TypedConstant("situp_up", 5.0)
    }
}