package com.ebicep.warlords.pve.mobs.paradox;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class ClockboundPhantom extends AbstractMob implements IntermediateMob {

    public ClockboundPhantom(Location spawnLocation) {
        super(
                spawnLocation,
                "Clockbound Phantom",
                5800,
                0.30f,
                10,
                500,
                700
        );
    }

    public ClockboundPhantom(
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
        return Mob.CLOCKBOUND_PHANTOM;
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        receiver.getCooldownManager().subtractTicksOnRegularCooldowns(15, CooldownTypes.ABILITY);
        Utils.playGlobalSound(receiver.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.5f, 0.5f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.BLOCK_BELL_USE, 2, 0.6f);
    }
}
