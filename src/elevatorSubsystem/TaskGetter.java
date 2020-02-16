package elevatorSubsystem;

import main.Task;

class TaskGetter implements Runnable {

	private final ElevatorSubsystem subsystem;

	TaskGetter(ElevatorSubsystem elevator) {
		this.subsystem = elevator;
	}

	@Override
	public void run() {
		
		while (subsystem.poweredOn) {
			
			Integer[] workDoing = (Integer[]) subsystem.scheduler.get();
			subsystem.updateWorkDoing(workDoing);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		
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