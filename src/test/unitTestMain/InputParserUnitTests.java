package test.unitTestMain;


import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import main.InputParser;
import main.Task;

public class InputParserUnitTests {

	@Test
	public void test() {
		InputParser parser = new InputParser("\\src\\assets\\Inputs.txt");
		HashMap<String,Task> tasks = parser.getTasks();
		parser.printTasks();
		assertTrue(tasks.size() == 1);
		
		Task task = tasks.get("14:05:15");
		assertTrue(task.getRequestTime().toString().equals("14:05:15"));
		assertTrue(task.getDestinationFloor() == 7);
		assertTrue(task.getStartFloor() == 3);
		assertTrue(task.getDirection() == 1);
	}

}
