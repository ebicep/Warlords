package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import com.ebicep.warlords.util.bukkit.ComponentUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
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
    private static final float BASE_HEALTH = 5500f;
    private static final float BASE_SPEED_MODIFIER = 13f;
    private static final float BASE_KNOCKBACK_MODIFIER = 0f;
    private static final Component MESSAGE_DIVIDER =
            Component.text("---------------------------------------", NamedTextColor.DARK_GRAY);

    private static final Map<Class<? extends AbilityIcon>, List<Ability<?>>> ABILITY_POOLS = buildAbilityPools();

    private final Mode mode;
    private Game game;
    private int secondsUntilNextSwap = 0;

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
                swapAbilities(event.getWarlordsEntity());
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
        players.forEach(player -> swapAbilities(player, true));
    }

    @Override
    public void start(@Nonnull Game game) {
        if (mode != Mode.RANDOM) {
            return;
        }
        generateNextSwapTime();

        new GameRunnable(game) {
            int secondsPast = 0;

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    return;
                }
                if (secondsPast >= secondsUntilNextSwap) {
                    game.warlordsPlayers().forEach(AbilityChangeOption.this::swapAbilities);
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

    private void swapAbilities(WarlordsEntity player) {
        swapAbilities(player, false);
    }

    private void swapAbilities(WarlordsEntity player, boolean healBaseStats) {
        if (!(player instanceof WarlordsPlayer warlordsPlayer)) {
            return;
        }
        List<AbstractAbility> abilities = player.getAbilities();
        List<Component> changeLines = new ArrayList<>();
        for (int i = 0; i < abilities.size(); i++) {
            AbstractAbility oldAbility = abilities.get(i);
            Class<? extends AbilityIcon> iconType = getIconType(oldAbility);
            if (iconType == null) {
                continue;
            }
            List<Ability<?>> pool = ABILITY_POOLS.get(iconType);
            if (pool == null || pool.isEmpty()) {
                continue;
            }
            Ability<?> currentRegistry = Ability.getAbility(oldAbility.getClass());
            Ability<?> newRegistry = pickRandomAbility(pool, currentRegistry);
            if (newRegistry == null) {
                continue;
            }
            Component oldComponent = formatAbility(oldAbility);
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
                    formatAbility(newAbility)
            ));
        }
        warlordsPlayer.resetAbilityTree();
        if (!changeLines.isEmpty()) {
            warlordsPlayer.sendMessage(MESSAGE_DIVIDER);
            warlordsPlayer.sendMessage(Component.text("Your abilities have changed:", NamedTextColor.YELLOW));
            changeLines.forEach(line ->
                    warlordsPlayer.sendMessage(Component.text("  ").append(line))
            );
            warlordsPlayer.sendMessage(MESSAGE_DIVIDER);
        }
        applyBaseStats(warlordsPlayer, healBaseStats);
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

    private static Component formatAbility(AbstractAbility ability) {
        return Component.text(ability.getName(), ability.getAbilityColor())
                        .hoverEvent(HoverEvent.showText(
                                ComponentUtils.flattenComponentWithNewLine(ability.getItemComponent())
                        ));
    }

    @Nullable
    private static Ability<?> pickRandomAbility(List<Ability<?>> pool, @Nullable Ability<?> exclude) {
        List<Ability<?>> candidates = pool;
        if (exclude != null && pool.size() > 1) {
            candidates = pool.stream().filter(ability -> ability != exclude).toList();
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }

}
