
namespace PicoContainer.Tests.TestModel
{
	/// <summary>
	/// Summary description for DecoratedTouchable.
	/// </summary>
	public class DecoratedTouchable : ITouchable
	{
		private readonly ITouchable theDelegate;

		public DecoratedTouchable(ITouchable theDelegate)
		{
			this.theDelegate = theDelegate;
		}

		public bool WasTouched
		{
			get { return theDelegate.WasTouched; }
		}

		public void Touch()
		{
			theDelegate.Touch();
		}

	}
}