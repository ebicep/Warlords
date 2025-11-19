package com.ebicep.warlords.pve.mobs.pvp;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TricksterDummy extends AbstractMob implements PlayerMob {

    private WarlordsEntity warlordsEntity;

    public TricksterDummy(Location spawnLocation) {
        this(spawnLocation, "Dummy", 1000, 0, 0, 0, 0);
    }

    public TricksterDummy(
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
                maxMeleeDamage
        );
    }


    public TricksterDummy(Location spawnLocation, int health, WarlordsEntity warlordsEntity) {
        this(spawnLocation, "Dummy", health, 0, 0, 0, 0);
        this.warlordsEntity = warlordsEntity;
        this.equipment = new Utils.SimpleEntityEquipment(
                warlordsEntity.getHelmet(),
                warlordsEntity.getChestplate(),
                warlordsEntity.getLeggings(),
                warlordsEntity.getBoots(),
                warlordsEntity.getWeaponItem()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TRICKSTER_DUMMY;
    }

    @Override
    public void giveGoals() {

    }

    @Override
    public void onSpawn(PveOption option) {
        this.warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Dummy Health",
                null,
                TricksterDummy.class,
                null,
                warlordsNPC,
                CooldownTypes.INTERNAL,
                cooldownManager -> {},
                false
        ) {
            @Override
            protected Listener getListener() {
                return new Listener() {

                    @EventHandler(ignoreCancelled = true)
                    public void onDamageHealEvent(WarlordsDamageHealingEvent event) {
                        if (!event.getWarlordsEntity().equals(warlordsNPC)) {
                            return;
                        }
                        if (!event.isHealingInstance()) {
                            return;
                        }
                        event.setPlayer(warlordsEntity);
                    }

                    @EventHandler
                    public void onFinalDamageTaken(WarlordsDamageHealingFinalEvent event) {
                        if (!event.getWarlordsEntity().equals(warlordsNPC)) {
                            return;
                        }
                        if (event.isHealingInstance()) {
                            return;
                        }
                        WarlordsEntity attacker = event.getSource();
                        final FloatModifiable energyPerHit = new FloatModifiable(attacker.getEnergyPerHit().getCalculatedValue());
                        for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
                            abstractCooldown.applyModifiers(Modifier.ENERGY_GAIN_PER_HIT, m -> m.apply(energyPerHit));
                        }
                        energyPerHit.refresh();
                        attacker.addEnergy(attacker, null, -energyPerHit.getCalculatedValue());

                        attacker.addInstance(InstanceBuilder
                                .damage()
                                .cause("Trickster Dummy")
                                .source(warlordsEntity)
                                .value(event.getValue())
                                .showAsCrit(event.isCrit())
                        );
                    }

                    @EventHandler(priority = EventPriority.LOWEST)
                    public void onWarlordsDeathEvent(WarlordsDeathEvent event) {
                        if (event.getWarlordsEntity().equals(warlordsNPC)) {
                            event.setForceCancel(true);
                            event.setCancelled(true);
                            new GameRunnable(event.getGame()) {
                                @Override
                                public void run() {
                                    warlordsNPC.cleanup();
                                    warlordsNPC.getGame().getPlayers().remove(warlordsNPC.getUuid());
                                    Warlords.removePlayer(warlordsNPC.getUuid());
                                }
                            }.runTaskLater(1);

                        }
                    }

                };
            }
        });
    }

}
