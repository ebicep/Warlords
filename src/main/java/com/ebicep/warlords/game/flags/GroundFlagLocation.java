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
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.game.option.pvp.FlagSpawnPointOption;
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

    private int ticksElapsed;
    private int flagMultiplier;
    private int despawnTicks;
    private int repickTickCooldown;
    private final boolean manuallyDropped;

    public GroundFlagLocation(Location location, int ticksElapsed, int flagMultiplier, boolean manuallyDropped) {
        super(location);
        this.ticksElapsed = ticksElapsed;
        this.flagMultiplier = flagMultiplier;
        this.despawnTicks = 15 * 20;
        this.repickTickCooldown = manuallyDropped ? 0 : 10;
        this.manuallyDropped = manuallyDropped;
    }

    public GroundFlagLocation(PlayerFlagLocation playerFlagLocation, boolean manuallyDropped) {
        this(playerFlagLocation.getLocation(),
                playerFlagLocation.getTicksElapsed(),
                playerFlagLocation.getPlayer()
                                  .isDead() ? (int) (playerFlagLocation.getFlagMultiplier() + 15 * (1 + playerFlagLocation.getFlagMultiplier() / 100f)) : playerFlagLocation.getFlagMultiplier(),
                manuallyDropped
        );
    }

    @Nonnull
    @Override
    public Location getLocation() {
        return location;
    }

    public int getFlagMultiplier() {
        return flagMultiplier;
    }

    public int getDespawnTicks() {
        return despawnTicks;
    }

    public int getDespawnTimerSeconds() {
        return this.despawnTicks / 20;
    }

    public boolean isManuallyDropped() {
        return manuallyDropped;
    }

    public int getTicksElapsed() {
        return ticksElapsed;
    }

    public int getRepickTickCooldown() {
        return repickTickCooldown;
    }

    @Override
    public FlagLocation update(Game game, @Nonnull FlagInfo info) {
        this.ticksElapsed++;
        this.despawnTicks--;
        this.repickTickCooldown--;
        if (ticksElapsed >= PlayerFlagLocation.INCREASE_DELAY && ticksElapsed % FlagSpawnPointOption.FLAG_MULTIPLIER_PERIOD == 0) {
            this.flagMultiplier++;
        }
        if (this.despawnTicks <= 0) {
            List<FlagHolder> flagHolders = game.getMarkers(FlagHolder.class);
            for (FlagHolder flagHolder : flagHolders) {
                if (flagHolder.getInfo() != info && flagHolder.getFlag() instanceof PlayerFlagLocation playerFlagLocation) {
                    playerFlagLocation.setFlagMultiplier((int) (playerFlagLocation.getFlagMultiplier() * .5));
                }
            }
            return new SpawnFlagLocation(info.getSpawnLocation(), null, flagMultiplier);
        }
        return null;
    }

    @Nonnull
    @Override
    public List<TextComponent> getDebugInformation() {
        return Arrays.asList(
                Component.text("Type: " + this.getClass().getSimpleName()),
                Component.text("Despawn ticks: " + getDespawnTicks()),
                Component.text("Despawn seconds: " + getDespawnTimerSeconds()),
                Component.text("Multiplier: " + getFlagMultiplier())
        );
    }

    public static GroundFlagLocation of(@Nonnull FlagLocation flag) {
        return flag instanceof PlayerFlagLocation ? new GroundFlagLocation((PlayerFlagLocation) flag, false)
                                                  : new GroundFlagLocation(flag.getLocation(), 0, 0, false);
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
