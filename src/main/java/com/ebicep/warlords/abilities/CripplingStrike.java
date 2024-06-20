package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractStrike;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.revenant.CripplingStrikeBranch;
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

public class CripplingStrike extends AbstractStrike implements Damages<CripplingStrike.DamageValues> {

    public static void cripple(WarlordsEntity from, WarlordsEntity target, String name, int tickDuration) {
        cripple(from, target, name, 0, tickDuration, .9f);
    }

    public static void cripple(
            WarlordsEntity from,
            WarlordsEntity target,
            String name,
            int consecutiveStrikeCounter,
            int tickDuration,
            float crippleAmount
    ) {
        CripplingStrikeData cripplingStrikeData = new CripplingStrikeData(consecutiveStrikeCounter);
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "CRIP",
                CripplingStrikeData.class,
                cripplingStrikeData,
                from,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                cooldownManager -> {
                    if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("CRIP").stream().count() == 1) {
                        target.sendMessage(Component.text("You are no longer ", NamedTextColor.GRAY)
                                                    .append(Component.text("crippled", NamedTextColor.RED))
                                                    .append(Component.text(".", NamedTextColor.GRAY)));
                    }
                },
                tickDuration
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * crippleAmount;
            }

            @Override
            public PlayerNameData addSuffixFromOther() {
                return new PlayerNameData(Component.text("CRIP", NamedTextColor.RED),
                        we -> we == from || (we.isTeammate(target) && we.getSpecClass().specType == SpecType.HEALER)
                );
            }
        });
    }

    private final int crippleDuration = 3;
    private final DamageValues damageValues = new DamageValues();
    private int cripple = 10;
    private int cripplePerStrike = 5;

    public CripplingStrike() {
        super("Crippling Strike", 362.25f, 498, 0, 100, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Strike the targeted enemy player, causing ")
                               .append(Damages.formatDamage(damageValues.strikeDamage))
                               .append(Component.text(" damage and "))
                               .append(Component.text("crippling ", NamedTextColor.RED))
                               .append(Component.text("them for "))
                               .append(Component.text(format(crippleDuration), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. A "))
                               .append(Component.text("crippled ", NamedTextColor.RED))
                               .append(Component.text("player deals "))
                               .append(Component.text(format(cripple) + "%", NamedTextColor.RED))
                               .append(Component.text(" less damage for the duration of the effect. Adds "))
                               .append(Component.text(format(cripplePerStrike) + "%", NamedTextColor.RED))
                               .append(Component.text(" less damage dealt per additional strike. (Max " + format(cripple + (cripplePerStrike * 2)) + "%" + ")"));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new CripplingStrikeBranch(abilityTree, this);
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "warrior.mortalstrike.impact", 2, 1);
        randomHitEffect(location, 7, 255, 0, 0);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        nearPlayer.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .value(damageValues.strikeDamage)
        ).ifPresent(finalEvent -> onFinalEvent(wp, nearPlayer, finalEvent));

        if (pveMasterUpgrade || pveMasterUpgrade2) {
            additionalHit(1, wp, nearPlayer, warlordsEntity -> {
                warlordsEntity.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .value(damageValues.strikeDamage)
                ).ifPresent(event -> onFinalEvent(wp, event.getWarlordsEntity(), event));
            });
        }

        return true;
    }

    private void onFinalEvent(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer, WarlordsDamageHealingFinalEvent finalEvent) {
        if (finalEvent.isDead()) {
            if (pveMasterUpgrade2) {
                wp.getAbilitiesMatching(OrbsOfLife.class).forEach(ability -> ability.subtractCurrentCooldown(.25f));
                playCooldownReductionEffect(finalEvent.getWarlordsEntity());
            }
            return;
        }

        Optional<CripplingStrikeData> optionalCripplingStrike = new CooldownFilter<>(nearPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(CripplingStrikeData.class)
                .findAny();
        if (optionalCripplingStrike.isPresent()) {
            CripplingStrikeData data = optionalCripplingStrike.get();
            nearPlayer.getCooldownManager().removeCooldown(CripplingStrike.class, true);
            int newCrippleCounter = Math.min(data.consecutiveStrikeCounter + 1, 2);
            cripple(wp,
                    nearPlayer,
                    name,
                    newCrippleCounter,
                    crippleDuration * 20,
                    convertToDivisionDecimal(cripple) - newCrippleCounter * convertToPercent(cripplePerStrike)
            );
        } else {
            nearPlayer.sendMessage(Component.text("You are ", NamedTextColor.GRAY)
                                            .append(Component.text("crippled", NamedTextColor.RED))
                                            .append(Component.text(".", NamedTextColor.GRAY)));
            cripple(wp, nearPlayer, name, crippleDuration * 20, convertToDivisionDecimal(cripple));
        }
    }

    public static void cripple(
            WarlordsEntity from,
            WarlordsEntity target,
            String name,
            int tickDuration,
            float crippleAmount
    ) {
        cripple(from, target, name, 0, tickDuration, crippleAmount);
    }

    public int getCripple() {
        return cripple;
    }

    public void setCripple(int cripple) {
        this.cripple = cripple;
    }

    public int getCripplePerStrike() {
        return cripplePerStrike;
    }

    public void setCripplePerStrike(int cripplePerStrike) {
        this.cripplePerStrike = cripplePerStrike;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(362.25f, 498, 20, 175);
        private final List<Value> values = List.of(strikeDamage);

        public Value.RangedValueCritable getStrikeDamage() {
            return strikeDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public record CripplingStrikeData(int consecutiveStrikeCounter) {

    }

}