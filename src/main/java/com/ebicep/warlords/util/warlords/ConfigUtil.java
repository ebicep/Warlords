package com.ebicep.warlords.util.warlords;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.Map;

public class ConfigUtil {


    public static void loadConfigs(Warlords instance) {
        readKeysConfig(instance);
        readBotConfig(instance);
        new BukkitRunnable() {
            @Override
            public void run() {
                readWeaponConfig(instance);
                saveWeaponConfig(instance);
                try {
                    readMobConfig(instance);
                    Mob.validateMobConfig();
                } catch (Exception e) {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
                }
            }
        }.runTaskAsynchronously(instance);
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

    public static void readMobConfig(Warlords instance) throws FileNotFoundException {
        File file = new File(instance.getDataFolder(), "mobs.json");
        JsonObject mobJson = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : mobJson.entrySet()) {
            String mobEnumName = stringJsonElementEntry.getKey();
            JsonObject mobConfig = stringJsonElementEntry.getValue().getAsJsonObject();
            try {
                Mob mob = Mob.valueOf(mobEnumName);
                mob.name = mobConfig.get("name").getAsString();
                mob.maxHealth = mobConfig.get("max_health").getAsInt();
                mob.walkSpeed = mobConfig.get("walk_speed").getAsFloat();
                mob.damageResistance = mobConfig.get("damage_resistance").getAsInt();
                mob.minMeleeDamage = mobConfig.get("min_melee_damage").getAsFloat();
                mob.maxMeleeDamage = mobConfig.get("max_melee_damage").getAsFloat();
            } catch (IllegalArgumentException | NullPointerException e) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Mob " + mobEnumName + " does not exist!");
            }
        }
    }

    public static void saveMobConfig(Warlords instance) {
        try {
            File file = new File(instance.getDataFolder(), "mobs.json");
            PrintWriter printWriter = new PrintWriter(new FileWriter(file));
            //TODO
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }
    }


}
