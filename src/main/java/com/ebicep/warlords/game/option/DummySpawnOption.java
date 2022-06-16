package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;

public class DummySpawnOption implements Option {

    private final Location loc;
    private final Team team;

    public DummySpawnOption(Location loc, Team team) {
        this.loc = loc;
        this.team = team;
    }

    @Override
    public void start(@Nonnull Game game) {
        // Delay spawn by 5 seconds to avoid Null reference in PlayingState
        new GameRunnable(game) {
            @Override
            public void run() {
                if (getGame().getState() instanceof EndState) {
                    System.out.print(ChatColor.RED + "[DEBUG] CAUGHT INVALID DUMMY SPAWN - game was ended before initial spawn.");
                    return;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(team == Team.RED ? "TestDummy1" : "TestDummy2");
                WarlordsPlayer testDummy = new WarlordsPlayer(
                        offlinePlayer,
                        (PlayingState) game.getState(),
                        team,
                        new PlayerSettings()
                );

                Warlords.addPlayer(testDummy);
                game.addPlayer(offlinePlayer, false);
                //SKULL
                ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
                skullMeta.setOwner(offlinePlayer.getName());
                playerSkull.setItemMeta(skullMeta);
                Warlords.getPlayerHeads().put(offlinePlayer.getUniqueId(), CraftItemStack.asNMSCopy(playerSkull));

                testDummy.teleport(loc);
                testDummy.setTakeDamage(true);
                testDummy.setMaxHealth(1000000);
                testDummy.setHealth(1000000);
                testDummy.updateJimmyHealth();
            }
        }.runTaskLater(100);
    }
}
