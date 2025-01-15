# UfoData - A new, blazing fast JSON manager 🚀

Developed to provide a simple solution for Java developers to handle their configuration and data using flat files (json based), UfoData strives to provide exactly that. Here’s why you should consider using UfoData:

- 📑 **Notes** - You can comment the files with a '#' infront of your line. This is usualy not the case in json.
- ⚡ **Fast** - Thanks to its in-built cache, it's even faster than GSON!
- 🍎 **Simple** - By looking at the examples down below you will notice UD is very beginner friendly, readable and simple to use.
- 🌈 **Unique** - Featuring a new, easy-to-understand format, UfoData’s format is really nice to write and read.
- 🔄 **Updated and Well-Maintained** - Enjoy new features regularly. 😃
- ❗ **Responsible** - Found a bug or missing a feature? Let us know on our [Discord](https://discord.gg/gzxrub5ABQ).

## Implementation
(repo is closed unfortunatly! please implement it your self or copy the project.)

## Usage

> 1 - your path | 2 - your file name | 3 - enable or disable auto shutdown save

**NOTE:** You can NOT save your UD regulary when using the init from string! Use the saveToFile(FILE) instead!

```java
UD ud = UD.init("C:\\YOUR\\PATH\\", "your-file-name", true);

UD ud = UD.init("{your json string here}");
```

> **<1>** this can be everything including premitives, java objects such as user profiles, lists and maps

```java
ud.set("key", <1>);
```

> here you you see a demenstration of the usage
  
```java
ud.get("key", <1>.class);
```
> the boolean is used to force the save even tho nothing was changed (according to the file system). please use true if you work with immutables like maps or lists here!
```java
ud.save(true);
```

## Important: 

> This software is free to use and modify for everyone. Please dont sell it tho! If you would like to give me/the repo a credit I would be very happy but it´s no must have!
Also have a nice day 👋


For more information and support, join our [Discord community](https://discord.gg/gzxrub5ABQ).

![Views Counter](https://views-counter.vercel.app/badge?pageId=ud-file-configuration%2FViews-Counter&leftColor=400000&rightColor=ff8080&type=unique&sessionExpire=60&label=Visitors&style=upper)

