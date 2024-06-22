package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
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
import com.ebicep.warlords.util.java.Pair;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AstralPlague extends AbstractAbility implements OrangeAbilityIcon, Duration {

    public int hexesProlonged = 0;
    public int hexesNotConsumed = 0;
    public int tripleStackBeams = 0;
    public int shieldsPierced = 0;
    public int intervenesPierced = 0;

    private int tickDuration = 240;
    private int hexTickDurationIncrease = 40;

    public AstralPlague() {
        super("Astral Plague", 50, 10);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Grant yourself Astral Energy, increasing Poisonous Hex duration by ")
                               .append(Component.text(format(hexTickDurationIncrease / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds and causing Soulfire Beam to not consume Poisonous Hex stacks. " +
                                       "\n\nYour attacks pierces shields and defenses of enemies with "))
                               .append(Component.text("3", NamedTextColor.RED))
                               .append(Component.text(" stacks of Poisonous Hex. Lasts"))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. "));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Hexes Prolonged", "" + hexesProlonged));
        info.add(new Pair<>("Hexes Not Consumed", "" + hexesNotConsumed));
        info.add(new Pair<>("Triple Stack Beams", "" + tripleStackBeams));
        info.add(new Pair<>("Shields Pierced", "" + shieldsPierced));
        info.add(new Pair<>("Intervenes Pierced", "" + intervenesPierced));
        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
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
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ASTRAL",
                AstralPlague.class,
                new AstralPlague(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                },
                tickDuration
        ) {

            @Override
            public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                if (pveMasterUpgrade) {
                    return currentCritMultiplier + 40;
                }
                return currentCritMultiplier;
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveMasterUpgrade2 && event.getCause().equals("Soulfire Beam")) {
                    return currentDamageValue * 1.4f;
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
                                cooldown.getCooldownObject() instanceof PoisonousHex
                        ) {
                            regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + hexTickDurationIncrease);
                            hexesProlonged++;
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
                        PoisonousHex fromHex = PoisonousHex.getFromHex(wp);
                        if (new CooldownFilter<>(victim, RegularCooldown.class)
                                .filterCooldownClass(PoisonousHex.class)
                                .stream()
                                .count() < fromHex.getMaxStacks()
                        ) {
                            return;
                        }
                        event.getFlags().add(InstanceFlags.PIERCE);
                        if (inPve) {
                            event.getFlags().add(InstanceFlags.IGNORE_SELF_RES);
                        }
                        if (pveMasterUpgrade && Objects.equals(event.getCause(), "Soulfire Beam")) {
                            event.setCritChance(100);
                        }
                        tripleStackBeams++;
                    }

                    @EventHandler
                    public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                        if (event.getAttacker() != wp) {
                            return;
                        }
                        if (!event.getAbility().equals("Soufire Beam")) {
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
                                .filterCooldownClass(Intervene.class)
                                .filter(regularCooldown -> !Objects.equals(regularCooldown.getFrom(), target))
                                .findAny()
                                .isPresent()
                        ) {
                            intervenesPierced++;
                        }
                        if (new CooldownFilter<>(cooldowns, RegularCooldown.class)
                                .filterCooldownClass(Shield.class)
                                .filter(RegularCooldown::hasTicksLeft)
                                .findAny()
                                .isPresent()
                        ) {
                            shieldsPierced++;
                        }
                    }

                };
            }
        });
        PlayerFilter.playingGame(wp.getGame())
                    .enemiesOf(wp)
                    .forEach(enemy -> {
                        new CooldownFilter<>(enemy, RegularCooldown.class)
                                .filterCooldownClass(PoisonousHex.class)
                                .filterCooldownFrom(wp)
                                .forEach(cd -> {
                                    cd.setTicksLeft(cd.getTicksLeft() + hexTickDurationIncrease);
                                    hexesProlonged++;
                                });
                    });
        return true;
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
}
