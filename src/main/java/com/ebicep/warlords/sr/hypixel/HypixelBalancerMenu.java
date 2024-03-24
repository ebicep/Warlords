package com.ebicep.warlords.sr.hypixel;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import io.github.rapha149.signgui.SignGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class HypixelBalancerMenu {

    private static final Map<UUID, PlayerMenuData> PLAYER_MENU_DATA = new HashMap<>();
    private static final Color MINECRAFT_COLOR = new Color() {

        @Override
        public String black() {
            return ChatColor.BLACK.toString();
        }

        @Override
        public String darkBlue() {
            return ChatColor.DARK_BLUE.toString();
        }

        @Override
        public String darkGreen() {
            return ChatColor.DARK_GREEN.toString();
        }

        @Override
        public String darkAqua() {
            return ChatColor.DARK_AQUA.toString();
        }

        @Override
        public String darkRed() {
            return ChatColor.DARK_RED.toString();
        }

        @Override
        public String darkPurple() {
            return ChatColor.DARK_PURPLE.toString();
        }

        @Override
        public String gold() {
            return ChatColor.GOLD.toString();
        }

        @Override
        public String gray() {
            return ChatColor.GRAY.toString();
        }

        @Override
        public String darkGray() {
            return ChatColor.DARK_GRAY.toString();
        }

        @Override
        public String blue() {
            return ChatColor.BLUE.toString();
        }

        @Override
        public String green() {
            return ChatColor.GREEN.toString();
        }

        @Override
        public String aqua() {
            return ChatColor.AQUA.toString();
        }

        @Override
        public String red() {
            return ChatColor.RED.toString();
        }

        @Override
        public String lightPurple() {
            return ChatColor.LIGHT_PURPLE.toString();
        }

        @Override
        public String yellow() {
            return ChatColor.YELLOW.toString();
        }

        @Override
        public String white() {
            return ChatColor.WHITE.toString();
        }
    };


    private static Material getBalanceMethodMaterial(BalanceMethod balanceMethod) {
        if (balanceMethod == BalanceMethod.V1) {
            return Material.DIRT;
        }
        if (balanceMethod == BalanceMethod.V2) {
            return Material.COBBLESTONE;
        }
        return Material.BARRIER;
    }

    private static Material getRandomWeightMethodMaterial(WeightGenerationMethod randomWeightMethod) {
        if (randomWeightMethod == WeightGenerationMethod.DEFAULT_RANDOM) {
            return Material.ENCHANTING_TABLE;
        }
        if (randomWeightMethod == WeightGenerationMethod.DEFAULT_NORMAL_DISTRIBUTION) {
            return Material.BOOKSHELF;
        }
        return Material.BARRIER;
    }

    private static <T> List<Component> getSettingSelectedLore(T selected, T[] values) {
        List<Component> lore = new ArrayList<>();
        for (T value : values) {
            if (value == selected) {
                lore.add(Component.text(" - " + value.getClass().getSimpleName(), NamedTextColor.GREEN));
            } else {
                lore.add(Component.text(" - " + value.getClass().getSimpleName(), NamedTextColor.GRAY));
            }
        }
        return lore;
    }

    private static <T> T getNext(T value, T[] values) {
        int index = Arrays.asList(values).indexOf(value);
        return values[(index + 1) % values.length];
    }

    public static void openMenu(Player player) {
        PlayerMenuData menuData = PLAYER_MENU_DATA.computeIfAbsent(player.getUniqueId(), k -> new PlayerMenuData());

        Menu menu = new Menu("Hypixel Balancer", 9 * 6);

        menu.setItem(1, 1,
                new ItemBuilder(Material.OAK_SIGN)
                        .name(Component.text("Player Count", NamedTextColor.AQUA))
                        .lore(Component.text("Current: ").append(Component.text(menuData.getPlayerCount(), NamedTextColor.GREEN)))
                        .get(),
                (m, e) -> {
                    SignGUI.builder()
                           .setLines("", "^^^^^^", "Enter", "Player Count")
                           .setHandler((p, lines) -> {
                               String amount = lines.getLine(0);
                               try {
                                   int amountInt = Integer.parseInt(amount);
                                   menuData.setPlayerCount(amountInt);
                               } catch (Exception exception) {
                                   p.sendMessage(Component.text("Invalid Amount", NamedTextColor.RED));
                               }
                               new BukkitRunnable() {
                                   @Override
                                   public void run() {
                                       openMenu(p);
                                   }
                               }.runTaskLater(Warlords.getInstance(), 1);
                               return null;
                           })
                           .build()
                           .open(player);
                }
        );
        addSettingToMenu(2, 1, player, menu,
                HypixelBalancerMenu::getBalanceMethodMaterial,
                "Balance Method",
                menuData.getBalanceMethod(),
                BalanceMethod.VALUES,
                menuData::setBalanceMethod
        );
        addSettingToMenu(3, 1, player, menu,
                HypixelBalancerMenu::getRandomWeightMethodMaterial,
                "Random Weight Method",
                menuData.getWeightGenerationMethod(),
                WeightGenerationMethod.VALUES,
                menuData::setWeightGenerationMethod
        );
        ExtraBalanceFeature[] values = ExtraBalanceFeature.VALUES;
        for (int i = 0; i < values.length; i++) {
            ExtraBalanceFeature balanceFeature = values[i];
            ItemBuilder itemBuilder = new ItemBuilder(Utils.getWoolFromIndex(i))
                    .name(Component.text(balanceFeature.getClass().getSimpleName(), NamedTextColor.AQUA));
            if (menuData.getExtraBalanceFeatures().contains(balanceFeature)) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
            menu.setItem(i + 1, 2, itemBuilder.get(), (m, e) -> {
                if (menuData.getExtraBalanceFeatures().contains(balanceFeature)) {
                    menuData.getExtraBalanceFeatures().remove(balanceFeature);
                } else {
                    menuData.getExtraBalanceFeatures().add(balanceFeature);
                }
                openMenu(player);
            });
        }

        menu.setItem(4, 3,
                new ItemBuilder(Material.GREEN_WOOL)
                        .name(Component.text("Balance", NamedTextColor.AQUA))
                        .lore(
                                Component.text("Player Count: ").append(Component.text(menuData.getPlayerCount(), NamedTextColor.GREEN)),
                                Component.text("Balance Method: ").append(Component.text(menuData.getBalanceMethod().getClass().getSimpleName(), NamedTextColor.GREEN)),
                                Component.text("Random Weight Method: ")
                                         .append(Component.text(menuData.getWeightGenerationMethod().getClass().getSimpleName(), NamedTextColor.GREEN)),
                                Component.text("Extra Balance Features: ").append(Component.text(menuData.getExtraBalanceFeatures().toString(), NamedTextColor.GREEN))
                        ).get(),
                (m, e) -> {
                    HypixelBalancer.balance(
                            new HypixelBalancer.Printer(player::sendMessage, MINECRAFT_COLOR),
                            1,
                            menuData.getPlayerCount(),
                            menuData.getBalanceMethod(),
                            menuData.getWeightGenerationMethod(),
                            menuData.getExtraBalanceFeatures()
                    );
                    player.closeInventory();
                }
        );

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static <T> void addSettingToMenu(
            int x,
            int y,
            Player player,
            Menu menu,
            Function<T, Material> itemMaterial,
            String itemName,
            T selected,
            T[] values,
            Consumer<T> setter
    ) {
        menu.setItem(x, y,
                new ItemBuilder(itemMaterial.apply(selected))
                        .name(Component.text(itemName, NamedTextColor.AQUA))
                        .lore(getSettingSelectedLore(selected, values))
                        .get(),
                (m, e) -> {
                    setter.accept(getNext(selected, values));
                    openMenu(player);
                }
        );
    }

    static class PlayerMenuData {
        private int playerCount = 22;
        private BalanceMethod balanceMethod = BalanceMethod.V1;
        private WeightGenerationMethod weightGenerationMethod = WeightGenerationMethod.DEFAULT_RANDOM;
        private List<ExtraBalanceFeature> extraBalanceFeatures = new ArrayList<>();

        public int getPlayerCount() {
            return playerCount;
        }

        public void setPlayerCount(int playerCount) {
            this.playerCount = playerCount;
        }

        public BalanceMethod getBalanceMethod() {
            return balanceMethod;
        }

        public void setBalanceMethod(BalanceMethod balanceMethod) {
            this.balanceMethod = balanceMethod;
        }

        public WeightGenerationMethod getWeightGenerationMethod() {
            return weightGenerationMethod;
        }

        public void setWeightGenerationMethod(WeightGenerationMethod weightGenerationMethod) {
            this.weightGenerationMethod = weightGenerationMethod;
        }

        public List<ExtraBalanceFeature> getExtraBalanceFeatures() {
            return extraBalanceFeatures;
        }
    }

}
