package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsHammerToCrownEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.protector.HammerOfLightBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HammerOfLight extends AbstractAbility implements OrangeAbilityIcon, Duration, Damages<HammerOfLight.DamageValues>, Heals<HammerOfLight.HealingValues>, AbilityStats<HammerOfLight, HammerOfLight.HammerOfLightStats> {

    private final HammerOfLightStats stats = new HammerOfLightStats();
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private FloatModifiable hammerRadius = new FloatModifiable(6);
    private FloatModifiable crownRadius = new FloatModifiable(6);
    private int tickDuration = 200;
    private int crownEnergyReduction = 10;
    private float crownBonusHealing = 35;

    public HammerOfLight() {
        super(AbstractAbilityBuilder.create("hammerOfLight").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.hammerRadius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.crownRadius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("crownRadius"), float.class));
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hammerRadius"), int.class);
        this.crownEnergyReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("crownEnergyReduction"), int.class);
        this.crownBonusHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("crownBonusHealing"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Block targetBlock = !(wp instanceof WarlordsPlayer) ? LocationUtils.getGroundLocation(wp.getLocation()).add(0, -1, 0).getBlock() : Utils.getTargetBlock(wp, 25);
        if (targetBlock.getType() == Material.AIR) {
            return false;
        }
        Utils.playGlobalSound(wp.getLocation(), "paladin.hammeroflight.impact", 2, 0.85f);
        Location location = targetBlock.getLocation().clone().add(.6, 0, .6).clone();
        if (location.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
            if (location.clone().add(1, 0, 0).getBlock().getType() == Material.AIR) {
                location.add(.6, 0, 0);
            } else if (location.clone().add(-1, 0, 0).getBlock().getType() == Material.AIR) {
                location.add(-.6, 0, 0);
            } else if (location.clone().add(0, 0, 1).getBlock().getType() == Material.AIR) {
                location.add(0, 0, .6);
            } else if (location.clone().add(0, 0, -1).getBlock().getType() == Material.AIR) {
                location.add(0, 0, -.6);
            }
        }
        float hammerRad = hammerRadius.getCalculatedValue();
        float crownRad = crownRadius.getCalculatedValue();
        CircleEffect circleEffect = new CircleEffect(wp.getGame(),
                wp.getTeam(),
                location,
                hammerRad,
                new CircumferenceEffect(Particle.HAPPY_VILLAGER, Particle.DUST),
                new LineEffect(location.clone().add(0, 2.3, 0), Particle.EFFECT)
        );
        BukkitTask particleTask = wp.getGame().registerGameTask(circleEffect::playEffects, 0, 1);
        ArmorStand hammer = spawnHammer(location);
        HammerOfLightData data = new HammerOfLightData(location);
        RegularCooldown<HammerOfLightData> hammerOfLightCooldown = new RegularCooldown<>(name,
                "HAMMER",
                HammerOfLightData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    hammer.remove();
                    particleTask.cancel();
                    for (ProtectorsStrike protectorsStrike : wp.getAbilitiesMatching(ProtectorsStrike.class)) {
                        protectorsStrike.getEnergyCost().removeModifier("Hammer of Light");
                    }
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (pveMasterUpgrade2 && ticksElapsed % 5 == 0) {
                        for (WarlordsEntity allyTarget : PlayerFilter.entitiesAround(data.getLocation(), hammerRad, hammerRad, hammerRad).aliveTeammatesOfExcludingSelf(wp)) {
                            allyTarget.getSpeed().removeNegativeModifiers();
                            CooldownManager allyTargetCooldownManager = allyTarget.getCooldownManager();
                            allyTargetCooldownManager.removeDebuffCooldowns();
                            allyTargetCooldownManager.removeCooldownByObject(data);
                            allyTargetCooldownManager.addCooldown(new RegularCooldown<>("Hammer of Disillusion",
                                    null,
                                    HammerOfLightData.class,
                                    data,
                                    wp,
                                    CooldownTypes.ABILITY,
                                    cooldownManager -> {
                                    },
                                    5
                            ) {

                                @Override
                                protected Listener getListener() {
                                    return CooldownManager.getDefaultDebuffImmunityListener(allyTarget);
                                }
                            });
                        }
                    }
                    if (ticksElapsed % 20 != 0) {
                        return;
                    }
                    if (data.isCrownOfLight()) {
                        if (!wp.isAlive()) {
                            return;
                        }
                        for (WarlordsEntity crownTarget : PlayerFilter.entitiesAround(wp.getLocation(), crownRad, crownRad, crownRad).isAlive()) {
                            if (wp.isTeammate(crownTarget)) {
                                stats.targetsHealed++;
                                crownTarget.addInstance(InstanceBuilder.healing()
                                                                       .cause("Crown of Light")
                                                                       .source(wp)
                                                                       .min(healingValues.hammerHealing.getMinValue() * convertToMultiplicationDecimal(crownBonusHealing))
                                                                       .max(healingValues.hammerHealing.getMaxValue() * convertToMultiplicationDecimal(crownBonusHealing))
                                                                       .crit(healingValues.hammerHealing)).ifPresent(warlordsDamageHealingFinalEvent -> {
                                    data.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                                });
                            } else {
                                if (pveMasterUpgrade2) {
                                    giveHammerOfDisillusionEffect(crownTarget, wp);
                                }
                            }
                        }
                    } else {
                        for (WarlordsEntity hammerTarget : PlayerFilter.entitiesAround(location, hammerRad, hammerRad, hammerRad).isAlive()) {
                            if (wp.isTeammate(hammerTarget)) {
                                stats.targetsHealed++;
                                hammerTarget.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.hammerHealing))
                                            .ifPresent(warlordsDamageHealingFinalEvent -> {
                                                data.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                                            });
                            } else {
                                stats.targetsDamaged++;
                                hammerTarget.addInstance(InstanceBuilder.damage().ability(this).source(wp).value(damageValues.hammerDamage));
                                if (pveMasterUpgrade2) {
                                    giveHammerOfDisillusionEffect(hammerTarget, wp);
                                }
                            }
                        }
                    }
                })
        ) {

            @Override
            protected Listener getListener() {
                return new Listener() {

                    @EventHandler
                    public void onDamageHeal(WarlordsDamageHealingEvent event) {
                        if (!event.isDamageInstance() || !event.getSource().equals(wp)) {
                            return;
                        }
                        if (data.isCrownOfLight) {
                            return;
                        }
                        if (event.getWarlordsEntity().getLocation().distanceSquared(location) > hammerRad * hammerRad) {
                            return;
                        }
                        event.getFlags().add(InstanceFlags.PIERCE);
                    }

                    @EventHandler
                    public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                        if (event.getSource() != wp) {
                            return;
                        }
                        if (!event.getInstanceFlags().contains(InstanceFlags.PIERCE)) {
                            return;
                        }
                        WarlordsEntity target = event.getWarlordsEntity();
                        List<AbstractCooldown<?>> cooldowns = event.getPlayerCooldowns()
                                                                   .stream()
                                                                   .map(WarlordsDamageHealingFinalEvent.CooldownRecord::getAbstractCooldown)
                                                                   .collect(Collectors.toList());
                        if (new CooldownFilter<>(cooldowns, RegularCooldown.class).filterCooldownClass(Intervene.class)
                                                                                  .filter(regularCooldown -> !Objects.equals(regularCooldown.getFrom(), target))
                                                                                  .findAny()
                                                                                  .isPresent()) {
                            stats.intervenesPierced++;
                        }
                        if (new CooldownFilter<>(cooldowns, RegularCooldown.class).filterCooldownClass(Shield.class).filter(RegularCooldown::hasTicksLeft).findAny().isPresent()) {
                            stats.shieldsPierced++;
                        }
                    }
                };
            }
        };
        wp.getCooldownManager().addCooldown(hammerOfLightCooldown);
        location.add(0, 1, 0);
        addSecondaryAbility(3, () -> {
                    if (!wp.isAlive() || !wp.getCooldownManager().hasCooldown(hammerOfLightCooldown)) {
                        return;
                    }
                    hammer.remove();
                    particleTask.cancel();
                    Utils.playGlobalSound(wp.getLocation(), "warrior.revenant.orbsoflife", 2, 0.15f);
                    Utils.playGlobalSound(wp.getLocation(), "mage.firebreath.activation", 2, 0.25f);
                    hammerOfLightCooldown.setRemoveOnDeath(true);
                    hammerOfLightCooldown.addTriConsumer((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 6 == 0) {
                            double angle = 0;
                            for (int i = 0; i < 9; i++) {
                                double x = .4 * Math.cos(angle);
                                double z = .4 * Math.sin(angle);
                                angle += 40;
                                Vector v = new Vector(x, 2, z);
                                Location loc = wp.getLocation().clone().add(v);
                                loc.getWorld().spawnParticle(Particle.EFFECT, loc, 1, 0, 0, 0, 0, null, true);
                            }
                            new CircleEffect(wp.getGame(),
                                    wp.getTeam(),
                                    wp.getLocation().add(0, 0.75f, 0),
                                    hammerRad / 2f,
                                    new CircumferenceEffect(Particle.EFFECT).particlesPerCircumference(0.5f)
                            ).playEffects();
                        }
                    });
                    data.setCrownOfLight(true);
                    hammerOfLightCooldown.setNameAbbreviation("CROWN");
                    // prot strike energy reduction
                    for (ProtectorsStrike protectorsStrike : wp.getAbilitiesMatching(ProtectorsStrike.class)) {
                        protectorsStrike.getEnergyCost().addAdditiveModifier("Hammer of Light", -crownEnergyReduction);
                    }
                    if (pveMasterUpgrade) {
                        pulseHeal(wp, 20, 1.5, data);
                        pulseHeal(wp, 40, 2.5, data);
                        pulseHeal(wp, 60, 3.5, data);
                        pulseHeal(wp, 80, 4.5, data);
                    }
            Bukkit.getPluginManager().callEvent(new WarlordsHammerToCrownEvent(wp, hammerOfLightCooldown));
                }, false, secondaryAbility -> !wp.getCooldownManager().hasCooldown(hammerOfLightCooldown) || wp.isDead()
        );
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Throw down a Hammer of Light on the ground, dealing ")
                                               .damage(damageValues.hammerDamage)
                                               .text(" damage every ")
                                               .durationSeconds(1)
                                               .text(" to enemies and healing allies for ")
                                               .heal(healingValues.hammerHealing)
                                               .text(" every ")
                                               .durationSeconds(1)
                                               .text(" within ")
                                               .blocks(hammerRadius)
                                               .text(". Your attacks pierces shields and defenses of enemies standing on top of the Hammer of Light. Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .emptyLine()
                                               .text("Recast to turn your hammer into Crown of Light. Removing the damage and piercing BUT increasing the healing by ")
                                               .percent(crownBonusHealing, NamedTextColor.GREEN)
                                               .text(" and reducing the energy cost of your Protector's Strike by ")
                                               .energy(crownEnergyReduction)
                                               .text(". You cannot put the Hammer of Light back down after you converted it.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HammerOfLightBranch(abilityTree, this);
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        hammerRadius.tick();
        crownRadius.tick();
        super.runEveryTick(warlordsEntity);
    }

    private void pulseHeal(WarlordsEntity wp, int delay, double radiusMultiplier, HammerOfLightData hammerOfLightData) {
        new GameRunnable(wp.getGame()) {

            @Override
            public void run() {
                Utils.playGlobalSound(wp.getLocation(), "warrior.revenant.orbsoflife", 2, 0.4f);
                EffectUtils.strikeLightning(wp.getLocation(), false, delay / 10);
                float rad = hammerRadius.getCalculatedValue();
                EffectUtils.playHelixAnimation(wp.getLocation(), rad * radiusMultiplier, Particle.WITCH, 1, 20);
                new CircleEffect(wp.getGame(),
                        wp.getTeam(),
                        wp.getLocation().add(0, 0.75f, 0),
                        rad * radiusMultiplier,
                        new CircumferenceEffect(Particle.EFFECT).particlesPerCircumference(1)
                ).playEffects();
                for (WarlordsEntity allyTarget : PlayerFilter.entitiesAround(wp.getLocation(), rad * radiusMultiplier, rad * radiusMultiplier, rad * radiusMultiplier)
                                                             .aliveTeammatesOf(wp)) {
                    stats.targetsHealed++;
                    allyTarget.addInstance(InstanceBuilder.healing()
                                                          .cause("Hammer of Illusion")
                                                          .source(wp)
                                                          .min(healingValues.hammerHealing.getMinValue() * 5)
                                                          .max(healingValues.hammerHealing.getMaxValue() * 5)
                                                          .critChance(20)
                                                          .critMultiplier(150)).ifPresent(warlordsDamageHealingFinalEvent -> {
                        hammerOfLightData.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                    });
                }
                for (WarlordsEntity enemyTarget : PlayerFilter.entitiesAround(wp.getLocation(), rad * radiusMultiplier, rad * radiusMultiplier, rad * radiusMultiplier)
                                                              .aliveEnemiesOf(wp)) {
                    enemyTarget.addInstance(InstanceBuilder.damage()
                                                           .cause("Hammer of Illusion")
                                                           .source(wp)
                                                           .min(damageValues.hammerDamage.getMinValue() * 5)
                                                           .max(damageValues.hammerDamage.getMaxValue() * 5)
                                                           .critChance(20)
                                                           .critMultiplier(150));
                }
            }
        }.runTaskLater(delay);
    }

    public float getCrownBonusHealing() {
        return crownBonusHealing;
    }

    public ArmorStand spawnHammer(Location location) {
        Location newLocation = location.clone();
        for (int i = 0; i < 10; i++) {
            if (newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 0)).getType() == Material.AIR) {
                newLocation.add(0, -1, 0);
            }
        }
        newLocation.add(0, -1, 0);
        return Utils.spawnArmorStand(newLocation.clone().add(.25, 1.9, -.25), armorStand -> {
                    armorStand.setRightArmPose(new EulerAngle(20.25, 0, 0));
                    armorStand.getEquipment().setItemInMainHand(new ItemStack(Material.STRING));
                    armorStand.setMarker(true);
                }
        );
    }

    private static void giveHammerOfDisillusionEffect(WarlordsEntity hammerTarget, @Nonnull WarlordsEntity wp) {
        hammerTarget.getCooldownManager().removeCooldownByName("Hammer of Disillusion");
        hammerTarget.getCooldownManager().addCooldown(new RegularCooldown<>("Hammer of Disillusion", null, HammerOfLight.class, null, wp, CooldownTypes.DEBUFF, cooldownManager -> {
        }, 20
        ) {

            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.15f;
            }
        });
    }

    public void setCrownBonusHealing(float crownBonusHealing) {
        this.crownBonusHealing = crownBonusHealing;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public HammerOfLightStats getAbilityStats() {
        return stats;
    }

    public FloatModifiable getCrownRadius() {
        return crownRadius;
    }

    public FloatModifiable getHammerRadius() {
        return hammerRadius;
    }

    public static class HammerOfLightData {

        private final Location location;

        private boolean isCrownOfLight;

        private float amountHealed;

        public HammerOfLightData(Location location) {
            this.location = location;
        }

        public boolean isHammer() {
            return !isCrownOfLight;
        }

        public boolean isCrownOfLight() {
            return isCrownOfLight;
        }

        public void setCrownOfLight(boolean crownOfLight) {
            isCrownOfLight = crownOfLight;
        }

        public Location getLocation() {
            return location;
        }

        public float getAmountHealed() {
            return amountHealed;
        }

        public void addAmountHealed(float amountHealed) {
            this.amountHealed += amountHealed;
        }

    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable hammerDamage = new Value.RangedValueCritable(178, 244, 20, 175);

        private List<Value> values = List.of(hammerDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.hammerDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("hammerDamage"), Value.RangedValueCritable.class);
            this.values = List.of(hammerDamage);
        }

        public Value.RangedValueCritable getHammerDamage() {
            return hammerDamage;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable hammerHealing = new Value.RangedValueCritable(178, 244, 20, 175);

        private List<Value> values = List.of(hammerHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.hammerHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("hammerHealing"),
                    Value.RangedValueCritable.class
            );
            this.values = List.of(hammerHealing);
        }

        public Value.RangedValueCritable getHammerHealing() {
            return hammerHealing;
        }

    }

    public static class HammerOfLightStats extends AbstractAbilityStats<HammerOfLight, HammerOfLightStats> {

        @Field("times_crowned")
        private int timesCrowned = 0;

        @Field("targets_healed")
        private int targetsHealed = 0;

        @Field("targets_damaged")
        private int targetsDamaged = 0;

        @Field("shields_pierced")
        private int shieldsPierced = 0;

        @Field("intervenes_pierced")
        private int intervenesPierced = 0;

        @Override
        public Class<HammerOfLightStats> getClazz() {
            return HammerOfLightStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Crowned", timesCrowned));
            statsDisplay.add(new AbilityStatDisplay("Targets Healed", targetsHealed));
            statsDisplay.add(new AbilityStatDisplay("Targets Damaged", targetsDamaged));
            statsDisplay.add(new AbilityStatDisplay("Shields Pierced", shieldsPierced));
            statsDisplay.add(new AbilityStatDisplay("Intervenes Pierced", intervenesPierced));
            return statsDisplay;
        }

        @Override
        public HammerOfLightStats merge(HammerOfLightStats other, int multiplier) {
            HammerOfLightStats stats = super.merge(other, multiplier);
            stats.targetsHealed = this.targetsHealed + other.targetsHealed * multiplier;
            stats.targetsDamaged = this.targetsDamaged + other.targetsDamaged * multiplier;
            stats.timesCrowned = this.timesCrowned + other.timesCrowned * multiplier;
            stats.shieldsPierced = this.shieldsPierced + other.shieldsPierced * multiplier;
            stats.intervenesPierced = this.intervenesPierced + other.intervenesPierced * multiplier;
            return stats;
        }

        @Override
        public HammerOfLightStats create() {
            return new HammerOfLightStats();
        }

    }

}
