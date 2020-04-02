package test;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import elevator.ElevatorSubsystem;
import main.FloorSubsystem;
import main.InputParser;
import scheduler.ElevatorScheduler;
import scheduler.FloorsScheduler;
import scheduler.MainScheduler;
import util.Transport;

public class TesterClass {

	public static void main(String[] args) throws SocketException, UnknownHostException {

		// Printer.print("Testing");

		// InputParser inputParser = new InputParser("\\src\\assets\\Inputs.txt");
		// inputParser.printTasks();
		// Printer.print("");
		// inputParser.printTimeRequests();

//		MainScheduler scheduler = new MainScheduler();
//		FloorsScheduler floorScheduler = new FloorsScheduler();
//		ElevatorScheduler elevatorScheduler = new ElevatorScheduler();
//		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorScheduler);
//		
//		FloorSubsystem floorSS = new FloorSubsystem(floorScheduler);
//		
//		new Thread(floorSS,"FloorSS").start();
//		elevatorSubsystem.powerOn();

		// Active Main Scheduler
		MainScheduler mainScheduler = new MainScheduler();
		mainScheduler.setVerbose(true); // code works now, no need for spam
		mainScheduler.activate();

		// Power on elevator subsystem
		ElevatorSubsystem subsystem = new ElevatorSubsystem(new ElevatorScheduler(1));
		ElevatorSubsystem.setVerbose(true);
		subsystem.powerOn();

		// Active floor scheduler
		Transport.setVerbose(true);
		FloorsScheduler floorScheduler = new FloorsScheduler(-1);

		// Delay
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		FloorSubsystem floorSS = new FloorSubsystem(floorScheduler);

		new Thread(floorSS, "FloorSS").start();

		String inputFileDestination = "\\src\\assets\\Inputs.txt";
		InputParser ip = new InputParser(inputFileDestination);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
		long strt = System.currentTimeMillis();
		LocalTime l = null;

		while (ip.requests.size() > 0) {
			String[] request = ip.requests.remove(0).split(" ");
			if (l == null) {
				l = LocalTime.parse(request[0]);
			} else {
				try {
					Thread.sleep(MILLIS.between(l, LocalTime.parse(request[0])));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			String time = LocalTime.now(ZoneId.systemDefault()).format(formatter);
			floorSS.parseAdd(time + " " + request[1] + " " + request[2]);
			System.out.println(time + " " + request[1] + " " + request[2]);
			System.out.println((System.currentTimeMillis() - strt) + ": " + FloorSubsystem.tasks.size());
		}

	}
}
