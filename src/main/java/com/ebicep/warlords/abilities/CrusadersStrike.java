package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractStrike;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.crusader.CrusadersStrikeBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CrusadersStrike extends AbstractStrike implements Damages<CrusadersStrike.DamageValues> {

    public float energyGivenToPlayers = 0;
    private final DamageValues damageValues = new DamageValues();
    private int energyGiven = 21;
    private int energyRadius = 10;
    private int energyMaxAllies = 2;

    public CrusadersStrike() {
        super("Crusader's Strike", 326, 441, 0, 90, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Strike the targeted enemy player, causing ")
                               .append(Damages.formatDamage(damageValues.strikeDamage))
                               .append(Component.text(" damage and restoring "))
                               .append(Component.text(energyGiven, NamedTextColor.YELLOW))
                               .append(Component.text(" energy to " + energyMaxAllies + " nearby allies within "))
                               .append(Component.text(energyRadius, NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."))
                               .append(Component.newline())
                               .append(Component.newline())
                               .append(Component.text("MARKED allies get priority in restoring energy and increases their speed by "))
                               .append(Component.text("40%", NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text("1", NamedTextColor.GOLD))
                               .append(Component.text(" second."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));
        info.add(new Pair<>("Energy Given", NumberFormat.addCommaAndRound(energyGivenToPlayers)));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new CrusadersStrikeBranch(abilityTree, this);
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "paladin.paladinstrike.activation", 2, 1);
        randomHitEffect(location, 5, 255, 0, 0);
        EffectUtils.displayParticle(
                Particle.SPELL,
                location.clone().add(0, 1, 0),
                4,
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                1
        );
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        boolean crit = false;
        Optional<WarlordsDamageHealingFinalEvent> finalEvent = nearPlayer.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .value(damageValues.strikeDamage)
        );
        if (finalEvent.isPresent()) {
            crit = finalEvent.get().isCrit();
        }

        if (pveMasterUpgrade) {
            additionalHit(2, wp, nearPlayer, warlordsEntity -> {
                warlordsEntity.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .value(damageValues.strikeDamage)
                );
            });
        } else if (pveMasterUpgrade2) {
            PlayerFilter.entitiesAround(wp, energyRadius, energyRadius, energyRadius)
                        .aliveTeammatesOfExcludingSelf(wp)
                        .limit(2)
                        .forEach(teammate -> {
                            teammate.addSpeedModifier(wp, "Crusading Strike", 10, 40);
                        });
        }

        float previousEnergyGiven = energyGivenToPlayers;
        // Give energy to nearby allies and check if they have mark active
        for (WarlordsEntity energyTarget : PlayerFilter
                .entitiesAround(wp, energyRadius, energyRadius, energyRadius)
                .aliveTeammatesOfExcludingSelf(wp)
                .sorted(Comparator.comparing((WarlordsEntity p) -> p.getCooldownManager().hasCooldown(HolyRadianceCrusader.class) ? 0 : 1)
                                  .thenComparing(LocationUtils.sortClosestBy(WarlordsEntity::getLocation, wp.getLocation()))
                )
                .limit(energyMaxAllies)
        ) {
            if (energyTarget.getCooldownManager().hasCooldown(HolyRadianceCrusader.class)) {
                energyTarget.addSpeedModifier(wp, "CRUSADER MARK", 40, 20, "BASE"); // 20 ticks
            }

            energyGivenToPlayers += energyTarget.addEnergy(wp, name, energyGiven + (pveMasterUpgrade2 && crit ? 5 : 0));
        }

        new CooldownFilter<>(wp, RegularCooldown.class)
                .filterCooldownFrom(wp)
                .filterCooldownClassAndMapToObjectsOfClass(InspiringPresence.class)
                .forEach(inspiringPresence -> inspiringPresence.addEnergyGivenFromStrikeAndPresence(energyGivenToPlayers - previousEnergyGiven));

        return true;
    }

    public int getEnergyGiven() {
        return energyGiven;
    }

    public void setEnergyGiven(int energyGiven) {
        this.energyGiven = energyGiven;
    }

    public int getEnergyRadius() {
        return energyRadius;
    }

    public void setEnergyRadius(int energyRadius) {
        this.energyRadius = energyRadius;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(326, 441, 20, 175);
        private final List<Value> values = List.of(strikeDamage);

        public Value.RangedValueCritable getStrikeDamage() {
            return strikeDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
