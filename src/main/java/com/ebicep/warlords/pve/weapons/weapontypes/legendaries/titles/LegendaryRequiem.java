package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilties.UndyingArmy;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsUndyingArmyPopEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LegendaryRequiem extends AbstractLegendaryWeapon {

    public static final int DAMAGE_HEAL_BOOST = 20;
    public static final int DURATION = 60;
    public static final int MAX_STACKS = 5;

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
        return "Gain a " + DAMAGE_HEAL_BOOST + "% damage and healing bonus whenever a teammate dies or Undying Army is cast/popped on teammates " +
                "(Only applicable to caster).";
    }

    @Override
    public List<Pair<String, String>> getPassiveEffectUpgrade() {
        return null;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 180;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);


        player.getGame().registerEvents(new Listener() {

            final AtomicInteger damageHealBonus = new AtomicInteger(0);
            RegularCooldown<LegendaryRequiem> cooldown = null;

            @EventHandler
            public void onDeath(WarlordsDeathEvent event) {
                WarlordsEntity warlordsEntity = event.getPlayer();
                if (!warlordsEntity.isTeammate(player) || warlordsEntity.equals(player)) {
                    return;
                }
                resetCooldown();
            }

            private void resetCooldown() {
                damageHealBonus.set(Math.min(MAX_STACKS, damageHealBonus.get() + 1));
                if (cooldown == null || !player.getCooldownManager().hasCooldown(cooldown)) {
                    player.getCooldownManager().addCooldown(cooldown = new RegularCooldown<>(
                            "Requiem",
                            "REQ",
                            LegendaryRequiem.class,
                            null,
                            player,
                            CooldownTypes.WEAPON,
                            cooldownManager -> {
                            },
                            cooldownManager -> {
                                cooldown = null;
                                damageHealBonus.set(0);
                            },
                            DURATION * 20
                    ) {
                        @Override
                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            return currentDamageValue * (1 + damageHealBonus.get() * DAMAGE_HEAL_BOOST / 100f);
                        }

                        @Override
                        public float doBeforeHealFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                            return currentHealValue * (1 + damageHealBonus.get() * DAMAGE_HEAL_BOOST / 100f);
                        }

                    });
                } else {
                    cooldown.setTicksLeft(DURATION * 20);
                    cooldown.setName("Requiem " + damageHealBonus.get());
                    cooldown.setNameAbbreviation("REQ " + damageHealBonus.get());
                }
            }

            @EventHandler
            public void onAddCooldown(WarlordsAddCooldownEvent event) {
                AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                if (!(cooldown.getCooldownObject() instanceof UndyingArmy)) {
                    return;
                }
                if (!Objects.equals(cooldown.getFrom(), player)) {
                    return;
                }
                if (cooldown.getFrom() == null || cooldown.getFrom().isEnemy(player)) {
                    return;
                }
                if (Objects.equals(event.getPlayer(), player)) {
                    return;
                }
                resetCooldown();
            }

            @EventHandler
            public void onUndyingArmyPop(WarlordsUndyingArmyPopEvent event) {
                WarlordsEntity warlordsEntity = event.getPlayer();
                if (Objects.equals(warlordsEntity, player)) {
                    return;
                }
                if (warlordsEntity.isEnemy(player)) {
                    return;
                }
                resetCooldown();
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

    @Override
    public LinkedHashMap<Enum<? extends Spendable>, Long> getTitleUpgradeCost(int tier) {
        return null;
    }
}
