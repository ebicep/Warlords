package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LegendaryRequiem extends AbstractLegendaryWeapon {

    public LegendaryRequiem() {
    }

    public LegendaryRequiem(UUID uuid) {
        super(uuid);
    }

    public LegendaryRequiem(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getPassiveEffect() {
        return "Gain a 20% Damage and Healing bonus for 60s whenever a teammate dies (Max 3 stacks)";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 180;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);


        player.getGame().registerEvents(new Listener() {

            final AtomicDouble damageHealBonus = new AtomicDouble(0);
            RegularCooldown<LegendaryRequiem> cooldown = null;

            @EventHandler
            public void onDeath(WarlordsDeathEvent event) {
                WarlordsEntity warlordsEntity = event.getPlayer();
                if (warlordsEntity.isTeammate(player) && !warlordsEntity.equals(player)) {
                    damageHealBonus.set(Math.min(.6, damageHealBonus.get() + .20));
                    if (cooldown == null || !player.getCooldownManager().hasCooldown(cooldown)) {
                        player.getCooldownManager().addCooldown(cooldown = new RegularCooldown<>(
                                "Requiem",
                                "REQ",
                                LegendaryRequiem.class,
                                null,
                                player,
                                CooldownTypes.BUFF,
                                cooldownManager -> {
                                    cooldown = null;
                                    damageHealBonus.set(0);
                                },
                                60 * 20
                        ) {
                            @Override
                            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return (float) (currentDamageValue * (1 + damageHealBonus.get()));
                            }

                            @Override
                            public float doBeforeHealFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                                return (float) (currentHealValue * (1 + damageHealBonus.get()));
                            }

                        });
                    } else {
                        cooldown.setTicksLeft(60 * 20);
                    }
                }
            }

        });

    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.REQUIEM;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 160;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 175;
    }

    @Override
    protected float getHealthBonusValue() {
        return 800;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 8;
    }
}
