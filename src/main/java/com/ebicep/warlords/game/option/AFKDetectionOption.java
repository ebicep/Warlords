package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.permissions.PermissionHandler;
import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AFKDetectionOption implements Option, Listener {

    public static boolean enabled = true;

    private final HashMap<WarlordsEntity, List<Location>> playerLocations = new HashMap<>();
    private boolean canFreeze = false;

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
        this.canFreeze = Utils.collectionHasItem(game.getOptions(), o -> o instanceof GameFreezeOption);
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {

            boolean wasFrozen = false;

            @Override
            public void run() {
                if (!enabled) return;
                if (game.getPlayers().size() < 14 || game.getAddons().contains(GameAddon.CUSTOM_GAME)) return;

                //skips right after unfreeze
                if (wasFrozen) {
                    wasFrozen = false;
                    return;
                }

                game.getState(PlayingState.class).ifPresent(state -> {
                    for (WarlordsEntity warlordsPlayer : PlayerFilter.playingGame(game)) {
                        if (warlordsPlayer.isDead()) continue;
                        if (warlordsPlayer.getName().equalsIgnoreCase("TestDummy")) continue;
                        if (warlordsPlayer.isSneaking())
                            continue; //make sure no ppl that are sneaking are marked as AFK

                        playerLocations.computeIfAbsent(warlordsPlayer, k -> new ArrayList<>()).add(warlordsPlayer.getLocation());
                        List<Location> locations = playerLocations.get(warlordsPlayer);
                        if (locations.size() >= 2) {
                            Location lastLocation = locations.get(locations.size() - 1);
                            Location secondLastLocation = locations.get(locations.size() - 2);
                            if (locations.size() >= 3) {
                                Location thirdLastLocation = locations.get(locations.size() - 3);
                                if (locations.size() >= 4) {
                                    Location fourthLastLocation = locations.get(locations.size() - 4);
                                    if (lastLocation.equals(secondLastLocation) && lastLocation.equals(thirdLastLocation) && lastLocation.equals(fourthLastLocation)) {
                                        //hasnt moved for 10 seconds
                                        for (WarlordsEntity wp : PlayerFilter.playingGame(game)) {
                                            PermissionHandler.sendMessageToDebug(wp, ChatColor.RED + "----------------------------------------");
                                            PermissionHandler.sendMessageToDebug(wp, ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.RED + " is AFK. (Hasn't moved for 10 seconds)");
                                            PermissionHandler.sendMessageToDebug(wp, ChatColor.RED + "----------------------------------------");
                                        }
                                        if (canFreeze) {
                                            warlordsPlayer.getGame().addFrozenCause(ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.RED + " has been detected as AFK.");
                                            wasFrozen = true;
                                        }
                                        continue;
                                    }
                                }
                                if (secondLastLocation.equals(thirdLastLocation)) {
                                    //hasnt moved for 7.5 seconds
                                    for (WarlordsEntity wp : PlayerFilter.playingGame(game)) {
                                        PermissionHandler.sendMessageToDebug(wp, ChatColor.RED + "----------------------------------------");
                                        PermissionHandler.sendMessageToDebug(wp, ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.RED + " is possibly AFK. (Hasn't moved for 7.5 seconds)");
                                        PermissionHandler.sendMessageToDebug(wp, ChatColor.RED + "----------------------------------------");
                                    }
                                }
                                continue;
                            }
                            if (lastLocation.equals(secondLastLocation)) {
                                //hasnt moved for 5 seconds
                                for (WarlordsEntity wp : PlayerFilter.playingGame(game)) {
                                    PermissionHandler.sendMessageToDebug(wp, ChatColor.RED + "----------------------------------------");
                                    PermissionHandler.sendMessageToDebug(wp, ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.RED + " is possibly AFK. (Hasn't moved for 5 seconds)");
                                    PermissionHandler.sendMessageToDebug(wp, ChatColor.RED + "----------------------------------------");
                                }
                            }
                        }
                    }
                });
            }
        }.runTaskTimer(20 * 15, 50); //5 seconds after gates fall - every 2.5 seconds
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        WarlordsEntity warlordsPlayer = Warlords.getPlayer(player);
        if (warlordsPlayer != null) {
            //clearing player location list for clicking while standing still
            playerLocations.computeIfAbsent(warlordsPlayer, k -> new ArrayList<>()).clear();
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        WarlordsEntity warlordsPlayer = Warlords.getPlayer(player);
        if (warlordsPlayer != null) {
            //clearing player location list for sneaking while standing still
            playerLocations.computeIfAbsent(warlordsPlayer, k -> new ArrayList<>()).clear();
        }
    }
}
