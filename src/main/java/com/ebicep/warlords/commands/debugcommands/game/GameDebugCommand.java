package com.ebicep.warlords.commands.debugcommands.game;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.anomaly.Anomalies;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyRotation;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;

@CommandAlias("gamedebug|gd")
@CommandPermission("warlords.game.debuggame")
public class GameDebugCommand extends BaseCommand {

    @Default
    @Description("Auto creates debugparty and starts game in sandbox with players with timer skipped")
    public void gameDebug(@Conditions("outsideGame") Player player) {
        player.performCommand("p debugcreate");
        GameStartCommand.startGame(player, false, queueEntryBuilder -> {
                    queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
                    queueEntryBuilder.setGameMode(GameMode.DEBUG);
                    queueEntryBuilder.setMap(GameMap.DEBUG);
                    queueEntryBuilder.setOnResult((queueResult, game) -> game.getState(PreLobbyState.class).ifPresent(PreLobbyState::skipTimer));
                }
        );
    }

    @CommandAlias("gamedebug2|gd2")
    @Description("Auto starts game in wave defense with mobs not spawning")
    public void gameDebug2(@Conditions("outsideGame") Player player, @Optional Specializations spec) {
        GameStartCommand.startGame(player, false, queueEntryBuilder -> {
                    queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
                    queueEntryBuilder.setGameMode(GameMode.WAVE_DEFENSE);
                    queueEntryBuilder.setMap(GameMap.ILLUSION_CROSSFIRE);
                    queueEntryBuilder.setOnResult((queueResult, game) -> {
                        game.getState(PreLobbyState.class).ifPresent(PreLobbyState::skipTimer);
                        game.getOption(PveOption.class)
                            .stream()
                            .findFirst()
                            .ifPresent(pveOption -> pveOption.setPauseMobSpawn(true));
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                game.warlordsPlayers().forEach(warlordsPlayer -> {
                                    warlordsPlayer.setTakeDamage(false);
                                    warlordsPlayer.setDisableCooldowns(true);
                                    warlordsPlayer.setNoEnergyConsumption(true);
                                    warlordsPlayer.addCurrency(1000000);
                                    if (spec != null) {
                                        warlordsPlayer.setSpec(spec);
                                    }
                                });
                            }
                        }.runTaskLater(Warlords.getInstance(), 30);
                    });
                }
        );
    }

    @CommandAlias("gamedebug22|gd22")
    @Description("Auto starts game in wave defense with mobs not spawning")
    public void gameDebug2(@Conditions("outsideGame") Player player, Specializations spec, @Optional Integer branch) {
        GameStartCommand.startGame(player, false, queueEntryBuilder -> {
                    queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
                    queueEntryBuilder.setGameMode(GameMode.WAVE_DEFENSE);
                    queueEntryBuilder.setMap(GameMap.ILLUSION_CROSSFIRE);
                    queueEntryBuilder.setOnResult((queueResult, game) -> {
                        game.getState(PreLobbyState.class).ifPresent(PreLobbyState::skipTimer);
                        game.getOption(PveOption.class)
                            .stream()
                            .findFirst()
                            .ifPresent(pveOption -> pveOption.setPauseMobSpawn(true));
                        Integer branchNumber = branch;
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                game.warlordsPlayers().forEach(warlordsPlayer -> {
                                    warlordsPlayer.setTakeDamage(false);
                                    warlordsPlayer.setDisableCooldowns(true);
                                    warlordsPlayer.setNoEnergyConsumption(true);
                                    warlordsPlayer.addCurrency(1000000);
                                    warlordsPlayer.setSpec(spec);
                                    if (branchNumber != null) {
                                        AbstractUpgradeBranch<?> branch = warlordsPlayer.getAbilityTree().getUpgradeBranches().get(branchNumber);
                                        branch.purchaseMasterUpgrade(warlordsPlayer, branch.getMasterUpgrade2(), true, true);
                                    } else {
                                        for (int i = 0; i < 5; i++) {
                                            AbstractUpgradeBranch<?> branch = warlordsPlayer.getAbilityTree().getUpgradeBranches().get(i);
                                            branch.purchaseMasterUpgrade(warlordsPlayer, branch.getMasterUpgrade2(), true, true);
                                        }
                                    }
                                });
                            }
                        }.runTaskLater(Warlords.getInstance(), 30);

                    });
                }
        );
    }

    @CommandAlias("gamedebugsiege|gds")
    @Description("Auto starts siege game")
    public void gameDebugSiege(@Conditions("outsideGame") Player player) {
        GameStartCommand.startGame(player, false, queueEntryBuilder -> {
                    queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
                    queueEntryBuilder.setGameMode(GameMode.SIEGE);
                    queueEntryBuilder.setMap(GameMap.PAYLOAD2);
                    queueEntryBuilder.setOnResult((queueResult, game) -> {
                        game.getState(PreLobbyState.class).ifPresent(PreLobbyState::skipTimer);
                    });
                }
        );
    }

    @CommandAlias("gamedebugtd|gdtd")
    @Description("Auto starts tower defense game")
    public void gameDebugTD(@Conditions("outsideGame") Player player) {
        GameStartCommand.startGame(player, false, queueEntryBuilder -> {
                    queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
                    queueEntryBuilder.setGameMode(GameMode.TOWER_DEFENSE);
                    queueEntryBuilder.setMap(GameMap.TD_TEST);
                    queueEntryBuilder.setOnResult((queueResult, game) -> {
                        game.getState(PreLobbyState.class).ifPresent(PreLobbyState::skipTimer);
                    });
                }
        );
    }


    @CommandAlias("gamedebugevent|gde")
    @Description("Debug event game")
    public void gameDebugEvent(@Conditions("outsideGame") Player player, Integer mode) {
        GameMap map = mode == 1 ? GameMap.ACROPOLIS : mode == 2 ? GameMap.TARTARUS : null;
        if (map == null) {
            return;
        }
        GameStartCommand.startGame(player, false, queueEntryBuilder -> {
                    queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
                    queueEntryBuilder.setGameMode(GameMode.EVENT_WAVE_DEFENSE);
                    queueEntryBuilder.setMap(map);
                    queueEntryBuilder.setOnResult((queueResult, game) -> {
                        game.getState(PreLobbyState.class).ifPresent(PreLobbyState::skipTimer);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                game.warlordsPlayers().forEach(warlordsPlayer -> {
                                    warlordsPlayer.setTakeDamage(false);
                                    warlordsPlayer.setDisableCooldowns(true);
                                    warlordsPlayer.setNoEnergyConsumption(true);
                                    warlordsPlayer.addCurrency(1000000);
                                    warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                                            getName(),
                                            null,
                                            GameDebugCommand.class,
                                            null,
                                            warlordsPlayer,
                                            CooldownTypes.INTERNAL,
                                            cooldownManager -> {
                                            },
                                            false
                                    ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 100);
                                            }
                                    ));
                                });
                            }
                        }.runTaskLater(Warlords.getInstance(), 20);
                    });
                }
        );
    }

    @CommandAlias("gamedebugmole|gdm")
    @Description("Auto starts whack a mole game")
    public void gameDebugMole(@Conditions("outsideGame") Player player) {
        GameStartCommand.startGame(player, false, queueEntryBuilder -> {
                    queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
                    queueEntryBuilder.setGameMode(GameMode.WHACK_A_MOLE);
                    queueEntryBuilder.setMap(GameMap.MAIN_LOBBY_WHACK_A_MOLE);
                    queueEntryBuilder.setOnResult((queueResult, game) -> {
                        game.getState(PreLobbyState.class).ifPresent(PreLobbyState::skipTimer);
                    });
                }
        );
    }

    @CommandAlias("anomalytest|anomalyoverride")
    @CommandCompletion("OPEX_ANOMALY|BRIDGE_OF_DUNESTAR|WHAT_ONCE_WAS|clear")
    @Description("Temporarily overrides the active Anomaly for testing")
    public void anomalyTest(Player player, @Optional String anomalyInput) {
        if (anomalyInput == null || anomalyInput.isBlank()) {
            Anomalies active = AnomalyRotation.getCurrentAnomaly();
            sendDebugMessage(player, Component.text("Active Anomaly: ", NamedTextColor.GRAY)
                    .append(Component.text(active.getName(), NamedTextColor.GOLD)));
            sendDebugMessage(player, Component.text(
                    AnomalyRotation.hasTestAnomalyOverride()
                            ? "A test override is active. Use /anomalytest clear to restore hourly rotation."
                            : "Hourly rotation is active.",
                    NamedTextColor.YELLOW
            ));
            return;
        }

        if (anomalyInput.equalsIgnoreCase("clear") || anomalyInput.equalsIgnoreCase("reset")) {
            AnomalyRotation.clearTestAnomalyOverride();
            sendDebugMessage(player, Component.text("Cleared the Anomaly test override. Active Anomaly: ", NamedTextColor.GREEN)
                    .append(Component.text(AnomalyRotation.getCurrentAnomaly().getName(), NamedTextColor.GOLD)));
            return;
        }

        String normalizedInput = normalizeAnomalyName(anomalyInput);
        Anomalies anomaly = Arrays.stream(Anomalies.VALUES)
                .filter(value -> normalizeAnomalyName(value.name()).equals(normalizedInput)
                        || normalizeAnomalyName(value.getName()).equals(normalizedInput))
                .findFirst()
                .orElse(null);
        if (anomaly == null) {
            String available = Arrays.stream(Anomalies.VALUES)
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            sendDebugMessage(player, Component.text("Unknown Anomaly '" + anomalyInput + "'. Available: " + available, NamedTextColor.RED));
            return;
        }

        AnomalyRotation.setTestAnomalyOverride(anomaly);
        sendDebugMessage(player, Component.text("Anomaly test override set to ", NamedTextColor.GREEN)
                .append(Component.text(anomaly.getName(), NamedTextColor.GOLD))
                .append(Component.text(". New Anomaly games will use " + anomaly.getMap().getMapName() + ".", NamedTextColor.GREEN)));
    }

    private static String normalizeAnomalyName(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    @CommandAlias("endtimer")
    @Description("Sets timer of game to 00:01")
    public void endTimer(@Conditions("requireGame") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        game.getOption(WinAfterTimeoutOption.class)
            .stream()
            .findFirst()
            .ifPresent(winAfterTimeoutOption -> {
                winAfterTimeoutOption.setTimeRemaining(1);
                sendDebugMessage(player, Component.text("Set timer of game " + game.getGameId() + " to 00:01", NamedTextColor.GREEN));
            });
    }


}
