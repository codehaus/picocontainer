using System;
using System.Runtime.Serialization;
using PicoContainer.Core;

namespace NanoContainer.IntegrationKit
{
	public class PicoCompositionException : PicoException
	{
		public PicoCompositionException()
		{
		}

		public PicoCompositionException(Exception ex) : base(ex)
		{
		}

		public PicoCompositionException(string message) : base(message)
		{
		}

		public PicoCompositionException(string message, Exception ex) : base(message, ex)
		{
		}

		protected PicoCompositionException(SerializationInfo info, StreamingContext context) : base(info, context)
		{
		}
	}
}