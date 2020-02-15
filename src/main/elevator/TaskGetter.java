package main.elevator;

import main.Task;

class TaskGetter implements Runnable {

	private final ElevatorSubsystem elevator;

	TaskGetter(ElevatorSubsystem elevator) {
		this.elevator = elevator;
	}

	@Override
	public void run() {
		
		// FOLLOWING CODE IS BROKEN NOW
		
		
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