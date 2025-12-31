# GUI Library Examples

This module contains example code and test cases demonstrating the features of the Gui library.

## Purpose

- **Learning Resource**: Provides working examples for developers using the library
- **Testing**: Serves as a test bed for library features
- **Documentation**: Shows best practices and common patterns

## Building

Build the example plugin JAR:

```bash
./gradlew :bukkit-gui-example:shadowJar
```

The compiled plugin will be located at:
```
test/build/libs/GuiExample-<version>.jar
```

## Installation

1. Build the plugin using the command above
2. Copy the JAR file to your server's `plugins/` folder
3. Start or reload your server

## Usage

Once installed, use the following commands in-game:

```
/guiexample    - Simple 3-level navigation example
/shopexample   - Complex 6-level shop system example
```

## Examples Included

### 1. Simple Navigation Example

**Location**: `me.huanmeng.gui.example.navigation`

**Command**: `/guiexample`

Demonstrates basic stack-based navigation:

- **MainMenuGui**: Entry point showing three categories
- **CategoryGui**: Category browser with items
- **ItemDetailGui**: Detailed view of individual items

**Navigation Flow** (3 levels):
```
Main Menu → Category → Item Detail
    ↑          ↑           ↑
    └──────────┴───────────┘
         Back Navigation
```

### 2. Advanced Shop System

**Location**: `me.huanmeng.gui.example.shop`

**Command**: `/shopexample`

Demonstrates complex 6-level deep navigation:

- **MainShopGui**: Shop entry with category selection
- **CategoryBrowserGui**: Paginated item browser
- **ItemDetailGui**: Item information and purchase options
- **PurchaseWizardGui**: Customization wizard
- **EnchantmentSelectorGui**: Enchantment selection
- **TransactionSummaryGui**: Final confirmation

**Navigation Flow** (6 levels):
```
Main Shop → Category → Item Details → Purchase Wizard → Enchantments → Summary
    ↑          ↑           ↑              ↑                ↑            ↑
    └──────────┴───────────┴──────────────┴────────────────┴────────────┘
                              Back Navigation
```

## Key Features

- Stack-based navigation (push/pop)
- Proper state preservation when navigating back
- Custom constructor parameters support
- No memory leaks or null pointer exceptions

## Code Examples

### Basic HGui Usage

```java
public class MyGui extends HGui {
    public MyGui(Player player, boolean allowBack) {
        super(player, allowBack);
    }

    @Override
    protected AbstractGui<?> gui() {
        GuiCustom gui = new GuiCustom(context.getPlayer());
        gui.line(3);
        gui.title("My GUI");
        // Add buttons...
        return gui;
    }
}

// Open the GUI
new MyGui(player, true).open();
```

### Custom Constructor Parameters

For GUIs with additional constructor parameters, use `setConstructor`:

```java
public class CategoryGui extends HGui {
    private final String categoryName;

    public CategoryGui(Player player, boolean allowBack, String categoryName) {
        super(player, allowBack);
        this.categoryName = categoryName;

        // Register constructor for back navigation
        setConstructor(
            MethodType.methodType(void.class, Player.class, boolean.class, String.class),
            (p, back) -> Arrays.asList(p, back, categoryName)
        );
    }
}
```

### Using Back Navigation

```java
// In button click handler
gui.draw().set(Slot.of(0), Button.of(backItem, click -> {
    gui.back();  // Navigate to previous GUI
}));
```

## Project Structure

```
test/
├── src/main/java/me/huanmeng/gui/example/
│   ├── GuiExamplePlugin.java          # Plugin main class
│   ├── navigation/                    # Simple 3-level example
│   │   ├── MainMenuGui.java
│   │   ├── CategoryGui.java
│   │   └── ItemDetailGui.java
│   └── shop/                          # Complex 6-level example
│       ├── ShopCategory.java
│       ├── ShopItem.java
│       ├── ShopManager.java
│       ├── MainShopGui.java
│       ├── CategoryBrowserGui.java
│       ├── ItemDetailGui.java
│       ├── PurchaseWizardGui.java
│       ├── EnchantmentSelectorGui.java
│       └── TransactionSummaryGui.java
├── src/main/resources/
│   └── plugin.yml
├── build.gradle.kts
└── README.md
```

## Navigation System

The navigation uses a **stack (Deque)** to track GUI history:

1. **Forward Navigation**: When opening a new GUI, the current GUI is pushed onto the stack
2. **Back Navigation**: Pop from stack and recreate the previous GUI
3. **Cleanup**: Stack is cleared when player closes all GUIs

**Stack Evolution Example**:
```
Open MainShop      → Stack: []
Open Category      → Stack: [MainShop]
Open ItemDetail    → Stack: [MainShop, Category]
Back to Category   → Stack: [MainShop]
Back to MainShop   → Stack: []
Back (close)       → GUI closed
```

## Testing

To test the navigation system:

1. Run `/shopexample` in-game
2. Navigate through all 6 levels to Transaction Summary
3. Click "Back" repeatedly to return through each level
4. Verify smooth navigation without errors

Expected behavior:
- No errors or exceptions
- Correct GUI displayed at each back step
- Memory properly cleaned up when GUI closes

## Support

For issues or questions about the library:
- GitHub: https://github.com/huanmeng-qwq/Gui
- Wiki: https://github.com/huanmeng-qwq/Gui/wiki
