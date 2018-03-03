package net.timmy.th2.entity;

import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySpellcasterIllager;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.List;

public class EntityWitch extends EntitySpellcasterIllager {
    private EntitySheep wololoTarget;

    public EntityWitch(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.95F);
        this.experienceValue = 10;
    }

    public static void registerFixesEvoker(DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, EntityWitch.class);
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new AICastingSpell());
        this.tasks.addTask(2, new EntityAIAvoidEntity(this, EntityPlayer.class, 8.0F, 0.6D, 1.0D));
        this.tasks.addTask(4, new AISummonSpell());
        this.tasks.addTask(5, new AIAttackSpell());
        this.tasks.addTask(6, new AIWololoSpell());
        this.tasks.addTask(8, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[]{EntityWitch.class}));
        this.targetTasks.addTask(2, (new EntityAINearestAttackableTarget(this, EntityPlayer.class, true)).setUnseenMemoryTicks(300));
        this.targetTasks.addTask(3, (new EntityAINearestAttackableTarget(this, EntityVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, false));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
    }

    protected void entityInit() {
        super.entityInit();
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
    }

    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_EVOCATION_ILLAGER;
    }

    protected void updateAITasks() {
        super.updateAITasks();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        super.onUpdate();
    }

    /**
     * Returns whether this Entity is on the same team as the given Entity.
     */
    public boolean isOnSameTeam(Entity entityIn) {
        if (entityIn == null) {
            return false;
        } else if (entityIn == this) {
            return true;
        } else if (super.isOnSameTeam(entityIn)) {
            return true;
        } else if (entityIn instanceof EntityVex) {
            return this.isOnSameTeam(((EntityVex) entityIn).getOwner());
        } else if (entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getCreatureAttribute() == EnumCreatureAttribute.ILLAGER) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else {
            return false;
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_EVOCATION_ILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOCATION_ILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_EVOCATION_ILLAGER_HURT;
    }

    @Nullable
    private EntitySheep getWololoTarget() {
        return this.wololoTarget;
    }

    private void setWololoTarget(@Nullable EntitySheep wololoTargetIn) {
        this.wololoTarget = wololoTargetIn;
    }

    protected SoundEvent getSpellSound() {
        return SoundEvents.EVOCATION_ILLAGER_CAST_SPELL;
    }

    class AIAttackSpell extends AIUseSpell {
        private AIAttackSpell() {
            super();
        }

        protected int getCastingTime() {
            return 40;
        }

        protected int getCastingInterval() {
            return 100;
        }

        protected void castSpell() {
            EntityLivingBase entitylivingbase = EntityWitch.this.getAttackTarget();
            double d0 = Math.min(entitylivingbase.posY, EntityWitch.this.posY);
            double d1 = Math.max(entitylivingbase.posY, EntityWitch.this.posY) + 1.0D;
            float f = (float) MathHelper.atan2(entitylivingbase.posZ - EntityWitch.this.posZ, entitylivingbase.posX - EntityWitch.this.posX);

            if (EntityWitch.this.getDistanceSq(entitylivingbase) < 9.0D) {
                for (int i = 0; i < 5; ++i) {
                    float f1 = f + (float) i * (float) Math.PI * 0.4F;
                    this.spawnFangs(EntityWitch.this.posX + (double) MathHelper.cos(f1) * 1.5D, EntityWitch.this.posZ + (double) MathHelper.sin(f1) * 1.5D, d0, d1, f1, 0);
                }

                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 2.0F / 8.0F + ((float) Math.PI * 2F / 5F);
                    this.spawnFangs(EntityWitch.this.posX + (double) MathHelper.cos(f2) * 2.5D, EntityWitch.this.posZ + (double) MathHelper.sin(f2) * 2.5D, d0, d1, f2, 3);
                }
            } else {
                for (int l = 0; l < 16; ++l) {
                    double d2 = 1.25D * (double) (l + 1);
                    int j = 1 * l;
                    this.spawnFangs(EntityWitch.this.posX + (double) MathHelper.cos(f) * d2, EntityWitch.this.posZ + (double) MathHelper.sin(f) * d2, d0, d1, f, j);
                }
            }
        }

        private void spawnFangs(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
            BlockPos blockpos = new BlockPos(p_190876_1_, p_190876_7_, p_190876_3_);
            boolean flag = false;
            double d0 = 0.0D;

            while (true) {
                if (!EntityWitch.this.world.isBlockNormalCube(blockpos, true) && EntityWitch.this.world.isBlockNormalCube(blockpos.down(), true)) {
                    if (!EntityWitch.this.world.isAirBlock(blockpos)) {
                        IBlockState iblockstate = EntityWitch.this.world.getBlockState(blockpos);
                        AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(EntityWitch.this.world, blockpos);

                        if (axisalignedbb != null) {
                            d0 = axisalignedbb.maxY;
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.down();

                if (blockpos.getY() < MathHelper.floor(p_190876_5_) - 1) {
                    break;
                }
            }

            if (flag) {
                EntityEvokerFangs entityevokerfangs = new EntityEvokerFangs(EntityWitch.this.world, p_190876_1_, (double) blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, EntityWitch.this);
                EntityWitch.this.world.spawnEntity(entityevokerfangs);
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOCATION_ILLAGER_PREPARE_ATTACK;
        }

        protected SpellType getSpellType() {
            return SpellType.FANGS;
        }
    }

    class AICastingSpell extends AICastingApell {
        private AICastingSpell() {
            super();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask() {
            if (EntityWitch.this.getAttackTarget() != null) {
                EntityWitch.this.getLookHelper().setLookPositionWithEntity(EntityWitch.this.getAttackTarget(), (float) EntityWitch.this.getHorizontalFaceSpeed(), (float) EntityWitch.this.getVerticalFaceSpeed());
            } else if (EntityWitch.this.getWololoTarget() != null) {
                EntityWitch.this.getLookHelper().setLookPositionWithEntity(EntityWitch.this.getWololoTarget(), (float) EntityWitch.this.getHorizontalFaceSpeed(), (float) EntityWitch.this.getVerticalFaceSpeed());
            }
        }
    }

    class AISummonSpell extends AIUseSpell {
        private AISummonSpell() {
            super();
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if (!super.shouldExecute()) {
                return false;
            } else {
                int i = EntityWitch.this.world.getEntitiesWithinAABB(EntityVex.class, EntityWitch.this.getEntityBoundingBox().grow(16.0D)).size();
                return EntityWitch.this.rand.nextInt(8) + 1 > i;
            }
        }

        protected int getCastingTime() {
            return 100;
        }

        protected int getCastingInterval() {
            return 340;
        }

        protected void castSpell() {
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos = (new BlockPos(EntityWitch.this)).add(-2 + EntityWitch.this.rand.nextInt(5), 1, -2 + EntityWitch.this.rand.nextInt(5));
                EntityVex entityvex = new EntityVex(EntityWitch.this.world);
                entityvex.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);
                entityvex.onInitialSpawn(EntityWitch.this.world.getDifficultyForLocation(blockpos), (IEntityLivingData) null);
                entityvex.setOwner(EntityWitch.this);
                entityvex.setBoundOrigin(blockpos);
                entityvex.setLimitedLife(20 * (30 + EntityWitch.this.rand.nextInt(90)));
                EntityWitch.this.world.spawnEntity(entityvex);
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOCATION_ILLAGER_PREPARE_SUMMON;
        }

        protected SpellType getSpellType() {
            return SpellType.SUMMON_VEX;
        }
    }

    public class AIWololoSpell extends AIUseSpell {
        final Predicate<EntitySheep> wololoSelector = new Predicate<EntitySheep>() {
            public boolean apply(EntitySheep p_apply_1_) {
                return p_apply_1_.getFleeceColor() == EnumDyeColor.BLUE;
            }
        };

        public AIWololoSpell() {
            super();
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if (EntityWitch.this.getAttackTarget() != null) {
                return false;
            } else if (EntityWitch.this.isSpellcasting()) {
                return false;
            } else if (EntityWitch.this.ticksExisted < this.spellCooldown) {
                return false;
            } else if (!EntityWitch.this.world.getGameRules().getBoolean("mobGriefing")) {
                return false;
            } else {
                List<EntitySheep> list = EntityWitch.this.world.<EntitySheep>getEntitiesWithinAABB(EntitySheep.class, EntityWitch.this.getEntityBoundingBox().grow(16.0D, 4.0D, 16.0D), this.wololoSelector);

                if (list.isEmpty()) {
                    return false;
                } else {
                    EntityWitch.this.setWololoTarget(list.get(EntityWitch.this.rand.nextInt(list.size())));
                    return true;
                }
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return EntityWitch.this.getWololoTarget() != null && this.spellWarmup > 0;
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            super.resetTask();
            EntityWitch.this.setWololoTarget((EntitySheep) null);
        }

        protected void castSpell() {
            EntitySheep entitysheep = EntityWitch.this.getWololoTarget();

            if (entitysheep != null && entitysheep.isEntityAlive()) {
                entitysheep.setFleeceColor(EnumDyeColor.RED);
            }
        }

        protected int getCastWarmupTime() {
            return 40;
        }

        protected int getCastingTime() {
            return 60;
        }

        protected int getCastingInterval() {
            return 140;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOCATION_ILLAGER_PREPARE_WOLOLO;
        }

        protected SpellType getSpellType() {
            return SpellType.WOLOLO;
        }
    }
}