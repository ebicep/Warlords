package com.ebicep.warlords.game.option.towerdefense;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMobInfo;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.game.option.towerdefense.towers.TowerRegistry;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

@CommandAlias("towerdefense|td")
@CommandPermission("group.administrator")
public class TowerDefenseCommand extends BaseCommand {

    @Subcommand("build")
    public void build(@Conditions("requireGame:gamemode=TOWER_DEFENSE") Player player, TowerRegistry tower) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        Location location = player.getLocation();
        location.setYaw(0);
        tower.create.apply(game, player.getUniqueId(), location).build();
    }

    @Subcommand("exp")
    public void exp(@Conditions("requireGame:gamemode=TOWER_DEFENSE") Player player, Integer amount, @Optional WarlordsPlayer target) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (Option option : game.getOptions()) {
            if (option instanceof TowerDefenseOption towerDefenseOption) {
                towerDefenseOption.getPlayerInfo(target).setCurrentIncome(amount);
            }
        }
    }

    @Subcommand("debug")
    public void debug(@Conditions("requireGame:gamemode=TOWER_DEFENSE") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (Option option : game.getOptions()) {
            if (option instanceof TowerBuildOption towerBuildOption) {
                towerBuildOption.toggleDebug();
                ChatChannels.sendDebugMessage(player, Component.text("Debug: " + towerBuildOption.isDebug(), NamedTextColor.GREEN));
            }
        }
    }

    @Subcommand("removeall")
    public void removeAll(@Conditions("requireGame:gamemode=TOWER_DEFENSE") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (Option option : game.getOptions()) {
            if (option instanceof TowerBuildOption towerBuildOption) {
                Map<AbstractTower, Integer> builtTowers = towerBuildOption.getBuiltTowers();
                for (AbstractTower builtTower : builtTowers.keySet()) {
                    builtTower.remove();
                }
                builtTowers.clear();
            }
        }
        ChatChannels.sendDebugMessage(player, Component.text("Removed all towers", NamedTextColor.GREEN));
    }

    @Subcommand("reloadtowers")
    public void reloadTowers(CommandIssuer issuer) {
        EnumSet<TowerRegistry> updated = TowerRegistry.updateCaches();
        List<TowerRegistry> notUpdated = new ArrayList<>();
        for (TowerRegistry value : TowerRegistry.VALUES) {
            if (!updated.contains(value)) {
                notUpdated.add(value);
            }
        }
        ChatChannels.sendDebugMessage(issuer, Component.text("Updated: ", NamedTextColor.GREEN)
                                                       .append(updated.stream()
                                                                      .sorted(Comparator.comparing(Enum::ordinal))
                                                                      .map(tower -> Component.text(tower.name(), NamedTextColor.YELLOW))
                                                                      .collect(Component.toComponent(Component.text(", ", NamedTextColor.GRAY))))
        );
        if (!notUpdated.isEmpty()) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Not Updated: ", NamedTextColor.RED)
                                                           .append(notUpdated.stream()
                                                                             .map(tower -> Component.text(tower.name(), NamedTextColor.YELLOW))
                                                                             .collect(Component.toComponent(Component.text(", ", NamedTextColor.GRAY))))
            );
        }
    }

    @Subcommand("spawnmob")
    public void spawnMob(
            @Conditions("requireGame:gamemode=TOWER_DEFENSE") Player player,
            TowerDefenseMobInfo mob,
            @Default("1") @Conditions("limits:min=0,max=25") Integer amount,
            @Optional Team team
    ) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (Option option : game.getOptions()) {
            if (option instanceof TowerDefenseOption towerDefenseOption) {
                for (Team t : TeamMarker.getTeams(game)) {
                    if (t != team) {
                        continue;
                    }
                    for (int i = 0; i < amount; i++) {
                        towerDefenseOption.spawnNewMob(mob.getMob().createMob(towerDefenseOption.getRandomSpawnLocation(t)), (WarlordsEntity) null);
                    }
                    ChatChannels.sendDebugMessage(player, Component.text("Spawned " + amount + " Mobs", NamedTextColor.GREEN));
                }
                break;
            }
        }
    }

}
