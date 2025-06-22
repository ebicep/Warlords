package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.AstralPlague;
import com.ebicep.warlords.abilities.SoulfireBeam;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerHorseEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AirStrike implements SpecBoostManager.SpecBoost<AirStrike> {

    private float ascendHeight;
    private int ascendDurationTicks;
    private int airStrikeDurationTicks;
    private float soulfireBeamDamageReductionPercent;
    private float soulfireBeamMaxRange;
    private int maxBeamCastsNormal;
    private int maxBeamCastsOnCooldown;

    @Override
    public void init() {
        this.ascendHeight = getValue("ascendHeight", float.class);
        this.ascendDurationTicks = getValue("ascendDurationTicks", int.class);
        this.airStrikeDurationTicks = getValue("airStrikeDurationTicks", int.class);
        this.soulfireBeamDamageReductionPercent = getValue("soulfireBeamDamageReductionPercent", float.class);
        this.soulfireBeamMaxRange = getValue("soulfireBeamMaxRange", float.class);
        this.maxBeamCastsNormal = getValue("maxBeamCastsNormal", int.class);
        this.maxBeamCastsOnCooldown = getValue("maxBeamCastsOnCooldown", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "airStrike";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                ascendHeight,
                ascendDurationTicks,
                soulfireBeamDamageReductionPercent,
                soulfireBeamMaxRange,
                airStrikeDurationTicks,
                maxBeamCastsNormal,
                maxBeamCastsOnCooldown
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public AirStrike get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
        }

        @EventHandler
        public void onWarlordsAbilityActivatePostEvent(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (!(event.getAbility() instanceof AstralPlague)) {
                return;
            }
            Location location = warlordsEntity.getLocation();
            Location targetLoc = location.clone();
            for (int i = 0; i < ascendHeight; i++) {
                if (targetLoc.clone().add(0, 2, 0).getBlock().getType() == Material.AIR) {
                    targetLoc.add(0, 1, 0);
                }
            }
            int maxCasts = warlordsEntity.getAbilitiesMatching(SoulfireBeam.class)
                                         .stream()
                                         .anyMatch(soulfireBeam -> !soulfireBeam.anyCharges()) ?
                           maxBeamCastsOnCooldown :
                           maxBeamCastsNormal;
            warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                    getStringName(),
                    "AIR",
                    Boost.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {
                        if (warlordsEntity.isDead()) {
                            if (warlordsEntity.getEntity() instanceof Player player) {
                                player.setAllowFlight(false);
                                player.setFlying(false);
                                player.setFlySpeed(0.15f);
                            }
                            return;
                        }
                        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
                        warlordsEntity.getAbilitiesMatching(SoulfireBeam.class).forEach(soulfireBeam -> {
                            modifiers.add(soulfireBeam.getCooldown().addOverridingModifier(getStringName(), 0, airStrikeDurationTicks));
                            modifiers.add(soulfireBeam.getMaxDistance().addOverridingModifier(getStringName(), soulfireBeamMaxRange, airStrikeDurationTicks));
                            soulfireBeam.setCurrentCooldown(0);
                        });
                        warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                                getStringName(),
                                "STRIKE",
                                Boost.class,
                                null,
                                warlordsEntity,
                                CooldownTypes.SPEC_BOOST,
                                cooldownManager1 -> {
                                    warlordsEntity.teleportLocationOnly(location);
                                },
                                cooldownManager1 -> {
                                    modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                                    if (warlordsEntity.getEntity() instanceof Player player) {
                                        player.setAllowFlight(false);
                                        player.setFlying(false);
                                        player.setFlySpeed(0.15f);
                                    }
                                    warlordsEntity.getAbilitiesMatching(SoulfireBeam.class).forEach(soulfireBeam -> {
                                        soulfireBeam.getCooldown().tick();
                                        soulfireBeam.useAbility();
                                    });
                                },
                                airStrikeDurationTicks
                        ) {

                            @Override
                            protected Listener getListener() {
                                RegularCooldown<Boost> regularCooldown = this;
                                return new Listener() {

                                    int casts = 0;

                                    @EventHandler
                                    public void onWarlordsAbilityActivatePreEvent(WarlordsAbilityActivateEvent.Pre event) {
                                        if (isMarkedForRemoval()) {
                                            return;
                                        }
                                        if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                                            return;
                                        }
                                        if (!(event.getAbility() instanceof SoulfireBeam)) {
                                            event.setCancelled(true);
                                        }
                                    }

                                    @EventHandler
                                    public void onWarlordsAbilityActivatePreEvent(WarlordsAbilityActivateEvent.Post event) {
                                        if (isMarkedForRemoval()) {
                                            return;
                                        }
                                        if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                                            return;
                                        }
                                        if (event.getAbility() instanceof SoulfireBeam) {
                                            casts++;
                                            if (casts >= maxCasts) {
                                                warlordsEntity.getCooldownManager().removeCooldown(regularCooldown);
                                                modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                                                return;
                                            }
                                        }
                                    }

                                    @EventHandler
                                    public void onHorseActivate(WarlordsPlayerHorseEvent event) {
                                        if (event.getWarlordsEntity().equals(warlordsEntity)) {
                                            event.setCancelled(true);
                                        }
                                    }
                                };
                            }

                            @Override
                            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                if (event.getAbility() instanceof SoulfireBeam) {
                                    return currentDamageValue * AbstractAbility.convertToDivisionDecimal(soulfireBeamDamageReductionPercent);
                                }
                                return currentDamageValue;
                            }
                        });
                    },
                    ascendDurationTicks,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        float ratio = (float) ticksElapsed / ascendDurationTicks;
                        if (warlordsEntity.getEntity() instanceof Player player) {
                            player.setAllowFlight(true);
                            player.setFlying(true);
                            player.setFlySpeed(0);
                        }
                        Location newLocation = new Location(location.getWorld(),
                                MathUtils.lerp(location.getX(), targetLoc.getX(), ratio),
                                MathUtils.lerp(location.getY(), targetLoc.getY(), ratio),
                                MathUtils.lerp(location.getZ(), targetLoc.getZ(), ratio),
                                targetLoc.getYaw(),
                                targetLoc.getPitch()
                        );
                        warlordsEntity.teleportLocationOnly(newLocation);
                    })
            ) {
                @Override
                protected Listener getListener() {
                    return new Listener() {
                        @EventHandler
                        public void onHorseActivate(WarlordsPlayerHorseEvent event) {
                            if (event.getWarlordsEntity().equals(warlordsEntity)) {
                                event.setCancelled(true);
                            }
                        }

                        @EventHandler
                        public void onWarlordsAbilityActivatePreEvent(WarlordsAbilityActivateEvent.Pre event) {
                            if (warlordsEntity.equals(event.getWarlordsEntity())) {
                                event.setCancelled(true);
                            }
                        }
                    };
                }
            });
        }

    }

}
