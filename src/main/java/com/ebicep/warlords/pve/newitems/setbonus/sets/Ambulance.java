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
import org.bukkit.util.Vector;

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
            String speedName = getName() + " Speed";
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Ambulance.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticks) -> {
                        if (ticks % 10 != 0) {
                            return;
                        }
                        lowHealthAllies.forEach(ally -> ally.removePotionEffect(PotionEffectType.GLOWING));
                        lowHealthAllies.clear();
                        PlayerFilter
                                .entitiesAround(warlordsPlayer, 100, 100, 100)
                                .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                                .filter(ally -> ally.getCurrentHealth() / ally.getMaxHealth() * 100f <= allyHealthThresholdPercent)
                                .forEach(ally -> {
                                    lowHealthAllies.add(ally);
                                    ally.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 15, 1, false, false, false));
                                });
                        WarlordsEntity closest = PlayerFilter
                                .entitiesAround(warlordsPlayer, 100, 100, 100)
                                .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                                .filter(lowHealthAllies::contains)
                                .closestFirst(warlordsPlayer)
                                .findFirstOrNull();
                        warlordsPlayer.getSpeed().removeModifier(speedName);
                        if (closest != null && isMovingToward(warlordsPlayer, closest)) {
                            warlordsPlayer.addSpeedModifier(
                                    warlordsPlayer,
                                    speedName,
                                    movementSpeedBonusPercent,
                                    15
                            );
                        }
                    }
            ).addModifier(Modifier.MODIFY_OUTGOING_HEALING, (event, currentHealValue) -> {
                if (lowHealthAllies.contains(event.getWarlordsEntity())) {
                    currentHealValue.addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            1 + healingBonusToAllyPercent / 100f
                    );
                }
            }));
        }

        private boolean isMovingToward(WarlordsPlayer warlordsPlayer, WarlordsEntity ally) {
            Vector movement = warlordsPlayer.getEntity().getVelocity().setY(0);
            Vector toAlly = ally
                    .getLocation()
                    .toVector()
                    .subtract(warlordsPlayer.getLocation().toVector())
                    .setY(0);
            return movement.lengthSquared() >= 0.0001 &&
                    toAlly.lengthSquared() > 0 &&
                    movement.normalize().dot(toAlly.normalize()) > 0.25;
        }

    }

}
