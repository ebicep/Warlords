package com.ebicep.warlords.game;

import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.MapSymmetryMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public enum MapSymmetry {
    NONE() {
        @Override
        public Location getOppositeLocation(Game game, Team sourceTeam, Team targetTeam, Location original, Location target) {
            return target;
        }

    }, SPIN {
        @Override
        public Location getOppositeLocation(Game game, Team sourceTeam, Team targetTeam, Location original, Location target) {
            Collection<Team> teams = TeamMarker.getTeams(game);
            Location mapCenter = new Location(target.getWorld(), 0, 0, 0);
            int indexSource = 0;
            int indexTarget = 0;
            int index = 0;
            for (Team team : teams) {
                if (team == sourceTeam) {
                    indexSource = index;
                }
                if (team == targetTeam) {
                    indexSource = index;
                }
                LobbyLocationMarker lobby = LobbyLocationMarker.getFirstLobbyLocation(game, team);
                if (lobby != null) {
                    mapCenter.add(lobby.getLocation());
                }
                index++;
            }
            if (indexSource == indexTarget) {
                return target;
            }
            mapCenter.multiply(1 / teams.size());
            double rotationAngle = indexSource * Math.PI * 2 / teams.size() + indexTarget * Math.PI * 2 / teams.size();
            Vector difference = mapCenter.toVector().subtract(original.toVector());
            Vector rotated = new Vector(
                    difference.getX() * Math.cos(rotationAngle) + difference.getZ() * Math.sin(rotationAngle),
                    difference.getY(),
                    difference.getZ() * Math.cos(rotationAngle) + difference.getX() * -Math.sin(rotationAngle)
            );
            mapCenter.subtract(rotated);
            mapCenter.setPitch(original.getPitch());
            mapCenter.setYaw((float) (original.getYaw() + rotationAngle / Math.PI * 180));
            return mapCenter;
        }

    }, MIRROR {
        @Override
        public Location getOppositeLocation(Game game, Team sourceTeam, Team targetTeam, Location original, Location target) {
            return target; // TODO implement a nice warping for mirrored games
        }

    };

    public MapSymmetryMarker asMarker() {
        return new MapSymmetryMarker() {
            @Override
            public MapSymmetry getSymmetry() {
                return MapSymmetry.this;
            }

            @Override
            public String toString() {
                return MapSymmetry.this.toString();
            }
        };
    }
    
    public Option asOption() {
        return asMarker().asOption();
    }

    public abstract Location getOppositeLocation(Game game, Team sourceTeam, Team targetTeam, Location original, Location target);
}
