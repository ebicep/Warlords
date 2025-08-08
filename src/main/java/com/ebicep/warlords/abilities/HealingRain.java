package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectPlayer;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.circle.AreaEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.aquamancer.HealingRainBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HealingRain extends AbstractAbility implements OrangeAbilityIcon, Duration, HitBox, Damages<HealingRain.DamageValues>, Heals<HealingRain.HealingValues>, AbilityStats<HealingRain, HealingRain.HealingRainStats> {

    private final HealingRainStats stats = new HealingRainStats();
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private int tickDuration = 200;
    private int castRadius;

    private FloatModifiable radius = new FloatModifiable(8);

    public HealingRain() {
        super(AbstractAbilityBuilder.create("healingRain").pvp());
    }

    public HealingRain(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.radius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.castRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("castRadius"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        float radius = this.radius.getCalculatedValue();
        List<WarlordsEntity> targets = PlayerFilter
                .entitiesAround(wp, castRadius, castRadius, castRadius)
                .aliveTeammatesOfExcludingSelf(wp)
                .requireLineOfSightIntervene(wp, false)
                .lookingAtFirst(wp)
                .limit(1)
                .toList();
        WarlordsAbilityTargetEvent placeEvent = new WarlordsAbilityTargetEvent(wp, name, targets.isEmpty() ? wp : targets.get(0));
        Bukkit.getPluginManager().callEvent(placeEvent);

        targets = new ArrayList<>(placeEvent.getTargets());
        WarlordsEntity target = targets.isEmpty() ? wp : targets.get(0);

        Location location = target.getLocation();
        Utils.playGlobalSound(location, "mage.healingrain.impact", 2, 1);
        List<EffectPlayer<? super CircleEffect>> effects = new ArrayList<>();
        effects.add(new CircumferenceEffect(Particle.HAPPY_VILLAGER, Particle.DUST));
        if (!pveMasterUpgrade2) {
            effects.add(new AreaEffect(5, Particle.CLOUD).particlesPerSurface(0.025));
            effects.add(new AreaEffect(5, Particle.DRIPPING_WATER).particlesPerSurface(0.025));
        }
        CircleEffect circleEffect = new CircleEffect(wp.getGame(), wp.getTeam(), location, radius, effects.toArray(new EffectPlayer[0]));
        AtomicReference<List<Pair<WarlordsEntity, CircleEffect>>> personalCloud = new AtomicReference<>(new ArrayList<>()); // pveMasterUpgrade2
        HealingRainData data = new HealingRainData(target);
        if (wp == data.target) {
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" You casted your ", NamedTextColor.GRAY)
                                     .append(Component.text("Healing Rain", NamedTextColor.GREEN))
                                     .append(Component.text(" upon yourself."))
                    ));
        } else {
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" You casted your ", NamedTextColor.GRAY)
                                     .append(Component.text("Healing Rain", NamedTextColor.GREEN))
                                     .append(Component.text(" upon " + data.target.getName() + "."))
                    ));
            data.target.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" " + wp.getName() + " casted ", NamedTextColor.GRAY)
                                     .append(Component.text("Healing Rain", NamedTextColor.GREEN))
                                     .append(Component.text(" upon you."))
                    ));
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), location, Particle.BUBBLE_POP);
        }
        RegularCooldown<HealingRainData> healingRainCooldown = new RegularCooldown<>(
                name,
                "RAIN",
                HealingRainData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (pveMasterUpgrade) {
                        for (WarlordsEntity enemyInRain : PlayerFilter.entitiesAround(data.target.getLocation(), radius, radius, radius).aliveEnemiesOf(wp).limit(8)) {
                            Utils.playGlobalSound(enemyInRain.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 1.8f);
                            EffectUtils.playFirework(enemyInRain.getLocation(), FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.BURST).build());
                            strikeInRain(wp, enemyInRain);
                        }
                    }
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    List<Pair<WarlordsEntity, CircleEffect>> personalCloudList = personalCloud.get();
                    if (pveMasterUpgrade2) {
                        personalCloudList.forEach(warlordsEntityCircleEffectPair -> {
                            WarlordsEntity cloudTeammate = warlordsEntityCircleEffectPair.getA();
                            CircleEffect effect = warlordsEntityCircleEffectPair.getB();
                            Location cloudTeammateLocation = cloudTeammate.getLocation();
                            Location center = effect.getCenter();
                            center.set(cloudTeammateLocation.getX(), cloudTeammateLocation.getY(), cloudTeammateLocation.getZ());
                            effect.playEffects();
                        });
                    }
                    if ((inPve && ticksElapsed % 8 == 0) || (!inPve && ticksElapsed % 4 == 0)) {
                        if (data.target.isAlive()) {
                            Location center = circleEffect.getCenter();
                            Location newLocation = data.target.getLocation();
                            center.set(newLocation.getX(), newLocation.getY() + .01, newLocation.getZ());
                        }
                        circleEffect.playEffects();
                    }
                    if (ticksElapsed % 10 == 0) {
                        List<WarlordsEntity> teammatesInRain = PlayerFilter.entitiesAround(data.target.getLocation(), radius, radius, radius).aliveTeammatesOf(wp).toList();
                        if (pveMasterUpgrade2) {
                            // cloud only give to those in cloud or has been in cloud and is within 40 blocks of player
                            personalCloudList.removeIf(teammate -> teammate.getA().getLocation().distanceSquared(wp.getLocation()) > 40 * 40);
                            for (WarlordsEntity teammateInRain : teammatesInRain) {
                                if (personalCloudList.stream().noneMatch(pair -> pair.getA() == teammateInRain)) {
                                    personalCloudList.add(new Pair<>(teammateInRain,
                                            new CircleEffect(wp.getGame(),
                                                    wp.getTeam(),
                                                    teammateInRain.getLocation().clone(),
                                                    2,
                                                    new AreaEffect(4, Particle.CLOUD).particlesPerSurface(0.1),
                                                    new AreaEffect(4, Particle.DRIPPING_WATER).particlesPerSurface(0.1)
                                            )
                                    ));
                                }
                            }
                            for (Pair<WarlordsEntity, CircleEffect> cloudTeammatePair : personalCloudList) {
                                WarlordsEntity cloudTeammate = cloudTeammatePair.getA();
                                heal(wp, cloudTeammate, "Rain Cloud");
                                CooldownManager cloudTeammateCooldownManager = cloudTeammate.getCooldownManager();
                                cloudTeammateCooldownManager.removeCooldownByName("Nimbus");
                                cloudTeammateCooldownManager.addCooldown(new RegularCooldown<>("Nimbus",
                                        null,
                                        HealingRain.class,
                                        null,
                                        wp,
                                        CooldownTypes.ABILITY,
                                        cooldownManager -> {
                                        },
                                        10
                                ) {

                                    @Override
                                    public float addEnergyGainPerTick(float energyGainPerTick) {
                                        return energyGainPerTick + .25f;
                                    }
                                });
                            }
                        } else {
                            for (WarlordsEntity teammateInRain : teammatesInRain) {
                                heal(wp, teammateInRain, name);
                            }
                        }
                    }
                    if (ticksElapsed % 40 == 0) {
                        if (pveMasterUpgrade) {
                            for (WarlordsEntity enemyInRain : PlayerFilter.entitiesAround(data.target.getLocation(), radius, radius, radius).aliveEnemiesOf(wp).limit(8)) {
                                Utils.playGlobalSound(enemyInRain.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 1.8f);
                                FireWorkEffectPlayer.playFirework(enemyInRain.getLocation(),
                                        FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.BURST).build()
                                );
                                strikeInRain(wp, enemyInRain);
                            }
                        }
                    }
                })
        );
        wp.getCooldownManager().addCooldown(healingRainCooldown);
        addSecondaryAbility(
                1,
                () -> {
                    if (wp.isAlive()) {
                        Location wpLocation = wp.getLocation();
                        List<WarlordsEntity> newTargets = PlayerFilter
                                .entitiesAround(wp, castRadius, castRadius, castRadius)
                                .aliveTeammatesOfExcludingSelf(wp)
                                .requireLineOfSightIntervene(wp, false)
                                .lookingAtFirst(wp)
                                .limit(1)
                                .toList();
                        WarlordsAbilityTargetEvent newPlaceEvent = new WarlordsAbilityTargetEvent(wp, name, newTargets.isEmpty() ? wp : newTargets.get(0));
                        Bukkit.getPluginManager().callEvent(newPlaceEvent);

                        newTargets = new ArrayList<>(newPlaceEvent.getTargets());
                        data.target = newTargets.isEmpty() ? wp : newTargets.get(0);
                        location.set(wpLocation.getX(), wpLocation.getY(), wpLocation.getZ());
                        wp.playSound(wpLocation, "mage.timewarp.teleport", 2, 1.35f);
                        if (wp == data.target) {
                            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                    .append(Component.text(" You casted your ", NamedTextColor.GRAY)
                                                     .append(Component.text("Healing Rain", NamedTextColor.GREEN))
                                                     .append(Component.text(" upon yourself."))
                                    ));
                        } else {
                            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                    .append(Component.text(" You casted your ", NamedTextColor.GRAY)
                                                     .append(Component.text("Healing Rain", NamedTextColor.GREEN))
                                                     .append(Component.text(" upon " + data.target.getName() + "."))
                                    ));
                            data.target.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                    .append(Component.text(" " + wp.getName() + " casted ", NamedTextColor.GRAY)
                                                     .append(Component.text("Healing Rain", NamedTextColor.GREEN))
                                                     .append(Component.text(" upon you."))
                                    ));
                            EffectUtils.playParticleLinkAnimation(wp.getLocation(), data.target.getLocation(), Particle.BUBBLE_POP);
                        }
                    }
                },
                true,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(healingRainCooldown)
        );
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Cast a Healing Rain upon an ally or yourself that will restore ")
                                               .heal(healingValues.rainHealing)
                                               .text(" health every ")
                                               .durationSeconds(0.5f)
                                               .text(" to allies. Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .emptyLine()
                                               .text("Recast to retarget the Healing Rain.")
                                               .emptyLine()
                                               .text("Healing Rain can overheal allies for up to ")
                                               .percent(10, NamedTextColor.GREEN)
                                               .text(" of their max health as bonus health for ")
                                               .durationSeconds(Overheal.OVERHEAL_DURATION)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HealingRainBranch(abilityTree, this);
    }

    private void strikeInRain(WarlordsEntity giver, WarlordsEntity hit) {
        for (WarlordsEntity strikeTarget : PlayerFilter.entitiesAround(hit, 2, 3, 2).aliveEnemiesOf(giver)) {
            strikeTarget.getWorld().spigot().strikeLightningEffect(strikeTarget.getLocation(), true);
            float healthDamage = strikeTarget.getMaxHealth() * 0.01f;
            healthDamage = DamageCheck.clamp(healthDamage);
            strikeTarget.addInstance(InstanceBuilder.damage()
                                                    .ability(this)
                                                    .source(giver)
                                                    .min(damageValues.rainStrikeDamage.getMinValue() + healthDamage)
                                                    .max(damageValues.rainStrikeDamage.getMaxValue() + healthDamage));
        }
    }

    private void heal(@Nonnull WarlordsEntity wp, WarlordsEntity teammateInRain, String name) {
        stats.targetsHealed++;
        teammateInRain.addInstance(InstanceBuilder
                .healing()
                .ability(this)
                .source(wp)
                .value(healingValues.rainHealing)
                .flags(InstanceFlags.CAN_OVERHEAL_OTHERS, InstanceFlags.NO_HIT_SOUND)
        );
        if (teammateInRain != wp) {
            Overheal.giveOverHeal(wp, teammateInRain);
        }
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
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
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public HealingRainStats getAbilityStats() {
        return stats;
    }

    public static class HealingRainData {

        private WarlordsEntity target;

        public HealingRainData(WarlordsEntity target) {
            this.target = target;
        }

    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValue rainStrikeDamage = new Value.RangedValue(224, 377);

        private List<Value> values = List.of(rainStrikeDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.rainStrikeDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("rainStrikeDamage"), Value.RangedValue.class);
            this.values = List.of(rainStrikeDamage);
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable rainHealing = new Value.RangedValueCritable(100, 125, 25, 180);

        private List<Value> values = List.of(rainHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.rainHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("rainHealing"), Value.RangedValueCritable.class);
            this.values = List.of(rainHealing);
        }

        public Value.RangedValueCritable getRainHealing() {
            return rainHealing;
        }

    }

    public static class HealingRainStats extends AbstractAbilityStats<HealingRain, HealingRainStats> {

        @Field("targets_healed")
        private int targetsHealed = 0;

        @Override
        public Class<HealingRainStats> getClazz() {
            return HealingRainStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Healed", targetsHealed));
            return statsDisplay;
        }

        @Override
        public HealingRainStats merge(HealingRainStats other, int multiplier) {
            HealingRainStats stats = super.merge(other, multiplier);
            stats.targetsHealed = this.targetsHealed + other.targetsHealed * multiplier;
            return stats;
        }

        @Override
        public HealingRainStats create() {
            return new HealingRainStats();
        }

    }

}
