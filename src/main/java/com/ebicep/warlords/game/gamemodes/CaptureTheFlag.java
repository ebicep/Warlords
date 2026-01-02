package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.cuboid.GateOption;
import com.ebicep.warlords.game.option.pvp.ApplySpecBoostsOption;
import com.ebicep.warlords.game.option.pvp.FlagGlowOption;
import com.ebicep.warlords.game.option.pvp.HorseOption;
import com.ebicep.warlords.game.option.pvp.ctf.FlagOption;
import com.ebicep.warlords.game.option.respawn.NoRespawnIfOfflineOption;
import com.ebicep.warlords.game.option.respawn.RespawnSpawnDamageOption;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.TriFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class CaptureTheFlag implements Mode {

    @Override
    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = Mode.super.initMap(map, loc, addons);

        Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
        options.add(TextOption.Type.CHAT_CENTERED.create(
                Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.empty(),
                base.append(Component.text("Steal and capture the enemy team's flag to")),
                base.append(Component.text("earn "))
                    .append(Component.text("250 ", NamedTextColor.AQUA, TextDecoration.BOLD))
                    .append(base.append(Component.text("points! The first team with a"))),
                base.append(Component.text("score of "))
                    .append(Component.text("1000 ", NamedTextColor.AQUA, TextDecoration.BOLD))
                    .append(base.append(Component.text("wins!"))),
                Component.empty()
        ));
        options.add(TextOption.Type.TITLE.create(
                ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "ctf.gateDelaySeconds", int.class, GateOption.DEFAULT_GATE_DELAY),
                Component.text("GO!", NamedTextColor.GREEN),
                Component.text("Steal and capture the enemy flag!", NamedTextColor.YELLOW)
        ));
        options.add(CompassOption.flagOption());
        options.add(new FlagOption());
        options.add(new NoRespawnIfOfflineOption());
        options.add(new WeaponOption());
        options.add(new ApplySpecBoostsOption(addons.contains(GameAddon.RANDOM_SPEC_BOOST)));
        options.add(new HorseOption());
        options.add(new FlagGlowOption());
        options.add(new PlayerCooldownDisplayOption());
        options.add(new RespawnWaveOption(
                ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "ctf.respawnWaveDelayTicks", int.class, RespawnWaveOption.DEFAULT_INITIAL_DELAY),
                ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "ctf.respawnWavePeriodSeconds", int.class, RespawnWaveOption.DEFAULT_TASK_PERIOD),
                ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "ctf.respawnWaveMinTimerSeconds", int.class, RespawnWaveOption.DEFAULT_MIN_RESPAWN_TIMER)
        ));
        options.add(new RespawnSpawnDamageOption(
                ConfigManager.getGameConfigValue(
                        ConfigManager.DEFAULT_NAMESPACES,
                        "ctf.spawnDamageTickDuration",
                        Integer.class
                ),
                AbstractAbility.convertToMultiplicationDecimal(
                        ConfigManager.getGameConfigValue(
                                ConfigManager.DEFAULT_NAMESPACES,
                                "ctf.spawnDamageBoost",
                                Float.class
                        ))
        ));

        return options;
    }

    @Override
    public List<Option> postMapModifyOptions(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons, List<Option> options) {
        int additionalCooldown = ConfigManager.getGameConfigValue(
                ConfigManager.DEFAULT_NAMESPACES,
                "ctf.additionalPowerupCooldownSeconds",
                int.class,
                0
        );
        for (Option option : options) {
            if (option instanceof PowerupOption powerupOption) {
                powerupOption.setCurrentCooldown(powerupOption.getCurrentCooldown() + additionalCooldown);
            }
        }
        return options;
    }

    @Override
    public String getName() {
        return "Capture The Flag";
    }

    @Override
    public String getAbbreviation() {
        return "CTF";
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.BLACK_BANNER);
    }

    @Override
    public TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> getCreateDatabaseGame() {
        return DatabaseGameCTF::new;
    }

    @Override
    public GamesCollections getGamesCollections() {
        return GamesCollections.CTF;
    }

    @Override
    public int getMinPlayersToAddToDatabase() {
        return 16;
    }

}
