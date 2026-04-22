package de.saschat.brick;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import de.saschat.brick.entity.ThrownBrick;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class Brick implements ModInitializer {
    /**
     * Mirrors {@link net.minecraft.world.entity.EntityType#SNOWBALL}
     */
    public static final EntityType<ThrownBrick> BRICK = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation("brick", "brick"),
            EntityType.Builder.of(ThrownBrick.Factory.INSTANCE, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("cube")
    );

    public static final GameRules.Key<GameRules.IntegerValue> BRICK_DAMAGE = GameRuleRegistry.register("brickDamage", GameRules.Category.MISC, GameRuleFactory.createIntRule(5));

    @Override
    public void onInitialize() {
        DispenserBlock.registerBehavior(Items.BRICK, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return (Projectile) Util.make(new ThrownBrick(level, position.x(), position.y(), position.z()), (brick) -> brick.setItem(itemStack));
            }
        });
    }
}
