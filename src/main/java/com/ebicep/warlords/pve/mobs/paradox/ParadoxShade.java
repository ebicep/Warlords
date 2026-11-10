package com.ebicep.warlords.pve.mobs.paradox;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class ParadoxShade extends AbstractMob implements IntermediateMob {

    public ParadoxShade(Location spawnLocation) {
        super(
                spawnLocation,
                "Paradox Shade",
                6000,
                0.42f,
                10,
                400,
                550
        );
    }

    public ParadoxShade(
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
        return Mob.PARADOX_SHADE;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 10 == 0) {
            EffectUtils.displayParticle(Particle.SMOKE, warlordsNPC.getLocation().add(0, 1, 0), 4, .2, .4, .2, .01);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(receiver.getLocation(), Sound.ENTITY_PHANTOM_BITE, 1.5f, 0.8f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_PHANTOM_DEATH, 2, 0.7f);
    }
}
