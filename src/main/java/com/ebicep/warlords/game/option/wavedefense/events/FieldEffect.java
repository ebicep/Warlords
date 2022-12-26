package com.ebicep.warlords.game.option.wavedefense.events;

import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.TextOption;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

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
        fieldEffects.forEach(fieldEffect -> fieldEffect.onStart(game));
        new GameRunnable(game) {

            int ticksElapsed = 0;

            @Override
            public void run() {
                fieldEffects.forEach(fieldEffect -> fieldEffect.run(game, ticksElapsed));
                ticksElapsed++;
            }
        }.runTaskTimer(0, 0);
    }

    public enum FieldEffects {

        WARRIORS_TRIUMPH("Warrior's Triumph",
                "Ability durations are reduced by 50% on ability activation for non-Warrior specializations. All strikes deal 100% more damage."
        ) {
            @Override
            public void onStart(Game game) {
                game.registerEvents(new Listener() {
                    @EventHandler
                    public void onCooldown(WarlordsAddCooldownEvent event) {
                        if (!(event.getPlayer() instanceof WarlordsPlayer)) {
                            return;
                        }
                        if (Specializations.getClass(event.getPlayer().getSpecClass()) == Classes.WARRIOR) {
                            return;
                        }
                        AbstractCooldown<?> abstractCooldown = event.getAbstractCooldown();
                        if (abstractCooldown instanceof LinkedCooldown) {
                            if (abstractCooldown.getFrom().equals(event.getPlayer())) {
                                LinkedCooldown<?> linkedCooldown = (LinkedCooldown<?>) abstractCooldown;
                                linkedCooldown.setTicksLeft((int) (linkedCooldown.getTicksLeft() * 0.7));
                            }
                        } else if (abstractCooldown instanceof RegularCooldown) {
                            RegularCooldown<?> regularCooldown = (RegularCooldown<?>) abstractCooldown;
                            regularCooldown.setTicksLeft((int) (regularCooldown.getTicksLeft() * 0.7));
                        }
                    }

                    @EventHandler
                    public void onDamageHeal(WarlordsDamageHealingEvent event) {
                        if (!(event.getAttacker() instanceof WarlordsPlayer)) {
                            return;
                        }
                        if (Specializations.getClass(event.getAttacker().getSpecClass()) == Classes.WARRIOR) {
                            return;
                        }
                        String ability = event.getAbility();
                        if (ability.contains("Strike")) {
                            event.setMin(event.getMin() * 2);
                            event.setMax(event.getMax() * 2);
                        }
                    }
                });
            }

        };

        public final String name;
        public final String description;

        FieldEffects(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public void onStart(Game game) {

        }

        public void run(Game game, int ticksElapsed) {

        }

    }

}
