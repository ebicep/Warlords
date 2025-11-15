package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownUtils;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.vindicator.VindicateBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vindicate extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<Vindicate, Vindicate.VindicateStats> {

    private final VindicateStats stats = new VindicateStats();
    private int radius = 8;
    private int vindTickDuration = 240;
    private int damageReductionTickDuration = 160;
    private float vindicateDamageReduction = 30;

    public Vindicate() {
        super(AbstractAbilityBuilder.create("vindicate").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), int.class);
        this.vindTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("vindTickDuration"), int.class);
        this.damageReductionTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReductionTickDuration"), int.class);
        this.vindicateDamageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("vindicateDamageReduction"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "rogue.vindicate.activation", 2, 0.7f);
        Utils.playGlobalSound(wp.getLocation(), "shaman.capacitortotem.pulse", 2, 0.7f);
        new CircleEffect(wp.getGame(), wp.getTeam(), wp.getLocation(), radius, new CircumferenceEffect(Particle.EFFECT, Particle.DUST).particlesPerCircumference(2)).playEffects();
        EffectUtils.playHelixAnimation(wp.getLocation(), radius, 230, 130, 5);
        for (WarlordsEntity vindicateTarget : PlayerFilter.entitiesAround(wp, radius, radius, radius).aliveTeammatesOf(wp)) {
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
            stats.debuffsRemovedOnCast += vindicateTarget.getCooldownManager().removeDebuffCooldownsVind();
            giveVindicateCooldown(wp, vindicateTarget, Vindicate.class, null, vindTickDuration);
        }
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Vindicate Resistance",
                "VIND RESIST",
                Vindicate.class,
                null,
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                damageReductionTickDuration
        ).addModifier(Modifier.DAMAGE_AFTER_INTERVENE_SELF, (event, currentDamageValue) -> {
                    WarlordsEntity hit = event.getWarlordsEntity();
                    WarlordsEntity attacker = event.getSource();
                    if (pveMasterUpgrade && !Objects.equals(attacker, hit)) {
                        Utils.addKnockback(name, wp.getLocation(), attacker, -1, 0.15);
                        attacker.addInstance(InstanceBuilder
                                .damage()
                                .cause(name)
                                .source(hit)
                                .value(currentDamageValue.getCalculatedValue() * .75f)
                                .flags(InstanceFlags.IGNORE_SELF_RES, InstanceFlags.RECURSIVE, InstanceFlags.REFLECTIVE_DAMAGE)
                        );
                        currentDamageValue.addMultiplicativeModifierMult(name, .1f);
                    } else {
                        currentDamageValue.addMultiplicativeModifierMult(name, getCalculatedVindicateDamageReduction());
                    }
                }
        ));
        if (pveMasterUpgrade2) {
            for (WarlordsEntity vindicateTarget : PlayerFilter.entitiesAround(wp, radius, radius, radius).aliveEnemiesOf(wp)) {
                SoulShackle.shacklePlayer(wp, vindicateTarget, 10 * 20);
            }
        }
        return true;
    }

    public static <T> void giveVindicateCooldown(WarlordsEntity from, WarlordsEntity target, Class<T> cooldownClass, T cooldownObject, int tickDuration) {
        // remove other instances of vindicate buff to override
        target.getCooldownManager().removeCooldownByName("Vindicate");
        boolean vindPveMaster2 = cooldownClass.equals(Vindicate.class) && from.getAbilitiesMatching(Vindicate.class).stream().anyMatch(t -> t.pveMasterUpgrade2);
        RegularCooldown<T> vindiateCooldown = new RegularCooldown<>(
                "Vindicate", "VIND",
                cooldownClass,
                cooldownObject,
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                tickDuration
        ) {

            @Override
            protected Listener getListener() {
                if (target.isInPve()) {
                    return CooldownUtils.getDebuffImmunityListener(CooldownUtils.DebuffImmunity.getFullImmunity(target));
                }

                return CooldownUtils.getPartialDebuffImmunityListener(target);
            }
        };
        vindiateCooldown.addModifier(Modifier.DAMAGE_AFTER_INTERVENE_SELF, (event, currentDamageValue) -> {
                    if (vindPveMaster2) {
                        currentDamageValue.addMultiplicativeModifierMult("Vindicate", .85f);
                    }
                }
        );
        target.addKnockbackModifier(from, "Vindicate", -getKnockbackResistance(), vindiateCooldown);
        target.getCooldownManager().addCooldown(vindiateCooldown);
        if (vindPveMaster2) {
            EffectUtils.playParticleLinkAnimation(from.getLocation(), target.getLocation(), Particle.FALLING_HONEY, 1, 1, -1);
        }
    }

    public float getCalculatedVindicateDamageReduction() {
        return (100 - vindicateDamageReduction) / 100f;
    }

    private static int getKnockbackResistance() {
        return ConfigManager.getAbilityConfigValue(ConfigManager.DEFAULT_NAMESPACES, "vindicate.knockbackResistance", int.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("All allies within ")
                .blocks(radius)
                .text(" gain the status ")
                .text("VIND", NamedTextColor.DARK_GREEN)
                .text(" for ")
                .durationTicks(vindTickDuration)
                .text(", granting an immunity to de-buffs and ")
                .percent(getKnockbackResistance(), AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" knockback resistance. You gain")
                .percent(vindicateDamageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" damage reduction for ")
                .durationTicks(damageReductionTickDuration)
                .text(".")
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new VindicateBranch(abilityTree, this);
    }

    @Override
    public void multiplyTickDuration(float multiplier) {
        this.vindTickDuration *= multiplier;
        this.damageReductionTickDuration *= multiplier;
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
    public VindicateStats getAbilityStats() {
        return stats;
    }

    public float getVindicateDamageReduction() {
        return vindicateDamageReduction;
    }

    public void setVindicateDamageReduction(float vindicateDamageReduction) {
        this.vindicateDamageReduction = vindicateDamageReduction;
    }

    public int getDamageReductionTickDuration() {
        return damageReductionTickDuration;
    }

    public void setDamageReductionTickDuration(int damageReductionTickDuration) {
        this.damageReductionTickDuration = damageReductionTickDuration;
    }

    public int getVindTickDuration() {
        return vindTickDuration;
    }

    public void setVindTickDuration(int vindTickDuration) {
        this.vindTickDuration = vindTickDuration;
    }

    public static class VindicateStats extends AbstractAbilityStats<Vindicate, VindicateStats> {

        @Field("debuffs_removed_on_cast")
        private int debuffsRemovedOnCast = 0;

        @Override
        public Class<VindicateStats> getClazz() {
            return VindicateStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Debuffs Removed On Cast", debuffsRemovedOnCast));
            return statsDisplay;
        }

        @Override
        public VindicateStats merge(VindicateStats other, int multiplier) {
            VindicateStats stats = super.merge(other, multiplier);
            stats.debuffsRemovedOnCast = this.debuffsRemovedOnCast + other.debuffsRemovedOnCast * multiplier;
            return stats;
        }

        @Override
        public VindicateStats create() {
            return new VindicateStats();
        }

    }

}
