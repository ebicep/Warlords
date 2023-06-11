package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrike;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
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
import java.util.concurrent.atomic.AtomicReference;

import static com.ebicep.warlords.util.bukkit.LocationUtils.lerp;

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
                               .append(Component.text(" damage and healing two nearby allies for "))
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
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        AtomicReference<Float> minDamage = new AtomicReference<>(minDamageHeal);
        AtomicReference<Float> maxDamage = new AtomicReference<>(maxDamageHeal);
        getStandingOnConsecrate(wp, nearPlayer).ifPresent(consecrate -> {
            wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
            minDamage.getAndUpdate(value -> value * (1 + consecrate.getStrikeDamageBoost() / 100f));
            maxDamage.getAndUpdate(value -> value * (1 + consecrate.getStrikeDamageBoost() / 100f));
        });
        nearPlayer.addDamageInstance(
                wp,
                name,
                minDamage.get() * (wp.isInPve() ? 1.25f : 1),
                maxDamage.get() * (wp.isInPve() ? 1.25f : 1),
                critChance,
                critMultiplier,
                false
        ).ifPresent(warlordsDamageHealingFinalEvent -> {
            float currentDamageValue = warlordsDamageHealingFinalEvent.getValue();
            boolean isCrit = warlordsDamageHealingFinalEvent.isCrit();

            float healthFraction = lerp(0, 1, wp.getHealth() / wp.getMaxHealth());

            if (healthFraction > 1) {
                healthFraction = 1; // in the case of overheal
            }

            if (healthFraction < 0) {
                healthFraction = 0;
            }

            float allyHealing = (minConvert / 100f) + healthFraction * 0.25f;
            float ownHealing = ((maxConvert / 100f) / 2f) + (1 - healthFraction) * 0.25f;
            // Self Heal
            wp.addHealingInstance(
                    wp,
                    name,
                    currentDamageValue * ownHealing,
                    currentDamageValue * ownHealing,
                    isCrit ? 100 : 0,
                    100,
                    false,
                    false
            ).ifPresent(event -> {
                new CooldownFilter<>(wp, RegularCooldown.class)
                        .filterCooldownFrom(wp)
                        .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                        .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(event.getValue()));
            });
            // Ally Heal
            if (pveUpgrade) {
                for (WarlordsEntity ally : PlayerFilter
                        .entitiesAround(wp, strikeRadius, strikeRadius, strikeRadius)
                        .aliveTeammatesOfExcludingSelf(wp)
                        .limit(maxAllies)
                        .leastAliveFirst()
                ) {
                    boolean isLeastAlive = ally.getHealth() < ally.getMaxHealth();
                    float healing = (currentDamageValue * allyHealing) * (isLeastAlive ? 1.5f : 1);
                    ally.addHealingInstance(
                            wp,
                            name,
                            healing,
                            healing,
                            isCrit ? 100 : 0,
                            100,
                            false,
                            false
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
                    if (PlayerSettings.getPlayerSettings(wp.getUuid()).getSkillBoostForClass() == SkillBoosts.PROTECTOR_STRIKE) {
                        ally.addHealingInstance(
                                wp,
                                name,
                                currentDamageValue * allyHealing * 1.2f,
                                currentDamageValue * allyHealing * 1.2f,
                                isCrit ? 100 : 0,
                                100,
                                false,
                                false
                        ).ifPresent(event -> {
                            new CooldownFilter<>(wp, RegularCooldown.class)
                                    .filterCooldownFrom(wp)
                                    .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                                    .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(event.getValue()));
                        });
                    } else {
                        ally.addHealingInstance(
                                wp,
                                name,
                                currentDamageValue * allyHealing,
                                currentDamageValue * allyHealing,
                                isCrit ? 100 : 0,
                                100,
                                false,
                                false
                        ).ifPresent(event -> {
                            new CooldownFilter<>(wp, RegularCooldown.class)
                                    .filterCooldownFrom(wp)
                                    .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                                    .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(event.getValue()));
                        });
                    }
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