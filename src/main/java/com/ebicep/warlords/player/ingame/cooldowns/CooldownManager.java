package com.ebicep.warlords.player.ingame.cooldowns;

import com.ebicep.warlords.abilities.Soulbinding;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.abilities.internal.WoundingCooldown;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.TriConsumer;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CooldownManager {

    private final WarlordsEntity warlordsEntity;
    private final Set<AbstractCooldown<?>> abstractCooldowns = new LinkedHashSet<>();
    private final Set<AbstractCooldown<?>> cooldownsToRemove = ConcurrentHashMap.newKeySet();
    private List<AbstractCooldown<?>> distinctCache = List.of();
    private boolean distinctDirty = true;
    private int totalCooldowns = 0;
    private boolean nameDisplayDirty = false;

    public CooldownManager(WarlordsEntity warlordsEntity) {
        this.warlordsEntity = warlordsEntity;
    }

    public int removeDebuffCooldowns() {
        List<AbstractCooldown<?>> toRemove = new ArrayList<>(abstractCooldowns)
                .stream()
                .filter(cooldown -> {
                    boolean isLowLevelDebuff = cooldown.getCooldownType() == CooldownTypes.LOW_LEVEL_DEBUFF;
                    boolean clearedHighLevelDebuff = false;
                    if (cooldown.getCooldownType() == CooldownTypes.HIGH_LEVEL_DEBUFF && cooldown instanceof RegularCooldown<?> regularCooldown) {
                        regularCooldown.subtractTime(40);
                        clearedHighLevelDebuff = !regularCooldown.hasTicksLeft();
                    }
                    return isLowLevelDebuff || clearedHighLevelDebuff;
                })
                .toList();
        toRemove.forEach(this::removeCooldown);
        return toRemove.size();
    }

    public void removeCooldown(AbstractCooldown<?> abstractCooldown) {
        removeCooldown(abstractCooldown, false);
    }

    public void reduceCooldowns() {
        List<AbstractCooldown<?>> cooldowns = new ArrayList<>(abstractCooldowns);
        for (AbstractCooldown<?> cooldown : cooldowns) {
            if (cooldownsToRemove.contains(cooldown) || !abstractCooldowns.contains(cooldown)) {
                continue;
            }
            cooldown.onTick(warlordsEntity);
            if (cooldown.removeCheck()) {
                cooldown.expire(this);
            }
        }
        if (!cooldownsToRemove.isEmpty()) {
            Set<AbstractCooldown<?>> snapshot = Set.copyOf(cooldownsToRemove);
            synchronized (abstractCooldowns) {
                if (abstractCooldowns.removeAll(snapshot)) {
                    markDistinctDirty();
                }
            }
            cooldownsToRemove.removeAll(snapshot);
        }
        if (nameDisplayDirty) {
            nameDisplayDirty = false;
            warlordsEntity.getGame().getState(PlayingState.class)
                    .ifPresent(playingState -> playingState.getUpdater().markNamesDirty(warlordsEntity));
        }
    }

    public void updatePlayerNames(AbstractCooldown<?> abstractCooldown) {
        if (abstractCooldown.changesPlayerName()) {
            markNameDisplayDirty();
        }
    }

    public void markNameDisplayDirty() {
        nameDisplayDirty = true;
    }

    public void markNameDisplayDirtyIfChanged(int beforeDisplayed, int afterDisplayed) {
        if (beforeDisplayed != afterDisplayed) {
            markNameDisplayDirty();
        }
    }

    public int removeDebuffCooldownsVind() {
        List<AbstractCooldown<?>> toRemove = new ArrayList<>(abstractCooldowns)
                .stream()
                .filter(cooldown -> {
                    boolean isLowLevelDebuff = cooldown.getCooldownType() == CooldownTypes.LOW_LEVEL_DEBUFF && !(cooldown.cooldownObject instanceof WoundingCooldown.WoundingData);
                    boolean clearedHighLevelDebuff = false;
                    if ((cooldown.getCooldownType() == CooldownTypes.HIGH_LEVEL_DEBUFF || cooldown.cooldownObject instanceof WoundingCooldown.WoundingData) && cooldown instanceof RegularCooldown<?> regularCooldown) {
                        regularCooldown.subtractTime(cooldown.cooldownObject instanceof WoundingCooldown.WoundingData ? 30 : 40);
                        clearedHighLevelDebuff = !regularCooldown.hasTicksLeft();
                    }
                    return isLowLevelDebuff || clearedHighLevelDebuff;
                })
                .toList();
        toRemove.forEach(this::removeCooldown);
        return toRemove.size();
    }

    public int removeStrongDebuffCooldowns() {
        List<AbstractCooldown<?>> toRemove = new ArrayList<>(abstractCooldowns)
                .stream()
                .filter(cooldown -> {
                    boolean isDebuff = (cooldown.getCooldownType() == CooldownTypes.LOW_LEVEL_DEBUFF || cooldown.getCooldownType() == CooldownTypes.HIGH_LEVEL_DEBUFF);
                    boolean clearedTrueDebuff = false;
                    if (cooldown.getCooldownType() == CooldownTypes.TRUE_DEBUFF && cooldown instanceof RegularCooldown<?> regularCooldown) {
                        regularCooldown.subtractTime(40);
                        clearedTrueDebuff = !regularCooldown.hasTicksLeft();
                    }
                    return isDebuff || clearedTrueDebuff;
                })
                .toList();
        toRemove.forEach(this::removeCooldown);
        return toRemove.size();
    }

    public WarlordsEntity getWarlordsEntity() {
        return warlordsEntity;
    }

    public boolean hasCooldown(AbstractCooldown<?> abstractCooldown) {
        return new ArrayList<>(abstractCooldowns).contains(abstractCooldown);
    }

    public boolean hasCooldown(Class<?> cooldownClass) {
        return new ArrayList<>(abstractCooldowns).stream().anyMatch(cooldown -> cooldown.getCooldownClass() != null && cooldown.getCooldownClass().equals(cooldownClass));
    }

    public boolean hasCooldownExtends(Class<?> cooldownClass) {
        return new ArrayList<>(abstractCooldowns).stream().anyMatch(cooldown -> cooldown.getCooldownClass() != null && cooldownClass.isAssignableFrom(cooldown.getCooldownClass()));
    }

    public boolean hasCooldown(Object cooldownObject) {
        return new ArrayList<>(abstractCooldowns).stream().anyMatch(cooldown -> cooldown.getCooldownObject() != null && cooldown.getCooldownObject() == cooldownObject);
    }

    public void tick() {
        reduceCooldowns();
    }

    public boolean markedForRemoval(AbstractCooldown<?> abstractCooldown) {
        return cooldownsToRemove.contains(abstractCooldown);
    }

    public void markForRemoval(AbstractCooldown<?> abstractCooldown) {
        cooldownsToRemove.add(abstractCooldown);
        markDistinctDirty();
    }

    public void subtractTicksOnRegularCooldowns(int ticks, CooldownTypes... cooldownTypes) {
        List<CooldownTypes> types = Arrays.asList(cooldownTypes);
        new ArrayList<>(abstractCooldowns).stream()
                                          .filter(abstractCooldown -> types.contains(abstractCooldown.getCooldownType()))
                                          .filter(regularCooldown -> !regularCooldown.getFlags().contains(CooldownFlag.CANNOT_BE_REDUCED))
                                          .filter(RegularCooldown.class::isInstance)
                                          .map(RegularCooldown.class::cast)
                                          .forEachOrdered(regularCooldown -> regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() - ticks));
    }

    public int getTotalCooldowns() {
        return totalCooldowns;
    }

    public void removeCooldown(Predicate<AbstractCooldown<?>> predicate) {
        removeCooldown(predicate, false);
    }

    public void addTicksToRegularCooldowns(int ticks, CooldownTypes... cooldownTypes) {
        List<CooldownTypes> types = Arrays.asList(cooldownTypes);
        abstractCooldowns.stream()
                         .filter(abstractCooldown -> types.contains(abstractCooldown.getCooldownType()))
                         .filter(RegularCooldown.class::isInstance)
                         .map(RegularCooldown.class::cast)
                         .forEachOrdered(regularCooldown -> regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + ticks));
    }

    public void removeBuffCooldowns() {
        removeCooldown(cd -> cd.getCooldownType() == CooldownTypes.BUFF);
    }

    public void removeCooldown(Predicate<AbstractCooldown<?>> predicate, boolean noForce) {
        new ArrayList<>(abstractCooldowns).forEach(cd -> {
            if (predicate.test(cd)) {
                removeCooldown(cd, noForce);
            }
        });
    }

    private void removeCooldown(AbstractCooldown<?> abstractCooldown, boolean noForce) {
        if (!noForce) {
            abstractCooldown.getOnRemoveForce().accept(this);
        }
        cooldownsToRemove.add(abstractCooldown);
        markDistinctDirty();
        updatePlayerNames(abstractCooldown);
    }

    public List<AbstractCooldown<?>> getDebuffCooldowns(boolean distinct) {
        if (distinct) {
            return getCooldownsSingular().stream()
                                         .filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.LOW_LEVEL_DEBUFF)
                                         .toList();
        } else {
            return new ArrayList<>(abstractCooldowns).stream()
                                                     .filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.LOW_LEVEL_DEBUFF)
                                                     .toList();
        }
    }

    public List<AbstractCooldown<?>> getCooldownsSingular() {
        List<AbstractCooldown<?>> cooldowns = new ArrayList<>();
        List<Pair<Class<?>, String>> previousCooldowns = new ArrayList<>();
        for (AbstractCooldown<?> abstractCooldown : abstractCooldowns) {
            if (previousCooldowns.stream().anyMatch(classStringPair -> classStringPair.getA().equals(abstractCooldown.getCooldownClass()) &&
                    classStringPair.getB().equals(abstractCooldown.getName()))
            ) {
                continue;
            }
            cooldowns.add(abstractCooldown);
            if (abstractCooldown.distinct()) {
                previousCooldowns.add(new Pair<>(abstractCooldown.getCooldownClass(), abstractCooldown.getName()));
            }
        }
        return cooldowns;
    }

    public List<AbstractCooldown<?>> getCooldownsDistinct() {
        if (!distinctDirty) {
            return distinctCache;
        }
        List<AbstractCooldown<?>> cooldowns = new ArrayList<>();
        Set<Pair<Class<?>, String>> seenDistinct = new HashSet<>();
        for (AbstractCooldown<?> abstractCooldown : abstractCooldowns) {
            if (abstractCooldown.distinct()) {
                Pair<Class<?>, String> key = new Pair<>(abstractCooldown.getCooldownClass(), abstractCooldown.getName());
                if (!seenDistinct.add(key)) {
                    continue;
                }
            }
            cooldowns.add(abstractCooldown);
        }
        distinctCache = List.copyOf(cooldowns);
        distinctDirty = false;
        return distinctCache;
    }

    private void markDistinctDirty() {
        distinctDirty = true;
    }

    public void removeAbilityCooldowns() {
        removeCooldown(cd -> cd.getCooldownType() == CooldownTypes.ABILITY);
    }

    public <T extends AbstractCooldown<T>> void limitCooldowns(Class<T> cooldownClass, Class<?> filterCooldownClass, int limit) {
        List<T> matchingCooldowns = new CooldownFilter<>(this, cooldownClass)
                .filterCooldownClass(filterCooldownClass)
                .filter(t -> !cooldownsToRemove.contains(t))
                .stream()
                .toList();
        if (matchingCooldowns.size() >= limit) {
            for (int i = 0; i < matchingCooldowns.size() - limit + 1; i++) {
                removeCooldown(matchingCooldowns.get(0));
            }
        }
    }

    public <T extends AbstractCooldown<T>> void limitCooldowns(Class<T> cooldownClass, String name, int limit) {
        List<T> matchingCooldowns = new CooldownFilter<>(this, cooldownClass)
                .filterCooldownName(name)
                .filter(t -> !cooldownsToRemove.contains(t))
                .stream()
                .toList();
        if (matchingCooldowns.size() >= limit) {
            removeCooldown(matchingCooldowns.get(0));
        }
    }

    public final <T> void addRegularCooldown(
            String name,
            String actionBarName,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            int timeLeft,
            List<TriConsumer<RegularCooldown<T>, Integer, Integer>> triConsumers
    ) {
        addRegularCooldown(name, actionBarName, cooldownClass, cooldownObject, from, cooldownType, onRemove, true, timeLeft, triConsumers);
    }

    /**
     * @param name           is the name of the cooldown.
     * @param actionBarName  what name should be displayed in the action bar.
     * @param cooldownClass  java class of the ability.
     * @param cooldownObject object of the ability or cooldown.
     * @param from           what player did they get the cooldown from.
     * @param cooldownType   what type of cooldown is it, eg. DEBUFF, BUFF, ABILITY.
     * @param onRemove       runs when the cooldown is over
     * @param removeOnDeath  should the cooldown be removed on death.
     * @param timeLeft       how long should the cooldown last.
     * @param triConsumers
     */
    public final <T> void addRegularCooldown(
            String name,
            String actionBarName,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            boolean removeOnDeath,
            int timeLeft,
            List<TriConsumer<RegularCooldown<T>, Integer, Integer>> triConsumers
    ) {
        addRegularCooldown(
                name,
                actionBarName,
                cooldownClass,
                cooldownObject,
                from,
                cooldownType,
                onRemove,
                onForceRemove -> {
                },
                removeOnDeath,
                timeLeft,
                triConsumers
        );
    }

    public final <T> void addRegularCooldown(
            String name,
            String actionBarName,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            Consumer<CooldownManager> onRemoveForce,
            boolean removeOnDeath,
            int timeLeft,
            List<TriConsumer<RegularCooldown<T>, Integer, Integer>> triConsumers
    ) {
        addCooldown(new RegularCooldown<>(
                name,
                actionBarName,
                cooldownClass,
                cooldownObject,
                from,
                cooldownType,
                onRemove,
                onRemoveForce,
                removeOnDeath,
                timeLeft,
                triConsumers
        ));
    }

    public void addCooldown(AbstractCooldown<?> abstractCooldown) {
        if (abstractCooldown == null) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(new Throwable("null cooldown"));
            return;
        }
        WarlordsAddCooldownEvent event = new WarlordsAddCooldownEvent(warlordsEntity, abstractCooldown);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            abstractCooldown.getOnRemoveForce().accept(this);
            return;
        }
        this.totalCooldowns++;
        if (abstractCooldowns.add(abstractCooldown)) {
            markDistinctDirty();
        }
        updatePlayerNames(abstractCooldown);
        if (abstractCooldown.getCooldownObject() instanceof Shield shield
                && abstractCooldown.changesPlayerName()) {
            shield.setOnHealthChanged(this::markNameDisplayDirty);
        }
    }

    public boolean hasCooldownFromName(String name) {
        return new ArrayList<>(abstractCooldowns).stream().anyMatch(cooldown -> cooldown.getName() != null && cooldown.getName().equalsIgnoreCase(name));
    }

    public boolean hasCooldownFromActionBarName(String name) {
        return new ArrayList<>(abstractCooldowns).stream().anyMatch(cooldown -> cooldown.getActionBarName() != null && cooldown.getActionBarName().equalsIgnoreCase(name));
    }

    public final <T> void addRegularCooldown(
            String name,
            String actionBarName,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            Consumer<CooldownManager> onRemoveForce,
            int timeLeft,
            List<TriConsumer<RegularCooldown<T>, Integer, Integer>> triConsumers
    ) {
        addRegularCooldown(name, actionBarName, cooldownClass, cooldownObject, from, cooldownType, onRemove, onRemoveForce, true, timeLeft, triConsumers);
    }

    public final <T> void addRegularCooldown(
            String name,
            String actionBarName,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            int timeLeft
    ) {
        addRegularCooldown(name, actionBarName, cooldownClass, cooldownObject, from, cooldownType, onRemove, true, timeLeft, new ArrayList<>());
    }

    public final <T> void addRegularCooldown(
            String name,
            String actionBarName,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            Consumer<CooldownManager> onRemoveForce,
            int timeLeft
    ) {
        addRegularCooldown(name, actionBarName, cooldownClass, cooldownObject, from, cooldownType, onRemove, onRemoveForce, true, timeLeft, new ArrayList<>());
    }

    public void removeCooldownNoForce(AbstractCooldown<?> abstractCooldown) {
        removeCooldown(abstractCooldown, true);
    }

    public void removeCooldownByObject(Object cooldownObject) {
        removeCooldown(cd -> Objects.equals(cd.getCooldownObject(), cooldownObject));
    }

    public void removeCooldownByName(String cooldownName) {
        removeCooldownByName(cooldownName, false);
    }

    public void removeCooldownByName(String cooldownName, boolean noForce) {
        removeCooldown(cd -> Objects.equals(cd.getName(), cooldownName), noForce);
    }

    public void clearAllCooldowns() {
        new ArrayList<>(abstractCooldowns).forEach(cooldown -> removeCooldown(cooldown, false));
    }

    public void expireCooldowns() {
        new ArrayList<>(abstractCooldowns).forEach(cooldown -> {
            if (cooldown.isRemoveOnDeath()) {
                cooldown.expire(this);
            }
        });
    }

    public Collection<AbstractCooldown<?>> getCooldowns() {
        return abstractCooldowns;
    }

    public void removePreviousWounding() {
        removeCooldown(WoundingCooldown.WoundingData.class, true);
    }

    public void removeCooldown(Class<?> cooldownClass, boolean noForce) {
        removeCooldown(cd ->
                        cd.getCooldownClass() != null &&
                                (Objects.equals(cd.getCooldownClass(), cooldownClass) || cooldownClass.isAssignableFrom(cd.getCooldownClass())),
                noForce
        );
    }

    public boolean hasBoundPlayer(WarlordsEntity warlordsPlayer) {
        for (Soulbinding.SoulbindingData data : new CooldownFilter<>(this, PersistentCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.SoulbindingData.class)
                .toList()
        ) {
            if (data.hasBoundPlayer(warlordsPlayer)) {
                return true;
            }
        }
        return false;
    }

    public List<Soulbinding.SoulbindingData> getNumberOfBoundPlayersLink(WarlordsEntity warlordsPlayer) {
        List<Soulbinding.SoulbindingData> soulbindings = new ArrayList<>();
        for (Soulbinding.SoulbindingData data : new CooldownFilter<>(this, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.SoulbindingData.class)
                .toList()
        ) {
            if (data.hasBoundPlayerLink(warlordsPlayer)) {
                soulbindings.add(data);
            }
        }
        int counter = soulbindings.size();
        for (AbstractAbility ability : this.warlordsEntity.getAbilities()) {
            if (ability instanceof Soulbinding soulbinding) {
                this.warlordsEntity.addKnockbackModifier(this.warlordsEntity, "Spirit Link", -soulbinding.getKbRes(), (int) (counter * 1.2 * 20));
                break;
            }
        }
        return soulbindings;
    }

    @SuppressWarnings("unchecked")
    public <T> void incrementCooldown(RegularCooldown<T> regularCooldown, int ticksToAdd, int tickCap) {
        Optional<RegularCooldown> optionalRegularCooldown = new CooldownFilter<>(this, RegularCooldown.class)
                .filterCooldownClass(regularCooldown.cooldownClass)
                .filterName(regularCooldown.name)
                .findAny();
        if (optionalRegularCooldown.isPresent()) {
            RegularCooldown<T> cd = (RegularCooldown<T>) optionalRegularCooldown.get();
            if (cd.getTicksLeft() + ticksToAdd >= tickCap) {
                cd.setTicksLeft(tickCap);
            } else {
                cd.subtractTime(-ticksToAdd);
            }
        } else {
            addCooldown(regularCooldown);
        }
    }

}


