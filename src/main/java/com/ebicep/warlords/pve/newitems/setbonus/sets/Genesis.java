package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;

import java.util.List;

public class Genesis extends BaseSet {

    private int healthThreshold;
    private int maxHealthDamageMultiplier;
    private int cooldownSeconds;

    @Override
    public void init() {
        super.init();
        this.healthThreshold = getValue("healthThreshold", int.class);
        this.maxHealthDamageMultiplier = getValue("maxHealthDamageMultiplier", int.class);
        this.cooldownSeconds = getValue("cooldownSeconds", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "genesis";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthThreshold, maxHealthDamageMultiplier, cooldownSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Genesis.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        cooldownSeconds--;
                    }
            ).addModifier(
                    Modifier.ON_INCOMING_DAMAGE,
                    (event, currentDamageValue, isCrit) -> {
                        if (cooldownSeconds > 0) {
                            return;
                        }
                        float lowHealthThreshold = warlordsPlayer.getMaxHealth() * (healthThreshold / 100f);
                        if (warlordsPlayer.getCurrentHealth() < lowHealthThreshold) {
                            Utils.playGlobalSound(warlordsPlayer.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 5, 0.7f);
                            new GameRunnable(warlordsPlayer.getGame()) {
                                @Override
                                public void run() {
                                    EffectUtils.playFirework(warlordsPlayer.getLocation(), FireworkEffect.builder()
                                            .with(FireworkEffect.Type.BALL_LARGE)
                                            .withColor(Color.WHITE)
                                            .withTrail()
                                            .build()
                                    );
                                    EffectUtils.strikeLightningInCylinder(warlordsPlayer.getLocation(), 10, false);
                                    PlayerFilter.entitiesAround(warlordsPlayer, 10, 10, 10)
                                            .aliveEnemiesOf(warlordsPlayer)
                                            .forEach(enemy -> {
                                                        enemy.addInstance(InstanceBuilder
                                                                .damage()
                                                                .cause(getName())
                                                                .source(warlordsPlayer)
                                                                .value(warlordsPlayer.getMaxHealth() * maxHealthDamageMultiplier)
                                                                .flags(InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST)
                                                        );
                                                    }
                                            );
                                    cooldownSeconds = 30 * 20;
                                }
                            }.runTaskLater(40);
                        }
                    }
            ));
        }
    }
}
