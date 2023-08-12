package com.ebicep.warlords.util.warlords;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigUtil {


    public static void loadConfigs(Warlords instance) {
        readKeysConfig(instance);
        readWeaponConfig(instance);
        saveWeaponConfig(instance);
        readBotConfig(instance);
    }

    public static void readKeysConfig(Warlords instance) {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "keys.yml"));
            ApplicationConfiguration.key = config.getString("database_key");
            BotManager.botToken = config.getString("botToken");
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }
    }

    public static void readWeaponConfig(Warlords instance) {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "weapons.yml"));
            for (String key : config.getKeys(false)) {
                Weapons.getWeapon(key).isUnlocked = config.getBoolean(key);
            }
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
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
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }
    }

    public static void readBotConfig(Warlords instance) {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "bot.yml"));
            for (String key : config.getKeys(false)) {
                BotManager.DiscordServer discordServer = new BotManager.DiscordServer(
                        key,
                        config.getString(key + ".id"),
                        config.getString(key + ".statusChannel"),
                        config.getString(key + ".queueChannel")
                );
                BotManager.DISCORD_SERVERS.add(discordServer);
                ChatUtils.MessageType.DISCORD_BOT.sendMessage("Added server " + key + " = " + discordServer.getId() + ", " + discordServer.getStatusChannel() + ", " + discordServer.getQueueChannel());
            }
            /*
            server1
                id
                statusChannel
                waitingChannel
            server2
                id
                statusChannel
                waitingChannel
             */
        } catch (Exception e) {
            ChatUtils.MessageType.DISCORD_BOT.sendErrorMessage(e);
        }
    }
}
