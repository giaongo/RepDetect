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

package com.example.poseexercise.views.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.poseexercise.viewmodels.CameraXViewModel
import com.example.poseexercise.views.graphic.GraphicOverlay
import com.example.poseexercise.R
import com.example.poseexercise.util.VisionImageProcessor
import com.example.poseexercise.posedetector.PoseDetectorProcessor
import com.example.poseexercise.views.fragment.preference.PreferenceUtils
import com.google.android.gms.common.annotation.KeepName
import com.google.mlkit.common.MlKitException


/** Live preview demo app for ML Kit APIs using CameraX. */
@KeepName
@RequiresApi(VERSION_CODES.LOLLIPOP)
class CameraXLivePreviewActivity :
  AppCompatActivity(), OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d(TAG, "onCreate")

    if (!allRuntimePermissionsGranted()) {
      getRuntimePermissions()
    }

    cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    setContentView(R.layout.activity_vision_camerax_live_preview)
    previewView = findViewById(R.id.preview_view)
    if (previewView == null) {
      Log.d(TAG, "previewView is null")
    }
    graphicOverlay = findViewById(R.id.graphic_overlay)
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null")
    }


/*    val spinner = findViewById<Spinner>(R.id.spinner)
    val options: MutableList<String> = ArrayList()
    options.add(POSE_DETECTION)


    // Creating adapter for spinner
    val dataAdapter = ArrayAdapter(this, R.layout.spinner_style, options)
    // Drop down layout style - list view with radio button
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    // attaching data adapter to spinner
    spinner.adapter = dataAdapter
    spinner.onItemSelectedListener = this*/

    val facingSwitch = findViewById<ToggleButton>(R.id.facing_switch)
    facingSwitch.setOnCheckedChangeListener(this)

    ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
      .get(CameraXViewModel::class.java)
      .processCameraProvider
      .observe(
        this,
        Observer { provider: ProcessCameraProvider? ->
          cameraProvider = provider
          bindAllCameraUseCases()
        }
      )

