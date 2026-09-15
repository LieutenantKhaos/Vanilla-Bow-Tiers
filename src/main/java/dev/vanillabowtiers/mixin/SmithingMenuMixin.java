package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.ModItems;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin {
    private static boolean vanillaBowTiers$isBatchArrowUpgrade(ItemStack template, ItemStack base, ItemStack addition) {
        if (!template.isEmpty()) return false;
        return (base.is(Items.ARROW) && addition.is(Items.IRON_INGOT))
                || (base.is(ModItems.IRON_ARROW) && addition.is(Items.DIAMOND))
                || (base.is(ModItems.DIAMOND_ARROW) && addition.is(Items.NETHERITE_INGOT))
                || (base.is(ModItems.NETHERITE_ARROW)
                    && BuiltInRegistries.ITEM.getKey(addition.getItem()).equals(
                        Identifier.fromNamespaceAndPath("enderitemod", "enderite_ingot")));
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    private void vanillaBowTiers$requireEightArrows(CallbackInfo ci) {
        SmithingMenu menu = (SmithingMenu)(Object)this;
        ItemStack template = menu.getSlot(SmithingMenu.TEMPLATE_SLOT).getItem();
        ItemStack base = menu.getSlot(SmithingMenu.BASE_SLOT).getItem();
        ItemStack addition = menu.getSlot(SmithingMenu.ADDITIONAL_SLOT).getItem();
        if (vanillaBowTiers$isBatchArrowUpgrade(template, base, addition) && base.getCount() < 8)
            menu.getSlot(SmithingMenu.RESULT_SLOT).set(ItemStack.EMPTY);
    }

    @Inject(method = "shrinkStackInSlot", at = @At("HEAD"), cancellable = true)
    private void vanillaBowTiers$consumeEightArrows(int index, CallbackInfo ci) {
        if (index != SmithingMenu.BASE_SLOT) return;
        SmithingMenu menu = (SmithingMenu)(Object)this;
        ItemStack template = menu.getSlot(SmithingMenu.TEMPLATE_SLOT).getItem();
        ItemStack base = menu.getSlot(SmithingMenu.BASE_SLOT).getItem();
        ItemStack addition = menu.getSlot(SmithingMenu.ADDITIONAL_SLOT).getItem();
        if (base.getCount() >= 8 && vanillaBowTiers$isBatchArrowUpgrade(template, base, addition)) {
            base.shrink(8);
            ci.cancel();
        }
    }
}
