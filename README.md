# RoboRally Game Client

A Java-based client application for the RoboRally board game, implementing a MVC architecture with REST API integration. This client allows users to manage game sessions, including creating, joining, and managing multiplayer games.

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   ├── module-info.java
│   │   └── dk.dtu.compute.course02324.part4.consuming_rest/
│   │       ├── RunRoborallyApp.java
│   │       ├── model/
│   │       │   ├── Game.java
│   │       │   ├── User.java
│   │       │   └── Player.java
│   │       ├── controller/
│   │       │   ├── UserController.java
│   │       │   ├── GameController.java
│   │       │   ├── PlayerController.java
│   │       │   └── RestApiService.java
│   │       └── view/
│   │           ├── MainView.java
│   │           └── dialogs/
│   │               ├── LeaveGameDialog.java
│   │               ├── ShowOnlineGamesDialog.java
│   │               ├── SignUpDialog.java
│   │               ├── GameSignUpDialog.java
│   │               ├── CreateGameDialog.java
│   │               ├── DeleteGameDialog.java
│   │               └── SignInDialog.java
│   └── resources/
└── test/
    └── java/
```

## Implementation Guide

When the app is launched, no information about the games is shown. When the user either signs up and signs in, the main menu shows all the registered games in the program (both, the ACTIVE ones and the SIGNUP ones) with all the information for every game. Only when the user goes to the SOG screen, the action buttons on each games appear to be able to Join, Leave, Start and Delete a game. 

For the backend, we followed the pre-existing architecture, though removed the HAL wrapper classed and used ordinary json deserialization via spring.
For the frontend, we have chosen to implement the Model-View-Controller (MVC) architectural pattern. Models include the core game structure; controller the business logic and API interactions; and view handles UI. If the project grew in size, you should separate the API calls into a separate /api/ folder.

## Features

As for features, we have successfully added all necessary features, plus a few of our own flurishes, including:
- Sign Up / Sign In / Sign Out
- Launching games, e.g. Create, Join, Leave, Delete, Start.
- Game state functionality (ACTIVE, SIGNUP etc)
- Show Online Games (abbr. SOG)
- Logs of dialogs for signing up, signing in, leaving game etc.

## Known Limitations

As for shortcomings, one should probably do better error handling. Also, one could do proper syncing; we have a manual sleep(200ms) after starting a game. Furthermore, we haven't added functionality for actually starting a RoboRally game, but that is outside the scope of this course.
