package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.ModItems;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowItem.class)
public abstract class BowItemMixin {
    @Inject(method = "use", at = @At("HEAD"))
    private void vanillaBowTiers$rememberSelectedAmmo(
            Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack bow = player.getItemInHand(hand);
        ItemStack ammo = player.getProjectile(bow);

        String tier = "vanilla";
        if (ammo.is(ModItems.IRON_ARROW)) tier = "iron";
        else if (ammo.is(ModItems.GOLDEN_ARROW)) tier = "golden";
        else if (ammo.is(ModItems.DIAMOND_ARROW)) tier = "diamond";
        else if (ammo.is(ModItems.NETHERITE_ARROW)) tier = "netherite";
        else if (ammo.is(ModItems.ENDERITE_ARROW)) tier = "enderite";

        bow.set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(List.of(), List.of(), List.of(tier), List.of())
        );
    }
}
