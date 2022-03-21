package com.ebicep.warlords.player.cooldowns;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CooldownManager {

    private final WarlordsPlayer warlordsPlayer;
    private final List<AbstractCooldown<?>> abstractCooldowns;
    private int totalCooldowns = 0;

    public CooldownManager(WarlordsPlayer warlordsPlayer) {
        this.warlordsPlayer = warlordsPlayer;
        abstractCooldowns = new ArrayList<>();
    }

    public boolean hasCooldownFromName(String name) {
        return abstractCooldowns.stream().anyMatch(cooldown -> cooldown.getName().equalsIgnoreCase(name));
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
        for (int i = 0; i < abstractCooldowns.size(); i++) {
            AbstractCooldown<?> abstractCooldown = abstractCooldowns.get(i);
            abstractCooldown.onTick();

            if (abstractCooldown.removeCheck()) {
                abstractCooldown.getOnRemove().accept(this);
                abstractCooldowns.remove(i);
                i--;
            }
        }
    }

    public List<AbstractCooldown<?>> getCooldowns() {
        return abstractCooldowns;
    }

    public List<AbstractCooldown<?>> getCooldownsDistinct() {
        List<AbstractCooldown<?>> cooldowns = new ArrayList<>();
        List<Pair<Class<?>, String>> previousCooldowns = new ArrayList<>();
        for (AbstractCooldown<?> abstractCooldown : abstractCooldowns) {
            if (abstractCooldown.distinct() && previousCooldowns.stream().anyMatch(classStringPair -> classStringPair.getA().equals(abstractCooldown.getCooldownClass()) && classStringPair.getB().equals(abstractCooldown.getName()))) {
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

    public void addTicksToRegularCooldowns(CooldownTypes cooldownTypes, int ticks) {
        abstractCooldowns.stream().filter(abstractCooldown -> abstractCooldown.getCooldownType() == cooldownTypes)
                .filter(RegularCooldown.class::isInstance)
                .map(RegularCooldown.class::cast)
                .forEachOrdered(regularCooldown -> regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + ticks));
    }

    public void subtractTicksOnRegularCooldowns(CooldownTypes cooldownTypes, int ticks) {
        abstractCooldowns.stream().filter(abstractCooldown -> abstractCooldown.getCooldownType() == cooldownTypes)
                .filter(RegularCooldown.class::isInstance)
                .map(RegularCooldown.class::cast)
                .forEachOrdered(regularCooldown -> regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() - ticks));
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

    public void removeDebuffCooldowns() {
        abstractCooldowns.removeIf(cd -> cd.getCooldownType() == CooldownTypes.DEBUFF);
    }

    public List<AbstractCooldown<?>> getAbilityCooldowns() {
        return abstractCooldowns.stream().filter(cooldown -> cooldown.getCooldownType() == CooldownTypes.ABILITY).collect(Collectors.toList());
    }

    public void removeAbilityCooldowns() {
        abstractCooldowns.removeIf(cd -> cd.getCooldownType() == CooldownTypes.ABILITY);
    }

    /**
     * @param name           is the name of the cooldown.
     * @param actionBarName  what name should be displayed in the action bar.
     * @param cooldownClass  java class of the ability.
     * @param cooldownObject object of the ability or cooldown.
     * @param from           what player did they get the cooldown from.
     * @param cooldownType   what type of cooldown is it, eg. DEBUFF, BUFF, ABILITY.
     * @param onRemove       runs when the cooldown is over
     * @param timeLeft       how long should the cooldown last.
     */
    public <T> void addRegularCooldown(String name, String actionBarName, Class<T> cooldownClass, T cooldownObject, WarlordsPlayer from, CooldownTypes cooldownType, Consumer<CooldownManager> onRemove, int timeLeft) {
        addCooldown(new RegularCooldown<>(name, actionBarName, cooldownClass, cooldownObject, from, cooldownType, onRemove, timeLeft));
    }

    public <T> void addPersistentCooldown(String name, String actionBarName, Class<T> cooldownClass, T cooldownObject, WarlordsPlayer from, CooldownTypes cooldownType, Consumer<CooldownManager> onRemove, int timeLeft, Predicate<T> objectCheck) {
        addCooldown(new PersistentCooldown<>(name, actionBarName, cooldownClass, cooldownObject, from, cooldownType, onRemove, timeLeft, objectCheck));
    }

    public void addCooldown(AbstractCooldown<?> abstractCooldown) {
        if (hasCooldownFromName("Vindicate Debuff Immunity") && abstractCooldown.getCooldownType() == CooldownTypes.DEBUFF) {
            return;
        }
        this.totalCooldowns++;
        abstractCooldowns.add(abstractCooldown);
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

    public void removeCooldown(Class<?> cooldownClass) {
        abstractCooldowns.removeIf(cd -> cd.getCooldownClass() == cooldownClass);
    }

    public void removeCooldown(Object cooldownObject) {
        abstractCooldowns.removeIf(cd -> cd.getCooldownObject() == cooldownObject);
    }

    public void removeCooldownByName(String cooldownName) {
        abstractCooldowns.removeIf(cd -> cd.getName().equals(cooldownName));
    }

    public void clearAllCooldowns() {
        abstractCooldowns.clear();
    }

    public void clearCooldowns() {
        abstractCooldowns.removeIf(cd ->
                cd.getCooldownClass() != OrbsOfLife.class &&
                        cd.getCooldownClass() != HammerOfLight.class
        );
        PlayerFilter.playingGame(warlordsPlayer.getGame()).teammatesOf(warlordsPlayer).forEach(wp -> {
            wp.getCooldownManager().getCooldowns().removeIf(cd -> cd.getFrom() == warlordsPlayer && cd.getCooldownClass() == Intervene.class);
        });
    }

    public boolean hasBoundPlayer(WarlordsPlayer warlordsPlayer) {
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

    public int getNumberOfBoundPlayersLink(WarlordsPlayer warlordsPlayer) {
        int counter = 0;
        for (Soulbinding soulbinding : new CooldownFilter<>(this, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                .collect(Collectors.toList())
        ) {
            if (soulbinding.hasBoundPlayerLink(warlordsPlayer)) {
                counter++;
            }
        }
        incrementCooldown(
                new RegularCooldown<Void>("KB Resistance", "KB", null, null, this.warlordsPlayer, CooldownTypes.BUFF, cooldownManager -> {
                }, counter * 20),
                (int) (counter * 1.2 * 20),
                (int) (3.6 * 20)
        );
        return counter;
    }

    public boolean checkUndyingArmy(boolean popped) {
        for (UndyingArmy undyingArmy : new CooldownFilter<>(this, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(UndyingArmy.class)
                .collect(Collectors.toList())
        ) {
            if (popped) {
                //returns true if any undying is popped
                if (undyingArmy.isArmyDead(warlordsPlayer)) {
                    return true;
                }
            } else {
                //return true if theres any unpopped armies
                if (!undyingArmy.isArmyDead(warlordsPlayer)) {
                    return true;
                }
            }
        }
        //if popped returns false - all undying armies are not popped (there is no popped armies)
        //if !popped return false - all undying armies are popped (there is no unpopped armies)
        return false;
    }

}


