package com.ebicep.warlords.abilities;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.ChasingBlockEffect;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.EarthenSpikeBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;

public class EarthenSpike extends AbstractAbility implements WeaponAbilityIcon, HitBox, Damages<EarthenSpike.DamageValues>, AbilityStats<EarthenSpike, EarthenSpike.EarthenSpikeStats> {

    public static final Map<UUID, Long> PLAYER_SPIKE_COOLDOWN = new HashMap<>();

    private static final String[] REPEATING_SOUND = new String[]{
            "shaman.earthenspike.animation.a",
            "shaman.earthenspike.animation.b",
            "shaman.earthenspike.animation.c",
            "shaman.earthenspike.animation.d"
    };
    private final EarthenSpikeStats stats = new EarthenSpikeStats();
    private final DamageValues damageValues = new DamageValues();
    private FloatModifiable radius = new FloatModifiable(10);
    private float speed = 1;
    private double spikeHitbox = 2.5;
    private double verticalVelocity = .625;

    public EarthenSpike() {
        super(AbstractAbilityBuilder.create("earthenSpike").pvp());
    }

    public EarthenSpike(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.speed = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speed"), float.class);
        this.spikeHitbox = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("spikeHitbox"), float.class);
        this.verticalVelocity = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("verticalVelocity"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        List<WarlordsEntity> spiked = new ArrayList<>();
        float rad = radius.getCalculatedValue() + PacketUtils.pingCompensationAmount(wp);
        for (WarlordsEntity spikeTarget : PlayerFilter.entitiesAround(wp, rad, rad, rad).aliveEnemiesOf(wp).lookingAtFirst(wp)) {
            if (!LocationUtils.isLookingAt(wp, spikeTarget) || !LocationUtils.hasLineOfSight(wp, spikeTarget)) {
                continue;
            }
            spiked.add(spikeTarget);
            spikeTarget(wp, spikeTarget);
            break;
        }
        return !spiked.isEmpty();
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Send forth an underground earth spike that locks onto a targeted enemy player. When the spike reaches its target it emerges from the ground, dealing ")
                .damage(damageValues.spikeDamage)
                .text(" damage to any nearby enemies and launches them up into the air.")
                .initialRange(radius)
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EarthenSpikeBranch(abilityTree, this);
    }

    protected void spikeTarget(@Nonnull WarlordsEntity wp, WarlordsEntity spikeTarget) {
        spikeTarget(wp, spikeTarget, wp, new ArrayList<>());
    }

