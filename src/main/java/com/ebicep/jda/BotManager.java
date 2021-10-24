package com.ebicep.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;


public class BotManager {

    public static JDA jda;
    public static String compGamesServerID = "776590423501045760";

    public static void connect() throws LoginException {
        try {
            File myObj = new File(System.getProperty("user.dir") + "/plugins/Warlords/botToken.TXT");
            Scanner myReader = new Scanner(myObj);
            if (myReader.hasNextLine()) {
                jda = JDABuilder.createDefault(myReader.nextLine())
                        .addEventListeners(new BotListener())
                        .build();
                myReader.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Guild getCompGamesServer() {
        return jda.getGuildById(compGamesServerID);
    }

    public static Optional<TextChannel> getTextChannelByName(String name) {
        return getCompGamesServer().getTextChannels().stream().filter(textChannel -> textChannel.getName().equalsIgnoreCase(name)).findFirst();
    }

    public static void sendMessageToNotificationChannel(String message) {
        getTextChannelByName("instant-updates").ifPresent(textChannel -> textChannel.sendMessage(message).queue());
    }

}
