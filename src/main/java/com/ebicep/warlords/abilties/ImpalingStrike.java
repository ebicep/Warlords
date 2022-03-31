package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ImpalingStrike extends AbstractStrikeBase {

    private int leechDuration = 5;
    private float healingDoneFromEnemyCarrier = 0;

    public ImpalingStrike() {
        super("Impaling Strike", 323, 427, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Impale an enemy, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7and afflict them with the §aLEECH §7effect for §6" + leechDuration + " §7seconds.\n" +
                "§7Whenever an ally (including yourself) deals\n" +
                "§7damage to a leeched enemy, they heal for §a15%\n" +
                "§7of the damage dealt. You heal for §a25% §7of the\n" +
                "§7damage you deal to a leeched enemy instead.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        nearPlayer.getCooldownManager().removeCooldown(ImpalingStrike.class);
        nearPlayer.getCooldownManager().addCooldown(new RegularCooldown<ImpalingStrike>(
                "Leech Debuff",
                "LEECH",
                ImpalingStrike.class,
                new ImpalingStrike(),
                wp,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                leechDuration * 20
        ) {
            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                float healingMultiplier;
                if (event.getAttacker() == wp) {
                    healingMultiplier = 0.25f;
                } else {
                    healingMultiplier = 0.15f;
                }

                event.getAttacker().addHealingInstance(
                        wp,
                        "Leech",
                        currentDamageValue * healingMultiplier,
                        currentDamageValue * healingMultiplier,
                        -1,
                        100,
                        false,
                        false
                ).ifPresent(warlordsDamageHealingFinalEvent -> {
                    if (event.getPlayer().hasFlag()) {
                        this.getCooldownObject().addHealingDoneFromEnemyCarrier(warlordsDamageHealingFinalEvent.getValue());
                    }
                });
            }
        });
    }

    public int getLeechDuration() {
        return leechDuration;
    }

    public void setLeechDuration(int leechDuration) {
        this.leechDuration = leechDuration;
    }

    public float getHealingDoneFromEnemyCarrier() {
        return healingDoneFromEnemyCarrier;
    }

    public void addHealingDoneFromEnemyCarrier(float amount) {
        this.healingDoneFromEnemyCarrier += amount;
    }
}
