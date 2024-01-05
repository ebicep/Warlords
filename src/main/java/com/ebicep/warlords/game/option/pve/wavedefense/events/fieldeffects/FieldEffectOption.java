package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.TextOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects.*;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FieldEffectOption implements Option {

    private final List<com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect> fieldEffects;

    public FieldEffectOption(List<Option> options, FieldEffect... fieldEffects) {
        this.fieldEffects = Arrays.stream(fieldEffects).map(effect -> effect.create.get()).collect(Collectors.toList());
        addOptions(options);
    }

    private void addOptions(List<Option> options) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.text("Field Effects", NamedTextColor.WHITE, TextDecoration.BOLD));
        lines.add(Component.empty());
        fieldEffects.forEach(effect -> {
            lines.addAll(WordWrap.wrap(Component.text(effect.getName() + ": ", NamedTextColor.GREEN)
                                                .append(Component.text(effect.getDescription(), NamedTextColor.GRAY)), 180));
            lines.addAll(effect.getSubDescription());
            lines.add(Component.empty());
        });
        options.add(TextOption.Type.CHAT_CENTERED.create(lines));
    }

    @Override
    public void start(@Nonnull Game game) {
        fieldEffects.forEach(fieldEffect -> fieldEffect.onStart(game));
        new GameRunnable(game) {

            int ticksElapsed = 0;

            @Override
            public void run() {
                if (game.isState(EndState.class)) {
                    cancel();
                    return;
                }
                fieldEffects.forEach(fieldEffect -> fieldEffect.run(game, ticksElapsed));
                ticksElapsed++;
            }
        }.runTaskTimer(0, 0);
    }

    public List<com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect> getFieldEffects() {
        return fieldEffects;
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        fieldEffects.forEach(fieldEffect -> fieldEffect.onWarlordsEntityCreated(player));
    }

    @Override
    public void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {
        fieldEffects.forEach(fieldEffect -> fieldEffect.afterAllWarlordsEntitiesCreated(players));
    }

    public enum FieldEffect {

        WARRIORS_TRIUMPH(WarriorsTriumph::new),
        CONQUERING_ENERGY(ConqueringEnergy::new),
        ARACHNOPHOBIA(Arachnophobia::new),
        LOST_BUFF(LostBuff::new),
        DUMB_DEBUFFS(DumbDebuffs::new),
        TYCHE_PROSPERITY(TycheProsperity::new),
        ACCUMULATING_KNOWLEDGE(AccumulatingKnowledge::new),
        CODEX_COLLECTOR(CodexCollector::new);

        public final Supplier<? extends com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect> create;

        FieldEffect(Supplier<? extends com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect> create) {
            this.create = create;
        }

    }

}
