package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.WeaponDisplayMarker;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ComponentUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ApplySpecBoostsOption implements Option {

    private static final Component MESSAGE_DIVIDER =
            Component.text("---------------------------------------", NamedTextColor.DARK_GRAY);

    public static String getPlayerSpecBoost(WarlordsPlayer warlordsPlayer) {
        for (ApplySpecBoostsOption applySpecBoostsOption : warlordsPlayer.getGame().getOption(ApplySpecBoostsOption.class)) {
            PlayerSpecAppliedBoost appliedBoost = applySpecBoostsOption.getPlayerSpecBoosts().get(warlordsPlayer);
            if (appliedBoost != null) {
                return appliedBoost.specBoost.getDatabaseName();
            }
        }
        return "";
    }

    @Override
    public boolean isEnabled(@Nonnull Game game) {
        return game.getOption(AbilityChangeOption.class).isEmpty();
    }

    private Game game;

    @Override
    public void start(@Nonnull Game game) {
        this.game = game;
        if (random) {
            try {
                Map<Specializations, List<SpecBoostManager.SpecBoost<?>>> boosts = new HashMap<>(SpecBoostManager.getSpecBoosts());
                for (Map.Entry<Specializations, List<SpecBoostManager.SpecBoost<?>>> entry : SpecBoostManager.getSpecBoosts().entrySet()) {
                    boosts.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                }
                boosts.forEach((specializations, specBoosts) -> {
                    for (int i = 0; i < specBoosts.size(); i++) {
                        SpecBoostManager.SpecBoost<?> specBoost = specBoosts.get(i);
                        if (specBoost.isDisabled() ||
                                game.offlinePlayersWithoutSpectators()
                                    .map(offlinePlayerTeamEntry -> offlinePlayerTeamEntry.getKey().getUniqueId())
                                    .anyMatch(uuid -> {
                                                String uuidString = uuid.toString().replace("-", "");
                                                List<String> permittedPlayers = specBoost.getPermittedPlayers();
                                                return DatabaseManager.getPlayer(uuid).getLastSpec() == specializations &&
                                                        (!permittedPlayers.isEmpty() && !permittedPlayers.contains(uuidString) || specBoost.getBannedPlayers().contains(uuidString));
                                            }
                                    )
                        ) {
                            System.out.println("REMOVED: " + specBoost.getStringName() + " for " + specializations.name);
                            specBoosts.remove(i);
                            i--;
                        }
                    }
                });
                boosts.forEach((specializations, specBoosts) -> {
                    preassignedSpecBoosts.put(specializations, specBoosts.get(ThreadLocalRandom.current().nextInt(specBoosts.size())));
                });
            } catch (Exception e) {
                ChatUtils.MessageType.GAME.sendErrorMessage(e);
            }
        }
    }
    private final Map<WarlordsEntity, PlayerSpecAppliedBoost> playerSpecBoosts = new HashMap<>();
    private final Map<Specializations, SpecBoostManager.SpecBoost<?>> preassignedSpecBoosts = new HashMap<>();
    private final boolean random;

    public ApplySpecBoostsOption(boolean random) {
        this.random = random;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        game.registerGameMarker(WeaponDisplayMarker.class, new WeaponDisplayMarker() {
                    @Override
                    public int weaponDisplayPriority() {
                        return 1;
                    }

                    @Override
                    public @Nullable List<Component> leftClickDescription(WarlordsPlayer wp, Player player) {
                        List<Component> description = new ArrayList<>();
                        PlayerSpecAppliedBoost appliedBoost = playerSpecBoosts.get(wp);
                        if (appliedBoost != null) {
                            SpecBoostManager.SpecBoost<?> boost = appliedBoost.specBoost;
                            description.add(Component.text("Active Boost: ", NamedTextColor.GREEN).append(Component.text(boost.getStringName(), NamedTextColor.AQUA)));
                            description.addAll(boost.getDescriptionLore());
                            return description;
                        }
                        List<SpecBoostManager.SpecBoost<?>> specBoosts = SpecBoostManager.getSpecBoosts(wp.getSpecClass());
                        if (specBoosts.isEmpty()) {
                            return null;
                        }
                        SpecBoostManager.SpecBoost<?> boost = specBoosts.getFirst();
                        description.add(boost.getName());
                        description.addAll(boost.getDescriptionLore());
                        return description;
                    }
                }
        );
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity wp) {
        Specializations newSpec = wp.getSpecClass();
        if (!(wp instanceof WarlordsPlayer warlordsPlayer)) {
            return;
        }
        if (random) {
            giveRandomBoost(warlordsPlayer, newSpec);
        } else {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(wp.getUuid());
            List<SpecBoostManager.SpecBoost<?>> specBoosts = SpecBoostManager.getSpecBoosts(newSpec);
            if (specBoosts.isEmpty()) {
                return;
            }
            SpecBoostManager.SpecBoost<?> specBoost = specBoosts.get(databasePlayer.getSelectedSpecBoost(newSpec));
            if (specBoost.isDisabled()) {
                // find spec boost that is not disabled
                specBoost = specBoosts.stream()
                                      .filter(boost -> !boost.isDisabled())
                                      .findFirst()
                                      .orElse(null);
                if (specBoost == null) {
                    return;
                }
            }
            applyBoost(warlordsPlayer, specBoost);
        }
    }

    @Override
    public void onSpecChange(@Nonnull WarlordsEntity wp, Specializations oldSpec) {
        Specializations newSpec = wp.getSpecClass();
        if (!(wp instanceof WarlordsPlayer warlordsPlayer)) {
            return;
        }
        PlayerSpecAppliedBoost oldAppliedBoost = playerSpecBoosts.get(warlordsPlayer);
        if (oldAppliedBoost != null) {
            HandlerList.unregisterAll(oldAppliedBoost.boost);
            oldAppliedBoost.boost.unapply(warlordsPlayer);
        }
        if (random) {
            giveRandomBoost(warlordsPlayer, newSpec);
        } else {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(wp.getUuid());
            List<SpecBoostManager.SpecBoost<?>> specBoosts = SpecBoostManager.getSpecBoosts(newSpec);
            if (specBoosts.isEmpty()) {
                return;
            }
            SpecBoostManager.SpecBoost<?> specBoost = specBoosts.get(databasePlayer.getSelectedSpecBoost(newSpec));
            if (specBoost.isDisabled()) {
                specBoost = specBoosts.stream()
                                      .filter(boost -> !boost.isDisabled())
                                      .findFirst()
                                      .orElse(null);
                if (specBoost == null) {
                    return;
                }
            }
            applyBoost(warlordsPlayer, specBoost);
        }
    }

    @Override
    public void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {
        List<WarlordsPlayer> withBoosts = players.stream()
                .filter(WarlordsPlayer.class::isInstance)
                .map(WarlordsPlayer.class::cast)
                .filter(playerSpecBoosts::containsKey)
                .toList();
        notifyTeammatesBatch(withBoosts);
    }

    public Map<WarlordsEntity, PlayerSpecAppliedBoost> getPlayerSpecBoosts() {
        return playerSpecBoosts;
    }

    private void giveRandomBoost(WarlordsPlayer warlordsPlayer, Specializations newSpec) {
        SpecBoostManager.SpecBoost<?> specBoost = preassignedSpecBoosts.get(newSpec);
        if (specBoost == null) {
            return;
        }
        applyBoost(warlordsPlayer, specBoost);
    }

    private void applyBoost(WarlordsPlayer warlordsPlayer, SpecBoostManager.SpecBoost<?> specBoost) {
        SpecBoostManager.Boost boost = specBoost.create();
        boost.apply(warlordsPlayer);
        warlordsPlayer.getGame().registerEvents(boost);
        playerSpecBoosts.put(warlordsPlayer, new PlayerSpecAppliedBoost(specBoost, boost));
        if (warlordsPlayer.getEntity() instanceof Player player) {
            warlordsPlayer.getAbilities().forEach(abstractAbility -> abstractAbility.updateDescription(player));
        }
    }

    private @Nullable Component buildTeammateSpecBoostLine(WarlordsPlayer player) {
        PlayerSpecAppliedBoost appliedBoost = playerSpecBoosts.get(player);
        if (appliedBoost == null) {
            return null;
        }
        return Component.textOfChildren(
                player.getColoredName(),
                Component.text(": ", NamedTextColor.GRAY),
                formatSpecBoost(appliedBoost.specBoost)
        );
    }

    private void notifyTeammatesBatch(List<WarlordsPlayer> players) {
        if (players.isEmpty() || game == null) {
            return;
        }
        Map<Team, List<WarlordsPlayer>> byTeam = new HashMap<>();
        for (WarlordsPlayer player : players) {
            byTeam.computeIfAbsent(player.getTeam(), team -> new ArrayList<>()).add(player);
        }
        for (List<WarlordsPlayer> teamPlayers : byTeam.values()) {
            PlayerFilter.playingGame(game)
                    .teammatesOf(teamPlayers.get(0))
                    .forEach(recipient -> {
                        List<Component> lines = teamPlayers.stream()
                                .filter(member -> member != recipient)
                                .map(this::buildTeammateSpecBoostLine)
                                .filter(Objects::nonNull)
                                .toList();
                        if (lines.isEmpty()) {
                            return;
                        }
                        recipient.sendMessage(MESSAGE_DIVIDER);
                        lines.forEach(recipient::sendMessage);
                        recipient.sendMessage(MESSAGE_DIVIDER);
                    });
        }
    }

    private static Component formatSpecBoost(SpecBoostManager.SpecBoost<?> boost) {
        List<Component> hoverParts = new ArrayList<>();
        hoverParts.add(boost.getName());
        for (Component loreLine : boost.getDescriptionLore()) {
            hoverParts.add(loreLine.color(NamedTextColor.GRAY));
        }
        return Component.text(boost.getStringName(), NamedTextColor.GREEN)
                        .hoverEvent(HoverEvent.showText(ComponentUtils.flattenComponentWithNewLine(hoverParts)));
    }

    public record PlayerSpecAppliedBoost(SpecBoostManager.SpecBoost<?> specBoost, SpecBoostManager.Boost boost) {
    }

}
