# UfoData (ud) - file formate 

The ud file configuration is a methode to manage your data in general.

Implementation
-

> maven
```xml

    <dependencies>
        <dependency>
            <groupId>de.ufomc</groupId>
            <artifactId>ud-fileformate</artifactId>
            <version>1.0.0</version>
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

> gradle
```groovy
repositories {
    maven {
        url "https://www.ufomc.de/repo/"
    }
}

dependencies {
    implementation 'de.ufomc:ud-fileformate:1.0.0'
}
```

Usage
-

> importante

- Every complexe object just like user data wich you want to save to your file has to extend the UfObject class or else it wont be formatted correctly

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
