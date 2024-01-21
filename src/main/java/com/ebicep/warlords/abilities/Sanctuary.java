package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.SanctuaryBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.*;

public class Sanctuary extends AbstractAbility implements OrangeAbilityIcon, Duration {

    private int hexTickDurationIncrease = 40;
    private int additionalDamageReduction = 10;
    private int tickDuration = 240;

    public Sanctuary() {
        super("Sanctuary", 0, 0, 50, 10, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Summon your full protective power, increasing Fortifying Hex duration by ")
                               .append(Component.text(format(hexTickDurationIncrease / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds and causing Guardian Beam to not consume Fortifying Hex stacks. " +
                                       "\n\nAll allies with max stacks of Fortifying Hex gain an additional "))
                               .append(Component.text(additionalDamageReduction + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" damage reduction and reflect all reduced damage from Fortifying Hexes back to the dealer. Lasts "))
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


        Location loc = wp.getLocation();
        Utils.playGlobalSound(wp.getLocation(), "warrior.laststand.activation", 2, 1.8f);
        Utils.playGlobalSound(loc, "arcanist.sanctuary.activation", 2, 0.55f);

        EffectUtils.playCircularShieldAnimation(loc, Particle.END_ROD, 5, 0.8, 2);
        EffectUtils.playCircularShieldAnimation(loc, Particle.DRIP_WATER, 3, 0.6, 1.2);

        List<FloatModifiable.FloatModifier> modifiers;
        if (pveMasterUpgrade2) {
            modifiers = wp.getAbilitiesMatching(GuardianBeam.class)
                          .stream()
                          .map(ability -> ability.getCooldown().addMultiplicativeModifierMult(name + " Master", 0.55f))
                          .toList();
        } else {
            modifiers = Collections.emptyList();
        }
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "SANCTUARY",
                Sanctuary.class,
                new Sanctuary(),
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
            protected Listener getListener() {
                return new Listener() {

                    @EventHandler
                    public void onDamageHeal(WarlordsDamageHealingEvent event) {
                        WarlordsEntity teammate = event.getWarlordsEntity();
                        if (event.isHealingInstance()) {
                            return;
                        }
                        if (event.getFlags().contains(InstanceFlags.RECURSIVE)) {
                            return;
                        }
                        if (teammate.isEnemy(wp)) {
                            return;
                        }
                        int hexStacks = (int) new CooldownFilter<>(teammate, RegularCooldown.class)
                                .filterCooldownFrom(wp)
                                .filterCooldownClass(FortifyingHex.class)
                                .stream()
                                .count();
                        if (hexStacks < FortifyingHex.getFromHex(wp).getMaxStacks()) {
                            return;
                        }
                        FortifyingHex fromHex = FortifyingHex.getFromHex(wp);
                        float damageToReflect = (additionalDamageReduction + fromHex.getDamageReduction() * 3) / 100f;
                        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_VEX_HURT, 1, 1.9f);
                        EnumSet<InstanceFlags> flags = EnumSet.of(InstanceFlags.RECURSIVE, InstanceFlags.REFLECTIVE_DAMAGE);
                        if (pveMasterUpgrade) {
                            flags.add(InstanceFlags.TRUE_DAMAGE);
                        }
                        event.getAttacker().addDamageInstance(
                                teammate,
                                name,
                                event.getMin() * damageToReflect,
                                event.getMax() * damageToReflect,
                                0,
                                100,
                                flags
                        );
                        float damageToReduce = 1 - damageToReflect;
                        event.setMin(event.getMin() * damageToReduce);
                        event.setMax(event.getMax() * damageToReduce);
                    }

                    @EventHandler(priority = EventPriority.LOWEST)
                    private void onAddCooldown(WarlordsAddCooldownEvent event) {
                        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                        if (!Objects.equals(cooldown.getFrom(), wp) ||
                                !(cooldown instanceof RegularCooldown<?> regularCooldown)
                        ) {
                            return;
                        }
                        Object cdObject = cooldown.getCooldownObject();
                        if (!(cdObject instanceof FortifyingHex)) {
                            regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + hexTickDurationIncrease);
                        }
                        if (pveMasterUpgrade2 &&
                                !event.getWarlordsEntity().equals(wp) &&
                                cdObject instanceof GuardianBeam.GuardianBeamShield guardianBeamShield
                        ) {
                            float oldShieldPercent = guardianBeamShield.getShieldPercent() / 100;
                            float newShieldPercent = oldShieldPercent + .15f;
                            float newShieldHealth = guardianBeamShield.getMaxShieldHealth() / oldShieldPercent * newShieldPercent;
                            guardianBeamShield.setMaxShieldHealth(newShieldHealth);
                            guardianBeamShield.setShieldHealth(newShieldHealth);
                        }
                    }
                };
            }
        });
        PlayerFilter.playingGame(wp.getGame())
                    .teammatesOf(wp)
                    .forEach(enemy -> {
                        new CooldownFilter<>(enemy, RegularCooldown.class)
                                .filterCooldownClass(FortifyingHex.class)
                                .filterCooldownFrom(wp)
                                .forEach(cd -> cd.setTicksLeft(cd.getTicksLeft() + hexTickDurationIncrease));
                    });
        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SanctuaryBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getHexTickDurationIncrease() {
        return hexTickDurationIncrease;
    }

    public void setHexTickDurationIncrease(int hexTickDurationIncrease) {
        this.hexTickDurationIncrease = hexTickDurationIncrease;
    }

    public int getAdditionalDamageReduction() {
        return additionalDamageReduction;
    }

    public void setAdditionalDamageReduction(int additionalDamageReduction) {
        this.additionalDamageReduction = additionalDamageReduction;
    }

}
