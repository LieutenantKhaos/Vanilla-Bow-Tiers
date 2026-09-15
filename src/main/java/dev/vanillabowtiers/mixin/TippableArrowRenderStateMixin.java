package dev.vanillabowtiers.mixin;

import dev.vanillabowtiers.access.ProjectileTierRenderStateAccess;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TippableArrowRenderState.class)
public abstract class TippableArrowRenderStateMixin implements ProjectileTierRenderStateAccess {
    @Unique private int vanillaBowTiers$projectileTier;

    @Override
    public void vanillaBowTiers$setProjectileTier(int tier) {
        this.vanillaBowTiers$projectileTier = tier;
    }

    @Override
    public int vanillaBowTiers$getProjectileTier() {
        return this.vanillaBowTiers$projectileTier;
    }
}
