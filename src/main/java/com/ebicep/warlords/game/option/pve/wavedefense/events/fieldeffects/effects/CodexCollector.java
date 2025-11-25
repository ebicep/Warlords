package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabasePlayerPvEEventLibraryArchivesDifficultyStats;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropWeaponEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.gameevents.libraryarchives.PlayerCodex;
import com.ebicep.warlords.pve.mobs.events.libraryarchives.EventInquisiteur;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CodexCollector implements FieldEffect {

    private final Map<WarlordsEntity, PlayerCodex> playerCodexEquipped = new HashMap<>();
    private int codexesEquipped = 0;

    public Map<WarlordsEntity, PlayerCodex> getPlayerCodexEquipped() {
        return playerCodexEquipped;
    }

    @Override
    public void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        for (WarlordsEntity player : players) {
            if (!(player instanceof WarlordsPlayer warlordsPlayer)) {
                return;
            }
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player.getUuid());
            EventMode eventMode = currentGameEvent.getEvent().eventsStatsFunction.apply(databasePlayer.getPveStats().getEventStats())
                                                                                 .get(currentGameEvent.getStartDateSecond());
            if (!(eventMode instanceof DatabasePlayerPvEEventLibraryArchivesDifficultyStats stats)) {
                return;
            }
            Specializations specClass = player.getSpecClass();
            PlayerCodex codexForSpec = PlayerCodex.getCodexForSpec(specClass);
            if (stats.getCodexesEarned().getOrDefault(codexForSpec, 0) > 0) {
                codexesEquipped++;
                player.getSpec().getAbilities().clear();
                for (Ability<?> ability : codexForSpec.abilities) {
                    AbstractAbility abstractAbility = ability.create.get();
                    abstractAbility.init(abstractAbility.getBuilder());
                    player.getSpec().getAbilities().add(abstractAbility);
                }
                warlordsPlayer.resetAbilityTree();
                playerCodexEquipped.put(player, codexForSpec);
            }
        }
        Game game = players.get(0).getGame();
        game.registerEvents(new Listener() {

            @EventHandler(ignoreCancelled = true)
            public void onKill(WarlordsDeathEvent event) {
                if (codexesEquipped < 2) {
                    return;
                }
                if (!(event.getWarlordsEntity() instanceof WarlordsPlayer warlordsPlayer)) {
                    return;
                }
                float healing = warlordsPlayer.getMaxHealth() * 0.05f;
                warlordsPlayer.addInstance(InstanceBuilder
                        .healing()
                        .cause("Codex Collector")
                        .source(warlordsPlayer)
                        .value(healing)
                );
            }

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (codexesEquipped < 4) {
                    return;
                }
                if (!(event.getSource() instanceof WarlordsPlayer warlordsPlayer)) {
                    return;
                }
                if (event.getCause().isEmpty()) {
                    event.getCritChance().addModifier(FloatModifiable.ModifierType.ADDITIVE, getName(), 5);
                    event.getCritMultiplier().addModifier(FloatModifiable.ModifierType.ADDITIVE, getName(), 10);
                }
            }

            @EventHandler
            public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                if (codexesEquipped < 6) {
                    return;
                }
                if (!event.isDead()) {
                    return;
                }
                if (!(event.getSource() instanceof WarlordsPlayer warlordsPlayer)) {
                    return;
                }
                Map<String, AbstractAbility> abilityMap = new HashMap<>();
                warlordsPlayer.getAbilities().forEach(ability -> abilityMap.put(ability.getName(), ability));
                if (abilityMap.containsKey(event.getCause())) {
                    if (ThreadLocalRandom.current().nextDouble() < 0.25) {
                        AbstractAbility ability = abilityMap.get(event.getCause());
                        ability.setCurrentCooldown(0);
                    }
                }
            }

            @EventHandler
            public void onWeaponDrop(WarlordsDropWeaponEvent event) {
                if (codexesEquipped < 6) {
                    return;
                }
                if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof EventInquisiteur) {
                    event.getDropRate().set(.2);
                }
            }

        });

        if (codexesEquipped >= 4) {
            for (WarlordsEntity player : players) {
                for (AbstractAbility ability : player.getAbilities()) {
                    Value.applyDamageHealing(ability, value -> {
                                if (value instanceof Value.RangedValueCritable rangedValueCritable) {
                                    rangedValueCritable.critChance().addModifier(FloatModifiable.ModifierType.ADDITIVE, getName(), 5);
                                    rangedValueCritable.critMultiplier().addModifier(FloatModifiable.ModifierType.ADDITIVE, getName(), 10);
                                }
                            }
                    );
                }
            }
        }
    }

    public int getCodexesEquipped() {
        return codexesEquipped;
    }

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
            add(Component.text("2 Codexes", NamedTextColor.GOLD));
            add(Component.text("Defeating an opponent instantly restores", NamedTextColor.GRAY));
            add(Component.text("5% of max HP.", NamedTextColor.GRAY));
            add(Component.empty());
            add(Component.text("4 Codexes", NamedTextColor.GOLD));
            add(Component.text("+5% Crit Chance and +10 Crit Multiplier.", NamedTextColor.GRAY));
            add(Component.empty());
            add(Component.text("6 Codexes", NamedTextColor.GOLD));
            add(Component.text("Defeating an opponent with a rune ability", NamedTextColor.GRAY));
            add(Component.text("has a 25% chance of ending its cooldown", NamedTextColor.GRAY));
        }};
    }




}
