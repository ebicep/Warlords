package com.ebicep.warlords.util.warlords;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class ConfigUtil {


    public static void loadConfigs(Warlords instance) {
        ChatUtils.MessageType.CONFIG.sendMessage("Loading file configs...");
        readKeysConfig(instance);
        readBotConfig(instance);
        new BukkitRunnable() {
            @Override
            public void run() {
                readWeaponConfig(instance);
                saveWeaponConfig(instance);
            }
        }.runTaskAsynchronously(instance);
    }

    public static void readKeysConfig(Warlords instance) {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "keys.yml"));
            ApplicationConfiguration.key = config.getString("database_key");
            BotManager.botToken = config.getString("botToken");
            ChatUtils.MessageType.CONFIG.sendMessage("Loaded file keys config.");
        } catch (Exception e) {
            ChatUtils.MessageType.DISCORD_BOT.sendErrorMessage(e);
        }
    }

    public static void readBotConfig(Warlords instance) {
        try {
            BotManager.DISCORD_SERVERS.clear();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "bot.yml"));
            for (String key : config.getKeys(false)) {
                String guildId = config.getString(key + ".id");
                if (guildId == null || guildId.isEmpty()) {
                    ChatUtils.MessageType.DISCORD_BOT.sendMessage("WARN: bot.yml guild '" + key + "' is missing id");
                    continue;
                }
                java.util.Map<BotManager.BotChannel, String> channels = new java.util.EnumMap<>(BotManager.BotChannel.class);
                for (BotManager.BotChannel botChannel : BotManager.BotChannel.values()) {
                    String channelName = config.getString(key + ".channels." + botChannel.getConfigKey());
                    if (channelName != null && !channelName.isEmpty()) {
                        channels.put(botChannel, channelName);
                    }
                }
                BotManager.DiscordServer discordServer = new BotManager.DiscordServer(key, guildId, channels);
                BotManager.DISCORD_SERVERS.add(discordServer);
                ChatUtils.MessageType.DISCORD_BOT.sendMessage("Added server " + key + " = " + guildId + ", channels=" + channels);
            }
            ChatUtils.MessageType.DISCORD_BOT.sendMessage("Loaded file bot config.");
        } catch (Exception e) {
            ChatUtils.MessageType.DISCORD_BOT.sendErrorMessage(e);
        }
    }

    public static void reloadBotConfig(Warlords instance) {
        readBotConfig(instance);
        if (BotManager.jda != null) {
            for (BotManager.DiscordServer discordServer : BotManager.DISCORD_SERVERS) {
                discordServer.rebindGuild(BotManager.jda);
            }
        }
    }

    public static void readWeaponConfig(Warlords instance) {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "weapons.yml"));
            for (String key : config.getKeys(false)) {
                Weapons.getWeapon(key).isUnlocked = config.getBoolean(key);
            }
            ChatUtils.MessageType.DISCORD_BOT.sendMessage("Loaded file weapon config.");
        } catch (Exception e) {
            ChatUtils.MessageType.DISCORD_BOT.sendErrorMessage(e);
        }
    }

    public static void saveWeaponConfig(Warlords instance) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            for (Weapons weapons : Weapons.VALUES) {
                config.set(weapons.getName(), weapons.isUnlocked);
            }
            config.save(new File(instance.getDataFolder(), "weapons.yml"));
        } catch (Exception e) {
            ChatUtils.MessageType.DISCORD_BOT.sendErrorMessage(e);
        }
    }

}
