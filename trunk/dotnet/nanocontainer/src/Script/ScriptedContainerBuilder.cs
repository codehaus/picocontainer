using System;
using System.IO;
using PicoContainer;
using NanoContainer.IntegrationKit;
using PicoContainer.Defaults;

namespace NanoContainer.Script {
  /// <summary>
  /// Summary description for AbstractScriptedContainerBuilder.
  /// </summary>
  public abstract class ScriptedContainerBuilder : LifeCycleContainerBuilder {
    protected readonly StreamReader stream;

    public ScriptedContainerBuilder (StreamReader reader) {
      this.stream = reader;
    }

    protected override IMutablePicoContainer CreateContainer (IPicoContainer parentContainer, object assemblyScope) {
      try {
        return CreateContainerFromScript (parentContainer, assemblyScope);
      }
      finally {
        try {
          stream.Close ();
        }
        catch (IOException) {
        }
      }
    }

    protected abstract IMutablePicoContainer CreateContainerFromScript (IPicoContainer parentContainer, object assemblyScope);

    protected  override void ComposeContainer (IMutablePicoContainer container, object assemblyScope) {
      // do nothing. assume that this is done in createContainer().
    }
  }
}