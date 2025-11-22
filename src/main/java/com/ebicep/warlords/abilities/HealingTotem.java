package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.HealingTotemBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HealingTotem extends AbstractTotem implements Duration, HitBox, Heals<HealingTotem.HealingValues>, AbilityStats<HealingTotem, HealingTotem.HealingTotemStats> {

    private final HealingTotemStats stats = new HealingTotemStats();
    private final HealingValues healingValues = new HealingValues();
    private FloatModifiable radius = new FloatModifiable(7);
    private int tickDuration = 100;
    private int crippleDuration = 6;
    private float healingIncrement = 25;
    private int healingPeriod;

    public HealingTotem() {
        super(AbstractAbilityBuilder.create("healingTotem").pvp());
    }

    @Override
    protected void playSound(WarlordsEntity warlordsEntity, Location location) {
        Utils.playGlobalSound(location, "shaman.totem.activation", 2, 1);
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.PINK_TULIP);
    }

    @Override
    protected void onActivation(WarlordsEntity wp, ArmorStand totemStand) {
        float rad = radius.getCalculatedValue();
        HealingTotemData data = new HealingTotemData(this, wp, totemStand);
        CircleEffect circle = new CircleEffect(wp.getGame(),
                wp.getTeam(),
                totemStand.getLocation().add(0, 1, 0),
                rad,
                new CircumferenceEffect(Particle.HAPPY_VILLAGER, Particle.DUST).particlesPerCircumference(.7)
        );
        RegularCooldown<HealingTotemData> healingTotemCooldown = new RegularCooldown<>(
                name,
                "TOTEM",
                HealingTotemData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    Utils.playGlobalSound(totemStand.getLocation(), Sound.ENTITY_BLAZE_DEATH, 1.2f, 0.7f);
                    Utils.playGlobalSound(totemStand.getLocation(), "shaman.heal.impact", 2, 1);
                    FallingBlockWaveEffect.create(totemStand.getLocation().clone().add(0, 1, 0), 3, 8, Material.SPRUCE_SAPLING);
                    List<WarlordsEntity> toHeal = PlayerFilter.entitiesAround(totemStand, rad, rad, rad).aliveTeammatesOf(wp).toList();
                    toHeal.forEach((nearPlayer) -> {
                        stats.playersHealed++;
                        nearPlayer.addInstance(InstanceBuilder
                                .healing()
                                .ability(this)
                                .source(wp)
                                .value(healingValues.totemHealing)
                                .customFlags(new CustomInstanceFlags.PlayersEffectedInstanceFlag(toHeal))
                        ).ifPresent(warlordsDamageHealingFinalEvent -> {
                            data.amountHealed += warlordsDamageHealingFinalEvent.getValue();
                        });
                    });

                    if (data.amountHealed >= 20000) {
                        ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.JUNGLE_HEALING);
                    }

                    if (pveMasterUpgrade2) {
                        for (WarlordsEntity enemy : PlayerFilter
                                .entitiesAround(totemStand, rad, rad, rad)
                                .aliveEnemiesOf(wp)
                        ) {
                            if (enemy instanceof WarlordsNPC npc) {
                                npc.setDamageResistance(npc.getSpec().getDamageResistance() - 15);
                            }
                            Utils.playGlobalSound(totemStand.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.5f, 1.2f);
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Void Totem")
                                    .source(wp)
                                    .min(1000)
                                    .max(1500)
                            );
                        }
                    }
                },
                cooldownManager -> {
                    totemStand.remove();
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (pveMasterUpgrade && ticksElapsed % 10 == 0) {
                        EffectUtils.playSphereAnimation(totemStand.getLocation(), rad, Particle.HAPPY_VILLAGER, 2);
                    }
                    if ((inPve && ticksElapsed % 30 == 0) || (!inPve && ticksElapsed % 20 == 0)) {
                        circle.setCenter(totemStand.getLocation().add(0, 1, 0));
                        circle.playEffects();
                    }
                    if (ticksElapsed % healingPeriod == 0) {
                        Utils.playGlobalSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, pveMasterUpgrade ? 0.4f : 0.9f);
                        EffectUtils.displayParticle(Particle.HAPPY_VILLAGER, totemStand.getLocation().clone().add(0, 1.6, 0), 3, 0.4, 0.2, 0.4, 0.05);
                        Location totemLoc = totemStand.getLocation();
                        totemLoc.add(0, 2, 0);
                        EffectUtils.playCylinderAnimation(totemLoc, rad, Particle.FIREWORK, 10, 1, 1);
                        int secondsElapsed = ticksElapsed / healingPeriod;
                        float healMultiplier = (float) Math.pow((1 - healingIncrement / 100f), secondsElapsed);
                        List<WarlordsEntity> toHeal = PlayerFilter.entitiesAround(totemStand, rad, rad, rad).aliveTeammatesOf(wp).toList();
                        toHeal.forEach(teammate -> {
                            stats.playersHealed++;
                            teammate.addInstance(InstanceBuilder
                                    .healing()
                                    .ability(this)
                                    .source(wp)
                                    .min(healingValues.totemHealing.getMinValue() * healMultiplier)
                                    .max(healingValues.totemHealing.getMaxValue() * healMultiplier)
                                    .crit(healingValues.totemHealing)
                                    .customFlags(new CustomInstanceFlags.PlayersEffectedInstanceFlag(toHeal))
                            ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                data.amountHealed += warlordsDamageHealingFinalEvent.getValue();
                            });
                        });

                        if (pveMasterUpgrade) {
                            PlayerFilter.entitiesAround(totemStand, rad, rad, rad).aliveEnemiesOf(wp).forEach(enemy -> {
                                enemy.addSpeedModifier(wp, "Totem Slowness", -50, 20);
                                if (enemy instanceof WarlordsNPC npc) {
                                    npc.setDamageResistance(npc.getSpec().getDamageResistance() - 2);
                                }
                                EffectUtils.playParticleLinkAnimation(enemy.getLocation(), totemStand.getLocation(), 255, 255, 255, 1);
                                enemy.getCooldownManager()
                                     .addCooldown(new RegularCooldown<>("Totem Crippling",
                                             "CRIP",
                                             HealingTotemData.class,
                                             data,
                                             wp,
                                             CooldownTypes.LOW_LEVEL_DEBUFF,
                                             cooldownManager -> {
                                             }, 20
                                     ).addModifier(Modifier.OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                                                 currentDamageValue.addMultiplicativeModifierMult(name, 0.5f);
                                             }
                                     ));
                            });
                        }

                        if (pveMasterUpgrade2) {
                            new CircleEffect(
                                    wp.getGame(),
                                    wp.getTeam(),
                                    totemStand.getLocation().clone().add(0, 1.5, 0),
                                    4,
                                    new CircumferenceEffect(Particle.PORTAL, Particle.PORTAL).particlesPerCircumference(1),
                                    new DoubleLineEffect(Particle.WITCH)
                            ).playEffects();
                            Utils.playGlobalSound(totemStand.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 0.6f);

                            for (WarlordsEntity enemy : PlayerFilter
                                    .entitiesAround(totemStand, rad, rad, rad)
                                    .aliveEnemiesOf(wp)
                            ) {
                                Utils.addKnockback("Totem Master", totemStand.getLocation(), enemy, 1.25, 0.08);
                            }

                            for (WarlordsEntity enemy : PlayerFilter
                                    .entitiesAround(totemStand, 4, 4, 4)
                                    .aliveEnemiesOf(wp)
                            ) {
                                enemy.addInstance(InstanceBuilder
                                        .damage()
                                        .cause("Void Totem")
                                        .min(500)
                                        .max(800)
                                        .source(wp)
                                );
                            }
                        }
                    }
                })
        );
        wp.getCooldownManager().addCooldown(healingTotemCooldown);
        if (inPve) {
            addSecondaryAbility(5, () -> {
                        Utils.playGlobalSound(totemStand.getLocation(), "paladin.hammeroflight.impact", 1.5f, 0.2f);
                FallingBlockWaveEffect.create(totemStand.getLocation().add(0, 1, 0), 7, 2, Material.SPRUCE_SAPLING);
                        PlayerFilter.entitiesAround(totemStand.getLocation(), rad, rad, rad).aliveEnemiesOf(wp).forEach((p) -> {
                            stats.playersCrippled++;
                            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Your Healing Totem has crippled ", NamedTextColor.GRAY))
                                                                          .append(Component.text(p.getName(), NamedTextColor.YELLOW))
                                                                          .append(Component.text("!", NamedTextColor.GRAY)));
                            p.getCooldownManager()
                             .addCooldown(new RegularCooldown<>("Totem Crippling", "CRIP", HealingTotemData.class, data, wp, CooldownTypes.LOW_LEVEL_DEBUFF, cooldownManager -> {
                             }, crippleDuration * 20
                             ).addModifier(Modifier.OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                                         currentDamageValue.addMultiplicativeModifierMult(name, 0.75f);
                                     }
                             ));
                        });
                    }, false, secondaryAbility -> !wp.getCooldownManager().hasCooldown(healingTotemCooldown) || wp.isDead()
            );
        }
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.crippleDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("crippleDuration"), int.class);
        this.healingIncrement = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healingIncrement"), float.class);
        this.healingPeriod = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healingPeriod"), int.class);
    }

    @Override
    public void updateDescription(Player player) {
        if (inPve) {
            description = AbilityDescriptionBuilder.create("Place a totem on the ground that pulses constantly, healing allies within ")
                                                   .blocks(radius)
                                                   .text(" for ")
                                                   .heal(healingValues.totemHealing)
                                                   .text(" health every second. The healing will gradually decrease by ")
                                                   .percent(healingIncrement, NamedTextColor.GREEN)
                                                   .text(" until the final proc which heals for the normal amount once again. Lasts ")
                                                   .durationTicks(tickDuration)
                                                   .text(".")
                                                   .emptyLine()
                                                   .text("Reactivating the ability causes your totem to pulse with immense force, crippling all enemies for ")
                                                   .durationSeconds(crippleDuration)
                                                   .text(". Crippled enemies deal ")
                                                   .percent(25, NamedTextColor.RED)
                                                   .text(" less damage.")
                                                   .build();
        } else {
            description = AbilityDescriptionBuilder.create("Place a totem on the ground that pulses constantly, healing nearby allies in a ")
                                                   .blocks(radius)
                                                   .text(" radius for ")
                                                   .heal(healingValues.totemHealing)
                                                   .text(" health every second. The healing will gradually decrease by ")
                                                   .percent(healingIncrement, NamedTextColor.GREEN)
                                                   .text(" until the final proc which heals for the normal amount once again. Lasts ")
                                                   .durationTicks(tickDuration)
                                                   .text(".")
                                                   .build();
        }
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HealingTotemBranch(abilityTree, this);
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        radius.tick();
        super.runEveryTick(warlordsEntity);
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
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public HealingTotemStats getAbilityStats() {
        return stats;
    }

    public int getHealingPeriod() {
        return healingPeriod;
    }

    public void setHealingPeriod(int healingPeriod) {
        this.healingPeriod = healingPeriod;
    }

    public float getHealingIncrement() {
        return healingIncrement;
    }

    public void setHealingIncrement(float healingIncrement) {
        this.healingIncrement = healingIncrement;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable totemHealing = new Value.RangedValueCritable(621, 728, 25, 175);

        private List<Value> values = List.of(totemHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.totemHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("totemHealing"), Value.RangedValueCritable.class);
            this.values = List.of(totemHealing);
        }

        public Value.RangedValueCritable getTotemHealing() {
            return totemHealing;
        }

    }

    public static class HealingTotemData extends TotemData<HealingTotem> {

        private float amountHealed = 0;

        public HealingTotemData(HealingTotem totem, WarlordsEntity owner, ArmorStand armorStand) {
            super(totem, owner, armorStand);
        }

    }

    public static class HealingTotemStats extends AbstractAbilityStats<HealingTotem, HealingTotemStats> {

        @Field("targets_healed")
        private int playersHealed = 0;

        @Field("targets_crippled")
        private int playersCrippled = 0;

        @Override
        public Class<HealingTotemStats> getClazz() {
            return HealingTotemStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Healed", playersHealed));
            statsDisplay.add(new AbilityStatDisplay("Targets Crippled", playersCrippled));
            return statsDisplay;
        }

        @Override
        public HealingTotemStats merge(HealingTotemStats other, int multiplier) {
            HealingTotemStats stats = super.merge(other, multiplier);
            stats.playersHealed = this.playersHealed + other.playersHealed * multiplier;
            stats.playersCrippled = this.playersCrippled + other.playersCrippled * multiplier;
            return stats;
        }

        @Override
        public HealingTotemStats create() {
            return new HealingTotemStats();
        }

    }

}
