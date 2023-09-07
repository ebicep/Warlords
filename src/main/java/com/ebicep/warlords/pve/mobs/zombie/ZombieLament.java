package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

public class ZombieLament extends AbstractZombie implements AdvancedMob {

    public ZombieLament(Location spawnLocation) {
        super(
                spawnLocation,
                "Zombie Lament",
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.BLUE_GHOST),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 69, 176),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 69, 176),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 69, 176),
                        Weapons.SILVER_PHANTASM_STAFF_2.getItem()
                ),
                3500,
                0.38f,
                10,
                300,
                500
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        receiver.subtractEnergy(5, true);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playFirework(
                deathLocation,
                FireworkEffect.builder()
                   .withColor(Color.BLUE)
                   .with(FireworkEffect.Type.BURST)
                   .withTrail()
                   .build(),
                1
        );
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ZOMBIE_DEATH, 2, 0.4f);
    }
}