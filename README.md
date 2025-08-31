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

Lightweight Inventory API for Bukkit(Paper/Spigot) plugins, with 1.8.8 to 1.21 support.

### Useful links
- [Documentation](https://github.com/huanmeng-qwq/Gui/wiki)
- [Bugs & issues report](https://github.com/huanmeng-qwq/Gui/issues)

## Features
* Works with all versions from 1.8.8 to 1.21
* Very small (around 3k lines of code with the JavaDoc) and no dependencies
* Easy to use
* Kotlin DSL
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
                        <pattern>me.huanmeng.gui.guime.huanmeng.gui.gui</pattern>
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
        <artifactId>bukkit-gui</artifactId>
        <version>2.5.1</version>
    </dependency>

    <!--Kotlin DSL-->

    <dependency>
        <groupId>com.huanmeng-qwq</groupId>
        <artifactId>bukkit-gui-kotlin-dsl</artifactId>
        <version>2.5.1</version>
    </dependency>
</dependencies>
```
When using Maven, make sure to build directly with Maven and not with your IDE configuration. (on IntelliJ IDEA: in the `Maven` tab on the right, in `Lifecycle`, use `package`).

### Gradle

```groovy
plugins {
    id("com.gradleup.shadow") version "8.3.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.huanmeng-qwq:bukkit-gui:2.5.1'
    // Kotlin DSL
    implementation 'com.huanmeng-qwq:bukkit-gui-kotlin-dsl:2.5.1'
}

shadowJar {
    // Replace 'com.yourpackage' with the package of your plugin
    relocate 'me.huanmeng.gui', 'com.yourpackage.huanmeng.gui'
}
```

## Use

### Creating a Gui

Just create a `GuiCustom`:

#### Java
```java
import me.huanmeng.gui.gui.impl.GuiCustom;
import me.huanmeng.gui.gui.slot.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Example extends JavaPlugin {
    @Override
    public void onEnable() {
        new GuiManager(this);
    }

    @Override
    public void onDisable() {
        GuiManager.instance().close();
    }

    public static void open(Player player) {
        GuiCustom gui = new GuiCustom(player);
        // Set the line
        gui.line(3);

        // Set the title
        gui.title("Test Gui");

        // Add an apple
        gui.draw().set(Slot.of(1), Button.of(player-> new ItemStack(Material.APPLE)));

        // Open for player
        gui.openGui();
    }
}
```

#### Kotlin DSL
<details>
<summary>GuiCustom Dsl</summary>

```kotlin
import org.bukkit.entity.Player

fun openGui(player: Player) {
    player.openGui {
        draw {
            setButton(buildSlot(0)) {
                var a = 1
                showingItem = buildButtonItem {
                    ItemStack(Material.values()[a++])
                }
                updateClick {
                    it.inventory.addItem(showingItem!!.get(it))
                }
            }
        }
    }
}
```
</details>

<details>

<summary>GuiPage Dsl</summary>

```kotlin
import org.bukkit.entity.Player

fun openPageGui(player: Player) {
    buildPagedGui {
        allItems = buildButtons {
            for (i in 0..60) {
                button {
                    showingItem = buildButtonItem(ItemStack(Material.values()[i]))
                }
            }
        }
        elementsPerPage = size() - 9
        elementSlots = buildSlotsByLine { line ->
            return@buildSlotsByLine buildList {
                for (i in 0..9 * line) {
                    add(buildSlot(i))
                }
            }
        }
        pageSetting {
            PageSettings.normal(this)
        }
    }.openGui(player)
}
```

</details>

<details>

<summary>PageSetting Dsl</summary>

```kotlin
buildPagedGui {
    pageSetting {
        buildPageSetting {
            button {
                buildPageButton {
                    types(PageButtonTypes.PREVIOUS)
                    setButton {
                        showingItem = buildButtonItem(ItemStack(Material.ARROW))
                    }
                    click(PlayerClickPageButtonInterface.simple())
                }
            }
            button {
                buildPageButton {
                    types(PageButtonTypes.NEXT)
                    setButton {
                        showingItem = buildButtonItem(ItemStack(Material.ARROW))
                    }
                    handleClick { _, gui, buttonType ->
                        buttonType.changePage(gui)
                    }
                }
            }
        }
    }
    // Do something...
}
```

</details>

## Adventure support

For servers on modern [PaperMC](https://papermc.io) versions, The Gui project supports
using [Adventure](https://github.com/KyoriPowered/adventure) components instead of strings,
by using the method `gui.title(Component)`.

# Support
[<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg" alt="" width="120">](https://www.jetbrains.com/?from=https://github.com/huanmeng-qwq/Gui)

[JetBrains](https://www.jetbrains.com/), creators of the IntelliJ IDEA,
supports Gui with one of their [Open Source Licenses](https://jb.gg/OpenSourceSupport).
IntelliJ IDEA is the recommended IDE for working with Gui.
