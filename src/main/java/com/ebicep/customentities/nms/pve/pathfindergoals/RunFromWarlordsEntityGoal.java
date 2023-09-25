package com.ebicep.customentities.nms.pve.pathfindergoals;

//public class RunFromWarlordsEntityGoal extends Goal {
//
//    private final double walkSpeedModifier = 1;
//    private final double sprintSpeedModifier = 1.5;
//
//    private final Mob mob;
//    private Path path;
//    private final PathNavigation pathNav;
//    private WarlordsEntity toAvoid;
//
//
//    public RunFromWarlordsEntityGoal(Mob mob) {
//        this.mob = mob;
//        this.pathNav = mob.getNavigation();
//        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
//    }
//
//
//    @Override
//    public boolean canUse() {
//        WarlordsEntity thisWarlordsEntity = Warlords.getPlayer(mob.getBukkitEntity());
//        if (thisWarlordsEntity == null) {
//            return false;
//        }
//        List<LivingEntity> nearbyWarlordEntities = GoalUtils.getNearbyWarlordEntities(mob, thisWarlordsEntity, 30);
//        nearbyWarlordEntities.sort((o1, o2) -> Double.compare(o1.distanceToSqr(this.mob), o2.distanceToSqr(this.mob)));
//        if (nearbyWarlordEntities.isEmpty()) {
//            return false;
//        }
//        LivingEntity avoidEntity = nearbyWarlordEntities.get(0);
//        this.toAvoid = Warlords.getPlayer(avoidEntity.getBukkitEntity());
//        if (this.toAvoid == null) {
//            return false;
//        }
//        Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, avoidEntity.position());
//        if (vec3 == null) {
//            return false;
//        }
//        if (avoidEntity.distanceToSqr(vec3.x, vec3.y, vec3.z) < avoidEntity.distanceToSqr(this.mob)) {
//            return false;
//        }
//        this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
//        return this.path != null;
//    }
//
//    @Override
//    public boolean canContinueToUse() {
//        return !this.pathNav.isDone();
//    }
//
//    @Override
//    public void start() {
//        this.pathNav.moveTo(this.path, this.walkSpeedModifier);
//    }
//
//    @Override
//    public void stop() {
//        this.toAvoid = null;
//    }
//
//    @Override
//    public void tick() {
//        Location avoidLocation = this.toAvoid.getLocation();
//        if (this.mob.distanceToSqr(avoidLocation.getX(), avoidLocation.getY(), avoidLocation.getZ()) < 49.0D) {
//            this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
//        } else {
//            this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
//        }
//
//    }
//
//}