package com.ebicep.jda;

import com.ebicep.warlords.Warlords;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.security.auth.login.LoginException;
import java.util.Optional;


public class BotManager {

    public static JDA jda;
    public static String botToken;
    public static String compGamesServerID = "776590423501045760";
    public static int numberOfMessagesSentLast30Sec = 0;

    public static void connect() throws LoginException {
        if(botToken != null) {
            jda = JDABuilder.createDefault(botToken)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(new BotListener())
                    .build();
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if(numberOfMessagesSentLast30Sec > 0) {
                    numberOfMessagesSentLast30Sec--;
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 20, 70);
    }

    public static Guild getCompGamesServer() {
        return jda.getGuildById(compGamesServerID);
    }

    public static Optional<TextChannel> getTextChannelByName(String name) {
        return getCompGamesServer().getTextChannels().stream().filter(textChannel -> textChannel.getName().equalsIgnoreCase(name)).findFirst();
    }

    public static void sendMessageToNotificationChannel(String message) {
        if(numberOfMessagesSentLast30Sec > 15) {
            return;
        }
        getTextChannelByName("instant-updates").ifPresent(textChannel -> textChannel.sendMessage(message).queue());
    }

}
