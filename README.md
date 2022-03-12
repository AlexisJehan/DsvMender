![DSV Mender](logo.png)
# DSV Mender
[![Maven Central](https://img.shields.io/maven-central/v/com.github.alexisjehan/dsv-mender.svg)](https://mvnrepository.com/artifact/com.github.alexisjehan/dsv-mender)
[![Javadoc](https://javadoc.io/badge/com.github.alexisjehan/dsv-mender.svg)](https://javadoc.io/doc/com.github.alexisjehan/dsv-mender)
[![Travis CI](https://img.shields.io/travis/com/AlexisJehan/DsvMender.svg)](https://travis-ci.com/github/AlexisJehan/DsvMender)
[![Codecov](https://img.shields.io/codecov/c/github/AlexisJehan/DsvMender.svg)](https://codecov.io/gh/AlexisJehan/DsvMender)
[![License](https://img.shields.io/github/license/AlexisJehan/DsvMender.svg)](LICENSE.txt)

A Java 11+ library to fix malformed DSV (Delimiter-Separated Values) data automatically.

## Introduction
As many developers you may already had to treat some input data with formats such as _CSV_ or _JSON_. Sometimes that
task could become tricky to achieve because some values are not always formatted how they are supposed to be.
**DSV Mender** is a library that aims to help you in such cases efficiently. Basically it collects some features from
each valid column of the data independently to find the best solution while handling invalid or missing values.

### Constraints and estimations
DSV Mender is working with a concept of constraints and estimations that are associated to specific columns of the data:

* **Constraints** eliminate some candidate possibilities of a malformed row if they do not respect a rule, without
taking into account previous valid values at all.
For example if the third column has to be exactly 5 characters long, then all candidates with a value that does not
will be discarded.

* **Estimations** could be used to collect some features from valid values. When an invalid value need to be fixed then
the closest generated possibility is chosen.
For example if you collect the length of valid values and get 5 characters 95% of the time then a possible fixed-value
that got a length of 5 got more chances to be selected than a candidate of 3 characters.

## Getting started
To include and use DSV Mender, you need to add the following dependency into your _Maven_ _pom.xml_ file:
```xml
<dependency>
	<groupId>com.github.alexisjehan</groupId>
	<artifactId>dsv-mender</artifactId>
	<version>1.0.0</version>
</dependency>
```

Or if you are using _Gradle_:
```xml
dependencies {
	compile "com.github.alexisjehan:dsv-mender:1.0.0"
}
```

Also the Javadoc can be accessed [here](http://www.javadoc.io/doc/com.github.alexisjehan/dsv-mender).

## Examples
Let's illustrate how it works step-by-step, consider the following CSV data:

```csv
Release,Release date,Highlights
Java SE 9,2017-09-21,Initial release
Java SE 9.0.1,2017-10-17,October 2017 security fixes and critical bug fixes
Java SE 9.0.4,2018-01-16,Final release for JDK 9; January 2018 security fixes and critical bug fixes
Java SE 10,2018-03-20,Initial release
Java SE 10.0.1,2018-04-17,Security fixes, 5 bug fixes
Java SE 11,2018-09-25,Initial release
Java SE 11.0.1,2018-10-16,Security & bug fixes
Java SE 11.0.2,2019-01-15,Security & bug fixes
Java SE 12,Initial release
```

As you may see, some lines are not well-formatted. The "Java SE 10.0.1" "Highlights" column contains the delimiter
character, and the "Java SE 12" "Release date" column is missing. Let's see how to use DSV Mender to fix it.

### Building the mender
First you need to create a _Mender_ object based on the input data. That requires to specify the delimiter string as
well as the expected number of columns.

#### Basic configuration
The lazy way, for a first attempt is to build a basic _Mender_, that can be able to mend most of input data:

```java
final var delimiter = ',';
final var length = 3;
final var mender = DsvMender.basic(delimiter, length);
```

#### Advanced configuration
For more accurate results, you can also build a _Mender_ with custom _Constraints_ and _Estimations_. For our example above
we will use the following ones:

```java
final var mender = DsvMender.builder()
		.withDelimiter(',')
		.withLength(3)
		.withConstraint(value -> value.startsWith("Java SE"), 0) // values[0] must start with "Java SE"
		.withConstraint(value -> value.isEmpty() || 10 == value.length(), 1)// values[1] must be empty or have a length of 10
		.build();
```

### Processing the data
Once you got your _Mender_ component built, you are able to process your data line by line. Note that you do not have to
worry of the passed line being valid or not, if it is then the _Mender_ will still fit its _Estimations_ before to
return it.

```java
String row;
while (null != (row = reader.readLine())) {
	printValues(mender.mend(row));
}
```

Finally here is the result we got for our example:

```
"Release", "Release date", "Highlights"
"Java SE 9", "2017-09-21", "Initial release"
"Java SE 9.0.1", "2017-10-17", "October 2017 security fixes and critical bug fixes"
"Java SE 9.0.4", "2018-01-16", "Final release for JDK 9; January 2018 security fixes and critical bug fixes"
"Java SE 10", "2018-03-20", "Initial release"
"Java SE 10.0.1", "2018-04-17", "Security fixes, 5 bug fixes"
"Java SE 11", "2018-09-25", "Initial release"
"Java SE 11.0.1", "2018-10-16", "Security & bug fixes"
"Java SE 11.0.2", "2019-01-15", "Security & bug fixes"
"Java SE 12", "", "Initial release"
```

(You can find the code of that example among others in the "examples" package)

## Maven phases and goals
Compile, test and install the JAR in the local Maven repository:
```
mvn install
```

Run JUnit 5 tests:
```
mvn test
```

Generate the Javadoc API documentation:
```
mvn javadoc:javadoc
```

Update sources license:
```
mvn license:format
```

Generate the Jacoco test coverage report:
```
mvn jacoco:report
```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE.txt) file for details