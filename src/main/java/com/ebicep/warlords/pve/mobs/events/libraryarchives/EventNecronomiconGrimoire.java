package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.bukkit.Laser;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class EventNecronomiconGrimoire extends AbstractMob implements BossMinionMob {

    private int smiteTickCooldown = 10 * 20;
    private int timesSmited = 0;
    @Nullable
    private WarlordsEntity targetWarlordsEntity = null;
    @Nullable
    private Laser.GuardianLaser laser;

    public EventNecronomiconGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Necronomicon Grimoire",
                10000,
                0f,
                10,
                0,
                0
        );
    }

    public EventNecronomiconGrimoire(
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
    public Mob getMobRegistry() {
        return Mob.EVENT_NECRONOMICON_GRIMOIRE;
    }

    @Override
    public void onNPCCreate() {
        super.onNPCCreate();
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(
                name,
                "gkxfGK/hYI3zcHkp5z5/1b3g7dR6Fer9svBrJYX/pHLAefgt/xVnFPHlzfQ08MMH7l4QuNAyVkqmU1MrLqBKwT7Z4oeXSItgsGEvwdo5RKPI7CR7JxSVx9lJVdE5Mg77FFeXRaEkBC8pLqBAHEkath3Fegpul4DkB7lpb0UOJPZPapFzC8vMKbXxnKIxVMCun4hIbeiFyaKOSLlWFtq1xy+1akkiufadyqv85L5nZDMbdiG588lNHBzGRAgp5eK2poFB+vOuoL8GMAove187xBdu+yYUlmCM0GR5In4ktITK4Jc5ILUX2ZzhdZhXXsR+y3y15ajsAAhGPcG8oMKDz+PSb2Zm7JXvCT08NggfvP2TX0lOiy2AJ5uC1B2Wacf8Bmx/MugQLsoZ00Y/Uk/DUvs1F2qemeBNHH62CDIS8PhdbROjjW8tl+PDQiGcHqAL4UiuCBO+r5DAnDARJzH1Bi0xGEjQCHol/v9M6PfmFgRX3a9xL1IUrl99CQvtqN7fNXbFbZXPMO6PpcCkq3qfGNTDLH2LWkGD16FwR8FDLorS1xg1zGXBuLIfIZ0oTOZrHgKyafepe5JgDrGesBoWsT5CQK6gXLFjuEKtBYI+jNT1ecdzjyL65roknAOez2WsiBLFJxFsP0RF2ra8tXd7Qrpdz8nVgZ75ZXc9AnmRU9U=",
                "ewogICJ0aW1lc3RhbXAiIDogMTY5NzU1NzY5MTMzNSwKICAicHJvZmlsZUlkIiA6ICJmYzg3ZTI3YTYwZjY0NjdhOGMwMDgyMmI2ZWY5ZTMyNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJhbmRyZWlvX28iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTdlNzA2ZGViNGIwYjkzMmM5MDA2YjEyNDQ5YjIyY2FjYjNkMmFmYmNmMjNkYzM0YjMzMjlhYzA1YTNjOTI0ZiIKICAgIH0KICB9Cn0="
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_WITHER_SPAWN, 500, 1.3f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Entity target = getTarget();
        if (target == null) {
            return;
        }
        targetWarlordsEntity = Warlords.getPlayer(target);
        if (targetWarlordsEntity == null) {
            return;
        }
        if (smiteTickCooldown > 0) {
            smiteTickCooldown--;
            if (laser == null) {
                try {
                    if (target instanceof LivingEntity livingEntity) {
                        laser = new Laser.GuardianLaser(getLaserStartLocation(), livingEntity, 25, -1);
                    } else {
                        laser = new Laser.GuardianLaser(getLaserStartLocation(), targetWarlordsEntity.getEyeLocation(), 25, -1);
                    }
                    laser.start();
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    laser.moveStart(getLaserStartLocation());
                    if (target instanceof LivingEntity livingEntity) {
                        laser.attachEndEntity(livingEntity);
                    } else {
                        laser.moveEnd(targetWarlordsEntity.getLocation());
                    }
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ticksElapsed % 10 == 0) {
                float pitch = 2 - smiteTickCooldown / 5f / 20 + .1f;
                Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, pitch);
            }
            if (smiteTickCooldown == 0) {
                smite();
            }
        }
    }

    private Location getLaserStartLocation() {
        return new LocationBuilder(warlordsNPC.getEyeLocation()).backward(.25f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        if (laser != null) {
            laser.stop();
        }
    }

    private void smite() {
        if (targetWarlordsEntity == null) {
            return;
        }
        timesSmited++;
        if (timesSmited == 2) {
            pveOption.despawnMob(this);
            if (laser != null) {
                laser.stop();
            }
        }
        targetWarlordsEntity.addDamageInstance(
                warlordsNPC,
                "Smite",
                3250,
                3250,
                0,
                100,
                EnumSet.of(InstanceFlags.TRUE_DAMAGE)
        ).ifPresent(event -> {
            if (!event.isDead()) {
                smiteTickCooldown = 10 * 20;
                return;
            }
            warlordsNPC.addHealingInstance(
                    warlordsNPC,
                    "Smite Kill",
                    2500,
                    2500,
                    0,
                    100
            );
            smiteTickCooldown = 5 * 20;
        });
        EffectUtils.playParticleLinkAnimation(targetWarlordsEntity.getLocation(), warlordsNPC.getLocation(), 255, 0, 0, 2, 2);
        EffectUtils.strikeLightning(targetWarlordsEntity.getLocation(), false);
        for (int i = 0; i < 3; i++) {
            Utils.playGlobalSound(targetWarlordsEntity.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 500, .6f);
        }
    }

}
