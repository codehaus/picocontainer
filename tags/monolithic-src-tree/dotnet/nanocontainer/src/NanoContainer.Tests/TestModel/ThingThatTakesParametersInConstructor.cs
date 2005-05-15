namespace NanoContainer.Test.TestModel
{
	public class ThingThatTakesParamsInConstructor
	{
		private string theValue;
		private int theInt;
		private bool theBool;

		public ThingThatTakesParamsInConstructor(string theValue, int theInt, bool theBool)
		{
			this.theValue = theValue;
			this.theInt = theInt;
			this.theBool = theBool;
		}

		public int Int
		{
			get { return theInt; }
		}

		public bool Bool
		{
			get { return theBool; }
		}

		public string Value
		{
			get { return theValue + theInt + theBool; }
		}
	}
}