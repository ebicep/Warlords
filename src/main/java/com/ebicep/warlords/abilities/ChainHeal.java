package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractChain;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.ChainHealBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class ChainHeal extends AbstractChain implements BlueAbilityIcon, Heals<ChainHeal.HealingValues> {

    private final HealingValues healingValues = new HealingValues();

    public ChainHeal() {
        super("Chain Heal", 533, 719, 7.99f, 40, 20, 175, 15, 10, 1);
    }

    @Override
    public void updateDescription(Player player) {
        description =
                Component.text("Discharge a beam of energizing lightning that heals you and a targeted friendly player for ")
                         .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                         .append(Component.text(" health and jumps to "))
                         .append(Component.text("1", NamedTextColor.YELLOW))
                         .append(Component.text(" additional target within "))
                         .append(Component.text(bounceRange, NamedTextColor.YELLOW))
                         .append(Component.text(" blocks.\n\nEach ally healed reduces the cooldown of Boulder by "))
                         .append(Component.text("2.5", NamedTextColor.GOLD))
                         .append(Component.text(" seconds.\n\nHas an initial cast range of "))
                         .append(Component.text(radius, NamedTextColor.YELLOW))
                         .append(Component.text(" blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHit));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ChainHealBranch(abilityTree, this);
    }

    @Override
    protected Set<WarlordsEntity> getEntitiesHitAndActivate(WarlordsEntity wp) {
        Set<WarlordsEntity> hitCounter = new HashSet<>();
        for (WarlordsEntity chainTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
        ) {
            if (!LocationUtils.isLookingAtChain(wp, chainTarget)) {
                continue;
            }
            wp.addInstance(InstanceBuilder
                    .healing()
                    .ability(this)
                    .source(wp)
                    .value(healingValues.chainHealing)
            );
            chainTarget.addInstance(InstanceBuilder
                    .healing()
                    .ability(this)
                    .source(wp)
                    .value(healingValues.chainHealing)
            );

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
                        new ChainHeal(),
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
                            warlordsEntity.addInstance(InstanceBuilder
                                    .healing()
                                    .ability(this)
                                    .source(wp)
                                    .value(healing)
                            );
                            EffectUtils.playParticleLinkAnimation(warlordsEntity.getLocation(), wp.getLocation(), Particle.VILLAGER_HAPPY, 1, 1.25, -1);
                            EffectUtils.displayParticle(
                                    Particle.VILLAGER_HAPPY,
                                    warlordsEntity.getLocation().add(0, 1.2, 0),
                                    4,
                                    0.5,
                                    0.3,
                                    0.5,
                                    0.01
                            );

                        })
                ) {
                    @Override
                    public float addEnergyGainPerTick(float energyGainPerTick) {
                        return energyGainPerTick + 0.5f;
                    }
                });
            }
        }

        return hitCounter;
    }

    @Override
    protected void onHit(WarlordsEntity wp, int hitCounter) {
        Utils.playGlobalSound(wp.getLocation(), "shaman.chainheal.activation", 2, 1);

        for (Boulder boulder : wp.getAbilitiesMatching(Boulder.class)) {
            float currentCD = boulder.getCurrentCooldown();
            if ((hitCounter + 1) * 2.5f > currentCD) {
                boulder.setCurrentCooldown(0);
            } else {
                boulder.subtractCurrentCooldown((hitCounter + 1) * 2.5f);
            }
        }
    }

    @Override
    protected ItemStack getChainItem() {
        return new ItemStack(Material.BLUE_ORCHID);
    }

    private void additionalBounce(WarlordsEntity wp, Set<WarlordsEntity> hitCounter, WarlordsEntity chainTarget, List<WarlordsEntity> toExclude, int bounceCount) {
        if (bounceCount >= additionalBounces) {
            return;
        }
        for (WarlordsEntity bounceTarget : PlayerFilter
                .entitiesAround(chainTarget, bounceRange, bounceRange, bounceRange)
                .aliveTeammatesOf(wp)
                .excluding(toExclude)
                .warlordPlayersFirst()
        ) {
            chain(chainTarget.getLocation(), bounceTarget.getLocation());
            bounceTarget.addInstance(InstanceBuilder
                    .healing()
                    .ability(this)
                    .source(wp)
                    .value(healingValues.chainHealing)
            );

            if (pveMasterUpgrade) {
                critStatsOnHit(bounceTarget);
            }

            hitCounter.add(bounceTarget);

            toExclude.add(bounceTarget);
            additionalBounce(wp, hitCounter, bounceTarget, toExclude, bounceCount + 1);

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
                        EffectUtils.displayParticle(
                                Particle.VILLAGER_HAPPY,
                                we.getLocation().add(0, 1.2, 0),
                                1,
                                0.5,
                                0.3,
                                0.5,
                                0.01
                        );
                    }
                })
        ) {
            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                if (event.getCause().isEmpty() || event.getCause().equals("Time Warp")) {
                    return currentCritChance;
                }

                return currentCritChance + 20;
            }

            @Override
            public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                if (event.getCause().isEmpty() || event.getCause().equals("Time Warp")) {
                    return currentCritMultiplier;
                }
                return currentCritMultiplier + 40;
            }
        });
    }

    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable chainHealing = new Value.RangedValueCritable(533, 719, 20, 175);
        private final List<Value> values = List.of(chainHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }


}