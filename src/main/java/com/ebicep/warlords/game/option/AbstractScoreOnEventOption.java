package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.events.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.events.WarlordsIntersectionCaptureEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import com.ebicep.warlords.game.flags.SpawnFlagLocation;
import com.ebicep.warlords.game.flags.WaitingFlagLocation;
import com.ebicep.warlords.game.option.marker.PointPredicterMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public abstract class AbstractScoreOnEventOption<T> implements Option {
    protected int scoreIncrease;
    private BiFunction<T, Pair<Team, Integer>, Pair<Team, Integer>> scoreModifier = (a, b) -> b;
    private Game game;

    public AbstractScoreOnEventOption(int scoreIncrease) {
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
        game.addPoints(res.getA(), res.getB());
    }

    public static class FlagCapture extends AbstractScoreOnEventOption<WarlordsFlagUpdatedEvent> {
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

    public static class FlagReturn extends AbstractScoreOnEventOption<WarlordsFlagUpdatedEvent> {
        public static int DEFAULT_SCORE = 100;

        public FlagReturn() {
            this(DEFAULT_SCORE);
        }

        public FlagReturn(int scoreIncrease) {
            super(scoreIncrease);
        }

        @Override
        public void register(Game game) {
            super.register(game);
            game.registerEvents(new Listener() {
                @EventHandler
                public void onEvent(WarlordsFlagUpdatedEvent event) {
                    if (event.getNew() instanceof SpawnFlagLocation) {
                        SpawnFlagLocation spawnFlagLocation = (SpawnFlagLocation) event.getNew();
                        WarlordsPlayer scorer = spawnFlagLocation.getFlagReturner();
                        if (scorer != null) {
                            giveScore(event, scorer.getTeam(), scoreIncrease);
                        }
                    }
                }
            });
        }
    }

    public static class FlagHolding extends AbstractScoreOnEventOption<WarlordsFlagUpdatedEvent> {
        public static int DEFAULT_SCORE = 1;

        public FlagHolding() {
            this(DEFAULT_SCORE);
        }

        public FlagHolding(int scoreIncrease) {
            super(scoreIncrease);
        }

        @Override
        public void register(Game game) {
            super.register(game);
            game.registerEvents(new Listener() {
                @EventHandler
                public void onEvent(WarlordsFlagUpdatedEvent event) {
                    if (event.getOld() instanceof PlayerFlagLocation && event.getNew() instanceof PlayerFlagLocation) {
                        PlayerFlagLocation playerFlagLocation = (PlayerFlagLocation) event.getNew();
                        WarlordsPlayer scorer = playerFlagLocation.getPlayer();
                        giveScore(event, scorer.getTeam(), scoreIncrease);
                    }
                }
            });
        }
    }

    public static class OnKill extends AbstractScoreOnEventOption<WarlordsDeathEvent> {
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
                    for (Team team : toReward) {
                        giveScore(event, team, scoreIncrease / toReward.size());
                    }
                }
            });
        }

    }

    public static class OnInterceptionCapture extends AbstractScoreOnEventOption<WarlordsIntersectionCaptureEvent> {
        public static int DEFAULT_SCORE = 5;

        public OnInterceptionCapture() {
            this(DEFAULT_SCORE);
        }

        public OnInterceptionCapture(int scoreIncrease) {
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

    public static class OnInterceptionTimer extends AbstractScoreOnEventOption<InterceptionPointOption> {
        public static int DEFAULT_SCORE = 1;

        public OnInterceptionTimer() {
            this(DEFAULT_SCORE);
        }

        public OnInterceptionTimer(int scoreIncrease) {
            super(scoreIncrease);
        }

        @Override
        public void register(Game game) {
            super.register(game);
            game.registerGameMarker(PointPredicterMarker.class, team -> {
                double predictedScoreIncrease = 0;
                for (Option option : game.getOptions()) {
                    if (option instanceof InterceptionPointOption) {
                        InterceptionPointOption intersectionPointOption = (InterceptionPointOption) option;
                        if (intersectionPointOption.getTeamOwning() == team) {
                            predictedScoreIncrease += scoreIncrease * 60;
                        }
                    }
                }
                return predictedScoreIncrease;
            });
        }

        @Override
        public void start(Game game) {
            super.register(game);
            new GameRunnable(game) {
				@Override
				public void run() {
					for (Option option : game.getOptions()) {
						if (option instanceof InterceptionPointOption) {
							InterceptionPointOption intersectionPointOption = (InterceptionPointOption) option;
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
