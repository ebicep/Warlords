package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.AstralPlagueBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
import java.util.stream.Collectors;

public class AstralPlague extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<AstralPlague, AstralPlague.AstralPlagueStats> {

    private final AstralPlagueStats stats = new AstralPlagueStats();
    private int tickDuration = 240;
    private int hexTickDurationIncrease = 40;

    public AstralPlague() {
        super(AbstractAbilityBuilder.create("astralPlague").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.hexTickDurationIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hexTickDurationIncrease"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "arcanist.astralplague.activation", 2, 1.1f);
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2, 0.7f);
        EffectUtils.playCircularShieldAnimation(wp.getLocation(), Particle.SOUL, 8, 3, 1);
        EffectUtils.playCircularEffectAround(wp.getGame(), wp.getLocation(), Particle.FLAME, 1, 1, 0.25, 1, 1, 2);
        List<FloatModifiable.FloatModifier> modifiers;
        if (pveMasterUpgrade2) {
            modifiers = wp.getAbilitiesMatching(SoulfireBeam.class)
                          .stream()
                          .map(soulfireBeam -> soulfireBeam.getCooldown().addMultiplicativeModifierMult(name + " Master", 0.6f))
                          .toList();
        } else {
            modifiers = Collections.emptyList();
        }
        wp.getCooldownManager().removeCooldown(AstralPlague.class, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(name, "ASTRAL", AstralPlague.class, new AstralPlague(), wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, cooldownManager -> {
            modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
        }, tickDuration
        ) {

            @Override
            protected Listener getListener() {
                return new Listener() {

                    @EventHandler(priority = EventPriority.LOWEST)
                    private void onAddCooldown(WarlordsAddCooldownEvent event) {
                        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                        if (Objects.equals(cooldown.getFrom(),
                                wp
                        ) && cooldown instanceof RegularCooldown<?> regularCooldown && cooldown.getCooldownObject() instanceof PoisonousHex) {
                            regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + hexTickDurationIncrease);
                            stats.hexesProlonged++;
                        }
                    }

                    @EventHandler
                    public void onDamageHeal(WarlordsDamageHealingEvent event) {
                        if (event.isHealingInstance()) {
                            return;
                        }
                        WarlordsEntity victim = event.getWarlordsEntity();
                        if (victim.equals(wp)) {
                            return;
                        }
                        if (!event.getSource().equals(wp)) {
                            return;
                        }
                        if (!(event.getAbility() instanceof SoulfireBeam soulfireBeam)) {
                            return;
                        }
                        event.setCritChance(100);
                        PoisonousHex fromHex = PoisonousHex.getFromHex(wp);
                        if (new CooldownFilter<>(victim, RegularCooldown.class).filterCooldownClass(PoisonousHex.class).stream().count() < fromHex.getMaxStacks()) {
                            return;
                        }
                        event.getFlags().add(InstanceFlags.PIERCE);
                        if (inPve) {
                            event.getFlags().add(InstanceFlags.IGNORE_SELF_RES);
                        }
                        stats.tripleStackBeams++;
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
                        List<AbstractCooldown<?>> cooldowns = event
                                .getPlayerCooldowns()
                                .stream()
                                .map(WarlordsDamageHealingFinalEvent.CooldownRecord::getAbstractCooldown)
                                .collect(Collectors.toList());
                        if (new CooldownFilter<>(cooldowns, RegularCooldown.class)
                                .filterCooldownClass(Intervene.InterveneData.class)
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

            @Override
            public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                if (pveMasterUpgrade) {
                    return currentCritMultiplier + 60;
                }
                return currentCritMultiplier;
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (inPve && event.getCause().equals("Poisonous Hex") && event.getFlags().contains(InstanceFlags.DOT)) {
                    return currentDamageValue * 5;
                }
                if (pveMasterUpgrade2 && event.getCause().equals("Soulfire Beam")) {
                    return currentDamageValue * 1.7f;
                }
                return currentDamageValue;
            }
        });
        PlayerFilter.playingGame(wp.getGame()).enemiesOf(wp).forEach(enemy -> {
            new CooldownFilter<>(enemy, RegularCooldown.class).filterCooldownClass(PoisonousHex.class).filterCooldownFrom(wp).forEach(cd -> {
                cd.setTicksLeft(cd.getTicksLeft() + hexTickDurationIncrease);
                stats.hexesProlonged++;
            });
        });
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        AbilityDescriptionBuilder builder = AbilityDescriptionBuilder
                .create("Grant yourself Astral Energy, increasing ")
                .text("PHEX", NamedTextColor.DARK_RED)
                .text(" duration by ")
                .durationTicks(hexTickDurationIncrease)
                .text(" and causing Soulfire Beam to not consume ")
                .text("PHEX", NamedTextColor.DARK_RED)
                .text(" stacks and always crit.")
                .emptyLine()
                .text("Soulfire Beam pierces the shields and defenses of enemies with max stacks of ")
                .text("PHEX", NamedTextColor.DARK_RED);
        if (inPve) {
            builder.text(". For the duration of Astral Plague the damage from Poisonous Hex stacks are increased by 400%");
        }
        description = builder.text(". Lasts ").durationTicks(tickDuration).text(".").build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new AstralPlagueBranch(abilityTree, this);
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
    public AstralPlagueStats getAbilityStats() {
        return stats;
    }

    public static class AstralPlagueStats extends AbstractAbilityStats<AstralPlague, AstralPlagueStats> {

        @Field("hexes_prolonged")
        private int hexesProlonged = 0;

        @Field("hexes_not_consumed")
        private int hexesNotConsumed = 0;

        @Field("triple_stack_beams")
        private int tripleStackBeams = 0;

        @Field("shields_pierced")
        private int shieldsPierced = 0;

        @Field("intervenes_pierced")
        private int intervenesPierced = 0;

        @Override
        public Class<AstralPlagueStats> getClazz() {
            return AstralPlagueStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Hexes Prolonged", hexesProlonged));
            statsDisplay.add(new AbilityStatDisplay("Hexes Not Consumed", hexesNotConsumed));
            statsDisplay.add(new AbilityStatDisplay("Triple Stack Beams", tripleStackBeams));
            statsDisplay.add(new AbilityStatDisplay("Shields Pierced", shieldsPierced));
            statsDisplay.add(new AbilityStatDisplay("Intervenes Pierced", intervenesPierced));
            return statsDisplay;
        }

        @Override
        public AstralPlagueStats merge(AstralPlagueStats other, int multiplier) {
            AstralPlagueStats stats = super.merge(other, multiplier);
            stats.hexesProlonged = this.hexesProlonged + other.hexesProlonged * multiplier;
            stats.hexesNotConsumed = this.hexesNotConsumed + other.hexesNotConsumed * multiplier;
            stats.tripleStackBeams = this.tripleStackBeams + other.tripleStackBeams * multiplier;
            stats.shieldsPierced = this.shieldsPierced + other.shieldsPierced * multiplier;
            stats.intervenesPierced = this.intervenesPierced + other.intervenesPierced * multiplier;
            return stats;
        }

        @Override
        public AstralPlagueStats create() {
            return new AstralPlagueStats();
        }

        public int getHexesNotConsumed() {
            return hexesNotConsumed;
        }

        public void setHexesNotConsumed(int hexesNotConsumed) {
            this.hexesNotConsumed = hexesNotConsumed;
        }

    }

}
