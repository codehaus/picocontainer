
namespace PicoContainer.Tests.TestModel
{
	/// <summary>
	/// Summary description for DecoratedTouchable.
	/// </summary>
	public class DecoratedTouchable : Touchable
	{
		private readonly Touchable theDelegate;

		public DecoratedTouchable(Touchable theDelegate)
		{
			this.theDelegate = theDelegate;
		}

		public void touch()
		{
			theDelegate.touch();
		}

	}
}