using System;
using System.Collections;
using System.IO;
using NanoContainer.IntegrationKit;
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
		public static readonly string XML = ".xml";

		private ScriptedContainerBuilder containerBuilder;

		private static readonly Hashtable extensionToBuilders = new Hashtable();

		static DefaultNanoContainer()
		{
			extensionToBuilders.Add(CS, typeof(NanoContainer.Script.CSharp.CSharpBuilder).FullName);
			extensionToBuilders.Add(VB, typeof(NanoContainer.Script.VB.VBBuilder).FullName);
			extensionToBuilders.Add(JS, typeof(NanoContainer.Script.JS.JSBuilder).FullName);
			extensionToBuilders.Add(JAVA, typeof(NanoContainer.Script.JSharp.JSharpBuilder).FullName);
			extensionToBuilders.Add(XML, typeof(NanoContainer.Script.Xml.XMLContainerBuilder).FullName);
		}

		public DefaultNanoContainer(FileStream composition) 
			: this(new StreamReader(composition), GetBuilderClassName(composition))
		{
		}

		public DefaultNanoContainer(Stream composition, String builderClass) 
			: this(new StreamReader(composition), GetBuilderClassName(builderClass))
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

		public ContainerBuilder ContainerBuilder
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