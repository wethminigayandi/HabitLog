# HabitLog — Mobile Well-Being & Health Tracker

HabitLog is a device-responsive Android application built to improve daily well-being and health by encouraging routine, mindfulness, and healthy habits through a clean, intuitive user interface.

---

## 📱 Core Features

* **Habit Management:** Easily add, modify, or remove daily habits. The app provides real-time graphical tracking to monitor the fulfillment of goals like water intake, meditation, exercise, or walking.
* **Mood Journal:** Features a simple, enjoyable journal panel where users record their emotional states using emojis accompanied by automatic timestamps to cleanly track behavioral and emotional patterns.
* **Hydration Reminder System:** Implemented via **Alarm Manager** to provide users with timely, background system notifications to stay hydrated throughout the day.
* **Home-Screen Widget:** A custom motivational widget displaying the live percentage completion of daily habits directly on the home screen for quick access.

---

## 🛠️ Architecture & Technical Implementation

The application is structured following modern Android development principles to ensure device responsiveness and optimal memory management:

* **UI Architecture (Fragments):** The application layout is split into distinct **Fragments** for different major views (Settings, Habits, and Mood Journal) to ensure smooth animations and clean lifecycle management.
* **Dynamic Content (RecyclerViews):** Dynamic lists—such as active habits and past mood entries—are managed efficiently using custom **Adapters** bound to fluid **RecyclerViews**.
* **Data Persistence (Shared Preferences):** Vital user configurations, custom app settings, and historical progress data are saved locally using **Shared Preferences** so that data is safely retained even after the application is closed.
