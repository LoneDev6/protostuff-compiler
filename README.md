Protocol Buffers parser and code generator
------------------------------------------

# How to use the Maven plugin

## Step 1

Add this to your maven plugins of your project.

```xml
<plugin>
    <groupId>io.protostuff</groupId>
    <artifactId>protostuff-maven-plugin</artifactId>
    <version>2.0.0-alpha4-mutable-1.0.1</version>
    <executions>
        <execution>
            <id>generate-sources</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>java</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Add these dependencies to your project

```xml
<dependency>
    <groupId>io.protostuff</groupId>
    <artifactId>protostuff-core</artifactId>
    <version>1.8.0</version>
</dependency>
<dependency>
    <groupId>io.protostuff</groupId>
    <artifactId>protostuff-runtime</artifactId>
    <version>1.7.4</version>
</dependency>
```

## Step 2

Create your `.proto` files inside `src/proto/` folder in your project.

Example:
```proto
syntax = "proto3";

package example.simple;

message Example {
  int32 id = 1;
  bool is_simple = 2;
  string name = 3;
  map<string, string> string_attribute = 4;
  map<string, int64> int64_attribute = 5;
  map<string, float> float_attribute = 6;
  optional AnotherClass another_class = 7;
}

message MyClass {
  repeated Example example = 1;
}

message AnotherClass {
  repeated string name = 1;
}
```

## Step 3

Run `package` or `clean package` command.

## Step 4

If Intellij IDEA doesn't autocomple the new generated classes you have to right click on `target/generated-sources/proto` and mark the directory as `Generated Sources Root`
