# Bingo RMI System

## Overview

This project is a distributed Bingo system implemented using **Java** and **RMI** (Remote Method Invocation). The system consists of three main components:

- **ProjectClient**: The client that connects to the game.
- **ProjectServer**: The server that manages the game and notifies the clients of various events.
- **ProjectWinnerServer**: Manages the list of winners and handles requests from the server to add or display winners.

### Project Structure

- **ProjectClient**: Communicates with `ProjectServer` using the `Operations` interface, which contains all the necessary functions for the Bingo game.
- **ProjectServer**: Notifies clients of the following events using a `Callback` mechanism:
  1. When a random number is generated (number draw).
  2. When a player wins.
  3. When the game is over (all 75 numbers have been drawn).
  
  Additionally, **ProjectServer** communicates with **ProjectWinnerServer** using the `WinnerOperation` interface to add and retrieve winners.
  
- **ProjectWinnerServer**: Stores and returns the list of winners based on requests from **ProjectServer**.

### Features

- **Real-time notifications**: Clients are notified of new numbers and winners via a callback mechanism.
- **Winner management**: The system keeps track of all game winners and can display the list of winners on request.
- **End-game notification**: Clients are notified when all numbers have been drawn, signaling the end of the game.

### How to Run

1. Start the `ProjectWinnerServer` to handle winner-related operations.
2. Start the `ProjectServer` to manage the Bingo game.
3. Start one or more instances of `ProjectClient` to participate in the game.
4. Clients will be notified in real-time of number draws and game results.

### Requirements

- **Java** (JDK 8 or higher)
- **NetBeans** or **Eclipse**

### Setup Instructions

#### Opening the Project in NetBeans:

1. Download and install [NetBeans IDE](https://netbeans.apache.org/).
2. Open NetBeans and click on **File > Open Project**.
3. Navigate to the location of the project folder, select it, and click **Open**.
4. Right-click on each project (**ProjectClient**, **ProjectServer**, **ProjectWinnerServer**) and click **Run** to execute.

#### Opening the Project in Eclipse:

1. Download and install [Eclipse IDE](https://www.eclipse.org/).
2. Open Eclipse and go to **File > Open Projects from File System**.
3. Browse to the location of the project folder, select it, and click **Finish**.
4. Right-click on each project (**ProjectClient**, **ProjectServer**, **ProjectWinnerServer**) and click **Run As > Java Application** to execute.

### License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.
