package com.ebicep.warlords.commands.debugcommands.game;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameManager.GameHolder;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.util.java.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.OptionalInt;

import static com.ebicep.warlords.util.java.StringUtils.toTitleHumanCase;

@CommandAlias("gamelist")
@CommandPermission("warlords.game.list")
public class GameListCommand extends BaseCommand {

    @Default
    @Description("Lists all games")
    public void listGames(CommandIssuer issuer) {
        for (GameHolder holder : Warlords.getGameManager().getGames()) {
            TextComponent.Builder message = Component.empty().color(NamedTextColor.GRAY)
                                                     .append(Component.text("["))
                                                     .append(Component.text(holder.getName(), NamedTextColor.AQUA))
                                                     .append(Component.text("|"))
                                                     .append(Component.text(toTitleHumanCase(holder.getMap().getMapName()), NamedTextColor.AQUA))
                                                     .append(Component.text("]"))
                                                     .toBuilder();
            Game game = holder.getGame();
            if (game == null) {
                message.append(Component.text("]"))
                       .append(Component.text(" <inactive>", NamedTextColor.GOLD));
            } else {
                if (holder.getMap().getGameModes().size() > 1) {
                    message.append(Component.text("/"))
                           .append(Component.text(toTitleHumanCase(game.getGameMode()), NamedTextColor.AQUA));
                }
                message.append(Component.text("]"));
                EnumSet<GameAddon> addons = game.getAddons();
                if (!addons.isEmpty()) {
                    message.append(Component.text("("));
                    for (GameAddon addon : addons) {
                        message.append(Component.text(addon.name(), NamedTextColor.GREEN));
                        message.append(Component.text(", "));
                    }
                    message.append(Component.text(") "));
                }
                message.append(Component.text(game.getState().getClass().getSimpleName(), NamedTextColor.GOLD))
                       .append(Component.text("["))
                       .append(Component.text(game.getPlayers().size(), NamedTextColor.GREEN))
                       .append(Component.text("/"))
                       .append(Component.text(game.getMinPlayers(), NamedTextColor.GREEN))
                       .append(Component.text(".."))
                       .append(Component.text(game.getMaxPlayers(), NamedTextColor.GREEN))
                       .append(Component.text("] "));
                OptionalInt timeLeft = WinAfterTimeoutOption.getTimeRemaining(game);
                String time = StringUtils.formatTimeLeft(timeLeft.isPresent() ? timeLeft.getAsInt() : (System.currentTimeMillis() - game.createdAt()) / 1000);
                String word = timeLeft.isPresent() ? " Left" : " Elapsed";
                message.append(Component.text(time))
                       .append(Component.text(word));
            }
            if (issuer.getIssuer() instanceof Player player) {
                player.sendMessage(message.build());
            } else {
                issuer.sendMessage(PlainTextComponentSerializer.plainText().serialize(message.build()));
            }
        }
    }

}