
public class TestComp2 {

	public TestComp2(TestComp tc, StringBuffer sb) {
        sb.append("-TestComp2");
        System.out.println("--> !!!");
	    if (tc == null) {
			throw new NullPointerException();
		}
	}
}

