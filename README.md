# UfoData - A new, blazing fast file format

Developed to provide a simple solution for java developers to handle their configuration and data using flatfile,
UfoData strives to provide exactly that. UfoData is:
- ⚡️ Fast - Due to using an in-built-cache, it's even faster than GSON!
- 🍎 Simple - Just create a writer for your files and start working with them.
- 🌈 Unique - With a new & easy to understand format, UD's format is really nice to write & read
- 🔄 Updated and well maintained - Make use of new features regularly :D

Implementation
-

> Maven
```xml

    <dependencies>
        <dependency>
            <groupId>de.ufomc</groupId>
            <artifactId>ud-fileformat</artifactId>
            <version>0.2.0</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>

    <repositories>
        <repository>
            <id>repo</id>
            <url>https://www.ufomc.de/repo/</url>
        </repository>
    </repositories>

```

> Gradle (Groovy DSL)
```groovy
repositories {
    maven {
        url "https://www.ufomc.de/repo/"
    }
}

dependencies {
    implementation 'de.ufomc:ud-fileformat:0.2.0'
}
```

> Gradle (Kotlin DSL)
```kotlin
repositories {
    maven("https://www.ufomc.de/repo/")
}

dependencies {
    implementation("de.ufomc", "ud-fileformat", "0.2.0")
}
```

Usage
-

> Important

- Every complex object just like user data which you want to save to your file has to extend the UfObject class or else it wont be formatted correctly
- THE JSON FETURE IS NOT ACCESSIBLE @ THE TIME! WE ARE WORKING ON IT!

```java

        /*
        *init your config with one of these implementations.
        *either you choose:
        */
        
        //only with name -> the path will be caught from the runtime
        Config config = new Config("fileName");
        //your path and name
        Config config = new Config("your/path", "filename");
        //or a file
        Config config = new Config(new File("your file"));

        //here you have some examples for putting objects into the cache
        config.put("myObject", new Value("hello", "world"));
        config.put("myList", List.of(1, 2, 3));
        config.put("myMap", Map.of("1. int", 1, "2. int", 2))
        //here we save the cache
        writer.save();

        //now we need to get the data out of the config
        Value value = config.get(Value.class, "myObject");
        List<Integer> list = config.get(Integer.class, "myList")
        Map<String, Integer> map = config.getMap(String.class, Integer.class, "myMap");


```

Why use?
-

The reason to use the ud file formate is simple. Just take a look at the bench mark:

ud file formate: 33ms
average gson configuration: 78ms

They both hat the same task but ud was more than double as fast. 
Thats because the data is beeing cached at ud file formate.
I know some of you might tell its fake but test it out your self with the following bench mark class:

```java
package org.example;

import de.ufomc.config.benchmark.Benchmark;
import de.ufomc.config.benchmark.BenchmarkResult;

public class CustomBenchmark {

    public static void main(final String[] args) {

        Runnable runnable = //your test here
        int iterations = //how many times do you want it to run?
                
        //actually run the benchmark        
        BenchmarkResult result = Benchmark.run(runnable, iterations);
        System.out.println(result.toString()); //print
        
    }

}
```
