# 🌿 Nature Explorer - Android App

Nature Explorer is a feature-rich Android application built with Kotlin and Jetpack Compose that helps users track walks, capture discoveries, and explore nature through maps, timelines, and personal progress statistics. The app integrates sensors, GPS, camera, ML classification, and cloud services and authentication into one cohesive experience.

Originally started during coursework and later expanded into an independent portfolio project with new features and UI improvements.

---

## 📱 Overview

Nature Game is an Android application built in Android Studio with Jetpack Compose, Google Maps, and Room. 
Nature Explorer encourages users to explore the outdoors by tracking walks, capturing photos, identifying plants, and saving discoveries.

The app combines:

- Sensors (step counter)
- GPS tracking and mapping
- CameraX image capture
- On-device machine learning (ML Kit)
- Firebase Authentication, Firestore & Storage
- Local persistence with Room
- Modern UI with Jetpack Compose + Material 3

Users can log in, track their walks, capture and classify nature photos, save discoveries, and view them on a map, in a list, or in a timeline.

--- 

## 🛠️ Tech Stack

### Languages & Frameworks
- Kotlin
- Jetpack Compose
- Material 3

### Architecture
- MVVM
- Repository Pattern
- StateFlow
- Hilt Dependency Injection

### Data & Backend
- Room Database
- Firebase Authentication
- Firestore
- Firebase Storage

### Device & APIs
- Google Maps Compose
- Fused Location Provider
- CameraX
- ML Kit Image Classification
- Step Counter Sensor

---

## ✨ Features

### Firebase Authentication
- Firebase login / register
- Secure logout
- Password reset flow
- protected screens

### 🗺️ Walk Tracking
- Real-time GPS route tracking
- Distance measurement
- Step counter integration
- Pause / Resume walk sessions
- Walk history and statistics saved to Room

### 📸 Discoveries & Memories
- Save discoveries with image, category, notes, and location
- Timeline screen showing discoveries in chronological order
- One-click jump from timeline to map location
- Category-based discovery list with color-coded cards

📷 Camera + ML Classification
- CameraX capture
- Loading indicator
- On‑device plant classifier
- Confidence scores & top labels
- Add notes before saving

### 👤 User Profile
- Personal statistics dashboard
- Longest walk tracking
- Achievement badges
- Activity overview
- Logout

### 💾 Data & Storage
- Room database for local persistence
- Firebase-ready cloud architecture


---

## 📸 Screenshots

| Map Screen | Camera Screen |
|------------|---------------|
| <img src="IMAGE_LINK_1" width="260"> | <img src="IMAGE_LINK_2" width="260"> |

| Discoveries | Timeline |
|-------------|----------|
| <img src="IMAGE_LINK_3" width="260"> | <img src="IMAGE_LINK_4" width="260"> |

| Statistics | Profile |
|------------|---------|
| <img src="IMAGE_LINK_5" width="260"> | <img src="IMAGE_LINK_6" width="260"> |

## 🗺️ Screenshot
**Map Screen**<br>
<img width="300" alt="Map_Screen" src="https://github.com/user-attachments/assets/467829fc-2843-49cb-b737-d0a940c01017" />

**Marker and Polyline**<br>
<img width="300" alt="Marker_And_Polyline" src="https://github.com/user-attachments/assets/7a81d728-14bd-447b-a9ae-003c638fca04" />

**Marker Note**<br>
<img width="300" alt="Marker_Note" src="https://github.com/user-attachments/assets/e972ffd8-98ee-4414-a428-c2a49af192f8" />

**Camera Screen**<br>
<img width="300" alt="Camera_Screen" src="https://github.com/user-attachments/assets/076dd4e4-e491-4986-b730-662c2c7e69ab" />

**Discover Screen**<br>
<img width="300" alt="Discovery_Screen" src="https://github.com/user-attachments/assets/6389f9d0-7c1c-43c0-8a05-2b3eb3a6998b" />

**Stats Screen**<br>
<img width="300" alt="Stats_Screen" src="https://github.com/user-attachments/assets/2eaf1f14-843e-4609-a57e-3b2f6977a9ef" />

**Profile Screen**<br>
<img width="300" alt="Profile_Screen" src="https://github.com/user-attachments/assets/4191295e-69c2-4bcb-a2cb-2d2776eb4a37" />

**Timeline Screen**<br>

---

## Demo video
https://github.com/user-attachments/assets/bef877bc-01cd-4c04-b2e7-ed2f85aa3d40

---

## 🏗️ Architecture

The app follows modern Android architecture principles:

- MVVM (Model-View-ViewModel)
- Jetpack Compose for UI  
- ViewModel for state management  
- Hilt for dependency injection  
- Room for local persistence  
- Repository pattern  
- Google Maps Compose  
- CameraX
- Firebase Auth, Firestore, Storage

---
## 🧭 Navigation Structure

- **LoginScreen** - authentication
- **RegisterScreen** - account creation
- **MapScreen** — main map with markers  
- **CameraScreen** — capture & classify photos
- **ExploreScreen** - list of discoveries
- **TimelineScreen** - chronological feed
- **StatsScreen** - walk statistics 
- **ProfileScreen** —  user info & logout  

---

## 📚 Learning Outcomes

This project demonstrates:

- Modern Android development with Compose
- Integration of sensors, GPS, and camera
- On‑device ML classification
- Firebase‑connected architecture
- Clean code, UI polish, and app lifecycle management
- Turning a course project into a portfolio‑ready app

---
## 👩‍💻 Author

Yvonne Frankort  
Originally developed for the Mobile App Development course at OAMK (Spring 2026), and extended with additional features and improvements.
