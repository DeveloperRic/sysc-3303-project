package test.unitTestMain;

import main.FloorSubsystem;
import main.InputParser;
import main.Task;
import scheduler.FloorsScheduler;

import static java.time.temporal.ChronoUnit.MILLIS;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.Test;


public class FloorSubsystemUnitTests {

	@SuppressWarnings("static-access")
	@Test
	public void test() {
	//the floor subsystem is basically just a house for the tasks
	//the only isolated functionality is the parsing
		
	FloorSubsystem floorSS = new FloorSubsystem(null);
	floorSS.parseAdd("this wont work !");
	assertTrue(floorSS.tasks.size() == 0);	

	floorSS.parseAdd("14:05:15.0 1 Down a");
	assertTrue(floorSS.tasks.size() == 0);	
	
	floorSS.parseAdd("14:05:15.0 2 Up 4");
	assertTrue(floorSS.tasks.size() == 1);	
	}
	
	@Test
	public void test2() {
		FloorsScheduler fs = new FloorsScheduler(-1);
		FloorSubsystem fss = new FloorSubsystem(fs);
		fss.run();
		String inputFileDestination = "\\src\\assets\\Inputs.txt";
		InputParser ip = new InputParser(inputFileDestination);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
		long strt = System.currentTimeMillis();
		LocalTime l = null;
		while(ip.requests.size() > 0) {
			String[] request = ip.requests.remove(0).split(" ");
			if(l == null) {
				l = LocalTime.parse(request[0]);
			}
			else {
				try {
					Thread.sleep(MILLIS.between(l, LocalTime.parse(request[0])));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			String time = LocalTime.now(ZoneId.systemDefault()).format(formatter);
			fss.parseAdd(time +" "+ request[1] +" "+ request[2]);
			System.out.println(time +" "+ request[1] +" "+ request[2]);
			System.out.println((System.currentTimeMillis() - strt)+": "+fss.tasks.size());
		}
	}
}
