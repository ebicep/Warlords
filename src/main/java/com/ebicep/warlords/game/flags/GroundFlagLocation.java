/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.game.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.Settings;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class GroundFlagLocation extends AbstractLocationBasedFlagLocation implements FlagLocation {
	
    int damageTimer;
    int despawnTimer;

    public GroundFlagLocation(Location location, int damageTimer) {
        super(location);
        this.damageTimer = damageTimer;
        this.despawnTimer = 15 * 20;
    }

    public GroundFlagLocation(PlayerFlagLocation playerFlagLocation) {
        this(playerFlagLocation.getLocation(), playerFlagLocation.getPlayer().isDead() ? playerFlagLocation.getPickUpTicks() + 600 : playerFlagLocation.getPickUpTicks());
    }

    @Nonnull
    @Override
    public Location getLocation() {
        return location;
    }

    public int getDamageTimer() {
        return damageTimer;
    }

    public int getDespawnTimer() {
        return despawnTimer;
    }

    public int getDespawnTimerSeconds() {
        return this.despawnTimer / 20;
    }

    @Override
    public FlagLocation update(@Nonnull FlagInfo info) {
        this.despawnTimer--;
        this.damageTimer++;
        return this.despawnTimer <= 0 ? new SpawnFlagLocation(info.getSpawnLocation(), null) : null;
    }

    @Nonnull
    @Override
    public List<TextComponent> getDebugInformation() {
        return Arrays.asList(
                Component.text("Type: " + this.getClass().getSimpleName()),
                Component.text("Despawn ticks: " + getDespawnTimer()),
                Component.text("Despawn seconds: " + getDespawnTimerSeconds()),
                Component.text("damageTimer: " + getDamageTimer())
        );
    }

    public static GroundFlagLocation of(@Nonnull FlagLocation flag) {
        return flag instanceof PlayerFlagLocation ? new GroundFlagLocation((PlayerFlagLocation) flag)
                                                  : new GroundFlagLocation(flag.getLocation(), 0);
    }

    @Override
    public void onFlagUpdateEventNew(WarlordsFlagUpdatedEvent event) {
        Game game = event.getGame();
        Team eventTeam = event.getTeam();
        NamedTextColor teamColor = eventTeam.getTeamColor();
        Component coloredPrefix = eventTeam.coloredPrefix();

        if (event.getOld() instanceof PlayerFlagLocation pfl) {
            pfl.getPlayer().updateArmor();
            if (pfl.getPlayer() instanceof WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.queueUpdateTabName();
            }
            game.forEachOnlinePlayer((p, t) -> DatabaseManager.getPlayer(p.getUniqueId(), databasePlayer -> {
                Component coloredName = pfl.getPlayer().getColoredName();
                Component flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                 .append(coloredName)
                                                 .append(Component.text(" has dropped the "))
                                                 .append(coloredPrefix)
                                                 .append(Component.text(" flag!"));
                if (databasePlayer.getFlagMessageMode() == Settings.FlagMessageMode.RELATIVE) {
                    if (t == eventTeam) {
                        flagMessage = Component.text("", NamedTextColor.YELLOW)
                                               .append(coloredName)
                                               .append(Component.text(" has dropped "))
                                               .append(Component.text("YOUR", teamColor))
                                               .append(Component.text(" flag!"));
                    } else {
                        flagMessage = Component.text("", NamedTextColor.YELLOW)
                                               .append(coloredName)
                                               .append(Component.text(" has dropped the "))
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
            }));
        }
    }
}
