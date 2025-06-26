package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.abilities.OrderOfEviscerate;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.game.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pvp.FlagSpawnPointOption;
import com.ebicep.warlords.player.general.settings.FlagMessageMode;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class PlayerFlagLocation implements FlagLocation {

    public static int getIncreaseDelay() {
        return ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "ctf.flagPercentageIncreaseTickDelay", int.class);
    }

    public static PlayerFlagLocation of(@Nonnull FlagLocation flag, WarlordsEntity player) {
        return flag instanceof GroundFlagLocation groundFlagLocation ?
               new PlayerFlagLocation(player, groundFlagLocation.getTicksElapsed(), groundFlagLocation.getFlagMultiplier()) :
               new PlayerFlagLocation(player, 0, 0);
    }

    private final WarlordsEntity player;
    private int ticksElapsed;
    private int flagMultiplier;

    public PlayerFlagLocation(WarlordsEntity player, int ticksElapsed, int flagMultiplier) {
        this.player = player;
        this.ticksElapsed = ticksElapsed;
        this.flagMultiplier = Math.min(flagMultiplier, getMaxMultiplier());
    }

    private static Integer getMaxMultiplier() {
        return ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "ctf.flagMultiplierCap", int.class, 1000);
    }

    @Nonnull
    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public FlagLocation update(Game game, @Nonnull FlagInfo info) {
        this.ticksElapsed++;
        if (ticksElapsed >= getIncreaseDelay() && ticksElapsed % FlagSpawnPointOption.FLAG_MULTIPLIER_PERIOD == 0 && flagMultiplier < getMaxMultiplier()) {
            flagMultiplier += 1;
        }
        return null;
    }

    @Nonnull
    @Override
    public List<TextComponent> getDebugInformation() {
        return Arrays.asList(
                Component.text("Type: " + this.getClass().getSimpleName()),
                Component.text("Player: " + this.getPlayer().getName()),
                Component.text("pickUpTicks: " + getTicksElapsed()),
                Component.text("pickUpTicks / 20: " + getTicksElapsed() / 20),
                Component.text("Multiplier: +" + getFlagMultiplier() + "%")
        );
    }

    @Override
    public void onFlagUpdateEventNew(WarlordsFlagUpdatedEvent event) {
        Game game = event.getGame();
        Team eventTeam = event.getTeam();
        NamedTextColor teamColor = eventTeam.getTeamColor();
        Component coloredPrefix = eventTeam.coloredPrefix();

        player.setCarriedFlag(event.getInfo());
        //removing invis for assassins
        OrderOfEviscerate.removeCloak(player, false);
        if (event.getOld() instanceof PlayerFlagLocation oldPlayerFlagLocation) {
            // PLAYER -> PLAYER only happens if the multiplier gets to a new scale
            int computedHumanMultiplier = getFlagMultiplier();
            if (computedHumanMultiplier % 10 == 0) {
                game.forEachOnlinePlayer((p, t) -> DatabaseManager.getPlayer(p.getUniqueId(), databasePlayer -> {
                            if (t != null && databasePlayer.getFlagMessageMode() == FlagMessageMode.RELATIVE) {
                                NamedTextColor playerColor = getPlayer().getTeam().getTeamColor();
                                if (t != eventTeam) {
                                    p.sendMessage(Component.text("", NamedTextColor.YELLOW)
                                                           .append(Component.text("YOUR", playerColor))
                                                           .append(Component.text(" flag carrier now takes "))
                                                           .append(Component.text(computedHumanMultiplier + "%", NamedTextColor.RED))
                                                           .append(Component.text(" increased damage!"))
                                    );
                                } else {
                                    p.sendMessage(Component.text("The ", NamedTextColor.YELLOW)
                                                           .append(Component.text("ENEMY", playerColor))
                                                           .append(Component.text(" flag carrier now takes "))
                                                           .append(Component.text(computedHumanMultiplier + "%", NamedTextColor.RED))
                                                           .append(Component.text(" increased damage!"))
                                    );
                                }
                            } else {
                                p.sendMessage(Component.text("The ", NamedTextColor.YELLOW)
                                                       .append(coloredPrefix)
                                                       .append(Component.text(" flag carrier now takes "))
                                                       .append(Component.text(computedHumanMultiplier + "%", NamedTextColor.RED))
                                                       .append(Component.text(" increased damage!"))
                                );
                            }
                        }
                ));
            }
        } else {
            // eg GROUND -> PLAYER
            // or SPAWN -> PLAYER
            game.forEachOnlinePlayer((p, t) -> DatabaseManager.getPlayer(p.getUniqueId(), databasePlayer -> {
                        Component playerColoredName = player.getColoredName();
                        Component flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                         .append(playerColoredName)
                                                         .append(Component.text(" picked up the "))
                                                         .append(coloredPrefix)
                                                         .append(Component.text(" §eflag!"));
                        if (t != null) {
                            if (t == eventTeam) {
                                p.playSound(player.getLocation(), "ctf.friendlyflagtaken", 500, 1);
                                if (databasePlayer.getFlagMessageMode() == FlagMessageMode.RELATIVE) {
                                    flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                           .append(playerColoredName)
                                                           .append(Component.text(" picked up "))
                                                           .append(Component.text("YOUR", teamColor))
                                                           .append(Component.text(" flag!"));
                                }
                            } else {
                                p.playSound(player.getLocation(), "ctf.enemyflagtaken", 500, 1);
                                if (databasePlayer.getFlagMessageMode() == FlagMessageMode.RELATIVE) {
                                    flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                           .append(playerColoredName)
                                                           .append(Component.text(" picked up the "))
                                                           .append(Component.text("ENEMY", teamColor))
                                                           .append(Component.text(" flag!"));
                                }
                            }
                        }
                        p.sendMessage(flagMessage);
                        p.showTitle(Title.title(
                                Component.empty(),
                                flagMessage,
                                Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0))
                        ));

                    }
            ));
        }
    }

    public int getFlagMultiplier() {
        return flagMultiplier;
    }

    public WarlordsEntity getPlayer() {
        return player;
    }

    public void setFlagMultiplier(int flagMultiplier) {
        this.flagMultiplier = flagMultiplier;
    }

    @Override
    public void onFlagUpdateEventOld(WarlordsFlagUpdatedEvent event) {
        player.setCarriedFlag(null);
    }

    public int getTicksElapsed() {
        return ticksElapsed;
    }

    public double getComputedMultiplier() {
        return 1 + flagMultiplier * 0.01;
    }

}
