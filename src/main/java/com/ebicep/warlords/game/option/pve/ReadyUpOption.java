package com.ebicep.warlords.game.option.pve;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.DatabasePlayerPvEEventGardenOfHesperidesStats;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class ReadyUpOption implements Option {

    private static final Map<Integer, List<Integer>> PLAYER_COUNT_MENU_POSITIONS = new HashMap<>() {{
        put(1, Collections.singletonList(4));
        put(2, List.of(3, 5));
        put(3, List.of(2, 4, 6));
        put(4, List.of(2, 3, 5, 6));
        put(5, List.of(2, 3, 4, 5, 6));
    }};
    protected NPC npc;
    private final Map<WarlordsEntity, Boolean> readyPlayers = new LinkedHashMap<>();
    private String name = "Ready Up";
    private Game game;
    private Runnable whenAllReady;

    public ReadyUpOption() {
    }

    public ReadyUpOption(String name) {
        this.name = name;
    }

    public void setWhenAllReady(Runnable whenAllReady) {
        this.whenAllReady = whenAllReady;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
    }

    @Override
    public void start(@Nonnull Game game) {
        createNPC(game);
        new GameRunnable(game) {
            @Override
            public void run() {
                if (checkAllReady()) {
                    onAllReady();
                }
            }
        }.runTaskLater(30);
    }

    protected void createNPC(@Nonnull Game game) {
    }

    private boolean checkAllReady() {
        return readyPlayers.values().stream().allMatch(b -> b);
    }

    private void onAllReady() {
        game.forEachOnlinePlayer((p, team) -> p.closeInventory());
        whenAllReady.run();
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        if (npc != null) {
            npc.destroy();
        }
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            DatabaseManager.getPlayer(player.getUuid(), databasePlayer -> {
                DatabasePlayerPvEEventGardenOfHesperidesStats gardenOfHesperidesStats = databasePlayer.getPveStats().getEventStats().getGardenOfHesperidesStats();
                boolean tartarusAutoReady = gardenOfHesperidesStats.isTartarusAutoReady();
                readyPlayers.put(player, tartarusAutoReady);
                if (tartarusAutoReady) {
                    sendNPCMessage(Component.textOfChildren(
                            Component.text(player.getName(), NamedTextColor.AQUA),
                            Component.text(" is now ready", NamedTextColor.GREEN)
                    ));
                    player.sendMessage(Component.text("[Click to disable auto ready]", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                                .clickEvent(ClickEvent.callback(audience -> gardenOfHesperidesStats.setTartarusAutoReady(false))));
                }
            });
            // safety
            if (!readyPlayers.containsKey(player)) {
                readyPlayers.put(player, false);
            }
        }
    }

    public void sendNPCMessage(Component message) {
        game.forEachOnlinePlayer((player, team) -> {
            player.sendMessage(Component.textOfChildren(
                    Component.text("Charon", NamedTextColor.RED),
                    Component.text(" > ", NamedTextColor.DARK_GRAY),
                    message
            ));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
        });
    }

    private void openReadyUpMenu(Player player, int page) {
        Menu menu = new Menu(name, 9 * 5);

        int startingIndex = (page - 1) * 5;
        int currentIndex = 0;
        List<WarlordsEntity> toDisplay = new ArrayList<>();
        for (Map.Entry<WarlordsEntity, Boolean> warlordsEntityBooleanEntry : readyPlayers.entrySet()) {
            WarlordsEntity warlordsEntity = warlordsEntityBooleanEntry.getKey();
            if (currentIndex >= startingIndex && currentIndex < startingIndex + 5) {
                toDisplay.add(warlordsEntity);
            }
            currentIndex++;
        }
        int relativeIndex = currentIndex % 5;
        for (int i = 0; i < toDisplay.size(); i++) {
            WarlordsEntity warlordsEntity = toDisplay.get(i);
            boolean ready = readyPlayers.get(warlordsEntity);
            int x = PLAYER_COUNT_MENU_POSITIONS.get(relativeIndex).get(i);
            menu.setItem(x, 1,
                    new ItemBuilder(HeadUtils.getHead(warlordsEntity.getUuid()))
                            .name(Component.text(warlordsEntity.getName(), NamedTextColor.AQUA))
                            .get(),
                    (m, e) -> {
                    }
            );
            ItemBuilder itemBuilder = new ItemBuilder(ready ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                    .name(Component.text(ready ? "Ready" : "Not Ready", ready ? NamedTextColor.GREEN : NamedTextColor.RED));
            if (warlordsEntity.getEntity().equals(player)) {
                itemBuilder.addLore(WordWrap.wrap(
                        Component.text("If all players are ready, the grace period will end.", NamedTextColor.GRAY),
                        160
                ));
                itemBuilder.addLore(
                        Component.empty(),
                        Component.text("Click to toggle ready status", NamedTextColor.YELLOW)
                );
            }
            menu.setItem(x, 2,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (!warlordsEntity.getEntity().equals(player)) {
                            return;
                        }
                        readyPlayers.put(warlordsEntity, !ready);
                        sendNPCMessage(Component.textOfChildren(
                                        Component.text(player.getName(), NamedTextColor.AQUA),
                                        Component.text(ready ? " is no longer ready" : " is now ready", ready ? NamedTextColor.RED : NamedTextColor.GREEN)
                                )
                        );
                        if (checkAllReady()) {
                            onAllReady();
                        } else {
                            // reopen/update for other players
                            game.forEachOnlinePlayer((p, team) -> {
                                if (!PlainTextComponentSerializer.plainText().serialize(p.getOpenInventory().title()).equals(name)) {
                                    return;
                                }
                                openReadyUpMenu(p, page);
                            });
                        }
                        player.closeInventory();
                    }
            );
        }

        if (page - 1 > 0) {
            menu.setItem(0, 2,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> {
                        openReadyUpMenu(player, page - 1);
                    }
            );
        }
        if (readyPlayers.size() > (page * 5)) {
            menu.setItem(8, 2,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> {
                        openReadyUpMenu(player, page + 1);
                    }
            );
        }

        menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
        DatabaseManager.getPlayer(player, databasePlayer -> {
            DatabasePlayerPvEEventGardenOfHesperidesStats gardenOfHesperidesStats = databasePlayer.getPveStats().getEventStats().getGardenOfHesperidesStats();
            boolean tartarusAutoReady = gardenOfHesperidesStats.isTartarusAutoReady();
            ItemBuilder itemBuilder = new ItemBuilder(Material.DIAMOND)
                    .name(Component.text("Auto Ready", NamedTextColor.AQUA))
                    .lore(
                            Component.text("Automatically ready up when joining", NamedTextColor.GRAY),
                            Component.empty(),
                            Component.text("Currently: ", NamedTextColor.GRAY)
                                     .append(Component.text(tartarusAutoReady ? "Enabled" : "Disabled", tartarusAutoReady ? NamedTextColor.GREEN : NamedTextColor.RED)),
                            Component.empty(),
                            Component.text("Click to toggle", NamedTextColor.YELLOW)
                    );
            if (tartarusAutoReady) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
            menu.setItem(5, 4,
                    itemBuilder.get(),
                    (m, e) -> {
                        gardenOfHesperidesStats.setTartarusAutoReady(!tartarusAutoReady);
                        openReadyUpMenu(player, page);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    }
            );
        });
        menu.openForPlayer(player);
    }

    public NPC getNpc() {
        return npc;
    }

    public Game getGame() {
        return game;
    }

    public static class ReadyUpTrait extends WarlordsTrait {

        private ReadyUpOption readyUpOption;

        public ReadyUpTrait() {
            super("ReadyUpTrait");
        }

        @Override
        public void run() {
            HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
            hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "FERRYMAN");
            hologramTrait.setLine(1, ChatColor.RED + "Charon");
        }

        @Override
        public void rightClick(NPCRightClickEvent event) {
            if (readyUpOption != null) {
                readyUpOption.openReadyUpMenu(event.getClicker(), 1);
            }
        }

        public void setReadyUpOption(ReadyUpOption readyUpOption) {
            this.readyUpOption = readyUpOption;
        }
    }

}
