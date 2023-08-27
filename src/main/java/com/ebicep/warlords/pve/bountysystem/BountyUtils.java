package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.DifficultyIndex;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BountyUtils {

    public static final TextColor COLOR = TextColor.color(255, 140, 0);
    public static final Map<Currencies, Long> COST = new HashMap<>() {{
        put(Currencies.COIN, 5000L);
    }};
    public static Map<PlayersCollections, Integer> MAX_BOUNTIES = new HashMap<>() {{
        put(PlayersCollections.DAILY, 2);
        put(PlayersCollections.WEEKLY, 2);
        put(PlayersCollections.LIFETIME, Integer.MAX_VALUE);
    }};

    public static void sendBountyMessage(Player player, Component component) {
        player.sendMessage(Component.textOfChildren(
                Component.text("Bounties", COLOR), // dark orange
                Component.text(" > ", NamedTextColor.DARK_GRAY),
                component
        ));
    }

    public static boolean waveDefenseMatchesDifficulty(Game game, DifficultyIndex difficulty) {
        return game.getOptions().stream().anyMatch(option -> option instanceof WaveDefenseOption waveDefenseOption && waveDefenseOption.getDifficulty() == difficulty);
    }

    public static <T> Optional<T> getPvEOptionFromGame(Game game, Class<T> optionClass) {
        return game.getOptions()
                   .stream()
                   .filter(option -> optionClass.isAssignableFrom(option.getClass()))
                   .map(option -> (T) option)
                   .findFirst();
    }

}
