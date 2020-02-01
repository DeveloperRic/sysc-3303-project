package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ElevatorTest {

	@Test
	void test() {
		System.out.println("\nAssigning a task");
		state.assignTask(new Task(new Date(), 5, 17));
		System.out.println("\nAssigning a task");
		state.assignTask(new Task(new Date(), 1, 3));
		System.out.println("\nAssigning a task");
		state.assignTask(new Task(new Date(), 4, 20));
		System.out.println("\nAssigning a task");
		state.assignTask(new Task(new Date(), 13, 3));
		System.out.println("\nAssigning a task");
		state.assignTask(new Task(new Date(), 0, 1));
	}

}
