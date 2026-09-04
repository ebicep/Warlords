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
    private static final int UPDATE_INTERVAL = 10;
    private static final int NAME_INTERVAL = 2;

    private final Game game;
    private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>();
    private final Map<WarlordsEntity, WarlordsPlayerName> cachedNames = new HashMap<>();
    private final Set<UUID> dirtyViewers = new HashSet<>();
    private final Set<UUID> dirtyTargets = new HashSet<>();
    private final Set<UUID> dirtyTabNames = new HashSet<>();
    private final Set<UUID> sidebarDirty = new HashSet<>();
    private final Set<UUID> dirtyHealthTargets = new HashSet<>();
    private final Set<UUID> pendingHealthViewers = new HashSet<>();
    private int updateTick;
    private int nameTick;

    public PlayingStateScoreboardUpdater(Game game) {
        this.game = game;
        for (ScoreboardHandler handler : game.getScoreboardHandlers()) {
            handler.registerChangeHandler(h -> markAllSidebarDirty());
        }
    }

    public void markNamesDirty(WarlordsEntity entity) {
        dirtyTargets.add(entity.getUuid());
        dirtyViewers.addAll(gamePlayers.keySet());
    }

    public void markTabNameDirty(WarlordsPlayer player) {
        dirtyTabNames.add(player.getUuid());
        markNamesDirty(player);
    }

    private void markAllSidebarDirty() {
        sidebarDirty.addAll(gamePlayers.keySet());
    }

    private static boolean inBucket(UUID uuid, int bucket, int interval) {
        return Math.floorMod(uuid.hashCode(), interval) == bucket;
    }

    private void markHealthDirty(UUID target) {
        dirtyHealthTargets.add(target);
        pendingHealthViewers.addAll(gamePlayers.keySet());
    }

    private void markPlayerJoined(UUID uuid) {
        dirtyViewers.addAll(gamePlayers.keySet());
        dirtyTargets.addAll(gamePlayers.keySet());
        dirtyTabNames.add(uuid);
        sidebarDirty.addAll(gamePlayers.keySet());
    }

    public void removePlayer(UUID uuid) {
        gamePlayers.remove(uuid);
        cachedNames.remove(Warlords.getPlayer(uuid));
        dirtyViewers.remove(uuid);
        dirtyTargets.remove(uuid);
        dirtyTabNames.remove(uuid);
        sidebarDirty.remove(uuid);
        dirtyHealthTargets.remove(uuid);
        pendingHealthViewers.remove(uuid);
    }

    /**
     * Health, tab list names, and sidebar — distributed across {@link #UPDATE_INTERVAL} ticks.
     * Also flushes above-head names if still dirty (distributed across {@link #NAME_INTERVAL} ticks).
     */
    public void update() {
        validGamePlayersCache();
        int bucket = updateTick++ % UPDATE_INTERVAL;
        for (UUID uuid : gamePlayers.keySet()) {
            if (!inBucket(uuid, bucket, UPDATE_INTERVAL)) {
                continue;
            }
            flushTabName(uuid);
            flushHealthForViewer(uuid);
            flushSidebar(uuid);
        }
        // Only clear after all pending viewers flushed; mid-cycle dirty re-adds them.
        if (pendingHealthViewers.isEmpty()) {
            dirtyHealthTargets.clear();
        }
        updateAboveHeadNames();
    }

    /**
     * Above-head team prefix/suffix overlays — distributed across {@link #NAME_INTERVAL} ticks.
     */
    public void updateAboveHeadNames() {
        if (dirtyViewers.isEmpty() && dirtyTargets.isEmpty()) {
            return;
        }
        int bucket = nameTick++ % NAME_INTERVAL;
        Set<UUID> targets = dirtyTargets.isEmpty() ? Set.copyOf(gamePlayers.keySet()) : Set.copyOf(dirtyTargets);
        for (UUID viewerUuid : gamePlayers.keySet()) {
            if (!inBucket(viewerUuid, bucket, NAME_INTERVAL)) {
                continue;
            }
            if (!dirtyViewers.remove(viewerUuid)) {
                continue;
            }
            GamePlayer gamePlayer = gamePlayers.get(viewerUuid);
            if (gamePlayer == null) {
                continue;
            }
            updateNamesForViewer(gamePlayer, targets);
        }
        if (dirtyViewers.isEmpty()) {
            dirtyTargets.clear();
        }
    }

    private void flushTabName(UUID uuid) {
        if (!dirtyTabNames.remove(uuid)) {
            return;
        }
        GamePlayer gamePlayer = gamePlayers.get(uuid);
        if (gamePlayer == null) {
            return;
        }
        WarlordsPlayer warlordsPlayer = gamePlayer.getWarlordsPlayer();
        if (warlordsPlayer == null || !(warlordsPlayer.getEntity() instanceof Player player)) {
            dirtyTabNames.add(uuid);
            return;
        }
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
        // Base parts feed above-head overlays; re-dirty in case names already flushed this period.
        markNamesDirty(warlordsPlayer);
    }

    private void flushSidebar(UUID uuid) {
        if (!sidebarDirty.remove(uuid)) {
            return;
        }
        GamePlayer gamePlayer = gamePlayers.get(uuid);
        if (gamePlayer != null) {
            updateBasedOnGameScoreboards(gamePlayer.getCustomScoreboard(), gamePlayer.getWarlordsPlayer());
        }
    }

    private void validGamePlayersCache() {
        game.forEachOnlinePlayer((player, team) -> {
            WarlordsEntity p = Warlords.getPlayer(player);
            UUID uuid = player.getUniqueId();
            GamePlayer gp = gamePlayers.get(uuid);
            int newHealth = p == null ? 0 : Math.round(p.getCurrentHealth());
            CustomScoreboard scoreboard = CustomScoreboard.getPlayerScoreboard(player);
            if (gp != null) {
                gp.setCustomScoreboard(scoreboard);
                gp.setWarlordsPlayer((WarlordsPlayer) p);
                if (gp.getHealth() != newHealth) {
                    gp.setHealth(newHealth);
                    markHealthDirty(uuid);
                }
            } else {
                gamePlayers.put(uuid, new GamePlayer(scoreboard, (WarlordsPlayer) p, newHealth));
                markPlayerJoined(uuid);
                markHealthDirty(uuid);
            }
        });
    }

    private static Component getClassComponent(WarlordsEntity p) {
        return p.getSpec().getClassNameShortWithBrackets(p.getSpecClass().specType.getTextColor());
    }

    @Nonnull
    private static Component getLevelComponent(UUID uuid, WarlordsEntity otherWarlordsPlayer) {
        return ExperienceManager.getLevelStringBracket(ExperienceManager.getLevelForSpec(uuid, otherWarlordsPlayer.getSpecClass()));
    }

    public void updateBasedOnGameState(WarlordsPlayer warlordsPlayer) {
        markNamesDirty(warlordsPlayer);
        sidebarDirty.add(warlordsPlayer.getUuid());
        GamePlayer gp = gamePlayers.get(warlordsPlayer.getUuid());
        if (gp != null) {
            gp.setHealthObjectiveInitialized(false);
            markHealthDirty(warlordsPlayer.getUuid());
        }
    }

    private void flushHealthForViewer(UUID viewerUuid) {
        GamePlayer viewer = gamePlayers.get(viewerUuid);
        if (viewer == null) {
            return;
        }
        boolean newHealth = !viewer.isHealthObjectiveInitialized();
        boolean pending = pendingHealthViewers.remove(viewerUuid);
        if (!newHealth && !pending) {
            return;
        }
        CustomScoreboard customScoreboard = viewer.getCustomScoreboard();
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        Objective health = customScoreboard.getHealth();
        if (health == null || scoreboard.getObjective("health") == null) {
            health = scoreboard.registerNewObjective("health", Criteria.DUMMY, Component.text("❤", NamedTextColor.RED));
            health.setDisplaySlot(DisplaySlot.BELOW_NAME);
            customScoreboard.setHealth(health);
        }
        if (newHealth) {
            viewer.setHealthObjectiveInitialized(true);
            for (GamePlayer target : gamePlayers.values()) {
                applyHealthScore(health, target);
            }
            return;
        }
        for (UUID targetUuid : dirtyHealthTargets) {
            applyHealthScore(health, gamePlayers.get(targetUuid));
        }
    }

    private static void applyHealthScore(Objective health, @Nullable GamePlayer target) {
        if (target == null || target.getWarlordsPlayer() == null) {
            return;
        }
        health.getScore(target.getWarlordsPlayer().getName()).setScore(target.getHealth());
    }

    private void updateNamesForViewer(@Nonnull GamePlayer viewerGamePlayer, @Nonnull Set<UUID> targetUuids) {
        WarlordsPlayer viewer = viewerGamePlayer.getWarlordsPlayer();
        CustomScoreboard customScoreboard = viewerGamePlayer.getCustomScoreboard();
        Scoreboard scoreboard = customScoreboard.getScoreboard();

        for (UUID targetUuid : targetUuids) {
            GamePlayer targetGamePlayer = gamePlayers.get(targetUuid);
            if (targetGamePlayer == null) {
                continue;
            }
            WarlordsPlayer target = targetGamePlayer.getWarlordsPlayer();
            if (target == null || target instanceof WarlordsPlayerDisguised) {
                continue;
            }
            WarlordsPlayerName name = cachedNames.computeIfAbsent(target, k -> new WarlordsPlayerName(target));

            Entity entity = target.getEntity();
            Team playerTeam = scoreboard.getEntityTeam(entity);
            if (playerTeam == null) {
                playerTeam = scoreboard.registerNewTeam(((CraftEntity) entity).getHandle().getScoreboardName());
                playerTeam.addEntity(entity);
            }
            playerTeam.color(target.getTeam().getTeamColor());

            OverlayResult overlay = buildOverlay(name, viewer, target);
            if (!playerTeam.prefix().equals(overlay.prefix())) {
                playerTeam.prefix(overlay.prefix());
            }
            if (!playerTeam.suffix().equals(overlay.suffix())) {
                playerTeam.suffix(overlay.suffix());
            }
        }
    }

    private static OverlayResult buildOverlay(
            @Nonnull WarlordsPlayerName name,
            @Nullable WarlordsPlayer viewer,
            @Nonnull WarlordsPlayer target
    ) {
        TextComponent.Builder prefix = Component.text();
        TextComponent.Builder suffix = Component.text();
        suffix.append(name.getBaseSuffix());
        if (viewer != null) {
            for (AbstractCooldown<?> cd : viewer.getCooldownManager().getCooldowns()) {
                if (!cd.changesPlayerName()) {
                    continue;
                }
                appendPrefixOverlay(prefix, cd.addPrefixFromSelf(), target);
                appendSuffixOverlay(suffix, cd.addSuffixFromSelf(), target);
            }
            for (AbstractCooldown<?> cd : target.getCooldownManager().getCooldowns()) {
                if (!cd.changesPlayerName()) {
                    continue;
                }
                appendPrefixOverlay(prefix, cd.addPrefixFromOther(), viewer);
                appendSuffixOverlay(suffix, cd.addSuffixFromOther(), viewer);
            }
        }
        prefix.append(name.getBasePrefix());
        return new OverlayResult(prefix.build().compact(), suffix.build().compact());
    }

    private static void appendPrefixOverlay(
            TextComponent.Builder prefix,
            @Nullable PlayerNameInstance.PlayerNameData data,
            WarlordsEntity peer
    ) {
        if (data == null || !data.displayPredicate().test(peer)) {
            return;
        }
        prefix.append(data.resolveText().append(Component.space()));
    }

    private static void appendSuffixOverlay(
            TextComponent.Builder suffix,
            @Nullable PlayerNameInstance.PlayerNameData data,
            WarlordsEntity peer
    ) {
        if (data == null || !data.displayPredicate().test(peer)) {
            return;
        }
        suffix.append(Component.space().append(data.resolveText()));
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

    public void forceUpdatePlayerGameScoreboards() {
        validGamePlayersCache();
        gamePlayers.values().forEach(gamePlayer ->
                updateBasedOnGameScoreboards(gamePlayer.getCustomScoreboard(), gamePlayer.getWarlordsPlayer()));
    }

    static final class GamePlayer {

        private CustomScoreboard customScoreboard;
        @Nullable
        private WarlordsPlayer warlordsPlayer;
        private int health;
        private boolean healthObjectiveInitialized;

        GamePlayer(CustomScoreboard customScoreboard, @Nullable WarlordsPlayer warlordsPlayer, int health) {
            this.customScoreboard = customScoreboard;
            this.warlordsPlayer = warlordsPlayer;
            this.health = health;
        }

        public CustomScoreboard getCustomScoreboard() {
            return customScoreboard;
        }

        public void setCustomScoreboard(CustomScoreboard customScoreboard) {
            this.customScoreboard = customScoreboard;
        }

        @Nullable
        public WarlordsPlayer getWarlordsPlayer() {
            return warlordsPlayer;
        }

        public void setWarlordsPlayer(@Nullable WarlordsPlayer warlordsPlayer) {
            this.warlordsPlayer = warlordsPlayer;
        }

        public int getHealth() {
            return health;
        }

        public void setHealth(int health) {
            this.health = health;
        }

        public boolean isHealthObjectiveInitialized() {
            return healthObjectiveInitialized;
        }

        public void setHealthObjectiveInitialized(boolean healthObjectiveInitialized) {
            this.healthObjectiveInitialized = healthObjectiveInitialized;
        }

    }

    private record OverlayResult(Component prefix, Component suffix) {
    }

    static final class WarlordsPlayerName {

        private Component basePrefix;
        private Component baseSuffix;

        public WarlordsPlayerName() {
        }

        public WarlordsPlayerName(WarlordsEntity warlordsPlayer) {
            this.basePrefix = getClassComponent(warlordsPlayer).compact();
            this.baseSuffix = getLevelComponent(warlordsPlayer.getUuid(), warlordsPlayer).compact();
        }

        public Component getBasePrefix() {
            return basePrefix;
        }

        public void setBasePrefix(Component basePrefix) {
            this.basePrefix = basePrefix.compact();
        }

        public Component getBaseSuffix() {
            return baseSuffix;
        }

        public void setBaseSuffix(Component baseSuffix) {
            this.baseSuffix = baseSuffix.compact();
        }

    }

}
