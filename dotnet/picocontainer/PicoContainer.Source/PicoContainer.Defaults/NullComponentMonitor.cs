using System;
using System.Reflection;

namespace PicoContainer.Core.Defaults
{
	[Serializable]
	public class NullComponentMonitor : IComponentMonitor
	{
		public void Instantiating(ConstructorInfo constructor)
		{
		}

		public void Instantiated(ConstructorInfo constructor, long beforeTime, long duration)
		{
		}

		public void InstantiationFailed(ConstructorInfo constructor, Exception e)
		{
		}

		public void Invoking(MethodInfo method, Object instance)
		{
		}

		public void Invoked(MethodInfo method, Object instance, long duration)
		{
		}

		public void InvocationFailed(MethodInfo method, Object instance, Exception e)
		{
		}
	}
}
