package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public class Bulwark extends BaseSet {

    private int radius;
    private int damageReduction;
    private int healthThreshold;

    @Override
    public void init() {
        super.init();
        this.radius = getValue("radius", int.class);
        this.damageReduction = getValue("damageReduction", int.class);
        this.healthThreshold = getValue("healthThreshold", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "bulwark";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(radius, damageReduction, healthThreshold);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Bulwark.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (ticksElapsed % 3 == 0) {
                            PlayerFilter.entitiesAround(warlordsPlayer, radius, radius, radius)
                                    .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                                    .forEach(ally -> {
                                        ally.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                getName(),
                                                null,
                                                Bulwark.class,
                                                null,
                                                warlordsPlayer,
                                                CooldownTypes.BUFF,
                                                cooldownManager -> {},
                                                4
                                        ).addModifier(
                                                Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE,
                                                (event, currentDamageValue) -> {
                                                    boolean isBelowThreshold = ally.getCurrentHealth() / ally.getMaxHealth() < healthThreshold / 100f;
                                                    float finalDamageReduction = 1 - (isBelowThreshold ? damageReduction * 2 : damageReduction) / 100f;
                                                    currentDamageValue.addModifier(
                                                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                                            warlordsPlayer.getName() + "'s " + getName(),
                                                            finalDamageReduction
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