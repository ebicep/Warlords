package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.DeathsDebtBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class DeathsDebt extends AbstractTotem implements Duration, AbilityStats<DeathsDebt, DeathsDebt.DeathsDebtStats> {

    public static final ItemStack BLUE_TOTEM = new ItemStack(Material.COPPER_BLOCK);
    public static final ItemStack PURPLE_TOTEM = new ItemStack(Material.CHISELED_COPPER);
    private final DeathsDebtStats stats = new DeathsDebtStats();
    private int tickDuration = 120;
    private int respiteRadius = 10;
    private int debtRadius = 8;
    private float damagePercent = 15;
    private float delayedDamageTaken = 90;
    private int debtTickDuration = 120;

    public DeathsDebt() {
        super(AbstractAbilityBuilder.create("deathsDebt").pvp());
    }

    @Override
    protected void playSound(WarlordsEntity warlordsEntity, Location location) {
        //TODO find the right sound - this aint right chief
        Utils.playGlobalSound(location, "shaman.chainlightning.impact", 2, 2);
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return BLUE_TOTEM;
    }

    @Override
    protected void onActivation(WarlordsEntity wp, ArmorStand totemStand) {
        final int duration = tickDuration + (2 * Math.round(wp.getCurrentHealth() / wp.getMaxHealth())) * 20;
        CircleEffect circleEffect = new CircleEffect(wp,
                totemStand.getLocation().clone().add(0, 1.25, 0),
                respiteRadius,
                new CircumferenceEffect(Particle.EFFECT),
                new DoubleLineEffect(Particle.DUST)
        );
        if (wp.isInPve()) {
            for (WarlordsEntity we : PlayerFilter.entitiesAround(totemStand.getLocation(), respiteRadius, respiteRadius, respiteRadius).aliveEnemiesOf(wp).closestFirst(wp)) {
                if (we instanceof WarlordsNPC) {
                    ((WarlordsNPC) we).getMob().setTarget(wp);
                }
            }
        }
        DeathsDebtData data = new DeathsDebtData(this, wp, totemStand);
        RegularCooldown<DeathsDebtData> spiritsRespiteCooldown = new RegularCooldown<>("Spirits' Respite",
                "RESP",
                DeathsDebtData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManagerRespite -> {
                    Optional<RegularCooldown> cd = new CooldownFilter<>(cooldownManagerRespite, RegularCooldown.class).filterCooldownObject(data).findAny();
                    if (wp.isDead() || wp.getWorld() != totemStand.getWorld() || (cd.isPresent() && cd.get().hasTicksLeft())) {
                        return;
                    }
                    data.inDebt = true;
                    if (!data.playerInRadius) {
                        wp.sendMessage(Component.text("You walked outside your ", NamedTextColor.GRAY)
                                                .append(Component.text("Death's Debt ", NamedTextColor.LIGHT_PURPLE))
                                                .append(Component.text("radius.", NamedTextColor.GRAY)));
                    } else {
                        wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED.append(Component.text(" Spirit's Respite ", NamedTextColor.DARK_GREEN))
                                                                       .append(Component.text("delayed "))
                                                                       .append(Component.text(Math.round(data.delayedDamage), NamedTextColor.RED))
                                                                       .append(Component.text(" damage. Your debt must now be paid.")));
                    }
                    stats.totalDelayed += data.delayedDamage;
                    //beginning debt
                    wp.getCooldownManager().addCooldown(new RegularCooldown<>(name, "DEBT", DeathsDebtData.class, data, wp, CooldownTypes.ABILITY, cooldownManagerDebt -> {
                        if (wp.isDead()) {
                            return;
                        }
                        wp.getWorld().spigot().strikeLightningEffect(totemStand.getLocation(), false);
                        // Final enemy damage tick
                        AtomicInteger over5000DamageInstances = new AtomicInteger();
                        List<WarlordsEntity> enemies = PlayerFilter.entitiesAround(totemStand, debtRadius, debtRadius - 1, debtRadius).aliveEnemiesOf(wp).toList();
                        for (WarlordsEntity totemTarget : enemies) {
                            stats.targetsDamaged++;
                            totemTarget.addInstance(InstanceBuilder.damage().ability(this).source(wp).value(data.delayedDamage * damagePercent / 100f)).ifPresent(finalEvent -> {
                                if (finalEvent.getValue() > 5000) {
                                    over5000DamageInstances.getAndIncrement();
                                }
                                stats.totalDebtDamage += finalEvent.getValue();
                            });
                        }
                        if (pveMasterUpgrade2) {
                            List<Soulbinding> soulbindings = wp.getAbilitiesMatching(Soulbinding.class);
                            if (soulbindings.isEmpty()) {
                                Soulbinding soulbinding = new Soulbinding();
                                soulbinding.init(soulbinding.getBuilder());
                                soulbindings.add(soulbinding);
                            }
                            List<Soulbinding.SoulbindingData> soulbindingData = soulbindings.stream().map(soulbinding -> soulbinding.activeSoulbinding(wp)).toList();
                            float damageReduction = 1;
                            for (int i = 0; i < enemies.size() && i < 10; i++) {
                                WarlordsEntity enemy = enemies.get(i);
                                soulbindingData.forEach(soulbinding -> soulbinding.bindPlayer(wp, enemy));
                                damageReduction -= .025f;
                            }
                            float finalDamageReduction = damageReduction;
                            RegularCooldown<DeathsDebt> deathParadeCooldown = new RegularCooldown<>(
                                    "Death Parade",
                                    "PARADE",
                                    DeathsDebt.class,
                                    null,
                                    wp,
                                    CooldownTypes.BUFF,
                                    cooldownManager -> {
                                    },
                                    5 * 20
                            );
                            deathParadeCooldown.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                                currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, finalDamageReduction);
                                    }
                            );
                            deathParadeCooldown.addModifier(Modifier.ENERGY_GAIN_PER_HIT, energyGainPerTick -> energyGainPerTick.addModifier(FloatModifiable.ModifierType.ADDITIVE,
                                            "Death Parade", 30
                                    )
                            );
                            wp.getCooldownManager().addCooldown(deathParadeCooldown);
                        }
                        if (over5000DamageInstances.get() >= 5) {
                            ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.RETRIBUTION_OF_THE_DEAD);
                        }
                    }, cooldownManager -> {
                        totemStand.remove();
                    }, debtTickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 5 == 0) {
                            circleEffect.playEffects();
                        }
                        //6 self damage ticks
                        if (ticksElapsed % 20 == 0) {
                            data.onDebtTick();
                        }
                    })
                    ));
                    circleEffect.replaceEffects(e -> e instanceof DoubleLineEffect, new DoubleLineEffect(Particle.WITCH));
                    circleEffect.setRadius(debtRadius);
                    //blue to purple totem
                    totemStand.getEquipment().setHelmet(PURPLE_TOTEM);
                },
                cooldownManager -> {
                    Optional<RegularCooldown> cd = new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterCooldownObject(data).findAny();
                    if (wp.isDead() || wp.getWorld() != totemStand.getWorld() || (cd.isPresent() && cd.get().hasTicksLeft())) {
                        totemStand.remove();
                    }
                },
                duration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (wp.getWorld() != totemStand.getWorld()) {
                        cooldown.setTicksLeft(0);
                        return;
                    }
                    if (ticksElapsed % 5 == 0) {
                        circleEffect.playEffects();
                    }
                    boolean isPlayerInRadius = wp.getLocation().distanceSquared(totemStand.getLocation()) < respiteRadius * respiteRadius;
                    if (!isPlayerInRadius && !data.inDebt) {
                        data.inDebt = true;
                        data.playerInRadius = false;
                        cooldown.setTicksLeft(0);
                        return;
                    }
                    if (ticksElapsed % 20 == 0) {
                        Utils.playGlobalSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, 1.5F);
                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Spirit's Respite", NamedTextColor.DARK_GREEN)
                                                                                       .append(Component.text(" delayed ", NamedTextColor.GRAY))
                                                                                       .append(Component.text(Math.round(data.delayedDamage), NamedTextColor.RED))
                                                                                       .append(Component.text(" damage. ", NamedTextColor.GRAY))
                                                                                       .append(Component.text(Math.round(ticksLeft / 20f), NamedTextColor.GOLD))
                                                                                       .append(Component.text(" seconds left.", NamedTextColor.GRAY))));
                        if (wp.isInPve()) {
                            for (WarlordsEntity we : PlayerFilter.entitiesAround(totemStand.getLocation(), respiteRadius, respiteRadius, respiteRadius)
                                                                 .aliveEnemiesOf(wp)
                                                                 .closestFirst(wp)) {
                                if (we instanceof WarlordsNPC) {
                                    ((WarlordsNPC) we).getMob().setTarget(wp);
                                }
                            }
                        }
                    }
                })
        );
        spiritsRespiteCooldown.addModifier(Modifier.ON_INCOMING_DAMAGE, (event, currentDamageValue, isCrit) -> {
                    data.delayedDamage += currentDamageValue;
                }
        );
        wp.getCooldownManager().addCooldown(spiritsRespiteCooldown);
        if (pveMasterUpgrade) {
            wp.addKnockbackModifier(wp, "Spirits Respite", -80, spiritsRespiteCooldown);
        }
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.respiteRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("respiteRadius"), int.class);
        this.debtRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("debtRadius"), int.class);
        this.damagePercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damagePercent"), float.class);
        this.delayedDamageTaken = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("delayedDamageTaken"), float.class);
        this.debtTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("debtTickDuration"), int.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Spirits’ Respite", NamedTextColor.DARK_AQUA)
                                               .text(": Place down a totem that delays ")
                                               .percent(100, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" of incoming damage towards yourself " + (inPve ? " and takes aggro of nearby mobs" + "." : ".") + " Transforms into ")
                                               .text("Death’s Debt ", NamedTextColor.LIGHT_PURPLE)
                                               .text("after ")
                                               .append(formatRange(tickDuration / 20f, (tickDuration / 20f + 2), NamedTextColor.GOLD))
                                               .text(" seconds (increases with higher health), or when you exit its ")
                                               .blocks(respiteRadius)
                                               .text(" radius.")
                                               .emptyLine()
                                               .text("Death’s Debt", NamedTextColor.LIGHT_PURPLE)
                                               .text(": Take ")
                                               .percent(delayedDamageTaken, NamedTextColor.RED)
                                               .text(" of the damage delayed by ")
                                               .text("Spirits’ Respite ", NamedTextColor.DARK_AQUA)
                                               .text("over ")
                                               .durationTicks(debtTickDuration)
                                               .text(". The totem will heal nearby allies for ")
                                               .percent(damagePercent, NamedTextColor.GREEN)
                                               .text(" of all damage that you take. If you survive, deal ")
                                               .percent(damagePercent, NamedTextColor.RED)
                                               .text(" of the damage delayed to nearby enemies in a ")
                                               .blocks(debtRadius)
                                               .text(" radius.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new DeathsDebtBranch(abilityTree, this);
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
    public DeathsDebtStats getAbilityStats() {
        return stats;
    }

    public float getDelayedDamageTaken() {
        return delayedDamageTaken;
    }

    public void setDelayedDamageTaken(float delayedDamageTaken) {
        this.delayedDamageTaken = delayedDamageTaken;
    }

    public int getDebtTickDuration() {
        return debtTickDuration;
    }

    public int getRespiteRadius() {
        return respiteRadius;
    }

    public void setRespiteRadius(int respiteRadius) {
        this.respiteRadius = respiteRadius;
    }

    public int getDebtRadius() {
        return debtRadius;
    }

    public void setDebtRadius(int debtRadius) {
        this.debtRadius = debtRadius;
    }

    public float getDamagePercent() {
        return damagePercent;
    }

    public void setDamagePercent(float damagePercent) {
        this.damagePercent = damagePercent;
    }

    public static class DeathsDebtData extends TotemData<DeathsDebt> {

        private boolean inDebt = false;

        private boolean playerInRadius = true;

        private float delayedDamage = 0;

        public DeathsDebtData(DeathsDebt totem, WarlordsEntity owner, ArmorStand armorStand) {
            super(totem, owner, armorStand);
        }

        public void onDebtTick() {
            Utils.playGlobalSound(armorStand.getLocation(), "shaman.lightningbolt.impact", 2, 1.5F);
            // 100% of damage over 6 seconds
            float selfDamageInPercentPerSecond = convertToPercent(totem.getDelayedDamageTaken() / (totem.getDebtTickDuration() / 20f));
            float damage = delayedDamage * selfDamageInPercentPerSecond;
            float debtTrueDamage = (float) (damage * Math.pow(.8, (int) new CooldownFilter<>(owner, RegularCooldown.class).filterCooldownClass(SpiritLink.class).stream().count()));
            // Player damage
            owner.addInstance(InstanceBuilder.melee().source(owner).value(debtTrueDamage));
            // Teammate heal
            for (WarlordsEntity allyTarget : PlayerFilter.entitiesAround(armorStand, totem.debtRadius, totem.debtRadius - 1, totem.debtRadius).aliveTeammatesOf(owner)) {
                totem.stats.targetsHealed++;
                allyTarget.addInstance(InstanceBuilder.healing().ability(totem).source(owner).value(damage * convertToPercent(totem.damagePercent)));
            }
            // Adding damage to Repentance Pool
            // @see Repentance.class
            for (Repentance repentance : owner.getAbilitiesMatching(Repentance.class)) {
                repentance.addToPool(debtTrueDamage);
            }
        }

    }

    public static class DeathsDebtStats extends AbstractAbilityStats<DeathsDebt, DeathsDebtStats> {

        @Field("targets_damaged")
        private int targetsDamaged = 0;

        @Field("targets_healed")
        private int targetsHealed = 0;

        @Field("total_delayed")
        private float totalDelayed = 0;

        @Field("total_debt_damage")
        private float totalDebtDamage = 0;

        @Override
        public Class<DeathsDebtStats> getClazz() {
            return DeathsDebtStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Damaged", targetsDamaged));
            statsDisplay.add(new AbilityStatDisplay("Targets Healed", targetsHealed));
            statsDisplay.add(new AbilityStatDisplay("Total Delayed", totalDelayed));
            statsDisplay.add(new AbilityStatDisplay("Total Debt Damage", totalDebtDamage));
            return statsDisplay;
        }

        @Override
        public DeathsDebtStats merge(DeathsDebtStats other, int multiplier) {
            DeathsDebtStats stats = super.merge(other, multiplier);
            stats.targetsDamaged = this.targetsDamaged + other.targetsDamaged * multiplier;
            stats.targetsHealed = this.targetsHealed + other.targetsHealed * multiplier;
            stats.totalDelayed = this.totalDelayed + other.totalDelayed * multiplier;
            stats.totalDebtDamage = this.totalDebtDamage + other.totalDebtDamage * multiplier;
            return stats;
        }

        @Override
        public DeathsDebtStats create() {
            return new DeathsDebtStats();
        }

    }

}
