# Battery Alert Notification - Testing Guide

## Implementation Summary

The battery alert notification feature has been implemented in `AccountScreen.kt` with the following characteristics:

### How It Works

1. **Toggle Control**: The "Battery Alerts" toggle in the Profile page controls whether notifications are shown
2. **Thresholds**: Notifications appear when battery drops below 20% and 10%
3. **Smart De-duplication**: Won't spam you with repeated notifications - only triggers once when crossing each threshold
4. **Auto-reset**: When battery goes back above the thresholds, the notification flags reset so you'll get alerted again next time

### Key Implementation Details

```kotlin
// Battery monitoring is registered when toggle is enabled
DisposableEffect(batteryAlertsEnabled) {
    var receiver: BroadcastReceiver? = null
    if (batteryAlertsEnabled) {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                // Monitors battery level changes
                val pct = (level * 100f / scale).toInt()
                
                // Shows notification at 20%
                if (pct < 20 && !notifiedBelow20) {
                    showTopToast(context, "⚠️ Battery low: 20%")
                }
                
                // Shows notification at 10%
                if (pct < 10 && !notifiedBelow10) {
                    showTopToast(context, "⚠️ Battery low: 10%")
                }
            }
        }
        // Registers for ACTION_BATTERY_CHANGED broadcasts
        context.registerReceiver(receiver, filter)
        
        // IMPORTANT: Also checks current battery level immediately
        // So if battery is already low when you enable the toggle,
        // you'll see the notification right away
    }
}
```

### Toast Positioning

Toasts appear at the **top center** of the screen (not bottom) for better visibility:
```kotlin
fun showTopToast(context: Context, message: String) {
    val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
    toast.setGravity(android.view.Gravity.TOP or android.view.Gravity.CENTER_HORIZONTAL, 0, 120)
    toast.show()
}
```

## How to Test

### Step 1: Enable Battery Alerts
1. Run the app
2. Navigate to Profile page (bottom right icon)
3. Find "Battery Alerts" card
4. Toggle it **ON** (should turn green)

### Step 2: Test with Emulator Battery Controls

#### Method A: Using Extended Controls (Recommended)
1. Click the **"..." (More)** button in emulator toolbar
2. Go to **Battery** section
3. Set battery level to **100%** first
4. Click "Apply"
5. Check logcat - you should see: `BatteryAlert: Initial battery level: 100%`
6. Now set battery level to **19%**
7. Click "Apply"
8. **Expected**: Toast appears at top saying "⚠️ Battery low: 20%"
9. Set battery level to **9%**
10. Click "Apply"
11. **Expected**: Toast appears at top saying "⚠️ Battery low: 10%"

#### Method B: Using ADB Commands
```bash
# Set battery to 19%
adb shell dumpsys battery set level 19

# Set battery to 9%
adb shell dumpsys battery set level 9

# Reset to normal (unplug)
adb shell dumpsys battery reset
```

### Step 3: Verify Logging

Open **Logcat** in Android Studio and filter by "BatteryAlert":

You should see logs like:
```
D/BatteryAlert: Battery alerts enabled - registering receiver
D/BatteryAlert: Initial battery level: 100%
D/BatteryAlert: Battery level: 19%, lastLevel: 100, notified20: false, notified10: false
D/BatteryAlert: Showing 20% notification
D/BatteryAlert: Battery level: 9%, lastLevel: 19, notified20: true, notified10: false
D/BatteryAlert: Showing 10% notification
```

### Step 4: Test Reset Behavior

1. After seeing the 20% notification
2. Set battery back to **50%**
3. **Expected**: Flag resets (check log: "Battery above 20%, resetting flag")
4. Set battery to **19%** again
5. **Expected**: You should see the 20% notification again

## Troubleshooting

### Toast Not Appearing?

**Check these:**
1. ✅ Is "Battery Alerts" toggle **ON** (green)?
2. ✅ Is battery level actually below 20% or 10%?
3. ✅ Check Logcat for "BatteryAlert" logs - is receiver getting battery changes?
4. ✅ Try toggling the switch OFF then ON again
5. ✅ Navigate away from Profile page and come back

### No Logs Appearing?

**If you don't see any "BatteryAlert" logs:**
1. The toggle might not be working
2. Receiver might not be registered
3. Battery broadcast might not be firing
4. Check that you've imported `android.util.Log`

### Toast Position Wrong?

The toast is positioned using:
```kotlin
toast.setGravity(android.view.Gravity.TOP or android.view.Gravity.CENTER_HORIZONTAL, 0, 120)
```
This places it 120dp from the top, centered horizontally.

## Technical Notes

### Why ACTION_BATTERY_CHANGED?

- It's a **sticky broadcast** - meaning we get the current battery status immediately when registering
- It fires whenever battery level, charging status, or other battery properties change
- No special permissions required

### Lifecycle Safety

- `DisposableEffect` ensures the receiver is properly unregistered when:
  - The composable leaves composition
  - The toggle is turned off
  - The app is closed
- Prevents memory leaks and battery drain

### State Management

- `notifiedBelow20` and `notifiedBelow10` are `remember` state
- They persist across recompositions but reset if the screen is recreated
- `lastBatteryLevel` helps track battery changes for debugging

## Expected Behavior Summary

| Battery Level | Toggle ON | Already Notified 20% | Already Notified 10% | Result |
|---------------|-----------|---------------------|---------------------|---------|
| 100% → 19%    | ✅        | ❌                  | ❌                  | Show "⚠️ Battery low: 20%" |
| 19% → 9%      | ✅        | ✅                  | ❌                  | Show "⚠️ Battery low: 10%" |
| 100% → 9%     | ✅        | ❌                  | ❌                  | Show "⚠️ Battery low: 10%" (also sets 20% flag) |
| 9% → 8%       | ✅        | ✅                  | ✅                  | No notification (already notified) |
| 15% → 25%     | ✅        | ✅                  | ❌                  | No notification (flags reset) |
| 25% → 19%     | ✅        | ❌                  | ❌                  | Show "⚠️ Battery low: 20%" (flags were reset) |
| Any level     | ❌        | Any                 | Any                 | No notification (toggle OFF) |

## Files Modified

- `app/src/main/java/com/example/pawelierapp/screens/AccountScreen.kt`
  - Added battery broadcast receiver with DisposableEffect
  - Added comprehensive logging
  - Added immediate battery status check on toggle enable
  - Added showTopToast helper function

## Testing Checklist

- [ ] Toggle turns green when enabled
- [ ] Logcat shows "Battery alerts enabled - registering receiver"
- [ ] Logcat shows "Initial battery level: X%"
- [ ] Setting battery to 19% shows 20% toast
- [ ] Setting battery to 9% shows 10% toast
- [ ] Toast appears at TOP of screen
- [ ] Setting battery back to 50% resets flags
- [ ] Setting battery to 19% again shows 20% toast again
- [ ] Turning toggle OFF stops notifications
- [ ] No crashes or errors

---

**Status**: Implementation complete with comprehensive logging for debugging ✅

