package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import com.ebicep.warlords.util.bukkit.ComponentUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AbilityChangeOption implements Option {

    public enum Mode {
        RANDOM,
        ON_DEATH,
    }

    private static final int MIN_SWAP_TIME = 50;
    private static final int MAX_SWAP_TIME = 80;
    private static final int MAX_SECONDS_WITHOUT_SWAP = 4 * 60;
    private static final int MAX_LOADOUT_ATTEMPTS = 25;
    private static final float BASE_HEALTH = 5500f;
    private static final float BASE_SPEED_MODIFIER = 13f;
    private static final float BASE_KNOCKBACK_MODIFIER = 0f;
    private static final Component MESSAGE_DIVIDER =
            Component.text("---------------------------------------", NamedTextColor.DARK_GRAY);

    private static final Map<Class<? extends AbilityIcon>, List<Ability<?>>> ABILITY_POOLS = buildAbilityPools();
    private static final Map<Class<? extends AbstractAbility>, Specializations> ABILITY_SPEC_MAP = buildAbilitySpecMap();
    private static final Set<String> AMBIGUOUS_ABILITY_NAMES = buildAmbiguousAbilityNames();

    private final Mode mode;
    private Game game;
    private int secondsUntilNextSwap = 0;
    private final Map<UUID, Integer> secondsSinceLastSwap = new HashMap<>();

    public AbilityChangeOption(Mode mode) {
        this.mode = mode;
    }

    private static Map<Class<? extends AbilityIcon>, List<Ability<?>>> buildAbilityPools() {
        Map<Class<? extends AbilityIcon>, List<Ability<?>>> pools = new HashMap<>();
        pools.put(WeaponAbilityIcon.class, new ArrayList<>());
        pools.put(RedAbilityIcon.class, new ArrayList<>());
        pools.put(BlueAbilityIcon.class, new ArrayList<>());
        pools.put(PurpleAbilityIcon.class, new ArrayList<>());
        pools.put(OrangeAbilityIcon.class, new ArrayList<>());

        Set<Ability<?>> seen = new HashSet<>();
        for (Specializations spec : Specializations.VALUES) {
            for (AbstractAbility abstractAbility : spec.create(ConfigManager.DEFAULT_NAMESPACES).getAbilities()) {
                Ability<?> ability = Ability.getAbility(abstractAbility.getClass());
                if (ability != null && seen.add(ability)) {
                    Class<? extends AbilityIcon> iconType = getIconType(ability.clazz);
                    if (iconType != null) {
                        pools.get(iconType).add(ability);
                    }
                }
            }
        }

        Map<Class<? extends AbilityIcon>, List<Ability<?>>> immutablePools = new HashMap<>();
        for (Map.Entry<Class<? extends AbilityIcon>, List<Ability<?>>> entry : pools.entrySet()) {
            immutablePools.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(immutablePools);
    }

    private static Map<Class<? extends AbstractAbility>, Specializations> buildAbilitySpecMap() {
        Map<Class<? extends AbstractAbility>, Specializations> map = new HashMap<>();
        for (Map.Entry<Specializations, Ability<?>[]> entry : Ability.SPEC_ABILITIES.entrySet()) {
            for (Ability<?> ability : entry.getValue()) {
                if (ability != null) {
                    map.put(ability.clazz, entry.getKey());
                }
            }
        }
        return Collections.unmodifiableMap(map);
    }

    private static Set<String> buildAmbiguousAbilityNames() {
        Map<String, Integer> counts = new HashMap<>();
        for (Ability<?> ability : Ability.VALUES) {
            AbstractAbility instance = ability.create.get();
            instance.init(instance.getBuilder());
            counts.merge(instance.getName(), 1, Integer::sum);
        }
        Set<String> ambiguous = new HashSet<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > 1) {
                ambiguous.add(entry.getKey());
            }
        }
        return Collections.unmodifiableSet(ambiguous);
    }

    @Nullable
    private static Class<? extends AbilityIcon> getIconType(Class<?> abilityClass) {
        if (WeaponAbilityIcon.class.isAssignableFrom(abilityClass)) {
            return WeaponAbilityIcon.class;
        }
        if (RedAbilityIcon.class.isAssignableFrom(abilityClass)) {
            return RedAbilityIcon.class;
        }
        if (BlueAbilityIcon.class.isAssignableFrom(abilityClass)) {
            return BlueAbilityIcon.class;
        }
        if (PurpleAbilityIcon.class.isAssignableFrom(abilityClass)) {
            return PurpleAbilityIcon.class;
        }
        if (OrangeAbilityIcon.class.isAssignableFrom(abilityClass)) {
            return OrangeAbilityIcon.class;
        }
        return null;
    }

    @Nullable
    private static Class<? extends AbilityIcon> getIconType(AbstractAbility ability) {
        if (ability instanceof WeaponAbilityIcon) {
            return WeaponAbilityIcon.class;
        }
        if (ability instanceof RedAbilityIcon) {
            return RedAbilityIcon.class;
        }
        if (ability instanceof BlueAbilityIcon) {
            return BlueAbilityIcon.class;
        }
        if (ability instanceof PurpleAbilityIcon) {
            return PurpleAbilityIcon.class;
        }
        if (ability instanceof OrangeAbilityIcon) {
            return OrangeAbilityIcon.class;
        }
        return null;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        if (mode != Mode.ON_DEATH) {
            return;
        }
        game.registerEvents(new Listener() {

            @EventHandler
            public void onRespawn(WarlordsRespawnEvent event) {
                if (!event.getWarlordsEntity().getGame().equals(game)) {
                    return;
                }
                WarlordsEntity entity = event.getWarlordsEntity();
                if (swapAbilities(entity, false) && entity instanceof WarlordsPlayer warlordsPlayer) {
                    notifyTeammatesSingle(warlordsPlayer);
                }
            }

        });
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer warlordsPlayer) {
            applyBaseStats(warlordsPlayer, true);
        }
    }

    @Override
    public void onSpecChange(@Nonnull WarlordsEntity player, Specializations oldSpec) {
        if (player instanceof WarlordsPlayer warlordsPlayer) {
            applyBaseStats(warlordsPlayer, false);
        }
    }

    @Override
    public void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {
        List<WarlordsPlayer> swapped = new ArrayList<>();
        for (WarlordsEntity player : players) {
            if (swapAbilities(player, true) && player instanceof WarlordsPlayer warlordsPlayer) {
                swapped.add(warlordsPlayer);
            }
        }
        notifyTeammatesBatch(swapped);
    }

    @Override
    public void start(@Nonnull Game game) {
        this.game = game;
        startPerPlayerSwapTimer(game);
        if (mode == Mode.RANDOM) {
            startRandomSwapTimer(game);
        }
    }

    private void startPerPlayerSwapTimer(@Nonnull Game game) {
        new GameRunnable(game) {

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    return;
                }
                List<WarlordsPlayer> swapped = new ArrayList<>();
                game.warlordsPlayers().forEach(player -> {
                    UUID uuid = player.getUuid();
                    if (!secondsSinceLastSwap.containsKey(uuid)) {
                        return;
                    }
                    int seconds = secondsSinceLastSwap.get(uuid) + 1;
                    if (seconds >= MAX_SECONDS_WITHOUT_SWAP) {
                        if (swapAbilities(player, false)) {
                            swapped.add(player);
                        } else {
                            secondsSinceLastSwap.put(uuid, seconds);
                        }
                    } else {
                        secondsSinceLastSwap.put(uuid, seconds);
                    }
                });
                notifyTeammatesBatch(swapped);
            }

        }.runTaskTimer(GameRunnable.SECOND, GameRunnable.SECOND);
    }

    private void startRandomSwapTimer(@Nonnull Game game) {
        generateNextSwapTime();

        new GameRunnable(game) {
            int secondsPast = 0;

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    return;
                }
                if (secondsPast >= secondsUntilNextSwap) {
                    List<WarlordsPlayer> swapped = new ArrayList<>();
                    game.warlordsPlayers().forEach(player -> {
                        if (swapAbilities(player, false)) {
                            swapped.add(player);
                        }
                    });
                    notifyTeammatesBatch(swapped);
                    generateNextSwapTime();
                    secondsPast = 0;
                }
                secondsPast++;
            }

        }.runTaskTimer(GameRunnable.SECOND, GameRunnable.SECOND);
    }

    private void generateNextSwapTime() {
        this.secondsUntilNextSwap = new Random().nextInt(MAX_SWAP_TIME - MIN_SWAP_TIME) + MIN_SWAP_TIME;
        ChatUtils.MessageType.WARLORDS.sendMessage("Abilities changing in " + secondsUntilNextSwap + " seconds");
    }

    private void markSwapped(WarlordsPlayer player) {
        secondsSinceLastSwap.put(player.getUuid(), 0);
    }

    private boolean swapAbilities(WarlordsEntity player, boolean healBaseStats) {
        if (!(player instanceof WarlordsPlayer warlordsPlayer)) {
            return false;
        }
        List<AbstractAbility> abilities = player.getAbilities();
        List<Ability<?>> plannedSwaps = planAbilitySwaps(abilities);
        if (plannedSwaps == null) {
            return false;
        }
        List<Component> changeLines = applyAbilitySwaps(player, warlordsPlayer, abilities, plannedSwaps);
        warlordsPlayer.resetAbilityTree();
        if (!changeLines.isEmpty()) {
            warlordsPlayer.sendMessage(MESSAGE_DIVIDER);
            warlordsPlayer.sendMessage(Component.text("Your abilities have changed:", NamedTextColor.YELLOW));
            changeLines.forEach(line ->
                    warlordsPlayer.sendMessage(Component.text("  ").append(line))
            );
            warlordsPlayer.sendMessage(MESSAGE_DIVIDER);
            markSwapped(warlordsPlayer);
        }
        applyBaseStats(warlordsPlayer, healBaseStats);
        return !changeLines.isEmpty();
    }

    @Nullable
    private static List<Ability<?>> planAbilitySwaps(List<AbstractAbility> abilities) {
        List<Ability<?>> withBans = planAbilitySwaps(abilities, true);
        if (withBans != null) {
            return withBans;
        }
        return planAbilitySwaps(abilities, false);
    }

    @Nullable
    private static List<Ability<?>> planAbilitySwaps(List<AbstractAbility> abilities, boolean enforceBans) {
        for (int attempt = 0; attempt < MAX_LOADOUT_ATTEMPTS; attempt++) {
            List<Ability<?>> planned = new ArrayList<>(abilities.size());
            Set<Ability<?>> selectedSoFar = new HashSet<>();
            boolean valid = true;
            for (AbstractAbility oldAbility : abilities) {
                Class<? extends AbilityIcon> iconType = getIconType(oldAbility);
                if (iconType == null) {
                    planned.add(null);
                    continue;
                }
                List<Ability<?>> pool = ABILITY_POOLS.get(iconType);
                if (pool == null || pool.isEmpty()) {
                    planned.add(null);
                    continue;
                }
                Ability<?> currentRegistry = Ability.getAbility(oldAbility.getClass());
                Collection<Ability<?>> loadoutSoFar = enforceBans ? selectedSoFar : null;
                Ability<?> pick = pickRandomAbility(pool, currentRegistry, loadoutSoFar);
                if (pick == null) {
                    valid = false;
                    break;
                }
                planned.add(pick);
                selectedSoFar.add(pick);
            }
            if (valid && planned.stream().anyMatch(Objects::nonNull)) {
                return planned;
            }
        }
        return null;
    }

    private List<Component> applyAbilitySwaps(
            WarlordsEntity player,
            WarlordsPlayer warlordsPlayer,
            List<AbstractAbility> abilities,
            List<Ability<?>> plannedSwaps
    ) {
        List<Component> changeLines = new ArrayList<>();
        for (int i = 0; i < abilities.size(); i++) {
            Ability<?> newRegistry = plannedSwaps.get(i);
            if (newRegistry == null) {
                continue;
            }
            AbstractAbility oldAbility = abilities.get(i);
            Component oldComponent = formatAbility(oldAbility, false);
            float oldCooldown = oldAbility.getCurrentCooldown();
            boolean wasOnCooldown = !oldAbility.anyCharges();
            oldAbility.cleanup();
            AbstractAbility newAbility = newRegistry.create.get();
            newAbility.init(newAbility.getBuilder());
            newAbility.initGame(player.getGame());
            if (player.getEntity() instanceof Player p) {
                newAbility.updateDescription(p);
            }
            newAbility.updateCustomStats(warlordsPlayer);
            abilities.set(i, newAbility);
            newAbility.setCurrentCooldown(oldCooldown);
            if (wasOnCooldown) {
                newAbility.setCurrentCharges(0);
            }
            changeLines.add(Component.textOfChildren(
                    oldComponent,
                    Component.text(" > ", NamedTextColor.DARK_GRAY),
                    formatAbility(newAbility, true)
            ));
        }
        return changeLines;
    }

    private Component buildTeammateAbilityLine(WarlordsPlayer player) {
        List<Component> parts = new ArrayList<>();
        parts.add(player.getColoredName());
        parts.add(Component.text(": ", NamedTextColor.GRAY));
        List<AbstractAbility> abilities = player.getAbilities();
        for (int i = 0; i < abilities.size(); i++) {
            if (i > 0) {
                parts.add(Component.text(" | ", NamedTextColor.GRAY));
            }
            parts.add(formatAbility(abilities.get(i), true));
        }
        return Component.textOfChildren(parts.toArray(Component[]::new));
    }

    private void notifyTeammatesSingle(WarlordsPlayer player) {
        Component line = buildTeammateAbilityLine(player);
        PlayerFilter.playingGame(game).teammatesOfExcludingSelf(player).forEach(teammate -> {
            teammate.sendMessage(MESSAGE_DIVIDER);
            teammate.sendMessage(line);
            teammate.sendMessage(MESSAGE_DIVIDER);
        });
    }

    private void notifyTeammatesBatch(List<WarlordsPlayer> players) {
        if (players.isEmpty()) {
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
                                .map(this::buildTeammateAbilityLine)
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

    private static void applyBaseStats(WarlordsPlayer player, boolean heal) {
        if (heal) {
            player.setMaxHealthAndHeal(BASE_HEALTH);
        } else {
            player.getHealth().setBaseValue(BASE_HEALTH);
            if (player.getCurrentHealth() > player.getMaxHealth()) {
                player.setCurrentHealth(player.getMaxHealth());
            }
            player.updateHealth();
        }
        player.getSpeed().modifyBase(modifier -> modifier.setModifier(BASE_SPEED_MODIFIER));
        player.getKnockback().addModifier(new MotionModifierBuilder()
                .setFrom(player)
                .setName("BASE")
                .setModifier(BASE_KNOCKBACK_MODIFIER)
                .setDuration(-1)
                .build());
    }

    private static Component formatAbility(AbstractAbility ability, boolean showSpecSuffix) {
        Component nameComponent = Component.text(ability.getName(), ability.getAbilityColor());
        if (showSpecSuffix && AMBIGUOUS_ABILITY_NAMES.contains(ability.getName())) {
            Specializations spec = ABILITY_SPEC_MAP.get(ability.getClass());
            if (spec != null) {
                nameComponent = Component.textOfChildren(
                        nameComponent,
                        Component.text(" (" + spec.name + ")", NamedTextColor.GRAY)
                );
            }
        }
        return nameComponent.hoverEvent(HoverEvent.showText(
                ComponentUtils.flattenComponentWithNewLine(ability.getItemComponent())
        ));
    }

    @Nullable
    private static Ability<?> pickRandomAbility(
            List<Ability<?>> pool,
            @Nullable Ability<?> exclude,
            @Nullable Collection<Ability<?>> loadoutSoFar
    ) {
        List<Ability<?>> candidates = pool;
        if (exclude != null && pool.size() > 1) {
            candidates = pool.stream().filter(ability -> ability != exclude).toList();
        }
        if (loadoutSoFar != null) {
            candidates = AbilityCombinationBans.filterCandidates(candidates, loadoutSoFar);
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

}
