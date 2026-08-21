package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IlluminatedPrism extends BaseSet {

    private int repeatedAttackDamageReduction;
    private int repeatedAttackMaxDamageReduction;

    @Override
    public void init() {
        super.init();
        this.repeatedAttackDamageReduction = getValue("repeatedAttackDamageReduction", int.class);
        this.repeatedAttackMaxDamageReduction = getValue("repeatedAttackMaxDamageReduction", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "illuminatedPrism";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(repeatedAttackDamageReduction, repeatedAttackMaxDamageReduction);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            Map<AbstractMob, Integer> repeatedAttacks = new ConcurrentHashMap<>();
            PveOption pveOption = warlordsPlayer.getGame().getOption(PveOption.class)
                    .stream()
                    .findFirst()
                    .get();
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    IlluminatedPrism.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (ticksElapsed % 40 == 0) {
                            repeatedAttacks.entrySet().removeIf(entry -> !pveOption.getMobs().contains(entry.getKey()));
                        }
                    }
            ).addModifier(
                    Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE,
                    (event, currentDamageValue) -> {
                        if (!(event.getSource() instanceof WarlordsNPC warlordsNPC) || warlordsNPC.getMob() == null) {
                            return;
                        }
                        AbstractMob mob = warlordsNPC.getMob();
                        int repeatedAttackCount = repeatedAttacks.getOrDefault(mob, 0);
                        float damageReduction = Math.min(
                                repeatedAttackCount * repeatedAttackDamageReduction,
                                repeatedAttackMaxDamageReduction
                        ) / 100f;
                        repeatedAttacks.merge(mob, 1, Integer::sum);
                        currentDamageValue.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 - damageReduction
                        );
                    }
            ));
        }
    }
}
