# Permissions — what each one is for

Theft Guard requests only what its anti-theft features need. Every permission
below maps to a specific behaviour you can find in the source. Nothing here is
used for analytics, ads, tracking, or sending your data anywhere except to the
trusted number/email **you** configure.

| Permission | Why the app needs it |
|---|---|
| `RECEIVE_SMS` | Detect the incoming trigger text (`Stolen`). This is the one Google watches most closely; it is the core of the app. |
| `SEND_SMS` | Text your location to the trusted number(s) you added. Works with no mobile data. |
| `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` | Get the phone's GPS/network position to report where it is. |
| `ACCESS_BACKGROUND_LOCATION` | Get a location while the app is not open (the phone is stolen, so it won't be). Requires choosing **Allow all the time**. |
| `moe.shizuku.manager.permission.API_V23` | Talk to [Shizuku](https://github.com/RikkaApps/Shizuku), *if you set it up*, to turn data/Wi-Fi/location on when stolen. No effect if Shizuku isn't installed. |
| `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_SPECIAL_USE` | Keep the alarm running reliably once triggered (Android requires a foreground service for this). |
| `POST_NOTIFICATIONS` | Show the ongoing alarm notification and launch the full-screen alarm. |
| `USE_FULL_SCREEN_INTENT` | Show the alarm screen over the lock screen (Android 14+ requires this permission). |
| `WAKE_LOCK` | Keep the CPU awake while the siren is sounding so it doesn't get suspended mid-alarm. |
| `MODIFY_AUDIO_SETTINGS` | Force the alarm/media volume to maximum and keep it there. |
| `ACCESS_NOTIFICATION_POLICY` | Temporarily switch off Do Not Disturb during the alarm, then restore it. |
| `RECEIVE_BOOT_COMPLETED` | Restart the alarm after a reboot if the phone hasn't been unlocked yet (a thief rebooting shouldn't silence it). |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Ask to be exempt from battery optimisation so Android doesn't delay the alarm. The app is idle (no drain) until a `Stolen` text arrives. |

## Optional elevated access (not Android permissions)

These are granted by you through Settings, not in the manifest, and the app
works without them (they are marked *recommended*, not *required*):

- **Accessibility service (`VolumeLockService`)** — blocks the hardware volume
  buttons during the alarm, including on the lock screen.
- **Device admin (`AdminReceiver`)** — locks the screen the instant the alarm
  fires, so only your PIN/pattern/fingerprint can stop it.

## Battery behaviour

When no `Stolen` text has arrived, Theft Guard runs **no background service** and
does no polling — it is effectively dormant and uses no measurable battery. The
only time it is active (and intentionally power-hungry, with a wake lock and
continuous audio) is while the alarm is sounding, which is exactly when you want
it to be. The volume-enforcement safety-net poll runs about **once per second**
during the alarm (an event-driven observer handles the instant case), keeping
CPU wakeups low even then.
