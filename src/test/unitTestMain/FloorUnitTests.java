package test.unitTestMain;

import main.Floor;
import static org.junit.Assert.*;

import org.junit.Test;


public class FloorUnitTests {

//just checks for state switching functionality

/*	@Test
	public void test() {
	Floor floor = new Floor(0);
	
	//NO REQUESTED STATE
	assertTrue(floor.state == floor.noRequest);
	floor.requestUp();
	assertTrue(floor.state == floor.upRequested);
	floor.requestServed(1);
	assertTrue(floor.state == floor.noRequest);
	
	//UP REQUESTED STATE
	floor.requestUp();
	floor.requestUp();
	assertTrue(floor.state == floor.upRequested);
	floor.requestDown();
	assertTrue(floor.state != floor.upRequested);
	floor.requestServed(-1);
	floor.requestServed(1);
	assertTrue(floor.state == floor.noRequest);

	//BOTH REQUESTED STATE
	floor.requestDown();
	assertTrue(floor.state == floor.downRequested);
	floor.requestUp();
	assertTrue(floor.state == floor.bothRequested);
	floor.requestServed(-1);
	floor.requestServed(1);
	assertTrue(floor.state != floor.bothRequested);
	
	//DOWN REQUESTED
	floor.requestDown();
	assertTrue(floor.state == floor.downRequested);
	floor.requestServed(-1);
	assertTrue(floor.state == floor.noRequest);
	floor.requestUp();
	floor.requestDown();
	assertTrue(floor.state == floor.bothRequested);
	}*/
}
