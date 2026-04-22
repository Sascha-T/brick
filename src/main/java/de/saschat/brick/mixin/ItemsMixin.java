package de.saschat.brick.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.saschat.brick.item.BrickItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Items.class)
public class ItemsMixin {

    /** I fucking hate this, ignore the error, stupid IntelliJ is lying to you **/

    @Definition(id = "BRICK", field = "Lnet/minecraft/world/item/Items;BRICK:Lnet/minecraft/world/item/Item;")
    @Definition(id = "registerItem", method = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/item/Item;")
    @Definition(id = "Item", type = Item.class)
    @Definition(id = "Properties", type = Item.Properties.class)
    @Expression("BRICK = registerItem('brick', @( new Item(new Properties()) ) )")
    @WrapOperation(method = "<clinit>", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private static Item createBrick(Item.Properties properties, Operation<Item> original) {
        return new BrickItem(properties);
    }
}
