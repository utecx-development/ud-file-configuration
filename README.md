# UfoData - A new, blazing fast file format

Developed to provide a simple solution for java developers to handle their configuration and data using flatfile,
UfoData strives to provide exactly that. UfoData is:
- ⚡️ Fast - Due to using an in-built-cache, it's even faster than GSON!
- 🍎 Simple - Just create a config for your files and start working with them.
- 🌈 Unique - With a new & easy to understand format, UD's format is really nice to write & read
- 🔄 Updated and well maintained - Make use of new features regularly :D
- ❗ Responsible - In case you find any bug or a feature you dont want to be missing you can always tell us in our discord https://discord.gg/gzxrub5ABQ
- JSON - Ud has an inbuild complexe json support so you can convert your ud to json or ud to json (read details down below)

Implementation
-

> Maven
```xml

    <dependencies>
        <dependency>
            <groupId>de.ufomc</groupId>
            <artifactId>ud-fileformat</artifactId>
            <version>1.0.2</version>
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
    implementation 'de.ufomc:ud-fileformat:1.0.2'
}
```

> Gradle (Kotlin DSL)
```kotlin
repositories {
    maven("https://www.ufomc.de/repo/")
}

dependencies {
    implementation("de.ufomc", "ud-fileformat", "1.0.2")
}
```

Usage
-

> Important

- Every complex object just like user data which you want to save to your file has to extend the UfObject class or else it wont be formatted correctly

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

simply use json to convert your file to or from json
-

> this feature is still in beta! please report any bugs to our staff

```java

        String json = config.toJson();
        config.fromJson("{\"your\": \"json string\"}");

```

How does the data look when it's formatted?
-
```
string:hello=world;
list<string>:list=[hello, world, ...];
map<string,string>:map={hello-Iamamap};
object:object={string:name=Tom,int:age=13};
```

Why use?
-

-- soon! --
