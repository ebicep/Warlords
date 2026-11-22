package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.WeaponDisplayMarker;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ComponentUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
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
            Component.text("---------------------------------------", NamedTextColor.DARK_BLUE);

    private static final Classes[] ROSTER_CLASS_ORDER = {
            Classes.ARCANIST,
            Classes.MAGE,
            Classes.WARRIOR,
            Classes.ROGUE,
            Classes.SHAMAN,
            Classes.PALADIN,
    };

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
        List<WarlordsPlayer> warlordsPlayers = players.stream()
                .filter(WarlordsPlayer.class::isInstance)
                .map(WarlordsPlayer.class::cast)
                .toList();
        announceSpecBoostRoster(warlordsPlayers);
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

    private void announceSpecBoostRoster(List<WarlordsPlayer> allPlayers) {
        if (allPlayers.isEmpty() || game == null) {
            return;
        }
        for (WarlordsPlayer recipient : allPlayers) {
            Team team = recipient.getTeam();
            if (team != Team.BLUE && team != Team.RED) {
                continue;
            }
            List<WarlordsPlayer> teamRoster = allPlayers.stream()
                    .filter(player -> player.getTeam() == team)
                    .sorted(this::compareRosterPlayers)
                    .toList();
            recipient.sendMessage(MESSAGE_DIVIDER);
            for (WarlordsPlayer member : teamRoster) {
                recipient.sendMessage(buildTeamRosterLine(member, recipient));
            }
            recipient.sendMessage(MESSAGE_DIVIDER);
        }
    }

    private Component buildTeamRosterLine(WarlordsPlayer subject, WarlordsPlayer viewer) {
        NamedTextColor nameColor = NamedTextColor.RED;
        if (subject.getName().equals(viewer.getName())) {
            nameColor = NamedTextColor.AQUA;
        } else if (subject.getTeam() == Team.BLUE) {
            nameColor = NamedTextColor.BLUE;
        }
        return Component.textOfChildren(
                Component.text(subject.getName(), nameColor),
                Component.text(" - ", NamedTextColor.GRAY),
                formatSpecForRoster(subject)
        );
    }

    private Component formatSpecForRoster(WarlordsPlayer subject) {
        Specializations spec = subject.getSpecClass();
        Component specComponent = Component.text(spec.name, spec.specType.getTextColor());
        PlayerSpecAppliedBoost appliedBoost = playerSpecBoosts.get(subject);
        if (appliedBoost != null) {
            specComponent = specComponent.hoverEvent(HoverEvent.showText(specBoostHover(appliedBoost.specBoost)));
        }
        return specComponent;
    }

    private static Component specBoostHover(SpecBoostManager.SpecBoost<?> boost) {
        List<Component> hoverParts = new ArrayList<>();
        hoverParts.add(boost.getName());
        for (Component loreLine : boost.getDescriptionLore()) {
            hoverParts.add(loreLine.color(NamedTextColor.GRAY));
        }
        return ComponentUtils.flattenComponentWithNewLine(hoverParts);
    }

    private int compareRosterPlayers(WarlordsPlayer a, WarlordsPlayer b) {
        int typeCompare = Integer.compare(a.getSpecClass().specType.ordinal(), b.getSpecClass().specType.ordinal());
        if (typeCompare != 0) {
            return typeCompare;
        }
        int classCompare = Integer.compare(
                indexOfRosterClass(Specializations.getClass(a.getSpecClass())),
                indexOfRosterClass(Specializations.getClass(b.getSpecClass()))
        );
        if (classCompare != 0) {
            return classCompare;
        }
        return a.getName().compareToIgnoreCase(b.getName());
    }

    private static int indexOfRosterClass(Classes classes) {
        for (int i = 0; i < ROSTER_CLASS_ORDER.length; i++) {
            if (ROSTER_CLASS_ORDER[i] == classes) {
                return i;
            }
        }
        return ROSTER_CLASS_ORDER.length;
    }

    public record PlayerSpecAppliedBoost(SpecBoostManager.SpecBoost<?> specBoost, SpecBoostManager.Boost boost) {
    }

}
