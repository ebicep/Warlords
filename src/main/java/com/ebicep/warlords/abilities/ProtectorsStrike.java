package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
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

import static com.ebicep.warlords.util.java.MathUtils.lerp;

public class ProtectorsStrike extends AbstractStrike {

    private int minConvert = 75; // %
    private int maxConvert = 100; // %
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
                               .append(Component.text(minConvert + "-" + maxConvert + "%", NamedTextColor.GREEN))
                               .append(Component.text(" of the damage done. Also heals yourself by "))
                               .append(Component.text("50-75%", NamedTextColor.GREEN))
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
        nearPlayer.addDamageInstance(
                wp,
                name,
                minDamageHeal,
                maxDamageHeal,
                critChance,
                critMultiplier
        ).ifPresent(warlordsDamageHealingFinalEvent -> {
            if (warlordsDamageHealingFinalEvent.getFinalEventFlag() != WarlordsDamageHealingFinalEvent.FinalEventFlag.REGULAR) {
                return;
            }
            float currentDamageValue = warlordsDamageHealingFinalEvent.getValue();
            boolean isCrit = warlordsDamageHealingFinalEvent.isCrit();

            float healthFraction = lerp(0, 1, wp.getCurrentHealth() / wp.getMaxHealth());

            if (healthFraction > 1) {
                healthFraction = 1; // in the case of overheal
            }

            if (healthFraction < 0) {
                healthFraction = 0;
            }

            float allyHealing = (minConvert / 100f) + healthFraction * 0.25f;
            float ownHealing = ((maxConvert / 100f) / 2f) + (1 - healthFraction) * 0.25f;

            if (pveMasterUpgrade2 && isCrit) {
                allyHealing = .8f;
                ownHealing = .8f;
            }
            // Self Heal
            wp.addHealingInstance(
                    wp,
                    name,
                    currentDamageValue * ownHealing,
                    currentDamageValue * ownHealing,
                    isCrit ? 100 : 0,
                    100
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
                    float healing = (currentDamageValue * allyHealing) * (isLeastAlive ? 1.5f : 1);
                    ally.addHealingInstance(
                            wp,
                            name,
                            healing,
                            healing,
                            isCrit ? 100 : 0,
                            100
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
                    ally.addHealingInstance(
                            wp,
                            name,
                            currentDamageValue * allyHealing,
                            currentDamageValue * allyHealing,
                            isCrit ? 100 : 0,
                            100
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

    public int getMinConvert() {
        return minConvert;
    }

    public void setMinConvert(int convertPercent) {
        this.minConvert = convertPercent;
    }

    public int getMaxConvert() {
        return maxConvert;
    }

    public void setMaxConvert(int selfConvertPercent) {
        this.maxConvert = selfConvertPercent;
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
}