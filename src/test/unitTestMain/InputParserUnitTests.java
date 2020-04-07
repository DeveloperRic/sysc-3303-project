package test.unitTestMain;


import static org.junit.Assert.assertTrue;
import org.junit.Test;
import main.InputParser;


public class InputParserUnitTests {

	@Test
	public void test() {
		InputParser parser = new InputParser("\\src\\assets\\Inputs.txt");
		assertTrue(parser.requests.size() == 5);
	}

}
