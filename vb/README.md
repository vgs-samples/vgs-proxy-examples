##Instructions

1. For a VB.NET use of our proxy the cert must be added as a trusted root cert on the server or individual machine you are testing on. Currently, there is no way to pass this cert through HTTPClientHandler or any other way (like other languages). 

2. Additionally, the build will need to be made on a .NET Framework (not .NET core). as .NET Core utilizes only C# and F# for their web APIs.

To test, build from solution in visual studio and run test.
