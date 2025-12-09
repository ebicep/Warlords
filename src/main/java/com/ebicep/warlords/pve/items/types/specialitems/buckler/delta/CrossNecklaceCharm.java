package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.omega.BreastplateBuckler;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.Set;

public class CrossNecklaceCharm extends SpecialDeltaBuckler implements CraftsInto {

    public CrossNecklaceCharm() {
    }

    public CrossNecklaceCharm(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Exorcism on the go!";
    }

    @Override
    public String getBonus() {
        return "Targets within 6 blocks of you take 10% more damage from all sources and are slowed by 35%.";
    }

    @Override
    public String getName() {
        return "Cross Necklace Chakram";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                CrossNecklaceCharm.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        PlayerFilter.entitiesAround(warlordsPlayer, 6, 6, 6)
                                    .aliveEnemiesOf(warlordsPlayer)
                                    .forEach(enemy -> {
                                        enemy.addSpeedModifier(enemy, getName(), -35, 3);
                                        enemy.getCooldownManager().removeCooldownByName(getName() + " Damage");
                                        enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                getName() + " Damage",
                                                null,
                                                CrossNecklaceCharm.class,
                                                null,
                                                enemy,
                                                CooldownTypes.ITEM,
                                                cooldownManager -> {
                                                },
                                                3
                                        ).addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                                    getName() + " Damage", 1.1f
                                            );
                                                }
                                        ));
                                    });
                    }
                }
        ));
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new BreastplateBuckler(statPool);
    }

}
