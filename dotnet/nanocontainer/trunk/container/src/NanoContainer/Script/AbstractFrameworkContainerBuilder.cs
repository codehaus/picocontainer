using System;
using System.Collections;
using System.IO;
using System.Reflection;
using Castle.DynamicProxy;
using NanoContainer.IntegrationKit;
using PicoContainer;

namespace NanoContainer.Script
{
	public abstract class AbstractFrameworkContainerBuilder : ScriptedContainerBuilder
	{
		private FrameworkCompiler frameworkCompiler = new FrameworkCompiler();

		public AbstractFrameworkContainerBuilder(StreamReader script) : base(script)
		{
		}

		protected FrameworkCompiler FrameworkCompiler
		{
			get { return frameworkCompiler; }
		}

		protected virtual Assembly GetCompiledAssembly(StreamReader scriptCode, IList assemblies)
		{
			return FrameworkCompiler.Compile(CodeDomProvider, scriptCode, assemblies);
		}

		protected override IMutablePicoContainer CreateContainerFromScript(IPicoContainer parentContainer,
		                                                                   IList assemblies)
		{
			IScript script = CreateScript(assemblies);
			script.Parent = parentContainer;
			return script.Compose();
		}

		private Type getCompiledType(Assembly assembly)
		{
			Type[] types = assembly.GetTypes();

			if(types.Length == 1) 
			{
				return types[0];
			}
			else
			{
				// JScript creates 2 classes in the assembly ... {JScript 0, the script class} 
				return types[1];
			}
		}

		protected IScript CreateScript(IList assemblies) 
		{
			try
			{
				Assembly dynamicAssembly = GetCompiledAssembly(StreamReader, assemblies);
				Type compiledType = getCompiledType(dynamicAssembly);
				object instance = dynamicAssembly.CreateInstance(compiledType.FullName);
				
				ProxyGenerator proxyGenerator = new ProxyGenerator();
				return (IScript)proxyGenerator.CreateProxy(typeof(IScript), new ScriptInterceptor(), instance);
			}
			catch(ArgumentException e)
			{
				throw new PicoCompositionException("The script is not usable. Please specify a correct script.", e);
			}
		}

		internal class ScriptInterceptor : IInterceptor
		{
			public object Intercept(IInvocation invocation, params object[] args)
			{
				return invocation.Proceed(args);
			}
		}
	}
}