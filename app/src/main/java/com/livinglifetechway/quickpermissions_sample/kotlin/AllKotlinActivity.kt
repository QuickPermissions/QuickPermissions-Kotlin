package com.livinglifetechway.quickpermissions_sample.kotlin

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.livinglifetechway.k4kotlin.onClick
import com.livinglifetechway.k4kotlin.setBindingView
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import com.livinglifetechway.quickpermissions_sample.R
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_sample.databinding.ActivityAllKotlinBinding

class AllKotlinActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AllKotlinActivity"
    }

    /**
     * You can hardcode the options like this
     * However for the sample purpose the options is dynamically generated based on the
     * values you have selected from the activity
     */
    private val quickPermissionsOption = QuickPermissionsOptions(
            rationaleMessage = "Custom rational message",
            permanentlyDeniedMessage = "Custom permanently denied message",
            rationaleMethod = { rationaleCallback(it) },
            permanentDeniedMethod = { permissionsPermanentlyDenied(it) },
            permissionsDeniedMethod = { whenPermAreDenied(it) }
    )

    lateinit var mBinding: ActivityAllKotlinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_all_kotlin)

        mBinding.button.onClick {
            // create the quickpermissions object
            val options = QuickPermissionsOptions()
            options.handleRationale = mBinding.handleRationale.isChecked
            if (mBinding.customRationaleMessage.isChecked) {
                options.rationaleMessage = "Custom rational message"
            }
            if (mBinding.customRationaleMethod.isChecked) {
                options.rationaleMethod = { rationaleCallback(it) }
            }
            options.handlePermanentlyDenied = mBinding.handlePermanentlyDenied.isChecked
            if (mBinding.customPermanentlyDeniedMessage.isChecked) {
                options.permanentlyDeniedMessage = "Custom permanently denied message"
            }
            if (mBinding.customPermanentlyDeniedMethod.isChecked) {
                options.permanentDeniedMethod = { permissionsPermanentlyDenied(it) }
            }
            if (mBinding.customDeniedMethod.isChecked) {
                options.permissionsDeniedMethod = { whenPermAreDenied(it) }
            }
            methodRequiresPermissions(options)
        }
    }

    private fun methodRequiresPermissions(quickPermissionsOptions: QuickPermissionsOptions) = runWithPermissions(Manifest.permission.WRITE_CALENDAR, Manifest.permission.RECORD_AUDIO, options = quickPermissionsOptions) {
        Log.d(TAG, "methodRequiresPermissions: Cal and microphone permission granted")

        val toast = Toast.makeText(this, "Cal and microphone permission granted", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun rationaleCallback(req: QuickPermissionsRequest) {
        // this will be called when permission is denied once or more time. Handle it your way
        AlertDialog.Builder(this)
                .setTitle("Permissions Denied")
                .setMessage("This is the custom rationale dialog. Please allow us to proceed " + "asking for permissions again, or cancel to end the permission flow.")
                .setPositiveButton("Go Ahead") { dialog, which -> req.proceed() }
                .setNegativeButton("cancel") { dialog, which -> req.cancel() }
                .setCancelable(false)
                .show()
    }

    private fun permissionsPermanentlyDenied(req: QuickPermissionsRequest) {
        // this will be called when some/all permissions required by the method are permanently
        // denied. Handle it your way.
        AlertDialog.Builder(this)
                .setTitle("Permissions Denied")
                .setMessage("This is the custom permissions permanently denied dialog. " +
                        "Please open app settings to open app settings for allowing permissions, " +
                        "or cancel to end the permission flow.")
                .setPositiveButton("App Settings") { dialog, which -> req.openAppSettings() }
                .setNegativeButton("Cancel") { dialog, which -> req.cancel() }
                .setCancelable(false)
                .show()
    }

    private fun whenPermAreDenied(req: QuickPermissionsRequest) {
        // handle something when permissions are not granted and the request method cannot be called
        AlertDialog.Builder(this)
                .setTitle("Permissions Denied")
                .setMessage("This is the custom permissions denied dialog. \n${req.deniedPermissions.size}/${req.permissions.size} permissions denied")
                .setPositiveButton("OKAY") { _, _ -> }
                .setCancelable(false)
                .show()
//        val toast = Toast.makeText(this, req.deniedPermissions.size.toString() + " permission(s) denied. This feature will not work.", Toast.LENGTH_LONG)
//        toast.setGravity(Gravity.CENTER, 0, 0)
//        toast.show()
    }

}
