package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.SkillBoosts;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.DamageHealCompleteCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Comparator;

import static com.ebicep.warlords.util.warlords.Utils.lerp;

public class ProtectorsStrike extends AbstractStrikeBase {

    // Percentage
    private int minConvert = 75;
    private int maxConvert = 100;

    public ProtectorsStrike() {
        super("Protector's Strike", 261, 352, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c261 §7- §c352 §7damage\n" +
                "§7and healing two nearby allies for\n" +
                "§a" + maxConvert + "-" + minConvert + "% §7of the damage done. Also\n" +
                "§7heals yourself by §a50-75% §7of the\n" +
                "§7damage done. Based on your current\n" +
                "health.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
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
                // Protector's Strike
                if (ability.equals("Protector's Strike")) {
                    float healthFraction = lerp(0, 1, (float) wp.getHealth() / wp.getMaxHealth());

                    if (healthFraction > 1) {
                        healthFraction = 1; // in the case of overheal
                    }

                    if (healthFraction < 0) {
                        healthFraction = 0;
                    }

                    float allyHealing = 0.75f + healthFraction * 0.25f;
                    float ownHealing = 0.5f + (1 - healthFraction) * 0.25f;

                    // Self Heal
                    wp.addHealingInstance(wp, ability, currentDamageValue * ownHealing, currentDamageValue * ownHealing, isCrit ? 100 : -1, 100, false, false);

                    // Ally Heal
                    for (WarlordsPlayer ally : PlayerFilter
                            .entitiesAround(wp, 10, 10, 10)
                            .aliveTeammatesOfExcludingSelf(wp)
                            .sorted(Comparator.comparing((WarlordsPlayer p) -> p.getCooldownManager().hasCooldown(HolyRadianceProtector.class) ? 0 : 1)
                            .thenComparing(Utils.sortClosestBy(WarlordsPlayer::getLocation, wp.getLocation())))
                            .limit(2)
                    ) {
                        if (Warlords.getPlayerSettings(wp.getUuid()).getSkillBoostForClass() == SkillBoosts.PROTECTOR_STRIKE) {
                            ally.addHealingInstance(wp, ability, currentDamageValue * allyHealing * 1.2f, currentDamageValue * allyHealing * 1.2f, isCrit ? 100 : -1, 100, false, false);
                        } else {
                            ally.addHealingInstance(wp, ability, currentDamageValue * allyHealing, currentDamageValue * allyHealing, isCrit ? 100 : -1, 100, false, false);
                        }
                    }
                }
            }
        });

        if (standingOnConsecrate(player, nearPlayer)) {
            nearPlayer.addDamageInstance(wp, name, minDamageHeal * 1.15f, maxDamageHeal * 1.15f, critChance, critMultiplier, false);
        } else {
            nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        }
    }

    public void setMinConvert(int convertPercent) {
        this.minConvert = convertPercent;
    }

    public void setMaxConvert(int selfConvertPercent) {
        this.maxConvert = selfConvertPercent;
    }
}