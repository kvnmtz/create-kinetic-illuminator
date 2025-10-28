package dev.kvnmtz.createkineticilluminator.mixin;

import dev.kvnmtz.createkineticilluminator.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Shadow
    private Minecraft minecraft;

    @Inject(
            method = "getMarkerParticleTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getMarkerParticleTarget(CallbackInfoReturnable<Block> cir) {
        //noinspection DataFlowIssue
        var itemstack = this.minecraft.player.getMainHandItem();
        var item = itemstack.getItem();

        if (item == ModItems.KINETIC_ILLUMINATOR.get()) {
            cir.setReturnValue(Blocks.LIGHT);
        }
    }
}