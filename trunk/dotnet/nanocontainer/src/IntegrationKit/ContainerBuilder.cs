using System;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer.IntegrationKit
{
  /// <summary>
  /// Summary description for ContainerBuilder.
  /// </summary>
  public interface ContainerBuilder
  {
    /// <summary>
    /// Create, assemble, init and start a new PicoContainer and store it at a given reference.
    /// </summary>
    /// <param name="parentContainerRef">reference to a container that may be used as a parent to the new container (may be null).</param>
    /// <param name="compositionScope">Hint about the scope for composition.</param>
    /// <returns></returns>
    void BuildContainer (IObjectReference contrainerRef, IObjectReference parentContainerRef, object compositionScope);

    /**
     * Locate a container at the given reference so it can be stopped,
     * destroyed and removed.
     *
     * @param containerRef Where the container is stored.
     */

   /// <summary>
   /// Locate a container at the given reference so it can be stopped, destroyed and removed.
   /// </summary>
   /// <param name="container"></param>
    void KillContainer (IObjectReference container);
  }
}