    protected void spikeTarget(@Nonnull WarlordsEntity wp, WarlordsEntity spikeTarget, @Nonnull WarlordsEntity startEntity,  List<WarlordsEntity> spiked) {
        Location startLocation = startEntity.getLocation();
        new ChasingBlockEffect.Builder()
                .setGame(wp.getGame())
                .setSpeed(speed)
                .setDestination(() -> spikeTarget.isDead() ? null : spikeTarget.getLocation())
                .setOnTick(ticksElapsed -> {
                    if (ticksElapsed % 5 == 1) {
                        Utils.playGlobalSound(startLocation, REPEATING_SOUND[(ticksElapsed / 5) % 4], 2, 1);
                    }
                })
                .setOnDestinationReached(() -> {
                    UUID spikeUUID = UUID.randomUUID();
                    Location targetLocation = spikeTarget.getLocation();
                    if (pveMasterUpgrade2) {
                        spiked.add(spikeTarget);
                        onSpikeTarget(wp, spikeTarget, spikeUUID);
                        chainNextSpike(wp, spikeTarget, spiked, radius.getCalculatedValue());
                    } else {
                        for (WarlordsEntity nearSpikeTarget : PlayerFilter
                                .entitiesAround(targetLocation, spikeHitbox, spikeHitbox, spikeHitbox)
                                .aliveEnemiesOf(wp)
                        ) {
                            onSpikeTarget(wp, nearSpikeTarget, spikeUUID);
                        }
                    }
                    if (pveMasterUpgrade) {
                        new GameRunnable(wp.getGame()) {

                            @Override
                            public void run() {
                                FallingBlockWaveEffect.create(targetLocation.add(0, 1, 0), 4, 7, Material.DIRT);
                                for (WarlordsEntity wave : PlayerFilter.entitiesAround(targetLocation, 6, 6, 6).aliveEnemiesOf(wp)) {
                                    wave.addInstance(InstanceBuilder
                                            .damage()
                                            .cause("Earthen Rupture")
                                            .source(wp)
                                            .value(damageValues.spikeDamage));
                                    wave.addSpeedModifier(wp, "Spike Slow", -35, 20);
                                }
                                Utils.playGlobalSound(targetLocation, Sound.BLOCK_GRAVEL_BREAK, 2, 0.5f);
                                EffectUtils.displayParticle(Particle.EXPLOSION, targetLocation, 2, 1, 1, 1, 0.01F);
                            }
                        }.runTaskLater(15);
                    }
                    Utils.playGlobalSound(wp.getLocation(), "shaman.earthenspike.impact", 2, 1);
                    targetLocation.setYaw(0);
                    for (int i = 0; i < 100; i++) {
                        if (targetLocation.clone().add(0, -1, 0).getBlock().getType() == Material.AIR) {
                            targetLocation.add(0, -1, 0);
                        } else {
                            break;
                        }
                    }
                    ArmorStand stand = Utils.spawnArmorStand(targetLocation.add(0, -.6, 0), armorStand -> {
                                armorStand.getEquipment().setHelmet(new ItemStack(Material.BROWN_MUSHROOM));
                                armorStand.setMarker(true);
                            }
                    );
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            stand.remove();
                            this.cancel();
                        }
                    }.runTaskTimer(Warlords.getInstance(), 10, 0);
                })
                .setMaxTicks(30)
                .create()
                .start(new LocationBuilder(startLocation).y(startLocation.getBlockY()));
    }

    protected void onSpikeTarget(WarlordsEntity caster, WarlordsEntity spikeTarget, UUID uuid) {
        stats.targetsSpiked++;
        if (spikeTarget.hasFlag()) {
            stats.carrierSpiked++;
        }
        spikeTarget.addInstance(InstanceBuilder
                .damage().ability(this)
                .source(caster)
                .value(damageValues.spikeDamage)
                .uuid(uuid)
        ).ifPresent(finalEvent -> {
            boolean closeToGround = LocationUtils.getDistance(spikeTarget.getEntity(), .1) < 1.82;
            boolean offSpikeCooldown = PLAYER_SPIKE_COOLDOWN.get(spikeTarget.getUuid()) == null || PLAYER_SPIKE_COOLDOWN.get(spikeTarget.getUuid()) + 750 < System.currentTimeMillis();
            if (closeToGround && offSpikeCooldown) {
                PLAYER_SPIKE_COOLDOWN.put(spikeTarget.getUuid(), System.currentTimeMillis());
                new GameRunnable(caster.getGame()) {
                    @Override
                    public void run() {
                        spikeTarget.setVelocity(name, new Vector(0, verticalVelocity, 0), false);
                    }
                }.runTaskLater(1);
            }
            if (!pveMasterUpgrade2) {
                return;
            }
            if (finalEvent.isCrit()) {
                caster.addEnergy(caster, "Earthen Verdancy", 10);
            }
            if (finalEvent.isDead()) {
                float healing = finalEvent.getValue() * .35f;
                caster.addInstance(InstanceBuilder
                        .healing()
                        .cause("Earthen Verdancy")
                        .source(caster)
                        .value(healing)
                        .showAsCrit(finalEvent.isCrit())
                );
            }
        });
        if (pveMasterUpgrade2) {
            spikeTarget.getCooldownManager().removeCooldownByName("Earthen Verdancy");
            CripplingStrike.cripple(caster, spikeTarget, "Earthen Verdancy", 5 * 20);
        }
    }
    // recursive spike chaining
    private void chainNextSpike(WarlordsEntity caster, WarlordsEntity lastTarget, List<WarlordsEntity> spiked, float radius) {
        int spikeHits = spiked.size(); // number of spike hits
        if (spikeHits >= 4) {
            return;
        }
        Optional<WarlordsEntity> nextTarget = PlayerFilter // find next closest target that hasn't been spiked yet
                .entitiesAround(lastTarget, radius, radius, radius)
                .aliveEnemiesOf(caster)
                .excluding(spiked)
                .closestFirst(lastTarget)
                .findFirst();
        if (nextTarget.isEmpty()) {
            return;
        }
        WarlordsEntity newTarget = nextTarget.get();
        spikeTarget(caster, newTarget, lastTarget, spiked);
    }


    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    @Override
    public EarthenSpikeStats getAbilityStats() {
        return stats;
    }

    public void setVerticalVelocity(double verticalVelocity) {
        this.verticalVelocity = verticalVelocity;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public double getSpikeHitbox() {
        return spikeHitbox;
    }

    public void setSpikeHitbox(double spikeHitbox) {
        this.spikeHitbox = spikeHitbox;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable spikeDamage = new Value.RangedValueCritable(404, 562, 15, 175);

        private Value.RangedValue earthenRuptureDamage = new Value.RangedValue(548, 695);

        private List<Value> values = List.of(spikeDamage, earthenRuptureDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.spikeDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("spikeDamage"), Value.RangedValueCritable.class);
            this.earthenRuptureDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameDamage("earthenRuptureDamage"),
                    Value.RangedValue.class
            );
            this.values = List.of(spikeDamage, earthenRuptureDamage);
        }

        public Value.RangedValueCritable getSpikeDamage() {
            return spikeDamage;
        }

    }

    public static class EarthenSpikeStats extends AbstractAbilityStats<EarthenSpike, EarthenSpikeStats> {

        @Field("targets_spiked")
        private int targetsSpiked = 0;

        @Field("carrier_spiked")
        private int carrierSpiked = 0;

        @Override
        public Class<EarthenSpikeStats> getClazz() {
            return EarthenSpikeStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Spiked", targetsSpiked));
            statsDisplay.add(new AbilityStatDisplay("Times Carrier Spiked", carrierSpiked));
            return statsDisplay;
        }

        @Override
        public EarthenSpikeStats merge(EarthenSpikeStats other, int multiplier) {
            EarthenSpikeStats stats = super.merge(other, multiplier);
            stats.targetsSpiked = this.targetsSpiked + other.targetsSpiked * multiplier;
            stats.carrierSpiked = this.carrierSpiked + other.carrierSpiked * multiplier;
            return stats;
        }

        @Override
        public EarthenSpikeStats create() {
            return new EarthenSpikeStats();
        }

    }

}
