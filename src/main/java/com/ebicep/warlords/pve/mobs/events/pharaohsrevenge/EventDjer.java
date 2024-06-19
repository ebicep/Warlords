package com.ebicep.warlords.pve.mobs.events.pharaohsrevenge;

import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddVelocityEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;

public class EventDjer extends AbstractMob implements BossMinionMob {

    private final HashSet<String> skillsImmuneTo = new HashSet<>() {{
        add("Seismic Wave");
        add("Ground Slam");
        add("Last Stand");
        add("Boulder");
        add("Earthen Spike");
        add("Lightning Rod");
        add("Water Breath");
    }};

    public EventDjer(Location spawnLocation) {
        super(spawnLocation,
                "Djer",
                9000,
                0.32f,
                10,
                1100,
                1310,
                new GroundShred()
        );
    }

    public EventDjer(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new GroundShred()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_NARMER_DJER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        int currentWave = option.getWaveCounter();
        if (currentWave % 5 == 0 && currentWave > 5) {
            float additionalHealthMultiplier = 1 + .15f * (currentWave / 5f - 1);
            warlordsNPC.setMaxHealthAndHeal(warlordsNPC.getMaxBaseHealth() * additionalHealthMultiplier);
        }
        option.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onVelocity(WarlordsAddVelocityEvent event) {
                if (!event.getWarlordsEntity().equals(warlordsNPC)) {
                    return;
                }
                if (aboveHealthThreshold()) {
                    return;
                }
                if (!skillsImmuneTo.contains(event.getFrom())) {
                    return;
                }
                event.setCancelled(true);
            }

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!event.getWarlordsEntity().equals(warlordsNPC)) {
                    return;
                }
                if (aboveHealthThreshold()) {
                    return;
                }
                if (!skillsImmuneTo.contains(event.getCause())) {
                    return;
                }
                event.setMin(event.getMin() * .75f);
                event.setMax(event.getMax() * .75f);
            }

        });
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

    private boolean aboveHealthThreshold() {
        return !(warlordsNPC.getCurrentHealth() <= warlordsNPC.getMaxBaseHealth() * .75);
    }

    private static class GroundShred extends AbstractPveAbility implements Damages<GroundShred.DamageValues> {

        private final int earthQuakeRadius = 12;

        public GroundShred() {
            super("Ground Shred", 5, 50);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            Location loc = wp.getLocation();
            Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.4f);
            EffectUtils.strikeLightning(loc, false);
            EffectUtils.playSphereAnimation(loc, earthQuakeRadius, Particle.SPELL_WITCH, 2);
            EffectUtils.playHelixAnimation(loc, earthQuakeRadius, Particle.FIREWORKS_SPARK, 2, 40);
            List<WarlordsPlayer> warlordsPlayers = PlayerFilterGeneric
                    .entitiesAround(wp, earthQuakeRadius, earthQuakeRadius, earthQuakeRadius)
                    .aliveEnemiesOf(wp)
                    .warlordsPlayers()
                    .stream()
                    .toList();
            for (WarlordsPlayer warlordsPlayer : warlordsPlayers) {
                Utils.addKnockback(name, loc, warlordsPlayer, -2.5, 0.25);
                warlordsPlayer.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .value(damageValues.groundShredDamage)
                );
            }
            new GameRunnable(pveOption.getGame()) {

                @Override
                public void run() {
                    for (WarlordsPlayer warlordsPlayer : warlordsPlayers) {
                        warlordsPlayer.stun();
                    }
                }
            }.runTaskLater(30);
            new GameRunnable(pveOption.getGame()) {

                @Override
                public void run() {
                    for (WarlordsPlayer warlordsPlayer : warlordsPlayers) {
                        warlordsPlayer.unstun();
                    }
                }
            }.runTaskLater(50);
            return true;
        }

        private final DamageValues damageValues = new DamageValues();

        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.RangedValue groundShredDamage = new Value.RangedValue(920, 1080);
            private final List<Value> values = List.of(groundShredDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }

    }
}
