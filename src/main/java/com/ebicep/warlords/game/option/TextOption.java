package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
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
    private List<String> text;
    private int delay;
    private Game game;

    public TextOption(@Nonnull Type type, @Nonnull List<String> text) {
        this(type, text, DEFAULT_DELAY);
    }
    public TextOption(@Nonnull Type type, @Nonnull List<String> text, int delay) {
        this.type = Objects.requireNonNull(type, "type");
        this.text = Objects.requireNonNull(text, "text");
        this.delay = delay;
    }

    @Nonnull
    public List<String> getText() {
        return text;
    }

    public void setText(@Nonnull List<String> text) {
        this.text = Objects.requireNonNull(text, "text");
    }

    @Nonnull
    public Type getType() {
        return type;
    }

    public void setType(@Nonnull Type type) {
        this.type = Objects.requireNonNull(type, "type");
    }
    
    public void sendText() {
        this.type.sendText(game, text);
    }

    @Override
    public void start(@Nonnull Game game) {
        Option.super.start(game);
        if(delay <= 0) {
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

    @Override
    public int getDelay() {
        return delay * 20;
    }

    @Override
    public void skipTimer(int delayInTicks) {
        delay -= delayInTicks / 20;
    }

    @Override
    public void register(Game game) {
        this.game = game;
        game.registerGameMarker(TimerSkipAbleMarker.class, this);
    }
    
    

    public enum Type {
        CHAT_CENTERED() {
            @Override
            public void sendText(@Nonnull Game game, @Nonnull List<String> messages) {
                game.forEachOnlinePlayer((p, t) -> {
                    ChatUtils.sendMessage(p, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                    for (String line : messages) {
                        ChatUtils.sendMessage(p, true, line);
                    }
                    ChatUtils.sendMessage(p, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                });
            }
        },
        CHAT() {
            @Override
            public void sendText(@Nonnull Game game, @Nonnull List<String> messages) {
                game.forEachOnlinePlayer((p, t) -> {
                    ChatUtils.sendMessage(p, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                    for (String line : messages) {
                        ChatUtils.sendMessage(p, false, line);
                    }
                    ChatUtils.sendMessage(p, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                });
            }
        },
        TITLE() {
            @Override
            public void sendText(@Nonnull Game game, @Nonnull List<String> messages) {
                Iterator<String> itr = messages.iterator();
                if (!itr.hasNext()) {
                    return;
                }
                new GameRunnable(game) {
                    @Override
                    public void run() {
                        String title = itr.next();
                        String subtitle = itr.hasNext() ? itr.next() : "";
                        game.forEachOnlinePlayer((p, t) -> {
                            PacketUtils.sendTitle(p, title, subtitle, 0, 40, 20);
                        });
                        if (!itr.hasNext()) {
                            cancel();
                        }
                    }
                }.runTaskTimer(0, 40);
            }
        };

        public abstract void sendText(@Nonnull Game game, @Nonnull List<String> messages);

        public TextOption create(@Nonnull List<String> text) {
            return new TextOption(this, text);
        }

        public TextOption create(String ... text) {
            return new TextOption(this, Arrays.asList(text));
        }

        public TextOption create(int delay, @Nonnull List<String> text) {
            return new TextOption(this, text, delay);
        }

        public TextOption create(int delay, String ... text) {
            return new TextOption(this, Arrays.asList(text), delay);
        }

    }
}
