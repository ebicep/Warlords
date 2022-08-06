package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ImpalingStrike extends AbstractStrikeBase {
    private boolean pveUpgrade = false;
    private float healingDoneFromEnemyCarrier = 0;

    private int leechDuration = 5;
    private float leechAllyAmount = 25;
    private float leechSelfAmount = 15;

    public ImpalingStrike() {
        super("Impaling Strike", 323, 427, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Impale an enemy, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7and afflict them with the §aLEECH §7effect for §6" + leechDuration + " §7seconds.\n" +
                "§7Whenever an ally (including yourself) deals\n" +
                "§7damage to a leeched enemy, they heal for §a" + leechAllyAmount + "%\n" +
                "§7of the damage dealt. You heal for §a" + leechSelfAmount + "% §7of the\n" +
                "§7damage you deal to a leeched enemy instead.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));

        return info;
    }

    @Override
    protected void onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        if (pveUpgrade) {
            // TODO: add leech
            tripleHit(wp, nearPlayer);
        }
        nearPlayer.getCooldownManager().removeCooldown(ImpalingStrike.class);
        nearPlayer.getCooldownManager().addCooldown(new RegularCooldown<ImpalingStrike>(
                "Leech Debuff",
                "LCH",
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
                    healingMultiplier = (leechSelfAmount / 100f);
                } else {
                    healingMultiplier = (leechAllyAmount / 100f);
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

    public float getLeechSelfAmount() {
        return leechSelfAmount;
    }

    public void setLeechSelfAmount(float leechSelfAmount) {
        this.leechSelfAmount = leechSelfAmount;
    }

    public float getLeechAllyAmount() {
        return leechAllyAmount;
    }

    public void setLeechAllyAmount(float leechAllyAmount) {
        this.leechAllyAmount = leechAllyAmount;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}
