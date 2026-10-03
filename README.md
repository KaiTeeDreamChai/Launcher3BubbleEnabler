# Launcher3BubbleEnabler (Launcher3 气泡小窗全开)

[English](#english) | [中文说明](#中文说明)

---

<a name="中文说明"></a>
## 🇨🇳 中文说明

**Launcher3BubbleEnabler** 是一个专为类原生 Android 系统（AOSP / crDroid / LineageOS / PixelExperience 等）打造的极简、无侵入式 **LSPosed 模块**。

它能够彻底解除桌面启动器（Launcher3）的限制，让**桌面长按所有应用图标（包括网易云音乐、老旧应用等）都能正常显示并启动「气泡小窗」（AOSP Native Bubble）**。

### 🌟 核心特性
- 🚀 **全面解锁**：彻底解决部分应用在桌面上长按不显示“气泡”快捷方式的问题。
- 🛡️ **极简安全**：作用域**严格仅限制在系统桌面（`com.android.launcher3`）**，绝不注入任何第三方 App，对微信、QQ、网易云、银行与各类反作弊环境 **0 侵入、0 封号风险**。
- ⚡ **轻量高效**：运行时仅做常量返回值替换，体积不足 10KB，内存占用与性能开销忽略不计。
- 📱 **广泛适配**：支持 Android 14 / Android 15 / Android 16 / Android 17 (QPR)，兼容搭载 Launcher3 / QuickstepLauncher 的绝大多数类原生 ROM。

---

### 🔍 逆向原理剖析

在 AOSP 原生及衍生 ROM 的系统桌面中，长按应用图标生成快捷方式菜单由 `com.android.launcher3.popup.SystemShortcut` 驱动：

```java
// AOSP Launcher3 内部逻辑
if (itemInfo instanceof ItemInfoWithIcon && ((ItemInfoWithIcon) itemInfo).isNonResizeable()) {
    boolean isPhone = activityContext.getDeviceProfile().getDeviceProperties().isPhone();
    if (!SystemShortcut.systemSupportsNonResizableMultiWindow(context, isPhone)) {
        return null; // 顽固应用在此被直接屏蔽！不生成气泡小窗快捷方式
    }
}
return new SystemShortcut.BubbleShortcut(...);
```

1. **为什么部分应用无法开启？**
   像网易云音乐等应用在 `AndroidManifest.xml` 中硬编码声明了 `resizeableActivity="false"`，导致桌面的 `isNonResizeable()` 检查为 `true`。
2. **手机端默认被禁用**：
   `SystemShortcut.systemSupportsNonResizableMultiWindow` 默认读取系统全局配置 `config_supportsNonResizableMultiWindow`。在手机设备上该值恒定为 `0`（平板/折叠屏才为 1），因此桌面直接返回 `null`，抹除了气泡按钮。
3. **本模块如何生效？**
   - 动态将 `SystemShortcut.systemSupportsNonResizableMultiWindow(...)` 替换为恒定返回 `true`。
   - 动态将 `ItemInfoWithIcon.isNonResizeable()` 替换为恒定返回 `false`。
   - 双重保险，无需修改任何第三方 App 的 APK 即可在桌面畅享气泡小窗。

---

### 📦 安装与使用教程

1. 确保手机已通过 **Magisk / KernelSU / APatch** 安装并激活 **LSPosed**（或 LSPosed_mod / JingMatrix 分支）。
2. 在 [Releases](../../releases) 页面下载最新版 `Launcher3BubbleEnabler-v1.0.0.apk` 并安装。
3. 打开 **LSPosed 管理器**，在模块列表中找到 **「Launcher3 气泡小窗解锁」**。
4. 打开 **「启用模块」** 开关（模块已预设推荐作用域为 **系统桌面 / `com.android.launcher3`**）。
5. 重启桌面（在设置中强行停止 Launcher3，或直接重启手机）。
6. 回到桌面长按任意应用图标（如网易云音乐），即可看到气泡小窗图标已成功显示！

---

<a name="english"></a>
## 🌐 English

**Launcher3BubbleEnabler** is an ultra-lightweight, non-intrusive **LSPosed module** designed for AOSP and custom ROMs (crDroid, LineageOS, PixelExperience, etc.).

It removes Launcher3's hardcoded multi-window restrictions, allowing you to **launch ANY app (including stubborn apps like NetEase Cloud Music) into an AOSP native freeform bubble directly from the home screen long-press popup**.

### ✨ Highlights
- **Universal Bubble Access**: Unlocks the conversation/app bubble shortcut for 100% of apps.
- **Zero Risk & Strict Scope**: Injects **only** into `com.android.launcher3`. Never touches any target apps or banking/anti-cheat apps.
- **Negligible Footprint**: APK size is under 10KB; runtime overhead is essentially zero.
- **Modern Android Support**: Compatible with Android 14, 15, 16, and 17.

### 🛠️ How It Works
AOSP's `Launcher3QuickStep` checks whether an activity declares `resizeableActivity="false"`. If so, and the device is a phone (where `config_supportsNonResizableMultiWindow` is disabled), `Launcher3` intentionally suppresses the `BubbleShortcut`. 

This module hooks:
1. `com.android.launcher3.popup.SystemShortcut.systemSupportsNonResizableMultiWindow(...)` -> returns `true`.
2. `com.android.launcher3.model.data.ItemInfoWithIcon.isNonResizeable()` -> returns `false`.

### 🚀 Usage
1. Install and activate **LSPosed** via Magisk / KernelSU / APatch.
2. Download and install `Launcher3BubbleEnabler-v1.0.0.apk` from [Releases](../../releases).
3. Enable the module in **LSPosed Manager** (scope `com.android.launcher3` is automatically selected).
4. Restart Launcher3 (`adb shell am force-stop com.android.launcher3` or reboot).
5. Long press any app icon on your home screen and enjoy native floating bubbles!

---

### 📄 License
Licensed under the [Apache License, Version 2.0](LICENSE).
