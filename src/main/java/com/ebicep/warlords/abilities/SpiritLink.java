package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.SpiritLinkBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public class SpiritLink extends AbstractChain<SpiritLink, SpiritLink.SpiritLinkStats> implements RedAbilityIcon, Damages<SpiritLink.DamageValues> {

    public static final ItemStack CHAIN_ITEM = new ItemStack(Material.BRAIN_CORAL);
    private final SpiritLinkStats stats = new SpiritLinkStats();
    private final DamageValues damageValues = new DamageValues();
    private float speedBuff = 40;
    private float speedDuration = 1.5f;
    private float damageReduction = 15;
    private float damageReductionDuration = 4.5f;
    private float damageDecreasePerBounce = 20;
    private int maxStacks = 10;

    public SpiritLink() {
        super(AbstractAbilityBuilder.create("spiritLink").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.speedBuff = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedBuff"), float.class);
        this.speedDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedDuration"), float.class);
        this.damageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReduction"), float.class);
        this.damageReductionDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReductionDuration"), float.class);
        this.damageDecreasePerBounce = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageDecreasePerBounce"), float.class);
        this.maxStacks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxStacks"), int.class);
    }

    @Override
    protected Set<WarlordsEntity> getEntitiesHitAndActivate(WarlordsEntity wp) {
        Set<WarlordsEntity> hitCounter = new HashSet<>();
        for (WarlordsEntity nearPlayer : PlayerFilter.entitiesAround(wp, radius, radius - 2, radius).aliveEnemiesOf(wp).lookingAtFirst(wp).soulBindedFirst(wp)) {
            if (LocationUtils.isLookingAtChain(wp, nearPlayer) && LocationUtils.hasLineOfSight(wp, nearPlayer)) {
                stats.addPlayersHit();
                if (nearPlayer.onHorse()) {
                    stats.numberOfDismounts++;
                }
                chain(wp.getLocation(), nearPlayer.getLocation());
                nearPlayer.addInstance(InstanceBuilder.damage().ability(this).source(wp).value(damageValues.linkDamage));
                hitCounter.add(nearPlayer);
                List<Soulbinding.SoulbindingData> soulbindings = wp.getCooldownManager().getNumberOfBoundPlayersLink(nearPlayer);
                for (Soulbinding.SoulbindingData data : soulbindings) {
                    healNearPlayers(wp, nearPlayer, data);
                }
                additionalBounce(wp, hitCounter, nearPlayer, new ArrayList<>(Arrays.asList(wp, nearPlayer)), pveMasterUpgrade2 && !soulbindings.isEmpty() ? -1 : 0);
                if (pveMasterUpgrade2 && nearPlayer instanceof WarlordsNPC warlordsNPC) {
                    warlordsNPC.getMob().setTarget(wp);
                    EffectUtils.displayParticle(Particle.INSTANT_EFFECT, warlordsNPC.getLocation().add(0, 1.2, 0), 5, .25, .25, .25, 0);
                }
                break;
            }
        }
        return hitCounter;
    }

    @Override
    protected void onHit(WarlordsEntity we, int hitCounter) {
        we.playSound(we.getLocation(), "mage.firebreath.activation", 1, 1);
        // speed buff
        we.addSpeedModifier(we, "Spirit Link", speedBuff, (int) (speedDuration * 20));
        SpiritLinkData spiritLinkData = new SpiritLinkData();
        we.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "LINK",
                SpiritLinkData.class,
                spiritLinkData,
                we,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                (int) (damageReductionDuration * 20)
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
            int stacks = (int) new CooldownFilter<>(we, RegularCooldown.class)
                    .filterCooldownClass(SpiritLinkData.class)
                    .stream()
                    .count();
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, (float) Math.pow(1 - damageReduction / 100f, stacks));
        }));
    }

    @Override
    protected ItemStack getChainItem() {
        return CHAIN_ITEM;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Links your spirit with up to ")
                                               .text(additionalBounces + 1, NamedTextColor.BLUE)
                                               .text(" enemy players, dealing ")
                                               .damage(damageValues.linkDamage)
                                               .text(" damage to the first target hit. Each additional hit deals ")
                                               .percent(damageDecreasePerBounce, NamedTextColor.RED)
                                               .text(" reduced damage. You gain ")
                                               .percent(speedBuff, NamedTextColor.WHITE)
                                               .text(" speed for ")
                                               .durationSeconds(speedDuration)
                                               .text(", and take ")
                                               .percent(damageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" reduced damage for ")
                                               .durationSeconds(damageReductionDuration)
                                               .text(".")
                                               .initialRange(radius)
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SpiritLinkBranch(abilityTree, this);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public SpiritLinkStats getAbilityStats() {
        return stats;
    }

    public float getDamageReduction() {
        return damageReduction;
    }

    public void setDamageReduction(float damageReduction) {
        this.damageReduction = damageReduction;
    }

    public static class SpiritLinkData {
    }

    private void additionalBounce(WarlordsEntity wp, Set<WarlordsEntity> hitCounter, WarlordsEntity chainTarget, List<WarlordsEntity> toExclude, int bounceCount) {
        float bounceDamageReduction = Math.max(0, 1 - (bounceCount + 1) * .2f);
        if (bounceCount >= additionalBounces || bounceDamageReduction == 0) {
            return;
        }
        for (WarlordsEntity bounceTarget : PlayerFilter.entitiesAround(chainTarget, bounceRange, bounceRange, bounceRange)
                                                       .aliveEnemiesOf(wp)
                                                       .excluding(toExclude)
                                                       .soulBindedFirst(wp)) {
            stats.addPlayersHit();
            if (bounceTarget.onHorse()) {
                stats.numberOfDismounts++;
            }
            chain(chainTarget.getLocation(), bounceTarget.getLocation());
            bounceTarget.addInstance(InstanceBuilder.damage()
                                                    .ability(this)
                                                    .source(wp)
                                                    .min(damageValues.linkDamage.getMinValue() * bounceDamageReduction)
                                                    .max(damageValues.linkDamage.getMaxValue() * bounceDamageReduction)
                                                    .crit(damageValues.linkDamage));
            hitCounter.add(bounceTarget);
            List<Soulbinding.SoulbindingData> soulbindings = wp.getCooldownManager().getNumberOfBoundPlayersLink(bounceTarget);
            for (Soulbinding.SoulbindingData data : soulbindings) {
                healNearPlayers(wp, bounceTarget, data);
            }
            toExclude.add(bounceTarget);
            additionalBounce(wp, hitCounter, bounceTarget, toExclude, bounceCount + (pveMasterUpgrade2 && !soulbindings.isEmpty() ? 0 : 1));
            if (pveMasterUpgrade2 && bounceTarget instanceof WarlordsNPC warlordsNPC) {
                warlordsNPC.getMob().setTarget(wp);
                EffectUtils.displayParticle(Particle.INSTANT_EFFECT, warlordsNPC.getLocation().add(0, 1.2, 0), 5, .25, .25, .25, 0);
            }
            break;
        }
    }

    private void healNearPlayers(WarlordsEntity warlordsPlayer, WarlordsEntity hitPlayer, Soulbinding.SoulbindingData data) {
        Soulbinding soulbinding = data.getSoulbinding();
        float radius = soulbinding.getRadius();
        int limit = soulbinding.getMaxAlliesHit();
        Soulbinding.HealingValues healValues = soulbinding.getHealValues();
        warlordsPlayer.addInstance(InstanceBuilder.healing().ability(soulbinding).source(warlordsPlayer).value(healValues.getSelfHealing()));
        for (WarlordsEntity nearPlayer : PlayerFilter.entitiesAround(warlordsPlayer, radius, radius, radius)
                                                     .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                                                     .closestWarlordPlayersFirst(warlordsPlayer.getLocation())
                                                     .limit(limit)) {
            soulbinding.addLinkTeammatesHealed();
            nearPlayer.addInstance(InstanceBuilder.healing().ability(soulbinding).source(warlordsPlayer).value(healValues.getAllyHealing()));
        }
        new CooldownFilter<>(warlordsPlayer, PersistentCooldown.class).filterCooldownClassAndMapToObjectsOfClass(Soulbinding.SoulbindingData.class)
                                                                      .filter(binding -> binding.hasBoundPlayerSoul(hitPlayer))
                                                                      .forEach(binding -> {
                                                                          if (binding.getSoulbinding().isPveMasterUpgrade()) {
                                                                              warlordsPlayer.addEnergy(warlordsPlayer, "Soulbinding Weapon", 1);
                                                                          }
                                                                      });
    }

    public float getSpeedDuration() {
        return speedDuration;
    }

    public void setSpeedDuration(float speedDuration) {
        this.speedDuration = speedDuration;
    }

    public float getDamageReductionDuration() {
        return damageReductionDuration;
    }

    public void setDamageReductionDuration(float damageReductionDuration) {
        this.damageReductionDuration = damageReductionDuration;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable linkDamage = new Value.RangedValueCritable(276, 372, 20, 175);

        private List<Value> values = List.of(linkDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.linkDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("linkDamage"), Value.RangedValueCritable.class);
            this.values = List.of(linkDamage);
        }

        public Value.RangedValueCritable getLinkDamage() {
            return linkDamage;
        }

    }

    public static class SpiritLinkStats extends AbstractChainStats<SpiritLink, SpiritLinkStats> {

        @Field("number_of_dismounts")
        private int numberOfDismounts = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Dismounts", numberOfDismounts));
            return statsDisplay;
        }

        @Override
        public SpiritLinkStats merge(SpiritLinkStats other, int multiplier) {
            SpiritLinkStats stats = super.merge(other, multiplier);
            stats.numberOfDismounts = this.numberOfDismounts + other.numberOfDismounts * multiplier;
            return stats;
        }

        @Override
        public Class<SpiritLinkStats> getClazz() {
            return SpiritLinkStats.class;
        }

        @Override
        public SpiritLinkStats create() {
            return new SpiritLinkStats();
        }

    }

}
