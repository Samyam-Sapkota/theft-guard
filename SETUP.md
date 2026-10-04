# Theft Guard — Setup Guide

This guide walks you through installing and configuring Theft Guard on your own
Android phone. It is written so a non-technical owner can follow it, with extra
sections for power users who want the most capable setup.

> **One-line summary:** install the app, set the checklist to green, add the
> number(s) that should receive your location, and test it. Everything beyond
> that (Shizuku, email, USB setup) is optional and only makes it *more* capable.

---

## 1. Install the app

1. Download the latest `app-release.apk` (or `app-debug.apk`) from the
   [Releases](../../releases) page, or build it yourself (see
   [README → Build](README.md#build)).
2. Open the APK on your phone and tap **Install**.
3. Android (and Google Play Protect) will warn you about installing an app from
   outside the Play Store. This is normal for any sideloaded app — see
   [Why Play Protect warns you](#why-play-protect-warns-you) below for what the
   warning means and how to proceed.

---

## 2. Choose your capability tier

Theft Guard works at three levels. Pick the one that matches how much setup you
are willing to do. **Even the simplest tier gives you a loud alarm and a
location text** — the higher tiers just make the location more reliable and add
email delivery.

| | Tier 1 — Basic | Tier 2 — ADB granted | Tier 3 — Shizuku |
|---|---|---|---|
| Setup effort | Tap through checklist | One USB command | Shizuku app + start once per boot |
| Loud unstoppable alarm | ✅ | ✅ | ✅ |
| Location by **SMS** to trusted numbers | ✅ | ✅ | ✅ |
| Grant all permissions in one step | ❌ (manual) | ✅ | ✅ |
| Turn **mobile data / Wi-Fi / location ON** automatically when stolen | ❌ | ❌ | ✅ |
| Deliver location by **email** | ❌ | ❌ | ✅ |
| Location accuracy | Depends on whether location was already on | Same | Best — location is forced on |

### Why location accuracy differs

A normal Android app **cannot turn your location on by itself**. So:

- **Without Shizuku (Tiers 1 & 2):** if you already keep **Location ON**, the
  app gets a real GPS fix and texts accurate coordinates. If location happens to
  be **off** when the phone is stolen, the app can only send the **last known
  position** (which may be old/approximate) or a note that location was off.
  It still sends *something* to your trusted number — it is just less accurate.
- **With Shizuku (Tier 3):** the app flips **location, mobile data and Wi-Fi ON**
  the instant the "Stolen" text arrives, so it can get a fresh, accurate GPS fix
  and reach the internet for email. This is the most reliable setup.

**Recommendation:** keep **Location turned on** at all times if you can — it is
the single biggest thing that makes the location accurate, with or without
Shizuku.

---

## 3. Configure the app (all tiers)

Open **Theft Guard** and complete the on-screen checklist:

1. **Secure screen lock** *(required)* — a PIN, pattern, password or fingerprint.
   Without it, anyone can "unlock" the phone and silence the alarm.
2. **SMS & notification permission** *(required)*.
3. **Trusted numbers** *(required)* — the phone number(s) that should receive
   your location when the phone is reported stolen. You can add more than one.
4. **Location permission** — choose **Allow all the time** *(required for the
   location text)*.
5. **Location turned on** *(strongly recommended — see accuracy note above)*.
6. **Full-screen alarm** *(required on Android 14+)*.
7. **Ignore battery optimisation** *(required)* — stops Android from delaying the
   alarm. (Theft Guard uses no battery when idle; this only matters at alarm time.)
8. **Lock screen on alarm** *(recommended)* — the device-admin feature; locks the
   phone the moment the alarm fires.
9. **Block volume buttons** *(recommended)* — turn on *Theft Guard volume lock*
   in **Settings → Accessibility**.
10. **Override Do Not Disturb** *(recommended)*.

> On Android 13+, a sideloaded app must first be allowed to use sensitive
> settings: **App info → ⋮ (top-right) → Allow restricted settings**. Do this
> before granting the SMS permission and the accessibility service.

### Test it

Use **Test alarm** in the app. To stop the test, **unlock the phone**. If device
admin is off, press the power button to lock, then unlock.

### How you trigger it for real

From any other phone, text the exact word:

```
Stolen
```

to your lost phone (case-sensitive — `stolen` or `Stolen phone` are ignored).
The alarm starts and your location is sent **once**. Text `Stolen` again to get
an updated location.

---

## 4. Tier 2 — Grant everything over USB (for tech users)

If you have a computer and a USB cable, you can grant every permission in one
command instead of tapping through Settings.

1. On the phone: **Settings → About phone**, tap **Build number** 7 times, then
   **Settings → Developer options → USB debugging** (on).
2. Install the app, connect USB, accept the **Allow USB debugging?** prompt.
3. Run the helper script for your OS (needs Android `platform-tools` / `adb`):
   - macOS / Linux: [`tools/adb-setup.sh`](tools/adb-setup.sh)
   - Windows: [`tools/adb-setup.bat`](tools/adb-setup.bat)

**What ADB can do:** grant SMS, background location, notifications, full-screen
alarm, battery-optimisation exemption, device admin and the volume-lock service —
and get past the "Allow restricted settings" gate automatically.

**What ADB cannot do:** let the *app itself* turn mobile data on when the phone
is stolen. That needs a privileged, signature-level permission
(`MODIFY_PHONE_STATE`) that only the phone maker or carrier can grant. ADB can
turn data on once, manually (`adb shell svc data enable`), which is only useful
for testing. For automatic connectivity, use Shizuku (Tier 3).

You still set a screen lock yourself and keep Location turned on.

---

## 5. Tier 3 — Shizuku (auto-enable data, Wi-Fi, location + email)

[Shizuku](https://shizuku.rikka.app/) lets an app run a few shell-privileged
actions at runtime. Theft Guard uses it to switch **mobile data, Wi-Fi and
location ON** the moment the "Stolen" text arrives, and to send your location by
**email** (which needs internet).

- **Official website:** https://shizuku.rikka.app/
- **Official source & releases (GitHub):** https://github.com/RikkaApps/Shizuku

> Install Shizuku only from its official sources above. Theft Guard talks to it
> through a tiny service that exposes **only three fixed switches** (enable
> mobile data, Wi-Fi, location) and accepts no arbitrary commands — it is not a
> general remote shell.

### Starting Shizuku

Shizuku needs to be started once. On a **non-rooted** phone it is started over
ADB — either:

- **Wireless debugging** (on-device, no computer needed after first setup), or
- **USB debugging** from a computer.

On a **rooted** phone, Shizuku can start itself.

Follow Shizuku's own on-screen instructions for your Android version (its app
walks you through wireless or USB start).

> **Important caveat:** on a non-rooted phone, Shizuku **stops when the phone
> reboots** and must be started again. Until it is restarted, the auto-enable of
> data/Wi-Fi/location won't fire. The SMS alarm and location-by-SMS keep working
> regardless, so the app is never *dependent* on Shizuku — Shizuku only upgrades
> it.

### Turn it on in Theft Guard

1. Install and start Shizuku.
2. In Theft Guard's checklist, tap **Auto-enable data & location** and grant
   Theft Guard access when Shizuku prompts.

### Email delivery (requires Shizuku)

Email is offered only when Shizuku is granted, because it needs the data/Wi-Fi
that Shizuku turns on. In setup, enter:

- **SMTP host** (e.g. `smtp.gmail.com`) and **port**
- **Sender address** and an **app password** (not your normal password — most
  providers require an app-specific password for SMTP)
- **Recipient address** that should receive the location email

When the phone is stolen, the app waits for the network Shizuku brought up, then
emails the Google Maps link, accuracy, time and battery level to that recipient.

---

## Why Play Protect warns you

When you sideload Theft Guard, Google Play Protect may warn you or try to block
the install. **This is expected and does not mean the app is malware.** Play
Protect flags apps by their *behaviour*, and Theft Guard deliberately uses
abilities that Google watches closely because they *can* be abused by bad apps:

- reading incoming SMS (`RECEIVE_SMS`) — to detect the "Stolen" trigger,
- an accessibility service — to block the volume buttons during the alarm,
- device admin — to lock the screen when stolen,
- background location — to find the phone.

Theft Guard uses these **only** for the anti-theft features described in this
repo, and the full source is here for you to read. There is no legitimate way to
make the warning disappear by signing the app a certain way — the warning is
about what the app *does*, not who built it. If you trust the app (your own, with
the source in front of you), you can install it:

1. When prompted, choose **Install anyway** / **More details → Install without
   scanning**, or
2. If Play Protect blocked it: **Play Store → profile → Play Protect →
   Settings**, and follow the prompts to allow this specific install.

If you would rather not change any Play Protect setting, the alternative is to
distribute through Google Play with the proper permission declarations — that is
a separate, official process and is the only way to remove the warning *legitimately*.

---

## Limitations (please read)

- A thief can power the phone **off** or pull the **SIM** before the text
  arrives. The alarm comes back if the phone is powered on again before it is
  unlocked.
- Without Shizuku, the app can't turn **mobile data** or **location** on by
  itself — see the accuracy note in [section 2](#why-location-accuracy-differs).
- Some manufacturers (Xiaomi, Huawei, Oppo, etc.) add their own **autostart** or
  battery restrictions. Allow **autostart** for Theft Guard on those phones.
- iOS is not supported: Apple does not let apps read incoming SMS.

---

See also: [README.md](README.md) for an overview and
[PERMISSIONS.md](PERMISSIONS.md) for exactly what each permission is used for.
