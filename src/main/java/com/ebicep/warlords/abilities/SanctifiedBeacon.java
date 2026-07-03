package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.ChasingOrbsAbility;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.SanctifiedBeaconBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SanctifiedBeacon extends AbstractBeaconAbility<SanctifiedBeacon, SanctifiedBeacon.SanctifiedBeaconData> implements BlueAbilityIcon, AbilityStats<SanctifiedBeacon, SanctifiedBeacon.SanctifiedBeaconStats> {

    public static final Map<Integer, Team> BEACON_IDS = new HashMap<>();
    private final SanctifiedBeaconStats stats = new SanctifiedBeaconStats();
    private int maxAllies = 2;
    private int critMultiplierReducedBy = 25;
    private int hexIntervalTicks = 60;
    private int stacksGranted = 1;
    private float damageReductionPve = 30;

    private ChasingOrbsAbility chasingItemDamage;
    private ChasingOrbsAbility chasingItemHealing;

    public SanctifiedBeacon() {
        super(AbstractAbilityBuilder.create("sanctifiedBeacon").pvp());
    }

    public SanctifiedBeacon(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.maxAllies = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxAllies"), int.class);
        this.critMultiplierReducedBy = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("critMultiplierReducedBy"), int.class);
        this.hexIntervalTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hexIntervalTicks"), int.class);
        this.stacksGranted = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("stacksGranted"), int.class);
        this.damageReductionPve = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReductionPve"), float.class);
    }

    @Override
    public Component getBonusDescription() {
        return AbilityDescriptionBuilder.create("All enemies within a ")
                                        .blocks(radius)
                                        .text(" radius have their Crit Multiplier reduced by ")
                                        .percent(critMultiplierReducedBy, NamedTextColor.RED)
                                        .text(". The beacon will emit a wave of energy that grants ")
                                        .text(maxAllies, NamedTextColor.BLUE)
                                        .text(" allies in range ")
                                        .text(stacksGranted, NamedTextColor.BLUE)
                                        .text(" stack" + (stacksGranted != 1 ? "s" : "") + " of ")
                                        .text("MHEX", NamedTextColor.DARK_GREEN)
                                        .text(" every ")
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
        return new LineEffect(target.clone().add(0, 0.5, 0), Particle.DUST, new Particle.DustOptions(Color.fromRGB(150, 8, 80), 1));
    }

    @Override
    public SanctifiedBeaconData getDataObject(WarlordsEntity wp, ArmorStand beacon, Location groundLocation, CircleEffect effect, float radius) {
        return new SanctifiedBeaconData(beacon, groundLocation, effect, radius, Utils.spawnArmorStand(groundLocation, armorStand -> {
                    BEACON_IDS.put(armorStand.getEntityId(), wp.getTeam());
                    armorStand.setGravity(true);
                    armorStand.setMarker(true);
                    armorStand.getEquipment().setHelmet(new ItemStack(pveMasterUpgrade ? Material.NETHERITE_SWORD : Material.LIME_STAINED_GLASS));
                }
        )
        );
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
                    RegularCooldown<Object> shadowGardenCooldown = new RegularCooldown<>(
                            "Shadow Garden",
                            null,
                            Object.class,
                            beacon.getM2Object(),
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {
                            },
                            // a little longer to make sure there's no gaps in the effect
                            6
                    );
                    shadowGardenCooldown.addModifier(Modifier.MODIFY_OUTGOING_CRIT_MULTIPLIER, (event, currentCritMultiplier) -> {
                        currentCritMultiplier.addModifier(FloatModifiable.ModifierType.ADDITIVE, "Shadow Garden", 30);
                            }
                    );
                    nearBy.addKnockbackModifier(wp, "Shadow Garden", -50, shadowGardenCooldown);
                    nearBy.getCooldownManager().removeCooldownByName("Shadow Garden");
                    nearBy.getCooldownManager().addCooldown(shadowGardenCooldown);
                } else {
                    nearBy.getCooldownManager().removeCooldownByObject(beacon);
                    boolean[] crit = {false};
                    RegularCooldown<SanctifiedBeaconData> enemyCooldown = new RegularCooldown<>(
                            name,
                            null,
                            SanctifiedBeaconData.class,
                            beacon,
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {},
                            6 // a little longer to make sure there's no gaps in the effect
                    );
                    enemyCooldown.addModifier(Modifier.MODIFY_OUTGOING_CRIT_MULTIPLIER_POST_CALC, (event, currentDamageValue, isCrit, critChance, critMultiplier) -> {
                        crit[0] = isCrit;
                        if (isCrit) {
                            stats.critsReduced++;
                        }
                            }
                    );
                    String modifierName = name + " " + Integer.toHexString(enemyCooldown.hashCode());
                    enemyCooldown.addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                                if (crit[0]) { // TODO unscuff
                                    currentDamageValue.addModifier(
                                            FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, modifierName,
                                            -critMultiplierReducedBy / 100f,
                                            contribution -> stats.critDamageReduced += Math.abs(contribution)
                                    );
                                }
                                if (wp.isInPve()) {
                                    currentDamageValue.addModifier(
                                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, modifierName,
                                            convertToDivisionDecimal(damageReductionPve)
                                    );
                                }
                            }
                    );
                    nearBy.getCooldownManager().addCooldown(enemyCooldown);
                    if (pveMasterUpgrade2) {
                        if (nearBy instanceof WarlordsNPC npc && npc.getMob() instanceof BossLike) {
                            return;
                        }
                        nearBy.getSpeed().removeModifier(name);
                        nearBy.addSpeedModifier(wp, name, -30, 9999);
                        nearBy.getCooldownManager().removeCooldownByName("Shadow Garden");
                        nearBy.getCooldownManager()
                              .addCooldown(new PermanentCooldown<>(
                                      "Shadow Garden",
                                      "GARDEN",
                                      SanctifiedBeacon.class,
                                      null,
                                      wp,
                                      CooldownTypes.ABILITY,
                                      cooldownManager -> {},
                                      false
                              ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                                  currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, 0.7f);
                                      }
                              ));
                    }
                }
            }
        }
        ArmorStand crystal = beacon.getCrystal();
        Location orbitLocation = crystal.getLocation().clone().add(0, -4, 0);
        chasingItemDamage = new ChasingOrbsAbility(wp, Math.min(3, wp.getGame().playersCount()), 40, 0.6, 3, 2000, 0, Material.AMETHYST_SHARD, 0.7f, false, orbitLocation);
        chasingItemHealing = new ChasingOrbsAbility(wp, 3, 40, 0.6, 3, 1000, 0, Material.LIME_STAINED_GLASS, 0.7f, true, orbitLocation);
        int yawIncrease = ticksElapsed % hexIntervalTicks == 0 ? 120 : 10;
        if (ticksElapsed % 2 == 0) {
            Location crystalLocation = crystal.getLocation();
            crystalLocation.setYaw(crystalLocation.getYaw() + yawIncrease);
            crystal.teleport(crystalLocation);
        }
        if (ticksElapsed % hexIntervalTicks == 0) {
            for (WarlordsEntity ally : PlayerFilter.entitiesAround(beacon.getGroundLocation(), rad, rad, rad)
                                                   .aliveTeammatesOf(wp)
                                                   .closestFirst(beacon.getGroundLocation())
                                                   .limit(maxAllies)) {
                EffectUtils.playParticleLinkAnimation(crystal.getLocation().clone().add(0, .5, 0), ally.getLocation(), 20, 200, 20, 2);
                for (int i = 0; i < stacksGranted; i++) {
                    MercifulHex.giveMercifulHex(wp, ally);
                }
                stats.hexesGiven++;

            }
            Utils.playGlobalSound(crystal.getLocation(), Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 1, 2);
            EffectUtils.playCircularEffectAround(wp.getGame(), crystal.getLocation(), Particle.TOTEM_OF_UNDYING, 3, 1, 0.15, 4, 1, 4);
            EffectUtils.playCircularEffectAround(wp.getGame(), crystal.getLocation(), Particle.HAPPY_VILLAGER, 1, 1, 0.1, 8, 1, 3);
        }
        if (ticksElapsed > 0 && pveMasterUpgrade) {
            if (ticksElapsed % 120 == 0) {
                chasingItemDamage.start(wp.getGame());
            } else if (ticksElapsed % 150 == 0) {
                chasingItemHealing.start(wp.getGame());
            }
        }
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SanctifiedBeaconBranch(abilityTree, this);
    }

    @Override
    public SanctifiedBeaconStats getAbilityStats() {
        return stats;
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

    public int getStacksGranted() {
        return stacksGranted;
    }

    public void setStacksGranted(int stacksGranted) {
        this.stacksGranted = stacksGranted;
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

    public static class SanctifiedBeaconStats extends AbstractAbilityStats<SanctifiedBeacon, SanctifiedBeaconStats> {

        @Field("hexes_given")
        private int hexesGiven = 0;

        @Field("crits_reduced")
        private int critsReduced = 0;

        @Field("crit_damage_reduced")
        private float critDamageReduced = 0;

        @Override
        public Class<SanctifiedBeaconStats> getClazz() {
            return SanctifiedBeaconStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Hexes Given", hexesGiven));
            statsDisplay.add(new AbilityStatDisplay("Crits Reduced", critsReduced));
            statsDisplay.add(new AbilityStatDisplay("Crit Damage Reduced", critDamageReduced));
            return statsDisplay;
        }

        @Override
        public SanctifiedBeaconStats merge(SanctifiedBeaconStats other, int multiplier) {
            SanctifiedBeaconStats stats = super.merge(other, multiplier);
            stats.hexesGiven = this.hexesGiven + other.hexesGiven * multiplier;
            stats.critsReduced = this.critsReduced + other.critsReduced * multiplier;
            stats.critDamageReduced = this.critDamageReduced + other.critDamageReduced * multiplier;
            return stats;
        }

        @Override
        public SanctifiedBeaconStats create() {
            return new SanctifiedBeaconStats();
        }

    }

}
