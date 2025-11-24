package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.omega.LovelyOmamori;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;
import java.util.Set;

public class OtherworldlyAmulet extends SpecialDeltaBuckler implements CraftsInto {

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


    public OtherworldlyAmulet() {
    }

    public OtherworldlyAmulet(Set<BasicStatPool> statPool) {
        super(statPool);
    }


    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                OtherworldlyAmulet.class,
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

    @Override
    public String getDescription() {
        return "Where did this come from? Where did it go?";
    }

    @Override
    public String getBonus() {
        return "+25% Crit chance when you are under vanilla damage preventing effects.";
    }

    @Override
    public String getName() {
        return "Otherworldly Amulet";
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new LovelyOmamori(statPool);
    }

}
