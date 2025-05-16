package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@CommandAlias("abilitystats")
@CommandPermission("group.administrator")
public class GetPlayerLastAbilityStatsCommand extends BaseCommand {

    public static final HashMap<UUID, List<Component>> PLAYER_LAST_ABILITY_STATS = new HashMap<>();

    @Default
    @CommandCompletion("@playerabilitystats")
    @Description("Prints the last ability stats of a player")
    public void getLastAbilityStats(Player player, @Values("@playerabilitystats") String name) {
        for (UUID uuid : PLAYER_LAST_ABILITY_STATS.keySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (Objects.equals(offlinePlayer.getName(), name)) {
                ChatUtils.sendCenteredMessage(player, Component.text("--------------------------------------------------", NamedTextColor.GREEN));
                ChatUtils.sendCenteredMessage(player, Component.text("Last ability stats for ", NamedTextColor.GREEN)
                                                               .append(Component.text(offlinePlayer.getName(), NamedTextColor.AQUA)));
                ChatUtils.sendCenteredMessage(player, Component.empty());
                sendLastAbilityStats(player, uuid);
                ChatUtils.sendCenteredMessage(player, Component.text("--------------------------------------------------", NamedTextColor.GREEN));
                return;
            }
        }
    }

    public static void sendLastAbilityStats(Player player, UUID uuid) {
        TextComponent.Builder formattedData = Component.text();
        List<Component> components = PLAYER_LAST_ABILITY_STATS.get(uuid);
        for (int i = 0; i < components.size(); i++) {
            if (i % 3 == 0 && i != 0) {
                ChatUtils.sendCenteredMessage(player, formattedData.build());
                formattedData = Component.text();
            }
            if (i % 3 == 1 || i % 3 == 2) {
                formattedData.append(ChatUtils.SPACER);
            }
            formattedData.append(components.get(i));
        }
        ChatUtils.sendCenteredMessage(player, formattedData.build());
    }

}
