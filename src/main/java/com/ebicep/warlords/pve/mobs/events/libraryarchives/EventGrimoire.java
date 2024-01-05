package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class EventGrimoire extends AbstractMob implements BossMinionMob {

    private final Map<AbstractAbility, Integer> abilities = new HashMap<>();

    public EventGrimoire(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
        getAbilities().forEach(ability -> {
            AbstractAbility abstractAbility = ability.create.get();
            this.abilities.put(abstractAbility, 0);
        });
        getAbilitiesM1().forEach(ability -> {
            AbstractAbility abstractAbility = ability.create.get();
            this.abilities.put(abstractAbility, 1);
        });
        getAbilitiesM2().forEach(ability -> {
            AbstractAbility abstractAbility = ability.create.get();
            abstractAbility.setPveMasterUpgrade2(true);
            this.abilities.put(abstractAbility, 2);
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % (20 * getAbilityActivationPeriod()) == 0) {
            AbstractAbility ability = new ArrayList<>(abilities.keySet()).get(ThreadLocalRandom.current().nextInt(abilities.size()));
            boolean scriptedAlive = pveOption.getMobs().stream().anyMatch(mob -> mob.getMobRegistry() == Mob.EVENT_SCRIPTED_GRIMOIRE);
            switch (abilities.get(ability)) {
                case 1 -> ability.setPveMasterUpgrade(scriptedAlive);
                case 2 -> ability.setPveMasterUpgrade2(scriptedAlive);
            }
            ability.onActivate(warlordsNPC);
        }
    }

    public abstract int getAbilityActivationPeriod();

    public abstract EnumSet<Ability> getAbilities();

    public abstract EnumSet<Ability> getAbilitiesM1();

    public abstract EnumSet<Ability> getAbilitiesM2();

}
