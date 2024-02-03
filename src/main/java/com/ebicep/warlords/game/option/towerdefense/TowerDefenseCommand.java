package com.ebicep.warlords.game.option.towerdefense;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.towerdefense.towers.Tower;
import com.ebicep.warlords.game.option.towerdefense.towers.TowerRegistry;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

@CommandAlias("towerdefense|td")
@CommandPermission("group.administrator")
public class TowerDefenseCommand extends BaseCommand {

    @Subcommand("build")
    public void build(Player player, TowerRegistry tower) {
        tower.create.get().build(player.getLocation());
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
                List<Tower> builtTowers = towerBuildOption.getBuiltTowers();
                for (Tower builtTower : builtTowers) {
                    Block[][][] builtBlocks = builtTower.getBuiltBlocks();
                    for (Block[][] builtBlock : builtBlocks) {
                        for (Block[] blocks : builtBlock) {
                            for (Block block : blocks) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                    builtTower.onRemove();
                }
                builtTowers.clear();
            }
        }
        ChatChannels.sendDebugMessage(player, Component.text("Removed all towers", NamedTextColor.GREEN));
    }

    @Subcommand("reloadtowers")
    public void reloadTowers(CommandIssuer issuer) {
        EnumSet<TowerCache.Tower> updated = TowerCache.updateCaches();
        List<TowerCache.Tower> notUpdated = new ArrayList<>();
        for (TowerCache.Tower value : TowerCache.Tower.VALUES) {
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

}
