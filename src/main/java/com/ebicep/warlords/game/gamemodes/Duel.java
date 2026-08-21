package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.PlayerCooldownDisplayOption;
import com.ebicep.warlords.game.option.TextOption;
import com.ebicep.warlords.game.option.WeaponOption;
import com.ebicep.warlords.game.option.pvp.ApplySpecBoostsOption;
import com.ebicep.warlords.game.option.pvp.HorseOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.TriFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class Duel implements Mode {

    @Override
    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = Mode.super.initMap(map, loc, addons);
        Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
        options.add(TextOption.Type.CHAT_CENTERED.create(
                Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.empty(),
                base.append(Component.text("First player to kill their opponent")),
                base.append(Component.text("5 times wins the duel!")),
                Component.empty()
        ));
        options.add(TextOption.Type.TITLE.create(
                10,
                Component.text("GO!", NamedTextColor.GREEN)
        ));

        options.add(new WeaponOption());
        options.add(new ApplySpecBoostsOption(addons.contains(GameAddon.RANDOM_SPEC_BOOST)));
        options.add(new HorseOption());
        options.add(new PlayerCooldownDisplayOption());

        return options;
    }

    @Override
    public String getName() {
        return "Duel";
    }

    @Override
    public String getAbbreviation() {
        return "DUEL";
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.DIAMOND_SWORD);
    }

    @Override
    public TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> getCreateDatabaseGame() {
        return DatabaseGameDuel::new;
    }

    @Override
    public GamesCollections getGamesCollections() {
        return GamesCollections.DUEL;
    }

    @Override
    public int getMinPlayersToAddToDatabase() {
        return 2;
    }

}

