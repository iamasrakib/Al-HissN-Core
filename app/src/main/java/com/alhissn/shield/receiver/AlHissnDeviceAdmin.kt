/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.shield.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlHissnDeviceAdmin : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        // iamasrakib was here
        super.onEnabled(context, intent)
        Toast.makeText(context, "Uninstall Protection Enabled", Toast.LENGTH_SHORT).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(context, "Uninstall Protection Disabled", Toast.LENGTH_SHORT).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        return "Disabling Uninstall Protection puts your security filters at risk!"
    }
}


