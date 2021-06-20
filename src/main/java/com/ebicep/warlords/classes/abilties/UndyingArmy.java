package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UndyingArmy extends AbstractAbility {

    public static final ItemStack BONE = new ItemBuilder(Material.BONE)
            .name(ChatColor.RED + "Instant Kill")
            .lore("§7Right-click this item to die\n§7instantly instead of waiting for\n§7the decay.")
            .get();

    public UndyingArmy() {
        super("Undying Army", 0, 0, 60f + 10.47f, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7When you or nearby allies take\n" +
                "§7fatal damage within §610 §7seconds,\n" +
                "§7instantly restore them to §a100% §7health\n" +
                "§7instead. They will take §c500 §7TRUE DAMAGE\n" +
                "§7every second for the rest of their life.\n" +
                "§7Allies not revived will heal for §a200 §7+\n" +
                "§a35% §7of their missing health §610 §7seconds\n" +
                "§7after this abilty was cast.";
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setUndyingArmyDuration(10);
        warlordsPlayer.setUndyingArmyBy(warlordsPlayer);
        List<Entity> near = player.getNearbyEntities(4.0D, 4.0D, 4.0D);
        near = Utils.filterOnlyTeammates(near, player);
        int numberOfPlayersWithArmy = 0;
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                    if (Warlords.game.isBlueTeam(player)) {
                        warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + ChatColor.GRAY + "Your Undying Army is protecting " + ChatColor.BLUE + nearPlayer.getName() + ChatColor.GRAY + ".");
                    } else if (Warlords.game.isRedTeam(player)) {
                        warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + ChatColor.GRAY + "Your Undying Army is protecting " + ChatColor.RED + nearPlayer.getName() + ChatColor.GRAY + ".");
                    }
                    WarlordsPlayer warlordsNearPlayer = Warlords.getPlayer(nearPlayer);
                    warlordsNearPlayer.setUndyingArmyDuration(10);
                    warlordsNearPlayer.setUndyingArmyBy(warlordsPlayer);
                    warlordsNearPlayer.getPlayer().sendMessage("§a\u00BB§7 " + ChatColor.GRAY + warlordsPlayer.getName() + "'s Undying Army protects you for " + ChatColor.GOLD + "10 " + ChatColor.GRAY + "seconds.");
                    numberOfPlayersWithArmy++;
                }
            }
        }
        if (numberOfPlayersWithArmy == 1) {
            warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + ChatColor.GRAY + "Your Undying Army is protecting " + ChatColor.YELLOW + numberOfPlayersWithArmy + ChatColor.GRAY + " nearby ally.");
        } else {
            warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + ChatColor.GRAY + "Your Undying Army is protecting " + ChatColor.YELLOW + numberOfPlayersWithArmy + ChatColor.GRAY + " nearby allies.");
        }

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), Sound.ZOMBIE_IDLE, 1, 1.1f);
        }

        CircleEffect circle = new CircleEffect(Warlords.game, Warlords.game.getPlayerTeam(player), player.getLocation(), 5);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY).particlesPerCircumference(1));
        circle.playEffects();
    }
}
