![screenshot](https://i.imgur.com/JEn7UgP.png)

🔒 "Secure your gameplay with a swipe—draw your way to safety!"

---

## 🚀 For Developers

Although the plugin is not officially listed as a library, you can still integrate it into your project by following these steps:

### 1️⃣ Create a `libs/` folder in your project directory.
### 2️⃣ Add the latest JAR file to the folder.
### 3️⃣ Import the plugin like this:

#### 📦 **Groovy Gradle**:
```groovy
dependencies {
    implementation files('libs/plugin.jar')
}
```

#### 📦 **Kotlin Gradle**:
```kotlin
implementation(files("libs/plugin.jar"))
```

#### 📦 **Maven**:
```xml
<dependencies>
    <dependency>
        <groupId>net.solostudio</groupId>
        <artifactId>drawlock</artifactId>
        <version>{VERSION}</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/libs/plugin.jar</systemPath>
    </dependency>
</dependencies>
```

---
## 📜 Commands

- `/drawlock reload` - Reloads the plugin.
- `/drawlock changepassword` - Changes Password.

---

## 🔑 Permissions

- `drawlock.reload`
- `drawlock.changepassword`

---
## 🤝 Contributing

Contributions are always welcome! If you have ideas for improvements, feel free to join the Discord server and open a ticket. Let's build something awesome together!

---

## 📜 License

This plugin is licensed under the [Apache-2.0 License](https://www.apache.org/licenses/LICENSE-2.0).