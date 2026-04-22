package de.saschat.brick.entity;

import de.saschat.brick.Brick;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Inspiration taken fron {@link net.minecraft.world.entity.projectile.Snowball}
 */
public class ThrownBrick extends ThrowableItemProjectile {
    public ThrownBrick(EntityType<? extends ThrownBrick> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownBrick(Level level, LivingEntity livingEntity) {
        super(Brick.BRICK, livingEntity, level);
    }

    public ThrownBrick(Level level, double x, double y, double z) {
        super(Brick.BRICK, x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.BRICK;
    }


    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (level() instanceof ServerLevel level) {
            Entity entity = entityHitResult.getEntity();
            entity.hurt(this.damageSources().thrown(this, this.getOwner()), level.getGameRules().getInt(Brick.BRICK_DAMAGE));
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState blockState = this.level().getBlockState(blockHitResult.getBlockPos());
        if(blockState.is(ConventionalBlockTags.GLASS_BLOCKS) || blockState.is(ConventionalBlockTags.GLASS_PANES)) {
            // yeah we're cooking now
            level().playSound((Player)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 0.5F, 0.4F / (level().getRandom().nextFloat() * 0.4F + 0.8F));
            if(!level().isClientSide) {
                level().destroyBlock(pos, false);
            }
        }

    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
        // drop the item
        if (!this.level().isClientSide) {
            System.out.println("Hit");
            Vec3 location = hitResult.getLocation();
            ItemEntity itemEntity = new ItemEntity(this.level(), location.x, location.y, location.z, new ItemStack(Items.BRICK, 1));
            itemEntity.setPickUpDelay(40);
            level().addFreshEntity(itemEntity);
        }
    }

    /**
     * This is entirely necessary because java.
     */
    public static class Factory implements EntityType.EntityFactory<ThrownBrick> {
        public static Factory INSTANCE = new Factory();

        private Factory() {
        }

        @Override
        public ThrownBrick create(EntityType<ThrownBrick> entityType, Level level) {
            return new ThrownBrick(entityType, level);
        }
    }
}
