package com.ebicep.jda;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.miscellaneouscommands.DiscordCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotListener extends ListenerAdapter implements Listener {

    private static BukkitTask onGoingBalance;

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (BotManager.DISCORD_SERVERS.stream().noneMatch(discordServer -> Objects.equals(discordServer.getServer(), event.getGuild()))) {
            return;
        }
        Member member = event.getMember();
        Message message = event.getMessage();
        switch (event.getChannelType()) {
            case PRIVATE -> parsePrivateLinkMessage(event, message);
            case TEXT -> readBalanceStatuses(event, message);
        }
    }

    private void parsePrivateLinkMessage(MessageReceivedEvent event, Message message) {
        if (event.getAuthor().isBot()) {
            return;
        }
        try {
            Long key = Long.parseLong(message.getContentRaw());
            if (DiscordCommand.playerLinkKeys.containsValue(key)) {
                UUID uuid = DiscordCommand.playerLinkKeys.getKey(key);
                DatabaseManager.updatePlayer(uuid, databasePlayer -> {
                    Long id = event.getAuthor().getIdLong();
                    databasePlayer.setDiscordID(id);
                    event.getPrivateChannel()
                         .sendMessage("You linked **" + Bukkit.getOfflinePlayer(uuid).getName() + "** to your discord account (" + id + ").")
                         .queue();
                    Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
                    if (player != null) {
                        player.sendMessage(Component.text(
                                "Your account was linked to the discord account " + event.getAuthor().getAsTag() + " (" + id + ").",
                                NamedTextColor.GREEN
                        ));
                    }

                    BotManager.sendDebugMessage(
                            new EmbedBuilder()
                                    .setColor(3066993)
                                    .setTitle("Player Linked - " + id)
                                    .setDescription("UUID: " + uuid + "\n" + "IGN: " + databasePlayer.getName() + "\n" + "KEY: " + key)
                                    .build()
                    );
                });
            }
        } catch (Exception e) {
            ChatUtils.MessageType.DISCORD_BOT.sendErrorMessage(e);
        }
    }

    private void readBalanceStatuses(MessageReceivedEvent event, Message message) {
        TextChannel textChannel = event.getTextChannel();
        switch (textChannel.getName().toLowerCase()) {
            case "gs-teams", "bot-teams" -> readOnGoingBalance(message);
            case "teams" -> readNewTeamPosted(message);
        }
    }

    private void readOnGoingBalance(Message message) {
        if (message.getContentRaw().contains(", Balance Cancelled")) {
            cancelOnGoingBalance();
        } else if (!message.getEmbeds().isEmpty() && message.getEmbeds().get(0).getFields().size() == 2) {
            cancelOnGoingBalance();
            MessageEmbed embed = message.getEmbeds().get(0);
            List<String> playerNames = new ArrayList<>();
            for (MessageEmbed.Field field : embed.getFields()) {
                String fieldName = field.getName();
                String fieldValue = field.getValue();
                if (fieldName != null && fieldValue != null) {
                    String[] players = fieldValue
                            .replace("```", "")
                            .replace(" ", "")
                            .split("\n");
                    if (fieldName.contains("Blue Team") || fieldName.contains("Red Team")) {
                        for (String player : players) {
                            playerNames.add(player.substring(0, player.indexOf('-')));
                        }
                    }
                }
            }
            onGoingBalance = new BukkitRunnable() {
                int counter = 0;

                @Override
                public void run() {
                    playerNames.forEach(name -> {
                        Player player = Bukkit.getPlayer(name);
                        if (player != null) {
                            Random random = new Random();
                            TextComponent subtitle = random.nextInt(2) == 0 ?
                                                     Component.text("BLUE", NamedTextColor.BLUE) :
                                                     Component.text("RED", NamedTextColor.RED);
                            player.showTitle(Title.title(
                                    Component.text(Utils.SPECS_ORDERED[random.nextInt(Utils.SPECS_ORDERED.length)], NamedTextColor.GREEN),
                                    subtitle,
                                    Title.Times.times(Ticks.duration(0), Ticks.duration(5), Ticks.duration(0))
                            ));
                        }
                    });
                    //auto cancel after 15 seconds
                    if (counter++ > 20 * 15) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(Warlords.getInstance(), 10, 0);

        }
    }

    private void readNewTeamPosted(Message message) {
        if (!message.getEmbeds().isEmpty() && message.getEmbeds().get(0).getFields().size() == 2) {
            cancelOnGoingBalance();
            MessageEmbed embed = message.getEmbeds().get(0);
            boolean isExperimental = embed.getTitle().contains("*");
            List<TeamBalance> blueTeam = new ArrayList<>();
            List<TeamBalance> redTeam = new ArrayList<>();
            for (MessageEmbed.Field field : embed.getFields()) {
                String fieldName = field.getName();
                String fieldValue = field.getValue();
                if (fieldName != null && fieldValue != null) {
                    for (String player : fieldValue.replace("```", "").split("\n")) {
                        String name = player.substring(0, player.indexOf('-'));
                        String spec = player.substring(player.indexOf('-') + 1);
                        if (fieldName.contains("Blue Team")) {
                            blueTeam.add(new TeamBalance(name, spec));
                        } else {
                            redTeam.add(new TeamBalance(name, spec));
                        }
                    }
                }
            }
            AtomicBoolean resetMenu = new AtomicBoolean(true);
            for (MessageEmbed.Field field : embed.getFields()) {
                String fieldName = field.getName();
                String fieldValue = field.getValue();
                String[] players;
                if (fieldName == null || fieldValue == null) {
                    continue;
                }
                boolean isBlueTeam = fieldName.contains("Blue Team");
                boolean isRedTeam = fieldName.contains("Red Team");
                players = fieldValue
                        .replace("```", "")
                        .replace(" ", "")
                        .split("\n");
                try {
                    Bukkit.getScheduler().callSyncMethod(Warlords.getInstance(), () -> {
                        for (String player : players) {
                            String name = player.substring(0, player.indexOf('-'));
                            String spec = player.substring(player.indexOf('-') + 1);
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(name);
                            if (offlinePlayer == null) {
                                continue;
                            }
                            Player targetPlayer = offlinePlayer.getPlayer();
                            UUID uuid = offlinePlayer.getUniqueId();
                            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(uuid);
                            if (resetMenu.get()) {
                                if (partyPlayerPair != null) {
                                    partyPlayerPair.getA().getRegularGamesMenu().reset();
                                }
                                resetMenu.set(false);
                            }
                            //includes offline players
                            if (isBlueTeam) {
                                PlayerSettings.getPlayerSettings(uuid).setWantedTeam(Team.BLUE);
                            } else if (isRedTeam) {
                                PlayerSettings.getPlayerSettings(uuid).setWantedTeam(Team.RED);
                            }
                            if (!spec.isEmpty()) {
                                PlayerSettings.getPlayerSettings(uuid).setSelectedSpec(Specializations.getSpecFromName(spec));
                                DatabaseManager.updatePlayer(uuid, databasePlayer -> {
                                    databasePlayer.setLastSpec(Specializations.getSpecFromName(spec));
                                });
                                if (!isExperimental) {
                                    if (partyPlayerPair != null) {
                                        partyPlayerPair.getA()
                                                       .getRegularGamesMenu()
                                                       .addPlayer(isBlueTeam ? Team.BLUE : Team.RED, uuid, Specializations.getSpecFromName(spec));
                                    }
                                }
                            } else {
                                if (!isExperimental) {
                                    if (partyPlayerPair != null) {
                                        partyPlayerPair.getA()
                                                       .getRegularGamesMenu()
                                                       .addPlayer(isBlueTeam ? Team.BLUE : Team.RED, uuid, Specializations.PYROMANCER);
                                    }
                                }
                            }
                            if (!isExperimental) {
                                if (partyPlayerPair != null && targetPlayer != null) {
                                    targetPlayer.getInventory().setItem(7,
                                            new ItemBuilder((isBlueTeam ? Team.BLUE : Team.RED).getWoolItem())
                                                    .name(Component.text("Team Builder", NamedTextColor.GREEN))
                                                    .get()
                                    );

                                }
                            }
                            //only send messages to online
                            if (targetPlayer != null) {
                                targetPlayer.sendMessage(Component.text("---------------------------------------", NamedTextColor.DARK_BLUE));
                                if (isBlueTeam) {
                                    targetPlayer.sendMessage(Component.text("You were automatically put into the ", NamedTextColor.GREEN)
                                                                      .append(Component.text("BLUE", NamedTextColor.BLUE, TextDecoration.BOLD))
                                                                      .append(Component.text(" team!", NamedTextColor.GREEN)));
                                } else if (isRedTeam) {
                                    targetPlayer.sendMessage(Component.text("You were automatically put into the ", NamedTextColor.GREEN)
                                                                      .append(Component.text("RED", NamedTextColor.RED, TextDecoration.BOLD))
                                                                      .append(Component.text(" team!", NamedTextColor.GREEN)));
                                }
                                if (!spec.isEmpty()) {
                                    TextComponent subtitle = isBlueTeam ? Component.text("BLUE", NamedTextColor.BLUE, TextDecoration.BOLD)
                                                                        : isRedTeam ? Component.text("RED", NamedTextColor.RED, TextDecoration.BOLD)
                                                                                    : Component.empty();
                                    targetPlayer.showTitle(Title.title(
                                            Component.text(spec, NamedTextColor.GREEN),
                                            subtitle,
                                            Title.Times.times(Ticks.duration(0), Ticks.duration(100), Ticks.duration(40))
                                    ));
                                    targetPlayer.sendMessage(Component.text("Your spec was automatically changed to ", NamedTextColor.GREEN)
                                                                      .append(Component.text(spec, NamedTextColor.YELLOW))
                                                                      .append(Component.text("!", NamedTextColor.GREEN)));
                                }
                                targetPlayer.sendMessage("");
                                targetPlayer.sendMessage(Component.text("Blue Team", NamedTextColor.DARK_BLUE).append(Component.text(" - ", NamedTextColor.DARK_GRAY)));
                                blueTeam.forEach(s -> {
                                    targetPlayer.sendMessage(Component.text(s.name, s.name.contains(name) ? NamedTextColor.GREEN : NamedTextColor.BLUE)
                                                                      .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                      .append(Component.text(s.spec, NamedTextColor.YELLOW)));
                                });
                                targetPlayer.sendMessage(Component.text("Red Team", NamedTextColor.DARK_RED).append(Component.text(" - ", NamedTextColor.DARK_GRAY)));
                                redTeam.forEach(s -> {
                                    targetPlayer.sendMessage(Component.text(s.name, s.name.contains(name) ? NamedTextColor.GREEN : NamedTextColor.RED)
                                                                      .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                      .append(Component.text(s.spec, NamedTextColor.YELLOW)));
                                });
                                targetPlayer.sendMessage(Component.text("---------------------------------------", NamedTextColor.DARK_BLUE));
                            }
                        }
                        return null;
                    }).get();
                } catch (InterruptedException | ExecutionException e) {
                    ChatUtils.MessageType.DISCORD_BOT.sendErrorMessage(e);
                }
            }
        }
    }

    private static void cancelOnGoingBalance() {
        if (onGoingBalance != null) {
            onGoingBalance.cancel();
        }
    }

    private record TeamBalance(String name, String spec) {

    }

}
