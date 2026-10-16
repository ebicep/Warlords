package com.ebicep.warlords.game.option.pve.darkdescent.objectives;

import com.ebicep.warlords.game.option.pve.darkdescent.DarkDescentOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class BossRoomObjective implements DarkDescentObjective {

    private final DarkDescentOption option;
    private final Mob bossType;
    private AbstractMob boss;

    public BossRoomObjective(DarkDescentOption option, Mob bossType) {
        this.option = option;
        this.bossType = bossType;
    }

    @Override
    public Component getTitle() {
        String name = bossType.name != null ? bossType.name : "Descendant";
        return Component.text("Slay the " + name, NamedTextColor.DARK_PURPLE);
    }

    @Override
    public void tick() {
        if (boss != null) {
            return;
        }
        boss = bossType.createMob(option.getFloorCenter().add(6, 0, 0));
        option.spawnNewMob(boss);
    }

    @Override
    public boolean isComplete() {
        return boss != null && boss.getWarlordsNPC() != null && boss.getWarlordsNPC().isDead();
    }

    @Override
    public Component getScoreboardLine() {
        if (!isComplete() && boss != null && boss.getWarlordsNPC() != null) {
            return Component.text("Boss: ").append(Component.text(boss.getName(), NamedTextColor.DARK_PURPLE));
        }
        return Component.text("Boss: ").append(Component.text(isComplete() ? "Slain" : "Approaching", isComplete() ? NamedTextColor.GREEN : NamedTextColor.YELLOW));
    }
}
