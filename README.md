# UfoData - A New, Blazing Fast File Format 🚀

Developed to provide a simple solution for Java developers to handle their configuration and data using flat files, UfoData strives to provide exactly that. Here’s why you should consider using UfoData:

- ⚡ **Fast** - Thanks to its in-built cache, it's even faster than GSON!
- 🍎 **Simple** - Just create a config for your files and start working with them.
- 🌈 **Unique** - Featuring a new, easy-to-understand format, UfoData’s format is really nice to write and read.
- 🔄 **Updated and Well-Maintained** - Enjoy new features regularly. 😃
- ❗ **Responsible** - Found a bug or missing a feature? Let us know on our [Discord](https://discord.gg/gzxrub5ABQ).
- 🛠️ **JSON Support** - UfoData includes complex JSON support, allowing you to convert between UfoData and JSON formats seamlessly.

## Implementation

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>dev.ufo</groupId>
        <artifactId>ufodata</artifactId>
        <version>0.3.1</version>
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

### Gradle (Groovy DSL)

```groovy
repositories {
    maven {
        url "https://www.ufomc.de/repo/"
    }
}

dependencies {
    implementation 'dev.ufo:ufodata:0.3.1'
}
```

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://www.ufomc.de/repo/")
}

dependencies {
    implementation("dev.ufo", "ufodata", "0.3.1")
}
```

## Usage

> **Important:** Every complex object, such as user data that you want to save to your file, must extend the `UDObject` class, or it won't be formatted correctly.

```java
// Initialize your UfoFile with one of these implementations:

// Using a class to determine the path and filename
UfoFile ufoFile = UfoFile.of(YourMainClass.class, "fileName");

// Your path and name
UfoFile ufoFile = UfoFile.of("your/path", "filename");

// Or a file
UfoFile ufoFile = UfoFile.of(new File("your file"));

// Examples for putting objects into the cache
ufoFile.put("myObject", new Value("hello", "world"));
ufoFile.put("myList", List.of(1, 2, 3));
ufoFile.put("myMap", Map.of("1. int", 1, "2. int", 2));

// Save the cache
ufoFile.save(true);

// Retrieve the UfoData from the config
Value value = ufoFile.get(Value.class, "myObject");
List<Integer> list = ufoFile.getList(Integer.class, "myList");
Map<String, Integer> map = ufoFile.getMap(String.class, Integer.class, "myMap");
```

## JSON Conversion

> **Note:** This feature is still in beta! Please report any bugs to our staff.

```java
String json = ufoFile.toJson();
ufoFile.fromJson("{\"your\": \"json string\"}");
```

## Data Format Example

Here's how the data looks when formatted:

```
string:hello=world;
list<string>:list=[hello, world, ...];
map<string,string>:map={hello-Iamamap};
object:object={string:name=Tom,int:age=13};
```

## Why Use UfoData?

- **Performance:** Lightning-fast operations with in-built caching.
- **Simplicity:** Easy configuration and usage.
- **Readability:** Clean and understandable data format.
- **Maintenance:** Regular updates and active community support.
- **Responsiveness:** Quick bug fixes and feature implementations based on user feedback.
- **Versatility:** Seamless JSON integration.

---

For more information and support, join our [Discord community](https://discord.gg/gzxrub5ABQ).