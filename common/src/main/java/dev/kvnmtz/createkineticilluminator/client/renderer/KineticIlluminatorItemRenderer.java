package dev.kvnmtz.createkineticilluminator.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.kvnmtz.createkineticilluminator.CreateKineticIlluminator;
import dev.kvnmtz.createkineticilluminator.client.CreateKineticIlluminatorClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;

public class KineticIlluminatorItemRenderer extends CustomRenderedItemModelRenderer {

    private KineticIlluminatorItemRenderer() {
    }

    private static final KineticIlluminatorItemRenderer INSTANCE = new KineticIlluminatorItemRenderer();

    public static KineticIlluminatorItemRenderer getInstance() {
        return INSTANCE;
    }

    public static final PartialModel CORE = PartialModel.of(CreateKineticIlluminator.asResource("item" +
            "/kinetic_illuminator/core"));
    public static final PartialModel CORE_GLOW = PartialModel.of(CreateKineticIlluminator.asResource("item" +
            "/kinetic_illuminator/core_glow"));
    public static final PartialModel ACCELERATOR = PartialModel.of(CreateKineticIlluminator.asResource("item" +
            "/kinetic_illuminator/accelerator"));

    public static float getAnimationProgress(float pt, boolean leftHanded, boolean mainHand) {
        var animation = CreateKineticIlluminatorClient.KINETIC_ILLUMINATOR_RENDER_HANDLER
                .getAnimation(mainHand ^ leftHanded, pt);
        return Mth.clamp(animation * 5.0f, 0.0f, 1.0f);
    }

    public static float getScrollModifiedAngle(float baseAngle, float partialTicks) {
        var scrollMultiplier =
                CreateKineticIlluminatorClient.KINETIC_ILLUMINATOR_RENDER_HANDLER.getScrollAnimationMultiplier(partialTicks);

        if (Math.abs(scrollMultiplier) > 0.001f) {
            var smoothProgress = (float) Math.sin(Math.abs(scrollMultiplier) * Math.PI * 0.5);
            var scrollEffect = Math.signum(scrollMultiplier) * smoothProgress * 120.0f;
            return baseAngle + scrollEffect;
        }

        return baseAngle;
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light,
                          int overlay) {

        renderPlatformAgnostic(stack, model, renderer, transformType, ms, buffer, light, overlay);
    }

    @SuppressWarnings("unused")
    @ExpectPlatform
    private static void renderPlatformAgnostic(ItemStack stack, CustomRenderedItemModel model,
                                               PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                                               PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        throw new NotImplementedException();
    }
}