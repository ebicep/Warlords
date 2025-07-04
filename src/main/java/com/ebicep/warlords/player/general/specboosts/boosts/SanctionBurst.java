package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.CripplingStrike;
import com.ebicep.warlords.abilities.HeartToHeart;
import com.ebicep.warlords.abilities.PrismGuard;
import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerHeartToHeartEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Consumer;

public class SanctionBurst implements SpecBoostManager.SpecBoost<SanctionBurst> {

    private float soulShackleDamageDecrease;
    private float crippledDamageReductionPercent;
    private int crippledDurationTicks;

    @Override
    public void init() {
        this.soulShackleDamageDecrease = getValue("soulShackleDamageDecrease", float.class);
        this.crippledDamageReductionPercent = getValue("crippledDamageReductionPercent", float.class);
        this.crippledDurationTicks = getValue("crippledDurationTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sanctionBurst";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(crippledDamageReductionPercent, crippledDurationTicks, soulShackleDamageDecrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SanctionBurst get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(HeartToHeart.class).forEach(heartToHeart -> {
                heartToHeart.setTargetEnemies(true);
            });
            warlordsPlayer.getAbilitiesMatching(SoulShackle.class).forEach(soulShackle -> {
                soulShackle.getDamageValues()
                           .getShackleDamage()
                           .forEachValue(floatModifier -> floatModifier.addAdditiveModifier("Spec Boost", -soulShackleDamageDecrease));
            });
        }

        @EventHandler
        public void onWarlordsPlayerHeartToHeartEvent(WarlordsPlayerHeartToHeartEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            WarlordsEntity heartTarget = event.getHeartTarget();
            if (heartTarget.isTeammate(warlordsEntity)) {
                return;
            }
            CripplingStrike.cripple(
                    warlordsEntity,
                    heartTarget,
                    null,
                    getStringName(),
                    0,
                    crippledDurationTicks,
                    AbstractAbility.convertToDivisionDecimal(crippledDamageReductionPercent)
            );
        }

        @EventHandler
        public void onWarlordsAbilityActivatePostApplyEvent(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof HeartToHeart) {
                warlordsEntity.getAbilitiesMatching(SoulShackle.class).forEach(soulShackle -> {
                    soulShackle.setCurrentCooldown(0);
                });
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!(cooldown.getCooldownObject() instanceof PrismGuard.PrismGuardData data) ||
                    !(cooldown.getName().equals("Prism Guard")) ||
                    !cooldown.getFrom().equals(warlordsEntity)
            ) {
                return;
            }
            Consumer<CooldownManager> oldOnRemove = cooldown.getOnRemove();
            cooldown.setOnRemove(cooldownManager -> {
                oldOnRemove.accept(cooldownManager);
                warlordsEntity.getSpeed().removeNegativeModifiers();
                warlordsEntity.getCooldownManager().removeDebuffCooldowns();
                float radius = data.getPrismGuard().getBubbleRadius();
                PlayerFilter.entitiesAround(warlordsEntity, radius, radius, radius).aliveEnemiesOf(warlordsEntity).forEach(target -> {
                    final Location loc = target.getLocation();
                    final Vector v = warlordsEntity.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.5).setY(0.35);
                    target.setVelocity(getStringName(), v, false);
                });
            });
        }

    }

}
