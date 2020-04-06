package elevator;

import java.io.IOException;

import scheduler.ElevatorMessage;
import scheduler.FloorMessage;
import util.Communication.Selector;
import util.Printer;

class TaskGetter implements Runnable {

	private final ElevatorSubsystem subsystem;

	TaskGetter(ElevatorSubsystem elevator) {
		this.subsystem = elevator;
	}

	@Override
	public void run() {

		while (subsystem.poweredOn) {

			// wait for a request (that will benefit us)
			// if a request does not benefit us (e.g. assigned to a different elevator)
			// the communication object will wait() till there exists a request that
			// benefits us
			FloorMessage request;
			try {
				request = subsystem.scheduler.get(new Selector() {
					@Override
					public boolean equals(Object obj) {
						FloorMessage request = (FloorMessage) obj;
						// request must either be directed to this elevator or must require our response
						return request.selectedElevator == subsystem.elevatorNumber
								|| request.responses[subsystem.elevatorNumber - 1] == null;
					}
				});
			} catch (IOException e1) {
				e1.printStackTrace();
				continue;
			}

			if (ElevatorSubsystem.verbose) {
				Printer.print("ELEVATOR SUBSYSTEM: Processing request from Scheduler\n");
			}

			// deconstruct the floor request
			int floor = request.getRequest()[0];
			int direction = request.getRequest()[1];

			// retrieve eta for this elevator to respond to the request
			float eta = subsystem.elevator.timeToStopAtFloor(floor, direction);

			ElevatorMessage response = new ElevatorMessage() {
				@Override
				public FloorMessage getFloorRequest() {
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
					try {
						subsystem.scheduler.put(response);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(0);
						return;
					}
				} else {
					// the request has been processed by the scheduler and the scheduler assigned
					// the task to us
					subsystem.assignTask(floor);
				}
			} else {
				// re-queue the request until an elevator can handle it
				// add a delay to prevent spamming console
				request.responses[subsystem.elevatorNumber - 1] = eta;
				request.numResponses++;

				subsystem.scheduler.delay(response);
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
}