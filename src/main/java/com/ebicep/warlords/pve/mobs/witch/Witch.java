package com.ebicep.warlords.pve.mobs.witch;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;

public class Witch extends AbstractWitch implements EliteMob {

    public Witch(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Deacon",
                MobTier.ELITE,
                null,
                3500,
                0.05f,
                10,
                0,
                0
        );
    }

    @Override
    public void onSpawn(PveOption option) {

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 4 == 0) {
            new CircleEffect(
                    warlordsNPC.getGame(),
                    warlordsNPC.getTeam(),
                    warlordsNPC.getLocation(),
                    9,
                    new CircumferenceEffect(ParticleEffect.SPELL_WITCH, ParticleEffect.REDSTONE).particlesPerCircumference(1),
                    new DoubleLineEffect(ParticleEffect.SPELL)
            ).playEffects();
        }

        for (WarlordsEntity ally : PlayerFilter
                .entitiesAround(warlordsNPC, 9, 9, 9)
                .aliveTeammatesOfExcludingSelf(warlordsNPC)
        ) {
            EffectUtils.playRandomHitEffect(ally.getLocation(), 0, 150, 0, 2);
            ally.addSpeedModifier(warlordsNPC, "Witch Speed Buff", 20, 3 * 20);
            if (option.getDifficulty() == DifficultyIndex.HARD || option.getDifficulty() == DifficultyIndex.ENDLESS) {
                ally.getCooldownManager().removeDebuffCooldowns();
                ally.getSpeed().removeSlownessModifiers();
            }
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
        attacker.getCooldownManager().subtractTicksOnRegularCooldowns(CooldownTypes.ABILITY, 5);
    }
}
