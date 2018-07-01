# QuickPermissions-Kotlin
The most easiest way to handle Android Runtime Permissions in Kotlin

# Example

![example](/media/example.png)

# Usage

In your project's `build.gradle` file add the line as shown below:
```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "http://jitpack.io/" }  // <-- THIS SHOULD BE ADDED
    }
}
```

And in your app module's gradle file add the dependency:

```
    implementation 'com.github.quickpermissions:quickpermissions-kotlin:0.1.0'
```

You're all set!
