/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * Ported to .NET by Jeremey Stell-Smith                                     *
 *****************************************************************************/



using System;
using System.Collections;
using System.Reflection;

namespace PicoContainer
{
	public class HierarchicalPicoContainer : AbstractContainer, IPicoContainer 
	{
		private readonly IContainer parentContainer;
		private readonly IStartableLifecycleManager startableLifecycleManager;
		private readonly IComponentFactory componentFactory;
		private ArrayList registeredComponents = new ArrayList();
		private Hashtable componentTypeToInstanceMap = new Hashtable();
		// Keeps track of the order in which components should be started
		private ArrayList orderedComponents = new ArrayList();

		private Hashtable parametersForComponent = new Hashtable();
		private bool disposedOf;
		private bool initialized;
		private bool started;

		public HierarchicalPicoContainer(IContainer parentContainer,
			IStartableLifecycleManager startableLifecycleManager,
			IComponentFactory componentFactory) 
		{
			PicoNullReferenceException.AssertNotNull("parentContainer cannot be null", parentContainer);
			PicoNullReferenceException.AssertNotNull("startableLifecycleManager cannot be null", startableLifecycleManager);
			PicoNullReferenceException.AssertNotNull("componentFactory cannot be null", componentFactory);

			this.parentContainer = parentContainer;
			this.startableLifecycleManager = startableLifecycleManager;
			this.componentFactory = componentFactory;
		}

		public class Default : HierarchicalPicoContainer 
		{
			public Default() 
				: base(new NullContainer(), new NullStartableLifecycleManager(), new DefaultComponentFactory()) {}
		}

		public class WithParentContainer : HierarchicalPicoContainer 
		{
			public WithParentContainer(IContainer parentContainer) 
				: base(parentContainer, new NullStartableLifecycleManager(), new DefaultComponentFactory()) {}
		}

		public class WithStartableLifecycleManager : HierarchicalPicoContainer 
		{
			public WithStartableLifecycleManager(IStartableLifecycleManager startableLifecycleManager) 
				: base(new NullContainer(), startableLifecycleManager, new DefaultComponentFactory()) {}
		}

		public class WithComponentFactory : HierarchicalPicoContainer 
		{
			public WithComponentFactory(IComponentFactory componentFactory) 
				: base(new NullContainer(), new NullStartableLifecycleManager(), componentFactory) {}
		}

		public void RegisterComponent(Type componentImplementation) 
		{
			RegisterComponent(componentImplementation, componentImplementation);
		}

		public void RegisterComponent(Type componentType, Type componentImplementation) 
		{
			CheckConcrete(componentImplementation);
			CheckConstructor(componentImplementation);
			CheckTypeCompatibility(componentType, componentImplementation);
			CheckTypeDuplication(componentType);
			registeredComponents.Add(new ComponentSpecification(componentType, componentImplementation));
		}

		private void CheckConstructor(Type componentImplementation) 
		{
			// TODO move this check to checkConstructor and rename the exception to
			// WrongNumberOfConstructorsRegistrationException : PicoRegistrationException
			ConstructorInfo[] constructors = componentImplementation.GetConstructors();
			if (constructors.Length != 1) 
			{
				throw new WrongNumberOfConstructorsRegistrationException(constructors.Length);
			}
		}

		private void CheckTypeDuplication(Type componentType) 
		{
			foreach (ComponentSpecification component in registeredComponents) 
			{
				if (component.ComponentType == componentType) 
				{
					throw new DuplicateComponentTypeRegistrationException(componentType);
				}
			}
		}

		private void CheckTypeCompatibility(Type componentType, Type componentImplementation) 
		{
			if (!componentType.IsAssignableFrom(componentImplementation)) 
			{
				throw new AssignabilityRegistrationException(componentType, componentImplementation);
			}
		}

