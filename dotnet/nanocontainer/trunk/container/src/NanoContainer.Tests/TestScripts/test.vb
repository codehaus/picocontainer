Imports PicoContainer
Imports PicoContainer.Defaults

Public Class Test

    Dim p As IPicoContainer

    Public WriteOnly Property Parent() As IPicoContainer
        Set
            p = Value
        End Set
        
    End Property

    Public Function Compose() As IMutablePicoContainer
        Dim c as new DefaultPicoContainer(p)
        c.RegisterComponentInstance("hello","VB")
        Return c
    End Function

End Class


