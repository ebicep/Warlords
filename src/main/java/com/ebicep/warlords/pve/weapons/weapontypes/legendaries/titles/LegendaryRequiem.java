package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.annotation.Transient;

import java.util.UUID;

public class LegendaryRequiem extends AbstractLegendaryWeapon {

    public static final int MELEE_DAMAGE_MIN = 160;
    public static final int MELEE_DAMAGE_MAX = 180;
    public static final int CRIT_CHANCE = 20;
    public static final int CRIT_MULTIPLIER = 175;
    public static final int HEALTH_BONUS = 800;
    public static final int SPEED_BONUS = 8;

    @Transient
    public float damageHealBonus = 0;

    public LegendaryRequiem() {
    }

    public LegendaryRequiem(UUID uuid) {
        super(uuid);
    }

    public LegendaryRequiem(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getTitle() {
        return "Requiem";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        damageHealBonus = 0;

        player.getGame().registerEvents(new Listener() {

            RegularCooldown<LegendaryRequiem> cooldown = null;

            @EventHandler
            public void onDeath(WarlordsDeathEvent event) {
                if (event.getPlayer().isTeammate(player)) {
                    damageHealBonus = (float) Math.min(.6, damageHealBonus + .20);
                    if (cooldown == null) {
                        player.getCooldownManager().addCooldown(cooldown = new RegularCooldown<>(
                                "Requiem",
                                "REQ",
                                LegendaryRequiem.class,
                                null,
                                player,
                                CooldownTypes.BUFF,
                                cooldownManager -> {
                                    cooldown = null;
                                    damageHealBonus = 0;
                                },
                                60 * 20
                        ) {
                            @Override
                            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * (1 + damageHealBonus);
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
    public String getPassiveEffect() {
        return "Gain a 20% Damage and Healing bonus for 60s whenever a teammate dies (Max 3 stacks)";
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}
