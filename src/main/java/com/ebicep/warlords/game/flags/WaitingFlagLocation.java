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

public class WaitingFlagLocation extends AbstractLocationBasedFlagLocation {

    private final WarlordsEntity scorer;
    private int ticksUntilSpawn;

    public WaitingFlagLocation(Location location, WarlordsEntity scorer) {
        super(location);
        this.ticksUntilSpawn = 15 * 20;
        this.scorer = scorer;
    }

    @Override
    public FlagLocation update(Game game, @Nonnull FlagInfo info) {
        this.ticksUntilSpawn--;
        return this.ticksUntilSpawn <= 0 ? new SpawnFlagLocation(info.getSpawnLocation(), null) : null;
    }

    @Nonnull
    @Override
    public List<TextComponent> getDebugInformation() {
        return Arrays.asList(
                Component.text("Type: " + this.getClass().getSimpleName()),
                Component.text("scorer: " + getScorer()),
                Component.text("ticksUntilSpawn: " + getTicksUntilSpawn())
        );
    }

    @Override
    public void onFlagUpdateEventNew(WarlordsFlagUpdatedEvent event) {
        Game game = event.getGame();
        Team eventTeam = event.getTeam();
        NamedTextColor teamColor = eventTeam.getTeamColor();
        Component coloredPrefix = eventTeam.coloredPrefix();

        WarlordsEntity player = getScorer();
        if (player != null) {
            player.addFlagCap();
            game.forEachOnlinePlayer((p, t) -> {
                DatabasePlayer databasePlayer = DatabaseManager.getPlayer(p);
                boolean sameTeam = t == eventTeam;
                Component coloredName = player.getColoredName();
                Component flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                 .append(coloredName)
                                                 .append(Component.text(" has captured the "))
                                                 .append(coloredPrefix)
                                                 .append(Component.text(" flag!"));
                if (databasePlayer.getFlagMessageMode() == FlagMessageMode.RELATIVE) {
                    if (sameTeam) {
                        flagMessage = Component.text("", NamedTextColor.YELLOW)
                                               .append(coloredName)
                                               .append(Component.text(" has captured "))
                                               .append(Component.text("YOUR", teamColor))
                                               .append(Component.text(" flag!"));
                    } else {
                        flagMessage = Component.text("", NamedTextColor.YELLOW)
                                               .append(coloredName)
                                               .append(Component.text(" has captured the "))
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

                if (t != null) {
                    if (sameTeam) {
                        p.playSound(player.getLocation(), "ctf.enemycapturedtheflag", 500, 1);
                    } else {
                        p.playSound(player.getLocation(), "ctf.enemyflagcaptured", 500, 1);
                    }
                }
            });
        }
    }

    @Nullable
    public WarlordsEntity getScorer() {
        return scorer;
    }

    public int getTicksUntilSpawn() {
        return ticksUntilSpawn;
    }

    @Deprecated
    public boolean wasWinner() {
        return scorer != null;
    }

}
