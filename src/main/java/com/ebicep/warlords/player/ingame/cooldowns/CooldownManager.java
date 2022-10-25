package com.ebicep.warlords.player.ingame.cooldowns;

import com.ebicep.warlords.abilties.Intervene;
import com.ebicep.warlords.abilties.Soulbinding;
import com.ebicep.warlords.abilties.UndyingArmy;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.TriConsumer;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CooldownManager {

    private final WarlordsEntity warlordsEntity;
    private final List<AbstractCooldown<?>> abstractCooldowns = new ArrayList<>();
    private int totalCooldowns = 0;

    public CooldownManager(WarlordsEntity warlordsEntity) {
        this.warlordsEntity = warlordsEntity;
    }

    public boolean hasCooldown(AbstractCooldown<?> abstractCooldown) {
        return abstractCooldowns.contains(abstractCooldown);
    }

    public boolean hasCooldown(Class<?> cooldownClass) {
        return abstractCooldowns.stream().anyMatch(cooldown -> cooldown.getCooldownClass() != null && cooldown.getCooldownClass().equals(cooldownClass));
    }

    public boolean hasCooldown(Object cooldownObject) {
        return abstractCooldowns.stream().anyMatch(cooldown -> cooldown.getCooldownObject() != null && cooldown.getCooldownObject() == cooldownObject);
    }

    public void reduceCooldowns() {
        Iterator<AbstractCooldown<?>> iterator = abstractCooldowns.iterator();
        while (iterator.hasNext()) {
            AbstractCooldown<?> abstractCooldown = iterator.next();
            abstractCooldown.onTick(warlordsEntity);

            if (abstractCooldown.removeCheck()) {
                abstractCooldown.getOnRemove().accept(this);
                iterator.remove();
            }
        }
    }

    public List<AbstractCooldown<?>> getCooldownsDistinct() {
        List<AbstractCooldown<?>> cooldowns = new ArrayList<>();
        List<Pair<Class<?>, String>> previousCooldowns = new ArrayList<>();
        for (AbstractCooldown<?> abstractCooldown : abstractCooldowns) {
            if (abstractCooldown.distinct() && previousCooldowns.stream()
                    .anyMatch(classStringPair -> classStringPair.getA().equals(abstractCooldown.getCooldownClass()) && classStringPair.getB()
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
        abstractCooldowns.stream().filter(abstractCooldown -> abstractCooldown.getCooldownType() == cooldownTypes)
                .filter(RegularCooldown.class::isInstance)
                .map(RegularCooldown.class::cast)
                .forEachOrdered(regularCooldown -> {
                    regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + ticks);
                    if (Objects.equals(regularCooldown.getCooldownClass(), Intervene.class)) {
                        Intervene intervene = (Intervene) regularCooldown.getCooldownObject();
                        //player is defender that vened, then reduce vened target cooldown
                        if (regularCooldown.getFrom() == warlordsEntity) {
                            new CooldownFilter<>(intervene.getTarget(), RegularCooldown.class)
                                    .filterCooldownObject(intervene)
                                    .findFirst()
                                    .ifPresent(cooldown -> cooldown.setTicksLeft(cooldown.getTicksLeft() + ticks));
                        }
                        //player with vene from defender, then reduce defender vene cooldown
                        else {
                            new CooldownFilter<>(intervene.getCaster(), RegularCooldown.class)
                                    .filterCooldownObject(intervene)
                                    .findFirst()
                                    .ifPresent(cooldown -> cooldown.setTicksLeft(cooldown.getTicksLeft() + ticks));
                        }
                    }
                });
    }

    public List<AbstractCooldown<?>> getBuffCooldowns() {
        return abstractCooldowns.stream().filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.BUFF).collect(Collectors.toList());
    }

    public void removeBuffCooldowns() {
        abstractCooldowns.removeIf(cd -> cd.getCooldownType() == CooldownTypes.BUFF);
    }

    public List<AbstractCooldown<?>> getDebuffCooldowns() {
        return abstractCooldowns.stream().filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.DEBUFF).collect(Collectors.toList());
    }

    public int removeDebuffCooldowns() {
        List<AbstractCooldown<?>> toRemove = abstractCooldowns.stream()
                .filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.DEBUFF)
                .collect(Collectors.toList());
        abstractCooldowns.removeAll(toRemove);
        return toRemove.size();
    }

    public List<AbstractCooldown<?>> getAbilityCooldowns() {
        return abstractCooldowns.stream().filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.ABILITY).collect(Collectors.toList());
    }

    public void removeAbilityCooldowns() {
        abstractCooldowns.removeIf(cd -> cd.getCooldownType() == CooldownTypes.ABILITY);
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
        addCooldown(new RegularCooldown<>(
                name,
                actionBarName,
                cooldownClass,
                cooldownObject,
                from,
                cooldownType,
                onRemove,
                removeOnDeath,
                timeLeft,
                triConsumers
        ));
    }

    public void addCooldown(AbstractCooldown<?> abstractCooldown) {
        if (hasCooldownFromName("Vindicate Debuff Immunity") && abstractCooldown.getCooldownType() == CooldownTypes.DEBUFF) {
            return;
        }
        this.totalCooldowns++;
        abstractCooldowns.add(abstractCooldown);
    }

    public boolean hasCooldownFromName(String name) {
        return abstractCooldowns.stream().anyMatch(cooldown -> cooldown.getName() != null && cooldown.getName().equalsIgnoreCase(name));
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

    public final <T> void addPersistentCooldown(
            String name,
            String actionBarName,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
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
        abstractCooldowns.remove(abstractCooldown);
    }

    public void removeCooldown(Class<?> cooldownClass) {
        abstractCooldowns.removeIf(cd -> cd.getCooldownClass() == cooldownClass);
    }

    public void removeCooldownByObject(Object cooldownObject, boolean callOnRemove) {
        abstractCooldowns.removeIf(cd -> {
            if (Objects.equals(cd.getCooldownObject(), cooldownObject)) {
                if (callOnRemove) {
                    cd.getOnRemove().accept(this);
                }
                return true;
            }
            return false;
        });
    }

    public void removeCooldownByName(String cooldownName) {
        abstractCooldowns.removeIf(cd -> cd.getName().equals(cooldownName));
    }

    public void clearAllCooldowns() {
        abstractCooldowns.clear();
    }

    public void clearCooldowns() {
        List<AbstractCooldown<?>> cooldownsToRemove = abstractCooldowns.stream().filter(AbstractCooldown::isRemoveOnDeath).collect(Collectors.toList());

        cooldownsToRemove.forEach(abstractCooldown -> abstractCooldown.getOnRemove().accept(this));
        abstractCooldowns.removeAll(cooldownsToRemove);
    }

    public List<AbstractCooldown<?>> getCooldowns() {
        return abstractCooldowns;
    }

    public boolean hasBoundPlayer(WarlordsEntity warlordsPlayer) {
        for (Soulbinding soulbinding : new CooldownFilter<>(this, PersistentCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                .collect(Collectors.toList())
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
                .collect(Collectors.toList())
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
        for (UndyingArmy undyingArmy : new CooldownFilter<>(this, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(UndyingArmy.class)
                .collect(Collectors.toList())
        ) {
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


