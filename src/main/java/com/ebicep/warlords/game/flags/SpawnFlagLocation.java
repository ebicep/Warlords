package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.events.game.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Settings;
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

    @Nullable
    private final WarlordsEntity flagReturner;

    public SpawnFlagLocation(@Nonnull Location location, @Nullable WarlordsEntity flagReturner) {
        super(location);
        this.flagReturner = flagReturner;
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

    @Override
    public FlagLocation update(@Nonnull FlagInfo info) {
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
        NamedTextColor teamColor = eventTeam.teamColor();
        Component coloredPrefix = eventTeam.coloredPrefix();


        WarlordsEntity toucher = getFlagReturner();
        if (event.getOld() instanceof GroundFlagLocation) {
            if (toucher != null) {
                toucher.addFlagReturn();
                game.forEachOnlinePlayer((p, t) -> {
                    boolean sameTeam = t == eventTeam;
                    PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(p);
                    Component toucherColoredName = toucher.getColoredName();
                    Component flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                     .append(toucherColoredName)
                                                     .append(Component.text(" has returned the "))
                                                     .append(coloredPrefix)
                                                     .append(Component.text(" flag!"));
                    if (playerSettings.getFlagMessageMode() == Settings.FlagMessageMode.RELATIVE) {
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
                    PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(p);
                    if (playerSettings.getFlagMessageMode() == Settings.FlagMessageMode.RELATIVE) {
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
}
