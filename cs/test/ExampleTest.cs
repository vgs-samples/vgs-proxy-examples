using System;
using Xunit;
using static example.Example;

namespace test
{
    public class ExampleTest
    {
        [Fact]
        public void TestMe()
        {
            var lorem = new Bogus.DataSets.Lorem();
            
            var originalData = "{\"secret\":\"" + lorem.Sentence(8) + "\"}";
            Console.WriteLine(originalData);

            var redactData = RedactViaReverseProxy(originalData).Result;
            Console.WriteLine(redactData);
            Assert.NotEqual(originalData, redactData);

            var revealData = RevealViaForwardProxy(redactData).Result;
            Console.WriteLine(revealData);
            Assert.NotEqual(revealData, redactData);

            Assert.Equal(originalData, revealData);
            Console.WriteLine("Test passed");
        }
    }
}