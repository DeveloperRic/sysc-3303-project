package test;

import main.FloorSubsystem;
import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;


public class FloorSubsystemTest {

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
}
