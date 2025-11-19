package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.flags.NoTargetAbilities;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.ChainHealBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChainHeal extends AbstractChain<ChainHeal, ChainHeal.ChainHealStats> implements BlueAbilityIcon, Heals<ChainHeal.HealingValues> {

    private final ChainHealStats stats = new ChainHealStats();
    private final HealingValues healingValues = new HealingValues();
    private float cooldownReductionInSeconds = 2.5f;

    public ChainHeal() {
        super(AbstractAbilityBuilder.create("chainHeal").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.cooldownReductionInSeconds = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("cooldownReductionInSeconds"), float.class);
    }

    @Override
    protected Set<WarlordsEntity> getEntitiesHitAndActivate(WarlordsEntity wp) {
        Set<WarlordsEntity> hitCounter = new HashSet<>();
        float rad = radius + PacketUtils.pingCompensationAmount(wp);
        for (WarlordsEntity chainTarget : PlayerFilter
                .entitiesAround(wp, rad, rad, rad)
                .aliveTeammatesOfExcludingSelf(wp)
                .warlordPlayersFirst()
                .lookingAtFirst(wp)
        ) {
            if (!LocationUtils.isLookingAtChain(wp, chainTarget)) {
                continue;
            }
            wp.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.chainHealing));
            chainTarget.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.chainHealing));
            if (pveMasterUpgrade) {
                critStatsOnHit(wp);
                critStatsOnHit(chainTarget);
            }
            chain(wp.getLocation(), chainTarget.getLocation());
            hitCounter.add(chainTarget);
            additionalBounce(wp, hitCounter, chainTarget, new ArrayList<>(Arrays.asList(wp, chainTarget)), 0);
            break;
        }
        if (pveMasterUpgrade2) {
            for (WarlordsEntity warlordsEntity : hitCounter) {
                warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Chains of Blessings",
                        "CHAINS",
                        ChainHeal.class,
                        null,
                        wp,
                        CooldownTypes.BUFF,
                        cooldownManager -> {
                        },
                        5 * 20,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksLeft % 20 != 0) {
                                return;
                            }
                            float healing = 0.025f * wp.getMaxHealth();
                            warlordsEntity.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healing));
                            EffectUtils.playParticleLinkAnimation(warlordsEntity.getLocation(), wp.getLocation(), Particle.HAPPY_VILLAGER, 1, 1.25, -1);
                            EffectUtils.displayParticle(Particle.HAPPY_VILLAGER, warlordsEntity.getLocation().add(0, 1.2, 0), 4, 0.5, 0.3, 0.5, 0.01);
                        })
                ).addModifier(Modifier.ENERGY_GAIN_PER_TICK, energyGainPerTick -> energyGainPerTick.addAdditiveModifier("Chains of Blessings", 0.5f)));
            }
        }
        return hitCounter;
    }

    @Override
    protected void onHit(WarlordsEntity wp, int hitCounter) {
        Utils.playGlobalSound(wp.getLocation(), "shaman.chainheal.activation", 2, 1);
        for (Boulder boulder : wp.getAbilitiesMatching(Boulder.class)) {
            float currentCD = boulder.getCurrentCooldown();
            if ((hitCounter + 1) * cooldownReductionInSeconds > currentCD) {
                boulder.setCurrentCooldown(0);
            } else {
                boulder.subtractCurrentCooldown((hitCounter + 1) * cooldownReductionInSeconds);
            }
        }
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.BLUE_ORCHID);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Discharge a beam of energizing lightning that heals you and a targeted ally for ")
                                               .heal(healingValues.chainHealing)
                                               .text(" health and jumps to ")
                                               .text(additionalBounces, NamedTextColor.BLUE)
                                               .text(" additional target within ")
                                               .blocks(bounceRange)
                                               .text(".")
                                               .emptyLine()
                                               .text("Each ally healed reduces the cooldown of Boulder by ")
                                               .durationSeconds(cooldownReductionInSeconds)
                                               .text(".")
                                               .initialRange(radius)
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ChainHealBranch(abilityTree, this);
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public ChainHealStats getAbilityStats() {
        return stats;
    }

    private void additionalBounce(WarlordsEntity wp, Set<WarlordsEntity> hitCounter, WarlordsEntity chainTarget, List<WarlordsEntity> toExclude, int bounceCount) {
        if (bounceCount >= additionalBounces) {
            return;
        }
        for (WarlordsEntity bounceTarget : PlayerFilter.entitiesAround(chainTarget, bounceRange, bounceRange, bounceRange)
                                                       .aliveTeammatesOf(wp)
                                                       .excluding(toExclude)
                                                       .warlordPlayersFirst()) {
            chain(chainTarget.getLocation(), bounceTarget.getLocation());
            bounceTarget.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.chainHealing));
            if (pveMasterUpgrade) {
                critStatsOnHit(bounceTarget);
            }
            hitCounter.add(bounceTarget);
            toExclude.add(bounceTarget);
            additionalBounce(wp, hitCounter, bounceTarget, toExclude, bounceCount + 1);
            Bukkit.getPluginManager().callEvent(new WarlordsAbilityTargetEvent.WarlordsBlueAbilityTargetEvent(wp, name, bounceTarget));
            break;
        }
    }

    private void critStatsOnHit(WarlordsEntity we) {
        we.getCooldownManager().removeCooldown(ChainHeal.class, false);
        we.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "CHAIN CRIT",
                ChainHeal.class,
                new ChainHeal(),
                we,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                8 * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksLeft % 6 == 0) {
                        EffectUtils.displayParticle(Particle.HAPPY_VILLAGER, we.getLocation().add(0, 1.2, 0), 1, 0.5, 0.3, 0.5, 0.01);
                    }
                })
        ).addModifier(Modifier.DAMAGE_CRIT_CHANCE_ATTACKER, (event, currentCritChance) -> {
                    if (event.getCause().isEmpty() || event.getCause().equals("Time Warp")) {
                        return;
                    }
                    currentCritChance.addAdditiveModifier(name, 10);
                }
        ).addModifier(Modifier.DAMAGE_CRIT_MULTIPLIER_ATTACKER, (event, currentCritMultiplier) -> {
                    if (event.getCause().isEmpty() || event.getCause().equals("Time Warp")) {
                        return;
                    }
                    currentCritMultiplier.addAdditiveModifier(name, 20);
                }
        ));
    }

    public float getCooldownReductionInSeconds() {
        return cooldownReductionInSeconds;
    }

    public void setCooldownReductionInSeconds(float cooldownReductionInSeconds) {
        this.cooldownReductionInSeconds = cooldownReductionInSeconds;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable chainHealing = new Value.RangedValueCritable(533, 719, 20, 175);

        private List<Value> values = List.of(chainHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.chainHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("chainHealing"), Value.RangedValueCritable.class);
            this.values = List.of(chainHealing);
        }

        public Value.RangedValueCritable getChainHealing() {
            return chainHealing;
        }

    }

    public static class ChainHealStats extends AbstractChainStats<ChainHeal, ChainHealStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public ChainHealStats merge(ChainHealStats other, int multiplier) {
            ChainHealStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<ChainHealStats> getClazz() {
            return ChainHealStats.class;
        }

        @Override
        public ChainHealStats create() {
            return new ChainHealStats();
        }

    }

}
