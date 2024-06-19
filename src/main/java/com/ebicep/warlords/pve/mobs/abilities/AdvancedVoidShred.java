package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.List;

public class AdvancedVoidShred extends AbstractAbility {

    private final float voidRadius;
    private final int slowness;
    private final int helixDots;

    public AdvancedVoidShred(float minDamageHeal, float maxDamageHeal, float cooldown, int slowness, float voidRadius, int helixDots) {
        super("Void Shred", minDamageHeal, maxDamageHeal, cooldown, 50);
        this.voidRadius = voidRadius;
        this.slowness = slowness;
        this.helixDots = helixDots;
        this.damageValues = new DamageValues(maxDamageHeal, maxDamageHeal);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        EffectUtils.playHelixAnimation(wp.getLocation(), voidRadius, Particle.SMOKE_NORMAL, 1, helixDots);
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(wp, voidRadius, voidRadius, voidRadius)
                .aliveEnemiesOf(wp)
        ) {
            enemy.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .value(damageValues.voidShredDamage)
            );
            enemy.addSpeedModifier(wp, "Void Slowness", slowness, 10, "BASE");
        }
        return true;
    }

    private final DamageValues damageValues;

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValue voidShredDamage;
        private final List<Value> values;

        public DamageValues(float min, float max) {
            this.voidShredDamage = new Value.RangedValue(min, max);
            this.values = List.of(voidShredDamage);
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
