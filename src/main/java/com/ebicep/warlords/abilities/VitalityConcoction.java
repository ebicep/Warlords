package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.apothecary.VitalityConcoctionBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;

public class VitalityConcoction extends AbstractAbility implements PurpleAbilityIcon, Duration {

    private int tickDuration = 15;
    private int damageResistance = 80;
    private int speedBoost = 150;

    public VitalityConcoction() {
        super("Vitality Concoction", 12, 20);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Consume a powerful concoction, granting yourself an additional ")
                               .append(Component.text(speedBoost + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" movement speed, ")
                                                .append(Component.text(damageResistance + "%", NamedTextColor.YELLOW))
                                                .append(Component.text(" damage reduction, and an immunity to de-buffs for "))
                                                .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                                .append(Component.text(" seconds.\n\nVitality Concoction has reduced effectiveness when holding a flag.")));

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new VitalityConcoctionBranch(abilityTree, this);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 0.1f);
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_BLAZE_DEATH, 2, 0.7f);
        new FallingBlockWaveEffect(wp.getLocation(), 4, 1, Material.BIRCH_SAPLING).play();

        List<WarlordsEntity> playersHit = new ArrayList<>();
        Set<WarlordsEntity> targets = new HashSet<>();
        targets.add(wp);
        if (pveMasterUpgrade2) {
            targets.addAll(PlayerFilterGeneric
                    .entitiesAround(wp, 5, 5, 5)
                    .aliveTeammatesOfExcludingSelf(wp)
                    .toList()
            );
        }
        for (WarlordsEntity target : targets) {
            target.addSpeedModifier(wp, name, wp.hasFlag() ? 40 : speedBoost, tickDuration, true);
            target.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Debuff Immunity",
                    "STIM",
                    VitalityConcoction.class,
                    new VitalityConcoction(),
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {

                    },
                    tickDuration,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (!pveMasterUpgrade) {
                            return;
                        }
                        if (ticksElapsed % 5 != 0) {
                            return;
                        }
                        for (WarlordsNPC we : PlayerFilterGeneric
                                .entitiesAround(wp, 3, 3, 3)
                                .aliveEnemiesOf(wp)
                                .excluding(playersHit)
                                .warlordsNPCs()
                        ) {
                            playersHit.add(we);
                            we.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(this)
                                    .source(wp)
                                    .value(damageValues.concoctionZoneDamage)
                            );
                        }
                    })
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * convertToDivisionDecimal(damageResistance);
                }
            });
        }

        return true;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    private final DamageValues damageValues = new DamageValues();

    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValue concoctionZoneDamage = new Value.RangedValue(1245, 1625);
        private final List<Value> values = List.of(concoctionZoneDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}
