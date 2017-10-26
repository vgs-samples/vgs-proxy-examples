Imports System
Imports System.Collections.Generic
Imports System.Net.Http
Imports System.Text
Imports System.Web.Http
Imports Microsoft.VisualStudio.TestTools.UnitTesting
Imports vb

<TestClass()> Public Class ValuesControllerTest
    <TestMethod()> Public Sub GetValues()
        'Arrange
        Dim controller As New ValuesController()

        'Act
        Dim result As IEnumerable(Of String) = controller.GetValues()

        'Assert
        Assert.IsNotNull(result)
        Assert.AreEqual(2, result.Count())
        Assert.AreEqual("value1", result.ElementAt(0))
        Assert.AreEqual("value2", result.ElementAt(1))
    End Sub

    <TestMethod()> Public Sub GetValueById()
        'Arrange
        Dim controller As New ValuesController()

        'Act
        Dim result As String = controller.GetValue(5)

        'Assert
        Assert.AreEqual("value", result)
    End Sub

    <TestMethod()> Public Sub PostValue()
        'Arrange
        Dim controller As New ValuesController()

        'Act
        controller.PostValue("value")

        'Assert
    End Sub

    <TestMethod()> Public Sub PutValue()
        'Arrange
        Dim controller As New ValuesController()

        'Act
        controller.PutValue(5, "value")

        'Assert
    End Sub

    <TestMethod()> Public Sub DeleteValue()
        'Arrange
        Dim controller As New ValuesController()

        'Act
        controller.DeleteValue(5)

        'Assert
    End Sub
End Class
