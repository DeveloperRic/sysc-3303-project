package elevator;

import java.util.Arrays;

import scheduler.ElevatorMessage;
import scheduler.FloorRequest;
import util.Communication.Selector;

class TaskGetter implements Runnable {

	private final ElevatorSubsystem subsystem;

	TaskGetter(ElevatorSubsystem elevator) {
		this.subsystem = elevator;
	}

	@Override
	public void run() {

		while (subsystem.poweredOn) {

//			System.out.println("entering loop");

			// wait for a request (that will benefit us)
			// if a request does not benefit us (e.g. assigned to a different elevator)
			// the communication object will wait() till there exists a request that
			// benefits us
			FloorRequest request = subsystem.scheduler.get(new Selector() {
				@Override
				public boolean equals(Object obj) {
					FloorRequest request = (FloorRequest) obj;
//					System.out.println("sel " + request.selectedElevator);
					// request must either be directed to this elevator or must require our response
					return request.selectedElevator == subsystem.elevatorNumber
							|| request.responses[subsystem.elevatorNumber - 1] == null;
				}
			});

			if (ElevatorSubsystem.verbose) {
				System.out.println("ELEVATOR SUBSYSTEM: Processing request from Scheduler\n");
			}

			// deconstruct the floor request
			int floor = request.getRequest()[0];
			int direction = request.getRequest()[1];
			
			// retrieve eta for this elevator to respond to the request
			float eta = subsystem.elevator.timeToStopAtFloor(floor, direction);

			ElevatorMessage response = new ElevatorMessage() {
				@Override
				public FloorRequest getFloorRequest() {
					return request;
				}
			};

			// if the elevator can respond to the request
			if (eta >= 0) {

				// if the request is in the eta-collection phase (i.e. needs our response)
				if (request.selectedElevator == -1) {
					request.responses[subsystem.elevatorNumber - 1] = eta;
					request.numResponses++;

					// send the request back to the scheduler for further processing
					subsystem.scheduler.put(response);
				} else {
					// the request has been processed by the scheduler and the scheduler assigned
					// the task to us
					subsystem.assignTask(floor);
				}
			} else {
				// re-queue the request until an elevator can handle it
				// add a delay to prevent spamming console
				subsystem.scheduler.delay(response);
			}

//			if (task != null) {
//				subsystem.workDoing.add(task);
//				
//				if (!subsystem.elevator.isAwake())
//					subsystem.elevator.wakeup();
//				
//				while (subsystem.elevator.isAwake()) {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {}
//				}
//				subsystem.removeTask();
////				System.out.println("[TaskGetter] " + subsystem.workDoing.toString());
//			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

//			System.out.println("slept " + subsystem.poweredOn);
		}

//		System.out.println("exit");

		// FOLLOWING CODE IS BROKEN NOW

//		this.elevator.assignTask(13);

//		int i = 0;
//		while (true) {
//
//			Task o = (Task) this.elevator.SchedulerElevator.get();
//			System.out.println("ELEVATOR SUBSYSYTEM: Elevator received task " + i
//					+ " from Scheduler\n Task Information : " + o.toString() + "\n");
//			this.elevator.state.assignTask(o);
//			System.out.println("ELEVATOR SUBSYSTEM: Elevator sending confirmation message to Scheduler: Task " + i
//					+ " received. Moving...\n");
//			this.elevator.SchedulerElevator.put("Task " + i + " received. Moving...");
//			i++;
//		}
	}

}