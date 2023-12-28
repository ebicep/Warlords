package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabasePlayerPvEEventLibraryArchivesDifficultyStats;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.gameevents.libraryarchives.PlayerCodex;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class CodexCollector implements FieldEffect {

    @Override
    public String getName() {
        return "Codex Collector";
    }

    @Override
    public String getDescription() {
        return "Players gain a special bonus based on the amount of codexes equipped.";
    }

    @Override
    public List<Component> getSubDescription() {
        return new ArrayList<>() {{
            add(Component.empty());
            add(Component.text("2 Codexes: ", NamedTextColor.DARK_RED)
                         .append(Component.text("Defeating an opponent instantly restores 5% of max HP.")));
            add(Component.text("4 Codexes: ", NamedTextColor.DARK_RED)
                         .append(Component.text("+5% Crit Chance and +10 Crit Multiplier.")));
            add(Component.text("6 Codexes: ", NamedTextColor.DARK_RED)
                         .append(Component.text("Defeating an opponent with a rune ability has a 25% chance of ending its cooldown")));
        }};
    }

    @Override
    public void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        AtomicInteger codexesEquipped = new AtomicInteger();
        for (WarlordsEntity player : players) {
            DatabaseManager.getPlayer(player.getUuid(), databasePlayer -> {
                EventMode eventMode = currentGameEvent.getEvent().eventsStatsFunction.apply(databasePlayer.getPveStats().getEventStats())
                                                                                     .get(currentGameEvent.getStartDateSecond());
                if (!(eventMode instanceof DatabasePlayerPvEEventLibraryArchivesDifficultyStats stats)) {
                    return;
                }
                Specializations specClass = player.getSpecClass();
                PlayerCodex codexForSpec = PlayerCodex.getCodexForSpec(specClass);
                if (stats.getCodexesEarned().getOrDefault(codexForSpec, 0) > 0) {
                    codexesEquipped.incrementAndGet();
                    player.getSpec().getAbilities().clear();
                    for (Ability ability : codexForSpec.abilities) {
                        player.getSpec().getAbilities().add(ability.create.get());
                    }
                }
            });
        }
        Game game = players.get(0).getGame();
        game.registerEvents(new Listener() {

            @EventHandler
            public void onKill(WarlordsDeathEvent event) {
                if (codexesEquipped.get() < 2) {
                    return;
                }
                if (!(event.getWarlordsEntity() instanceof WarlordsPlayer warlordsPlayer)) {
                    return;
                }
                float healing = warlordsPlayer.getMaxHealth() * 0.05f;
                warlordsPlayer.addHealingInstance(warlordsPlayer, "Codex Collector", healing, healing, 0, 0);
            }

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (codexesEquipped.get() < 4) {
                    return;
                }
                if (!(event.getAttacker() instanceof WarlordsPlayer warlordsPlayer)) {
                    return;
                }
                if (event.getAbility().isEmpty()) {
                    event.setCritChance(event.getCritChance() + 5);
                    event.setCritMultiplier(event.getCritMultiplier() + 10);
                }
            }

            @EventHandler
            public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                if (codexesEquipped.get() < 6) {
                    return;
                }
                if (!event.isDead()) {
                    return;
                }
                if (!(event.getAttacker() instanceof WarlordsPlayer warlordsPlayer)) {
                    return;
                }
                Map<String, AbstractAbility> abilityMap = new HashMap<>();
                warlordsPlayer.getAbilities().forEach(ability -> abilityMap.put(ability.getName(), ability));
                if (abilityMap.containsKey(event.getAbility())) {
                    if (ThreadLocalRandom.current().nextDouble() < 0.25) {
                        AbstractAbility ability = abilityMap.get(event.getAbility());
                        ability.setCurrentCooldown(0);
                        warlordsPlayer.updateItem(ability);
                    }
                }
            }

        });

        if (codexesEquipped.get() >= 4) {
            for (WarlordsEntity player : players) {
                for (AbstractAbility ability : player.getAbilities()) {
                    ability.setCritChance(ability.getCritChance() + 5);
                    ability.setCritMultiplier(ability.getCritMultiplier() + 10);
                }
            }
        }

    }

}
