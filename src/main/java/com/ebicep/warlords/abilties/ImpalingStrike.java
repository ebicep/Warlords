package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ImpalingStrike extends AbstractStrike {

    public static void giveLeechCooldown(
            WarlordsEntity wp,
            WarlordsEntity target,
            int secondDuration,
            float selfHealMultiplier,
            float allyHealMultiplier,
            Consumer<WarlordsDamageHealingFinalEvent> finalEvent
    ) {
        target.getCooldownManager().removeCooldown(ImpalingStrike.class, false);
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Leech Debuff",
                "LCH",
                ImpalingStrike.class,
                new ImpalingStrike(),
                wp,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                secondDuration * 20
        ) {
            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                float healingMultiplier;
                if (event.getAttacker() == wp) {
                    healingMultiplier = selfHealMultiplier;
                } else {
                    healingMultiplier = allyHealMultiplier;
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
                    finalEvent.accept(warlordsDamageHealingFinalEvent);
                    if (event.getWarlordsEntity().hasFlag()) {
                        this.getCooldownObject().addHealingDoneFromEnemyCarrier(warlordsDamageHealingFinalEvent.getValue());
                    }
                });
            }

            @Override
            public PlayerNameData addSuffixFromOther() {
                return new PlayerNameData(Component.text("LCH", NamedTextColor.RED),
                        we -> we.isEnemy(target) || (we.isTeammate(target) && we.getSpecClass().specType == SpecType.HEALER)
                );
            }
        });
    }

    public void addHealingDoneFromEnemyCarrier(float amount) {
        this.healingDoneFromEnemyCarrier += amount;
    }

    protected float healingDoneFromEnemyCarrier = 0;
    private int leechDuration = 5;
    private float leechAllyAmount = 25;
    private float leechSelfAmount = 15;

    public ImpalingStrike() {
        super("Impaling Strike", 323, 427, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Impale an enemy, dealing")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text("damage and afflict them with the "))
                               .append(Component.text("LEECH", NamedTextColor.GREEN))
                               .append(Component.text(" effect for "))
                               .append(Component.text(leechDuration, NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Whenever an ally deals damage to a leeched enemy, they heal for "))
                               .append(Component.text(format(leechAllyAmount) + "%", NamedTextColor.GREEN))
                               .append(Component.text(" of the damage dealt. You heal for "))
                               .append(Component.text(format(leechSelfAmount) + "%", NamedTextColor.GREEN))
                               .append(Component.text(" of the damage you deal to a leeched enemy instead."));

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
        int multiplier = pveMasterUpgrade && nearPlayer.getCooldownManager().hasCooldownFromName("Leech Debuff") ? 3 : 1;
        Optional<WarlordsDamageHealingFinalEvent> finalEvent = nearPlayer.addDamageInstance(
                wp,
                name,
                minDamageHeal * multiplier,
                maxDamageHeal * multiplier,
                critChance,
                critMultiplier,
                false
        );

        giveLeechCooldown(
                wp,
                nearPlayer,
                leechDuration,
                leechSelfAmount / 100f,
                leechAllyAmount / 100f,
                warlordsDamageHealingFinalEvent -> {
                }
        );

        return true;
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
