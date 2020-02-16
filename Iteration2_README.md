# SYSC 3303 W20 Project

Group 7

- Ralton Vaz        Student ID: 1010*****
- Austin Do         Student ID: 1010*****
- Kevin Guy         Student ID: 1009*****
- Victor Olaitan    Student ID: 1010*****
- Zeen Luo          Student ID: 1010*****

## Iteration 1

### Files

ASSETS
- Input: txt file of input strings

MAIN
- TesterClass: A class that simulate the inputs
- Task: A class that contains all the information about the task
- FloorSubsystem: A subsystem that handles the input for the floors
- InputParser: Pares inputs for Elevator and Floor subsystems
- Lamp: Simulates a floor lamp
- Floor: Simulates a floor

ELEVATORSUBSYSTEM
- Elevator: A elevator class that contains the task queue and the power of the elevator
- ElevatorButton: Simulates an elevator button
- ElevatorDoors: Simulates elevator doors
- ElevatorLamp: Simulates an elevator lamp
- ElevatorMotor: Controls elevator movement logic
- ElevatorSubsystem:A subsystem that handles the input for the elevators
- TaskGetter: A class that get all the tasks from scheduler assigned to the elevator

SCHEDULER
- Communication: Object used to pass messages between classes
- ElevatorScheduler: A class that contains get and put method for the elevator
- FloorsScheduler: A class that contains get and put method for Floors
- MainScheduler: A class that contains get and put method for floor and elevator
- SchedulerState: States for the scheduler state machine
- SchedulerType: An interface that contains the get and put method

### Set-Up Instruction

1. Unzip the file and open eclipse

2. Import project by selecting the General>Existing Projects into Workspace and select root directory(file location) and the project below

### Test Instruction

1. After project is imported, open TesterClass.java and run it as java application

### Responsibility Breakdown
Elevator Classes: Zeen, Victor
Scheduler Classes: Ralton, Kevin
Floor Classes: Austin
