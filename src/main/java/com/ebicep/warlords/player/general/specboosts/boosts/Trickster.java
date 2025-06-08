package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.IncendiaryCurse;
import com.ebicep.warlords.abilities.OrderOfEviscerate;
import com.ebicep.warlords.abilities.ShadowStep;
import com.ebicep.warlords.abilities.SoulSwitch;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.circle.AreaEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerSwapEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsThrowableProjectileImpactEvent;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.MobHologram;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.pvp.TricksterDummy;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

public class Trickster implements SpecBoostManager.SpecBoost<Trickster> {

    private int incendiaryCurseDamageIncrease;
    private int incendiaryCurseIgniteDurationTicks;
    private int incendiaryCurseIgnitePeriod;
    private Value.RangedValue incendiaryCurseIgniteDamage;
    private int incendiaryCurseEnergyCostIncrease;
    private float soulSwitchCooldownReductionSeconds;
    private int soulSwitchInvisTickDuration;
    private int soulSwitchDummyDurationTicks;
    private int soulSwitchDummyHealth;
    private int shadowLeapHealthThreshold;

    @Override
    public void init() {
        this.incendiaryCurseDamageIncrease = getValue("incendiaryCurseDamageIncrease", int.class);
        this.incendiaryCurseIgniteDurationTicks = getValue("incendiaryCurseIgniteDurationTicks", int.class);
        this.incendiaryCurseIgnitePeriod = getValue("incendiaryCurseIgnitePeriod", int.class);
        this.incendiaryCurseIgniteDamage = getValue("incendiaryCurseIgniteDamage", Value.RangedValue.class);
        this.incendiaryCurseEnergyCostIncrease = getValue("incendiaryCurseEnergyCostIncrease", int.class);
        this.soulSwitchCooldownReductionSeconds = getValue("soulSwitchCooldownReductionSeconds", float.class);
        this.soulSwitchInvisTickDuration = getValue("soulSwitchInvisTickDuration", int.class);
        this.soulSwitchDummyDurationTicks = getValue("soulSwitchDummyDurationTicks", int.class);
        this.soulSwitchDummyHealth = getValue("soulSwitchDummyHealth", int.class);
        this.shadowLeapHealthThreshold = getValue("shadowLeapHealthThreshold", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "trickster";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                incendiaryCurseDamageIncrease,
                incendiaryCurseIgniteDurationTicks,
                incendiaryCurseIgniteDamage,
                incendiaryCurseEnergyCostIncrease,
                soulSwitchCooldownReductionSeconds,
                soulSwitchInvisTickDuration,
                soulSwitchDummyDurationTicks,
                soulSwitchDummyHealth,
                shadowLeapHealthThreshold
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Trickster get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(IncendiaryCurse.class).forEach(incendiaryCurse -> {
                incendiaryCurse.getDamageValues().getCurseDamage().forEachValue(floatModifiable ->
                        floatModifiable.addAdditiveModifier("Spec Boost", incendiaryCurseDamageIncrease)
                );
                incendiaryCurse.getEnergyCost().addAdditiveModifier("Spec Boost", incendiaryCurseEnergyCostIncrease);
            });
            warlordsPlayer.getAbilitiesMatching(SoulSwitch.class).forEach(soulSwitch -> {
                soulSwitch.getCooldown().addAdditiveModifier("Spec Boost", -soulSwitchCooldownReductionSeconds);
            });
            warlordsPlayer.getAbilitiesMatching(ShadowStep.class).forEach(shadowStep -> {
                shadowStep.setLeapHealThreshold(shadowLeapHealthThreshold);
            });
        }

        @EventHandler
        public void onWarlordsThrowableProjectileImpactEvent(WarlordsThrowableProjectileImpactEvent event) {
            if (!event.getWarlordsEntity().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof IncendiaryCurse incendiaryCurse)) {
                return;
            }
            float radius = incendiaryCurse.getHitBoxRadius().getCalculatedValue();
            Location location = event.getLocation();
            CircleEffect circleEffect = new CircleEffect(warlordsEntity.getGame(),
                    warlordsEntity.getTeam(),
                    location,
                    radius,
                    new AreaEffect(1.5, Particle.SMOKE)
            );
            new GameRunnable(warlordsEntity.getGame()) {

                int ticksElapsed = 0;

                @Override
                public void run() {
                    if (ticksElapsed % 5 == 0) {
                        circleEffect.playEffects();
                    }
                    if (ticksElapsed % incendiaryCurseIgnitePeriod == 0) {
                        PlayerFilter.entitiesAround(location, radius, radius, radius)
                                    .aliveEnemiesOf(warlordsEntity)
                                    .forEach(enemy -> {
                                        enemy.addInstance(InstanceBuilder
                                                .damage()
                                                .cause(getStringName())
                                                .source(warlordsEntity)
                                                .value(incendiaryCurseIgniteDamage)
                                                .flags(InstanceFlags.DOT)
                                        );
                                    });
                    }
                    ticksElapsed++;
                    if (ticksElapsed >= incendiaryCurseIgniteDurationTicks) {
                        cancel();
                    }
                }
            }.runTaskTimer(20, 0);
        }

