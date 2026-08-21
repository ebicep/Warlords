# Warlords 2.0 - 1.21.4

Warlords 2.0 is a remake of the infamous Warlords minigame available on the Hypixel Network.

Our version holds various QoL improvements, new classes, new maps, and balance changes.

Project built upon the frameworks of Paper.

You may use our work for your own purposes as long as we are credited.

# Links

- Discord: https://discord.gg/tkGFQvzwAd
- Server IP: 46.202.179.172

# Requirements

- A world folder named `MainLobby` under the server world container (Warlords loads it via Bukkit at startup). Map instances use folders named `fileName-0`, `fileName-1`, etc., and are loaded on demand.

# Dependencies

- Paper 1.21.4 ([Latest 1.21.4](https://papermc.io/downloads/all))
- Holographic Displays ([Latest](https://www.curseforge.com/minecraft/bukkit-plugins/holographic-displays/files/all?page=1&pageSize=20))
- Citizens ([Latest](https://ci.citizensnpcs.co/job/Citizens2/))
- ProtocolLib ([Latest](https://www.spigotmc.org/resources/protocollib.1997/updates))
- LuckPerms ([Latest Bukkit](https://luckperms.net/download))
- LibDisguises ([Latest](https://www.spigotmc.org/resources/libs-disguises-free.81/updates))

# Config

### /plugins/Warlords

`keys.yml` - Contains the keys for the plugin.

- `database_key` - MongoDB srv connection string.
- `botToken` - Discord bot token to post the status of the server inside a discord channel named "server-status".

`mobs.json` - Contains basic mob values for PvE. Running "/mobs tojson" will generate this file with the default values.

`weapons.yml` - Contains the unlocked weapon skins usable by players.
