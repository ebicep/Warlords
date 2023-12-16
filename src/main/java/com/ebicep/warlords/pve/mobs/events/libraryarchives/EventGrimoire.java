package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class EventGrimoire extends AbstractMob implements BossMob {

    private final List<AbstractAbility> abilities = new ArrayList<>();

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
            this.abilities.add(abstractAbility);
        });
        getAbilitiesM1().forEach(ability -> {
            AbstractAbility abstractAbility = ability.create.get();
            abstractAbility.setPveMasterUpgrade(true);
            this.abilities.add(abstractAbility);
        });
        getAbilitiesM2().forEach(ability -> {
            AbstractAbility abstractAbility = ability.create.get();
            abstractAbility.setPveMasterUpgrade2(true);
            this.abilities.add(abstractAbility);
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 20 * getAbilityActivationPeriod() == 0) {
            AbstractAbility ability = abilities.get(ThreadLocalRandom.current().nextInt(abilities.size()));
            ability.onActivate(warlordsNPC);
        }
    }

    public abstract int getAbilityActivationPeriod();

    public abstract EnumSet<Ability> getAbilities();

    public abstract EnumSet<Ability> getAbilitiesM1();

    public abstract EnumSet<Ability> getAbilitiesM2();


}
