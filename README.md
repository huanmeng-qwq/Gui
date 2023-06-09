<!--- @formatter:off --->
# Gui

<div style="text-align: center;">

![Version](https://img.shields.io/github/v/release/huanmeng-qwq/Gui?style=plastic)
![Servers](https://img.shields.io/bstats/servers/18670?style=flat-square)
![Code-Size](https://img.shields.io/github/languages/code-size/huanmeng-qwq/Gui?style=plastic)
![Repo-Size](https://img.shields.io/github/repo-size/huanmeng-qwq/Gui?style=plastic)
![License](https://img.shields.io/github/license/huanmeng-qwq/Gui?style=plastic)
![Language](https://img.shields.io/github/languages/top/huanmeng-qwq/Gui?style=plastic)
![Last-Commit](https://img.shields.io/github/last-commit/huanmeng-qwq/Gui?style=plastic)
</div>

Lightweight Inventory API for Bukkit plugins, with 1.7.10 to 1.20 support.

## Features
* Works with all versions from 1.7.10 to 1.20
* Very small (around 2k lines of code with the JavaDoc) and no dependencies
* Easy to use
* [Adventure](https://github.com/KyoriPowered/adventure) components support

### Maven

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.2.3</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <relocations>
                    <relocation>
                        <pattern>me.huanmeng.opensource.bukkit.gui</pattern>
                        <!-- Replace 'com.yourpackage' with the package of your plugin ! -->
                        <shadedPattern>com.yourpackage.gui</shadedPattern>
                    </relocation>
                </relocations>
            </configuration>
        </plugin>
    </plugins>
</build>

<dependencies>
    <dependency>
        <groupId>com.huanmeng-qwq</groupId>
        <artifactId>Bukkit-Gui</artifactId>
        <version>1.3.7</version>
    </dependency>
</dependencies>
```
When using Maven, make sure to build directly with Maven and not with your IDE configuration. (on IntelliJ IDEA: in the `Maven` tab on the right, in `Lifecycle`, use `package`).

### Gradle

```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.huanmeng-qwq:Bukkit-Gui:1.3.7'
}

shadowJar {
    // Replace 'com.yourpackage' with the package of your plugin 
    relocate 'me.huanmeng.opensource.bukkit.gui', 'com.yourpackage.fastboard'
}
```

## Use

### Creating a Gui

Just create a `GuiCustom`：

```java
GuiCustom gui = new GuiCustom(player);
        
// Set the line
gui.line(3);

// Set the title
gui.title("Test Gui");

// Add a apple
gui.draw().set(Slot.of(1), Button.of(player-> new ItemStack(Material.APPLE)));

// Open for player
gui.openGui();
```

## Adventure support

For servers on modern [PaperMC](https://papermc.io) versions, FastBoard supports
using [Adventure](https://github.com/KyoriPowered/adventure) components instead of strings,
by using the method `gui.title(Component)`.