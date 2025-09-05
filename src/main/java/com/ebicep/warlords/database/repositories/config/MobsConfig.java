package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MobsConfig implements ConfigManager.Config {

    public Document mobsConfig;

    @Override
    public String getName() {
        return "MOBS";
    }

    @Override
    public void load(Document doc) {
        this.mobsConfig = doc;
        readMobConfig(doc);
        validateMobConfig();
    }

    @Override
    public Document getConfigDocument() {
        return this.mobsConfig;
    }

    private void readMobConfig(Document document) {
        for (Map.Entry<String, Object> entry : document.get("pve", Document.class).entrySet()) {
            String mobEnumName = entry.getKey();
            Document mobConfig = (Document) entry.getValue();
            try {
                Mob mob = Mob.valueOf(mobEnumName);
                mob.name = mobConfig.getString("name");
                Number maxHealthNum = mobConfig.get("max_health", Number.class);
                mob.maxHealth = maxHealthNum != null ? maxHealthNum.intValue() : 0;
                Number walkSpeedNum = mobConfig.get("walk_speed", Number.class);
                mob.walkSpeed = walkSpeedNum != null ? walkSpeedNum.floatValue() : 0f;
                Number drNum = mobConfig.get("damage_resistance", Number.class);
                mob.damageResistance = drNum != null ? drNum.intValue() : 0;
                Number minMeleeNum = mobConfig.get("min_melee_damage", Number.class);
                mob.minMeleeDamage = minMeleeNum != null ? minMeleeNum.floatValue() : 0f;
                Number maxMeleeNum = mobConfig.get("max_melee_damage", Number.class);
                mob.maxMeleeDamage = maxMeleeNum != null ? maxMeleeNum.floatValue() : 0f;
            } catch (IllegalArgumentException | NullPointerException e) {
                ChatUtils.MessageType.CONFIG.sendErrorMessage("Mob " + mobEnumName + " does not exist!");
            } catch (Exception e) {
                ChatUtils.MessageType.CONFIG.sendErrorMessage("Problem loading mob " + mobEnumName);
                ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
            }
        }
    }

    public void validateMobConfig() {
        List<Mob> missingMob = new ArrayList<>();
        for (Mob mob : Mob.VALUES) {
            if (mob.name == null) {
                missingMob.add(mob);
            }
        }
        if (missingMob.isEmpty()) {
            return;
        }
        ChatUtils.MessageType.CONFIG.sendErrorMessage("Missing Mobs: " + missingMob.stream().map(mob -> mob.name).collect(Collectors.joining(", ")));
        ChatUtils.MessageType.CONFIG.sendMessage("Automatically adding mobs to config...");
        try {
            Document missingMobsDocument = new Document();
            missingMob.forEach(value -> {
                AbstractMob mob = value.createMobLegacy.apply(null);
                Document mobObject = new Document()
                        .append("name", value.name = mob.getName())
                        .append("max_health", value.maxHealth = mob.getMaxHealth())
                        .append("walk_speed", value.walkSpeed = mob.getWalkSpeed())
                        .append("damage_resistance", value.damageResistance = mob.getPlayerClass().getDamageResistance())
                        .append("min_melee_damage", value.minMeleeDamage = mob.getMinMeleeDamage())
                        .append("max_melee_damage", value.maxMeleeDamage = mob.getMaxMeleeDamage());
                missingMobsDocument.put(value.name(), mobObject);
            });
            ChatUtils.MessageType.CONFIG.sendMessage("Missing mobs to add: " + missingMobsDocument.toJson());
        } catch (Exception e) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Problem writing missing mobs config");
            ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
        }
    }

}
