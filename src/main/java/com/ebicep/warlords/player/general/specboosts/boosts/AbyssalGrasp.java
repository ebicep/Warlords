package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import java.util.List;

public class AbyssalGrasp implements SpecBoostManager.SpecBoost<AbyssalGrasp> {

    private float soulShackleDamageIncreasePercent;
    private int soulShackleCooldownIncreaseTicks;
    private int soulShackleRangeDecrease;
    private float normalPullRange;
    private float flagCarrierPullRange;

    @Override
    public void init() {
        this.soulShackleDamageIncreasePercent = getValue("soulShackleDamageIncreasePercent", float.class);
        this.soulShackleCooldownIncreaseTicks = getValue("soulShackleCooldownIncreaseTicks", int.class);
        this.soulShackleRangeDecrease = getValue("soulShackleRangeDecrease", int.class);
        this.normalPullRange = getValue("normalPullRange", float.class);
        this.flagCarrierPullRange = getValue("flagCarrierPullRange", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "abyssalGrasp";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(soulShackleDamageIncreasePercent, soulShackleCooldownIncreaseTicks, soulShackleRangeDecrease, normalPullRange, flagCarrierPullRange);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public AbyssalGrasp get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;
        private int soulShackleRange;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(SoulShackle.class).forEach(soulShackle -> {
                soulShackle.getDamageValues()
                           .getShackleDamage()
                           .forEachValue(floatModifiable -> floatModifiable.addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER,
                                   "Spec Boost", soulShackleDamageIncreasePercent / 100
                           ));
                soulShackle.getCooldown().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", soulShackleCooldownIncreaseTicks / 20f);
                this.soulShackleRange = soulShackle.getShackleRange() - soulShackleRangeDecrease;
                soulShackle.setShackleRange(soulShackleRange);
            });
        }

        @EventHandler
        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof SoulShackle soulShackle)) {
                return;
            }
            WarlordsEntity target = event.getWarlordsEntity();
            Location location = new LocationBuilder(warlordsEntity.getLocation());
            new GameRunnable(warlordsEntity.getGame()) {

                int ticksElapsed = 0;

                @Override
                public void run() {
                    ticksElapsed++;
                    if ((ticksElapsed >= normalPullRange || target.hasFlag() && ticksElapsed >= flagCarrierPullRange) ||
                            (warlordsEntity.isDead() || target.isDead())
                    ) {
                        this.cancel();
                        return;
                    }
                    float ratio = (float) ticksElapsed / soulShackleRange;
                    Location targetLoc = target.getLocation();
                    Location newLocation = new Location(location.getWorld(),
                            MathUtils.lerp(targetLoc.getX(), location.getX(), ratio),
                            MathUtils.lerp(targetLoc.getY(), location.getY(), ratio),
                            MathUtils.lerp(targetLoc.getZ(), location.getZ(), ratio),
                            targetLoc.getYaw(),
                            targetLoc.getPitch()
                    );
                    EffectUtils.playChainAnimation(warlordsEntity, target, SoulShackle.ITEM_STACK, ticksElapsed);
                    target.teleportLocationOnly(newLocation);
                }
            }.runTaskTimer(0, 1);
        }

    }

}
