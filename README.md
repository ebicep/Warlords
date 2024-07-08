# Warlords 2.0 - 1.20.2

Warlords 2.0 is a remake of the infamous Warlords minigame available on the Hypixel Network.

Our version holds various QoL improvements, new classes, new maps, and balance changes.

Project built upon the frameworks of Paper.

You may use our work for your own purposes as long as we are credited.

# Links

- Discord: https://discord.gg/tkGFQvzwAd
- Server IP: compwl.apexmc.co

# Requirements

- A world named `MainLobby`. `autoLoad` should be set to true inside its MultiVerse config.

# Dependencies

- Paper 1.20.2 ([Latest 1.20.2](https://papermc.io/downloads/all))
- Multiverse Core ([Latest](https://github.com/Multiverse/Multiverse-Core/releases))
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
