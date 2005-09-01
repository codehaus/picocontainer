namespace NanoContainer.Attributes
{
	public class AttributeBasedContainerBuilderFacade : ContainerBuilderFacade
	{
		public AttributeBasedContainerBuilderFacade() 
			: base(new AttributeBasedContainerBuilder())
		{
		}
	}
}
