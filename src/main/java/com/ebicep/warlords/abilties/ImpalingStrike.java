package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImpalingStrike extends AbstractStrikeBase {

    protected float healingDoneFromEnemyCarrier = 0;

    private int leechDuration = 5;
    private float leechAllyAmount = 25;
    private float leechSelfAmount = 15;

    public ImpalingStrike() {
        super("Impaling Strike", 323, 427, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Impale an enemy, dealing" + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage and afflict them with the §aLEECH §7effect for §6" + leechDuration +
                " §7seconds. Whenever an ally deals damage to a leeched enemy, they heal for §a" + format(leechAllyAmount) +
                "% §7of the damage dealt. You heal for §a" + format(leechSelfAmount) +
                "% §7of the damage you deal to a leeched enemy instead.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));

        return info;
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "rogue.apothecarystrike.activation", 2, 0.5f);
        Utils.playGlobalSound(location, "mage.fireball.activation", 2, 1.8f);
        randomHitEffect(location, 7, 100, 255, 100);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        Optional<WarlordsDamageHealingFinalEvent> finalEvent = nearPlayer.addDamageInstance(
                wp,
                name,
                minDamageHeal,
                maxDamageHeal,
                critChance,
                critMultiplier,
                false
        );

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

        return true;
    }

    public void addHealingDoneFromEnemyCarrier(float amount) {
        this.healingDoneFromEnemyCarrier += amount;
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


}
