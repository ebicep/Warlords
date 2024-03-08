package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

public class WarlordsTower extends WarlordsEntity {

    @Nonnull
    private final AbstractTower tower;

    public WarlordsTower(
            @Nonnull AbstractTower tower, @Nonnull UUID uuid,
            @Nonnull String name,
            @Nonnull Entity entity,
            @Nonnull Game game,
            @Nonnull Team team,
            @Nonnull AbstractPlayerClass playerClass
    ) {
        super(uuid, name, entity, game, team, playerClass);
        this.tower = tower;
    }

    @Nonnull
    public AbstractTower getTower() {
        return tower;
    }

    @Override
    public void runEveryTick() {
        this.spec.runEveryTick(this);
        getCooldownManager().reduceCooldowns();
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public void updateHealth() {

    }

    @Override
    public void updateEntity() {

    }

    @Override
    public void setDamageResistance(float damageResistance) {

    }

    @Override
    public ItemStack getHead() {
        return null;
    }

    @Nullable
    @Override
    public ItemStack getHelmet() {
        return null;
    }

    @Nullable
    @Override
    public ItemStack getChestplate() {
        return null;
    }

    @Nullable
    @Override
    public ItemStack getLeggings() {
        return null;
    }

    @Nullable
    @Override
    public ItemStack getBoots() {
        return null;
    }

    @Nullable
    @Override
    public ItemStack getWeaponItem() {
        return null;
    }

    @Override
    protected void addToSpecMinuteStats(Consumer<PlayerStatisticsMinute> consumer) {
        // override to do nothing
    }
}
