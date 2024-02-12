package com.ebicep.warlords.player.ingame.cooldowns;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.Soulbinding;
import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CooldownManager {

    private final WarlordsEntity warlordsEntity;
    private final List<AbstractCooldown<?>> abstractCooldowns = new ArrayList<>();
    private int totalCooldowns = 0;

    public CooldownManager(WarlordsEntity warlordsEntity) {
        this.warlordsEntity = warlordsEntity;
    }

    public WarlordsEntity getWarlordsEntity() {
        return warlordsEntity;
    }

    public boolean hasCooldown(AbstractCooldown<?> abstractCooldown) {
        return abstractCooldowns.contains(abstractCooldown);
    }

    public boolean hasCooldown(Class<?> cooldownClass) {
        return abstractCooldowns.stream().anyMatch(cooldown -> cooldown.getCooldownClass() != null && cooldown.getCooldownClass().equals(cooldownClass));
    }

    public boolean hasCooldownExtends(Class<?> cooldownClass) {
        return abstractCooldowns.stream()
                                .anyMatch(cooldown -> cooldown.getCooldownClass() != null && cooldownClass.isAssignableFrom(cooldown.getCooldownClass()));
    }

    public boolean hasCooldown(Object cooldownObject) {
        return abstractCooldowns.stream().anyMatch(cooldown -> cooldown.getCooldownObject() != null && cooldown.getCooldownObject() == cooldownObject);
    }

    public void reduceCooldowns() {
        //List<AbstractCooldown<?>> cooldowns = Collections.synchronizedList(abstractCooldowns);
        synchronized (abstractCooldowns) {
            for (int i = 0; i < abstractCooldowns.size(); i++) {
                AbstractCooldown<?> abstractCooldown = abstractCooldowns.get(i);
                abstractCooldown.onTick(warlordsEntity);

                if (abstractCooldown.removeCheck()) {
                    abstractCooldown.getOnRemove().accept(this);
                    abstractCooldown.getOnRemoveForce().accept(this);
                    if (abstractCooldowns.contains(abstractCooldown)) {
                        abstractCooldowns.remove(i);
                        i--;

                        updatePlayerNames(abstractCooldown);
                    }
                }
            }
        }
    }

    public void updatePlayerNames(AbstractCooldown<?> abstractCooldown) {
        if (abstractCooldown.changesPlayerName()) {
            updatePlayerNames();
        }
    }

    public void updatePlayerNames() {
        Game game = warlordsEntity.getGame();
        game.getState(PlayingState.class)
            .ifPresent(playingState -> {
                game.forEachOnlinePlayer((player, team) -> {
                    WarlordsEntity wp = Warlords.getPlayer(player);
                    if (wp == null) {
                        return;
                    }
                    playingState.updateNames(CustomScoreboard.getPlayerScoreboard(player), wp);
                });
            });
    }

    public int getTotalCooldowns() {
        return totalCooldowns;
    }

    public void subtractTicksOnRegularCooldowns(int ticks, CooldownTypes... cooldownTypes) {
        addTicksToRegularCooldowns(-ticks, cooldownTypes);
    }

    public void addTicksToRegularCooldowns(int ticks, CooldownTypes... cooldownTypes) {
        List<CooldownTypes> types = Arrays.asList(cooldownTypes);
        abstractCooldowns.stream()
                         .filter(abstractCooldown -> types.contains(abstractCooldown.getCooldownType()))
                         .filter(RegularCooldown.class::isInstance)
                         .map(RegularCooldown.class::cast)
                         .forEachOrdered(regularCooldown -> regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + ticks));
    }

    public List<AbstractCooldown<?>> getBuffCooldowns() {
        return abstractCooldowns.stream()
                                .filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.BUFF)
                                .toList();
    }

    public void removeBuffCooldowns() {
        new ArrayList<>(abstractCooldowns).forEach(cd -> {
            if (abstractCooldowns.contains(cd) && cd.getCooldownType() == CooldownTypes.BUFF) {
                removeCooldown(cd);
            }
        });
    }

    public void removeCooldown(AbstractCooldown<?> abstractCooldown) {
        removeCooldown(abstractCooldown, false);
    }

    public void removeCooldown(AbstractCooldown<?> abstractCooldown, boolean noForce) {
        if (!noForce) {
            abstractCooldown.getOnRemoveForce().accept(this);
            Listener activeListener = abstractCooldown.getActiveListener();
            if (activeListener != null) {
                ChatUtils.MessageType.WARLORDS.sendMessage("Unregistering listener " + abstractCooldown.getName() + " - " + abstractCooldown + " - " + abstractCooldown.getCooldownObject());
                HandlerList.unregisterAll(activeListener);
            }
        }
        abstractCooldowns.remove(abstractCooldown);
        updatePlayerNames(abstractCooldown);
    }

    public List<AbstractCooldown<?>> getDebuffCooldowns(boolean distinct) {
        if (distinct) {
            return getCooldownsSingular().stream()
                                         .filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.DEBUFF)
                                         .toList();
        } else {
            return abstractCooldowns.stream()
                                    .filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.DEBUFF)
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
        List<AbstractCooldown<?>> cooldowns = new ArrayList<>();
        List<Pair<Class<?>, String>> previousCooldowns = new ArrayList<>();
        for (AbstractCooldown<?> abstractCooldown : abstractCooldowns) {
            if (abstractCooldown.distinct() && previousCooldowns.stream()
                                                                .anyMatch(classStringPair -> classStringPair.getA().equals(abstractCooldown.getCooldownClass())
                                                                        && classStringPair.getB().equals(abstractCooldown.getName()))
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

    public List<AbstractCooldown<?>> getAbilityCooldowns() {
        return abstractCooldowns.stream()
                                .filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.ABILITY)
                                .toList();
    }

    public void removeAbilityCooldowns() {
        List<AbstractCooldown<?>> removed = new ArrayList<>();
        abstractCooldowns.removeIf(cd -> {
            if (cd != null && cd.getCooldownType() == CooldownTypes.ABILITY) {
                cd.getOnRemoveForce().accept(this);
                removed.add(cd);
                return true;
            }
            return false;
        });
        removed.forEach(this::updatePlayerNames);
    }

    public <T extends AbstractCooldown<T>> void limitCooldowns(Class<T> cooldownClass, Class<?> filterCooldownClass, int limit) {
        List<T> matchingCooldowns = new CooldownFilter<>(this, cooldownClass)
                .filterCooldownClass(filterCooldownClass)
                .stream()
                .toList();
        if (matchingCooldowns.size() >= limit) {
            removeCooldown(matchingCooldowns.get(0));
        }
    }

    public <T extends AbstractCooldown<T>> void limitCooldowns(Class<T> cooldownClass, String name, int limit) {
        List<T> matchingCooldowns = new CooldownFilter<>(this, cooldownClass)
                .filterCooldownName(name)
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
        if (Objects.equals(abstractCooldown.getName(), "Debuff Immunity")) {
            warlordsEntity.getSpeed().removeSlownessModifiers();
            warlordsEntity.getCooldownManager().removeDebuffCooldowns();
        }
        if (hasCooldownFromName("Debuff Immunity") && abstractCooldown.getCooldownType() == CooldownTypes.DEBUFF) {
            return;
        }
        WarlordsAddCooldownEvent event = new WarlordsAddCooldownEvent(warlordsEntity, abstractCooldown);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        this.totalCooldowns++;
        abstractCooldowns.add(abstractCooldown);
        if (abstractCooldown.changesPlayerName()) {
            updatePlayerNames();
        }
    }

    public int removeDebuffCooldowns() {
        List<AbstractCooldown<?>> toRemove = abstractCooldowns.stream()
                                                              .filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.DEBUFF)
                                                              .toList();
        toRemove.forEach(cooldown -> cooldown.getOnRemoveForce().accept(this));
        abstractCooldowns.removeAll(toRemove);
        toRemove.forEach(this::updatePlayerNames);
        return toRemove.size();
    }

    public boolean hasCooldownFromName(String name) {
        return abstractCooldowns.stream().anyMatch(cooldown -> cooldown.getName() != null && cooldown.getName().equalsIgnoreCase(name));
    }

    public boolean hasCooldownFromActionBarName(String name) {
        return abstractCooldowns.stream().anyMatch(cooldown -> cooldown.getActionBarName() != null && cooldown.getActionBarName().equalsIgnoreCase(name));
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

    public final <T> void addPersistentCooldown(
            String name,
            String actionBarName,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            Consumer<CooldownManager> onRemoveForce,
            int timeLeft,
            Predicate<T> objectCheck,
            List<TriConsumer<RegularCooldown<T>, Integer, Integer>> triConsumers
    ) {
        addCooldown(new PersistentCooldown<>(name,
                actionBarName,
                cooldownClass,
                cooldownObject,
                from,
                cooldownType,
                onRemove,
                onRemoveForce,
                timeLeft,
                objectCheck,
                triConsumers
        ));
    }

    @SafeVarargs
    public final <T> void addPermanentCooldown(
            String name,
            String actionBarName,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            boolean removeOnDeath,
            BiConsumer<PermanentCooldown<T>, Integer>... biConsumers
    ) {
        addCooldown(new PermanentCooldown<>(
                name,
                actionBarName,
                cooldownClass,
                cooldownObject,
                from,
                cooldownType,
                cooldownManager -> {
                },
                removeOnDeath,
                biConsumers
        ));
    }

    public void removeCooldownNoForce(AbstractCooldown<?> abstractCooldown) {
        abstractCooldowns.remove(abstractCooldown);
    }

    public void removeCooldownByObject(Object cooldownObject) {
        new ArrayList<>(abstractCooldowns).forEach(cd -> {
            if (abstractCooldowns.contains(cd) && Objects.equals(cd.getCooldownObject(), cooldownObject)) {
                cd.getOnRemoveForce().accept(this);
                abstractCooldowns.remove(cd);
            }
        });
    }

    public void removeCooldownByName(String cooldownName) {
        removeCooldownByName(cooldownName, false);
    }

    public void removeCooldownByName(String cooldownName, boolean noForce) {
        new ArrayList<>(abstractCooldowns).forEach(cd -> {
            if (abstractCooldowns.contains(cd) && Objects.equals(cd.getName(), cooldownName)) {
                removeCooldown(cd, noForce);
            }
        });
    }

    public void clearAllCooldowns() {
        try {
            new ArrayList<>(abstractCooldowns).forEach(cd -> {
                if (abstractCooldowns.contains(cd)) {
                    removeCooldown(cd);
                }
            });

            abstractCooldowns.clear();
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
        }
    }

    public void clearCooldowns() {
        List<AbstractCooldown<?>> cooldownsToRemove = abstractCooldowns.stream()
                                                                       .filter(AbstractCooldown::isRemoveOnDeath)
                                                                       .toList();

        cooldownsToRemove.forEach(abstractCooldown -> {
            abstractCooldown.getOnRemove().accept(this);
            abstractCooldown.getOnRemoveForce().accept(this);
        });
        abstractCooldowns.removeAll(cooldownsToRemove);
        cooldownsToRemove.forEach(this::updatePlayerNames);
    }

    public List<AbstractCooldown<?>> getCooldowns() {
        return abstractCooldowns;
    }

    public void removePreviousWounding() {
        removeCooldownByName("Wounding Strike", true);
    }

    public void removeCooldown(Class<?> cooldownClass, boolean noForce) {
        new ArrayList<>(abstractCooldowns).forEach(cd -> {
            if (cd.getCooldownClass() == null) {
                return;
            }
            if (abstractCooldowns.contains(cd) && (Objects.equals(cd.getCooldownClass(), cooldownClass) || cooldownClass.isAssignableFrom(cd.getCooldownClass()))) {
                removeCooldown(cd, noForce);
            }
        });
    }

    public boolean hasBoundPlayer(WarlordsEntity warlordsPlayer) {
        for (Soulbinding soulbinding : new CooldownFilter<>(this, PersistentCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                .toList()
        ) {
            if (soulbinding.hasBoundPlayer(warlordsPlayer)) {
                return true;
            }
        }
        return false;
    }

    public List<LinkInformation> getNumberOfBoundPlayersLink(WarlordsEntity warlordsPlayer) {
        List<LinkInformation> linkInformation = new ArrayList<>();
        for (Soulbinding soulbinding : new CooldownFilter<>(this, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                .toList()
        ) {
            if (soulbinding.hasBoundPlayerLink(warlordsPlayer)) {
                this.warlordsEntity.doOnStaticAbility(Soulbinding.class, Soulbinding::addLinkProcs);
                linkInformation.add(new LinkInformation(soulbinding.getRadius(), soulbinding.getMaxAlliesHit()));
            }
        }
        int counter = linkInformation.size();
        incrementCooldown(
                new RegularCooldown<Void>("KB Resistance",
                        "KB",
                        null,
                        null,
                        this.warlordsEntity,
                        CooldownTypes.BUFF,
                        cooldownManager -> {
                        },
                        counter * 20
                ) {
                    @Override
                    public void multiplyKB(Vector currentVector) {
                        currentVector.multiply(0.75);
                    }
                },
                (int) (counter * 1.2 * 20),
                (int) (3.6 * 20)
        );
        return linkInformation;
    }

    public record LinkInformation(float radius, int limit) {
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

    public boolean checkUndyingArmy(boolean popped) {
        return checkUndyingArmy(popped, null);
    }

    public boolean checkUndyingArmy(boolean popped, UndyingArmy exclude) {
        for (UndyingArmy undyingArmy : new CooldownFilter<>(this, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(UndyingArmy.class)
                .toList()
        ) {
            if (Objects.equals(undyingArmy, exclude)) {
                continue;
            }
            if (popped) {
                //returns true if any undying is popped
                if (undyingArmy.isArmyDead(warlordsEntity)) {
                    return true;
                }
            } else {
                //return true if theres any unpopped armies
                if (!undyingArmy.isArmyDead(warlordsEntity)) {
                    return true;
                }
            }
        }
        //if popped returns false - all undying armies are not popped (there is no popped armies)
        //if !popped return false - all undying armies are popped (there is no unpopped armies)
        return false;
    }

}