        @EventHandler
        public void onWarlordsPlayerSwapEvent(WarlordsPlayerSwapEvent event) {
            if (!event.getWarlordsEntity().equals(warlordsEntity)) {
                return;
            }
            OrderOfEviscerate.giveCloak(warlordsEntity, soulSwitchInvisTickDuration);
            Location oldLocation = event.getWarlordsEntity().getLocation();
            Location newLocation = event.getSwappedPlayer().getLocation();
            WarlordsNPC npc = warlordsEntity
                    .getGame()
                    .addNPC(new TricksterDummy(
                            oldLocation,
                            soulSwitchDummyHealth,
                            warlordsEntity
                    ).toNPC(
                            warlordsEntity.getGame(),
                            warlordsEntity.getTeam(),
                            warlordsNPC -> warlordsNPC.getMob().onSpawn(null)
                    ));
            npc.setName(warlordsEntity.getName());
            if (warlordsEntity.getEntity() instanceof Player player) {
                SkinTrait skinTrait = npc.getNpc().getOrAddTrait(SkinTrait.class);
                skinTrait.setSkinPersistent(player);
            }
            Component classNameShortWithBrackets = warlordsEntity.getSpec().getClassNameShortWithBrackets(warlordsEntity.getSpecClass().specType.getTextColor());
            Component levelStringBracket = ExperienceManager.getLevelStringBracket(ExperienceManager.getLevelForSpec(warlordsEntity.getUuid(), warlordsEntity.getSpecClass()));
            List<MobHologram.CustomHologramLine> customHologramLines = npc.getMobHologram().getCustomHologramLines();
            customHologramLines.forEach(customHologramLine -> customHologramLine.setDelete(true));
            customHologramLines.add(new MobHologram.CustomHologramLine(Component.textOfChildren(
                    classNameShortWithBrackets,
                    warlordsEntity.getColoredName(),
                    levelStringBracket
            )));
            new GameRunnable(warlordsEntity.getGame()) {
                @Override
                public void run() {
                    if (npc.isAlive()) {
                        npc.cleanup();
                        getGame().getPlayers().remove(npc.getUuid());
                        Warlords.removePlayer(npc.getUuid());
                    }
                }
            }.runTaskLater(soulSwitchDummyDurationTicks);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onWarlordsDeathEvent(WarlordsDeathEvent event) {
            if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob().getMobRegistry() == Mob.TRICKSTER_DUMMY) {
                event.setForceCancel(true);
                event.setCancelled(true);
                warlordsNPC.cleanup();
                warlordsNPC.getGame().getPlayers().remove(warlordsNPC.getUuid());
                Warlords.removePlayer(warlordsNPC.getUuid());
            }
        }

    }

}