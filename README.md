# DSV Mender

A Java 8 library to fix malformed Delimiter-separated values (DSV) data automatically.

## Introduction

As many developers you may already had to treat some input data with formats such as _CSV_ or _JSON_. Sometimes that
task could become tricky to achieve because some values are not formatted how they are supposed to be. DSV Mender is a
library that aims to help you in such cases efficiently. Basically it collects some features from each valid column of
the data independently to find the best solution while handling invalid or missing values.

### Estimations and Constraints

DSV Mender is working with concepts of estimations and constraints that are assigned to desired columns:

* **Estimations** could be used to collect some features from valid values. When an invalid value need to be fixed then
the closest generated possibility is chosen.
For example if you collect the length of valid values and get 5 characters 95% of the time then a possible fixed-value
that got a length of 5 got more chances to be selected than a possibility of 3 characters.

* **Constraints** unlike estimations, eliminate some possibilities if they do not respect a precise rule, without taking
into account valid values at all.
For example if the third column has to be exactly 5 characters long, then all possibilities with a value that does not
will be discarded.

## Example

Let's illustrate how it works step-by-step, consider the following CSV data:

```csv
ID,NAME,DESCRIPTION,BIRTHDAY,COUNTRY
1,John,Hey everyone I'm the first user,1984-05-16,United Kingdom
2,Pierre,Bonjour à tous vous allez bien ?,1992-11-26,France
3,Pedro,Holà qué tal ?,1962-01-05,Spain
4,Arnold,My country name contains a , in it,1974-05-30,Macedonia, Rep. of
5,Peter,I, like, to, use, commas, between, words,1994-12-04,United States
```

As you can see, it looks like CSV data but values are not quoted. Have a look especially to the two last lines, yeah...
some values appear to contain some comma characters, which is used also as the delimiter. How to fix it ? Let's see how
DSV Mender works...

### Building the mender

The first thing is to configure a _Mender_ object based on the input data. You need to specify the delimiter string as
well as the valid number of columns.

#### Automatic configuration

If you don't know so much about the data or you want to see how the Mender acts automatically you can use that:

```java
final DsvMender mender = DsvMender.auto(",", 5); // delimiter and number of columns
```

#### Advanced configuration

If you know approximately how some columns need to be formatted and to get more accurate results, you would better use a
more advanced configuration. Concerning our example the Mender could be created like this:

```java
final DsvMender mender = DsvMender.builder(",", 5)
		.withLengthEstimations() // Estimating the length of the value for every columns
		.withContainsEstimations(" ") // Estimating if the value contains a space character for every columns
		.withPatternConstraint(0, Pattern.compile("[0-9]+")) // The ID column is always numerical, not empty
		.withLengthConstraint(3, 10) // The birthday column always contains 10 characters
		.build();
```

### Processing the data

Before to fix, and because we configured estimations, then we first need to fit the DSV Mender with valid rows.

```java
mender.fit("1,John,Hey everyone I'm the first user,1984-05-16,United Kingdom");
mender.fit("2,Pierre,Bonjour à tous vous allez bien ?,1992-11-26,France");
mender.fit("3,Pedro,Holà, qué tal ?,1962-01-05,Spain");
```

Finally we can now fix invalid rows and display the result:

```java
try {
	Arrays.asList(mender.fix("4,Arnold,My country name contains a , in it,1974-05-30,Macedonia, Rep. of")).forEach(System.out::println);
	System.out.println();
	Arrays.asList(mender.fix("5,Peter,I, like, to, use, commas, between, words,1994-12-04,United States")).forEach(System.out::println);
} catch (final MenderException e) {
	System.err.println("ERROR: No solution has been found, try others estimations and constraints");
}
```

If you had properly configured the DSV Mender as described earlier, then the data should be fixed.

#### Notes:
* If you don't know which row is valid or not, you should use _fitIfValid_ and _fixIfNotValid_ instead of _fit_ and
_fix_.
* Even better, you can use the _DSVReader_ wrapper class that automatically fit and fix while reading from a source.

More examples can be found in the _examples_ package.

## Maven commands

### Compiling

```
mvn compile
```

### Running unit tests

```
mvn test
```

### Generating the Javadoc

```
mvn javadoc:javadoc
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details