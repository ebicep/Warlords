package com.ebicep.warlords.pve.consumables;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.AbstractWarlordsDropRewardEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropNewItemEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropWeaponEvent;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.consumables.menu.ConsumableMenu;
import com.ebicep.warlords.pve.consumables.vials.VialEffect;
import com.ebicep.warlords.pve.consumables.vials.VialManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ConsumableListener implements Listener {

    public ConsumableListener() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!DatabaseManager.inCache(player.getUniqueId(), PlayersCollections.LIFETIME)) {
                        continue;
                    }
                    DatabasePlayer databasePlayer = DatabaseManager.CACHED_PLAYERS
                            .get(PlayersCollections.LIFETIME)
                            .get(player.getUniqueId());
                    if (databasePlayer == null) {
                        continue;
                    }
                    cleanupAndNotify(player, databasePlayer);
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 20, 20);
    }

    public static void cleanupAndNotify(Player player, DatabasePlayer databasePlayer) {
        if (databasePlayer == null) {
            return;
        }
        List<ActiveConsumable> expired = databasePlayer.getPveStats().getConsumableManager().cleanupExpired();
        if (expired.isEmpty()) {
            return;
        }
        if (player != null && player.isOnline()) {
            for (ActiveConsumable activeConsumable : expired) {
                Consumable consumable = ConsumableRegistry.get(activeConsumable.getConsumableId());
                String name = consumable != null ? consumable.getName() : "vial";
                player.sendMessage(Component.text("Your ", NamedTextColor.RED)
                                            .append(Component.text(name, NamedTextColor.YELLOW))
                                            .append(Component.text(" has expired.", NamedTextColor.RED)));
            }
            if (ConsumableMenu.isVialInventoryOpen(player)) {
                ConsumableMenu.updateActiveVialsItem(player, databasePlayer.getPveStats().getConsumableManager());
            }
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!DatabaseManager.inCache(player.getUniqueId(), PlayersCollections.LIFETIME)) {
            return;
        }
        DatabasePlayer databasePlayer = DatabaseManager.CACHED_PLAYERS
                .get(PlayersCollections.LIFETIME)
                .get(player.getUniqueId());
        cleanupAndNotify(player, databasePlayer);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ConsumableMenu.cancelActiveVialsUpdateTask(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInsigniaGain(WarlordsAddCurrencyEvent event) {
        if (!(event.getWarlordsEntity() instanceof WarlordsPlayer warlordsPlayer)
                || !GameMode.isPvE(warlordsPlayer.getGame().getGameMode())) {
            return;
        }
        double multiplier = VialManager.getMultiplier(warlordsPlayer.getDatabasePlayer(), VialEffect.INSIGNIA_GAIN);
        event.setCurrencyToAdd((float) (event.getCurrencyToAdd() * multiplier));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeaponDrop(WarlordsDropWeaponEvent event) {
        applyDropMultiplier(event, VialEffect.WEAPON_DROP_RATE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNewItemDrop(WarlordsDropNewItemEvent event) {
        applyDropMultiplier(event, VialEffect.ITEM_DROP_RATE);
    }

    private void applyDropMultiplier(AbstractWarlordsDropRewardEvent event, VialEffect effect) {
        if (!(event.getWarlordsEntity() instanceof WarlordsPlayer warlordsPlayer)
                || !GameMode.isPvE(warlordsPlayer.getGame().getGameMode())) {
            return;
        }
        double multiplier = VialManager.getMultiplier(warlordsPlayer.getDatabasePlayer(), effect);
        event.setModifier(event.getModifier() * multiplier);
    }
}
