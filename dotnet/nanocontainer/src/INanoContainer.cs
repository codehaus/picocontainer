using System;
using NanoContainer.Script;

namespace NanoContainer
{
  public interface INanoContainer {
    ScriptedContainerBuilder ContainerBuilder  {
      get;
    }
  }
}
