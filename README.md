Protocol Buffers parser and code generator
------------------------------------------

# How to use the Maven plugin

## Step 1

Add this to your maven plugins of your project.

```xml
<project>
    
    <pluginRepositories>
        <pluginRepository>
            <id>github</id>
            <url>https://maven.pkg.github.com/LoneDev6/protostuff-compiler</url>
        </pluginRepository>
    </pluginRepositories>
    
    <build>
        <plugins>
            <plugin>
                <groupId>dev.lone.protostuff</groupId>
                <artifactId>protostuff-maven-plugin</artifactId>
                <version>2.0.0-alpha4-mutable-1.0.2</version>
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
        </plugins>
    </build>
    
</project>
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

Open your `.m2/settings.xml` file (or create it) and add this (took from the [Github official tutorial](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token)).
Remember to create the token with the `read:packages`.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">


  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/LoneDev/protostuff-compiler</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
    <servers>
    <server>
        <id>github</id>
        <username>YOUR_USERNAME</username> <!--  ############# CHANGE THIS!!!!! ############# -->
        <password>YOUR_TOKEN</password> <!--  ############# CHANGE THIS!!!!! ############# -->
    </server>
    </servers>
</settings>
```


## Step 2

Create your `.proto` files inside `src/proto/` folder in your project.

Example:
```proto
syntax = "proto3";

package example.simple;

message MyClass {
  int32 id = 1;
  bool is_simple = 2;
  string name = 3;
  map<string, string> string_attribute = 4;
  map<string, int64> int64_attribute = 5;
  map<string, float> float_attribute = 6;
  optional AnotherClass another_class = 7;
}

message AnotherClass {
  repeated string name = 1;
}
```

## Step 3

Run `package` or `clean package` command.

## Step 4

If Intellij IDEA doesn't autocomple the new generated classes you have to right click on `target/generated-sources/proto` and mark the directory as `Generated Sources Root`


# Advanced

## Thread safe classes

You can generate classes with thread safe maps and lists.

Example:
```xml
<plugin>
    <groupId>dev.lone.protostuff</groupId>
    <artifactId>protostuff-maven-plugin</artifactId>
    <version>2.0.0-alpha4-mutable-1.0.2</version>
    <configuration>
        <threadSafe>
            <entry>com.mypackage.mysubpackage.*</entry> <!-- Supports wildcard to add all classes in a package-->
            <entry>com.mypackage.another.SpecificClass</entry>
            <entry>com.mypackage.another.SpecificClass2</entry>
        </threadSafe>
    </configuration>
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

