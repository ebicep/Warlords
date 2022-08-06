package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.DamageHealCompleteCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.ebicep.warlords.util.warlords.Utils.lerp;

public class ProtectorsStrike extends AbstractStrikeBase {
    private boolean pveUpgrade = false;
    // Percentage
    private int minConvert = 75;
    private int maxConvert = 100;
    private int maxAllies = 2;

    public ProtectorsStrike() {
        super("Protector's Strike", 261, 352, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + minDamageHeal + " §7- §c" + maxDamageHeal + " §7damage\n" +
                "§7and healing two nearby allies for\n" +
                "§a" + maxConvert + "-" + minConvert + "% §7of the damage done. Also\n" +
                "§7heals yourself by §a50-75% §7of the\n" +
                "§7damage done. Based on your current\n" +
                "health.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Struck", "" + timesUsed));

        return info;
    }

    @Override
    protected void onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        wp.getCooldownManager().addCooldown(new DamageHealCompleteCooldown<ProtectorsStrike>(
                "Protectors Strike",
                "",
                ProtectorsStrike.class,
                new ProtectorsStrike(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                }
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                String ability = event.getAbility();
                if (ability.equals("Protector's Strike")) {
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
                            ability,
                            currentDamageValue * ownHealing,
                            currentDamageValue * ownHealing,
                            isCrit ? 100 : -1,
                            100,
                            false,
                            false
                    );
                    // Ally Heal
                    if (pveUpgrade) {
                        for (WarlordsEntity ally : PlayerFilter
                                .entitiesAround(wp, 10, 10, 10)
                                .aliveTeammatesOfExcludingSelf(wp)
                                .limit(maxAllies)
                                .leastAliveFirst()
                        ) {
                            boolean isLeastAlive = ally.getHealth() < ally.getHealth();
                            float healing = (currentDamageValue * allyHealing) * (isLeastAlive ? 1.7f : 1);
                            ally.addHealingInstance(
                                    wp,
                                    ability,
                                    healing,
                                    healing,
                                    isCrit ? 100 : -1,
                                    100,
                                    false,
                                    false
                            );
                        }
                    } else {
                        for (WarlordsEntity ally : PlayerFilter
                                .entitiesAround(wp, 10, 10, 10)
                                .aliveTeammatesOfExcludingSelf(wp)
                                .sorted(Comparator.comparing((WarlordsEntity p) -> p.getCooldownManager().hasCooldown(HolyRadianceProtector.class) ? 0 : 1)
                                        .thenComparing(Utils.sortClosestBy(WarlordsEntity::getLocation, wp.getLocation())))
                                .limit(maxAllies)
                        ) {
                            if (Warlords.getPlayerSettings(wp.getUuid()).getSkillBoostForClass() == SkillBoosts.PROTECTOR_STRIKE) {
                                ally.addHealingInstance(
                                        wp,
                                        ability,
                                        currentDamageValue * allyHealing * 1.2f,
                                        currentDamageValue * allyHealing * 1.2f,
                                        isCrit ? 100 : -1,
                                        100,
                                        false,
                                        false
                                );
                            } else {
                                ally.addHealingInstance(
                                        wp,
                                        ability,
                                        currentDamageValue * allyHealing,
                                        currentDamageValue * allyHealing,
                                        isCrit ? 100 : -1,
                                        100,
                                        false,
                                        false
                                );
                            }
                        }
                    }
                }
            }
        });

        Optional<Consecrate> optionalConsecrate = getStandingOnConsecrate(wp, nearPlayer);
        if (optionalConsecrate.isPresent()) {
            wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
            nearPlayer.addDamageInstance(
                    wp,
                    name,
                    minDamageHeal * (1 + optionalConsecrate.get().getStrikeDamageBoost() / 100f),
                    maxDamageHeal * (1 + optionalConsecrate.get().getStrikeDamageBoost() / 100f),
                    critChance,
                    critMultiplier,
                    false
            );
        } else {
            nearPlayer.addDamageInstance(
                    wp,
                    name,
                    minDamageHeal,
                    maxDamageHeal,
                    critChance,
                    critMultiplier,
                    false
            );
        }
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

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public int getMaxAllies() {
        return maxAllies;
    }

    public void setMaxAllies(int maxAllies) {
        this.maxAllies = maxAllies;
    }
}