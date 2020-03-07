package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import main.InputParser;
import main.Task;

class InputParserTest {

	@Test
	void test() {
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
