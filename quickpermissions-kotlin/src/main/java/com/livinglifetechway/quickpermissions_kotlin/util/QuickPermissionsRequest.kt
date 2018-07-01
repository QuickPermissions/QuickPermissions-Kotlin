package com.livinglifetechway.quickpermissions_kotlin.util

data class QuickPermissionsRequest(
        private var target: PermissionCheckerFragment,
        var permissions: Array<String> = emptyArray(),
        var handleRationale: Boolean = true,
        var rationaleMessage: String = "",
        var handlePermanentlyDenied: Boolean = true,
        var permanentlyDeniedMessage: String = "",
        internal var rationaleMethod: ((QuickPermissionsRequest) -> Unit)? = null,
        internal var permanentDeniedMethod: ((QuickPermissionsRequest) -> Unit)? = null,
        internal var permissionsDeniedMethod: ((QuickPermissionsRequest) -> Unit)? = null,
        var deniedPermissions: Array<String> = emptyArray(),
        var permanentlyDeniedPermissions: Array<String> = emptyArray()
) {
    /**
     * Proceed with requesting permissions again with user request
     */
    fun proceed() = target.requestPermissionsFromUser()

    /**
     * Cancels the current permissions request flow
     */
    fun cancel() = target.clean()

    /**
     * In case of permissions permanently denied, request user to enable from app settings
     */
    fun openAppSettings() = target.openAppSettings()
}