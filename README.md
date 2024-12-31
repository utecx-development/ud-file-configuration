# UfoData - A new, blazing fast JSON manager 🚀

Developed to provide a simple solution for Java developers to handle their configuration and data using flat files (json based), UfoData strives to provide exactly that. Here’s why you should consider using UfoData:

- ⚡ **Fast** - Thanks to its in-built cache, it's even faster than GSON!
- 🍎 **Simple** - By looking at the examples down below you will notice UD is very beginner friendly, readable and simple to use.
- 🌈 **Unique** - Featuring a new, easy-to-understand format, UfoData’s format is really nice to write and read.
- 🔄 **Updated and Well-Maintained** - Enjoy new features regularly. 😃
- ❗ **Responsible** - Found a bug or missing a feature? Let us know on our [Discord](https://discord.gg/gzxrub5ABQ).

## Implementation
(repo is closed unfortunatly! please implement it your self or copy the project.)

## Usage

> 1 - your path | 2 - your file name | 3 - enable or disable auto shutdown save

```java
UD ud = UD.init("C:\\YOUR\\PATH\\", "test", true);
```

> <1> this can be everything including premitives, java objects such as user profiles, lists and maps

```java
ud.set("key", <1>);
```

> here you you see a demenstration of the usage
  
```java
ud.get(<1>.class, "key");
```
> the boolean is used to force the save even tho nothing was changed (according to the file system). please use true if you work with immutables like maps or lists here!
```java
ud.save(true);
```

For more information and support, join our [Discord community](https://discord.gg/gzxrub5ABQ).
