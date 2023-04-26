package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Allows you to send a text to all players on start
 */
public class TextOption implements Option, TimerSkipAbleMarker {
    public static final int DEFAULT_DELAY = 0;

    @Nonnull
    private Type type;
    @Nonnull
    private List<Component> text;
    private int delay;
    private Game game;

    public TextOption(@Nonnull Type type, @Nonnull List<Component> text) {
        this(type, text, DEFAULT_DELAY);
    }

    public TextOption(@Nonnull Type type, @Nonnull List<Component> text, int delay) {
        this.type = Objects.requireNonNull(type, "type");
        this.text = Objects.requireNonNull(text, "text");
        this.delay = delay;
    }

    @Nonnull
    public List<Component> getText() {
        return text;
    }

    public void setText(@Nonnull List<Component> text) {
        this.text = Objects.requireNonNull(text, "text");
    }

    @Nonnull
    public Type getType() {
        return type;
    }

    public void setType(@Nonnull Type type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    @Override
    public int getDelay() {
        return delay * 20;
    }

    @Override
    public void skipTimer(int delayInTicks) {
        delay -= delayInTicks / 20;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        game.registerGameMarker(TimerSkipAbleMarker.class, this);
    }

    @Override
    public void start(@Nonnull Game game) {
        Option.super.start(game);
        if (delay <= 0) {
            sendText();
        } else {
            new GameRunnable(game) {
                @Override
                public void run() {
                    if (delay <= 0) {
                        sendText();
                        cancel();
                    }
                    delay--;
                }
            }.runTaskTimer(0, 20);
        }
    }

    public void sendText() {
        this.type.sendText(game, text);
    }


    public enum Type {
        CHAT_CENTERED() {
            @Override
            public void sendText(@Nonnull Game game, @Nonnull List<Component> messages) {
                game.forEachOnlinePlayer((p, t) -> {
                    ChatUtils.sendMessage(p, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                    for (Component line : messages) {
                        ChatUtils.sendMessage(p, true, line);
                    }
                    ChatUtils.sendMessage(p, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                });
            }
        },
        CHAT() {
            @Override
            public void sendText(@Nonnull Game game, @Nonnull List<Component> messages) {
                game.forEachOnlinePlayer((p, t) -> {
                    ChatUtils.sendMessage(p, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                    for (Component line : messages) {
                        ChatUtils.sendMessage(p, false, line);
                    }
                    ChatUtils.sendMessage(p, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                });
            }
        },
        TITLE() {
            @Override
            public void sendText(@Nonnull Game game, @Nonnull List<Component> messages) {
                Iterator<Component> itr = messages.iterator();
                if (!itr.hasNext()) {
                    return;
                }
                new GameRunnable(game) {
                    @Override
                    public void run() {
                        Component title = itr.next();
                        Component subtitle = itr.hasNext() ? itr.next() : Component.empty();
                        game.forEachOnlinePlayer((p, t) -> {
                            p.showTitle(Title.title(
                                    title,
                                    subtitle,
                                    Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(20))
                            ));
                        });
                        if (!itr.hasNext()) {
                            cancel();
                        }
                    }
                }.runTaskTimer(0, 40);
            }
        };

        public abstract void sendText(@Nonnull Game game, @Nonnull List<Component> messages);

        public TextOption create(@Nonnull List<Component> text) {
            return new TextOption(this, text);
        }

        public TextOption create(Component... text) {
            return new TextOption(this, Arrays.asList(text));
        }

        public TextOption create(int delay, @Nonnull List<Component> text) {
            return new TextOption(this, text, delay);
        }

        public TextOption create(int delay, Component... text) {
            return new TextOption(this, Arrays.asList(text), delay);
        }

    }
}
