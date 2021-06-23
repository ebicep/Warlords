package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class UndyingArmy extends AbstractAbility {

    public static final ItemStack BONE = new ItemBuilder(Material.BONE)
            .name(ChatColor.RED + "Instant Kill")
            .lore("§7Right-click this item to die\n§7instantly instead of waiting for\n§7the decay.")
            .get();

    public UndyingArmy() {
        super("Undying Army", 0, 0, 60 + 10, 20, 0, 0,
                "§7When you or nearby allies take\n" +
                        "§7fatal damage within §610 §7seconds,\n" +
                        "§7instantly restore them to §a100% §7health\n" +
                        "§7instead. They will take §c500 §7TRUE DAMAGE\n" +
                        "§7every second for the rest of their life.\n" +
                        "§7Allies not revived will heal for §a200 §7+\n" +
                        "§a35% §7of their missing health §610 §7seconds\n" +
                        "§7after this abilty was cast.");
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.setUndyingArmyDuration(10);
        warlordsPlayer.setUndyingArmyBy(warlordsPlayer);
        
        Iterator<WarlordsPlayer> iterator = PlayerFilter.entitiesAround(warlordsPlayer, 4, 4, 4)
                .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                .iterator();
        int numberOfPlayersWithArmy = 0;
        while(iterator.hasNext()) {
            WarlordsPlayer warlordsNearPlayer = iterator.next();
            warlordsPlayer.sendMessage("§a\u00BB§7 " + ChatColor.GRAY + "Your Undying Army is protecting " + warlordsNearPlayer.getColoredName()+ ChatColor.GRAY + ".");
            warlordsNearPlayer.setUndyingArmyDuration(10);
            warlordsNearPlayer.setUndyingArmyBy(warlordsPlayer);
            warlordsNearPlayer.sendMessage("§a\u00BB§7 " + ChatColor.GRAY + warlordsPlayer.getName() + "'s Undying Army protects you for " + ChatColor.GOLD + "10 " + ChatColor.GRAY + "seconds.");
            numberOfPlayersWithArmy++;
        }
        String allies = numberOfPlayersWithArmy == 1 ? "ally." : "allies.";
        warlordsPlayer.sendMessage("§a\u00BB§7 " + ChatColor.GRAY + "Your Undying Army is protecting " + ChatColor.YELLOW + numberOfPlayersWithArmy + ChatColor.GRAY + " nearby " + allies);
        
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), Sound.ZOMBIE_IDLE, 1, 1.1f);
        }

        CircleEffect circle = new CircleEffect(warlordsPlayer.getGame(), warlordsPlayer.getTeam(), player.getLocation(), 5);
        circle.addEffect(new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY).particlesPerCircumference(10));
        circle.playEffects();
    }
}
