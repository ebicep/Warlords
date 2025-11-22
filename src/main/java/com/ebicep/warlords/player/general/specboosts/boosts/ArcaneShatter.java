package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ArcaneShatter implements SpecBoostManager.SpecBoost<ArcaneShatter> {

    private float energyPerMelee;
    private float healthIncrease;
    private float damageIncrease;
    private float range;
    private int stunTicks;

    @Override
    public void init() {
        this.energyPerMelee = getValue("energyPerMelee", float.class);
        this.healthIncrease = getValue("healthIncrease", float.class);
        this.damageIncrease = getValue("damageIncrease", float.class);
        this.range = getValue("range", float.class);
        this.stunTicks = getValue("stunTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "arcaneShatter";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energyPerMelee, healthIncrease, damageIncrease, range, stunTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public ArcaneShatter get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getEnergyPerHit().addAdditiveModifier("Spec Boost", energyPerMelee);
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost (Base)", healthIncrease);
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().removeModifier("Spec Boost (Base)");
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown) ||
                    !Objects.equals(cooldown.getName(), "Arcane Shield") ||
                    !(cooldown.getCooldownObject() instanceof Shield shield) ||
                    !cooldown.getFrom().equals(warlordsEntity)
            ) {
                return;
            }
            Consumer<CooldownManager> oldOnRemove = cooldown.getOnRemove();
            cooldown.setOnRemove(cooldownManager -> {
                warlordsEntity.playSound(warlordsEntity.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 2, 0.5f);
                oldOnRemove.accept(cooldownManager);
                PlayerFilter.entitiesAround(warlordsEntity, range, range, range)
                            .aliveEnemiesOf(warlordsEntity)
                            .forEach(we -> we.setStunTicks(stunTicks));
                warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                        getStringName(),
                        null,
                        Boost.class,
                        null,
                        warlordsEntity,
                        CooldownTypes.SPEC_BOOST,
                        cM -> {},
                        stunTicks
                ).addModifier(Modifier.OUTGOING_DAMAGE_BEFORE_INTERVENE, (e, currentDamageValue) -> {
                            currentDamageValue.addMultiplicativeModifierMult(getStringName(), AbstractAbility.convertToMultiplicationDecimal(damageIncrease));
                        }
                ));
            });
            Consumer<CooldownManager> oldOnRemoveForce = cooldown.getOnRemoveForce();
            cooldown.setOnRemoveForce(cooldownManager -> {
                warlordsEntity.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                        .append(Component.text(" Your ", NamedTextColor.GRAY))
                        .append(Component.text("Arcane Shield", NamedTextColor.YELLOW))
                        .append(Component.text(" has expired!", NamedTextColor.GRAY))
                );
                oldOnRemoveForce.accept(cooldownManager);
            });
            regularCooldown.addTriConsumer((cd, ticksLeft, ticksElapsed) -> {
                if (ticksElapsed % 20 == 0) {
                    int timeLeft = Math.round(ticksLeft / 20f);
                    warlordsEntity.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                            .append(Component.text(" Your ", NamedTextColor.GRAY))
                            .append(Component.text("Arcane Shield", NamedTextColor.YELLOW))
                            .append(Component.text(" will expire in ", NamedTextColor.GRAY))
                            .append(Component.text(timeLeft, NamedTextColor.GOLD))
                            .append(Component.text(" second" + (timeLeft == 1 ? "!" : "s!"), NamedTextColor.GRAY))
                    );

                }
            });
        }


    }

}
