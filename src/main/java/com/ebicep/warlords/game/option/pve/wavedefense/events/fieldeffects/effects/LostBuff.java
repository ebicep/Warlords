package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;

public class LostBuff implements FieldEffect {
    @Override
    public String getName() {
        return "Lost Buff";
    }

    @Override
    public String getDescription() {
        return "Players and mobs will lose 1% of their max health every second.";
    }

    @Override
    public void run(Game game, int ticksElapsed) {
        if (ticksElapsed < 200) {
            return;
        }
        if (ticksElapsed % 20 != 0) {
            return;
        }
        PlayerFilter.playingGame(game)
                    .forEach(warlordsEntity -> {
                        if (warlordsEntity instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof BossMob) {
                            return;
                        }
                        float damage = warlordsEntity.getMaxHealth() * .01f;
                        warlordsEntity.resetRegenTimer();
                        if (warlordsEntity.getHealth() - damage <= 0 && !warlordsEntity.getCooldownManager().checkUndyingArmy(false)) {
                            warlordsEntity.setHealth(0);
                            warlordsEntity.die(warlordsEntity);
                        } else {
                            warlordsEntity.setHealth(warlordsEntity.getHealth() - damage);
                            //warlordsEntity.playHurtAnimation(warlordsEntity.getEntity(), warlordsEntity);
                        }
                    });
    }

}
