package com.ebicep.warlords.game.option.pvp.interception;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.SpawnpointOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.ComponentUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterceptionRespawnOption implements Option {

    private final Map<WarlordsEntity, Location> playerChosenSpawnLocations = new HashMap<>();
    private final List<SpawnpointOption.InterceptionSpawnPoint> spawnPoints = new ArrayList<>();

    @Override
    public void register(@Nonnull Game game) {
        for (Option option : game.getOptions()) {
            if (option instanceof SpawnpointOption.InterceptionSpawnPoint) {
                spawnPoints.add((SpawnpointOption.InterceptionSpawnPoint) option);
            }
        }
        game.registerEvents(new Listener() {

            @EventHandler
            public void onRespawnGive(WarlordsGiveRespawnEvent event) {
                updatePlayerInventory(event.getWarlordsEntity());
            }

            @EventHandler
            public void onRespawn(WarlordsRespawnEvent event) {
                Location respawnLocation = event.getRespawnLocation();
                Location chosenSpawnLocation = playerChosenSpawnLocations.get(event.getWarlordsEntity());
                if (chosenSpawnLocation == null) {
                    return;
                }
                respawnLocation.set(chosenSpawnLocation.getX(), chosenSpawnLocation.getY(), chosenSpawnLocation.getZ());
                respawnLocation.setPitch(chosenSpawnLocation.getPitch());
                respawnLocation.setYaw(chosenSpawnLocation.getYaw());
                playerChosenSpawnLocations.remove(event.getWarlordsEntity());
            }

            @EventHandler
            public void onInteract(InventoryClickEvent event) {
                HumanEntity clicked = event.getWhoClicked();
                WarlordsEntity warlordsEntity = Warlords.getPlayer(clicked);
                if (warlordsEntity == null) {
                    return;
                }
                if (!warlordsEntity.getGame().equals(game)) {
                    return;
                }
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null) {
                    return;
                }
                ItemMeta itemMeta = clickedItem.getItemMeta();
                if (itemMeta == null) {
                    return;
                }
                Component displayName = itemMeta.displayName();
                if (displayName == null) {
                    return;
                }
                for (SpawnpointOption.InterceptionSpawnPoint spawnPoint : spawnPoints) {
                    InterceptionPointOption interceptionPoint = spawnPoint.getInterceptionPoint();
                    if (!ComponentUtils.getFlattenedText(displayName).equals(interceptionPoint.getName())) {
                        continue;
                    }
                    if (!teamOwnsPoint(warlordsEntity, interceptionPoint)) {
                        warlordsEntity.sendMessage(Component.text("You can only spawn at points your team controls!", NamedTextColor.RED));
                        continue;
                    }
                    playerChosenSpawnLocations.put(warlordsEntity, spawnPoint.getLocation());
                    updatePlayerInventory(warlordsEntity);
                    warlordsEntity.sendMessage(Component.text("You selected to spawn at: ", NamedTextColor.YELLOW)
                                                        .append(Component.text(interceptionPoint.getName(), warlordsEntity.getTeam().getTeamColor(), TextDecoration.BOLD)));
                    break;
                }
            }

        });
    }

    public void updatePlayerInventory(WarlordsEntity warlordsEntity) {
        if (!(warlordsEntity.getEntity() instanceof Player player)) {
            return;
        }
        int start = 22;
        int numberOfPoints = spawnPoints.size();
        start -= numberOfPoints / 2;
        for (int i = 0; i < numberOfPoints; i++) {
            SpawnpointOption.InterceptionSpawnPoint interceptionSpawnPoint = spawnPoints.get(i);
            InterceptionPointOption interceptionPoint = interceptionSpawnPoint.getInterceptionPoint();
            ItemBuilder itemBuilder = new ItemBuilder(teamOwnsPoint(warlordsEntity, interceptionPoint) ? Material.GREEN_CONCRETE : Material.RED_CONCRETE)
                    .name(Component.text(interceptionPoint.getName(), NamedTextColor.GREEN));
            Location chosenSpawn = playerChosenSpawnLocations.get(warlordsEntity);
            if (chosenSpawn != null && chosenSpawn.equals(interceptionSpawnPoint.getLocation())) {
                itemBuilder.addLore(
                        Component.empty(),
                        Component.text("SELECTED", NamedTextColor.GREEN, TextDecoration.BOLD)
                );
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
            player.getInventory().setItem(start + i + (numberOfPoints % 2 == 0 && i > numberOfPoints / 2 ? 1 : 0), itemBuilder.get());
        }
    }

    private static boolean teamOwnsPoint(WarlordsEntity warlordsEntity, InterceptionPointOption interceptionPoint) {
        return interceptionPoint.getTeamOwning() == warlordsEntity.getTeam();
    }

}
