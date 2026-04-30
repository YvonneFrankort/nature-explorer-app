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

### Map & Camera
| Map Screen | Camera Screen |
|------------|---------------|
| <img width="260" alt="Map_Screen" src="https://github.com/user-attachments/assets/bd0e307d-9992-4019-8aa1-df576bb69ed7" />  | <img width="260" alt="Camera_Screen" src="https://github.com/user-attachments/assets/9e42438a-993d-48a5-b12f-7930d20e9414" />

### Discoveries & Timeline
| Discoveries | Timeline |
|-------------|----------|
| <img width="260" alt="Discovery_Screen" src="https://github.com/user-attachments/assets/3b760ba4-5472-4f8d-a397-c0c750b975ae" /> | <img width="260" alt="Timeline_Screen" src="https://github.com/user-attachments/assets/0701801a-ca15-418f-9a07-37e2de3a0cce" />|

### Stats & Profile
| Statistics | Profile |
|------------|---------|
| <img width="260" alt="Stats_Screen" src="https://github.com/user-attachments/assets/126a718c-f6f4-47ed-b612-49e917bd8604" /> | <img width="260" alt="Profile_Screen" src="https://github.com/user-attachments/assets/99ae57b9-e408-43e0-a4ec-56196e910054" /> |

### Login & Map Marker
| Login | Marker |
|-------------|----------|
| <img width="260" alt="Login" src="https://github.com/user-attachments/assets/e9686e39-7c18-4c1f-858e-666f90e6f544" /> | <img width="260" alt="Marker" src="https://github.com/user-attachments/assets/badc175d-2e82-4731-947a-67f85521d078" /> |

---

## Demo video

https://github.com/user-attachments/assets/4183ae9f-daf1-4784-88f8-1396120fc090

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
