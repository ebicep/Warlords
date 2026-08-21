package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.delta.CrossNecklaceCharm;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Charm extends BaseSet {

    private int radius;
    private int damage;
    private int slow;

    @Override
    public void init() {
        super.init();
        this.radius = getValue("radius", int.class);
        this.damage = getValue("damage", int.class);
        this.slow = getValue("slow", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "charm";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(radius, damage, slow);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Charm.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (ticksElapsed % 3 == 0) {
                            PlayerFilter.entitiesAround(warlordsPlayer, radius, radius, radius)
                                    .aliveEnemiesOf(warlordsPlayer)
                                    .forEach(enemy -> {
                                        enemy.addSpeedModifier(enemy, getName(), -slow, 3);
                                        enemy.getCooldownManager().removeCooldownByName(getName() + " Damage");
                                        enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                getName() + " Damage",
                                                null,
                                                Charm.class,
                                                null,
                                                enemy,
                                                CooldownTypes.ITEM,
                                                cooldownManager -> {
                                                },
                                                3
                                        ).addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                                                    currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                                            getName() + " Damage", 1 + damage / 100f
                                                    );
                                                }
                                        ));
                                    });
                        }
                    }
            ));
        }

    }

}