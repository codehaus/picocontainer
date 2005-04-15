using System;
using System.Text;
using PicoContainer;

public class TestComp2 : IStartable 
{
	public TestComp2(TestComp tc, StringBuilder sb) 
	{
		sb.Append("-TestComp2");
		if (tc == null) 
		{
			throw new NullReferenceException();
		}
	}

	public void Start() {}
	public void Stop() {}
}
