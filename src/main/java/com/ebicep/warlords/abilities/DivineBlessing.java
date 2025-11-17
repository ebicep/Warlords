package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.DivineBlessingBranch;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DivineBlessing extends AbstractAbility implements OrangeAbilityIcon, Duration, Heals<DivineBlessing.HealingValues>, AbilityStats<DivineBlessing, DivineBlessing.DivineBlessingStats> {

    private final DivineBlessingStats stats = new DivineBlessingStats();
    private final HealingValues healingValues = new HealingValues();
    private int hexTickDurationIncrease = 40;
    private int hexHealingBonus = 30;
    private int postHealthTickDelay = 40;
    private int tickDuration = 240;

    public DivineBlessing() {
        super(AbstractAbilityBuilder.create("divineBlessing").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.hexTickDurationIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hexTickDurationIncrease"), int.class);
        this.hexHealingBonus = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hexHealingBonus"), int.class);
        this.postHealthTickDelay = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("postHealthTickDelay"), int.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "arcanist.divineblessing.activation", 2, 1.2f);
        Utils.playGlobalSound(wp.getLocation(), "paladin.holyradiance.activation", 2, 1.6f);
        EffectUtils.strikeLightning(wp.getLocation(), true);
        Game game = wp.getGame();
        new GameRunnable(game) {

            double interval = 2;

            @Override
            public void run() {
                interval -= 0.5;
                EffectUtils.playCylinderAnimation(wp.getLocation(), 1.5 + interval, 70, 255, 70);
                if (interval <= 0) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 1);
        DivineBlessingData data = new DivineBlessingData(this);
        int maxStacks = MercifulHex.getFromHex(wp).getMaxStacks();
        List<FloatModifiable.FloatModifier> modifiers;
        if (pveMasterUpgrade2) {
            modifiers = wp.getAbilitiesMatching(RayOfLight.class).stream().map(ability -> ability.getCooldown().addMultiplicativeModifierMult(name + " Master", 0.55f)).toList();
        } else {
            modifiers = Collections.emptyList();
        }
        wp.getCooldownManager().addCooldown(new RegularCooldown<DivineBlessingData>(
                name,
                "BLESS",
                DivineBlessingData.class,
                data,
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
                    if (ticksElapsed == postHealthTickDelay) {
                        healAllies(wp);
                    }
                    if (ticksElapsed % 10 == 0) {
                        EffectUtils.displayParticle(Particle.CRIMSON_SPORE, wp.getLocation(), 10, 0.1, 0.1, 0.1, 0.5);
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
                                        if (pveMasterUpgrade) {
                                            Vindicate.giveVindicateCooldown(wp, teammate, DivineBlessing.class, new DivineBlessing(), tickDuration);
                                        }
                                        teammate.getCooldownManager().removeCooldownByObject(data);
                                        teammate.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                name,
                                                "DIV",
                                                DivineBlessingData.class,
                                                data,
                                                wp,
                                                CooldownTypes.ABILITY,
                                                cooldownManager -> {
                                                },
                                                21
                                        ).addModifier(Modifier.HEALING_MODIFY_SELF, (event, currentHealValue) -> {
                                            currentHealValue.addMultiplicativeModifierMult(
                                                    name,
                                                    convertToMultiplicationDecimal(hexHealingBonus),
                                                    contribution -> stats.healingIncreased += Math.abs(contribution)
                                            );
                                                }
                                        ));
                                    });
                    }
                })
        ) {

            @Override
            protected Listener getListener() {
                return new Listener() {

                    @EventHandler(priority = EventPriority.LOWEST)
                    private void onAddCooldown(WarlordsAddCooldownEvent event) {
                        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                        if (Objects.equals(cooldown.getFrom(),
                                wp
                        ) && cooldown instanceof RegularCooldown<?> regularCooldown && cooldown.getCooldownObject() instanceof MercifulHex) {
                            regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + hexTickDurationIncrease);
                            stats.hexesProlonged++;
                        }
                    }
                };
            }
        }.addModifier(Modifier.HEALING_MODIFY_SELF, (event, currentHealValue) -> {
                    if (new CooldownFilter<>(wp, RegularCooldown.class).filterCooldownFrom(wp).filterCooldownClass(MercifulHex.class).stream().count() >= maxStacks) {
                        currentHealValue.addMultiplicativeModifierMult(
                                name,
                                convertToMultiplicationDecimal(hexHealingBonus),
                                contribution -> stats.healingIncreased += Math.abs(contribution)
                        );
                    }
                }
        ));
        PlayerFilter.playingGame(game).teammatesOf(wp).forEach(enemy -> {
            new CooldownFilter<>(enemy, RegularCooldown.class).filterCooldownClass(MercifulHex.class).filterCooldownFrom(wp).forEach(cd -> {
                cd.setTicksLeft(cd.getTicksLeft() + hexTickDurationIncrease);
                stats.hexesProlonged++;
            });
        });
        if (pveMasterUpgrade2) {
            PlayerFilter.entitiesAround(wp, 10, 10, 10).aliveTeammatesOf(wp).forEach(warlordsEntity -> {
                new CooldownFilter<>(warlordsEntity, RegularCooldown.class).filterCooldownClass(MercifulHex.class).forEach(regularCooldown -> {
                    regularCooldown.setTicksLeft(regularCooldown.getStartingTicks() + hexTickDurationIncrease);
                    stats.hexesProlonged++;
                });
            });
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Imbue yourself with Holy Energy, increasing ")
                .text("MHEX", NamedTextColor.DARK_GREEN)
                .text(" duration by ")
                .durationTicks(hexTickDurationIncrease)
                .text(" and causing Ray of Light to not consume ")
                .text("MHEX", NamedTextColor.DARK_GREEN)
                .text(" stacks.")
                .emptyLine()
                .text("Allies with max stacks of ")
                .text("MHEX", NamedTextColor.DARK_GREEN)
                .text(" receive ")
                .percent(hexHealingBonus, NamedTextColor.GREEN)
                .text(" more healing from all sources. After ")
                .durationTicks(postHealthTickDelay)
                .text("seconds all allies restore ")
                .heal(healingValues.divineBlessingPostHeal)
                .text(" health. Lasts ")
                .durationTicks(tickDuration)
                .text(".")
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new DivineBlessingBranch(abilityTree, this);
    }

    private void healAllies(@Nonnull WarlordsEntity wp) {
        PlayerFilter.playingGame(wp.getGame()).teammatesOf(wp).forEach(teammate -> {
            teammate.playSound(teammate.getLocation(), "shaman.earthlivingweapon.impact", 1, 0.55f);
            teammate.playSound(teammate.getLocation(), "arcanist.divineblessing.impact", 0.2f, 1.75f);
            teammate.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.divineBlessingPostHeal));
        });
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
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public DivineBlessingStats getAbilityStats() {
        return stats;
    }

    public int getHexHealingBonus() {
        return hexHealingBonus;
    }

    public void setHexHealingBonus(int hexHealingBonus) {
        this.hexHealingBonus = hexHealingBonus;
    }

    public int getPostHealthTickDelay() {
        return postHealthTickDelay;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue divineBlessingPostHeal = new Value.SetValue(800);

        private List<Value> values = List.of(divineBlessingPostHeal);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.divineBlessingPostHeal = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("divineBlessingPostHeal"),
                    Value.SetValue.class
            );
            this.values = List.of(divineBlessingPostHeal);
        }

    }

    public static class DivineBlessingData {

        private DivineBlessing divineBlessing;

        public DivineBlessingData(DivineBlessing divineBlessing) {
            this.divineBlessing = divineBlessing;
        }

        public DivineBlessing getDivineBlessing() {
            return divineBlessing;
        }

    }

    public static class DivineBlessingStats extends AbstractAbilityStats<DivineBlessing, DivineBlessingStats> {

        @Field("hexes_prolonged")
        private int hexesProlonged = 0;

        @Field("hexes_not_consumed")
        private int hexesNotConsumed = 0;

        @Field("healing_increased")
        private float healingIncreased = 0;

        @Field("lethal_damge_healed")
        private int lethalDamgeHealed = 0;

        @Override
        public Class<DivineBlessingStats> getClazz() {
            return DivineBlessingStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Hexes Prolonged", hexesProlonged));
            statsDisplay.add(new AbilityStatDisplay("Hexes Not Consumed", hexesNotConsumed));
            statsDisplay.add(new AbilityStatDisplay("Healing Increased", Math.round(healingIncreased)));
            return statsDisplay;
        }

        @Override
        public DivineBlessingStats merge(DivineBlessingStats other, int multiplier) {
            DivineBlessingStats stats = super.merge(other, multiplier);
            stats.hexesProlonged = this.hexesProlonged + other.hexesProlonged * multiplier;
            stats.hexesNotConsumed = this.hexesNotConsumed + other.hexesNotConsumed * multiplier;
            stats.healingIncreased = this.healingIncreased + other.healingIncreased * multiplier;
            stats.lethalDamgeHealed = this.lethalDamgeHealed + other.lethalDamgeHealed * multiplier;
            return stats;
        }

        @Override
        public DivineBlessingStats create() {
            return new DivineBlessingStats();
        }

        public int getHexesNotConsumed() {
            return hexesNotConsumed;
        }

        public void setHexesNotConsumed(int hexesNotConsumed) {
            this.hexesNotConsumed = hexesNotConsumed;
        }

    }

}
