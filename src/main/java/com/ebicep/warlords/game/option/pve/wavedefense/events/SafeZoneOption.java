package com.ebicep.warlords.game.option.pve.wavedefense.events;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;

public class SafeZoneOption implements Option {

    private final Material safeMaterial = Material.BEDROCK;
    private final int yLevel = 20;//0;
    private final int safeDuration = 15;
    private final int maxEnterableTimes;
    private final HashMap<WarlordsPlayer, Integer> timesEntered = new HashMap<>();

    public SafeZoneOption() {
        this.maxEnterableTimes = 3;
    }

    public SafeZoneOption(int maxEnterableTimes) {
        this.maxEnterableTimes = maxEnterableTimes;
    }


    @Override
    public void start(@Nonnull Game game) {
        WinAfterTimeoutOption winAfterTimeoutOption = game.getOptions()
                                                          .stream()
                                                          .filter(option -> option instanceof WinAfterTimeoutOption)
                                                          .map(option -> (WinAfterTimeoutOption) option)
                                                          .findFirst()
                                                          .orElse(null);
        if (winAfterTimeoutOption == null) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("WinAfterTimeoutOption not found");
            return;
        }

        new GameRunnable(game) {

            @Override
            public void run() {
                game.warlordsPlayers().forEach(warlordsPlayer -> {
                    if (warlordsPlayer.isDead()) {
                        return;
                    }
                    if (!(warlordsPlayer.getEntity() instanceof Player)) {
                        return;
                    }
                    if (isSafeZone(warlordsPlayer.getLocation())) {
                        if (warlordsPlayer.getCooldownManager().hasCooldownFromActionBarName("SAFE")) {
                            return;
                        }
                        if (timesEntered.getOrDefault(warlordsPlayer, 0) >= maxEnterableTimes) {
                            ChatUtils.sendMessageToPlayer(warlordsPlayer,
                                    Component.text("You have already received the safe zone effect the maximum amount of times!", NamedTextColor.RED),
                                    NamedTextColor.GRAY,
                                    true
                            );
                            return;
                        }
                        if (winAfterTimeoutOption.getTimeRemaining() <= 60) {
                            ChatUtils.sendMessageToPlayer(warlordsPlayer,
                                    Component.text("You cannot receive safe zone effect if there is 60 seconds or less remaining!", NamedTextColor.RED),
                                    NamedTextColor.GRAY,
                                    true
                            );
                            return;
                        }
                        timesEntered.put(warlordsPlayer, timesEntered.getOrDefault(warlordsPlayer, 0) + 1);
                        giveSafeZoneEffect(warlordsPlayer);
                        sendEnterMessage(warlordsPlayer);
                    } else {
                        new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                                .filterNameActionBar("SAFE")
                                .findAny()
                                .ifPresent(regularCooldown -> {
                                    regularCooldown.setTicksLeft(0);
                                    sendExitMessage(warlordsPlayer);
                                });
                    }
                });
            }
        }.runTaskTimer(10 * 20, 10);

        game.registerEvents(new Listener() {

            @EventHandler
            public void onAbilityActivate(WarlordsAbilityActivateEvent.Pre event) {
                WarlordsEntity player = event.getWarlordsEntity();
                if (player.getCooldownManager().hasCooldownFromActionBarName("SAFE")) {
                    player.sendMessage(Component.text("You cannot use abilities while under the safe effect!", NamedTextColor.RED));
                    event.setCancelled(true);
                }
            }

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                WarlordsEntity player = event.getWarlordsEntity();
                if (player.getCooldownManager().hasCooldownFromActionBarName("SAFE") ||
                        event.getSource().getCooldownManager().hasCooldownFromActionBarName("SAFE")
                ) {
                    event.setCancelled(true);
                }
            }

        });
    }

    public boolean isSafeZone(Location location) {
        return location.getWorld().getBlockAt(new LocationBuilder(location.clone()).y(yLevel)).getType() == safeMaterial;
    }

    public void giveSafeZoneEffect(@Nonnull WarlordsEntity wp) {
        wp.getCooldownManager().removeCooldownByName("Safe Zone");
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Safe Zone",
                "SAFE",
                SafeZoneOption.class,
                null,
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                cooldownManager -> {
                    wp.removePotionEffect(PotionEffectType.INVISIBILITY);
                    wp.updateArmor();

                    Entity wpEntity = wp.getEntity();
                    if (wpEntity instanceof Player) {
                        PlayerFilter.playingGame(wp.getGame())
                                    .enemiesOf(wp)
                                    .stream()
                                    .map(WarlordsEntity::getEntity)
                                    .filter(Player.class::isInstance)
                                    .map(Player.class::cast)
                                    .forEach(enemyPlayer -> enemyPlayer.showPlayer(Warlords.getInstance(), (Player) wpEntity));
                    }
                },
                safeDuration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 5 == 0) {
                        wp.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticksLeft, 0, true, false));

                        Entity wpEntity = wp.getEntity();
                        if (wpEntity instanceof Player) {
                            ((Player) wpEntity).getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
                            PlayerFilter.playingGame(wp.getGame())
                                        .enemiesOf(wp)
                                        .stream()
                                        .map(WarlordsEntity::getEntity)
                                        .filter(Player.class::isInstance)
                                        .map(Player.class::cast)
                                        .forEach(enemyPlayer -> enemyPlayer.hidePlayer(Warlords.getInstance(), (Player) wpEntity));
                        }
                    }
                })
        ));
    }

    public void sendEnterMessage(WarlordsPlayer warlordsPlayer) {
        ChatUtils.sendMessageToPlayer(warlordsPlayer,
                Component.text("You have entered the safe zone. ", NamedTextColor.GREEN)
                         .append(Component.text((maxEnterableTimes - timesEntered.getOrDefault(warlordsPlayer, 0)) + " entries left.", NamedTextColor.RED)),
                NamedTextColor.GRAY,
                true
        );
    }

    public void sendExitMessage(WarlordsPlayer warlordsPlayer) {
        ChatUtils.sendMessageToPlayer(warlordsPlayer,
                Component.text("You have exited the safe zone.", NamedTextColor.RED)
                         .append(Component.newline())
                         .append(Component.text("Your safe effect has been removed.")),
                NamedTextColor.GRAY,
                true
        );
    }

}
