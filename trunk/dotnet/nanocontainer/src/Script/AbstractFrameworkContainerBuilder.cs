using System;
using System.CodeDom.Compiler;
using System.Collections;
using System.IO;
using System.Reflection;
using NanoContainer.IntegrationKit;
using NanoContainer.Script.Compiler;
using PicoContainer.Core;

namespace NanoContainer.Script
{
	public abstract class AbstractFrameworkContainerBuilder : ScriptedContainerBuilder
	{
		private static readonly string PARENT_PROPERTY = "Parent";
		private static readonly string COMPOSE_METHOD = "Compose";

		public AbstractFrameworkContainerBuilder(StreamReader stream) : base(stream)
		{
		}

		protected override IMutablePicoContainer CreateContainerFromScript(IPicoContainer parentContainer, object assemblyScope)
		{
			Type type = GetCompiledType(this.stream, (IList) assemblyScope);

			object creator = Activator.CreateInstance(type);
			if (parentContainer != null)
			{
				PropertyInfo info = GetParentProperty(type);
				if (info == null)
				{
					throw new PicoCompositionException("A parent container is provided but the composition script has no property defined for accepting the parent.\nPlease specify a property called Parent and it should be of the type PicoContainer");
				}
				info.SetValue(creator, parentContainer, new object[] {});
			}

			MethodInfo mi = GetComposeMethod(type);

			return (IMutablePicoContainer) mi.Invoke(creator, new object[] {});
		}

		protected Type GetCompiledType(StreamReader scriptCode, IList assemblies)
		{
			Assembly created = FrameworkCompiler.Compile(CodeDomProvider, scriptCode, assemblies);

			Type[] types = created.GetTypes();
			for (int i = 0; i < types.Length; i++)
			{
				if (TestCompliance(types[i]))
				{
					return types[i];
				}
			}
			throw new PicoCompositionException("The script is not usable. Please specify a correct script.");
		}

		protected abstract CodeDomProvider CodeDomProvider { get; }

		protected static bool TestCompliance(Type type)
		{
			ConstructorInfo i = type.GetConstructor(new Type[] {});
			if (i == null)
			{
				return false;
			}

			MethodInfo mi = GetComposeMethod(type);
			if (mi == null || mi.ReturnType != typeof (IMutablePicoContainer))
			{
				return false;
			}

			return true;
		}


		private static PropertyInfo GetParentProperty(Type type)
		{
			return type.GetProperty(PARENT_PROPERTY);
		}

		private static MethodInfo GetComposeMethod(Type type)
		{
			return type.GetMethod(COMPOSE_METHOD);
		}
	}
}