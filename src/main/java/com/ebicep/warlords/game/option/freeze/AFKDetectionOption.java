package com.ebicep.warlords.game.option.freeze;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import java.util.Map;
import java.util.UUID;

public class AFKDetectionOption implements Option {

    public static boolean enabled = true;
    private static final int COUNTER_CHECK = 5;

    private final Map<UUID, List<Location>> playerLocations = new HashMap<>();

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(new Listener() {

            @EventHandler
            public void onPlayerInteract(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                WarlordsEntity warlordsPlayer = Warlords.getPlayer(player);
                if (warlordsPlayer instanceof WarlordsPlayer) {
                    if (warlordsPlayer.getGame().equals(game)) {
                        //clearing player location list for clicking while standing still
                        playerLocations.computeIfAbsent(warlordsPlayer.getUuid(), k -> new ArrayList<>()).clear();
                    }
                }
            }

            @EventHandler
            public void onPlayerSneak(PlayerToggleSneakEvent event) {
                Player player = event.getPlayer();
                WarlordsEntity warlordsPlayer = Warlords.getPlayer(player);
                if (warlordsPlayer instanceof WarlordsPlayer) {
                    if (warlordsPlayer.getGame().equals(game)) {
                        //clearing player location list for sneaking while standing still
                        playerLocations.computeIfAbsent(warlordsPlayer.getUuid(), k -> new ArrayList<>()).clear();
                    }
                }
            }

        });
    }

    @Override
    public void start(@Nonnull Game game) {
        if (game.getPlayers().size() < 14 || game.getAddons().contains(GameAddon.CUSTOM_GAME) || GameMode.isPvE(game.getGameMode())) {
            return;
        }
        new GameRunnable(game) {

            boolean wasFrozen = false;

            @Override
            public void run() {
                if (!enabled) {
                    return;
                }

                //skips right after unfreeze
                if (wasFrozen) {
                    wasFrozen = false;
                    return;
                }

                game.getState(PlayingState.class).ifPresent(state -> {
                    for (WarlordsPlayer we : PlayerFilterGeneric.playingGame(game).warlordsPlayers()) {
                        if (we.isDead()) {
                            continue;
                        }
                        if (!(we.getEntity() instanceof Player)) {
                            continue;
                        }
                        if (we.isSneaking()) {
                            continue; //make sure no ppl that are sneaking are marked as AFK
                        }
                        UUID uuid = we.getUuid();
                        playerLocations.computeIfAbsent(uuid, k -> new ArrayList<>()).add(we.getLocation());
                        List<Location> locations = playerLocations.get(uuid);
                        List<Location> lastLocations = locations.subList(Math.max(locations.size() - COUNTER_CHECK, 0), locations.size());
                        int counter = 0;
                        for (int i = lastLocations.size() - 1; i >= 1; i--) {
                            if (lastLocations.get(i).equals(lastLocations.get(i - 1))) {
                                counter++;
                            } else {
                                break;
                            }
                        }
                        if (counter == 0) {
                            continue;
                        }
                        String message = " is AFK. (Hasn't moved for " + NumberFormat.formatOptionalTenths(counter * 2.5) + " seconds)";
                        for (WarlordsEntity wp : PlayerFilter.playingGame(game)) {
                            Permissions.sendMessageToDebug(wp, Component.text("----------------------------------------", NamedTextColor.RED));
                            Permissions.sendMessageToDebug(wp, Component.text(we.getName(), NamedTextColor.AQUA).append(Component.text(message, NamedTextColor.RED)));
                            Permissions.sendMessageToDebug(wp, Component.text("----------------------------------------", NamedTextColor.RED));
                        }
                        if (counter == COUNTER_CHECK - 1) {
                            game.getOption(GameFreezeOption.class).forEach(freezeOption -> {
                                freezeOption.addFrozenCause(Component.text(we.getName(), NamedTextColor.AQUA)
                                                                     .append(Component.text(" has been detected as AFK.", NamedTextColor.RED)));
                                wasFrozen = true;
                                Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(we.getUuid());
                                if (partyPlayerPair != null) {
                                    partyPlayerPair.getA().afk(we.getUuid());
                                }
                            });
                        }
                    }
                });
            }
        }.runTaskTimer(20 * 15 + 5, 50); //5 seconds after gates fall - every 2.5 seconds
    }

    @Override
    public void onPlayerQuit(Player player) {
        playerLocations.remove(player.getUniqueId());
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        playerLocations.clear();
    }

}
