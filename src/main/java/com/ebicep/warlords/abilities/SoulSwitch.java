package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerSwapEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.flags.DynamicFlags;
import com.ebicep.warlords.pve.mobs.flags.Unswappable;
import com.ebicep.warlords.pve.mobs.player.Animus;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.SoulSwitchBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoulSwitch extends AbstractAbility implements BlueAbilityIcon, HitBox, Heals<SoulSwitch.HealingValues>, AbilityStats<SoulSwitch, SoulSwitch.SoulSwitchStats> {

    private final SoulSwitchStats stats = new SoulSwitchStats();
    private final HealingValues healingValues = new HealingValues();
    private FloatModifiable radius = new FloatModifiable(13);
    private FloatModifiable radiusFlag = new FloatModifiable(3.5f);
    private float verticalLimit;
    private float verticalLimitFlag;
    private int blindnessTicks = 30;
    private int damageReduction;
    private int damageReductionTickDuration;
    // pve
    private int invisTicks = 30;
    private int decoyMaxTicksLived = 60;

    private boolean canSwitchToCarrier = false;

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        float maxHorizontal = wp.hasFlag() ? radiusFlag.getCalculatedValue() : radius.getCalculatedValue();
        float maxVertical = wp.hasFlag() ? verticalLimitFlag : verticalLimit;
        for (WarlordsEntity swapTarget : PlayerFilter
                .entitiesAround(wp.getLocation(), maxHorizontal, maxVertical, maxHorizontal)
                .aliveEnemiesOf(wp)
                .requireLineOfSight(wp)
                .lookingAtFirst(wp)
        ) {
            if (!canSwitchToCarrier && swapTarget.getCarriedFlag() != null) {
                wp.sendMessage(Component.text(" You cannot Soul Switch with a player holding the flag!", NamedTextColor.RED));
                continue;
            }
            if (swapTarget instanceof WarlordsNPC warlordsNPC) {
                AbstractMob mob = warlordsNPC.getMob();
                if (mob instanceof Unswappable || mob.getDynamicFlags().contains(DynamicFlags.UNSWAPPABLE) || mob instanceof BossMob || mob instanceof BossMinionMob) {
                    wp.sendMessage(Component.text("You cannot Soul Switch with that mob!", NamedTextColor.RED));
                    continue;
                }
            }
            Location swapLocation = swapTarget.getLocation();
            Location ownLocation = wp.getLocation();
            Location start = new Location(wp.getWorld(), ownLocation.getX(), ownLocation.getY(), ownLocation.getZ(), swapLocation.getYaw(), swapLocation.getPitch());
            Location end = new Location(swapLocation.getWorld(), swapLocation.getX(), swapLocation.getY(), swapLocation.getZ(), ownLocation.getYaw(), ownLocation.getPitch());
            WarlordsPlayerSwapEvent playerSwapEvent = new WarlordsPlayerSwapEvent(wp, swapTarget, start, end);
            Bukkit.getPluginManager().callEvent(playerSwapEvent);
            if (playerSwapEvent.isCancelled()) {
                return true;
            }
            Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 1.5f);
            EffectUtils.playCylinderAnimation(swapLocation, 1.05, Particle.CLOUD, 1);
            EffectUtils.playCylinderAnimation(ownLocation, 1.05, Particle.CLOUD, 1);
            swapTarget.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindnessTicks, 0, true, false));
            swapTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                    .append(Component.text(" You've been Soul Swapped by ", NamedTextColor.GRAY))
                    .append(Component.text(wp.getName(), NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY)));
            swapTarget.teleport(start);
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" You swapped with ", NamedTextColor.GRAY))
                    .append(Component.text(swapTarget.getName(), NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY)));
            wp.teleport(end);
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Soul Switch Res",
                    "SWITCH",
                    SoulSwitch.class,
                    null,
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {},
                    damageReductionTickDuration
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * convertToDivisionDecimal(damageReduction);
                }
            });
            if (swapTarget instanceof WarlordsNPC npc) {
                PveOption pveOption = wp.getGame().getOption(PveOption.class).stream().findFirst().orElse(null);
                if (pveOption != null) {
                    wp.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.switchHealing));
                    wp.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30, 0, true, false));
                    pveOption.despawnMob(npc.getMob());
                    Animus animus = new Animus(ownLocation, wp, swapTarget);
                    pveOption.spawnNewMob(animus, wp.getTeam());
                    addSecondaryAbility(2, () -> {
                                if (wp.isAlive()) {
                                    animus.getWarlordsNPC().die(animus.getWarlordsNPC(), WarlordsDeathEvent.DeathInfoBuilder.create().setForced(true));
                                    for (WarlordsEntity enemy : PlayerFilter.entitiesAround(animus.getWarlordsNPC().getLocation(), 4, 4, 4).aliveEnemiesOf(wp)) {
                                        enemy.addInstance(InstanceBuilder.damage().cause("Animus").source(wp).min(400).max(600));
                                    }
                                }
                            }, false, secondaryAbility -> animus.getWarlordsNPC().isDead()
                    );
                    if (pveMasterUpgrade) {
                        wp.getCooldownManager().removeCooldown(SoulSwitch.class, false);
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Soul Burst",
                                "SOUL BURST",
                                SoulSwitch.class,
                                null,
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager -> {},
                                20 * 20
                        ) {

                            @Override
                            public float modifyDamageAfterInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                if (event.getCause().equals("Judgement Strike")) {
                                    double speed = animus.getWarlordsNPC()
                                            .getSpeed()
                                            .getModifiers()
                                            .stream()
                                            .filter(modifier -> modifier.getModifier() > 0)
                                            .mapToDouble(MotionModifier::getModifier)
                                            .sum();
                                    float damageBoost = Math.min(1.1f, (float) (1 + (speed * 0.5f) / 100));
                                    return currentDamageValue * damageBoost;
                                }
                                return currentDamageValue;
                            }
                        });
                    }
                    if (pveMasterUpgrade2) {
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>("Tricky Switch", null, SoulSwitch.class, null, wp, CooldownTypes.ABILITY, cooldownManager -> {
                        }, 10 * 60 * 20, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (animus.getWarlordsNPC().isDead()) {
                                cooldown.setTicksLeft(0);
                            }
                        })
                        ) {

                            @Override
                            public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                                return currentCritChance + 15;
                            }
                        });
                        animus.getWarlordsNPC()
                              .getCooldownManager()
                              .addCooldown(new PermanentCooldown<>("Tricky Switch", null, SoulSwitch.class, null, wp, CooldownTypes.ABILITY, cooldownManager -> {
                              }, false
                              ) {

                                  @Override
                                  public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                                      if (event.getCause().equals("Judgement Strike")) {
                                          wp.addEnergy(wp, "Tricky Switch", 10);
                                          float heal = currentDamageValue * .1f;
                                          wp.addInstance(InstanceBuilder.healing().cause("Tricky Switch").source(wp).value(heal));
                                      }
                                  }
                              });
                    }
                }
            }
            if (pveMasterUpgrade) {
                wp.getCooldownManager().addCooldown(new RegularCooldown<>("Soul Burst", "SOUL", SoulSwitch.class, null, wp, CooldownTypes.BUFF, cooldownManager -> {
                }, cooldownManager -> {
                    wp.removePotionEffect(PotionEffectType.INVISIBILITY);
                }, 5 * 20, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        wp.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticksLeft, 0, true, false));
                    }
                })
                ) {

                    @Override
                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 0.5f;
                    }
                });
                PlayerFilter.entitiesAround(swapLocation, 3, 3, 3)
                            .aliveTeammatesOf(wp)
                            .forEach(warlordsEntity -> warlordsEntity.addSpeedModifier(wp, "Shadow Burst", 25, 3 * 20));
                PlayerFilter.entitiesAround(ownLocation, 3, 3, 3)
                            .aliveTeammatesOf(wp)
                            .forEach(warlordsEntity -> warlordsEntity.addSpeedModifier(wp, "Shadow Burst", 25, 3 * 20));
            }
            return true;
        }
        return false;
    }

    public SoulSwitch() {
        super(AbstractAbilityBuilder.create("soulSwitch").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.radiusFlag = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radiusFlag"), float.class));
        this.verticalLimit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("verticalLimit"), float.class);
        this.verticalLimitFlag = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("verticalLimitFlag"), float.class);
        this.blindnessTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("blindnessTicks"), int.class);
        this.decoyMaxTicksLived = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("decoyMaxTicksLived"), int.class);
        this.invisTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("invisTicks"), int.class);
        this.damageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReduction"), int.class);
        this.damageReductionTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReductionTickDuration"), int.class);
    }

    public void setCanSwitchToCarrier(boolean canSwitchToCarrier) {
        this.canSwitchToCarrier = canSwitchToCarrier;
    }

    public int getDamageReduction() {
        return damageReduction;
    }

    public void setDamageReduction(int damageReduction) {
        this.damageReduction = damageReduction;
    }

    public float getVerticalLimit() {
        return verticalLimit;
    }

    public void setVerticalLimit(float verticalLimit) {
        this.verticalLimit = verticalLimit;
    }

    @Override
    public void updateDescription(Player player) {
        if (inPve) {
            description = AbilityDescriptionBuilder.create("Switch locations with an enemy, stunning them for ")
                                                   .durationTicks(blindnessTicks)
                                                   .text(". Upon swapping, gain ")
                                                   .percent(damageReduction, NamedTextColor.RED)
                                                   .text(" damage reduction for ")
                                                   .durationTicks(damageReductionTickDuration)
                                                   .text(" and self heal for ")
                                                   .heal(healingValues.switchHealing)
                                                   .text(" health, go invisible for ")
                                                   .durationTicks(invisTicks)
                                                   .text(", and transform the swapped enemy into your own Animus. The Animus will inherit the max HP of the mob swapped and your current movement speed when swapped, no longer has its original stats/abilities, and will use Judgment Strike every 2 seconds based on the current your own Judgment Strike. " + "Enemies cannot target the Animus, and only 1 Animus can exist at a time. " + "For every enemy the Animus defeats, reduce the cooldown of Soul Switch by 1 second.")
                                                   .maxRange(radius)
                                                   .build();
        } else {
            description = AbilityDescriptionBuilder.create("Switch locations with an enemy, blinding them for ")
                                                   .durationTicks(blindnessTicks)
                                                   .text(". Upon swapping, gain ")
                                                   .percent(damageReduction, NamedTextColor.RED)
                                                   .text(" damage reduction for ")
                                                   .durationTicks(damageReductionTickDuration)
                                                   .text(".")
                                                   .maxRange(radius)
                                                   .text(" Soul Switch has low vertical range.")
                                                   .build();
        }
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SoulSwitchBranch(abilityTree, this);
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public SoulSwitchStats getAbilityStats() {
        return stats;
    }

    public int getBlindnessTicks() {
        return blindnessTicks;
    }

    public void setBlindnessTicks(int blindnessTicks) {
        this.blindnessTicks = blindnessTicks;
    }

    public int getDecoyMaxTicksLived() {
        return decoyMaxTicksLived;
    }

    public void setDecoyMaxTicksLived(int decoyMaxTicksLived) {
        this.decoyMaxTicksLived = decoyMaxTicksLived;
    }

    public int getInvisTicks() {
        return invisTicks;
    }

    public void setInvisTicks(int invisTicks) {
        this.invisTicks = invisTicks;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable switchHealing = new Value.RangedValueCritable(300, 500, 15, 175);

        private List<Value> values = List.of(switchHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.switchHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("switchHealing"),
                    Value.RangedValueCritable.class
            );
            this.values = List.of(switchHealing);
        }

        public Value.RangedValueCritable getSwitchHealing() {
            return switchHealing;
        }

    }

    public static class SoulSwitchStats extends AbstractAbilityStats<SoulSwitch, SoulSwitchStats> {

        @Override
        public Class<SoulSwitchStats> getClazz() {
            return SoulSwitchStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public SoulSwitchStats merge(SoulSwitchStats other, int multiplier) {
            SoulSwitchStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public SoulSwitchStats create() {
            return new SoulSwitchStats();
        }

    }

}
