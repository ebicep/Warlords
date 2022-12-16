package com.ebicep.warlords.game.option.wavedefense.events;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.TextOption;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;

public class FieldEffect implements Option {

    private final EnumSet<FieldEffects> fieldEffects;

    public FieldEffect(List<Option> options) {
        this.fieldEffects = EnumSet.allOf(FieldEffects.class);
        addOptions(options);
    }

    private void addOptions(List<Option> options) {
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.WHITE.toString() + ChatColor.BOLD + "Field Effects");
        lines.add("");
        fieldEffects.forEach(effect -> {
            List<String> effectLore = Arrays.asList(WordWrap.wrapWithNewline(ChatColor.GREEN + effect.name + ": " + ChatColor.GRAY + effect.description, 200)
                                                            .split("\n"));
            for (int i = 0; i < effectLore.size(); i++) {
                if (i != 0) {
                    lines.add(ChatColor.GRAY + effectLore.get(i));
                } else {
                    lines.add(effectLore.get(i));
                }
            }
            lines.add("");
        });
        options.add(TextOption.Type.CHAT_CENTERED.create(lines));
    }

    public FieldEffect(List<Option> options, FieldEffects... fieldEffects) {
        this.fieldEffects = EnumSet.of(fieldEffects[0], fieldEffects);
        addOptions(options);
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {

            int ticksElapsed = 0;

            @Override
            public void run() {
                fieldEffects.forEach(fieldEffect -> fieldEffect.effect.accept(game, ticksElapsed));
                ticksElapsed++;
            }
        }.runTaskTimer(0, 0);
    }

    public enum FieldEffects {

        WARRIORS_TRIUMPH("Warrior's Triumph",
                "Every 10 seconds, the active ability timers of all non-warrior specializations will be halved.",
                (game, integer) -> {
                    if (integer % 200 == 0) {
                        game.warlordsPlayers().forEach(warlordsPlayer -> {
                            if (Specializations.getClass(warlordsPlayer.getSpecClass()) == Classes.WARRIOR) {
                                return;
                            }
                            for (AbstractCooldown<?> cooldown : warlordsPlayer.getCooldownManager().getCooldowns()) {
                                if (cooldown instanceof RegularCooldown) {
                                    ((RegularCooldown<?>) cooldown).setTicksLeft(((RegularCooldown<?>) cooldown).getTicksLeft() / 2);
                                }
                            }
                        });
                    }
                }
        );

        public final String name;
        public final String description;
        public final BiConsumer<Game, Integer> effect;

        FieldEffects(String name, String description, BiConsumer<Game, Integer> effect) {
            this.name = name;
            this.description = description;
            this.effect = effect;
        }
    }

}
