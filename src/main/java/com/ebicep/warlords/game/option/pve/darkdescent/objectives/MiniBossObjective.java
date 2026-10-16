package com.ebicep.warlords.game.option.pve.darkdescent.objectives;

import com.ebicep.warlords.game.option.pve.darkdescent.DarkDescentOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MiniBossObjective implements DarkDescentObjective {

    private final DarkDescentOption option;
    private AbstractMob boss;

    public MiniBossObjective(DarkDescentOption option) {
        this.option = option;
    }

    @Override
    public Component getTitle() {
        return Component.text("Slay the mini boss", NamedTextColor.RED);
    }

    @Override
    public void tick() {
        if (boss != null) {
            return;
        }
        List<Mob> miniBosses = option.getMiniBosses();
        boss = miniBosses.get(ThreadLocalRandom.current().nextInt(miniBosses.size())).createMob(option.randomArenaLocation(10));
        float health = boss.getWarlordsNPC().getMaxHealth();
        boss.getWarlordsNPC().setMaxHealthAndHeal(health * 4);
        option.spawnNewMob(boss);
    }

    @Override
    public boolean isComplete() {
        return boss != null && boss.getWarlordsNPC().isDead();
    }

    @Override
    public Component getScoreboardLine() {
        if (boss == null) {
            return Component.text("Mini Boss: ").append(Component.text("Approaching", NamedTextColor.YELLOW));
        }
        return Component.text("Mini Boss: ").append(isComplete()
                                                    ? Component.text("Slain", NamedTextColor.GREEN)
                                                    : Component.text(boss.getWarlordsNPC().getName(), NamedTextColor.RED));
    }
}
