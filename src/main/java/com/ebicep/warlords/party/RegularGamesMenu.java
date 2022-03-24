package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.permissions.PermissionHandler;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.SpecType;
import com.ebicep.warlords.player.Specializations;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class RegularGamesMenu {

    private final Party party;
    private final List<RegularGamePlayer> regularGamePlayers = new ArrayList<>();
    private final HashMap<Team, List<UUID>> selectedPlayersToSwap = new HashMap<Team, List<UUID>>() {{
        put(Team.BLUE, new ArrayList<>());
        put(Team.RED, new ArrayList<>());
    }};
    private HashMap<Team, Boolean> checkPlayers = new HashMap<Team, Boolean>() {{
        put(Team.BLUE, true);
        put(Team.RED, true);
    }};

    public RegularGamesMenu(Party party) {
        this.party = party;
    }

    public void openMenuForPlayer(Player player) {
        Optional<RegularGamePlayer> gamePlayerOptional = regularGamePlayers.stream().filter(p -> p.getUuid().equals(player.getUniqueId())).findFirst();
        if (!gamePlayerOptional.isPresent()) return;

        RegularGamePlayer regularGamePlayer = gamePlayerOptional.get();
        Menu menu = new Menu("Team Builder", 9 * 6);
        Team team = regularGamePlayer.getTeam();

        //team wool surround
        for (int i = 0; i < 6; i++) {
            menu.setItem(0, i, new ItemBuilder(team.getItem()).name(team.coloredPrefix()).get(), (m, e) -> {
            });
            menu.setItem(8, i, new ItemBuilder(team.getItem()).name(team.coloredPrefix()).get(), (m, e) -> {
            });
        }
        for (int i = 1; i < 8; i++) {
            menu.setItem(i, 0, new ItemBuilder(team.getItem()).name(team.coloredPrefix()).get(), (m, e) -> {
            });
            if (i == 2) {
                i = 5;
            }
        }

        //two columns of class icons
        for (int i = 0; i < Classes.values().length; i++) {
            Classes classes = Classes.values()[i];
            menu.setItem(2, i + 1, new ItemBuilder(classes.item).name(ChatColor.GREEN + classes.name).get(), (m, e) -> {
            });
            menu.setItem(6, i + 1, new ItemBuilder(classes.item).name(ChatColor.GREEN + classes.name).get(), (m, e) -> {
            });
        }

        //row of spec icons
        for (int i = 0; i < SpecType.values().length; i++) {
            SpecType specType = SpecType.values()[i];
            menu.setItem(i + 3, 0, new ItemBuilder(specType.itemStack).name(specType.chatColor + specType.name).get(), (m, e) -> {
            });
        }

        //bottom items
        List<RegularGamePlayer> teamPlayers = regularGamePlayers.stream().filter(p -> p.getTeam() == team).collect(Collectors.toList());

        //players with perms to interact with menu
        List<UUID> uuidsWithPerms = new ArrayList<>();
        for (RegularGamePlayer p : teamPlayers) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getUuid());
            if (offlinePlayer.getPlayer() != null) {
                if (PermissionHandler.isGameStarter(offlinePlayer.getPlayer()) || offlinePlayer.getPlayer().isOp()) {
                    uuidsWithPerms.add(p.getUuid());
                }
            }
        }
        List<String> editors = new ArrayList<>();
        for (UUID uuid : uuidsWithPerms) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer != null) {
                editors.add(ChatColor.GRAY + offlinePlayer.getName());
            }
        }
        menu.setItem(
                2,
                5,
                new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.GREEN + "Editors")
                        .lore(editors)
                        .get(),
                (m, e) -> {
                }
        );
        menu.setItem(
                3,
                5,
                new ItemBuilder(Material.WOOL, 1, (short) 5)
                        .name(ChatColor.GREEN + "Confirm Team")
                        .get(),
                (m, e) -> {
                    if (!uuidsWithPerms.contains(player.getUniqueId())) return;
                    for (RegularGamePlayer teamPlayer : teamPlayers) {
                        UUID uuid = teamPlayer.getUuid();
                        Specializations spec = teamPlayer.getSelectedSpec();
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                        Warlords.getPlayerSettings(uuid).setSelectedSpec(spec);
                        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
                        databasePlayer.setLastSpec(spec);
                        DatabaseManager.updatePlayerAsync(databasePlayer);
                        if (offlinePlayer.getPlayer() != null) {
                            offlinePlayer.getPlayer().sendMessage(ChatColor.DARK_BLUE + "---------------------------------------");
                            offlinePlayer.getPlayer().sendMessage(ChatColor.GREEN + "Your spec was automatically changed to " + ChatColor.YELLOW + spec.name + ChatColor.GREEN + "!");
                            offlinePlayer.getPlayer().sendMessage(ChatColor.DARK_BLUE + "---------------------------------------");
                        }
                    }
                    regularGamePlayers.forEach(p -> {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getUuid());
                        if (offlinePlayer.getPlayer() != null) {
                            offlinePlayer.getPlayer().sendMessage(ChatColor.GREEN + "The " + team.coloredPrefix() + ChatColor.GREEN + " team is ready!");
                        }
                    });
                }
        );
        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);

        //showing general list of spec and respective players
        List<String> playerOnSpecs = new ArrayList<>();
        for (Specializations value : Specializations.values()) {
            Optional<RegularGamePlayer> playerOptional = teamPlayers.stream().filter(p -> p.getSelectedSpec() == value).findFirst();
            if (playerOptional.isPresent()) {
                playerOnSpecs.add(ChatColor.GOLD + value.name + ChatColor.GRAY + " - " + ChatColor.AQUA + Bukkit.getOfflinePlayer(playerOptional.get().getUuid()).getName());
            } else {
                playerOnSpecs.add(ChatColor.GOLD + value.name + ChatColor.GRAY + " - " + ChatColor.AQUA);
            }
        }
        menu.setItem(
                5,
                5,
                new ItemBuilder(Material.SIGN)
                        .name(ChatColor.GREEN + "General Information")
                        .lore(playerOnSpecs)
                        .get(),
                (m, e) -> {
                }
        );

        if (checkPlayers.get(team)) {
            checkPlayers.put(team, false);
            //removing all duplicate specs
            List<Specializations> assignedClasses = new ArrayList<>();
            List<RegularGamePlayer> playersToReassign = new ArrayList<>();
            for (RegularGamePlayer p : teamPlayers) {
                Specializations selectedSpec = p.getSelectedSpec();
                if (assignedClasses.contains(selectedSpec)) {
                    playersToReassign.add(p);
                } else {
                    assignedClasses.add(selectedSpec);
                }
            }
            for (RegularGamePlayer p : playersToReassign) {
                for (Specializations value : Specializations.values()) {
                    if (!assignedClasses.contains(value)) {
                        p.setSelectedSpec(value);
                        assignedClasses.add(value);
                        break;
                    }
                }
            }
        }
        //player skulls with their spec
        List<Specializations> assignedClasses = new ArrayList<>();
        for (RegularGamePlayer p : teamPlayers) {
            UUID uuid = p.getUuid();
            Specializations selectedSpec = p.getSelectedSpec();
            Classes classes = Specializations.getClass(selectedSpec);

            int x = selectedSpec.specType == SpecType.DAMAGE ? 3 :
                    selectedSpec.specType == SpecType.TANK ? 4 :
                            selectedSpec.specType == SpecType.HEALER ? 5 :
                                    -1;
            int y = classes == Classes.MAGE ? 1 :
                    classes == Classes.WARRIOR ? 2 :
                            classes == Classes.PALADIN ? 3 :
                                    classes == Classes.SHAMAN ? 4 :
                                            -1;
            if (x == -1 || y == -1) {
                System.out.println("ERROR trying to get players spec position for regular game menu");
                continue;
            }

            assignedClasses.add(selectedSpec);

            ItemBuilder itemBuilder;
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String name = offlinePlayer != null ? offlinePlayer.getName() : "UNKNOWN";
            if (selectedPlayersToSwap.get(team).contains(uuid)) {
                itemBuilder = new ItemBuilder(new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.CREEPER.ordinal()))
                        .name(ChatColor.AQUA + name + ChatColor.GREEN + " SELECTED")
                        .lore(ChatColor.GOLD + p.getSelectedSpec().name);
            } else {
                itemBuilder = new ItemBuilder(Warlords.getHead(uuid))
                        .name(ChatColor.AQUA + name)
                        .lore(ChatColor.GOLD + p.getSelectedSpec().name);
            }

            menu.setItem(
                    x,
                    y,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (!uuidsWithPerms.contains(player.getUniqueId())) return;

                        List<UUID> uuids = selectedPlayersToSwap.get(team);
                        //unselect player
                        if (uuids.contains(uuid)) {
                            uuids.remove(uuid);
                        } else {
                            uuids.add(uuid);
                            //check swap
                            if (uuids.size() == 2) {
                                swapPlayers(team);
                            }
                        }
                        menu.openForPlayer(player);
                    }
            );
        }
        //fillers
        Arrays.stream(Specializations.values())
                .filter(classes -> !assignedClasses.contains(classes)).collect(Collectors.toList())
                .forEach(selectedSpec -> {
                    Classes classes = Specializations.getClass(selectedSpec);

                    int x = selectedSpec.specType == SpecType.DAMAGE ? 3 :
                            selectedSpec.specType == SpecType.TANK ? 4 :
                                    selectedSpec.specType == SpecType.HEALER ? 5 :
                                            -1;
                    int y = classes == Classes.MAGE ? 1 :
                            classes == Classes.WARRIOR ? 2 :
                                    classes == Classes.PALADIN ? 3 :
                                            classes == Classes.SHAMAN ? 4 :
                                                    -1;
                    menu.setItem(
                            x,
                            y,
                            new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7).name(ChatColor.GRAY + "Available Spec").get(),
                            (m, e) -> {
                                if (!uuidsWithPerms.contains(player.getUniqueId())) return;

                                //give new spec
                                List<UUID> uuids = selectedPlayersToSwap.get(team);
                                if (uuids.size() == 1) {
                                    regularGamePlayers.stream()
                                            .filter(p -> p.getUuid().equals(selectedPlayersToSwap.get(team).get(0)))
                                            .findFirst()
                                            .ifPresent(p -> {
                                                p.setSelectedSpec(selectedSpec);
                                                selectedPlayersToSwap.get(team).clear();
                                            });
                                }
                                menu.openForPlayer(player);
                            }
                    );
                });

        menu.openForPlayer(player);
    }

    private void swapPlayers(Team team) {
        Optional<RegularGamePlayer> regularGamePlayer1 = regularGamePlayers.stream().filter(regularGamePlayer -> regularGamePlayer.getUuid().equals(selectedPlayersToSwap.get(team).get(0))).findFirst();
        Optional<RegularGamePlayer> regularGamePlayer2 = regularGamePlayers.stream().filter(regularGamePlayer -> regularGamePlayer.getUuid().equals(selectedPlayersToSwap.get(team).get(1))).findFirst();
        if (!regularGamePlayer1.isPresent() || !regularGamePlayer2.isPresent()) return;
        Specializations classToSwap = regularGamePlayer1.get().getSelectedSpec();
        regularGamePlayer1.get().setSelectedSpec(regularGamePlayer2.get().getSelectedSpec());
        regularGamePlayer2.get().setSelectedSpec(classToSwap);
        selectedPlayersToSwap.get(team).clear();
    }

    public void reset() {
        checkPlayers.put(Team.BLUE, true);
        checkPlayers.put(Team.RED, true);
        regularGamePlayers.clear();
    }

    public List<RegularGamePlayer> getRegularGamePlayers() {
        return regularGamePlayers;
    }

    public static class RegularGamePlayer {
        private final UUID uuid;
        private final Team team;
        private Specializations selectedSpec;

        public RegularGamePlayer(UUID uuid, Team team, Specializations selectedSpec) {
            this.uuid = uuid;
            this.team = team;
            this.selectedSpec = selectedSpec;
        }

        public UUID getUuid() {
            return uuid;
        }

        public Team getTeam() {
            return team;
        }

        public Specializations getSelectedSpec() {
            return selectedSpec;
        }

        public void setSelectedSpec(Specializations selectedSpec) {
            this.selectedSpec = selectedSpec;
        }
    }
}
