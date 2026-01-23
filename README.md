# ⏱️ Timing

A comprehensive server timing management plugin for Paper/Folia servers. Manage server opening/closing with countdown timers, customizable MOTD, and scheduled announcements.

## ✨ Features

- **Beginning Timer** - Countdown timer for server opening with automatic whitelist disable
- **Restart Timer** - Countdown timer for server shutdown with player kick
- **End Timer** - Countdown timer for End dimension opening
- **Custom MOTD Editor** - GUI-based MOTD editor with MiniMessage formatting support
- **Announcement System** - Create and schedule announcements (Action Bar, Title, Subtitle)
- **Timer Persistence** - Beginning and End timers resume after server restart
- **Folia Support** - Native support for Folia's region-based multithreading

## 📥 Installation

1. Download the latest release
2. Place `Timing.jar` in your server's `plugins` folder
3. Restart your server
4. Configure in `plugins/Timing/config.yml`

## 🔧 Commands

| Command | Alias | Description |
|---------|-------|-------------|
| `/beginningtimer <start\|stop\|status> [seconds]` | `/btimer` | Control server opening countdown |
| `/restarttimer <start\|stop\|status> [seconds]` | `/rtimer` | Control server shutdown countdown |
| `/endtimer <start\|stop\|status> [seconds]` | `/etimer` | Control End dimension countdown |
| `/motd [preview\|enable\|disable\|reload]` | - | Open MOTD editor GUI |
| `/announcer [send <name>\|reload]` | - | Open announcer GUI |

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `timing.*` | All Timing permissions | OP |
| `timing.beginningtimer` | Use beginning timer command | OP |
| `timing.restarttimer` | Use restart timer command | OP |
| `timing.endtimer` | Use end timer command | OP |
| `timing.motd` | Use MOTD editor | OP |
| `timing.announcer` | Use announcer | OP |

## ⚙️ Configuration

```yaml
# Custom MOTD Settings (MiniMessage format)
motd:
  enabled: true
  line1: "<gradient:gold:yellow><bold>My Server</bold></gradient>"
  line2: "<gray>Welcome to the server!</gray>"

# Beginning Timer Settings
beginning-timer:
  motd-format: "<red><bold>Server Starting</bold></red>\n<yellow>Starting in: <white>{time}</white></yellow>"
  kick-message: "<red><bold>Server is Starting!</bold></red>\n\n<yellow>The server will open in <white>{time}</white></yellow>"
  disable-whitelist-on-end: true

# Restart Timer Settings
restart-timer:
  motd-format: "<red><bold>Server Stopping</bold></red>\n<yellow>Stopping in: <white>{time}</white></yellow>"
  kick-message: "<red><bold>Server is Stopping!</bold></red>\n\n<yellow>The server will stop in <white>{time}</white></yellow>"
  kick-all-on-end: true
  final-kick-message: "<red><bold>Server Stopped</bold></red>\n\n<gray>Please reconnect shortly!</gray>"

# End Timer Settings
end-timer:
  motd-format: "<light_purple><bold>The End</bold></light_purple>\n<yellow>Opens in: <white>{time}</white></yellow>"

# Announcements (managed via GUI)
announcements:
  example:
    message: "<gradient:gold:yellow>Welcome!</gradient>"
    type: TITLE
    subtitle: "<gray>Enjoy your stay!</gray>"
    interval: 300
    enabled: true
```

## 📋 Supported Versions

- **Minecraft:** 1.21, 1.21.1, 1.21.2, 1.21.3, 1.21.4
- **Java:** 21+
- **Server Software:** Paper, Folia

## 📜 License

This project is licensed under the BSD 3-Clause License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

## 🤝 Partner

<a href="https://emeraldhost.de/frxme">
  <img src="https://cdn.emeraldhost.de/branding/icon/icon.png" width="80" alt="Emerald Host Logo">
</a>

### Powered by Emerald Host

*DDoS-Protection, NVMe Performance und 99.9% Uptime.* *Der Host meines Vertrauens für alle Development-Server.*

<a href="https://emeraldhost.de/frxme">
  <img src="https://img.shields.io/badge/Code-Frxme10-10b981?style=for-the-badge&logo=gift&logoColor=white&labelColor=0f172a" alt="Use Code Frxme10 for 10% off">
</a>

</div>

---
