package com.ebicep.warlords.menu.generalmenu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.ebicep.warlords.menu.Menu.*;

public class MainLobbySetupMenu {

    public static boolean isInMainLobbyGame(Player player) {
        WarlordsEntity entity = Warlords.getPlayer(player);
        return entity instanceof WarlordsPlayer wp
                && wp.getGame().getGameMode() == GameMode.LOBBY;
    }

    public static void openSetupMenu(Player player) {
        if (!isInMainLobbyGame(player)) {
            return;
        }
        WarlordsPlayer wp = (WarlordsPlayer) Warlords.getPlayer(player);
        Team currentTeam = wp.getTeam();

        Menu menu = new Menu("Team & Class Setup", 9 * 6);

        List<Team> teams = Arrays.asList(Team.RED, Team.BLUE);
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            ItemBuilder builder = new ItemBuilder(team.getWool())
                    .name(Component.text("Swap to the ", NamedTextColor.GREEN)
                                   .append(team.coloredPrefix())
                                   .append(Component.text(" team")));
            if (team == currentTeam) {
                builder.enchant(Enchantment.RESPIRATION, 1);
                builder.addLore(Component.text("Currently on this team", NamedTextColor.GREEN));
            } else {
                builder.addLore(Component.text("Click to swap teams", NamedTextColor.YELLOW));
            }
            int finalI = i;
            menu.setItem(
                    4,
                    1 + i,
                    builder.get(),
                    (m, e) -> {
                        Team selectedTeam = teams.get(finalI);
                        if (selectedTeam != wp.getTeam()) {
                            swapPlayerTeam(player, wp, selectedTeam);
                            player.closeInventory();
                        }
                    }
            );
        }

        Classes[] values = Classes.VALUES;
        int classesPerSide = values.length / 2;
        for (int i = 0; i < values.length; i++) {
            int column = i < classesPerSide ? i + 1 : i + 2;
            Classes group = values[i];
            menu.setItem(column, 0,
                    new ItemBuilder(group.item)
                            .name(Component.text(group.name, NamedTextColor.GREEN))
                            .get(),
                    (m, e) -> {
                    }
            );
            List<Specializations> subclasses = group.subclasses;
            for (int j = 0; j < subclasses.size(); j++) {
                Specializations spec = subclasses.get(j);
                ItemBuilder specItem = new ItemBuilder(spec.specType.itemStack)
                        .name(Component.text(spec.name, NamedTextColor.GREEN));
                if (wp.getSpecClass() == spec) {
                    specItem.enchant(Enchantment.RESPIRATION, 1);
                }
                menu.setItem(column, 1 + j,
                        specItem.get(),
                        (m, e) -> openSpecBoostMenu(player, spec)
                );
            }
        }

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openSpecBoostMenu(Player player, Specializations selectedSpec) {
        if (!isInMainLobbyGame(player)) {
            return;
        }
        List<SpecBoostManager.SpecBoost<?>> specBoosts = SpecBoostManager.getSpecBoosts(selectedSpec);
        if (specBoosts.isEmpty()) {
            applySpec(player, selectedSpec);
            return;
        }

        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Map<Specializations, Integer> selectedBoosts = databasePlayer.getSpecBoosts();

        Menu menu = new Menu("Spec Boosts", 9 * 4);
        for (int i = 0; i < specBoosts.size(); i++) {
            SpecBoostManager.SpecBoost<?> specBoost = specBoosts.get(i);
            int finalI = i;
            boolean selected = selectedBoosts.computeIfAbsent(selectedSpec, k -> 0) == i;
            ItemBuilder itemBuilder = new ItemBuilder(selectedSpec.specType.itemStack)
                    .name(specBoost.getName())
                    .lore(specBoost.getDescriptionLore());
            if (selected) {
                itemBuilder.enchant(Enchantment.RESPIRATION, 1);
            }
            menu.setItem(i + 2, 1,
                    itemBuilder.get(),
                    (m, e) -> {
                        selectedBoosts.put(selectedSpec, finalI);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        applySpec(player, selectedSpec);
                    }
            );
        }

        menu.setItem(3, 3, MENU_BACK, (m, e) -> openSetupMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static void swapPlayerTeam(Player player, WarlordsPlayer wp, Team otherTeam) {
        Game game = wp.getGame();
        game.setPlayerTeam(wp.getUuid(), otherTeam);
        wp.setTeam(otherTeam);
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        databasePlayer.setWantedTeam(otherTeam);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        wp.updateArmor();
        player.sendMessage(Component.text("You have joined the ", NamedTextColor.GREEN)
                                    .append(Component.text(otherTeam.getName(), otherTeam.getTeamColor()))
                                    .append(Component.text(" team!"))
        );
    }

    private static void applySpec(Player player, Specializations spec) {
        WarlordsPlayer wp = (WarlordsPlayer) Warlords.getPlayer(player);
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        databasePlayer.setLastSpec(spec);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        wp.setSpec(spec);
        player.sendMessage(Component.text("You have changed your specialization to: ", NamedTextColor.GREEN)
                                    .append(Component.text(spec.name, NamedTextColor.AQUA))
        );
        player.closeInventory();
    }

}