		private void CheckConcrete(Type componentImplementation) 
		{
			// Assert that the typeof(component) is concrete.
			// todo - stellj - this can't be right
			bool isAbstract = componentImplementation.Attributes == TypeAttributes.Abstract;
			if (componentImplementation.IsInterface || isAbstract) 
			{
				throw new NotConcreteRegistrationException(componentImplementation);
			}
		}

		public void RegisterComponent(object component) 
		{
			RegisterComponent(component.GetType(), component);
			orderedComponents.Add(component);
		}

		public void RegisterComponent(Type componentType, object component) 
		{
			CheckTypeCompatibility(componentType, component.GetType());
			CheckTypeDuplication(componentType);
			//checkImplementationDuplication(component.getClass());
			componentTypeToInstanceMap[componentType] = component;
		}

		public void AddParameterToComponent(Type componentType, Type parameter, object arg) 
		{
			if (!parametersForComponent.ContainsKey(componentType)) 
			{
				parametersForComponent[componentType] = new ArrayList();
			}
			ArrayList args = (ArrayList) parametersForComponent[componentType];
			args.Add(new ParameterSpec(/*parameter,*/ arg));
		}

		// TODO (AH): Shouldn't this be a private class????
		class ParameterSpec 
		{
			//        private Type parameterType;
			public object arg;

			public ParameterSpec(/*Type parameterType,*/ object parameter) 
			{
				//            this.parameterType = parameterType;
				this.arg = parameter;
			}
		}

		public void Start() 
		{
			CheckNotDisposedOf();
			if (initialized == false) 
			{
				InitializeComponents();
				CheckUnsatisfiedDependencies();
				initialized = true;
			}
			if (started == false) 
			{
				StartComponents();
				started = true;
			} 
			else 
			{
				throw new InvalidOperationException("Container Started Already");
			}
		}

		// This is Lazy and NOT public :-)
		private void InitializeComponents() 
		{
			bool progress = true;
			while (progress == true) 
			{
				progress = false;

				foreach(ComponentSpecification componentSpec in registeredComponents) 
				{
					Type componentImplementation = componentSpec.ComponentImplementation;
					Type componentType = componentSpec.ComponentType;

					if (componentTypeToInstanceMap[componentType] == null) 
					{
						bool reused = ReuseImplementationIfAppropriate(componentType, componentImplementation);
						if (reused) 
						{
							progress = true;
						} 
						else 
						{
							// hook'em up
							progress = HookEmUp(componentImplementation, componentType, progress);
						}
					}
				}
			}
		}

		protected bool HookEmUp(Type componentImplementation, Type componentType, bool progress) 
		{
			try 
			{
				ConstructorInfo[] constructors = componentImplementation.GetConstructors();
				ConstructorInfo constructor = constructors[0];
				ParameterInfo[] parameters = constructor.GetParameters();

				ArrayList paramSpecs = (ArrayList) parametersForComponent[componentImplementation];
				paramSpecs = paramSpecs == null ? new ArrayList() : new ArrayList(paramSpecs); // clone because we are going to modify it

				// For each param, look up the instantiated componentImplementation.
				object[] args = new object[parameters.Length];
				for (int i = 0; i < parameters.Length; i++) 
				{
					ParameterInfo param = parameters[i];
					args[i] = GetComponentForParam(param.ParameterType); // lookup a service for this param
					if (args[i] == null && paramSpecs.Count != 0) 
					{ // failing that, check if any params are available from AddParameterToComponent()
						args[i] = ((ParameterSpec) paramSpecs[0]).arg;
						paramSpecs.RemoveAt(0);
					}
				}
				if (HasAnyNullArguments(args) == false) 
				{
					object componentInstance = null;
					componentInstance = MakeComponentInstance(componentImplementation, constructor, args);
					// Put the instantiated comp back in the map
					componentTypeToInstanceMap[componentType] = componentInstance;
					orderedComponents.Add(componentInstance);
					progress = true;
				}

			} 
			catch (TargetInvocationException e) 
			{
				throw new PicoInvocationTargetStartException(e.InnerException);
			} 
			return progress;
		}

