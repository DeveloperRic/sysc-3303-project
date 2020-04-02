package test.unitTestMain;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.Assert.assertTrue;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import main.FloorSubsystem;
import main.InputParser;
import scheduler.FloorsScheduler;

public class FloorSubsystemUnitTests {

	@SuppressWarnings("static-access")
	@Test
	public void test() {
		// the floor subsystem is basically just a house for the tasks
		// the only isolated functionality is the parsing

		FloorSubsystem floorSS = new FloorSubsystem(null);
		floorSS.parseAdd("this wont work !");
		assertTrue(floorSS.tasks.size() == 0);

		floorSS.parseAdd("14:05:15.0 1 Down a");
		assertTrue(floorSS.tasks.size() == 0);

		floorSS.parseAdd("14:05:15.0 2 Up 4");
		assertTrue(floorSS.tasks.size() == 1);
	}

	@Test
	public void test2() throws UnknownHostException, SocketException {
		FloorsScheduler fs = new FloorsScheduler(-1);
		FloorSubsystem fss = new FloorSubsystem(fs);
		fss.run();
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
			fss.parseAdd(time + " " + request[1] + " " + request[2]);
			System.out.println(time + " " + request[1] + " " + request[2]);
			System.out.println((System.currentTimeMillis() - strt) + ": " + FloorSubsystem.tasks.size());
		}
	}
}
