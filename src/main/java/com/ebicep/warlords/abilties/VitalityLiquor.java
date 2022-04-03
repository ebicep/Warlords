package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class VitalityLiquor extends AbstractAbility {

    private final int acuRange = 8;
    private final int duration = 3;
    private final float minWaveHealing = 268;
    private final float maxWaveHealing = 324;

    public VitalityLiquor() {
        super("Vitality Liquor", 359, 485, 14, 30, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Discharge a shockwave of special potions\n" +
                "§7around you, healing allies in the range for\n" +
                "§a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health." +
                "\n\n" +
                "§7Each enemy afflicted with your §aLEECH §7effect\n" +
                "§7within the range will cause the enemy to\n" +
                "§7discharge an additional shockwave of vitality\n" +
                "§7that heals §e2 §7nearby allies for §a" + format(minWaveHealing) + " §7- §a" + format(maxWaveHealing) + " §7health\n" +
                "§7and increase their energy regeneration by\n" +
                "§e15 §7for §6" + duration + " §7seconds.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        VitalityLiquor tempVitalityLiquor = new VitalityLiquor();
        wp.subtractEnergy(energyCost);
        wp.addHealingInstance(
                wp,
                name,
                minDamageHeal,
                maxDamageHeal,
                critChance,
                critMultiplier,
                false,
                false
        );

        Utils.playGlobalSound(player.getLocation(), Sound.GLASS, 2, 0.1f);
        Utils.playGlobalSound(player.getLocation(), Sound.BLAZE_DEATH, 2, 0.7f);

        new FallingBlockWaveEffect(player.getLocation(), 7, 1, Material.SAPLING, (byte) 2).play();

        for (WarlordsPlayer acuTarget : PlayerFilter
                .entitiesAround(player, acuRange, acuRange, acuRange)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            acuTarget.addHealingInstance(
                    wp,
                    name,
                    minDamageHeal,
                    maxDamageHeal,
                    critChance,
                    critMultiplier,
                    false,
                    false
            );
        }

        for (WarlordsPlayer enemyTarget : PlayerFilter
                .entitiesAround(player, acuRange, acuRange, acuRange)
                .aliveEnemiesOf(wp)
        ) {
            new CooldownFilter<>(enemyTarget, RegularCooldown.class)
                    .filterCooldownClass(ImpalingStrike.class)
                    .filterCooldownFrom(wp)
                    .findAny()
                    .ifPresent(regularCooldown -> {
                        Utils.playGlobalSound(enemyTarget.getLocation(), Sound.GLASS, 2, 0.6f);
                        new GameRunnable(wp.getGame()) {
                            @Override
                            public void run() {
                                for (WarlordsPlayer allyTarget : PlayerFilter
                                        .entitiesAround(enemyTarget, 6, 6, 6)
                                        .aliveTeammatesOf(wp)
                                        .closestFirst(enemyTarget)
                                        .limit(2)
                                ) {
                                    allyTarget.addHealingInstance(
                                            wp,
                                            name,
                                            minWaveHealing,
                                            maxWaveHealing,
                                            critChance,
                                            critMultiplier,
                                            false,
                                            false
                                    );
                                    allyTarget.getCooldownManager().removeCooldown(VitalityLiquor.class);
                                    allyTarget.getCooldownManager().addRegularCooldown(
                                            "Vitality Liquor",
                                            "VITAL",
                                            VitalityLiquor.class,
                                            tempVitalityLiquor,
                                            wp,
                                            CooldownTypes.BUFF,
                                            cooldownManager -> {
                                            },
                                            duration * 20
                                    );
                                }
                            }
                        }.runTaskLater(5);

                        FireWorkEffectPlayer.playFirework(enemyTarget.getLocation(), FireworkEffect.builder()
                                .withColor(Color.ORANGE)
                                .with(FireworkEffect.Type.STAR)
                                .build());
                    });
        }

        return true;
    }

}
