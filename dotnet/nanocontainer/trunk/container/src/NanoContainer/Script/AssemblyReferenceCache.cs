using System;
using System.Collections;
using System.Reflection;

namespace NanoContainer.Script
{
	/// <summary>
	/// This class caches assembly name (i.e. TestComp) to the path where that dll resides.  This is
	/// needed so when we can load external assemblies that has references to other assemblies. 
	/// </summary>
	public class AssemblyReferenceCache
	{
		private Hashtable map = new Hashtable();

		public void add(string key, string path)
		{
			key = key.Split('.')[0];
			
			if(!map.Contains(key))
			{
				map.Add(key, path);	
			}
		}

		/// <summary>
		/// This method should subscribe to AppDomain.ResolveEvent.  This prevents Runtime exceptions
		/// from being thrown because external assembly dependencies would not be able to satisfied
		/// otherwise.
		/// </summary>
		public Assembly FindAssembly(object sender, ResolveEventArgs args)
		{
			string name = args.Name.Split(',')[0];
			
			if(map.Contains(name))
			{
				string path = map[name] as string;
				return Assembly.LoadFrom(path);	
			}

			return null; // not cached here
		}
	}
}
