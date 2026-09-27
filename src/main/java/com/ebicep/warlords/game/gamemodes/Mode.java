package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.option.FlyOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.PreGameItemOption;
import com.ebicep.warlords.game.option.damage.DrowningDamage;
import com.ebicep.warlords.game.option.damage.FallDamage;
import com.ebicep.warlords.game.option.damage.KillDamage;
import com.ebicep.warlords.game.option.damage.VoidDamage;
import com.ebicep.warlords.game.option.freeze.GameFreezeOption;
import com.ebicep.warlords.game.option.pvp.DebugLogOption;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.AbstractPlayerClass;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.TriFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;
import static com.ebicep.warlords.menu.generalmenu.WarlordsShopMenu.openMainMenu;

public interface Mode {

    default List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = new ArrayList<>(64);

        options.add(new PreGameItemOption(1, (g, p) -> {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(p);
            Specializations selectedSpec = databasePlayer.getLastSpec();
            AbstractPlayerClass apc = selectedSpec.create(getNamespaces());

            ItemStack weaponSkin = databasePlayer.getSpec(selectedSpec).getWeapon().getItem();
            return new ItemBuilder(apc.getWeapon().getItem(weaponSkin))
                    .name(Component.text("Weapon Skin Preview", NamedTextColor.GREEN))
                    .noLore()
                    .get();
        }
        ));
        options.add(new PreGameItemOption(4, new ItemBuilder(Material.NETHER_STAR)
                .name(Component.text("Pre-game Menu ", NamedTextColor.AQUA))
                .lore(WordWrap.wrap(Component.text("Allows you to change your class, select a weapon, and edit your settings.", NamedTextColor.GRAY), 150))
                .get(), (g, p) -> openMainMenu(p)
        ));
        options.add(new PreGameItemOption(5, new ItemBuilder(Material.NOTE_BLOCK)
                        .name(Component.text("Player Spec Information", NamedTextColor.AQUA))
                        .lore(Component.text("Displays the amount of people on each specialization.", NamedTextColor.GRAY))
                        .get(),
                        (g, p) -> {
                            openPlayerSpecInfoMenu(g, p);
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    if (PlainTextComponentSerializer.plainText().serialize(p.getOpenInventory().title()).equals("Player Specs")) {
                                        openPlayerSpecInfoMenu(g, p);
                                    } else {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(Warlords.getInstance(), 20, 20);
                        }
                )
        );
        options.add(new PreGameItemOption(7, (g, p) -> !g.acceptsPeople() ? null : new ItemBuilder(Material.BARRIER)
                        .name(Component.text("Leave", NamedTextColor.RED))
                        .lore(Component.text("Right-Click to leave the game.", NamedTextColor.GRAY))
                        .get(),
                        (g, p) -> {
                            if (g.acceptsPeople()) {
                                g.removePlayer(p.getUniqueId());
                            }
                        }
                )
        );

        options.add(new GameFreezeOption());
        options.add(new DrowningDamage());
        options.add(new FallDamage());
        options.add(new KillDamage());
        options.add(new VoidDamage());
        options.add(new FlyOption());
        options.add(new DebugLogOption());

        return options;
    }

    default List<String> getNamespaces() {
        return ConfigManager.DEFAULT_NAMESPACES;
    }

    static void openPlayerSpecInfoMenu(Game game, Player player) {
        Menu menu = new Menu("Player Specs", 9 * 4);
        int x = 3;
        for (SpecType value : SpecType.VALUES) {
            ItemBuilder itemBuilder = new ItemBuilder(value.itemStack)
                    .name(Component.text(value.name, value.getTextColor()));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Total: ", NamedTextColor.GREEN)
                              .append(Component.text((int) game.getPlayers().keySet().stream()
                                                               .map(DatabaseManager::getPlayer)
                                                               .map(DatabasePlayer::getLastSpec)
                                                               .filter(c -> c.specType == value)
                                                               .count(), NamedTextColor.GOLD
                              )));
            lore.add(Component.empty());
            Arrays.stream(Specializations.VALUES)
                  .filter(classes -> classes.specType == value)
                  .forEach(classes -> {
                      int playersOnSpec = (int) game.getPlayers().keySet().stream()
                                                    .map(DatabaseManager::getPlayer)
                                                    .map(DatabasePlayer::getLastSpec)
                                                    .filter(c -> c == classes)
                                                    .count();
                      lore.add(Component.text(classes.name + " : ").append(Component.text(playersOnSpec, NamedTextColor.YELLOW)));
                  });
            itemBuilder.lore(lore);
            menu.setItem(
                    x,
                    1,
                    itemBuilder.get(),
                    (m, e) -> {
                    }
            );
            x++;
        }
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    default List<Option> postMapModifyOptions(
            GameMap map,
            LocationFactory loc,
            EnumSet<GameAddon> addons,
            List<Option> options
    ) {
        return options;
    }

    String getName();

    String getAbbreviation();

    ItemStack getItemStack();

    default boolean isHiddenInMenu() {
        return false;
    }

    default TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> getCreateDatabaseGame() {
        return (game, triggerWinEvent, updatePlayerStats) -> null;
    }

    default GamesCollections getGamesCollections() {
        return null;
    }

    int getMinPlayersToAddToDatabase();

    default float getDropModifier() {
        return 1;
    }

}
