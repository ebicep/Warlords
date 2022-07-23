package com.ebicep.warlords.guilds;

import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class GuildManager {

    public static final List<Guild> GUILDS = new ArrayList<>();
    private static final Set<GuildInvite> INVITES = new HashSet<>();

    public static boolean addGuild(Guild guild) {
        if (GUILDS.stream().anyMatch(g -> g.getName().equalsIgnoreCase(guild.getName()))) {
            return false;
        }
        GUILDS.add(guild);
        return true;
    }

    public static Pair<Guild, GuildPlayer> getGuildAndGuildPlayerFromPlayer(Player player) {
        for (Guild guild : GUILDS) {
            if (guild.isDisbanded()) continue;
            for (GuildPlayer guildPlayer : guild.getPlayers()) {
                if (guildPlayer.getUUID().equals(player.getUniqueId())) {
                    return new Pair<>(guild, guildPlayer);
                }
            }
        }
        return null;
    }

    public static void addInvite(Player from, Player to, Guild guild) {
        INVITES.add(new GuildInvite(to.getUniqueId(), guild));
        ChatUtils.sendCenteredMessage(to, ChatColor.GREEN.toString() + ChatColor.BOLD + "------------------------------------------");
        ChatUtils.sendCenteredMessage(to, ChatColor.AQUA + from.getName() + ChatColor.YELLOW + " has invited you to join their guild!");
        TextComponent message = new TextComponent(ChatColor.YELLOW + "You have" + ChatColor.RED + " 5 " + ChatColor.YELLOW + "minutes to accept. " + ChatColor.GOLD + "Click here to join " + guild.getName());
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to join " + guild.getName()).create()));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guild join " + guild.getName()));
        ChatUtils.sendCenteredMessageWithEvents(to, Collections.singletonList(message));
        ChatUtils.sendCenteredMessage(to, ChatColor.GREEN.toString() + ChatColor.BOLD + "------------------------------------------");
    }

    public static Optional<Guild> getGuildFromInvite(Player player, String guildName) {
        return INVITES.stream()
                .filter(invite ->
                        invite.getUuid().equals(player.getUniqueId()) &&
                                invite.getGuild().getName().equalsIgnoreCase(guildName) &&
                                Instant.now().isBefore(invite.getExpiration()))
                .findFirst()
                .map(GuildInvite::getGuild);
    }

    public static Optional<Guild> getGuildFromName(String guildName) {
        return GUILDS.stream()
                .filter(guild -> !guild.isDisbanded() && guild.getName().equalsIgnoreCase(guildName))
                .findFirst();
    }

    static class GuildInvite {
        private final UUID uuid;
        private final Guild guild;
        private final Instant expiration = Instant.now().plus(5, ChronoUnit.MINUTES);

        public GuildInvite(UUID uuid, Guild guild) {
            this.uuid = uuid;
            this.guild = guild;
        }

        public UUID getUuid() {
            return uuid;
        }

        public Guild getGuild() {
            return guild;
        }

        public Instant getExpiration() {
            return expiration;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GuildInvite that = (GuildInvite) o;
            return uuid.equals(that.uuid) && guild.equals(that.guild);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uuid, guild);
        }
    }

}
