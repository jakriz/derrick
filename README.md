# Derrick

Derrick, the documentation inspector, enables you to test your high level Java documentation and tutorials. Make sure that the code samples you provide to your integrators are always correct and up-to-date!

## Motivation
If you have a library which you provide to your users, you need to document its usage. This is usually done through a low level JavaDoc documenting each class, interface and method, but also through a higher level documentation or tutorials in which you describe the typical usage of your product with code samples along the way.

The JavaDoc documentation is generated from your source code and is easy to take care of, however, the usage code samples can be much harder to maintain. They usually sit somewhere on your website, separate from your development and deployment processes.

Derrick allows you to write integration/functional-type tests for the code samples you use in your documentation or tutorials. You can make them a part of your automated build or deployment process to make sure your docs are always correct and working with your latest release.

## Sample Usage
Say you have a library which implements some math operations. Your wiki may look something like:

>To add a few numbers together you can use MathWizard.add() method, for example
>
> <code id="sample-math-wizard-add">mathWizard.add(1, 2, 3);</code>
>
>returns the sum of these numbers, in this case 6.

To make sure this part of the documentation is correct you:

1. make sure that you can identify the code sample block with a CSS selector,
2. create an interface which references the code in your documentation
    ```java
    @DerrickInterface(baseUrl = "http://mathwizard.io", imports = {"io.mathwizard.*"})
    public interface DocsMethods {

        @SourceFrom(path = "tutorial.html", selector = "#sample-add", returnLast = true)
        void add(MathWizard mathWizard);
    }
    ```
3. create a test which may look like:
    ```java
    class DocsTest {
        private DocsMethods docsMethods = Derrick.get(DocsMethods.class);

        private MathWizard mathWizard = new MathWizard();

        @Test
        public void testAdd() {
            assertEquals(docsMethods.add(mathWizard), 6);
        }
    }
    ```

That's all. Every time you build, Derrick will give you an up-to-date implementation of your interface by downloading the code from your docs, as long as the docs are accessible on the internet of course.

You can add as many methods to your interface and create as many interfaces as you like. You can also pass in parameters, like we passed in the `MathWizard` object in the sample, or leave them out.

## Annotations and Options
This section describes the annotations you have to use.

### @DerrickInterface
`@DerrickInterface` marks the interface to be processed by Derrick.

You need to specify a `baseUrl` parameter, which contains the base url for the paths to the code sample pages.

You will probably also want to specify an `imports` parameter, which specifies which Java imports are needed to run the code from the samples. Since this library is targeted at testing, not production builds, you can simply specify whole packages with wildcards.

### @SourceFrom
`@SourceFrom` marks the interface method to be filled in with downloaded code by Derrick.

You need to specify a `path` to the page (which is embedded to the base url from the class annotation) and a `selector`.

The `selector` is a [CSS selector](http://www.w3schools.com/cssref/css_selectors.asp) which identifies the block with the code to be downloaded. Internally we use the [Jsoup HTML parser](http://jsoup.org/) to extract the element.

The methods annotated with this annotation can also have parameters passed in, it is up to you to make sure they work with your code.

To be able to test a code sample, Derrick provides you a few tricks. First off, since we only support method calls, Derrick removes any top level method you may have in your code sample leaving just the code which the method contained.

You can specify additional options so that the interface method used for testing returns you the value you want tested.  You can set `returnLast` option to `true` makes the method return the last line of the code. If this line contains and assignment, the assigned value is returned. This option is meant mainly for testing one-liners and can be seen used in the example above.

You can also set `addReturn` option to a string which will be added to the end of the code with a return statement. To illustrate this option, suppose you have a tutorial about working with Lists:
<pre id="sample-list-add-element">
List&lt;String&gt; list = new ArrayList&lt;&gt;();
list.add(&quot;a&quot;);
</pre>
To test this piece of code, you can include a method in your interface such as:
```java
@SourceFrom(path = "list-tutorial.html", selector = "#sample-add-element", addReturn = "list")
List<String> addElement();
```

## Run the Included Example

The included example tests this very tutorial. The code is taken from the two code samples, `MathWizard.add()` and adding an element to a list.

To run the example:

1. Clone the repo
2. Run the example project `./gradlew example:run` or the example tests `./gradlew example:test`.

## Code Status

[![Build Status](https://travis-ci.org/jakriz/derrick.svg?branch=master)](https://travis-ci.org/jakriz/derrick)
[![codecov.io](https://codecov.io/github/jakriz/derrick/coverage.svg?branch=master)](https://codecov.io/github/jakriz/derrick?branch=master)
