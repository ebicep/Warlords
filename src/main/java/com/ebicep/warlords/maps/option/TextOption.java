package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.util.ChatUtils;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.PacketUtils;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;

/**
 * Allows you to send a text to all players on start
 */
public class TextOption implements Option {

    @Nonnull
    private Type type;
    @Nonnull
    private List<String> text;

    public TextOption(@Nonnull Type type, @Nonnull List<String> text) {
        this.type = Objects.requireNonNull(type, "type");
        this.text = Objects.requireNonNull(text, "text");
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

    @Override
    public void start(@Nonnull Game game) {
        Option.super.start(game);
        this.type.sendText(game, text);
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

    }
}
