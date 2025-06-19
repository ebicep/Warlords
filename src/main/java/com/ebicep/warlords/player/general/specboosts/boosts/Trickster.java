package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.IncendiaryCurse;
import com.ebicep.warlords.abilities.OrderOfEviscerate;
import com.ebicep.warlords.abilities.ShadowStep;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerSwapEvent;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.MobHologram;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.pvp.TricksterDummy;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Trickster implements SpecBoostManager.SpecBoost<Trickster> {

    private int incendiaryCurseDamageThresholdDecrease;
    private int incendiaryCurseDamageIncrease;
    private int incendiaryCurseEnergyCostIncrease;
    private int soulSwitchInvisTickDuration;
    private int soulSwitchDummyDurationTicks;
    private int soulSwitchDummyHealth;
    private float shadowLeapCooldownReductionSeconds;
    private int shadowLeapHealthThreshold;

    @Override
    public void init() {
        this.incendiaryCurseDamageThresholdDecrease = getValue("incendiaryCurseDamageThresholdDecrease", int.class);
        this.incendiaryCurseDamageIncrease = getValue("incendiaryCurseDamageIncrease", int.class);
        this.incendiaryCurseEnergyCostIncrease = getValue("incendiaryCurseEnergyCostIncrease", int.class);
        this.soulSwitchInvisTickDuration = getValue("soulSwitchInvisTickDuration", int.class);
        this.soulSwitchDummyDurationTicks = getValue("soulSwitchDummyDurationTicks", int.class);
        this.soulSwitchDummyHealth = getValue("soulSwitchDummyHealth", int.class);
        this.shadowLeapCooldownReductionSeconds = getValue("shadowLeapCooldownReductionSeconds", float.class);
        this.shadowLeapHealthThreshold = getValue("shadowLeapHealthThreshold", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "trickster";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                incendiaryCurseDamageThresholdDecrease,
                incendiaryCurseDamageIncrease,
                incendiaryCurseEnergyCostIncrease,
                soulSwitchInvisTickDuration,
                soulSwitchDummyDurationTicks,
                soulSwitchDummyHealth,
                shadowLeapCooldownReductionSeconds,
                shadowLeapHealthThreshold
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Trickster get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(IncendiaryCurse.class).forEach(incendiaryCurse -> {
                incendiaryCurse.setDamageIncreaseHealthThreshold(incendiaryCurse.getDamageIncreaseHealthThreshold() - incendiaryCurseDamageThresholdDecrease);
                incendiaryCurse.getDamageValues().getCurseDamage().forEachValue(floatModifiable ->
                        floatModifiable.addAdditiveModifier("Spec Boost", incendiaryCurseDamageIncrease)
                );
                incendiaryCurse.getEnergyCost().addAdditiveModifier("Spec Boost", incendiaryCurseEnergyCostIncrease);
            });
            warlordsPlayer.getAbilitiesMatching(ShadowStep.class).forEach(shadowStep -> {
                shadowStep.getCooldown().addAdditiveModifier("Spec Boost", -shadowLeapCooldownReductionSeconds);
                shadowStep.setLeapHealThreshold(shadowLeapHealthThreshold);
            });
        }

        @EventHandler
        public void onWarlordsPlayerSwapEvent(WarlordsPlayerSwapEvent event) {
            if (!event.getWarlordsEntity().equals(warlordsEntity)) {
                return;
            }
            OrderOfEviscerate.giveCloak(warlordsEntity, soulSwitchInvisTickDuration);
            Location oldLocation = event.getWarlordsEntity().getLocation();
            Location newLocation = event.getSwappedPlayer().getLocation();
            WarlordsNPC npc = warlordsEntity
                    .getGame()
                    .addNPC(new TricksterDummy(
                            oldLocation,
                            soulSwitchDummyHealth,
                            warlordsEntity
                    ).toNPC(
                            warlordsEntity.getGame(),
                            warlordsEntity.getTeam(),
                            warlordsNPC -> warlordsNPC.getMob().onSpawn(null)
                    ));
            npc.setName(warlordsEntity.getName());
            if (warlordsEntity.getEntity() instanceof Player player) {
                SkinTrait skinTrait = npc.getNpc().getOrAddTrait(SkinTrait.class);
                skinTrait.setSkinPersistent(player);
            }
            Component classNameShortWithBrackets = warlordsEntity.getSpec().getClassNameShortWithBrackets(warlordsEntity.getSpecClass().specType.getTextColor());
            Component levelStringBracket = ExperienceManager.getLevelStringBracket(ExperienceManager.getLevelForSpec(warlordsEntity.getUuid(), warlordsEntity.getSpecClass()));
            List<MobHologram.CustomHologramLine> customHologramLines = npc.getMobHologram().getCustomHologramLines();
            customHologramLines.forEach(customHologramLine -> customHologramLine.setDelete(true));
            customHologramLines.add(new MobHologram.CustomHologramLine(Component.textOfChildren(
                    classNameShortWithBrackets,
                    warlordsEntity.getColoredName(),
                    levelStringBracket
            )));
            new GameRunnable(warlordsEntity.getGame()) {
                @Override
                public void run() {
                    if (npc.isAlive()) {
                        npc.cleanup();
                        getGame().getPlayers().remove(npc.getUuid());
                        Warlords.removePlayer(npc.getUuid());
                    }
                }
            }.runTaskLater(soulSwitchDummyDurationTicks);
        }

    }

}