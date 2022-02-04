package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.events.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.events.WarlordsIntersectionCaptureEvent;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.WaitingFlagLocation;
import com.ebicep.warlords.maps.option.marker.TeamMarker;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public abstract class ScoreOnEventOption<T> implements Option {
    protected int scoreIncrease;
    private BiFunction<T, Pair<Team, Integer>, Pair<Team, Integer>> scoreModifier = (a, b) -> b;
    private Game game;

    public ScoreOnEventOption(int scoreIncrease) {
        this.scoreIncrease = scoreIncrease;
    }

    public int getScoreIncrease() {
        return scoreIncrease;
    }

    public void setScoreIncrease(int scoreIncrease) {
        this.scoreIncrease = scoreIncrease;
    }

    public BiFunction<T, Pair<Team, Integer>, Pair<Team, Integer>> getScoreModifier() {
        return scoreModifier;
    }

    public void setScoreModifier(BiFunction<T, Pair<Team, Integer>, Pair<Team, Integer>> scoreModifier) {
        this.scoreModifier = scoreModifier;
    }

    @Override
    public void register(Game game) {
        this.game = game;
    }
    
    protected void giveScore(T event, Team team, int score) {
        Pair<Team, Integer> res = scoreModifier.apply(event, new Pair<>(team, score));
        Game.Stats stats = game.getStats(res.getA());
        stats.setPoints(stats.points() + res.getB());
    }
    
    public static class FlagCapture extends ScoreOnEventOption<WarlordsFlagUpdatedEvent> {
        public static int DEFAULT_SCORE = 250;
        
        public FlagCapture() {
            this(DEFAULT_SCORE);
        }

        public FlagCapture(int scoreIncrease) {
            super(scoreIncrease);
        }

        @Override
        public void register(Game game) {
            super.register(game);
            game.registerEvents(new Listener() {
                @EventHandler
                public void onEvent(WarlordsFlagUpdatedEvent event) {
                    if (event.getNew() instanceof WaitingFlagLocation) {
                        WaitingFlagLocation waitingFlagLocation = (WaitingFlagLocation) event.getNew();
                        WarlordsPlayer scorer = waitingFlagLocation.getScorer();
                        if (scorer != null) {
                            giveScore(event, scorer.getTeam(), scoreIncrease);
                        }
                    }
                }
            });
        }
        
    }

    public static class OnKill extends ScoreOnEventOption<WarlordsDeathEvent> {
        public static int DEFAULT_SCORE = 5;

        public OnKill() {
            this(DEFAULT_SCORE);
        }

        public OnKill(int scoreIncrease) {
            super(scoreIncrease);
        }

        @Override
        public void register(Game game) {
            super.register(game);
            game.registerEvents(new Listener() {
                @EventHandler
                public void onEvent(WarlordsDeathEvent event) {
                    Collection<Team> teams = TeamMarker.getTeams(event.getGame());
                    List<Team> toReward = new ArrayList<>(teams.size() - 1);
                    for(Team team : teams) {
                        if(event.getKiller() != null && event.getKiller().getTeam() != event.getPlayer().getTeam() && event.getKiller().getTeam() != team) {
                            continue;
                        }
                        if(team == event.getPlayer().getTeam()) {
                            continue;
                        }
                        toReward.add(team);
                    }
                    for(Team team : toReward) {
                        giveScore(event, team, scoreIncrease / toReward.size());
                    }
                }
            });
        }

    }
    public static class OnIntersectionCapture extends ScoreOnEventOption<WarlordsIntersectionCaptureEvent> {
        public static int DEFAULT_SCORE = 5;

        public OnIntersectionCapture() {
            this(DEFAULT_SCORE);
        }

        public OnIntersectionCapture(int scoreIncrease) {
            super(scoreIncrease);
        }

        @Override
        public void register(Game game) {
            super.register(game);
            game.registerEvents(new Listener() {
                @EventHandler
                public void onEvent(WarlordsIntersectionCaptureEvent event) {
                    if (event.getOption().getTeamOwning() != null) {
                        giveScore(event, event.getOption().getTeamOwning(), scoreIncrease);
					}
                }
            });
        }

    }
    public static class OnIntersectionTimer extends ScoreOnEventOption<IntersectionPointOption> {
        public static int DEFAULT_SCORE = 1;

        public OnIntersectionTimer() {
            this(DEFAULT_SCORE);
        }

        public OnIntersectionTimer(int scoreIncrease) {
            super(scoreIncrease);
        }

        @Override
        public void start(Game game) {
            super.register(game);
            new GameRunnable(game) {
				@Override
				public void run() {
					for (Option option : game.getOptions()) {
						if (option instanceof IntersectionPointOption) {
							IntersectionPointOption intersectionPointOption = (IntersectionPointOption) option;
							if (intersectionPointOption.getTeamOwning() != null) {
								giveScore(intersectionPointOption, intersectionPointOption.getTeamOwning(), scoreIncrease);
							}
						}
					}
				}
			}.runTaskTimer(20, 20);
        }

    }
}
