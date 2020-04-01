Group 7:
Ralton Vaz		Student ID: 101010145
Austin Do		Student ID: 101042056
Kevin Guy		Student ID: 100995912
Victor Olaitan		Student ID: 101088982
Zeen Luo		Student ID: 101076185


Files: 

elevator package(7 classes):

Elevator: A elevator class that contains the task queue and the power of the elevator 

ElevatorButton: A class that simulate the floor buttons

ElevatorDoors: A class that simulate Open doors and close doors

ElevatorLamp: A class that simulate the the lamp of the floor buttons

ElevatorMotor: A class that simulates the motion when an elevator has tasks

ElevatorSubsystem: A class the simulate the elevator subsystem

TaskGetter: A class that get all the tasks from scheduler assigned to the elevator



main package(5 classes):

Floor: A class the simulate the floor lamp

FloorSubsystem: A subsystem that handles the input from the floor

InputParser: A class that take the input file and validate the input formate and input logic

Lamp: A simulation for lamp

Task: A class that contains all the information about the task



schduler package(7 classes):

ElevatorMessage: A class that serialize and deserialize the elevator message

ElevatorScheduler: A class that contains get and put method for the elevator

FloorRequest: A class that serialize and deserialize the floor request

FloorsScheduler: A class that contains get and put method for Floors

Main Scheduler: A class that contains get and put method for floor and elevator

SchedulerState: A state machine for scheduler

SchedulerType: An interface that contains the get and put method



test package(5 classes): 

ElevatorRuntimeTest: A class that run the elevator server

ElevatorTest: JUnit test for elevator

FloorRuntimeTest: A class that run the floor server

SchedulerRuntimeTest: A class that run the scheduler server

SchedulerTest: JUnit test for main scheduler

TesterClass: A class that runs the project



test.unitTestElevator(6 classes):

ElevatorButtonUnitTests: JUnit test for elevator button

ElevatorDoorsUnitTest: JUnit test for elevator doors

ElevatorLampUnitTests: JUnit test for elevator lamp

ElevatorMotorUnitTests: JUnit test for elevator motor

ElevatorSubsystemUnitTests: JUnit test for elevator subsystem

ElevatorUnitTests: JUnit test for elevator



test.unitTestMain(3 classes):

FloorSubsystemUnitTest: JUnit test for floor subsystem

FloorUnitTest: JUnit test for floor floor

InputParserUnitTest: JUnit test for input parser



test.unitTestScheduler(5 classes):

ElevatorMessageUnitTests: JUnit test for elevator message

ElevatorSchedulerUnitTest: JUnit test for elevator scheduler

FloorRequestUnitTests: JUnit test for floor request

FloorSchedulerUnitTest: JUnit test for floor scheduler

SchedulerStateUnitTests: JUnit test for scheduler state



util package(5 classes):

ByteUtils: A class that specifies some methods for bytes conversion

Communication: A class that contains two ways implmentation for scheduler and elevator subsystem

DblEndedPQ: A class that specifies some methods for the double ended queues

Printer: A class that print the time correctly

Transport: A class that allows floor subsystem, schdulers, and elevator subsystems to transport messages



Diagrams:
UML Class Diagram
Sequence Diagram
State Machine Diagram


Set-Up Instruction:

1. Unzip the file and open eclipse

2. import project by selecting the General>Existing Projects into Workspace and select root directory(file location) and the project below


Test Instruction:

After imported the project, do the following steps:
1. Open SchedulerRuntimeTest.java and run it as java application
2. Open ElevatorRuntimeTest.java and run it as java application
3. Open FloorRuntimeTest.java and run it as java application


Responsibility Breakdown:

Each team member was responsible for respective test cases and commenting of each class.

UDP - Victor

Fix Bugs - Zeen

Unit Testings - Austin, Kevin, Ralton

README - Zeen UML - Everyone


Reflection:

Concurrency control between iteration 2 and iteration 3 changed to where instead of having the elevator and floor memory shared between the scheduler, the elevator and scheduler are now separated from the scheduler. While memory was still shared, concurrency control structures such as wait() and notify() could no longer be used in the scheduler. Once the communication was switched over to UDP, those controls are found in the blocking socket functions such as receive(). Rather than the scheduler telling the thread to wait, the socket will block until an acknowledgement, or data, is received. The scheduler still synchronizes methods that involve critical data structures, but it is no longer protecting them against the floor or elevator subsystems, just itself. Now instead of the scheduler notifying the floor or elevator that there is data for them to collect, the scheduler sends the data to the elevator or floor directly through the UDP channels.