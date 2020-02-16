Group 7:
Ralton Vaz		Student ID: 101010145
Austin Do		Student ID: 101042056
Kevin Guy		Student ID: 100995912
Victor Olaitan		Student ID: 101088982
Zeen Luo		Student ID: 101076185


Files: 

main package(6 classes):

Floor: A class the simulate the floor lamp

FloorSubsystem: A subsystem that handles the input from the floor

InputParser: A class that take the input file and validate the input formate and input logic

Lamp: A simulation for lamp

Task: A class that contains all the information about the task

TesterClass: A class that simulate the inputs



elevatorSubsystem package(7):

Elevator: A elevator class that contains the task queue and the power of the elevator 

ElevatorButton: A class that simulate the floor buttons

ElevatorDoors: A class that simulate Open doors and close doors

ElevatorLamp: A class that simulate the the lamp of the floor buttons

ElevatorMotor: A class that simulates the motion when an elevator has tasks

ElevatorSubsystem: A class the simulate the elevator subsystem

TaskGetter: A class that get all the tasks from scheduler assigned to the elevator



schduler package(6):

Communication: A class that contains two ways implmentation for scheduler and elevator subsystem

ElevatorScheduler: A class that contains get and put method for the elevator

FloorsScheduler: A class that contains get and put method for Floors

Main Scheduler: A class that contains get and put method for floor and elevator

SchedulerState: A state machine for scheduler

SchedulerType: An interface that contains the get and put method




test package(4): 

ElevatorTest: JUnit test for elevator subsystem

FloorSybsystem: JUnit test for floor subsystem

FloorTest: JUnit test for floor

SchedulerTest: JUnit test for scheduler



Set-Up Instruction:

1. Unzip the file and open eclipse

2. import project by selecting the General>Existing Projects into Workspace and select root directory(file location) and the project below


Test Instruction:

1. After imported the project, open TesterClass.java and run it as java application

Responsibility Breakdown:

Each team member was responsible for respective test cases and commenting of each class.

ElevatorSubsystem - Zeen, Victor elevatorSubsystem package

Scheduler - Zeen, Victor, Kevin, Ralton scheduler package

FloorSubsystem - Austin FloorSubsystem.java Task.java

README - Zeen UML - Everyone