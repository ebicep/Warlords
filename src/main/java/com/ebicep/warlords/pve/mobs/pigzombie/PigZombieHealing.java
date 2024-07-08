package com.ebicep.warlords.pve.mobs.pigzombie;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.List;

public class PigZombieHealing extends AbstractAbility implements Heals<PigZombieHealing.HealingValues> {

    private final float hitbox;

    public PigZombieHealing(float heal, float hitbox) {
        super("Zombifaction", 3, 100);
        this.hitbox = hitbox;
        this.healingValues = new HealingValues(heal);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Location location = wp.getLocation();
        Utils.playGlobalSound(location, Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 1, 0.5f);
        Utils.playGlobalSound(location, "paladin.holyradiance.activation", 0.8f, 0.6f);
        EffectUtils.playCylinderAnimation(location, 6, Particle.FIREWORKS_SPARK, 1);
        for (WarlordsEntity ally : PlayerFilter
                .entitiesAround(wp, hitbox, hitbox, hitbox)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            ally.addInstance(InstanceBuilder
                    .healing()
                    .ability(this)
                    .source(wp)
                    .value(healingValues.zombificationHealing)
            );
        }
        return true;
    }

    private final HealingValues healingValues;

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.SetValue zombificationHealing;
        private final List<Value> values;

        public HealingValues(float value) {
            this.zombificationHealing = new Value.SetValue(value);
            this.values = List.of(zombificationHealing);
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}
