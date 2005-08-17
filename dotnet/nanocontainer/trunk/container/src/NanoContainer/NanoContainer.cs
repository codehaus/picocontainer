using System;
using System.Collections;
using System.IO;
using NanoContainer.IntegrationKit;
using NanoContainer.Reflection;
using NanoContainer.Script.CSharp;
using NanoContainer.Script.JS;
using NanoContainer.Script.JSharp;
using NanoContainer.Script.VB;
using NanoContainer.Script.Xml;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer
{
	public class NanoContainer : INanoContainer
	{
		public static readonly string CS = ".cs";
		public static readonly string VB = ".vb";
		public static readonly string JS = ".js";
		public static readonly string JAVA = ".java";
		public static readonly string XML = ".xml";

		private ContainerBuilder containerBuilder;

		private static readonly Hashtable extensionToBuilders = new Hashtable();

		static NanoContainer()
		{
			extensionToBuilders.Add(CS, typeof(CSharpBuilder).FullName);
			extensionToBuilders.Add(VB, typeof(VBBuilder).FullName);
			extensionToBuilders.Add(JS, typeof(JSBuilder).FullName);
			extensionToBuilders.Add(JAVA, typeof(JSharpBuilder).FullName);
			extensionToBuilders.Add(XML, typeof(XMLContainerBuilder).FullName);
		}

		public NanoContainer(FileStream composition) 
			: this(new StreamReader(composition), GetBuilderClassName(composition))
		{
		}

		public NanoContainer(Stream composition, string builderClass) 
			: this(new StreamReader(composition), GetBuilderClassName(builderClass))
		{
		}

		public NanoContainer(StreamReader composition, string builderClass)
		{
			DefaultReflectionContainerAdapter defaultReflectionContainerAdapter;
			DefaultPicoContainer dpc = new DefaultPicoContainer();
			dpc.RegisterComponentInstance(composition);

			defaultReflectionContainerAdapter = new DefaultReflectionContainerAdapter(dpc);
			IComponentAdapter componentAdapter = defaultReflectionContainerAdapter.RegisterComponentImplementation(builderClass);
			containerBuilder = (ContainerBuilder) componentAdapter.GetComponentInstance(dpc);
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

		public static string GetBuilderClassName(string extension)
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
