package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Splash;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

public class Compression extends BaseSet {

    private int primaryRangeReductionPercent;

    @Override
    public void init() {
        super.init();
        this.primaryRangeReductionPercent = getValue("primaryRangeReductionPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "compression";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(primaryRangeReductionPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            float rangeMultiplier = Math.max(0, 1 - primaryRangeReductionPercent / 100f);
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (!(ability instanceof WeaponAbilityIcon)) {
                    continue;
                }
                if (ability instanceof AbstractPiercingProjectile<?, ?> projectile) {
                    projectile.getMaxDistance().addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            rangeMultiplier
                    );
                } else if (ability instanceof HitBox hitBox) {
                    hitBox.getHitBoxRadius().addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            rangeMultiplier
                    );
                }
                if (ability instanceof Splash splash) {
                    splash.getSplashRadius().addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            rangeMultiplier
                    );
                }
            }
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Compression.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ));
            String primaryName = warlordsPlayer.getSpec().getWeapon().getName();
            warlordsPlayer.getGame().registerEvents(new Listener() {

                @EventHandler
                public void onDamageHeal(WarlordsDamageHealingEvent event) {
                    if (!Objects.equals(event.getSource(), warlordsPlayer) || !Objects.equals(event.getCause(), primaryName)) {
                        return;
                    }
                    event.getCritChance().addModifier(FloatModifiable.ModifierType.OVERRIDING, getName(), 100);
                }

            });
        }

    }

}
