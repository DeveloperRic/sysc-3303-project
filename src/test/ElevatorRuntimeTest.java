package test;

import elevator.ElevatorSubsystem;
import scheduler.ElevatorScheduler;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.junit.Test;

public class ElevatorRuntimeTest {

	@Test
	public void test() throws UnknownHostException, SocketException {
		System.out.print("Enter this elevator's number : ");
		Scanner scanner = new Scanner(System.in);
		int elevNum = Integer.parseInt(scanner.nextLine());
		scanner.close();

		System.out.println("elevatorNumber set to " + elevNum + "\n");

		ElevatorSubsystem subsystem = new ElevatorSubsystem(new ElevatorScheduler(elevNum));
		ElevatorSubsystem.setVerbose(false);
		subsystem.powerOn();

//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//				}
//
//				subsystem.pressButton(9);
//
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//				}
//
//				subsystem.pressButton(1);
//			}
//		});

		// wait for elevator to return to sleep mode
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

}
