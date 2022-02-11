package com.ebicep.warlords.game.option.marker.scoreboard;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class AbstractScoreboardHandler implements ScoreboardHandler {

    protected final List<Consumer<ScoreboardHandler>> handlers = new CopyOnWriteArrayList<>();

    public AbstractScoreboardHandler() {
    }

    @Override
    public Runnable registerChangeHandler(Consumer<ScoreboardHandler> onChange) {
        handlers.add(onChange);
        return () -> handlers.remove(onChange);
    }

    public void markChanged() {
        for (Consumer<ScoreboardHandler> handler : handlers) {
            handler.accept(this);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "{" + "priority=" + getPriority(null) + ", group=" + getGroup() + ", contents=" + computeLines(null) + '}';
    }

}
