package dev.vanillabowtiers.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    private void vanillaBowTiers$renameVanillaArrow(CallbackInfoReturnable<Component> cir) {
        ItemStack stack = (ItemStack)(Object)this;

        // Preserve names applied with an anvil/commands. Only replace the
        // ordinary vanilla Arrow name.
        if (stack.is(Items.ARROW) && !stack.has(DataComponents.CUSTOM_NAME)) {
            cir.setReturnValue(Component.literal("Flint Arrow"));
        }
    }
}
