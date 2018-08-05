Imports Microsoft.VisualStudio.TestTools.UnitTesting
Imports vb.example.Example


Namespace test
    <TestClass()>
    Public Class ExampleTest

        <TestInitialize()>
        <TestMethod()>
        Public Sub TestMe()
            Console.WriteLine(reverseProxy)
            Dim originalData = "{""secret"":""41111-1111-11111""}"
            Console.WriteLine(originalData)
            Dim redactData = RedactViaReverseProxy(originalData).Result
            Console.WriteLine(redactData)
            Assert.AreNotEqual(originalData, redactData)
            Dim revealData = RevealViaForwardProxy(redactData).Result
            Console.WriteLine(revealData)
            Assert.AreNotEqual(revealData, redactData)
            Assert.AreEqual(originalData, revealData)
            Console.WriteLine("Test passed")
        End Sub

    End Class
End Namespace

