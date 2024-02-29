package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.MobHologram;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class TowerDefenseMob extends AbstractMob {

    @Nullable
    private WarlordsEntity spawner;

    public TowerDefenseMob(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public WarlordsNPC toNPC(Game game, Team team, Consumer<WarlordsNPC> modifyStats) {
        EntityType entityType = getMobRegistry().entityType;
        this.npc = NPCManager.NPC_REGISTRY.createNPC(entityType, name);
        npc.data().set(NPC.Metadata.COLLIDABLE, false);
        npc.getDefaultGoalController().clear();
        npc.getNavigator().getDefaultParameters().distanceMargin(1);

        giveGoals();
        onNPCCreate();
        updateEquipment();

        this.npc.spawn(spawnLocation);

        if (npc.getEntity() instanceof Player player) {
            player.setNoDamageTicks(0);
        }

        this.warlordsNPC = new WarlordsNPC(
                name,
                npc,
                game,
                team,
                maxHealth,
                walkSpeed,
                minMeleeDamage,
                maxMeleeDamage,
                meleeCritChance,
                meleeCritMutiplier,
                this,
                playerClass,
                new MobHologram.TextDisplayHologram() {
                    @Nullable
                    @Override
                    public Entity getEntity() {
                        if (warlordsNPC == null) {
                            return null;
                        }
                        return warlordsNPC.getEntity();
                    }

                }
        );
        if (spawner != null) {
            warlordsNPC.getMobHologram().getCustomHologramLines().add(new MobHologram.CustomHologramLine(spawner.getColoredName()));
        }
        for (AbstractAbility ability : warlordsNPC.getAbilities()) {
            if (ability.getCurrentCooldown() < ability.getCooldownValue()) {
                warlordsNPC.setEnergy(warlordsNPC.getEnergy() + ability.getEnergyCostValue());
            }
        }

        return warlordsNPC;
    }

    @Override
    public void giveGoals() {

    }

    public void setSpawner(@Nullable WarlordsEntity spawner) {
        this.spawner = spawner;
    }
}
