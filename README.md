# SYSC 3303 W20 Project

Group 7

- Ralton Vaz        Student ID: 1010*****
- Austin Do         Student ID: 1010*****
- Kevin Guy         Student ID: 1009*****
- Victor Olaitan    Student ID: 1010*****
- Zeen Luo          Student ID: 1010*****

## Iteration 1

### Files

- TesterClass: A class that simulate the inputs

- FloorSubsystem: A subsystem that handles the input from the floor

- Task: A class that contains all the information about the task

- Main Scheduler: A class that contains get and put method for floor and elevator

- Scheduler: An interface that contains the get and put method

- SchedulerElevator: A class that contains get and put method for the elevator

- SchedulerFloors: A class that contains get and put method for Floors

- Elevator: A elevator class that contains the task queue and the power of the elevator

- ElevatorState: A class that contains an elevator state

- ElevatorMotion: A class that simulates the motion when an elevator has tasks

- TaskGetter: A class that get all the tasks from scheduler assigned to the elevator

- Doors: A class that simulate Open doors and close doors

### Set-Up Instruction

1. Unzip the file and open eclipse

2. import project by selecting the General>Existing Projects into Workspace and select root directory(file location) and the project below

### Test Instruction

1. After imported the project, open TesterClass.java and run it as java application

### Responsibility Breakdown
Each team member was responsible for respective test cases and commenting of each class.

ElevatorSubsystem - Zeen, Victor
  Elevator.java
  TesterClass.java

Scheduler - Kevin, Ralton
  MainScheduler.java
  Scheduler.java
  SchedulerElevator.java
  SchedulerFloors.java
  
FloorSubsystem - Austin
  FloorSubsystem.java
  Task.java

README - Zeen
UML - Everyone
