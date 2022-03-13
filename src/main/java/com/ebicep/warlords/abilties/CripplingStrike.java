package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Optional;

public class CripplingStrike extends AbstractStrikeBase {

    private int consecutiveStrikeCounter = 0;

    public CripplingStrike() {
        super("Crippling Strike", 362.25f, 498, 0, 100, 15, 200);
    }

    public CripplingStrike(int consecutiveStrikeCounter) {
        super("Crippling Strike", 362.25f, 498, 0, 100, 15, 200);
        this.consecutiveStrikeCounter = consecutiveStrikeCounter;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7and §ccrippling §7them for §63 §7seconds.\n" +
                "§7A §ccrippled §7player deals §c10% §7less\n" +
                "§7damage for the duration of the effect.\n" +
                "§7Adds §c5% §7less damage dealt per\n" +
                "§7additional strike. (max 20%)";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        Optional<CripplingStrike> optionalCripplingStrike = new CooldownFilter<>(nearPlayer, RegularCooldown.class).filterCooldownClassAndMapToObjectsOfClass(CripplingStrike.class).findAny();
        if (optionalCripplingStrike.isPresent()) {
            CripplingStrike cripplingStrike = optionalCripplingStrike.get();
            nearPlayer.getCooldownManager().removeCooldown(CripplingStrike.class);
            nearPlayer.getCooldownManager().addCooldown(new RegularCooldown<CripplingStrike>(
                    name,
                    "CRIP",
                    CripplingStrike.class,
                    new CripplingStrike(Math.min(cripplingStrike.getConsecutiveStrikeCounter() + 1, 2)),
                    wp,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {
                        if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("CRIP").stream().count() == 1) {
                            wp.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "crippled" + ChatColor.GRAY + ".");
                        }
                    },
                    3 * 20
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return (float) (currentDamageValue * (.9 - Math.min(cripplingStrike.getConsecutiveStrikeCounter() + 1, 2) * .05));
                }
            });
        } else {
            nearPlayer.sendMessage(ChatColor.GRAY + "You are " + ChatColor.RED + "crippled" + ChatColor.GRAY + ".");
            nearPlayer.getCooldownManager().addCooldown(new RegularCooldown<CripplingStrike>(
                    name, "CRIP",
                    CripplingStrike.class,
                    new CripplingStrike(),
                    wp,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {
                        if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("CRIP").stream().count() == 1) {
                            wp.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "crippled" + ChatColor.GRAY + ".");
                        }
                    }, 3 * 20
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * .9f;
                }
            });
        }
    }

    public int getConsecutiveStrikeCounter() {
        return consecutiveStrikeCounter;
    }
}