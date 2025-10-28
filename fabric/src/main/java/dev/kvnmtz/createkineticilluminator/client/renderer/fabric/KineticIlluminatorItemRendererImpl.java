package dev.kvnmtz.createkineticilluminator.client.renderer.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.kvnmtz.createkineticilluminator.util.ShaderUtils;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static dev.kvnmtz.createkineticilluminator.client.renderer.KineticIlluminatorItemRenderer.*;

@SuppressWarnings("unused")
public class KineticIlluminatorItemRendererImpl {

    public static void renderPlatformAgnostic(ItemStack stack, CustomRenderedItemModel model,
                                              PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                                              PoseStack ms, MultiBufferSource buffer, int light, int overlay) {

        var pt = AnimationTickHolder.getPartialTicks();
        var worldTime = AnimationTickHolder.getRenderTime() / 20.0f;
        renderer.renderSolid(model.getOriginalModel(), light);

        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        var leftHanded = player.getMainArm() == HumanoidArm.LEFT;
        var mainHand = player.getMainHandItem() == stack;
        var offHand = player.getOffhandItem() == stack;
        var animation = getAnimationProgress(pt, leftHanded, mainHand);
        float multiplier;
        if (!mainHand && !offHand) {
            multiplier = Mth.sin(worldTime * 5.0f);
        } else {
            multiplier = animation;
        }

        var lightItensity = (int) (15.0f * Mth.clamp(multiplier, 0.0f, 1.0f));
        var glowLight = LightTexture.pack(lightItensity, Math.max(lightItensity, 4));

        if (ShaderUtils.areShadersEnabled()) {
            // fallback rendering pathway that does not use custom shaders
            renderer.renderSolid(CORE.get(), glowLight);
            renderer.render(CORE_GLOW.get(), glowLight);
        } else {
            renderer.renderSolidGlowing(CORE.get(), glowLight);
            renderer.renderGlowing(CORE_GLOW.get(), glowLight);
        }

        var angle = worldTime * -25.0f;
        if (mainHand || offHand) {
            angle += 360.0f * animation;
            angle = getScrollModifiedAngle(angle, pt);
        }

        angle %= 360.0f;
        var offset = -0.155f;
        ms.translate(0.0f, offset, 0.0f);
        ms.mulPose(Axis.ZP.rotationDegrees(angle));
        ms.translate(0.0f, -offset, 0.0f);
        renderer.render(ACCELERATOR.get(), light);
    }
}
