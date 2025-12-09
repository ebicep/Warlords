package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractGroundSlam;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.GroundSlamBranchDefender;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;
import java.util.Set;

public class GroundSlamDefender extends AbstractGroundSlam implements Damages<GroundSlamDefender.DamageValues> {

    private final DamageValues damageValues = new DamageValues();

    public GroundSlamDefender() {
        super(AbstractAbilityBuilder.create("groundSlamDefender").pvp());
    }

    public GroundSlamDefender(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

    @Override
    public Value.RangedValueCritable getSlamDamage() {
        return damageValues.slamDamage;
    }

    @Override
    protected void onSecondSlamHit(WarlordsEntity wp, Set<WarlordsEntity> playersHit) {
        if (pveMasterUpgrade2) {
            float damageReduction = 1 - Math.min(5, playersHit.size()) * .05f;
            wp.getCooldownManager()
              .addCooldown(new RegularCooldown<>(
                      "Reverberation",
                      "REVERB",
                      GroundSlamDefender.class,
                      new GroundSlamDefender(),
                      wp,
                      CooldownTypes.BUFF,
                      cooldownManager -> {
                      },
                      5 * 20
              ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                  currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, damageReduction);
                      }
              ));
        }
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new GroundSlamBranchDefender(abilityTree, this);
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable slamDamage = new Value.RangedValueCritable(293, 397, 20, 175);

        private List<Value> values = List.of(slamDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.slamDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("slamDamage"), Value.RangedValueCritable.class);
            this.values = List.of(slamDamage);
        }

    }

}