		protected bool ReuseImplementationIfAppropriate(Type componentType, Type componentImplementation) 
		{
			foreach(DictionaryEntry entry in componentTypeToInstanceMap)
			{
				object exisitingCompClass = entry.Value;
				if (exisitingCompClass.GetType() == componentImplementation) 
				{
					componentTypeToInstanceMap[componentType] = exisitingCompClass;
					return true;
				}
			}
			return false;
		}

		public void Stop() 
		{
			CheckNotDisposedOf();
			if (started == true) 
			{
				StopComponents();
				started = false;
			} 
			else 
			{
				throw new InvalidOperationException("Container Not started");
			}
		}

		private void CheckNotDisposedOf() 
		{
			if (disposedOf == true) 
			{
				throw new InvalidOperationException("Container Disposed Of");
			}
		}

		public void Dispose() 
		{
			CheckNotDisposedOf();
			DisposeOfComponents();
		}

		protected void StartComponents() 
		{
			foreach (object component in orderedComponents) 
			{
				startableLifecycleManager.StartComponent(component);
			}
		}

		protected void StopComponents() 
		{
			for (int i = orderedComponents.Count - 1; i >= 0; i--) 
			{
				startableLifecycleManager.StopComponent(orderedComponents[i]);
			}
		}

		protected void DisposeOfComponents() 
		{
			foreach (object component in orderedComponents) 
			{
				startableLifecycleManager.DisposeOfComponent(component);
			}
			disposedOf = true;
		}


		private void CheckUnsatisfiedDependencies() 
		{
			foreach(ComponentSpecification componentSpecification in registeredComponents) 
			{
				Type componentType = componentSpecification.ComponentType;
				if (componentTypeToInstanceMap[componentType] == null) 
				{
					throw new UnsatisfiedDependencyStartupException(componentType);
				}
			}
		}

		protected virtual object MakeComponentInstance(Type type, ConstructorInfo constructor, object[] args)
		{
			return componentFactory.CreateComponent(type, constructor, args);
		}

		private object GetComponentForParam(Type parameter) 
		{
			object result = null;

			// If the parent container has the component type
			// it can be seen to be dominant. No need to check
			// for ambiguities
			if (parentContainer.HasComponent(parameter)) 
			{
				return parentContainer.GetComponent(parameter);
			}

			// We're keeping track of all candidate parameters, so we can bomb with a detailed error message
			// if there is ambiguity
			ArrayList candidateClasses = new ArrayList();

			foreach (DictionaryEntry entry in componentTypeToInstanceMap) 
			{
				Type type = (Type) entry.Key;
				if (parameter.IsAssignableFrom(type)) 
				{
					candidateClasses.Add(type);
					result = entry.Value;
				}
			}

			// We should only have one here.
			if (candidateClasses.Count > 1) 
			{
				Type[] ambiguities = (Type[]) candidateClasses.ToArray(typeof(Type));
				throw new AmbiguousComponentResolutionException(ambiguities);
			}

			return result;
		}

		private bool HasAnyNullArguments(object[] args) 
		{
			for (int i = 0; i < args.Length; i++) 
			{
				if (args[i] == null) 
				{
					return true;
				}
			}
			return false;
		}

		public override object GetComponent(Type componentType) 
		{
			object result = componentTypeToInstanceMap[componentType];
			if (result == null) 
			{
				result = parentContainer.GetComponent(componentType);
			}
			return result;
		}

		public override Type[] ComponentTypes
		{
			get 
			{
				// Get my own
				ArrayList types = new ArrayList(componentTypeToInstanceMap.Keys);

				// Get those from my parent.
				foreach (Type type in parentContainer.ComponentTypes) 
				{
					types.Add(type);
				}

				return (Type[]) types.ToArray(typeof(Type));
			}
		}

		public override bool HasComponent(Type componentType) 
		{
			return GetComponent(componentType) != null;
		}

	}
}