package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class EventEggSac extends AbstractZombie implements BossMob {

    public static boolean ARMOR_STAND = true;

    public EventEggSac(Location spawnLocation) {
        super(
                spawnLocation,
                "Egg Sac",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.EGG_SAC),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
                        null
                ),
                10000,
                0,
                0,
                0,
                0
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        if (ARMOR_STAND) {
            warlordsNPC.getEntity().remove();
            ArmorStand armorStand = warlordsNPC.getWorld().spawn(warlordsNPC.getLocation().clone().add(0, -1.3, 0), ArmorStand.class);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.DRAGON_EGG));
            armorStand.customName(Component.text(name));
            warlordsNPC.setEntity(armorStand);
            warlordsNPC.updateEntity();
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
