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
			
			Integer task = subsystem.getTask();
			
			if (task != null) {
				subsystem.workDoing.add(task);
				
				if (!subsystem.elevator.isAwake())
					subsystem.elevator.wakeup();
				
				while (subsystem.elevator.isAwake()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}
				subsystem.removeTask();
//				System.out.println("[TaskGetter] " + subsystem.workDoing.toString());
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		
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