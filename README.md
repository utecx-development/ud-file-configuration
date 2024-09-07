# ud-file-configuration

The ud file configuration is a methode to manage your user data or data in general.

Usage
-

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
