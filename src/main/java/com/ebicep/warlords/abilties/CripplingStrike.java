package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CripplingStrike extends AbstractStrikeBase {
    private boolean pveUpgrade = false;

    private int consecutiveStrikeCounter = 0;

    private final int crippleDuration = 3;
    private int cripple = 10;
    private int cripplePerStrike = 5;

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
                "§7and §ccrippling §7them for §6" + crippleDuration + " §7seconds.\n" +
                "§7A §ccrippled §7player deals §c" + cripple + "% §7less\n" +
                "§7damage for the duration of the effect.\n" +
                "§7Adds §c" + cripplePerStrike  +"% §7less damage dealt per\n" +
                "§7additional strike. (max " + (cripple + (cripplePerStrike * 2)) + "%)";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));

        return info;
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        Optional<WarlordsDamageHealingFinalEvent> finalEvent = nearPlayer.addDamageInstance(
                wp,
                name,
                minDamageHeal,
                maxDamageHeal,
                critChance,
                critMultiplier,
                false
        );

        Optional<CripplingStrike> optionalCripplingStrike = new CooldownFilter<>(
                nearPlayer,
                RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(CripplingStrike.class)
                .findAny();

        if (pveUpgrade) {
            tripleHit(wp, nearPlayer);
        }

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
                            nearPlayer.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "crippled" + ChatColor.GRAY + ".");
                        }
                    },
                    crippleDuration * 20
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * (((100 - cripple) / 100f) - Math.min(cripplingStrike.getConsecutiveStrikeCounter() + 1, 2) * (cripplePerStrike / 100f));
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
                            nearPlayer.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "crippled" + ChatColor.GRAY + ".");
                        }
                    }, crippleDuration * 20
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * (100 - cripple) / 100f;
                }
            });
        }

        return true;
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "warrior.mortalstrike.impact", 2, 1);
        randomHitEffect(location, 7, 255, 0, 0);
    }

    public int getConsecutiveStrikeCounter() {
        return consecutiveStrikeCounter;
    }

    public int getCripple() {
        return cripple;
    }

    public void setCripple(int cripple) {
        this.cripple = cripple;
    }

    public int getCripplePerStrike() {
        return cripplePerStrike;
    }

    public void setCripplePerStrike(int cripplePerStrike) {
        this.cripplePerStrike = cripplePerStrike;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}