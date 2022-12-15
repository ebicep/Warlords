package com.ebicep.warlords.game.option.wavedefense.events;

import com.ebicep.warlords.abilties.OrderOfEviscerate;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.HashMap;

public class SafeZoneOption implements Option {

    private final Material safeMaterial = Material.BEDROCK;
    private final int yLevel = 22;//0;
    private final int safeDuration = 15;
    private final int maxEnterableTimes = 3;
    private final HashMap<WarlordsPlayer, Integer> timesEntered = new HashMap<>();

    public SafeZoneOption() {

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
            System.out.println("WinAfterTimeoutOption not found");
            return;
        }

        new GameRunnable(game) {

            final EnumSet<Team> teams = TeamMarker.getTeams(game);

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
                            warlordsPlayer.sendMessage(ChatColor.RED + "You have already received the safe zone effect the maximum amount of times!");
                            return;
                        }
                        if (winAfterTimeoutOption.getTimeRemaining() <= 60) {
                            warlordsPlayer.sendMessage(ChatColor.RED + "You cannot receive safe zone effect if there is 60 seconds or less remaining!");
                            return;
                        }
                        timesEntered.put(warlordsPlayer, timesEntered.getOrDefault(warlordsPlayer, 0) + 1);
                        RegularCooldown<OrderOfEviscerate> cooldown = OrderOfEviscerate.giveCloak(warlordsPlayer, safeDuration, "SAFE");
                        cooldown.setOnRemoveForce(cooldownManager -> {
                            cooldownManager.removeCooldownNoForce(cooldown);
                            LivingEntity wpEntity = warlordsPlayer.getEntity();
                            if (wpEntity instanceof Player) {
                                PlayerFilter.playingGame(warlordsPlayer.getGame())
                                            .enemiesOf(warlordsPlayer)
                                            .stream().map(WarlordsEntity::getEntity)
                                            .filter(Player.class::isInstance)
                                            .map(Player.class::cast)
                                            .forEach(enemyPlayer -> enemyPlayer.showPlayer((Player) wpEntity));
                            }
                            warlordsPlayer.updateArmor();
                        });
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
            public void onAbilityActivate(WarlordsAbilityActivateEvent event) {
                WarlordsEntity player = event.getPlayer();
                if (player.getCooldownManager().hasCooldownFromActionBarName("SAFE")) {
                    player.sendMessage(ChatColor.RED + "You cannot use abilities while under the safe effect!");
                    event.setCancelled(true);
                }
            }

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                WarlordsEntity player = event.getPlayer();
                if (player.getCooldownManager().hasCooldownFromActionBarName("SAFE") ||
                        event.getAttacker().getCooldownManager().hasCooldownFromActionBarName("SAFE")
                ) {
                    event.setCancelled(true);
                }
            }

        });
    }

    public boolean isSafeZone(Location location) {
        return location.getWorld().getBlockAt(new LocationBuilder(location.clone()).y(yLevel)).getType() == safeMaterial;
    }

    public void sendEnterMessage(WarlordsPlayer warlordsPlayer) {
        if (warlordsPlayer.getEntity() instanceof Player) {
            ChatUtils.sendMessage(((Player) warlordsPlayer.getEntity()),
                    true,
                    ChatColor.GREEN + "You have entered the safe zone. " +
                            ChatColor.RED + (maxEnterableTimes - timesEntered.getOrDefault(warlordsPlayer, 0)) + " entries left."
            );
        }
    }

    public void sendExitMessage(WarlordsPlayer warlordsPlayer) {
        if (warlordsPlayer.getEntity() instanceof Player) {
            ChatUtils.sendMessage(((Player) warlordsPlayer.getEntity()),
                    true,
                    ChatColor.RED + "You have exited the safe zone. Your safe effect has been removed."
            );
        }
    }

}
