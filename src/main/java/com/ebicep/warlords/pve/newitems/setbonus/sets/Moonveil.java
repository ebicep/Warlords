package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.delta.OtherworldlyAmulet;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Moonveil extends BaseSet {

    private static final List<String> EFFECTS = List.of(
            "Arcane Shield",
            "Ice Barrier",
            "Last Stand",
            "Intervene",
            "Spirits' Respite",
            "Mystical Barrier Shield",
            "Guardian Beam Shield",
            "Contagious Facade Shield"
    );

    private int critChanceBoost;

    @Override
    public void init() {
        super.init();
        this.critChanceBoost = getValue("critChanceBoost", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "moonveil";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(critChanceBoost);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Moonveil.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(Modifier.MODIFY_OUTGOING_CRIT_CHANCE, (event, currentCritChance) -> {
                        for (String effect : EFFECTS) {
                            if (warlordsPlayer.getCooldownManager().hasCooldownFromName(effect)) {
                                currentCritChance.addModifier(FloatModifiable.ModifierType.ADDITIVE, getName(), 25f);
                            }
                        }
                    }
            ));
        }

    }

}