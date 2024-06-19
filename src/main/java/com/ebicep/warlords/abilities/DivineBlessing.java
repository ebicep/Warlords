package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.DivineBlessingBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.*;

public class DivineBlessing extends AbstractAbility implements OrangeAbilityIcon, Duration, Heals<DivineBlessing.HealingValues> {

    private final HealingValues healingValues = new HealingValues();
    private int hexTickDurationIncrease = 40;
    private int hexHealingBonus = 30;
    private int lethalDamageHealing = 15;
    private int postHealthTickDelay = 40;
    private int postHealthHealAmount = 800;
    private int tickDuration = 240;

    public DivineBlessing() {
        super("Divine Blessing", 0, 0, 50, 10, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Imbue yourself with Holy Energy, increasing Merciful Hex duration by ")
                               .append(Component.text(format(hexTickDurationIncrease / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds and causing Ray of Light to not consume Merciful Hex stacks.\n\nAllies with max stacks of Merciful Hex receive "))
                               .append(Component.text(hexHealingBonus + "%", NamedTextColor.GREEN))
                               .append(Component.text(" more healing from all sources and heal for "))
                               .append(Component.text(lethalDamageHealing + "%", NamedTextColor.GREEN))
                               .append(Component.text(" of their maximum health when taking lethal damage for the first time. After "))
                               .append(Component.text(format(postHealthTickDelay / 20f), NamedTextColor.GOLD))
                               .append(Component.text("seconds all allies restore "))
                               .append(Component.text(postHealthHealAmount, NamedTextColor.GREEN))
                               .append(Component.text(" health. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "arcanist.divineblessing.activation", 2, 1.2f);
        Utils.playGlobalSound(wp.getLocation(), "paladin.holyradiance.activation", 2, 1.6f);
        EffectUtils.strikeLightning(wp.getLocation(), true);
        Game game = wp.getGame();
        new GameRunnable(game) {
            double interval = 3;

            @Override
            public void run() {
                interval -= 0.5;
                EffectUtils.playCylinderAnimation(
                        wp.getLocation(),
                        1.5 + interval,
                        70,
                        255,
                        70
                );

                if (interval <= 0) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 1);

        DivineBlessing tempDivineBlessing = new DivineBlessing();
        int maxStacks = MercifulHex.getFromHex(wp).getMaxStacks();
        Set<WarlordsEntity> healedLethal = new HashSet<>();
        List<FloatModifiable.FloatModifier> modifiers;
        if (pveMasterUpgrade2) {
            modifiers = wp.getAbilitiesMatching(RayOfLight.class)
                          .stream()
                          .map(ability -> ability.getCooldown().addMultiplicativeModifierMult(name + " Master", 0.55f))
                          .toList();
        } else {
            modifiers = Collections.emptyList();
        }
        wp.getCooldownManager().addCooldown(new RegularCooldown<DivineBlessing>(
                name,
                "BLESS",
                DivineBlessing.class,
                tempDivineBlessing,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    if (pveMasterUpgrade) {
                        healAllies(wp);
                    }
                    modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 10 == 0) {
                        EffectUtils.displayParticle(
                                Particle.CRIMSON_SPORE,
                                wp.getLocation(),
                                10,
                                0.1,
                                0.1,
                                0.1,
                                0.5
                        );
                    }

                    if (ticksElapsed % 20 == 0 && ticksLeft != 0) {
                        PlayerFilter.playingGame(game)
                                    .teammatesOfExcludingSelf(wp)
                                    .filter(teammate -> new CooldownFilter<>(teammate, RegularCooldown.class)
                                            .filterCooldownFrom(wp)
                                            .filterCooldownClass(MercifulHex.class)
                                            .stream()
                                            .count() >= maxStacks)
                                    .forEach(teammate -> {
                                        teammate.getCooldownManager().removeCooldownByObject(tempDivineBlessing);
                                        teammate.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                name,
                                                null,
                                                DivineBlessing.class,
                                                tempDivineBlessing,
                                                wp,
                                                CooldownTypes.ABILITY,
                                                cooldownManager -> {
                                                },
                                                21
                                        ) {
                                            @Override
                                            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                                                return currentHealValue * convertToMultiplicationDecimal(hexHealingBonus);
                                            }

                                            @Override
                                            public float modifyDamageAfterAllFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                                                if (teammate.getCurrentHealth() - currentDamageValue < 0 && !healedLethal.contains(teammate)) {
                                                    healedLethal.add(teammate);
                                                    float healAmount = teammate.getMaxHealth() * convertToPercent(lethalDamageHealing);
                                                    teammate.addInstance(InstanceBuilder
                                                            .healing()
                                                            .ability(DivineBlessing.this)
                                                            .source(wp)
                                                            .value(healAmount)
                                                    );
                                                    teammate.playSound(teammate.getLocation(), Sound.ENTITY_ALLAY_ITEM_TAKEN, 2, 0.5f);
                                                }
                                                return currentDamageValue;
                                            }
                                        });
                                    });
                    }
                })
        ) {

            @Override
            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                if (hasMaxStacks()) {
                    return currentHealValue * (1 + hexHealingBonus / 100f);
                } else {
                    return currentHealValue;
                }
            }

            public boolean hasMaxStacks() {
                return new CooldownFilter<>(wp, RegularCooldown.class)
                        .filterCooldownFrom(wp)
                        .filterCooldownClass(MercifulHex.class)
                        .stream()
                        .count() >= maxStacks;
            }

            @Override
            public float modifyDamageAfterAllFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (hasMaxStacks()) {
                    if (wp.getCurrentHealth() - currentDamageValue < 0) {
                        float healAmount = wp.getMaxHealth() * convertToPercent(lethalDamageHealing);
                        wp.addInstance(InstanceBuilder
                                .healing()
                                .ability(DivineBlessing.this)
                                .source(wp)
                                .value(healAmount)
                        );
                        wp.playSound(wp.getLocation(), Sound.ENTITY_ALLAY_ITEM_TAKEN, 2, 0.5f);
                    }
                }
                return currentDamageValue;
            }

            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler(priority = EventPriority.LOWEST)
                    private void onAddCooldown(WarlordsAddCooldownEvent event) {
                        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                        if (Objects.equals(cooldown.getFrom(), wp) &&
                                cooldown instanceof RegularCooldown<?> regularCooldown &&
                                cooldown.getCooldownObject() instanceof MercifulHex
                        ) {
                            regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + hexTickDurationIncrease);
                        }
                    }
                };
            }
        });
        PlayerFilter.playingGame(game)
                    .teammatesOf(wp)
                    .forEach(enemy -> {
                        new CooldownFilter<>(enemy, RegularCooldown.class)
                                .filterCooldownClass(MercifulHex.class)
                                .filterCooldownFrom(wp)
                                .forEach(cd -> cd.setTicksLeft(cd.getTicksLeft() + hexTickDurationIncrease));
                    });
        new GameRunnable(game) {

            @Override
            public void run() {
                healAllies(wp);
            }
        }.runTaskLater(postHealthTickDelay);


        if (pveMasterUpgrade2) {
            PlayerFilter.entitiesAround(wp, 10, 10, 10)
                        .aliveTeammatesOf(wp)
                        .forEach(warlordsEntity -> {
                            new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                                    .filterCooldownClass(MercifulHex.class)
                                    .forEach(regularCooldown -> {
                                        regularCooldown.setTicksLeft(regularCooldown.getStartingTicks() + hexTickDurationIncrease);
                                    });
                        });
        }

        return true;
    }

    private void healAllies(@Nonnull WarlordsEntity wp) {
        PlayerFilter.playingGame(wp.getGame())
                    .teammatesOf(wp)
                    .forEach(teammate -> {
                        teammate.playSound(teammate.getLocation(), "shaman.earthlivingweapon.impact", 1, 0.55f);
                        teammate.playSound(teammate.getLocation(), "arcanist.divineblessing.impact", 0.2f, 1.75f);
                        teammate.addInstance(InstanceBuilder
                                .healing()
                                .ability(this)
                                .source(wp)
                                .value(healingValues.divineBlessingPostHeal)
                        );
                    });
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new DivineBlessingBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getLethalDamageHealing() {
        return lethalDamageHealing;
    }

    public void setLethalDamageHealing(int lethalDamageHealing) {
        this.lethalDamageHealing = lethalDamageHealing;
    }

    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.SetValue divineBlessingPostHeal = new Value.SetValue(800);
        private final List<Value> values = List.of(divineBlessingPostHeal);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
