package com.ebicep.warlords.pve;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ebicep.warlords.menu.Menu.*;

public class DifficultyMenu {

    // WIP

    public static void openPveMenu(Player player) {
        Menu menu = new Menu("Pve Menu", 9 * 4);
        menu.setItem(
                3,
                1,
                new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.GREEN + "Start a private PvE game").get(),
                (m, e) -> openDifficultyMenu(player)
        );
        menu.setItem(
                5,
                1,
                new ItemBuilder(Material.REDSTONE_COMPARATOR).name(ChatColor.GREEN + "Join a public PvE game").get(),
                (m, e) -> player.sendMessage(ChatColor.RED + "Public PvE is currently unavailable.")
        );
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openDifficultyMenu(Player player) {
        Menu menu = new Menu("Difficulty Menu", 9 * 4);
        DifficultyIndex[] index = DifficultyIndex.values();
        for (int i = 0; i < index.length; i++) {
            DifficultyIndex difficulty = index[i];
            int finalI = i;
            menu.setItem(
                    9 / 2 - index.length / 2 + i * 2,
                    1,
                    new ItemBuilder(Material.REDSTONE_LAMP_OFF)
                            .name(difficulty.getDifficultyColor() + ChatColor.BOLD.toString() + difficulty.getName())
                            .lore(ChatColor.GRAY + difficulty.getDescription())
                            .get(),
                    (m, e) -> {
                        switch (finalI) {
                            case 0:
                                startNormalGame(player, false);
                                break;
                            case 1:
                                startNormalGame(player, true);
                                break;
                        }
                    });
            menu.setItem(4, 3, MENU_BACK, (m, e) -> openPveMenu(player));
            menu.setItem(3, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        }
        menu.openForPlayer(player);
    }

    // TODO: random map
    private static void startNormalGame(Player player, boolean endless) {
        Optional<Party> party = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
        List<Player> people = party.map(Party::getAllPartyPeoplePlayerOnline).orElseGet(() -> Collections.singletonList(player));
        if (party.isPresent()) {
            if (!party.get().getPartyLeader().getUuid().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are not the party leader");
                return;
            } else if (!party.get().allOnlineAndNoAFKs()) {
                player.sendMessage(ChatColor.RED + "All party members must be online or not afk");
                return;
            }
        }

        Warlords.getGameManager()
                .newEntry(people)
                .setGamemode(GameMode.WAVE_DEFENSE)
                .setMap(endless ? GameMap.ILLUSION_CROSSFIRE : GameMap.ILLUSION_RIFT)
                .setPriority(0)
                .setRequestedGameAddons(GameAddon.PRIVATE_GAME, GameAddon.CUSTOM_GAME)
                .setOnResult((result, game) -> {
                    if (game == null) {
                        player.sendMessage(ChatColor.RED + "Failed to join/create a game: " + result);
                    }
                }).queue();
    }
}
