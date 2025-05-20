package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ArcaneShield;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class FortifiedAegis implements SpecBoostManager.SpecBoost<FortifiedAegis> {

    private int healthIncrease;
    private float arcaneShieldCooldownReductionSeconds;
    private float knockbackRangeBlocks;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.arcaneShieldCooldownReductionSeconds = getValue("arcaneShieldCooldownReductionSeconds", float.class);
        this.knockbackRangeBlocks = getValue("knockbackRangeBlocks", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "fortifiedAegis";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, arcaneShieldCooldownReductionSeconds, knockbackRangeBlocks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public FortifiedAegis get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost", healthIncrease);
            warlordsPlayer.getAbilitiesMatching(ArcaneShield.class).forEach(arcaneShield -> {
                arcaneShield.getCooldown().addAdditiveModifier("Spec Boost", -arcaneShieldCooldownReductionSeconds);
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().removeModifier("Spec Boost");
            warlordsPlayer.getAbilitiesMatching(ArcaneShield.class).forEach(arcaneShield -> {
                arcaneShield.getCooldown().removeModifier("Spec Boost");
            });
        }

        @EventHandler
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!Objects.equals(cooldown.getName(), "Arcane Shield") || !(cooldown.getCooldownObject() instanceof Shield shield) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            Consumer<CooldownManager> oldOnRemove = cooldown.getOnRemove();
            cooldown.setOnRemove(cooldownManager -> {
                oldOnRemove.accept(cooldownManager);
                Location location = warlordsEntity.getLocation().add(0, .7, 0);
                Utils.playGlobalSound(location, "mage.fireball.impact", 2, 2);
                EffectUtils.displayParticle(Particle.EXPLOSION, location, 1, 0, 0, 0, 0.35);
                PlayerFilter.entitiesAround(warlordsEntity, knockbackRangeBlocks, knockbackRangeBlocks, knockbackRangeBlocks)
                            .aliveEnemiesOf(warlordsEntity)
                            .forEach(target -> {
                                final Location loc = target.getLocation();
                                final Vector v = warlordsEntity.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.5).setY(0.35);
                                target.setVelocity(getStringName(), v, false);
                            });
            });
        }

    }

}