/*    val settingsButton = findViewById<ImageView>(R.id.settings_button)
    settingsButton.setOnClickListener {
      val intent = Intent(applicationContext, SettingsActivity::class.java)
      intent.putExtra(SettingsActivity.EXTRA_LAUNCH_SOURCE, SettingsActivity.LaunchSource.CAMERAX_LIVE_PREVIEW)
      startActivity(intent)
    }*/

  }

  override fun onSaveInstanceState(bundle: Bundle) {
    super.onSaveInstanceState(bundle)
    bundle.putString(STATE_SELECTED_MODEL, selectedModel)
  }

  @Synchronized
  override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent?.getItemAtPosition(pos).toString()
    Log.d(TAG, "Selected model: $selectedModel")
    bindAnalysisUseCase()
  }

  override fun onNothingSelected(parent: AdapterView<*>?) {
    // Do nothing.
  }

  override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
    if (cameraProvider == null) {
      return
    }
    val newLensFacing =
      if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
        CameraSelector.LENS_FACING_BACK
      } else {
        CameraSelector.LENS_FACING_FRONT
      }
    val newCameraSelector = CameraSelector.Builder().requireLensFacing(newLensFacing).build()
    try {
      if (cameraProvider!!.hasCamera(newCameraSelector)) {
        Log.d(TAG, "Set facing to " + newLensFacing)
        lensFacing = newLensFacing
        cameraSelector = newCameraSelector
        bindAllCameraUseCases()
        return
      }
    } catch (e: CameraInfoUnavailableException) {
      // Falls through
    }
    Toast.makeText(
        applicationContext,
        "This device does not have lens with facing: $newLensFacing",
        Toast.LENGTH_SHORT
      )
      .show()
  }

  public override fun onResume() {
    super.onResume()
    bindAllCameraUseCases()
  }

  override fun onPause() {
    super.onPause()

    imageProcessor?.run { this.stop() }
  }

  public override fun onDestroy() {
    super.onDestroy()
    imageProcessor?.run { this.stop() }
  }

  private fun bindAllCameraUseCases() {
    if (cameraProvider != null) {
      // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
      cameraProvider!!.unbindAll()
      bindPreviewUseCase()
      bindAnalysisUseCase()
    }
  }

  private fun bindPreviewUseCase() {
    if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
      return
    }
    if (cameraProvider == null) {
      return
    }
    if (previewUseCase != null) {
      cameraProvider!!.unbind(previewUseCase)
    }

    val builder = Preview.Builder()
    val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
    if (targetResolution != null) {
      builder.setTargetResolution(targetResolution)
    }
    previewUseCase = builder.build()
    previewUseCase!!.setSurfaceProvider(previewView!!.getSurfaceProvider())
    camera =
      cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector!!, previewUseCase)
  }

  private fun bindAnalysisUseCase() {
    if (cameraProvider == null) {
      return
    }
    if (analysisUseCase != null) {
      cameraProvider!!.unbind(analysisUseCase)
    }
    if (imageProcessor != null) {
      imageProcessor!!.stop()
    }
    imageProcessor =
      try {
        when (selectedModel) {

          POSE_DETECTION -> {
            val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
            val shouldShowInFrameLikelihood =
              PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
            val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
            val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
            val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)

            Log.d("CLASSIFICATION: ", runClassification.toString())

            PoseDetectorProcessor(
              this,
              poseDetectorOptions,
              shouldShowInFrameLikelihood,
              visualizeZ,
              rescaleZ,
              runClassification,
              /* isStreamMode = */ true
            )
          }
          else -> throw IllegalStateException("Invalid model name")
        }
      } catch (e: Exception) {
        Log.e(TAG, "Can not create image processor: $selectedModel", e)
        Toast.makeText(
            applicationContext,
            "Can not create image processor: " + e.localizedMessage,
            Toast.LENGTH_LONG
          )
          .show()
        return
      }

    val builder = ImageAnalysis.Builder()
    val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
    if (targetResolution != null) {
      builder.setTargetResolution(targetResolution)
    }
    analysisUseCase = builder.build()

    needUpdateGraphicOverlayImageSourceInfo = true

    analysisUseCase?.setAnalyzer(
      // imageProcessor.processImageProxy will use another thread to run the detection underneath,
      // thus we can just runs the analyzer itself on main thread.
      ContextCompat.getMainExecutor(this),
      ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
        if (needUpdateGraphicOverlayImageSourceInfo) {
          val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
          val rotationDegrees = imageProxy.imageInfo.rotationDegrees
          if (rotationDegrees == 0 || rotationDegrees == 180) {
            graphicOverlay!!.setImageSourceInfo(imageProxy.width, imageProxy.height, isImageFlipped)
          } else {
            graphicOverlay!!.setImageSourceInfo(imageProxy.height, imageProxy.width, isImageFlipped)
          }
          needUpdateGraphicOverlayImageSourceInfo = false
        }
        try {
          imageProcessor!!.processImageProxy(imageProxy, graphicOverlay)
        } catch (e: MlKitException) {
          Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
          Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
      }
    )
    cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector!!, analysisUseCase)
  }



  private fun allRuntimePermissionsGranted(): Boolean {
    for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
      permission?.let {
        if (!isPermissionGranted(this, it)) {
          return false
        }
      }
    }
    return true
  }

  private fun getRuntimePermissions() {
    val permissionsToRequest = java.util.ArrayList<String>()
    for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
      permission?.let {
        if (!isPermissionGranted(this, it)) {
          permissionsToRequest.add(permission)
        }
      }
    }

    if (permissionsToRequest.isNotEmpty()) {
      ActivityCompat.requestPermissions(
        this,
        permissionsToRequest.toTypedArray(),
        PERMISSION_REQUESTS
      )
    }
  }

  private fun isPermissionGranted(context: Context, permission: String): Boolean {
    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    ) {
      Log.i(TAG, "Permission granted: $permission")
      return true
    }
    Log.i(TAG, "Permission NOT granted: $permission")
    return false
  }


  companion object {
    private const val TAG = "CameraXLivePreview"
    private const val POSE_DETECTION = "Pose Detection"

    private const val STATE_SELECTED_MODEL = "selected_model"

    private const val PERMISSION_REQUESTS = 1

    private val REQUIRED_RUNTIME_PERMISSIONS =
      arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
      )
  }
}
