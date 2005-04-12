using System;
using System.Collections;
using System.IO;
using NanoContainer.Reflection;
using NanoContainer.Script;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer
{
	public class DefaultNanoContainer : INanoContainer
	{
		public static readonly string CS = ".cs";
		public static readonly string VB = ".vb";
		public static readonly string JS = ".js";
		public static readonly string JAVA = ".java";

		private ScriptedContainerBuilder containerBuilder;

		private static readonly Hashtable extensionToBuilders = new Hashtable();

		static DefaultNanoContainer()
		{
			extensionToBuilders.Add(CS, "NanoContainer.Script.CSharp.CSharpBuilder");
			extensionToBuilders.Add(VB, "NanoContainer.Script.VB.VBBuilder");
			extensionToBuilders.Add(JS, "NanoContainer.Script.JS.JSBuilder");
			extensionToBuilders.Add(JAVA, "NanoContainer.Script.JSharp.JSharpBuilder");
		}

		public DefaultNanoContainer(FileStream composition) : this(new StreamReader(composition), GetBuilderClassName(composition))
		{
		}

		public DefaultNanoContainer(Stream composition, String builderClass) : this(new StreamReader(composition), GetBuilderClassName(builderClass))
		{
		}

		public DefaultNanoContainer(StreamReader composition, String builderClass)
		{
			DefaultReflectionContainerAdapter defaultReflectionContainerAdapter;
			DefaultPicoContainer dpc = new DefaultPicoContainer();
			dpc.RegisterComponentInstance(composition);

			defaultReflectionContainerAdapter = new DefaultReflectionContainerAdapter(dpc);
			IComponentAdapter componentAdapter = defaultReflectionContainerAdapter.RegisterComponentImplementation(builderClass);
			containerBuilder = (ScriptedContainerBuilder) componentAdapter.GetComponentInstance(dpc);
		}

		public ScriptedContainerBuilder ContainerBuilder
		{
			get { return containerBuilder; }
		}

		#region static helpers

		private static string GetBuilderClassName(FileStream compositionFile)
		{
			string language = GetExtension(compositionFile.Name);
			return GetBuilderClassName(language);
		}

		public static String GetBuilderClassName(String extension)
		{
			return (String) extensionToBuilders[extension];
		}

		private static string GetExtension(string file)
		{
			return file.Substring(file.LastIndexOf("."));
		}

		#endregion
	}
}