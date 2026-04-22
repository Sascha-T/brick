package de.saschat.brick.item;

import de.saschat.brick.Brick;
import de.saschat.brick.entity.ThrownBrick;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

/**
 * Inspiration taken from {@link net.minecraft.world.item.SnowballItem}
 */
public class BrickItem extends ProjectileWeaponItem {
    public static final float MAX_DRAW_TIME = 20.0f;
    public static final float FORCE_MODIFIER = 1.3f;

    public BrickItem(Properties properties) {
        super(properties);

    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (item) -> item.is(Items.BRICK);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 5;
    }

    /**
     * Inspired by {@link net.minecraft.world.item.BowItem}
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        boolean bl = !player.getProjectile(itemStack).isEmpty();
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild && !bl) {
            return InteractionResultHolder.fail(itemStack);
        } else {
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.consume(itemStack);
        }
    }


    /***
     * Adjusted for modifiable draw time
     */
    public static float getPowerForTime(int i) {
        float f = (float)i / MAX_DRAW_TIME;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.SPEAR;
    }
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
        if (livingEntity instanceof Player player) {
            boolean bl = player.getAbilities().instabuild;
            ItemStack itemStack2 = player.getProjectile(itemStack);
            if (!itemStack2.isEmpty() || bl) {
                if (itemStack2.isEmpty()) {
                    itemStack2 = new ItemStack(Items.BRICK);
                }

                int j = this.getUseDuration(itemStack) - i;
                float f = getPowerForTime(j);
                if (!((double)f < 0.1)) {
                    boolean bl2 = bl && itemStack2.is(Items.BRICK);
                    if (!level.isClientSide) {
                        ThrownBrick brick = new ThrownBrick(level, player);
                        brick.setItem(itemStack2);
                        brick.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * FORCE_MODIFIER, 1.0F);

                        if (bl2 || player.getAbilities().instabuild) {
                            brick.setNoPickup();
                        }

                        level.addFreshEntity(brick);
                    }

                    if (!bl2 && !player.getAbilities().instabuild) {
                        itemStack2.shrink(1);
                        if (itemStack2.isEmpty()) {
                            player.getInventory().removeItem(itemStack2);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

}
