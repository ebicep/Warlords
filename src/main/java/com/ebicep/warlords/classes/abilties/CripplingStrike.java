package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.Cooldown;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

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
        List<Cooldown> cripplingStrikes = nearPlayer.getCooldownManager().getCooldown(CripplingStrike.class);
        if (cripplingStrikes.isEmpty()) {
            nearPlayer.sendMessage(ChatColor.GRAY + "You are " + ChatColor.RED + "crippled" + ChatColor.GRAY + ".");
            nearPlayer.getCooldownManager().addCooldown(name, this.getClass(), new CripplingStrike(), "CRIP", 3, wp, CooldownTypes.DEBUFF);
        } else {
            CripplingStrike cripplingStrike = (CripplingStrike) cripplingStrikes.get(0).getCooldownObject();
            nearPlayer.getCooldownManager().removeCooldown(CripplingStrike.class);
            nearPlayer.getCooldownManager().addCooldown(name, this.getClass(), new CripplingStrike(Math.min(cripplingStrike.getConsecutiveStrikeCounter() + 1, 2)), "CRIP", 3, wp, CooldownTypes.DEBUFF);
        }
    }

    public int getConsecutiveStrikeCounter() {
        return consecutiveStrikeCounter;
    }
}