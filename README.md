aludratest-hpalm-connector
==========================

This module allows to write test execution results to HP ALM. A link to the HP ALM tests and configurations is 
established via IDs, which are derived from annotations and / or test names. See below for customization of this
mechanism.

## Usage

To use the Connector in your AludraTest run, configure it as additional dependency to the maven-surefire-plugin, just
as you did with the aludratest-surefire-provider:

```
<plugin>
	<artifactId>maven-surefire-plugin</artifactId>
	<version>2.17</version>
	<dependencies>
		<!-- inject aludratest provider -->
		<dependency>
			<groupId>org.aludratest.maven</groupId>
			<artifactId>aludratest-surefire-provider</artifactId>
			<version>${aludratest.surefire.version}</version>
		</dependency>
		<!-- inject HP ALM connector -->
		<dependency>
			<groupId>org.aludratest</groupId>
			<artifactId>aludratest-hpalm-connector</artifactId>
			<version>${aludratest.hpalm.version}</version>
		</dependency>
	</dependencies>
	...
</plugin>
```

## Configuration

Configure the HP ALM connector by adding a `hpalm.properties` to your configuration directory. This could e.g. be
`src/main/resources/config`, if you do not want to use extra configuration for different environments (see 
[AludraTest Configuration](http://aludratest.github.io/aludratest/service-configuration.html) for details).
A typical `hpalm.properties` should look like this:

```
hpalmUrl=https://hpalm.appserver.int/qcbin
userName=myautomationuser
password=mytopsecretpwd
domain=DEFAULT
project=My_Software
testSetFolderPath=Root/Automation/UITests
testSetName=Nightly UI Tests
```

### Command-line overrides

It will be a common task to set the test set name per test execution. For this purpose, you can also specify the test
set name e.g. via Maven command line:

```
mvn test -DALUDRATEST_CONFIG/hpalm/testSetName=Tests\ 2015-04-01
```

(obviously, you would script this to use the current date).

### Disabling of HP ALM Connector

To disable the HP ALM connector for a test execution, set the `enabled` configuration flag to `false`, e.g. via command
line:

```
mvn test -DALUDRATEST_CONFIG/hpalm/enabled=false
``` 

## Customization of ID lookup

The aludratest-hpalm-connector ships with a default Resolver of HP ALM IDs. This resolver works as follows:

* If the test class is annotated with the `@HpAlmTestId` annotation, its value is used as HP ALM Test ID.
* Otherwise, the name of the test group (above the "configuration" level) is checked for a pattern like `ID_1234`,
and the number is used as the Test ID.
* For the Test Configuration ID, the test case name itself (with is constructed from the configuration name in 
AludraTest test data) is searched for a pattern like `CID_3456`, and the number is used as the Test Configuration ID.
* If no Test ID could be determined, the test execution result is not written to HP ALM.

To register a custom Resolver, create a class implementing the `TestCaseIdResolver` interface, and register it via your
`aludraservice.properties`:

```
org.aludratest.hpalm.TestCaseIdResolver=com.myacme.mytestproject.MyTestCaseIdResolver
```
