package com.zettabridge.launcher;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * Compatibility helpers for imported applications that ask Android Settings for package-scoped
 * access. Imported apps share the launcher's real UID/package, so Settings screens must target
 * the launcher package instead of the package name stored inside the imported APK.
 */
final class PermissionCompat {
    private PermissionCompat() {}

    static Intent routeSettingsIntent(Intent original, String pluginPackage, String hostPackage) {
        if (original == null || pluginPackage == null || hostPackage == null) return original;
        if (!isPackageScopedSettingsAction(original.getAction())) return original;

        Uri data = original.getData();
        if (data == null || !"package".equals(data.getScheme())) return original;
        if (!pluginPackage.equals(data.getSchemeSpecificPart())) return original;

        Intent routed = new Intent(original);
        routed.setData(Uri.fromParts("package", hostPackage, null));
        return routed;
    }

    private static boolean isPackageScopedSettingsAction(String action) {
        return Settings.ACTION_MANAGE_OVERLAY_PERMISSION.equals(action)
                || Settings.ACTION_MANAGE_WRITE_SETTINGS.equals(action)
                || Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES.equals(action)
                || Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS.equals(action)
                || Settings.ACTION_APPLICATION_DETAILS_SETTINGS.equals(action);
    }
}
