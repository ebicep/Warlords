package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.generalmenu.WarlordsShopMenu;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SwapSpecOption implements Option {

    private final Map<WarlordsEntity, Specializations> swappedSpecs = new HashMap<>();
    private Game game;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        game.registerEvents(new Listener() {

            @EventHandler
            public void onRespawnGive(WarlordsGiveRespawnEvent event) {
                updatePlayerInventory(event.getWarlordsEntity());
            }

            @EventHandler
            public void onInteract(InventoryClickEvent event) {
                HumanEntity clicked = event.getWhoClicked();
                WarlordsEntity warlordsEntity = Warlords.getPlayer(clicked);
                if (warlordsEntity == null) {
                    return;
                }
                if (warlordsEntity.isAlive()) {
                    return;
                }
                if (!warlordsEntity.getGame().equals(game)) {
                    return;
                }
                if (!Objects.equals(event.getClickedInventory(), clicked.getInventory())) {
                    return;
                }
                event.setCancelled(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (event.getSlot() == 22) {
                            openSpecMenu(warlordsEntity);
                        }
                    }
                }.runTaskLater(Warlords.getInstance(), 1);
            }

            @EventHandler
            public void onRespawn(WarlordsRespawnEvent event) {
                WarlordsEntity warlordsEntity = event.getWarlordsEntity();
                Specializations swappedSpec = swappedSpecs.get(warlordsEntity);
                if (swappedSpec == null) {
                    return;
                }
                try {
                    List<Float> oldCooldowns = warlordsEntity.getAbilities().stream().map(AbstractAbility::getCurrentCooldown).toList();
                    SkillBoosts skillBoost = PlayerSettings.getPlayerSettings(warlordsEntity.getUuid()).getSkillBoostForSpec(swappedSpec);
                    warlordsEntity.setSpec(swappedSpec, skillBoost);
                    for (int i = 0; i < warlordsEntity.getAbilities().size(); i++) {
                        AbstractAbility ability = warlordsEntity.getAbilities().get(i);
                        ability.setCurrentCooldown(oldCooldowns.get(i));
                    }
                } catch (Exception e) {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage("Problem changing specs");
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
                }
            }


        });
    }

    private void updatePlayerInventory(WarlordsEntity warlordsEntity) {
        if (!(warlordsEntity.getEntity() instanceof Player player)) {
            return;
        }

        player.getInventory().setItem(22, new ItemBuilder(Material.BOOK)
                .name(Component.text("Swap Specialization", NamedTextColor.GOLD))
                .addLore(WordWrap.wrap(Component.text("Click any spec to switch to that spec when you respawn. " +
                        "There can only be one type of each spec and at most 2 DPS/TANK/HEALER on the team at one time.", NamedTextColor.GRAY), 150))
                .addLore(Component.empty())
                .addLore(WordWrap.wrap(Component.text("The cooldowns of your abilities after you swap specs will match the cooldowns of your current abilities.",
                        NamedTextColor.GRAY
                ), 150))
                .get());
    }

    private void openSpecMenu(WarlordsEntity warlordsEntity) {
        if (!(warlordsEntity.getEntity() instanceof Player player)) {
            return;
        }
        Menu menu = new Menu("Swap Specialization", 6 * 9);

        player.getInventory().setItem(22, new ItemBuilder(Material.BOOK)
                .name(Component.text("Swap Specialization", NamedTextColor.GOLD))
                .lore(Component.empty())
                .addLore(WordWrap.wrap(Component.text("Click any spec to switch to that spec when you respawn. " +
                        "There can only be one type of each spec and at most 2 DPS/TANK/HEALER on the team at one time.", NamedTextColor.GRAY), 150))
                .addLore(Component.empty())
                .addLore(WordWrap.wrap(Component.text("The cooldowns of your abilities after you swap specs will match the cooldowns of your current abilities.",
                        NamedTextColor.GRAY
                ), 150))
                .get());


        for (int i = 0; i < Classes.VALUES.length; i++) {
            Classes classes = Classes.VALUES[i];
            menu.setItem((i < 3 ? 12 : 14) + ((i % 3) * 9),
                    new ItemBuilder(classes.item)
                            .name(Component.text(classes.name, NamedTextColor.GREEN))
                            .lore(Component.empty())
                            .addLore(WordWrap.wrap(Component.text(classes.description, NamedTextColor.GRAY), 150))
                            .get(),
                    (m, e) -> {}
            );
        }

        for (int i = 0; i < Specializations.VALUES.length; i++) {
            SpecChangeResult specChangeResult = canSwapToSpec(warlordsEntity, Specializations.VALUES[i]);
            Specializations spec = Specializations.VALUES[i];
            ItemBuilder itemBuilder = new ItemBuilder(specChangeResult == SpecChangeResult.CAN_CHANGE ? spec.specType.itemStack.getType() : Material.BARRIER)
                    .name(Component.text(spec.name, NamedTextColor.GREEN))
                    .lore();
            menu.setItem((i % 9) / 3 * 9 + (i < 9 ? 9 : 15) + (i % 3),
                    itemBuilder.get(),
                    (m, e) -> {
                        player.sendMessage(specChangeResult.getMessage(spec));
                        if (specChangeResult == SpecChangeResult.CAN_CHANGE) {
                            swappedSpecs.put(warlordsEntity, spec);
                            WarlordsShopMenu.openSkillBoostMenu(player, spec, m2 -> {
                                m2.setItem(3, 5, Menu.MENU_BACK, (m3, e3) -> openSpecMenu(warlordsEntity));
                                m2.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
                            });
                        }
                    }
            );
        }

        if (swappedSpecs.containsKey(warlordsEntity)) {
            menu.setItem(4, 2,
                    new ItemBuilder(Material.BEDROCK)
                            .name(Component.text("Swapped Spec: ", NamedTextColor.GREEN).append(Component.text(swappedSpecs.get(warlordsEntity).name, NamedTextColor.AQUA)))
                            .lore(
                                    Component.empty(),
                                    Component.text("Click to cancel swap", NamedTextColor.YELLOW)
                            )
                            .get(),
                    (m, e) -> {
                        player.sendMessage(Component.text("You cancelled your spec swap.", NamedTextColor.GREEN));
                        swappedSpecs.remove(warlordsEntity);
                        openSpecMenu(warlordsEntity);
                    }
            );
        }

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private SpecChangeResult canSwapToSpec(WarlordsEntity warlordsEntity, Specializations spec) {
        if (warlordsEntity.getSpecClass() == spec) {
            return SpecChangeResult.SAME_SPEC;
        }
        int sameSpecCount = 0;
        int sameSpecTypeCount = 0;
        List<Specializations> specs = game.warlordsPlayers()
                                          .filter(warlordsPlayer -> warlordsPlayer.isTeammate(warlordsEntity))
                                          .map(WarlordsEntity::getSpecClass)
                                          .collect(Collectors.toList());
        specs.addAll(swappedSpecs.entrySet().stream().filter(e -> e.getKey().isTeammate(warlordsEntity)).map(Map.Entry::getValue).toList());
        for (Specializations specializations : specs) {
            if (specializations == spec) {
                sameSpecCount++;
            }
            if (specializations.specType == spec.specType) {
                sameSpecTypeCount++;
            }
        }
        if (sameSpecCount > 0) {
            return SpecChangeResult.TOO_MANY_SAME_SPEC;
        }
        if (sameSpecTypeCount > 1) {
            return SpecChangeResult.TOO_MANY_SAME_SPEC_TYPE;
        }
        return SpecChangeResult.CAN_CHANGE;
    }

    enum SpecChangeResult {
        CAN_CHANGE {
            @Override
            public Component getMessage(Specializations spec) {
                return Component.text("You swapped your spec to ", NamedTextColor.GREEN)
                                .append(Component.text(spec.name, NamedTextColor.AQUA))
                                .append(Component.text("!"));
            }
        },
        SAME_SPEC {
            @Override
            public Component getMessage(Specializations spec) {
                return Component.text("You are already ", NamedTextColor.RED)
                                .append(Component.text(spec.specType.name, NamedTextColor.AQUA))
                                .append(Component.text("!"));
            }
        },
        TOO_MANY_SAME_SPEC {
            @Override
            public Component getMessage(Specializations spec) {
                return Component.text("Another player is already playing that specialization!", NamedTextColor.RED);
            }
        },
        TOO_MANY_SAME_SPEC_TYPE {
            @Override
            public Component getMessage(Specializations spec) {
                return Component.text("You can only have up to 2 of the same spec type (Damage/Tank/Healer)!", NamedTextColor.RED);
            }
        },
        ;

        public abstract Component getMessage(Specializations spec);
    }

}
