# QuickPermissions-Kotlin [![Release](https://jitpack.io/v/QuickPermissions/QuickPermissions-Kotlin.svg)](https://jitpack.io/#QuickPermissions/QuickPermissions-Kotlin)

The most easiest way to handle Android Runtime Permissions in Kotlin.

![Example](/media/example.png)

* [Inspiration](#inspiration)
* [Add it to your app](#add-it-to-your-app)
* [How to do it?](#how-to-do-it)
  * [Let the library do all the hard stuff](#let-the-library-do-all-the-hard-stuff)
  * [Advanced](#advanced)
* [Summary](#summary)
* [Sample](https://github.com/QuickPermissions/QuickPermissions-Sample)


## Inspiration

Android runtime permissions is pain. 

Android runtime permissions was introduced in the Marshmallow (v 6.0) version of Android. It asks for permissions to user when they are running the app instead of asking for all the permissions while installing the app. It gives more control to users as they can give the permissions they want and deny to those who they do not fill comfortable with. 

With this, it has increased the pain in the neck for the developer, to enable/disable features based on what permissions user has granted or denied. The model asking for permissions was design to function asynchronously, which increased the complexity of an app largely. 

To make it with that, google has created it's own library [easypermissions](https://github.com/googlesamples/easypermissions). (*I didn't find it easy, but that's what they said*). Still, you have to do handful of things and it's asynchronous way of handling things make it hard to manage. There are many other libraries as well to help developers easily handle it. **But,** all libraries has to manage it with proxy classes or managing and passing callbacks and all. 

So, to solve this issue [QuickPermissions](https://github.com/QuickPermissions/QuickPermissions) was created. But, it turns out that, it doesn't work with instant-run and lots of developers can not afford to disable instant run, because it dramatically increased the build time. So, if you are working with Kotlin, you are lucky. This library is created to solve that problem in Kotlin. No gradle plugin and no long running build times. Asking for permissions synchronous way (*It looks like that, but won't block the main thread, don't worry, no ANRs, promise!*). And after the permissions are granted, it will continue executing the method block which was on hold. As simple as that.



## Add it to your app

In your, **project**'s `build.gradle` file:

```groovy
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "http://jitpack.io/" }  // <-- THIS MUST BE ADDED
    }
}
```

As this library is using jitpack to publish this, you need to add jitpack url. If you already have, do not add that again.



In your **app**'s `build.gradle` file, add the following dependency: 

```
dependencies {
   implementation 'com.github.quickpermissions:quickpermissions-kotlin:0.4.0'
}
```



Now, read below for using directions.



## How to do it?



Add this to your `Activity` extending `AppCompatActivity` or `Fragment` from the **support library.** 



### Let the library do all the hard stuff

Use `runWithPermissions` block, and you are all done. Library will manage everything, will ask for permissions, will show rationale dialog if denied and also ask to user to allow permissions from settings if user has permanently denied some permission(s) required by the method. You 



```kotlin
fun methodWithPermissions() = runWithPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) {
    Toast.makeText(this, "Camera and audio recording permissions granted", Toast.LENGTH_SHORT).show();
    // Do the stuff with permissions safely
}
```

That's it! You are good to go.

Your inner code will not be executed until the permission mentioned will not be granted. If it already has the permissions, it will simply be executed right away. If not, it will ask for permissions and executes it after it was granted.



**NOTE:** The one thing is to make sure, your function should not return anything (return type should be Unit), as the asking permissions is asynchronous behavior, that can not be handled in a true synchronous way.



### Advanced

You can optionally pass on the `options` object to that method, which can do some handful of things.

Things you can do with it:

* `handleRationale` : true/false value indicating weather the library should handle rational dialog or not.
* `rationaleMessage` : Custom rational message which will override default value if specified.
* `handlePermanentlyDenied` : true/false value indicating weather the library should handle permissions permanently denied dialog or not.
* `permanentlyDeniedMessage` : Custom permanently denied message which will override default value if specified.
* `rationaleMethod` : Handle rational callback yourself. This will pass on the callback to this function if specified. This will provide you will `QuickPermissionsRequest` instance on which you can call, `proceed()` to continue asking permissions again, or call `cancel()` to cancel the flow.
* `permanentDeniedMethod` : Handle permissions permanently denied callback yourself. This will pass on the callback to this function if specified. This will provide you will `QuickPermissionsRequest` instance on which you can call `openAppSettings()` on it to continue flow or `cancel()` to cancel the flow.
* `permissionsDeniedMethod` : Callback method to handle cases when some/all permissions are denied at the end of asking permissions flow.  This will provide you will `QuickPermissionsRequest` instance from which you can retrieve the permissions which were denied.



```kotlin
val quickPermissionsOption = QuickPermissionsOptions(
	handleRationale = false
    rationaleMessage = "Custom rational message",
    permanentlyDeniedMessage = "Custom permanently denied message",
    rationaleMethod = { req -> rationaleCallback(req) },
    permanentDeniedMethod = { req -> permissionsPermanentlyDenied(req) }
)

private fun methodRequiresPermissions() = runWithPermissions(Manifest.permission.WRITE_CALENDAR, Manifest.permission.RECORD_AUDIO, options = quickPermissionsOption) {
    Toast.makeText(this, "Cal and microphone permissions granted", Toast.LENGTH_LONG).show()
}
```



## Summary 

It's super simple and super easy. Just wrap your code with `runWithPermissions` block and you are good to go by avoiding all the complexity android runtime permissions introduces.



## Sample

There is an sample `app` available in this repo. Just clone it and run `app` to check it out. Play with it and share your feedback.



----

Have any questions, or any trouble? Create an issue.
