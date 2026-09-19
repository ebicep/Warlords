package com.ebicep.warlords.player.general;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.GuildTag;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Locale;

/**
 * Shared lobby list identity for nametag scoreboard teams and custom tab rows:
 * rank → guild → name sort, prefix without name, guild tag suffix.
 */
public record LobbyPlayerIdentity(
        @Nonnull String name,
        @Nonnull Permissions rank,
        @Nonnull String guildName,
        @Nonnull Component prefix,
        @Nonnull Component suffix,
        @Nonnull NamedTextColor color
) {

    /**
     * Sortable key: rank ordinal, guild name ({@code ~} if unguilded), player name.
     */
    @Nonnull
    public static String sortKey(@Nonnull Permissions rank, @Nonnull String guildName, @Nonnull String playerName) {
        String guildKey = guildName.isEmpty()
                ? "~"
                : guildName.toLowerCase(Locale.ROOT);
        return rank.ordinal() + "_" + guildKey + "_" + playerName;
    }

    @Nonnull
    public String sortKey() {
        return sortKey(rank, guildName, name);
    }

    /**
     * Tab display name: prefix (honorific + rank) + colored name + guild tag suffix.
     */
    @Nonnull
    public Component tabDisplayName() {
        return prefix.append(Component.text(name, color)).append(suffix);
    }

    @Nonnull
    public static LobbyPlayerIdentity from(@Nonnull Player player) {
        Component suffix = Component.empty();
        String guildName = "";
        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player.getUniqueId());
        if (guildPlayerPair != null) {
            Guild guild = guildPlayerPair.getA();
            guildName = guild.getName();
            if (guild.getTag() != null) {
                GuildTag tag = guild.getTag();
                suffix = Component.space().append(tag.getTag(false)).compact();
            }
        }
        Permissions rank = Permissions.getPermission(player);
        return new LobbyPlayerIdentity(
                player.getName(),
                rank,
                guildName,
                Permissions.getPrefixWithColor(player, false).compact(),
                suffix,
                rank.prefixColor
        );
    }
}
