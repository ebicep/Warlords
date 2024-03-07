package com.ebicep.warlords.pve.mobs.witch;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class WitchDeacon extends AbstractMob implements IntermediateMob {

    public WitchDeacon(Location spawnLocation) {
        super(
                spawnLocation,
                "Witch Deacon",
                3500,
                0.05f,
                10,
                0,
                0,
                new WitchBuff()
        );
    }

    public WitchDeacon(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new WitchBuff()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.WITCH_DEACON;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 4 == 0) {
            new CircleEffect(
                    warlordsNPC.getGame(),
                    warlordsNPC.getTeam(),
                    warlordsNPC.getLocation(),
                    9,
                    new CircumferenceEffect(Particle.SPELL_WITCH, Particle.REDSTONE).particlesPerCircumference(1),
                    new DoubleLineEffect(Particle.SPELL)
            ).playEffects();
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(self.getLocation(), "shaman.earthlivingweapon.impact", 2, 1.7f);
        EffectUtils.playRandomHitEffect(self.getLocation(), 0, 120, 255, 4);
        EffectUtils.playRandomHitEffect(attacker.getLocation(), 0, 120, 255, 4);
        attacker.getCooldownManager().subtractTicksOnRegularCooldowns(5, CooldownTypes.ABILITY);
    }

    private static class WitchBuff extends AbstractPveAbility {

        public WitchBuff() {
            super("Witch Buff", 0, 100);
        }

        @Override
        public void updateDescription(Player player) {

        }

        @Override
        public List<Pair<String, String>> getAbilityInfo() {
            return null;
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {


            DifficultyIndex difficulty = pveOption.getDifficulty();
            boolean removeDebuffs = difficulty == DifficultyIndex.HARD || difficulty == DifficultyIndex.EXTREME || difficulty == DifficultyIndex.ENDLESS;
            for (WarlordsEntity ally : PlayerFilter
                    .entitiesAround(wp, 9, 9, 9)
                    .aliveTeammatesOfExcludingSelf(wp)
            ) {
                EffectUtils.playRandomHitEffect(ally.getLocation(), 0, 150, 0, 2);
                ally.addSpeedModifier(wp, "Witch Speed Buff", 20, 3 * 20);
                if (removeDebuffs) {
                    ally.getCooldownManager().removeDebuffCooldowns();
                    ally.getSpeed().removeSlownessModifiers();
                }
            }
            return true;
        }
    }
}
