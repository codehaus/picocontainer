using System.Collections;
using PicoContainer.Core.Defaults;
using PicoContainer.Core;

namespace Test.TestModel
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	public class A
	{
		private B b;
		private string theString;
		private IList list;


		public B B
		{
			get { return b; }
			set { b = value; }
		}

		public string TheString
		{
			get { return theString; }
			set { theString = value; }
		}


		public IList List
		{
			get { return list; }
			set { list = value; }
		}
	}

	public class B
	{
	}

	public class Webster : IThesaurus, IDictionairy
	{
		public Webster(IList list)
		{
			list.Add("Webster created");
		}


		public static void Main()
		{
			IList l = new ArrayList();
			l.Add(new InstanceComponentAdapter("aa", "ComponentC"));
			l.Add(new InstanceComponentAdapter("aaa", new DefaultPicoContainer()));
			l.Add(new InstanceComponentAdapter("aaa1", "ComponentV"));
			l.Add(new InstanceComponentAdapter("bbb", new DefaultPicoContainer()));
			l.Add(new InstanceComponentAdapter("aa11", "ComponentD"));
			l.Add(new InstanceComponentAdapter("ccc", new DefaultPicoContainer()));
			l.Add(new InstanceComponentAdapter("casa", "asa"));

			l = DefaultPicoContainer.OrderComponentAdaptersWithContainerAdaptersLast(l);
			System.Diagnostics.Debug.Assert(((IComponentAdapter) l[4]).GetComponentInstance(null) is DefaultPicoContainer);
			System.Diagnostics.Debug.Assert(((IComponentAdapter) l[5]).GetComponentInstance(null) is DefaultPicoContainer);
			System.Diagnostics.Debug.Assert(((IComponentAdapter) l[6]).GetComponentInstance(null) is DefaultPicoContainer);
		}
	}
}