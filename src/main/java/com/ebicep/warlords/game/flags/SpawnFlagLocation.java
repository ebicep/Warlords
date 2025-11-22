package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.settings.FlagMessageMode;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class SpawnFlagLocation extends AbstractLocationBasedFlagLocation {

    private static final int INITIAL_MULTIPLIER_DECREASE = 20;
    private static final int MULTIPLIER_DECAY_PERIOD = 20; // ticks
    private static final int MULTIPLIER_DECAY_AMOUNT = 5;

    @Nullable
    private final WarlordsEntity flagReturner;
    private int ticksElapsed = 0;
    private int flagMultiplier;

    public SpawnFlagLocation(@Nonnull Location location, @Nullable WarlordsEntity flagReturner) {
        this(location, flagReturner, 0);
    }

    public SpawnFlagLocation(@Nonnull Location location, @Nullable WarlordsEntity flagReturner, int flagMultiplier) {
        super(location);
        this.flagReturner = flagReturner;
        this.flagMultiplier = flagMultiplier;
    }

    @Override
    public FlagLocation update(Game game, @Nonnull FlagInfo info) {
        this.ticksElapsed++;
//        if (ticksElapsed % MULTIPLIER_DECAY_PERIOD == 0) {
//            flagMultiplier = Math.max(0, flagMultiplier - MULTIPLIER_DECAY_AMOUNT);
//        }
        return null;
    }

    @Nonnull
    @Override
    public List<TextComponent> getDebugInformation() {
        return Arrays.asList(
                Component.text("Type: " + this.getClass().getSimpleName()),
                Component.text("lastToucher: " + flagReturner)
        );
    }

    @Override
    public void onFlagUpdateEventNew(WarlordsFlagUpdatedEvent event) {
        Game game = event.getGame();
        Team eventTeam = event.getTeam();
        NamedTextColor teamColor = eventTeam.getTeamColor();
        Component coloredPrefix = eventTeam.coloredPrefix();

        WarlordsEntity toucher = getFlagReturner();
        if (event.getOld() instanceof GroundFlagLocation) {
            if (toucher != null) {
                toucher.addFlagReturn();
                game.forEachOnlinePlayer((p, t) -> {
                    DatabasePlayer databasePlayer = DatabaseManager.getPlayer(p);
                    boolean sameTeam = t == eventTeam;
                    Component toucherColoredName = toucher.getColoredName();
                    Component flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                     .append(toucherColoredName)
                                                     .append(Component.text(" has returned the "))
                                                     .append(coloredPrefix)
                                                     .append(Component.text(" flag!"));
                    if (databasePlayer.getFlagMessageMode() == FlagMessageMode.RELATIVE) {
                        if (sameTeam) {
                            flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                   .append(toucherColoredName)
                                                   .append(Component.text(" has returned "))
                                                   .append(Component.text("YOUR", teamColor))
                                                   .append(Component.text(" flag!"));
                        } else {
                            flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                   .append(toucherColoredName)
                                                   .append(Component.text(" has returned the "))
                                                   .append(Component.text("ENEMY", teamColor))
                                                   .append(Component.text(" flag!"));
                        }
                    }
                    p.sendMessage(flagMessage);
                    p.showTitle(Title.title(
                            Component.empty(),
                            flagMessage,
                            Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0))
                    ));

                    if (sameTeam) {
                        p.playSound(p.getLocation(), "ctf.flagreturned", 500, 1);
                    }
                });
            } else {
                game.forEachOnlinePlayer((p, t) -> {
                    DatabasePlayer databasePlayer = DatabaseManager.getPlayer(p);
                    if (databasePlayer.getFlagMessageMode() == FlagMessageMode.RELATIVE) {
                        if (t == eventTeam) {
                            p.sendMessage(Component.text("", NamedTextColor.YELLOW)
                                                   .append(Component.text("YOUR", teamColor))
                                                   .append(Component.text(" flag has returned to base!"))
                            );
                        } else {
                            p.sendMessage(Component.text("The ", NamedTextColor.YELLOW)
                                                   .append(Component.text("ENEMY", teamColor))
                                                   .append(Component.text(" flag has returned to base!"))
                            );
                        }
                    } else {
                        p.sendMessage(Component.text("The ", NamedTextColor.YELLOW)
                                               .append(coloredPrefix)
                                               .append(Component.text(" flag has returned to base!"))
                        );
                    }
                });
            }
        }
    }

    /**
     * Get the player who returned the flag
     *
     * @return the flag returner, or null is the flag automatically moved back
     */
    @Nullable
    public WarlordsEntity getFlagReturner() {
        return flagReturner;
    }

    public int getFlagMultiplier() {
        return flagMultiplier;
    }

    public void setFlagMultiplier(int flagMultiplier) {
        this.flagMultiplier = flagMultiplier;
    }

}
