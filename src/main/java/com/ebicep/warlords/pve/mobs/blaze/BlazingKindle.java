package com.ebicep.warlords.pve.mobs.blaze;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.List;

public class BlazingKindle extends AbstractMob implements IntermediateMob {

    private static final double kindleRadius = 6;

    public BlazingKindle(Location spawnLocation) {
        super(
                spawnLocation,
                "Blazing Kindle",
                4000,
                0,
                10,
                100,
                200,
                new KindleWave()
        );
    }

    public BlazingKindle(
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
                maxMeleeDamage,
                new KindleWave()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.BLAZING_KINDLE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), kindleRadius, Particle.FLAME, 1, 20);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 2, 0.5f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 2, 0.2f);
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), kindleRadius, Particle.FLAME, 1, 10);
    }

    private static class KindleWave extends AbstractAbility implements Damages<KindleWave.DamageValues> {

        public KindleWave() {
            super("Kindle Wave", 518, 805, 8, 100);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            Location loc = wp.getLocation();
            EffectUtils.playSphereAnimation(loc, kindleRadius, Particle.FLAME, 1);
            Utils.playGlobalSound(loc, "mage.inferno.activation", 2, 0.2f);
            new FallingBlockWaveEffect(
                    loc,
                    kindleRadius,
                    1.2,
                    Material.FIRE
            ).play();

            for (WarlordsEntity target : PlayerFilter
                    .entitiesAround(wp, kindleRadius, kindleRadius, kindleRadius)
                    .aliveEnemiesOf(wp)
            ) {
                target.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .value(damageValues.kindleWaveDamage)
                        .flags(InstanceFlags.TD_MAGIC)
                );
            }
            return true;
        }


        private final DamageValues damageValues = new DamageValues();

        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.RangedValue kindleWaveDamage = new Value.RangedValue(518, 805);
            private final List<Value> values = List.of(kindleWaveDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }
}
