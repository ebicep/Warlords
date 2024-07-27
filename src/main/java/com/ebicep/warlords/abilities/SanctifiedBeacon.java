package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.abilities.internal.AbstractBeaconAbility;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.SanctifiedBeaconBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SanctifiedBeacon extends AbstractBeaconAbility<SanctifiedBeacon, SanctifiedBeacon.SanctifiedBeaconData> implements BlueAbilityIcon {

    public static final Map<Integer, Team> BEACON_IDS = new HashMap<>();

    public int hexesGiven = 0;
    public int critsReduced = 0;

    private final int maxAllies = 2;
    private int critMultiplierReducedBy = 25;
    private int hexIntervalTicks = 60;
    private int stacksGranted = 1;
    private float damageReductionPve = 30;


    public SanctifiedBeacon() {
        super("Sanctified Beacon", 20, 40, 8, 15);
    }

    @Override
    public Component getBonusDescription() {
        return AbilityDescriptionBuilder
                .create("All enemies within a ")
                .blocks(radius)
                .text(" radius have their Crit Multiplier reduced by ")
                .percent(critMultiplierReducedBy, NamedTextColor.RED)
                .text(". The beacon will emit a wave of energy that grants ")
                .text(maxAllies, NamedTextColor.BLUE)
                .text(" allies in range ")
                .text(stacksGranted, NamedTextColor.BLUE)
                .text(" stack of Merciful Hex every ")
                .durationTicks(hexIntervalTicks)
                .text(".")
                .build();
    }

    @Override
    public Class<SanctifiedBeaconData> getDataClass() {
        return SanctifiedBeaconData.class;
    }

    @Override
    public LineEffect getLineEffect(Location target) {
        return new LineEffect(target.clone().add(0, 0.5, 0), Particle.REDSTONE, new Particle.DustOptions(Color.fromRGB(150, 8, 80), 1));
    }

    @Override
    public SanctifiedBeaconData getDataObject(WarlordsEntity wp, ArmorStand beacon, Location groundLocation, CircleEffect effect, float radius) {
        return new SanctifiedBeaconData(beacon, groundLocation, effect, radius, Utils.spawnArmorStand(groundLocation, armorStand -> {
            BEACON_IDS.put(armorStand.getEntityId(), wp.getTeam());
            armorStand.setGravity(true);
            armorStand.setMarker(true);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.LIME_STAINED_GLASS));
        }));
    }

    @Override
    public String getAbbreviation() {
        return "BEACON";
    }

    @Override
    protected void onRemove(SanctifiedBeaconData data) {
        data.getCrystal().remove();
        BEACON_IDS.remove(data.getCrystal().getEntityId());
    }

    @Override
    public void whileActive(@Nonnull WarlordsEntity wp, RegularCooldown<SanctifiedBeaconData> cooldown, Integer ticksLeft, Integer ticksElapsed) {
        SanctifiedBeaconData beacon = cooldown.getCooldownObject();
        float rad = beacon.getRadius().getCalculatedValue();
        if (ticksElapsed % 5 == 0) {
            for (WarlordsEntity nearBy : PlayerFilter.entitiesAround(beacon.getGroundLocation(), rad, rad, rad)) {
                if (nearBy.isTeammate(wp)) {
                    if (!pveMasterUpgrade2) {
                        continue;
                    }
                    nearBy.getCooldownManager().removeCooldownByObject(beacon.getM2Object());
                    nearBy.getCooldownManager().addCooldown(new RegularCooldown<>(
                            "Shadow Garden",
                            null,
                            Object.class,
                            beacon.getM2Object(),
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {
                            },
                            6 // a little longer to make sure there's no gaps in the effect
                    ) {
                        @Override
                        public float setCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                            return currentCritMultiplier + 25;
                        }

                        @Override
                        public void multiplyKB(Vector currentVector) {
                            currentVector.multiply(.9);
                        }
                    });
                } else {
                    nearBy.getCooldownManager().removeCooldownByObject(beacon);
                    nearBy.getCooldownManager().addCooldown(new RegularCooldown<>(
                            name,
                            null,
                            SanctifiedBeaconData.class,
                            beacon,
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {
                            },
                            6 // a little longer to make sure there's no gaps in the effect
                    ) {

                        @Override
                        public float setCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                            return currentCritMultiplier * convertToDivisionDecimal(critMultiplierReducedBy);
                        }

                        @Override
                        public void onPostCritCalculationFromAttacker(
                                WarlordsDamageHealingEvent event,
                                float currentDamageValue,
                                boolean isCrit,
                                float critChance,
                                float critMultiplier
                        ) {
                            if (isCrit) {
                                critsReduced++;
                            }
                        }

                        @Override
                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            if (wp.isInPve()) {
                                return currentDamageValue * convertToDivisionDecimal(damageReductionPve);
                            }
                            return currentDamageValue;
                        }
                    });
                    if (pveMasterUpgrade) {
                        nearBy.getSpeed().removeModifier(name);
                        nearBy.addSpeedModifier(wp, name, -20, 6, "BASE");
                    }
                }
            }
        }

        ArmorStand crystal = beacon.getCrystal();
        int yawIncrease = ticksElapsed % hexIntervalTicks == 0 ? 120 : 10;
        if (ticksElapsed % 2 == 0) {
            Location crystalLocation = crystal.getLocation();
            crystalLocation.setYaw(crystalLocation.getYaw() + yawIncrease);
            crystal.teleport(crystalLocation);
        }

        if (ticksElapsed % hexIntervalTicks == 0 && ticksElapsed != 0) {
            for (WarlordsEntity ally : PlayerFilter
                    .entitiesAround(beacon.getGroundLocation(), rad, rad, rad)
                    .aliveTeammatesOf(wp)
                    .closestFirst(beacon.getGroundLocation())
                    .limit(maxAllies)
            ) {
                EffectUtils.playParticleLinkAnimation(
                        crystal.getLocation().clone().add(0, .5, 0),
                        ally.getLocation(),
                        20, 200, 20,
                        2
                );
                MercifulHex.giveMercifulHex(wp, ally);
                hexesGiven++;
            }

            Utils.playGlobalSound(crystal.getLocation(), Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 1, 2);
            EffectUtils.playCircularEffectAround(
                    wp.getGame(),
                    crystal.getLocation(),
                    Particle.TOTEM,
                    3,
                    1,
                    0.15,
                    4,
                    1,
                    4
            );
            EffectUtils.playCircularEffectAround(
                    wp.getGame(),
                    crystal.getLocation(),
                    Particle.VILLAGER_HAPPY,
                    1,
                    1,
                    0.1,
                    8,
                    1,
                    3
            );
        }
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Hexes Given", "" + hexesGiven));
        info.add(new Pair<>("Crits Reduced", "" + critsReduced));
        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SanctifiedBeaconBranch(abilityTree, this);
    }

    public int getCritMultiplierReducedBy() {
        return critMultiplierReducedBy;
    }

    public void setCritMultiplierReducedBy(int critMultiplierReducedBy) {
        this.critMultiplierReducedBy = critMultiplierReducedBy;
    }

    public int getHexIntervalTicks() {
        return hexIntervalTicks;
    }

    public void setHexIntervalTicks(int hexIntervalTicks) {
        this.hexIntervalTicks = hexIntervalTicks;
    }

    public float getDamageReductionPve() {
        return damageReductionPve;
    }

    public void setDamageReductionPve(float damageReductionPve) {
        this.damageReductionPve = damageReductionPve;
    }

    public static class SanctifiedBeaconData extends AbstractBeaconAbility.BeaconData {

        private final ArmorStand crystal;
        private final Object m2Object = new Object();

        public SanctifiedBeaconData(ArmorStand beacon, Location groundLocation, CircleEffect effect, float radius, ArmorStand crystal) {
            super(beacon, groundLocation, effect, radius);
            this.crystal = crystal;
        }

        public ArmorStand getCrystal() {
            return crystal;
        }

        public Object getM2Object() {
            return m2Object;
        }
    }

}
