using System;
using System.Collections;
using NanoContainer.IntegrationKit;

namespace NanoContainer.Reflection
{
	/// <summary>
	/// Summary description for ObjectTypeSettings.
	/// </summary>
  /// <summary>
  /// Base class for all providers settings within the UIP configuration settings in the config file.
  /// </summary>
  public class ObjectTypeSettings 
  {
    #region Declares Variables
    private const string Comma = ",";
    
    private string _name;
    private string _type;
    private string _assembly;
    #endregion

    public ObjectTypeSettings (string name, string type, string assembly)
    {
      this._name = name;
      this._type = type;
      this._assembly = assembly;
    }

    public ObjectTypeSettings(string fullType)     {
      //  fix up type/asm strings
      SplitType( fullType );
      _name = fullType;
    }

    private void SplitType( string fullType ) 
    {
      string[] parts = fullType.Split( Comma.ToCharArray() );

      if( parts.Length == 1 )
        _type = fullType;
      else if (parts.Length == 2 ) 
      {
        _type = parts[0].Trim();
        this._assembly = parts[1].Trim();
      }
      else if ( parts.Length == 5 ) 
      {
        //  set the object type name
        this._type = parts[0].Trim();
				
        //  set the object assembly name
        this._assembly = String.Concat( parts[1].Trim() + Comma,
          parts[2].Trim() + Comma,
          parts[3].Trim() + Comma,
          parts[4].Trim() );
      }
      else
        throw new PicoCompositionException("The type "+fullType+"is not qualified enough to load or has an invalid format.");
    }

    #region Properties
    /// <summary>
    /// Gets the object name
    /// </summary>
    public String Name 
    {
      get{ return _name; }
    }


    /// <summary>
    /// Gets the object full qualified type name
    /// </summary>
    public String Type 
    {
      get{ return _type; }
    }

    /// <summary>
    /// Gets the fully qualified assembly name of the object.
    /// </summary>
    public String Assembly 
    {
      get { return _assembly; }
    }


    #endregion
  }
}
