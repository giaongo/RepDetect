<a name="readme-top"></a>
<h1 align="center">RepDetect</h1> 

<h3 align="center">Make counting of exercise easier with RepDetect.</h3>

<p align="center">
  <img width="200" height="160" src="https://users.metropolia.fi/~shilpasy/repdetectLogo.png">
</p>

## Mobile project with AI

Welcome to RepDetect, an Android application crafted with Kotlin that harnesses the power of MediaPipe Pose Landmark Detection to deliver real-time feedback on exercise form while accurately counting repetitions. This application supports four fundamental exercises—pushup, squat, situp, and lunges—alongside a dedicated yoga module.
The app's primary goal is to assist users in maintaining correct exercise techniques while tracking their workout progress.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Getting Started](#getting-started)
- [Screenshots](#screenshots)
- [References](#references)
- [Contributors](#contributors)


## Overview

Developed as part of our third-year course at Metropolia University of Applied Sciences, RepDetect is a collaborative effort by a team of four students. Our primary objective was to create an innovative fitness application adhering to specific guidelines and meeting the course requirements.

## Features

- **Pose Detection AI Model:** Accurate detection of user poses to evaluate exercise form.
- **Pose Classification:** Identify and classify different exercise poses for precise feedback.
- **Repetition Counting:** The AI model accurately counts repetitions for each exercise, helping users track their progress
- **Real-Time Feedback:** The app uses MediaPipe Pose Landmark Detection to analyze the user's exercise form in real-time and provides immediate feedback on correctness.
- **Exercise Plan:** Users can create customized exercise plans, specifying the number of repetitions for each exercise. Plans can be based on specific exercises, allowing for a personalized workout routine.
- **Room Database:** RepDetect utilizes the Room database to store and manage exercise plans and workout results.
- **Voice Notifications:** Receive voice notifications at the start and completion of exercises for a seamless workout experience.
- **Exercise History:** The app maintains a detailed exercise history, providing users with insights into their performance over time. Users can view weekly and daily summaries, helping them stay motivated and on track.
- **Camera Flipping:** Easily switch between front and back cameras for varied workout perspectives.

## Getting Started

### Prerequisites

- Android Studio: Make sure you have the latest version of Android Studio installed.
- Kotlin: The project is written in Kotlin, so familiarity with Kotlin is recommended.

### Installation

1. Clone the repository
    ```http
     https://github.com/SurajKRB/RepDetect.git
    ```
2. Open the project in Android Studio.

3. Build and run the app on your Android device or emulator.

### Usage

1. Open the app on your Android device.

2. Choose the exercise you want to perform (pushup, squat, situp, lunges, or yoga).

3. Set up your exercise plan, specifying the number of repetitions for each exercise.

4. Start your workout, and the app will provide real-time feedback on your form and count your repetitions.

5. View your exercise history to track your progress over time.

## Screenshots

<img width="174" height="340" alt="logoScreen" src="https://user-images.githubusercontent.com/59287175/286032211-89e92e59-5d96-4967-89d5-b03b53a8fa25.png"><img width="175" height="340" alt="homeScreen" src="https://user-images.githubusercontent.com/59287175/286029496-897ce822-1f2a-4b01-9302-2443068c0275.png"><img width="174" height="340" alt="profileScreen" src="https://user-images.githubusercontent.com/59287175/286029060-dcb8ce47-2dd4-4bb1-b46b-e9fd51e50501.png"><img width="174" alt="workoutScreen" src="https://user-images.githubusercontent.com/59287175/286029854-d3e3d56a-f175-4e3f-a4d0-c634b13b9ed3.png"><img width="174" height="340" alt="gif" src="https://user-images.githubusercontent.com/59287175/286030249-3af2ffb4-c296-4fd5-ac01-9089b6d169df.png"><img width="174" height="340" alt="externalCameraScreen" src="https://user-images.githubusercontent.com/59287175/286030588-476642c7-daf2-4062-8a1f-1a1e19512fc3.png"><img width="174" height="340" alt="planListScreen" src="https://user-images.githubusercontent.com/59287175/286030947-b0bf9440-f373-45c9-9c1a-ed1c63519d28.png"><img width="174" height="340" alt="exerciseListScreen" src="https://user-images.githubusercontent.com/59287175/286031666-817c7362-b81c-4c77-afd5-f65571cc90ed.png">><img width="174" height="340" alt="resultScreen" src="https://user-images.githubusercontent.com/59287175/286032381-72c84806-ff22-4773-8d9f-536fd8c14a85.png">

## References

[Pose landmark detection guide](https://developers.google.com/mediapipe/solutions/vision/pose_landmarker/index)

[Train the model ](https://colab.research.google.com/drive/19txHpN8exWhstO6WVkfmYYVC6uug_oVR)

## Contributors
[Chi Nguyen](https://github.com/chinguyen202)

[Giao Ngo](https://github.com/giaongo)

[Suraj Rana Bhat](https://github.com/SurajKRB)

[Shilpa Singh Yadav](https://github.com/Shilupa)


<p align="right">(<a href="#readme-top">back to top</a>)</p>
