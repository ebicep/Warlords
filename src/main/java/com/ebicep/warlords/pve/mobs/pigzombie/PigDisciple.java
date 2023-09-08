package com.ebicep.warlords.pve.mobs.pigzombie;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class PigDisciple extends AbstractPigZombie implements BasicMob {

    public PigDisciple(Location spawnLocation) {
        super(
                spawnLocation,
                "Pig Disciple",
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SAMURAI),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        Weapons.SILVER_PHANTASM_TRIDENT.getItem()
                ),
                2800,
                0.42f,
                0,
                250,
                350
        );
    }

    public PigDisciple(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SAMURAI),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        Weapons.SILVER_PHANTASM_TRIDENT.getItem()
                ),
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

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
