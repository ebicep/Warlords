package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class EventEggSac extends AbstractMob implements BossMinionMob {

    public static boolean ARMOR_STAND = true;

    public EventEggSac(Location spawnLocation) {
        this(spawnLocation, "Egg Sac", 10000, 0, 0, 0, 0);
    }

    public EventEggSac(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_MITHRA_EGG_SAC;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        if (ARMOR_STAND) {
            warlordsNPC.getEntity().remove();
            ArmorStand eggSac = Utils.spawnArmorStand(warlordsNPC.getLocation().clone().add(0, -1.3, 0), armorStand -> {
                armorStand.getEquipment().setHelmet(new ItemStack(Material.DRAGON_EGG));
                armorStand.customName(Component.text(name));
            });
            warlordsNPC.setEntity(eggSac);
            warlordsNPC.updateEntity();
        }
    }

}
