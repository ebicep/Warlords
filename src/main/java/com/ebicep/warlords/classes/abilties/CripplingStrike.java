package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CripplingStrike extends AbstractStrikeBase {

    public CripplingStrike() {
        super("Crippling Strike", -362.25f, -498, 0, 100, 15, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                "§7and §ccrippling §7them for §63 §7seconds.\n" +
                "§7A §ccrippled §7player deals §c12.5% §7less\n" +
                "§7damage for the duration of the effect.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        nearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
        nearPlayer.getCooldownManager().addCooldown(this.getClass(), new CripplingStrike(), "CRIP", 3, wp, CooldownTypes.DEBUFF);
        nearPlayer.sendMessage(ChatColor.GRAY + "You are " + ChatColor.RED + "crippled" + ChatColor.GRAY + ".");
    }
}