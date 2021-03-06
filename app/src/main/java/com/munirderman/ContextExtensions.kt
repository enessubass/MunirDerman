package com.munirderman
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest
import kotlin.system.exitProcess

@SuppressLint("StringFormatInvalid")
internal fun Context.showGrantedToast(permissions: List<PermissionStatus>) {
    val msg = getString(R.string.granted_permissions, permissions.toMessage<PermissionStatus.Granted>())
//    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

@SuppressLint("StringFormatInvalid")
internal fun Context.showRationaleDialog(permissions: List<PermissionStatus>, request: PermissionRequest) {
    val msg = getString(R.string.rationale_permissions, permissions.toMessage<PermissionStatus.Denied.ShouldShowRationale>())

    AlertDialog.Builder(this)
        .setTitle(R.string.permissions_required)
        .setMessage(msg)
        .setCancelable(false)
        .setPositiveButton(R.string.request_again) { _, _ ->
            // Send the request again.
            request.send()
        }
        .setNegativeButton(android.R.string.cancel){_,_ ->
            exitProcess(-1)
        }
        .show().setCanceledOnTouchOutside(true)
}

@SuppressLint("StringFormatInvalid")
internal fun Context.showPermanentlyDeniedDialog(permissions: List<PermissionStatus>) {
    val msg = getString(R.string.permanently_denied_permissions, permissions.toMessage<PermissionStatus.Denied.Permanently>())

    AlertDialog.Builder(this)
        .setTitle(R.string.permissions_required)
        .setMessage(msg)
        .setCancelable(false)
        .setPositiveButton(R.string.action_settings) { _, _ ->
            // Open the app's settings.
            val intent = createAppSettingsIntent()
            startActivity(intent)
        }
        .setNegativeButton(android.R.string.cancel, null)
        .show().setCanceledOnTouchOutside(true)
}

private fun Context.createAppSettingsIntent() = Intent().apply {
    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    data = Uri.fromParts("package", packageName, null)
}

private inline fun <reified T : PermissionStatus> List<PermissionStatus>.toMessage(): String = filterIsInstance<T>()
    .joinToString { it.permission }