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

```java

        //init your writer and reader
        Writer writer = new Writer("MyConfig");
        Reader reader = writer.getReader();

        //here you have some examples for putting objects into the cache
        writer.write("user1", new User("1234", "Berlin"));
        writer.write("anything", "you want");
        writer.write("example list", List.of("hello", "world"))
        //here we save the cache
        writer.save();

        //now we need to get the data out of the config
        User user = reader.get("user1", User.class);
        String s = reader.get("anything", "you want");

        //important! for maps and lists we use this methode!
        List<String> strings = reader.getList("example list", String.class);

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

public class Benchmark {

    public static long benchMark(Runnable runnable) {

        long start = System.nanoTime();
        runnable.run();
        return System.nanoTime() - start;

    }

}
```
