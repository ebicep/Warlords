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

    private final Party party;
    private final List<RegularGamePlayer> regularGamePlayers = new ArrayList<>();
    private final HashMap<Team, List<UUID>> selectedPlayersToSwap = new HashMap<>() {{
        put(Team.BLUE, new ArrayList<>());
        put(Team.RED, new ArrayList<>());
    }};
    private HashMap<Team, Boolean> checkPlayers = new HashMap<>() {{
        put(Team.BLUE, true);
        put(Team.RED, true);
    }};

    public RegularGamesMenu(Party party) {
        this.party = party;
    }

    public void openMenuForPlayer(Player player) {
        Optional<RegularGamePlayer> gamePlayerOptional = regularGamePlayers.stream().filter(p -> p.getUuid().equals(player.getUniqueId())).findFirst();
        if (gamePlayerOptional.isEmpty()) {
            return;
        }

        RegularGamePlayer regularGamePlayer = gamePlayerOptional.get();
        Menu menu = new Menu("Team Builder", 9 * 6);
        Team team = regularGamePlayer.getTeam();

        //team wool surround
        for (int i = 0; i < 6; i++) {
            menu.setItem(0, i, new ItemBuilder(team.getWool()).name(team.coloredPrefix()).get(), (m, e) -> {
            });
            menu.setItem(8, i, new ItemBuilder(team.getWool()).name(team.coloredPrefix()).get(), (m, e) -> {
            });
        }
        for (int i = 1; i < 8; i++) {
            menu.setItem(i, 0, new ItemBuilder(team.getWool()).name(team.coloredPrefix()).get(), (m, e) -> {
            });
            if (i == 2) {
                i = 5;
            }
        }

        //two columns of class icons
        for (int i = 0; i < Classes.VALUES.length; i++) {
            Classes spec = Classes.VALUES[i];
            menu.setItem(2, i + 1, new ItemBuilder(spec.item).name(Component.text(spec.name, NamedTextColor.GREEN)).get(), (m, e) -> {
            });
            menu.setItem(6, i + 1, new ItemBuilder(spec.item).name(Component.text(spec.name, NamedTextColor.GREEN)).get(), (m, e) -> {
            });
        }

        //row of spec icons
        for (int i = 0; i < SpecType.VALUES.length; i++) {
            SpecType specType = SpecType.VALUES[i];
            menu.setItem(i + 3, 0, new ItemBuilder(specType.itemStack).name(Component.text(specType.name, specType.getTextColor())).get(), (m, e) -> {
            });
        }

        //bottom items
        List<RegularGamePlayer> teamPlayers = regularGamePlayers.stream().filter(p -> p.getTeam() == team).toList();

        //players with perms to interact with menu
        List<UUID> uuidsWithPerms = new ArrayList<>();
        for (RegularGamePlayer p : teamPlayers) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getUuid());
            if (offlinePlayer.getPlayer() != null) {
                if (Permissions.isGameStarter(offlinePlayer.getPlayer()) || offlinePlayer.getPlayer().isOp()) {
                    uuidsWithPerms.add(p.getUuid());
                }
            }
        }
        List<Component> editors = new ArrayList<>();
        for (UUID uuid : uuidsWithPerms) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            editors.add(Component.text(offlinePlayer.getName(), NamedTextColor.GRAY));
        }

        //showing general list of spec and respective players
        List<Component> playerOnSpecs = new ArrayList<>();
        for (Specializations value : Specializations.VALUES) {
            Optional<RegularGamePlayer> playerOptional = teamPlayers.stream().filter(p -> p.getSelectedSpec() == value).findFirst();
            if (playerOptional.isPresent()) {
                playerOnSpecs.add(
                        Component.textOfChildren(
                                Component.text(value.name, NamedTextColor.GOLD),
                                Component.text(" - ", NamedTextColor.GRAY),
                                Component.text(Bukkit.getOfflinePlayer(playerOptional.get().getUuid()).getName(), NamedTextColor.AQUA)
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
                7,
                3,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name(Component.text("Editors", NamedTextColor.GREEN))
                        .lore(editors)
                        .get(),
                (m, e) -> {
                }
        );
        menu.setItem(
                7,
                4,
                new ItemBuilder(Material.OAK_SIGN)
                        .name(Component.text("General Information", NamedTextColor.GREEN))
                        .lore(playerOnSpecs)
                        .get(),
                (m, e) -> {
                }
        );
        menu.setItem(
                7,
                5,
                new ItemBuilder(Material.LIME_WOOL)
                        .name(Component.text("Confirm Team", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                    if (!uuidsWithPerms.contains(player.getUniqueId())) {
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
                            offlinePlayer.getPlayer().sendMessage(Component.text("Your spec was automatically changed to ")
                                                                           .append(Component.text(spec.name, NamedTextColor.YELLOW))
                                                                           .append(Component.text("!")));
                            offlinePlayer.getPlayer().sendMessage(Component.text("---------------------------------------", NamedTextColor.DARK_BLUE));
                        }
                    }
                    regularGamePlayers.forEach(p -> {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getUuid());
                        if (offlinePlayer.getPlayer() != null) {
                            offlinePlayer.getPlayer().sendMessage(Component.text("The ", NamedTextColor.GREEN)
                                                                           .append(team.coloredPrefix())
                                                                           .append(Component.text(" team is ready!"))
                            );
                        }
                    });
                }
        );
        menu.setItem(0, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);


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
                for (Specializations value : Specializations.VALUES) {
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
                ChatUtils.MessageType.WARLORDS.sendMessage("ERROR trying to get players spec position for regular game menu");
                continue;
            }

            assignedClasses.add(selectedSpec);

            ItemBuilder itemBuilder;
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            String name = offlinePlayer.getName();
            if (selectedPlayersToSwap.get(team).contains(uuid)) {
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
                        if (!uuidsWithPerms.contains(player.getUniqueId())) {
                            return;
                        }

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
        Arrays.stream(Specializations.VALUES)
              .filter(classes -> !assignedClasses.contains(classes)).toList()
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
                          classes == Classes.ROGUE ? 5 : -1;
                  menu.setItem(
                          x,
                          y,
                          new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                                  .name(Component.text("Available Spec", NamedTextColor.GRAY))
                                  .get(),
                          (m, e) -> {
                              if (!uuidsWithPerms.contains(player.getUniqueId())) {
                                  return;
                              }

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
        Optional<RegularGamePlayer> regularGamePlayer1 = regularGamePlayers.stream()
                                                                           .filter(regularGamePlayer -> regularGamePlayer.getUuid().equals(selectedPlayersToSwap.get(team).get(0)))
                                                                           .findFirst();
        Optional<RegularGamePlayer> regularGamePlayer2 = regularGamePlayers.stream()
                                                                           .filter(regularGamePlayer -> regularGamePlayer.getUuid().equals(selectedPlayersToSwap.get(team).get(1)))
                                                                           .findFirst();
        if (regularGamePlayer1.isEmpty() || regularGamePlayer2.isEmpty()) {
            return;
        }
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
