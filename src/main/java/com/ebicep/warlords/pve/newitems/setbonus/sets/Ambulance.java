package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ambulance extends BaseSet {

    private int allyHealthThresholdPercent;
    private int movementSpeedBonusPercent;
    private int healingBonusToAllyPercent;

    @Override
    public void init() {
        super.init();
        this.allyHealthThresholdPercent = getValue("allyHealthThresholdPercent", int.class);
        this.movementSpeedBonusPercent = getValue("movementSpeedBonusPercent", int.class);
        this.healingBonusToAllyPercent = getValue("healingBonusToAllyPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "ambulance";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(allyHealthThresholdPercent, movementSpeedBonusPercent, healingBonusToAllyPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        private final Set<WarlordsEntity> lowHealthAllies = new HashSet<>();

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Ambulance.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {},
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (ticksElapsed % 10 != 0) {
                            return;
                        }

                        lowHealthAllies.forEach(ally -> {
                            ally.removePotionEffect(PotionEffectType.GLOWING);
                        });
                        lowHealthAllies.clear();

                        for (WarlordsEntity ally : PlayerFilter
                                .entitiesAround(warlordsPlayer, 100, 100, 100)
                                .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                        ) {
                            float healthPercent = (ally.getCurrentHealth() / ally.getMaxHealth()) * 100f;

                            if (healthPercent <= allyHealthThresholdPercent) {
                                lowHealthAllies.add(ally);
                                ally.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 15, 1, false, false, false));
                            }
                        }

                        if (!lowHealthAllies.isEmpty()) {
                            WarlordsEntity closestLowHealthAlly = PlayerFilter
                                    .entitiesAround(warlordsPlayer, 100, 100, 100)
                                    .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                                    .filter(lowHealthAllies::contains)
                                    .closestFirst(warlordsPlayer)
                                    .findFirstOrNull();

                            if (closestLowHealthAlly != null) {
                                double distance = warlordsPlayer.getLocation().distance(closestLowHealthAlly.getLocation());
                                if (distance > 0) {
                                    // apply speed modifier
                                    warlordsPlayer.getCooldownManager().removeCooldownByName(getName() + " Speed");
                                    warlordsPlayer.addSpeedModifier(
                                            warlordsPlayer,
                                            getName() + " Speed",
                                            movementSpeedBonusPercent,
                                            25
                                    );
                                }
                            }
                        } else {
                            warlordsPlayer.getSpeed().removeModifier(getName() + " Speed");
                        }
                    }
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_HEALING,
                    (event, currentHealingValue) -> {
                        if (lowHealthAllies.contains(event.getWarlordsEntity())) {
                            currentHealingValue.addModifier(
                                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                    getName(),
                                    1 + (healingBonusToAllyPercent / 100f)
                            );
                        }
                    }
            ));

        }

    }

}