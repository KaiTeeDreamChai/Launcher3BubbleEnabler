package com.bubble.enabler;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Launcher3BubbleEnabler
 *
 * Forces AOSP Launcher3 / QuickstepLauncher to enable the Bubble shortcut (气泡小窗)
 * for all installed applications when long-pressing an app icon on the home screen.
 *
 * Technical Background:
 * In AOSP Launcher3 (`com.android.launcher3.popup.SystemShortcut`), the Bubble shortcut factory
 * checks:
 * 1. `if (itemInfo instanceof ItemInfoWithIcon && ((ItemInfoWithIcon) itemInfo).isNonResizeable())`
 * 2. `if (!SystemShortcut.systemSupportsNonResizableMultiWindow(context, isPhone))`
 *
 * Stubborn applications (like NetEase Cloud Music) explicitly declare `resizeableActivity="false"`,
 * causing `isNonResizeable()` to return true. On phone devices, `config_supportsNonResizableMultiWindow`
 * defaults to 0 (disabled), so Launcher3 completely suppresses the creation of `BubbleShortcut`.
 *
 * This hook:
 * - Overrides `SystemShortcut.systemSupportsNonResizableMultiWindow` -> true
 * - Overrides `ItemInfoWithIcon.isNonResizeable` -> false
 *
 * Scope: Exclusively targets `com.android.launcher3`. Zero impact on other applications or banking apps.
 */
public class BubbleHook implements IXposedHookLoadPackage {

    private static final String TAG = "[Launcher3BubbleEnabler]";
    private static final String TARGET_PACKAGE = "com.android.launcher3";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        XposedBridge.log(TAG + " Injecting into " + lpparam.packageName + " (" + lpparam.processName + ")");

        // Hook 1: Force SystemShortcut.systemSupportsNonResizableMultiWindow(Context, boolean) to return true
        try {
            Class<?> contextClass = Class.forName("android.content.Context", false, lpparam.classLoader);
            XposedHelpers.findAndHookMethod(
                "com.android.launcher3.popup.SystemShortcut",
                lpparam.classLoader,
                "systemSupportsNonResizableMultiWindow",
                contextClass,
                boolean.class,
                XC_MethodReplacement.returnConstant(true)
            );
            XposedBridge.log(TAG + " Successfully hooked SystemShortcut.systemSupportsNonResizableMultiWindow -> true");
        } catch (Throwable t) {
            XposedBridge.log(TAG + " Failed to hook SystemShortcut.systemSupportsNonResizableMultiWindow: " + t.getMessage());
        }

        // Hook 2: Also force ItemInfoWithIcon.isNonResizeable() to return false as double insurance
        try {
            XposedHelpers.findAndHookMethod(
                "com.android.launcher3.model.data.ItemInfoWithIcon",
                lpparam.classLoader,
                "isNonResizeable",
                XC_MethodReplacement.returnConstant(false)
            );
            XposedBridge.log(TAG + " Successfully hooked ItemInfoWithIcon.isNonResizeable -> false");
        } catch (Throwable t) {
            XposedBridge.log(TAG + " Failed to hook ItemInfoWithIcon.isNonResizeable: " + t.getMessage());
        }
    }
}
