package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.vindicator.VindicateBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.*;

public class Vindicate extends AbstractAbility implements OrangeAbilityIcon, Duration {

    private static int knockbackResistance = 50;
    public int debuffsRemovedOnCast = 0;
    private final int radius = 8;
    private int vindTickDuration = 240;
    private int damageReductionTickDuration = 160;
    private float vindicateDamageReduction = 30;

    public Vindicate() {
        super("Vindicate", 0, 0, 55, 25);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("All allies within an ")
                               .append(Component.text(radius, NamedTextColor.YELLOW))
                               .append(Component.text(" block radius gain the status "))
                               .append(Component.text("VIND", NamedTextColor.GOLD))
                               .append(Component.text(", which clears all de-buffs. In addition, the status "))
                               .append(Component.text("VIND", NamedTextColor.GOLD))
                               .append(Component.text(" prevents allies from being affected by de-buffs and grants "))
                               .append(Component.text(knockbackResistance + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" knockback resistance for "))
                               .append(Component.text(format(vindTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. You gain "))
                               .append(Component.text(format(vindicateDamageReduction) + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" damage reduction for "))
                               .append(Component.text(format(damageReductionTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Debuffs Removed On Cast", "" + debuffsRemovedOnCast));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "rogue.vindicate.activation", 2, 0.7f);
        Utils.playGlobalSound(wp.getLocation(), "shaman.capacitortotem.pulse", 2, 0.7f);

        new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                wp.getLocation(),
                radius,
                new CircumferenceEffect(Particle.SPELL, Particle.REDSTONE).particlesPerCircumference(2)
        ).playEffects();

        EffectUtils.playHelixAnimation(wp.getLocation(), radius, 230, 130, 5);

        Vindicate tempVindicate = new Vindicate();
        tempVindicate.setPveMasterUpgrade2(pveMasterUpgrade2);
        for (WarlordsEntity vindicateTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOf(wp)
        ) {
            if (vindicateTarget != wp) {
                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                        .append(Component.text(" Your Vindicate is now protecting ", NamedTextColor.GRAY))
                        .append(Component.text(vindicateTarget.getName(), NamedTextColor.YELLOW))
                        .append(Component.text("!", NamedTextColor.GRAY))
                );
                vindicateTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                        .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                        .append(Component.text("Vindicate", NamedTextColor.YELLOW))
                        .append(Component.text(" is now protecting you from de-buffs for ", NamedTextColor.GRAY))
                        .append(Component.text(format(vindTickDuration / 20f), NamedTextColor.GOLD))
                        .append(Component.text(" seconds!", NamedTextColor.GRAY))
                );
            }

            // Vindicate Immunity
            vindicateTarget.getSpeed().removeSlownessModifiers();
            debuffsRemovedOnCast += vindicateTarget.getCooldownManager().removeDebuffCooldowns();
            giveVindicateCooldown(wp, vindicateTarget, Vindicate.class, tempVindicate, vindTickDuration);
        }

        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Vindicate Resistance",
                "VIND RESIST",
                Vindicate.class,
                tempVindicate,
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                damageReductionTickDuration
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                WarlordsEntity hit = event.getWarlordsEntity();
                WarlordsEntity attacker = event.getSource();
                if (pveMasterUpgrade && !Objects.equals(attacker, hit)) {
                    Utils.addKnockback(name, wp.getLocation(), attacker, -1, 0.15);
                    attacker.addDamageInstance(
                            hit,
                            name,
                            currentDamageValue * .75f,
                            currentDamageValue * .75f,
                            0,
                            100,
                            EnumSet.of(InstanceFlags.IGNORE_SELF_RES, InstanceFlags.RECURSIVE, InstanceFlags.REFLECTIVE_DAMAGE)
                    );
                    return currentDamageValue * .1f;
                } else {
                    return currentDamageValue * getCalculatedVindicateDamageReduction();
                }
            }
        });

        if (pveMasterUpgrade2) {
            for (WarlordsEntity vindicateTarget : PlayerFilter
                    .entitiesAround(wp, radius, radius, radius)
                    .aliveEnemiesOf(wp)
            ) {
                SoulShackle.shacklePlayer(wp, vindicateTarget, 10 * 20);
            }
        }

        return true;
    }

    public static <T> void giveVindicateCooldown(WarlordsEntity from, WarlordsEntity target, Class<T> cooldownClass, T cooldownObject, int tickDuration) {
        // remove other instances of vindicate buff to override
        target.getCooldownManager().removeCooldownByName("Debuff Immunity");
        boolean vindPveMaster2 = cooldownObject instanceof Vindicate vindicate && vindicate.pveMasterUpgrade2;
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Debuff Immunity",
                "VIND",
                cooldownClass,
                cooldownObject,
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                })
        ) {
            @Override
            public void multiplyKB(Vector currentVector) {
                currentVector.multiply(knockbackResistance / 100f);
            }

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (vindPveMaster2) {
                    return currentDamageValue * .85f;
                }
                return currentDamageValue;
            }
        });
        if (vindPveMaster2) {
            EffectUtils.playParticleLinkAnimation(from.getLocation(), target.getLocation(), Particle.FALLING_HONEY, 1, 1, -1);
        }
    }

    public float getCalculatedVindicateDamageReduction() {
        return (100 - vindicateDamageReduction) / 100f;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new VindicateBranch(abilityTree, this);
    }

    public float getVindicateDamageReduction() {
        return vindicateDamageReduction;
    }

    public void setVindicateDamageReduction(float vindicateDamageReduction) {
        this.vindicateDamageReduction = vindicateDamageReduction;
    }


    @Override
    public int getTickDuration() {
        return vindTickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.vindTickDuration = tickDuration;
    }

    @Override
    public void multiplyTickDuration(float multiplier) {
        this.vindTickDuration *= multiplier;
        this.damageReductionTickDuration *= multiplier;
    }

    public int getDamageReductionTickDuration() {
        return damageReductionTickDuration;
    }

    public void setDamageReductionTickDuration(int damageReductionTickDuration) {
        this.damageReductionTickDuration = damageReductionTickDuration;
    }
}
