package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.VanillaBowTiers;
import dev.vanillabowtiers.access.ProjectileTierAccess;
import dev.vanillabowtiers.access.ProjectileTierRenderStateAccess;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TippableArrowRenderer.class)
public abstract class TippableArrowRendererMixin {
    private static Identifier vanillaBowTiers$texture(String name) {
        return Identifier.fromNamespaceAndPath(
                VanillaBowTiers.MOD_ID, "textures/entity/projectiles/" + name + ".png");
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void vanillaBowTiers$copyTier(
            Arrow arrow, TippableArrowRenderState state, float partialTick, CallbackInfo ci) {
        int tier = arrow instanceof ProjectileTierAccess access
                ? access.vanillaBowTiers$getProjectileTier() : 0;
        ((ProjectileTierRenderStateAccess)(Object)state).vanillaBowTiers$setProjectileTier(tier);
    }

    @Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true)
    private void vanillaBowTiers$useTierTexture(
            TippableArrowRenderState state, CallbackInfoReturnable<Identifier> cir) {
        int tier = ((ProjectileTierRenderStateAccess)(Object)state).vanillaBowTiers$getProjectileTier();
        Identifier texture = switch (tier) {
            case 1 -> vanillaBowTiers$texture("iron_arrow");
            case 2 -> vanillaBowTiers$texture("golden_arrow");
            case 3 -> vanillaBowTiers$texture("diamond_arrow");
            case 4 -> vanillaBowTiers$texture("netherite_arrow");
            case 5 -> vanillaBowTiers$texture("enderite_arrow");
            default -> null;
        };
        if (texture != null) cir.setReturnValue(texture);
    }
}
