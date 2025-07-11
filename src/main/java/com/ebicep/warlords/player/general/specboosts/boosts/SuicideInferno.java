package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Inferno;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;

public class SuicideInferno implements SpecBoostManager.SpecBoost<SuicideInferno> {


    private float energyPerMelee;
    private int infernoDurationReductionTicks;
    private float knockbackRangeBlocks;
    private Value.RangedValue infernoDeathDamage;

    @Override
    public void init() {
        this.energyPerMelee = getValue("energyPerMelee", float.class);
        this.infernoDurationReductionTicks = getValue("infernoDurationReductionTicks", int.class);
        this.knockbackRangeBlocks = getValue("knockbackRangeBlocks", float.class);
        this.infernoDeathDamage = getValue("infernoDeathDamage", Value.RangedValue.class);
    }

    @Override
    public String getConfigFieldName() {
        return "burstChain";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                energyPerMelee,
                infernoDurationReductionTicks,
                knockbackRangeBlocks,
                infernoDeathDamage
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SuicideInferno get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(Inferno.class).forEach(inferno -> {
                inferno.setTickDuration((inferno.getTickDuration()-infernoDurationReductionTicks));
            });
        }
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!Objects.equals(cooldown.getName(), "Inferno") || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            Consumer<CooldownManager> oldOnRemove = cooldown.getOnRemove();
            cooldown.setOnRemove(cooldownManager -> {
                oldOnRemove.accept(cooldownManager);
                Location location = warlordsEntity.getLocation().add(0, .7, 0);
                warlordsEntity.playSound(location, Sound.ENTITY_BLAZE_DEATH, 2, 0.5f);
                EffectUtils.displayParticle(Particle.EXPLOSION, location, 1, 0, 0, 0, 0.35);
                PlayerFilter.entitiesAround(warlordsEntity, knockbackRangeBlocks, knockbackRangeBlocks, knockbackRangeBlocks)
                        .aliveEnemiesOf(warlordsEntity)
                        .forEach(target -> {
                            target.addInstance(InstanceBuilder
                                    .damage()
                                    .cause(getStringName())
                                    .source(warlordsEntity)
                                    .value(infernoDeathDamage)
                            );
                            final Location loc = target.getLocation();
                            final Vector v = warlordsEntity.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.5).setY(0.35);
                            target.setVelocity(getStringName(), v, false);
                        });
            });


            warlordsEntity.addInstance(InstanceBuilder
                    .melee()
                    .source(warlordsEntity)
                    .value(100000)
            );



            Consumer<CooldownManager> oldOnRemoveForce = cooldown.getOnRemoveForce();
            cooldown.setOnRemoveForce(cooldownManager -> {
                warlordsEntity.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                        .append(Component.text(" Your ", NamedTextColor.GRAY))
                        .append(Component.text("Suicide Inferno", NamedTextColor.YELLOW))
                        .append(Component.text(" has exploded!", NamedTextColor.GRAY))
                );
                oldOnRemoveForce.accept(cooldownManager);
            });
            /*regularCooldown.addTriConsumer((cd, ticksLeft, ticksElapsed) -> {
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
            });*/
        }


    }

}