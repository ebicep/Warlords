package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractStrike;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.protector.ProtectorStrikeBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
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

public class ProtectorsStrike extends AbstractStrike {

    private int allyHealing = 90; // %
    private int selfHealing = 60; // %
    private int maxAllies = 2;
    private double strikeRadius = 10;

    public ProtectorsStrike() {
        super("Protector's Strike", 261, 352, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Strike the targeted enemy player, causing ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage and healing "))
                               .append(Component.text(maxAllies, NamedTextColor.GREEN))
                               .append(Component.text(" nearby allies for "))
                               .append(Component.text(allyHealing + "%", NamedTextColor.GREEN))
                               .append(Component.text(" of the damage done. Also heals yourself by "))
                               .append(Component.text(selfHealing + "%", NamedTextColor.GREEN))
                               .append(Component.text(" of the damage done. Based on your current health."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Struck", "" + timesUsed));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ProtectorStrikeBranch(abilityTree, this);
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "paladin.paladinstrike.activation", 2, 1);
        randomHitEffect(location, 5, 255, 0, 0);
        location.getWorld().spawnParticle(
                Particle.SPELL,
                location.clone().add(0, 1, 0),
                4,
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                1,
                null,
                true
        );
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        nearPlayer.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .value(damageValues.strikeDamage)
        ).ifPresent(warlordsDamageHealingFinalEvent -> {
            if (warlordsDamageHealingFinalEvent.getFinalEventFlag() != WarlordsDamageHealingFinalEvent.FinalEventFlag.REGULAR) {
                return;
            }
            float currentDamageValue = warlordsDamageHealingFinalEvent.getValue();
            boolean isCrit = warlordsDamageHealingFinalEvent.isCrit();

            float allyHealingMultiplier = allyHealing / 100f;
            float selfHealingMultiplier = selfHealing / 100f;

            // Self Heal
            wp.addInstance(InstanceBuilder
                    .healing()
                    .ability(this)
                    .source(wp)
                    .value(currentDamageValue * selfHealingMultiplier)
                    .showAsCrit(isCrit)
                    .flags(InstanceFlags.IGNORE_CRIT_MODIFIERS)
            ).ifPresent(event -> {
                new CooldownFilter<>(wp, RegularCooldown.class)
                        .filterCooldownFrom(wp)
                        .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                        .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(event.getValue()));
            });
            // Ally Heal
            if (pveMasterUpgrade) {
                for (WarlordsEntity ally : PlayerFilter
                        .entitiesAround(wp, strikeRadius, strikeRadius, strikeRadius)
                        .aliveTeammatesOfExcludingSelf(wp)
                        .limit(maxAllies)
                        .leastAliveFirst()
                ) {
                    boolean isLeastAlive = ally.getCurrentHealth() < ally.getMaxHealth();
                    float healing = currentDamageValue * (allyHealingMultiplier + (isLeastAlive ? .5f : 0));
                    ally.addInstance(InstanceBuilder
                            .healing()
                            .ability(this)
                            .source(wp)
                            .value(healing)
                            .showAsCrit(isCrit)
                            .flags(InstanceFlags.IGNORE_CRIT_MODIFIERS)
                    ).ifPresent(event -> {
                        new CooldownFilter<>(wp, RegularCooldown.class)
                                .filterCooldownFrom(wp)
                                .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                                .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(event.getValue()));
                    });
                }
            } else {
                for (WarlordsEntity ally : PlayerFilter
                        .entitiesAround(wp, strikeRadius, strikeRadius, strikeRadius)
                        .aliveTeammatesOfExcludingSelf(wp)
                        .sorted(Comparator.comparing((WarlordsEntity p) -> p.getCooldownManager().hasCooldown(HolyRadianceProtector.class) ? 0 : 1)
                                          .thenComparing(LocationUtils.sortClosestBy(WarlordsEntity::getLocation, wp.getLocation())))
                        .limit(maxAllies)
                ) {
                    ally.addInstance(InstanceBuilder
                            .healing()
                            .ability(this)
                            .source(wp)
                            .value(currentDamageValue * allyHealingMultiplier)
                            .showAsCrit(isCrit)
                            .flags(InstanceFlags.IGNORE_CRIT_MODIFIERS)
                    ).ifPresent(event -> {
                        new CooldownFilter<>(wp, RegularCooldown.class)
                                .filterCooldownFrom(wp)
                                .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                                .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(event.getValue()));
                    });
                }
            }
        });
        return true;
    }

    public int getAllyHealing() {
        return allyHealing;
    }

    public void setAllyHealing(int convertPercent) {
        this.allyHealing = convertPercent;
    }

    public int getMaxAllies() {
        return maxAllies;
    }

    public void setMaxAllies(int maxAllies) {
        this.maxAllies = maxAllies;
    }

    public double getStrikeRadius() {
        return strikeRadius;
    }

    public void setStrikeRadius(double strikeRadius) {
        this.strikeRadius = strikeRadius;
    }

    private final DamageValues damageValues = new DamageValues();

    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(261, 352, 20, 175);
        private final List<Value> values = List.of(strikeDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}