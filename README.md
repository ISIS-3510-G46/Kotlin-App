# Kotlin-App

Link to the team's [WIKI](https://github.com/ISIS-3510-G46/Backend-App/wiki)


# Overview

This project is a mobile application developed in Kotlin using Android Studio. The architecture used is MVVM (Model-View-ViewModel), and design patterns are implemented for this front-end component, such as DAO (Data Access Object) for interaction with data models managed from the backend; Repository for unified access to all information sources; Singleton for obtaining only one instance for each API and backend access; and finally, Observer, which operates on the frontend between the View and observes ViewModel state changes to apply them to the interface.

# Estructura del proyecto

```bash
com.moviles.clothingapp/
│
├── model/ (for DAOs and Repository)
│
├── navigation/
│   ├── AppNavigation.kt (to manage navigation between screens)
│
├── ui.theme/
│   ├── Color.kt (for color palette)
│   ├── Type.kt  (for text fonts)
│
├── view/ (Observers)
│   ├── Discover/
│   ├── HomeView/
│   ├── Login/
│
├── viewmodel/ (Subject/Observable)
│   ├── HomeViewModel.kt
│   ├── LoginViewModel.kt
│   ├── PostViewModel.kt
│   ├── ResetPasswordViewModel.kt
│   ├── WeatherViewModel.kt
│
├── MainActivity.kt (inits the app)
|
res/
├── drawable/ (stores static images)
├── font/ (stores installed fonts)
```

# Installation and Running

1. Clone the repository:

```bash
git clone https://github.com/ISIS-3510-G42/Kotlin-App.git
```

2. Open in Android Studio.
3. Run the application on an emulator or physical device.

Note: Keep in mind that development is done in the "develop" branch, while for each Sprint release, the final changes are moved to main.

# Using GitFlow:

In building this application, we used GitFlow, as shown below. Each member creates their own branches, publishes changes to them, and makes a pull request. If the changes are too large, they must wait to be approved and reviewed by one of the other members.

1. Make sure to be in the 'develop' branch and pull latest changes:

```bash
git checkout develop
git pull origin develop
```

2. Create a new feature branch:

```bash
git flow feature start nombre-de-la-feature
```

3. Work there, then commit changes:

```bash
git add .
git commit -m "Descripción del cambio"
```

4. Upload the branch to github:

```bash
git push origin feature/nombre-feature
```

5. Create the Pull Request in github to merge changes into develop or wait for approval and review of the merge by one of the peers.

# Additional information

## Authentication with Firebase auth

![image](https://github.com/user-attachments/assets/2957b2e1-1283-498c-a762-e0fb64957d76)

## Performance information with Firebase performance

![image](https://github.com/user-attachments/assets/f1968765-3229-41e2-86a4-9cc6e8b887a6)
![image](https://github.com/user-attachments/assets/d41d88d7-7681-4b95-b5ce-0e9ae0b0e22b)

## Firebase Analytics dashboard for user's locations

![image](https://github.com/user-attachments/assets/595ef7ec-b0a7-4bd3-b1c8-715fdf060cd5)

## Firebase Analytics dashboard with user's devices

![image](https://github.com/user-attachments/assets/59156e66-df80-4701-8950-42dbea1c45c2)

<p align="center">
    <img src="https://github.com/user-attachments/assets/1a87259d-9adf-47a9-b3e6-a3c5d3b4d0b9" width="45%">
    <img src="https://github.com/user-attachments/assets/fd35e1fc-a042-4bec-aa90-846ea70b15ee" width="45%">
</p>
