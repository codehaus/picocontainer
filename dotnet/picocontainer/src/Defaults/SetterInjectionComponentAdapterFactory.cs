using System;

namespace PicoContainer.Defaults
{
	/// <summary>
	/// Summary description for SetterInjectionComponentAdapterFactory.
	/// </summary>
	public class SetterInjectionComponentAdapterFactory : DecoratingComponentAdapterFactory 
	{
    /// <summary>
    /// Constructs a SetterInjectionComponentAdapterFactory.
    /// </summary>
    /// <param name="theDelegate">The delegated <see cref="IComponentAdapterFactory"/></param>
    public SetterInjectionComponentAdapterFactory(IComponentAdapterFactory theDelegate): base(theDelegate) {
    }

    public override IComponentAdapter CreateComponentAdapter(object componentKey,
                                                   Type componentImplementation,
                                                   IParameter[] parameters) {
        return new SetterInjectionComponentAdapter(base.CreateComponentAdapter(componentKey, componentImplementation, new IParameter[]{}));
    }
	}
}
