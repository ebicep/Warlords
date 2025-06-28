package com.ebicep.warlords.game.state;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.WarlordsPlayerDisguised;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.instances.type.PlayerNameInstance;
import com.ebicep.warlords.util.java.JavaUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PlayingStateScoreboardUpdater {

    private static final TextComponent WHITE_FLAG = Component.text("⚑", NamedTextColor.WHITE);
    private static final TextComponent WHITE_FLAG_SPACE = Component.text(" ⚑", NamedTextColor.WHITE);

    private final Game game;
    private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>();
    private final Map<WarlordsEntity, WarlordsPlayerName> cachedNames = new HashMap<>();

    public PlayingStateScoreboardUpdater(Game game) {
        this.game = game;
    }

    public void update() {
        game.forEachOnlinePlayer((player, team) -> {
            WarlordsEntity p = Warlords.getPlayer(player);
            UUID uuid = player.getUniqueId();
            gamePlayers.put(uuid, new GamePlayer(
                            CustomScoreboard.getPlayerScoreboard(player),
                            (WarlordsPlayer) p,
                            p == null ? 0 : Math.round(p.getCurrentHealth())
                    )
            );
        });
        gamePlayers.forEach((uuid, gamePlayer) -> {
            WarlordsPlayer warlordsPlayer = gamePlayer.warlordsPlayer();
            if (warlordsPlayer != null && warlordsPlayer.isUpdateTabName() && warlordsPlayer.getEntity() instanceof Player player) {
                warlordsPlayer.setUpdateTabName(false);
                Component classComponent = getClassComponent(warlordsPlayer);
                Component levelComponent = getLevelComponent(uuid, warlordsPlayer);

                TextComponent.Builder baseSuffix = Component.text().append(levelComponent);
                TextComponent.Builder playerTabName = Component
                        .text()
                        .append(classComponent)
                        .append(Component.text(warlordsPlayer.getName(), warlordsPlayer.getTeam().getTeamColor()))
                        .append(levelComponent);
                if (warlordsPlayer.getCarriedFlag() != null) {
                    baseSuffix.append(WHITE_FLAG_SPACE);
                    playerTabName.append(WHITE_FLAG);
                }
                WarlordsPlayerName name = cachedNames.computeIfAbsent(warlordsPlayer, k -> new WarlordsPlayerName());
                name.setBasePrefix(classComponent);
                name.setBaseSuffix(baseSuffix.build());
                player.playerListName(playerTabName.build());
            }

            CustomScoreboard customScoreboard = gamePlayer.customScoreboard();
            updateBasedOnGameState(customScoreboard, warlordsPlayer);
        });
    }

    private static Component getClassComponent(WarlordsEntity p) {
        return p.getSpec().getClassNameShortWithBrackets(p.getSpecClass().specType.getTextColor());
    }

    @Nonnull
    private static Component getLevelComponent(UUID uuid, WarlordsEntity otherWarlordsPlayer) {
        return ExperienceManager.getLevelStringBracket(ExperienceManager.getLevelForSpec(uuid, otherWarlordsPlayer.getSpecClass()));
    }

    public void updateBasedOnGameState(CustomScoreboard customScoreboard, WarlordsPlayer warlordsPlayer) {
        updateHealth(customScoreboard);
        updateNames(customScoreboard, warlordsPlayer);
        updateBasedOnGameScoreboards(customScoreboard, warlordsPlayer);
    }

    private void updateHealth(@Nonnull CustomScoreboard customScoreboard) {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        Objective health = customScoreboard.getHealth();
        if (health == null || scoreboard.getObjective("health") == null) {
            health = scoreboard.registerNewObjective("health", Criteria.DUMMY, Component.text("❤", NamedTextColor.RED));
            health.setDisplaySlot(DisplaySlot.BELOW_NAME);
            customScoreboard.setHealth(health);
        }
        Objective finalHealth = health;
        gamePlayers.values().forEach(gamePlayer -> {
            if (gamePlayer.warlordsPlayer() == null) {
                return;
            }
            finalHealth.getScore(gamePlayer.warlordsPlayer().getName()).setScore(gamePlayer.health());
        });
    }

    public void updateNames(@Nonnull CustomScoreboard customScoreboard, @Nullable WarlordsEntity warlordsPlayer) {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        List<AbstractCooldown<?>> cooldowns;
        if (warlordsPlayer != null) {
            cooldowns = warlordsPlayer.getCooldownManager().getCooldowns();
        } else {
            cooldowns = new ArrayList<>();
        }
        gamePlayers.values().forEach(otherGamePlayer -> {
            WarlordsPlayer otherWarlordsPlayer = otherGamePlayer.warlordsPlayer();
            if (otherWarlordsPlayer == null || otherWarlordsPlayer instanceof WarlordsPlayerDisguised) {
                return;
            }
            Entity entity = otherWarlordsPlayer.getEntity();
            UUID uuid = otherWarlordsPlayer.getUuid();
            List<AbstractCooldown<?>> otherPlayerCooldowns = otherWarlordsPlayer.getCooldownManager().getCooldowns();
            Team playerTeam = scoreboard.getEntityTeam(entity);
            if (playerTeam == null) {
                playerTeam = scoreboard.registerNewTeam(((CraftEntity) entity).getHandle().getScoreboardName());
                playerTeam.addEntity(entity);
            }
            playerTeam.color(otherWarlordsPlayer.getTeam().getTeamColor());

            WarlordsPlayerName name = cachedNames.computeIfAbsent(otherWarlordsPlayer, k -> new WarlordsPlayerName(otherWarlordsPlayer));
            TextComponent.Builder prefix = Component.text();
            TextComponent.Builder suffix = Component.text();
            suffix.append(name.getBaseSuffix());
            if (warlordsPlayer != null) {
                cooldowns.forEach(cd -> {
                    PlayerNameInstance.PlayerNameData prefixFromSelf = cd.addPrefixFromSelf();
                    if (prefixFromSelf != null && prefixFromSelf.displayPredicate().test(otherWarlordsPlayer)) {
                        prefix.append(prefixFromSelf.text().append(Component.space()));
                    }
                    PlayerNameInstance.PlayerNameData suffixFromSelf = cd.addSuffixFromSelf();
                    if (suffixFromSelf != null && suffixFromSelf.displayPredicate().test(otherWarlordsPlayer)) {
                        suffix.append(Component.space().append(suffixFromSelf.text()));
                    }
                });
                otherPlayerCooldowns.forEach(cd -> {
                    PlayerNameInstance.PlayerNameData prefixFromEnemy = cd.addPrefixFromOther();
                    if (prefixFromEnemy != null && prefixFromEnemy.displayPredicate().test(warlordsPlayer)) {
                        prefix.append(prefixFromEnemy.text().append(Component.space()));
                    }
                    PlayerNameInstance.PlayerNameData suffixFromEnemy = cd.addSuffixFromOther();
                    if (suffixFromEnemy != null && suffixFromEnemy.displayPredicate().test(warlordsPlayer)) {
                        suffix.append(Component.space().append(suffixFromEnemy.text()));
                    }
                });
            }
            prefix.append(name.getBasePrefix());
            playerTeam.prefix(prefix.build());
            playerTeam.suffix(suffix.build());
        });
    }

    private void updateBasedOnGameScoreboards(@Nonnull CustomScoreboard customScoreboard, @Nullable WarlordsPlayer warlordsPlayer) {
        List<Component> scoreboard = new ArrayList<>();
        ScoreboardHandler lastHandler = null;
        String lastGroup = null;
        boolean lastWasEmpty = true;
        for (ScoreboardHandler handler : JavaUtils.iterable(game
                .getScoreboardHandlers()
                .stream()
                .sorted(Comparator.comparing((ScoreboardHandler sh) -> sh.getPriority(warlordsPlayer)))
        )) {
            String group = handler.getGroup();
            if ((lastGroup == null || !lastGroup.equals(group)) && !lastWasEmpty && handler.emptyLinesBetween() && lastHandler.emptyLinesBetween()) {
                scoreboard.add(Component.empty());
                lastWasEmpty = true;
            }
            lastHandler = handler;
            lastGroup = group;
            List<Component> handlerContents = handler.computeLines(warlordsPlayer);
            if (!handlerContents.isEmpty()) {
                lastWasEmpty = false;
                scoreboard.addAll(handlerContents);
            }
        }
        customScoreboard.giveNewSideBar(false, scoreboard);
    }

    record GamePlayer(CustomScoreboard customScoreboard, @Nullable WarlordsPlayer warlordsPlayer, int health) {

    }

    static final class WarlordsPlayerName {

        private Component basePrefix;
        private Component baseSuffix;

        public WarlordsPlayerName() {
        }

        public WarlordsPlayerName(WarlordsEntity warlordsPlayer) {
            this.basePrefix = getClassComponent(warlordsPlayer);
            this.baseSuffix = getLevelComponent(warlordsPlayer.getUuid(), warlordsPlayer);
        }

        public Component getBasePrefix() {
            return basePrefix;
        }

        public void setBasePrefix(Component basePrefix) {
            this.basePrefix = basePrefix;
        }

        public Component getBaseSuffix() {
            return baseSuffix;
        }

        public void setBaseSuffix(Component baseSuffix) {
            this.baseSuffix = baseSuffix;
        }

    }

}
