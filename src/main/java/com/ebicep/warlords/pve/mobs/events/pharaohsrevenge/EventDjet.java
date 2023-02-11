package com.ebicep.warlords.pve.mobs.events.pharaohsrevenge;

import com.ebicep.warlords.abilties.CripplingStrike;
import com.ebicep.warlords.abilties.SoulShackle;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class EventDjet extends AbstractZombie implements BossMob {

    private boolean fireFlameBursts = false;

    public EventDjet(Location spawnLocation) {
        super(spawnLocation,
                "Djet",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.BURNING_WITHER_SKELETON),
                        null,
                        null,
                        null,
                        null
                ),
                9000,
                0.32f,
                10,
                930,
                1210
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        int currentWave = option.getWaveCounter();
        if (currentWave % 5 == 0 && currentWave > 5) {
            float additionalHealthMultiplier = 1 + .15f * (currentWave / 5f - 1);
            warlordsNPC.setMaxBaseHealth(warlordsNPC.getMaxBaseHealth() * additionalHealthMultiplier);
            warlordsNPC.heal();
        }
        AbstractAbility redAbility = warlordsNPC.getRedAbility();
        redAbility.setMinDamageHeal(1200);
        redAbility.setMinDamageHeal(1380);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 60 == 0 && aboveHealthThreshold()) {
            warlordsNPC.getRedAbility().onActivate(warlordsNPC, null);
        }
        if (ticksElapsed % 100 == 0) {
            for (WarlordsPlayer warlordsPlayer : PlayerFilterGeneric
                    .playingGameWarlordsPlayers(warlordsNPC.getGame())
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                SoulShackle.shacklePlayer(warlordsPlayer, warlordsPlayer, 60);
                warlordsPlayer.getCooldownManager().addCooldown(new RegularCooldown<CripplingStrike>(
                        name,
                        "CRIP",
                        CripplingStrike.class,
                        new CripplingStrike(),
                        warlordsNPC,
                        CooldownTypes.DEBUFF,
                        cooldownManager -> {
                        },
                        cooldownManager -> {
                            if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("CRIP").stream().count() == 1) {
                                warlordsPlayer.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "crippled" + ChatColor.GRAY + ".");
                            }
                        },
                        3 * 20
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * .9f;
                    }
                });
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (aboveHealthThreshold()) {
            warlordsNPC.getSpec().setDamageResistance(10);
        } else {
            warlordsNPC.getSpec().setDamageResistance(30);
        }
    }

    private boolean aboveHealthThreshold() {
        return !(warlordsNPC.getHealth() <= warlordsNPC.getMaxBaseHealth() * .75);
    }
}
