package de.saschat.brick;

import de.saschat.brick.entity.ThrownBrick;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.Util;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class Brick implements ModInitializer {
    public static final String MOD_ID = "brick";

    public static ResourceLocation location(String text) {
        return new ResourceLocation(MOD_ID, text);
    }

    /**
     * Mirrors {@link net.minecraft.world.entity.EntityType#SNOWBALL}
     */
    public static final EntityType<ThrownBrick> BRICK = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            location("brick"),
            EntityType.Builder.of(ThrownBrick.Factory.INSTANCE, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build("cube")
    );

    public static final GameRules.Key<GameRules.IntegerValue> BRICK_DAMAGE = GameRuleRegistry.register("brickDamage", GameRules.Category.MISC, GameRuleFactory.createIntRule(5));
    public static SoundEvent BRICK_SFX = SoundEvent.createVariableRangeEvent(location("whip"));
    public static SoundEvent THUD_SFX = SoundEvent.createVariableRangeEvent(location("thud"));
    public static final ResourceKey<DamageType> STONING_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, location("stoning"));
    @Override
    public void onInitialize() {
        // dispenser
        DispenserBlock.registerBehavior(Items.BRICK, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return (Projectile) Util.make(new ThrownBrick(level, position.x(), position.y(), position.z()), (brick) -> brick.setItem(itemStack));
            }
        });

        // sfx
        Registry.register(BuiltInRegistries.SOUND_EVENT, location("whip"), BRICK_SFX);
        Registry.register(BuiltInRegistries.SOUND_EVENT, location("thud"), THUD_SFX);

    }


}
