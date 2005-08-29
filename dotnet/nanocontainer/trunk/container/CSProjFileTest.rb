require 'test/unit'
require 'csprojfile'

class CSProjFileTest < Test::Unit::TestCase
	def CSProjFileTest.NanoConfig 
		%q!
		<VisualStudioProject>
		    <CSHARP
			ProjectType = "Local"
			ProductVersion = "7.10.3077"
			SchemaVersion = "2.0"
			ProjectGuid = "{10C07279-0C4B-49AC-8DA5-54062116C2ED}"
		    >
			<Build>
			    <Settings
				ApplicationIcon = ""
				AssemblyKeyContainerName = ""
				AssemblyName = "NanoContainer"
				AssemblyOriginatorKeyFile = ""
				DefaultClientScript = "JScript"
				DefaultHTMLPageLayout = "Grid"
				DefaultTargetSchema = "IE50"
				DelaySign = "false"
				OutputType = "Library"
				PreBuildEvent = ""
				PostBuildEvent = ""
				RootNamespace = "NanoContainer"
				RunPostBuildEvent = "OnBuildSuccess"
				StartupObject = ""
			    >
				<Config
				    Name = "Debug"
				    AllowUnsafeBlocks = "false"
				    BaseAddress = "285212672"
				    CheckForOverflowUnderflow = "false"
				    ConfigurationOverrideFile = ""
				    DefineConstants = "DEBUG;TRACE"
				    DocumentationFile = ""
				    DebugSymbols = "true"
				    FileAlignment = "4096"
				    IncrementalBuild = "false"
				    NoStdLib = "false"
				    NoWarn = ""
				    Optimize = "false"
				    OutputPath = "bin\Debug\"
				    RegisterForComInterop = "false"
				    RemoveIntegerChecks = "false"
				    TreatWarningsAsErrors = "false"
				    WarningLevel = "4"
				/>
				<Config
				    Name = "Release"
				    AllowUnsafeBlocks = "false"
				    BaseAddress = "285212672"
				    CheckForOverflowUnderflow = "false"
				    ConfigurationOverrideFile = ""
				    DefineConstants = "TRACE"
				    DocumentationFile = ""
				    DebugSymbols = "false"
				    FileAlignment = "4096"
				    IncrementalBuild = "false"
				    NoStdLib = "false"
				    NoWarn = ""
				    Optimize = "true"
				    OutputPath = "bin\Release\"
				    RegisterForComInterop = "false"
				    RemoveIntegerChecks = "false"
				    TreatWarningsAsErrors = "false"
				    WarningLevel = "4"
				/>
			    </Settings>
			    <References>
				<Reference
				    Name = "System"
				    AssemblyName = "System"
				    HintPath = "..\..\..\..\..\WINDOWS\Microsoft.NET\Framework\v1.1.4322\System.dll"
				/>
				<Reference
				    Name = "System.Data"
				    AssemblyName = "System.Data"
				    HintPath = "..\..\..\..\..\WINDOWS\Microsoft.NET\Framework\v1.1.4322\System.Data.dll"
				/>
				<Reference
				    Name = "System.XML"
				    AssemblyName = "System.Xml"
				    HintPath = "..\..\..\..\..\WINDOWS\Microsoft.NET\Framework\v1.1.4322\System.XML.dll"
				/>
				<Reference
				    Name = "PicoContainer"
				    AssemblyName = "PicoContainer"
				    HintPath = "..\..\lib\PicoContainer.dll"
				/>
				<Reference
				    Name = "Microsoft.JScript"
				    AssemblyName = "Microsoft.JScript"
				    HintPath = "..\..\..\..\..\WINDOWS\Microsoft.NET\Framework\v1.1.4322\Microsoft.JScript.dll"
				/>
				<Reference
				    Name = "VJSharpCodeProvider"
				    AssemblyName = "VJSharpCodeProvider"
				    HintPath = "..\..\..\..\..\WINDOWS\Microsoft.NET\Framework\v1.1.4322\VJSharpCodeProvider.DLL"
				/>
			    </References>
			</Build>
			<Files>
			    <Include>
				<File
				    RelPath = "AssemblyInfo.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "DefaultNanoContainer.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Attributes\AssemblyUtil.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Attributes\AttributeBasedContainerBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Attributes\ComponentAdapterType.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Attributes\DependencyInjectionType.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Attributes\RegisterWithContainerAttribute.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "IntegrationKit\ContainerBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "IntegrationKit\LifeCycleContainerBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "IntegrationKit\PicoCompositionException.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Script\AbstractFrameworkContainerBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Script\FrameworkCompiler.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Script\ScriptedContainerBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Script\CSharp\CSharpBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Script\JS\JSBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Script\JSharp\JSharpBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Script\VB\VBBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Script\Xml\ComposeMethodBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Script\Xml\Constants.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Script\Xml\ContainerStatementBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "Script\Xml\XmlContainerBuilder.cs"
				    SubType = "Code"
				    BuildAction = "Compile"
				/>
				<File
				    RelPath = "TestScripts\test.js"
				    BuildAction = "EmbeddedResource"
				/>
				<File
				    RelPath = "TestScripts\test.vb"
				    BuildAction = "EmbeddedResource"
				/>
			    </Include>
			</Files>
		    </CSHARP>
		</VisualStudioProject>!
	end

	def testOutputType
		projFile = CSProjFile.new(CSProjFileTest.NanoConfig)
		assert_equal(projFile.output_type, "library")
	end

	def testAssemblyName
		projFile = CSProjFile.new(CSProjFileTest.NanoConfig)
		assert_equal(projFile.assembly_name, "NanoContainer")
	end
	
	def testFiles
		projFile = CSProjFile.new(CSProjFileTest.NanoConfig)
		expectedFiles = %w(AssemblyInfo.cs DefaultNanoContainer.cs Attributes\\AssemblyUtil.cs Attributes\\AttributeBasedContainerBuilder.cs Attributes\\ComponentAdapterType.cs Attributes\\DependencyInjectionType.cs Attributes\\RegisterWithContainerAttribute.cs IntegrationKit\\ContainerBuilder.cs IntegrationKit\\LifeCycleContainerBuilder.cs IntegrationKit\\PicoCompositionException.cs Script\\AbstractFrameworkContainerBuilder.cs Script\\FrameworkCompiler.cs Script\\ScriptedContainerBuilder.cs Script\\CSharp\\CSharpBuilder.cs Script\\JS\\JSBuilder.cs Script\\JSharp\\JSharpBuilder.cs Script\\VB\\VBBuilder.cs Script\\Xml\\ComposeMethodBuilder.cs Script\\Xml\\Constants.cs Script\\Xml\\ContainerStatementBuilder.cs Script\\Xml\\XmlContainerBuilder.cs)
		assert_equal(projFile.files, expectedFiles)
	end
	
	def testReferences
		projFile = CSProjFile.new(CSProjFileTest.NanoConfig)
		expectedRefs = %w(System System.Data System.XML PicoContainer Microsoft.JScript VJSharpCodeProvider)
		assert_equal(projFile.references, expectedRefs)
	end
	
	def testCscOutput
		projFile = CSProjFile.new(CSProjFileTest.NanoConfig)
		expectedCsc = "csc /out:../build/NanoContainer.dll /target:library /lib:../build /r:'System.dll;System.Data.dll;System.XML.dll;PicoContainer.dll;Microsoft.JScript.dll;VJSharpCodeProvider.dll' /res:'TestScripts\\test.js,NanoContainer.TestScripts.test.js' /res:'TestScripts\\test.vb,NanoContainer.TestScripts.test.vb' AssemblyInfo.cs DefaultNanoContainer.cs /recurse:AssemblyUtil.cs /recurse:AttributeBasedContainerBuilder.cs /recurse:ComponentAdapterType.cs /recurse:DependencyInjectionType.cs /recurse:RegisterWithContainerAttribute.cs /recurse:ContainerBuilder.cs /recurse:LifeCycleContainerBuilder.cs /recurse:PicoCompositionException.cs /recurse:AbstractFrameworkContainerBuilder.cs /recurse:FrameworkCompiler.cs /recurse:ScriptedContainerBuilder.cs /recurse:CSharpBuilder.cs /recurse:JSBuilder.cs /recurse:JSharpBuilder.cs /recurse:VBBuilder.cs /recurse:ComposeMethodBuilder.cs /recurse:Constants.cs /recurse:ContainerStatementBuilder.cs /recurse:XmlContainerBuilder.cs"
		assert_equal(projFile.create_csc("../build"), expectedCsc)
	end
	
	def testEmbeddedResource
		projFile = CSProjFile.new(CSProjFileTest.NanoConfig)
		expectedResources = %w(TestScripts\test.js TestScripts\test.vb)
		assert_equal(projFile.embedded_resources, expectedResources)
	end
	
	def testConvertToResource
		projFile = CSProjFile.new(CSProjFileTest.NanoConfig)
		assert_equal(projFile.convert_to_resource("a\\b.c"),"/res:'a\\b.c,NanoContainer.a.b.c'")
	end
end