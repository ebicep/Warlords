package com.ebicep.warlords.pve.mobs.paradox;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class ChronoWarden extends AbstractMob implements IntermediateMob {

    public ChronoWarden(Location spawnLocation) {
        super(
                spawnLocation,
                "Chrono Warden",
                8500,
                0.28f,
                15,
                350,
                500
        );
    }

    public ChronoWarden(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.CHRONO_WARDEN;
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        receiver.addSpeedModifier(attacker, "Chrono Bind", -30, 2 * 20);
        Utils.playGlobalSound(receiver.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2, 0.6f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.BLOCK_BELL_RESONATE, 2, 0.8f);
    }
}
