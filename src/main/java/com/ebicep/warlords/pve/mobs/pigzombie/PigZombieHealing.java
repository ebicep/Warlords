package com.ebicep.warlords.pve.mobs.pigzombie;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
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
    private final HealingValues healingValues = new HealingValues();

    public PigZombieHealing(AbstractAbilityBuilder builder, float hitbox) {
        super(builder);
        this.hitbox = hitbox;
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Location location = wp.getLocation();
        Utils.playGlobalSound(location, Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 1, 0.5f);
        Utils.playGlobalSound(location, "paladin.holyradiance.activation", 0.8f, 0.6f);
        EffectUtils.playCylinderAnimation(location, 6, Particle.FIREWORK, 1);
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

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue zombificationHealing = new Value.SetValue(0);
        private List<Value> values = List.of(zombificationHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.zombificationHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("zombificationHealing"),
                    Value.SetValue.class
            );
            this.values = List.of(zombificationHealing);
        }

    }

}
