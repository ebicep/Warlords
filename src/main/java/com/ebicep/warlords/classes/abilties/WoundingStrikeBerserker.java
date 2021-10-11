package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class WoundingStrikeBerserker extends AbstractStrikeBase {

    public WoundingStrikeBerserker() {
        super("Wounding Strike", -497, -632, 0, 100, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + format(-minDamageHeal) + " §7- §c" + format(-maxDamageHeal) + " §7damage\n" +
                "§7and §cwounding §7them for §63 §7seconds.\n" +
                "§7A wounded player receives §c40% §7less\n" +
                "§7healing for the duration of the effect.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        nearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        if (!(nearPlayer.getCooldownManager().hasCooldown(WoundingStrikeBerserker.class) || nearPlayer.getCooldownManager().hasCooldown(WoundingStrikeDefender.class))) {
            nearPlayer.sendMessage(ChatColor.GRAY + "You are " + ChatColor.RED + "wounded" + ChatColor.GRAY + ".");
        }
        nearPlayer.getCooldownManager().removeCooldown(WoundingStrikeBerserker.class);
        nearPlayer.getCooldownManager().removeCooldown(WoundingStrikeDefender.class);
        nearPlayer.getCooldownManager().addCooldown(name, this.getClass(), new WoundingStrikeBerserker(), "WND", 3, wp, CooldownTypes.DEBUFF);
    }
}