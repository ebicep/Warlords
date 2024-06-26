package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractStrike;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.avenger.AvengerStrikeBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AvengersStrike extends AbstractStrike implements Damages<AvengersStrike.DamageValues> {

    public float energyStole = 0;
    private final DamageValues damageValues = new DamageValues();
    private float energySteal = 10;

    public AvengersStrike() {
        super("Avenger's Strike", 0, 90);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Strike the targeted enemy player, causing")
                               .append(Damages.formatDamage(damageValues.strikeDamage))
                               .append(Component.text("damage and removing "))
                               .append(Component.text(format(energySteal), NamedTextColor.YELLOW))
                               .append(Component.text(" energy."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));
        info.add(new Pair<>("Energy Removed", "" + Math.round(energyStole)));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new AvengerStrikeBranch(abilityTree, this);
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "paladin.paladinstrike.activation", 2, 1);
        randomHitEffect(location, 5, 255, 0, 0);
        EffectUtils.displayParticle(
                Particle.SPELL,
                location,
                4,
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                1
        );
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        float multiplier = 1;
        float healthDamage = 0;
        if (nearPlayer instanceof WarlordsNPC warlordsNPC) {
            if (pveMasterUpgrade) {
                AbstractMob mob = warlordsNPC.getMob();
                if (mob.getLevel() <= 3) {
                    multiplier += 0.4f;
                } else if (mob.getLevel() == 4 || mob.getLevel() == 5) {
                    healthDamage = nearPlayer.getMaxHealth() * 0.005f;
                }
            } else if (pveMasterUpgrade2) {
                int enemiesNearBy = Math.toIntExact(PlayerFilter.entitiesAround(wp, 20, 20, 20).aliveEnemiesOf(wp).stream().count());
                if (enemiesNearBy >= 2) {
                    multiplier += 0.25f;
                } else {
                    multiplier += 0.5f;
                }
            }
        }
        healthDamage = DamageCheck.clamp(healthDamage);

        nearPlayer.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .min((damageValues.strikeDamage.getMinValue() * multiplier) + (pveMasterUpgrade ? healthDamage : 0))
                .max((damageValues.strikeDamage.getMaxValue() * multiplier) + (pveMasterUpgrade ? healthDamage : 0))
                .crit(damageValues.strikeDamage)
        );

        if (pveMasterUpgrade) {
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(nearPlayer, 4, 4, 4)
                    .aliveEnemiesOf(wp)
                    .closestFirst(nearPlayer)
                    .excluding(nearPlayer)
                    .limit(2)
            ) {
                we.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .min(((damageValues.strikeDamage.getMinValue() * multiplier) + (pveMasterUpgrade ? healthDamage : 0)) * 0.5f)
                        .max(((damageValues.strikeDamage.getMaxValue() * multiplier) + (pveMasterUpgrade ? healthDamage : 0)) * 0.5f)
                        .crit(damageValues.strikeDamage)
                );
                Bukkit.getPluginManager().callEvent(new WarlordsStrikeEvent(wp, this, we));
            }
        }

        energyStole += nearPlayer.subtractEnergy(name, energySteal, true);
        return true;
    }

    public float getEnergySteal() {
        return energySteal;
    }

    public void setEnergySteal(float energySteal) {
        this.energySteal = energySteal;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(359, 485, 25, 185);
        private final List<Value> values = List.of(strikeDamage);

        public Value.RangedValueCritable getStrikeDamage() {
            return strikeDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}
