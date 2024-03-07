package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.MobHologram;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.CustomAttackStrategy;
import net.citizensnpcs.api.ai.NavigatorParameters;
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
            float damageResistance,
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

        NavigatorParameters defaultParameters = this.npc.getNavigator().getDefaultParameters();
        defaultParameters.attackStrategy(CustomAttackStrategy.ATTACK_STRATEGY);
        defaultParameters.attackRange(1)
                         .stuckAction(null) // disable tping to player if too far away
                         .updatePathRate(5)
                         .distanceMargin(.75)
                         .speedModifier(.9f)
                         .range(100);

        npc.data().set(NPC.Metadata.COLLIDABLE, false);

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
                new MobHologram.TextDisplayHologram(50) {
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

        modifyStats.accept(warlordsNPC);

        if (spawner instanceof WarlordsPlayer) {
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
