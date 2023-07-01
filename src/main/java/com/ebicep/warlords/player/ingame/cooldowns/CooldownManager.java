package com.ebicep.warlords.player.ingame.cooldowns;

import com.ebicep.warlords.abilities.Soulbinding;
import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.abilities.WoundingStrikeBerserker;
import com.ebicep.warlords.abilities.WoundingStrikeDefender;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
                    }
                }
            }
        }

    }

    public List<AbstractCooldown<?>> getCooldownsDistinct() {
        List<AbstractCooldown<?>> cooldowns = new ArrayList<>();
        List<Pair<Class<?>, String>> previousCooldowns = new ArrayList<>();
        for (AbstractCooldown<?> abstractCooldown : abstractCooldowns) {
            if (abstractCooldown.distinct() && previousCooldowns.stream()
                                                                .anyMatch(classStringPair -> classStringPair.getA()
                                                                                                            .equals(abstractCooldown.getCooldownClass()) && classStringPair.getB()
                                                                                                                                                                           .equals(abstractCooldown.getName()))) {
                continue;
            }
            cooldowns.add(abstractCooldown);
            if (abstractCooldown.distinct()) {
                previousCooldowns.add(new Pair<>(abstractCooldown.getCooldownClass(), abstractCooldown.getName()));
            }
        }
        return cooldowns;
    }

    public int getTotalCooldowns() {
        return totalCooldowns;
    }

    public void subtractTicksOnRegularCooldowns(CooldownTypes cooldownTypes, int ticks) {
        addTicksToRegularCooldowns(cooldownTypes, -ticks);
    }

    public void addTicksToRegularCooldowns(CooldownTypes cooldownTypes, int ticks) {
        abstractCooldowns.stream()
                         .filter(abstractCooldown -> abstractCooldown.getCooldownType() == cooldownTypes)
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
                cd.getOnRemoveForce().accept(this);
                abstractCooldowns.remove(cd);
            }
        });
    }

    public List<AbstractCooldown<?>> getDebuffCooldowns() {
        return abstractCooldowns.stream()
                                .filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.DEBUFF)
                                .toList();
    }

    public int removeDebuffCooldowns() {
        List<AbstractCooldown<?>> toRemove = abstractCooldowns.stream()
                                                              .filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.DEBUFF)
                                                              .toList();
        toRemove.forEach(cooldown -> cooldown.getOnRemoveForce().accept(this));
        abstractCooldowns.removeAll(toRemove);
        return toRemove.size();
    }

    public List<AbstractCooldown<?>> getAbilityCooldowns() {
        return abstractCooldowns.stream()
                                .filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.ABILITY)
                                .toList();
    }

    public void removeAbilityCooldowns() {
        abstractCooldowns.removeIf(cd -> {
            if (cd != null && cd.getCooldownType() == CooldownTypes.ABILITY) {
                cd.getOnRemoveForce().accept(this);
                return true;
            }
            return false;
        });
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
        if (hasCooldownFromName("Vindicate Debuff Immunity") && abstractCooldown.getCooldownType() == CooldownTypes.DEBUFF) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new WarlordsAddCooldownEvent(warlordsEntity, abstractCooldown));
        this.totalCooldowns++;
        abstractCooldowns.add(abstractCooldown);
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

    public void removeCooldown(AbstractCooldown<?> abstractCooldown) {
        abstractCooldown.getOnRemoveForce().accept(this);
        abstractCooldowns.remove(abstractCooldown);
    }

    public void removeCooldownNoForce(AbstractCooldown<?> abstractCooldown) {
        abstractCooldowns.remove(abstractCooldown);
    }

    public void removeCooldown(Class<?> cooldownClass, boolean noForce) {
        new ArrayList<>(abstractCooldowns).forEach(cd -> {
            if (abstractCooldowns.contains(cd) && (Objects.equals(cd.getCooldownClass(), cooldownClass) || cooldownClass.isAssignableFrom(cd.getCooldownClass()))) {
                if (!noForce) {
                    cd.getOnRemoveForce().accept(this);
                }
                abstractCooldowns.remove(cd);
            }
        });
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
        new ArrayList<>(abstractCooldowns).forEach(cd -> {
            if (abstractCooldowns.contains(cd) && Objects.equals(cd.getName(), cooldownName)) {
                cd.getOnRemoveForce().accept(this);
                abstractCooldowns.remove(cd);
            }
        });
    }

    public void clearAllCooldowns() {
        try {
            new ArrayList<>(abstractCooldowns).forEach(cd -> {
                if (abstractCooldowns.contains(cd)) {
                    cd.getOnRemoveForce().accept(this);
                    abstractCooldowns.remove(cd);
                }
            });

            abstractCooldowns.clear();
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e.getMessage());
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
    }

    public List<AbstractCooldown<?>> getCooldowns() {
        return abstractCooldowns;
    }

    public void removePreviousWounding() {
        removeCooldown(WoundingStrikeBerserker.class, true);
        removeCooldown(WoundingStrikeDefender.class, true);
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

    public int getNumberOfBoundPlayersLink(WarlordsEntity warlordsPlayer) {
        int counter = 0;
        for (Soulbinding soulbinding : new CooldownFilter<>(this, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                .toList()
        ) {
            if (soulbinding.hasBoundPlayerLink(warlordsPlayer)) {
                this.warlordsEntity.doOnStaticAbility(Soulbinding.class, Soulbinding::addLinkProcs);
                counter++;
            }
        }
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
        return counter;
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


