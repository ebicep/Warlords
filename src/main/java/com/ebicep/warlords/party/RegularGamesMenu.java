package com.ebicep.warlords.party;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RegularGamesMenu {

    private final HashMap<Team, RegularGameTeam> regularGameTeams;

    public RegularGamesMenu(Party party) {
        regularGameTeams = new HashMap<>();
        regularGameTeams.put(Team.BLUE, new RegularGameTeam(party, Team.BLUE));
        regularGameTeams.put(Team.RED, new RegularGameTeam(party, Team.RED));
    }

    public void openMenuForPlayer(Player player) {
        Optional<Map.Entry<Team, RegularGameTeam>> playerTeam = regularGameTeams
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue()
                                      .getTeamPlayers()
                                      .stream()
                                      .anyMatch(p -> p.getUuid().equals(player.getUniqueId())))
                .findFirst();
        if (playerTeam.isEmpty()) {
            Menu menu = new Menu("Team Builder", 9 * 4);
            int i = 0;
            for (Map.Entry<Team, RegularGameTeam> entry : regularGameTeams.entrySet()) {
                Team team = entry.getKey();
                RegularGameTeam regularGameTeam = entry.getValue();
                menu.setItem(i + 1, 1,
                        new ItemBuilder(team.getWoolItem())
                                .name(Component.text(team.getName(), team.teamColor()))
                                .get(),
                        (m, e) -> {
                            regularGameTeam.openMenuForPlayer(player);
                        }
                );
                i++;
            }
            return;
        }
        Team team = playerTeam.get().getKey();
        RegularGameTeam regularGameTeam = regularGameTeams.get(team);
        Menu menu = regularGameTeam.getMenu();
        menu.openForPlayer(player);
    }


    public void reset() {
        regularGameTeams.forEach((team, regularGameTeam) -> regularGameTeam.getTeamPlayers().clear());
    }

    public void addPlayer(Team team, UUID uuid, Specializations spec) {
        RegularGamePlayer regularGamePlayer = new RegularGamePlayer(uuid, spec);
        regularGameTeams.get(team).addPlayer(regularGamePlayer);
    }

    public HashMap<Team, RegularGameTeam> getRegularGameTeams() {
        return regularGameTeams;
    }

    public static class RegularGamePlayer {

        private final UUID uuid;
        private Specializations selectedSpec;

        public RegularGamePlayer(UUID uuid, Specializations selectedSpec) {
            this.uuid = uuid;
            this.selectedSpec = selectedSpec;
        }

        public UUID getUuid() {
            return uuid;
        }

        public Specializations getSelectedSpec() {
            return selectedSpec;
        }

        public void setSelectedSpec(Specializations selectedSpec) {
            this.selectedSpec = selectedSpec;
        }
    }

    public static class RegularGameTeam {

        private final Party party;
        private final Team team;
        private final List<RegularGamePlayer> teamPlayers = new ArrayList<>();
        private final List<UUID> selectedPlayersToSwap = new ArrayList<>();
        private final Menu menu = new Menu("Team Builder", 9 * 6);

        public RegularGameTeam(Party party, Team team) {
            this.party = party;
            this.team = team;
            resetMenu();
        }

        public void openMenuForPlayer(Player player) {
            menu.openForPlayer(player);
        }

        public void resetMenu() {
            updateMisc();
            updatePlayerSpecs();
            updateGeneralInformation();
            updateEditors();

            menu.setItem(
                    4,
                    4,
                    new ItemBuilder(Material.LIME_WOOL)
                            .name(Component.text("Confirm Team", NamedTextColor.GREEN))
                            .get(),
                    (m, e) -> {
                        if (!isEditor(e.getWhoClicked().getUniqueId())) {
                            return;
                        }
                        for (RegularGamePlayer teamPlayer : teamPlayers) {
                            UUID uuid = teamPlayer.getUuid();
                            Specializations spec = teamPlayer.getSelectedSpec();
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                            PlayerSettings.getPlayerSettings(uuid).setSelectedSpec(spec);
                            DatabaseManager.updatePlayer(uuid, databasePlayer -> {
                                databasePlayer.setLastSpec(spec);
                            });
                            if (offlinePlayer.getPlayer() != null) {
                                offlinePlayer.getPlayer().sendMessage(Component.text("---------------------------------------", NamedTextColor.DARK_BLUE));
                                offlinePlayer.getPlayer().sendMessage(Component.text("Your spec was automatically changed to ", NamedTextColor.GREEN)
                                                                               .append(Component.text(spec.name, NamedTextColor.YELLOW))
                                                                               .append(Component.text("!")));
                                offlinePlayer.getPlayer().sendMessage(Component.text("---------------------------------------", NamedTextColor.DARK_BLUE));
                            }
                        }
                        party.sendMessageToAllPartyPlayers(
                                Component.text("The ", NamedTextColor.GREEN)
                                         .append(team.coloredPrefix())
                                         .append(Component.text(" team is ready!"))
                        );
                    }
            );
            menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        }

        private void updateEditors() {
            //players with perms to interact with menu
            List<Component> editors = new ArrayList<>();
            for (RegularGamePlayer teamPlayer : teamPlayers) {
                UUID uuid = teamPlayer.getUuid();
                if (isEditor(uuid)) {
                    String name = Bukkit.getOfflinePlayer(uuid).getName();
                    editors.add(Component.text(name == null ? "Unknown" : name, NamedTextColor.AQUA));
                }
            }


            menu.setItem(
                    3,
                    4,
                    new ItemBuilder(Material.WRITABLE_BOOK)
                            .name(Component.text("Editors", NamedTextColor.GREEN))
                            .lore(editors)
                            .get(),
                    (m, e) -> {
                    }
            );
        }

        private void updateMisc() {
            //team wool surround
            for (int i = 0; i < 6; i++) {
                menu.setItem(0, i, new ItemBuilder(team.getWoolItem()).name(team.coloredPrefix()).get(), (m, e) -> {
                });
                menu.setItem(8, i, new ItemBuilder(team.getWoolItem()).name(team.coloredPrefix()).get(), (m, e) -> {
                });
            }

            //two columns of class icons
            for (int i = 0; i < Classes.VALUES.length; i++) {
                Classes spec = Classes.VALUES[i];
                menu.setItem(2 + i, 0, new ItemBuilder(spec.item).name(Component.text(spec.name, NamedTextColor.GREEN)).get(), (m, e) -> {
                });
            }

            //row of spec icons
            for (int i = 0; i < SpecType.VALUES.length; i++) {
                SpecType specType = SpecType.VALUES[i];
                menu.setItem(1, i + 1, new ItemBuilder(specType.itemStack).name(Component.text(specType.name, specType.getTextColor())).get(), (m, e) -> {
                });
            }

        }

        public void updateGeneralInformation() {
            //showing general list of spec and respective players
            List<Component> playerOnSpecs = new ArrayList<>();
            for (Specializations value : Specializations.VALUES) {
                Optional<RegularGamePlayer> playerOptional = teamPlayers.stream().filter(p -> p.getSelectedSpec() == value).findFirst();
                if (playerOptional.isPresent()) {
                    String name = Bukkit.getOfflinePlayer(playerOptional.get().getUuid()).getName();
                    playerOnSpecs.add(
                            Component.textOfChildren(
                                    Component.text(value.name, NamedTextColor.GOLD),
                                    Component.text(" - ", NamedTextColor.GRAY),
                                    Component.text(name == null ? "Unknown" : name, NamedTextColor.AQUA)
                            )
                    );
                } else {
                    playerOnSpecs.add(
                            Component.textOfChildren(
                                    Component.text(value.name, NamedTextColor.GOLD),
                                    Component.text(" - ", NamedTextColor.GRAY)
                            )
                    );
                }
            }
            menu.setItem(
                    5,
                    4,
                    new ItemBuilder(Material.OAK_SIGN)
                            .name(Component.text("General Information", NamedTextColor.GREEN))
                            .lore(playerOnSpecs)
                            .get(),
                    (m, e) -> {
                    }
            );
        }

        private boolean isEditor(UUID uuid) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.getPlayer() == null) {
                return false;
            }
            return Permissions.isGameStarter(offlinePlayer.getPlayer()) || offlinePlayer.getPlayer().isOp();
        }

        public void updatePlayerSpecs() {
            //player skulls with their spec
            for (RegularGamePlayer p : teamPlayers) {
                UUID uuid = p.getUuid();
                Specializations selectedSpec = p.getSelectedSpec();
                Classes classes = Specializations.getClass(selectedSpec);

                int y = selectedSpec.specType == SpecType.DAMAGE ? 1 :
                        selectedSpec.specType == SpecType.TANK ? 2 :
                        selectedSpec.specType == SpecType.HEALER ? 3 :
                        -1;
                int x = classes == Classes.MAGE ? 2 :
                        classes == Classes.WARRIOR ? 3 :
                        classes == Classes.PALADIN ? 4 :
                        classes == Classes.SHAMAN ? 5 :
                        classes == Classes.ROGUE ? 6 :
                        7;
                if (y == -1) {
                    ChatUtils.MessageType.WARLORDS.sendMessage("ERROR trying to get players spec position for regular game menu");
                    continue;
                }

                ItemBuilder itemBuilder;
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                String name = offlinePlayer.getName();
                if (name == null) {
                    name = "?";
                }
                if (selectedPlayersToSwap.stream().anyMatch(u -> u.equals(uuid))) {
                    itemBuilder = new ItemBuilder(new ItemStack(Material.CREEPER_HEAD))
                            .name(Component.text(name, NamedTextColor.AQUA).append(Component.text(" SELECTED", NamedTextColor.GREEN)))
                            .lore(Component.text(p.getSelectedSpec().name, NamedTextColor.GOLD));
                } else {
                    itemBuilder = new ItemBuilder(HeadUtils.getHead(uuid))
                            .name(Component.text(name, NamedTextColor.AQUA))
                            .lore(Component.text(p.getSelectedSpec().name, NamedTextColor.GOLD));
                }

                menu.setItem(
                        x,
                        y,
                        itemBuilder.get(),
                        (m, e) -> {
                            if (!isEditor(e.getWhoClicked().getUniqueId())) {
                                return;
                            }

                            //unselect player
                            if (selectedPlayersToSwap.contains(uuid)) {
                                selectedPlayersToSwap.remove(uuid);
                            } else {
                                selectedPlayersToSwap.add(uuid);
                                //check swap
                                if (selectedPlayersToSwap.size() == 2) {
                                    swapPlayers();
                                }
                            }
                            updatePlayerSpecs();
                            updateGeneralInformation();
                        }
                );
            }
            setFillers();
        }

        private void swapPlayers() {
            Optional<RegularGamePlayer> regularGamePlayer1 = teamPlayers
                    .stream()
                    .filter(regularGamePlayer -> regularGamePlayer.getUuid().equals(selectedPlayersToSwap.get(0)))
                    .findFirst();
            Optional<RegularGamePlayer> regularGamePlayer2 = teamPlayers
                    .stream()
                    .filter(regularGamePlayer -> regularGamePlayer.getUuid().equals(selectedPlayersToSwap.get(1)))
                    .findFirst();
            if (regularGamePlayer1.isEmpty() || regularGamePlayer2.isEmpty()) {
                return;
            }
            Specializations classToSwap = regularGamePlayer1.get().getSelectedSpec();
            regularGamePlayer1.get().setSelectedSpec(regularGamePlayer2.get().getSelectedSpec());
            regularGamePlayer2.get().setSelectedSpec(classToSwap);
            selectedPlayersToSwap.clear();
        }

        public void setFillers() {
            //fillers
            Arrays.stream(Specializations.VALUES)
                  .filter(spec -> teamPlayers.stream().noneMatch(regularGamePlayer -> regularGamePlayer.getSelectedSpec() == spec))
                  .forEach(selectedSpec -> {
                      Classes classes = Specializations.getClass(selectedSpec);

                      int y = selectedSpec.specType == SpecType.DAMAGE ? 1 :
                              selectedSpec.specType == SpecType.TANK ? 2 :
                              selectedSpec.specType == SpecType.HEALER ? 3 :
                              -1;
                      int x = classes == Classes.MAGE ? 2 :
                              classes == Classes.WARRIOR ? 3 :
                              classes == Classes.PALADIN ? 4 :
                              classes == Classes.SHAMAN ? 5 :
                              classes == Classes.ROGUE ? 6 :
                              7;
                      menu.setItem(
                              x,
                              y,
                              new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                                      .name(Component.text("Available Spec", NamedTextColor.GRAY))
                                      .get(),
                              (m, e) -> {
                                  if (!isEditor(e.getWhoClicked().getUniqueId())) {
                                      return;
                                  }

                                  //give new spec
                                  if (selectedPlayersToSwap.size() == 1) {
                                      UUID uuid = selectedPlayersToSwap.get(0);
                                      for (RegularGamePlayer teamPlayer : teamPlayers) {
                                          if (teamPlayer.getUuid().equals(uuid)) {
                                              teamPlayer.setSelectedSpec(selectedSpec);
                                              selectedPlayersToSwap.clear();

                                              updatePlayerSpecs();
                                              updateGeneralInformation();

                                              break;
                                          }
                                      }
                                  }
                              }
                      );
                  });
        }

        public void checkDuplicateSpecs() {
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
                for (Specializations value : Specializations.VALUES) {
                    if (!assignedClasses.contains(value)) {
                        p.setSelectedSpec(value);
                        assignedClasses.add(value);
                        break;
                    }
                }
            }
        }

        public void addPlayer(RegularGamePlayer regularGamePlayer) {
            teamPlayers.add(regularGamePlayer);
            checkDuplicateSpecs();
            resetMenu();
        }

        public List<UUID> getSelectedPlayersToSwap() {
            return selectedPlayersToSwap;
        }

        public Menu getMenu() {
            return menu;
        }

        public List<RegularGamePlayer> getTeamPlayers() {
            return teamPlayers;
        }
    }

}
