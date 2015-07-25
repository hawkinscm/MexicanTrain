
package model;

import ai.AIManagerTest;
import unit.UnitTester;

public class MexicanTrainUnitTester extends UnitTester {

	public MexicanTrainUnitTester() {
		
		addUnitTest(new AIManagerTest());
		
		testAll();
	}
	
	public static void main(String args[]) {
		new MexicanTrainUnitTester();
		System.exit(0);
	}
}
