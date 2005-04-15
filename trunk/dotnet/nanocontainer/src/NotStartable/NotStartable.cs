using System;
using System.Text;

public class NotStartable 
{
	public NotStartable(TestComp tc, StringBuilder sb) 
	{
		sb.Append("-NotStartable");
		if (tc == null) 
		{
			throw new NullReferenceException();
		}
	}

	public void Start() {}
	public void Stop() {}
}
