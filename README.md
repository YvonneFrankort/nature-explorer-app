# 🌿 Nature Game Mobile

A solo Android course project focused on building a feature-rich mobile application 
using modern Android development tools and APIs.

---

## 📱 Overview

Nature Game is an Android application build in Android Studio with Jetpack Compose, Google Maps, and Room. 
The app encourages users to explore nature by tracking walks and record their 
observations such as plants, animals, and other natural objects.

The app combines multiple mobile technologies:

- Sensors (step counter)
- GPS tracking and mapping
- Camera and image capture
- On-device machine learning (ML Kit)
- Cloud integration (Firebase)

Users can track their walks, capture photos, identify species, and store observations locally or in the cloud

--- 

## ✨ Features
### Core Features
- 👟 Step tracking using device sensors
- 🗺️ GPS route tracking with map visualization
- 📷 Photo capture using CameraX
- 🌿 Plant recognition using ML Kit
- 💾 Local storage with Room database
- ☁️ Cloud-ready architecture (Firebase)
- 👌 Playstore signing <br>

### Additional Features (Implemented)
- ✅ Full Firebase integration (Auth, Firestore, Storage)
- 📍 Observations displayed as markers on the map
- 📝 Custom user comments on observations
- 🧠 Multi-category ML recognition (plants, animals, landscape etc.)
- 👤 User profile with stats (steps, distance, discoveries)

---

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

---
## 🧭 Navigation Structure

- **MapScreen** — main map with markers  
- **CameraScreen** — take photos  
- **ProfileScreen** — user stats  
- **WalkHistoryScreen** — list of walks  
- **WalkStatsCard** — summary of walking activity  

---

## 🏃 Walk Tracking

- Uses fused location provider  
- Tracks distance in real time  
- Saves sessions to Room  
- Displays history and stats  

---
## 📚 Learning Outcomes

This project demonstrates:

- Practical Android development with modern tools
- Integration of hardware sensors and system services
- Use of on-device machine learning
- Cloud-connected mobile architecture
- Full app lifecycle from development to release

---
## 🔗 Course Materials

Course content: https://www.villemajava.com

--- 
## 👩‍💻 Author

**Yvonne Frankort**  
OAMK — Mobile App Development, Spring 2026
