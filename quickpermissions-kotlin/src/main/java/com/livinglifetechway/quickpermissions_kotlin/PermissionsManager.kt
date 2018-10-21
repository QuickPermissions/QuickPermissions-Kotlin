package com.livinglifetechway.quickpermissions_kotlin

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.livinglifetechway.quickpermissions_kotlin.util.PermissionCheckerFragment
import com.livinglifetechway.quickpermissions_kotlin.util.PermissionsUtil
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions

private const val TAG = "runWithPermissions"

/**
 * Injects code to ask for permissions before executing any code that requires permissions
 * defined in the annotation
 */
fun Context?.runWithPermissions(
        vararg permissions: String,
        options: QuickPermissionsOptions = QuickPermissionsOptions(),
        callback: () -> Unit
): Any? {
    return runWithPermissionsHandler(this, permissions, callback, options)
}

/**
 * Injects code to ask for permissions before executing any code that requires permissions
 * defined in the annotation
 */
fun Fragment?.runWithPermissions(
        vararg permissions: String,
        options: QuickPermissionsOptions = QuickPermissionsOptions(),
        callback: () -> Unit
): Any? {
    return runWithPermissionsHandler(this, permissions, callback, options)
}

private fun runWithPermissionsHandler(target: Any?, permissions: Array<out String>, callback: () -> Unit, options: QuickPermissionsOptions): Nothing? {
    Log.d(TAG, "runWithPermissions: start")

    // get the permissions defined in annotation
    Log.d(TAG, "runWithPermissions: permissions to check: $permissions")

    // get target
    if (target is AppCompatActivity || target is Fragment) {
        Log.d(TAG, "runWithPermissions: context found")

        val context = when (target) {
            is Context -> target
            is Fragment -> target.context
            else -> null
        }

        // check if we have the permissions
        if (PermissionsUtil.hasSelfPermission(context, arrayOf(*permissions))) {
            Log.d(TAG, "runWithPermissions: already has required permissions. Proceed with the execution.")
            callback()
        } else {
            // we don't have required permissions
            // begin the permission request flow

            Log.d(TAG, "runWithPermissions: doesn't have required permissions")

            // check if we have permission checker fragment already attached

            // support for AppCompatActivity and Activity
            var permissionCheckerFragment = when (context) {
            // for app compat activity
                is AppCompatActivity -> context.supportFragmentManager?.findFragmentByTag(PermissionCheckerFragment::class.java.canonicalName) as PermissionCheckerFragment?
            // for support fragment
                is Fragment -> context.childFragmentManager.findFragmentByTag(PermissionCheckerFragment::class.java.canonicalName) as PermissionCheckerFragment?
            // else return null
                else -> null
            }

            // check if permission check fragment is added or not
            // if not, add that fragment
            if (permissionCheckerFragment == null) {
                Log.d(TAG, "runWithPermissions: adding headless fragment for asking permissions")
                permissionCheckerFragment = PermissionCheckerFragment.newInstance()
                when (context) {
                    is AppCompatActivity -> {
                        context.supportFragmentManager.beginTransaction().apply {
                            add(permissionCheckerFragment, PermissionCheckerFragment::class.java.canonicalName)
                            commit()
                        }
                        // make sure fragment is added before we do any context based operations
                        context.supportFragmentManager?.executePendingTransactions()
                    }
                    is Fragment -> {
                        // this does not work at the moment
                        context.childFragmentManager.beginTransaction().apply {
                            add(permissionCheckerFragment, PermissionCheckerFragment::class.java.canonicalName)
                            commit()
                        }
                        // make sure fragment is added before we do any context based operations
                        context.childFragmentManager.executePendingTransactions()
                    }
                }
            }

            // set callback to permission checker fragment
            permissionCheckerFragment.setListener(object : PermissionCheckerFragment.QuickPermissionsCallback {
                override fun onPermissionsGranted(quickPermissionsRequest: QuickPermissionsRequest?) {
                    Log.d(TAG, "runWithPermissions: got permissions")
                    try {
                        callback()
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }

                override fun onPermissionsDenied(quickPermissionsRequest: QuickPermissionsRequest?) {
                    quickPermissionsRequest?.permissionsDeniedMethod?.invoke(quickPermissionsRequest)
                }

                override fun shouldShowRequestPermissionsRationale(quickPermissionsRequest: QuickPermissionsRequest?) {
                    quickPermissionsRequest?.rationaleMethod?.invoke(quickPermissionsRequest)
                }

                override fun onPermissionsPermanentlyDenied(quickPermissionsRequest: QuickPermissionsRequest?) {
                    quickPermissionsRequest?.permanentDeniedMethod?.invoke(quickPermissionsRequest)
                }
            })

            // create permission request instance
            val permissionRequest = QuickPermissionsRequest(permissionCheckerFragment, arrayOf(*permissions))
            permissionRequest.handleRationale = options.handleRationale
            permissionRequest.handlePermanentlyDenied = options.handlePermanentlyDenied
            permissionRequest.rationaleMessage = if (options.rationaleMessage.isBlank())
                "These permissions are required to perform this feature. Please allow us to use this feature. "
            else
                options.rationaleMessage
            permissionRequest.permanentlyDeniedMessage = if (options.permanentlyDeniedMessage.isBlank())
                "Some permissions are permanently denied which are required to perform this operation. Please open app settings to grant these permissions."
            else
                options.permanentlyDeniedMessage
            permissionRequest.rationaleMethod = options.rationaleMethod
            permissionRequest.permanentDeniedMethod = options.permanentDeniedMethod
            permissionRequest.permissionsDeniedMethod = options.permissionsDeniedMethod

            // begin the flow by requesting permissions
            permissionCheckerFragment.setRequestPermissionsRequest(permissionRequest)

            // start requesting permissions for the first time
            permissionCheckerFragment.requestPermissionsFromUser()
        }
    } else {
        // context is null
        // cannot handle the permission checking from the any class other than AppCompatActivity/Fragment
        // crash the app RIGHT NOW!
        throw IllegalStateException("Found " + target!!::class.java.canonicalName + " : No support from any classes other than AppCompatActivity/Fragment")
    }
    return null
}