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
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
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
        private boolean inAir = false;
        private boolean rising = false;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
        }

        @EventHandler
        public void onHorseActivate(WarlordsPlayerHorseEvent event) {
            if (event.getWarlordsEntity().equals(warlordsEntity) && inAir) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onWarlordsAbilityActivatePreEvent(WarlordsAbilityActivateEvent.Pre event) {
            if (warlordsEntity.equals(event.getWarlordsEntity()) && rising) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onWarlordsAbilityActivatePostEvent(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof AstralPlague) {
                inAir = true;
                rising = true;
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

                new GameRunnable(warlordsEntity.getGame()) {

                    int ticksElapsed = 0;

                    @Override
                    public void run() {
                        ticksElapsed++;
                        if (warlordsEntity.isDead()) {
                            this.cancel();
                            return;
                        }
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
                        if (ratio >= 1) {
                            this.cancel();
                            rising = false;

                            List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
                            warlordsEntity.getAbilitiesMatching(SoulfireBeam.class).forEach(soulfireBeam -> {
                                modifiers.add(soulfireBeam.getCooldown().addOverridingModifier(getStringName(), 0, airStrikeDurationTicks));
                                modifiers.add(soulfireBeam.getMaxDistance().addOverridingModifier(getStringName(), soulfireBeamMaxRange, airStrikeDurationTicks));
                                soulfireBeam.setCurrentCooldown(0);
                            });
                            RegularCooldown<Boost> cooldown = new RegularCooldown<>(
                                    getStringName(),
                                    "AIR",
                                    Boost.class,
                                    null,
                                    warlordsEntity,
                                    CooldownTypes.SPEC_BOOST,
                                    cooldownManager -> {
                                    },
                                    cooldownManager -> {
                                        inAir = false;
                                        modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                                        if (warlordsEntity.getEntity() instanceof Player player) {
                                            player.setAllowFlight(false);
                                            player.setFlying(false);
                                            player.setFlySpeed(0.15f);
                                        }
                                        warlordsEntity.teleportLocationOnly(location);
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
                                    };
                                }

                                @Override
                                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                    if (event.getAbility() instanceof SoulfireBeam) {
                                        return currentDamageValue * AbstractAbility.convertToDivisionDecimal(soulfireBeamDamageReductionPercent);
                                    }
                                    return currentDamageValue;
                                }
                            };
                            warlordsEntity.getCooldownManager().addCooldown(cooldown);
                        }
                    }
                }.runTaskTimer(0, 1);
            }
        }

    }

}
