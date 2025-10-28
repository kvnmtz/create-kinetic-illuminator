package dev.kvnmtz.createkineticilluminator.item.client;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.architectury.event.EventResult;
import dev.kvnmtz.createkineticilluminator.client.CreateKineticIlluminatorClient;
import dev.kvnmtz.createkineticilluminator.registry.ModItems;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class KineticIlluminatorHandler {

    private static final Object outlineSlot = new Object();

    public static BlockPos targetPos = null;
    public static int lightLevel = 15;

    public static void tick(Minecraft client) {
        targetPos = null;

        if (isInactive()) {
            return;
        }

        var player = client.player;
        if (player == null) {
            return;
        }

        var isTargetLightBlock = false;

        var trace = rayTraceRange(player.level(), player, 4.5);
        if (trace.getType() == HitResult.Type.BLOCK) {
            var hit = trace.getBlockPos();
            if (player.level().getBlockState(hit).is(Blocks.LIGHT) && !player.isShiftKeyDown()) {
                targetPos = hit;
                isTargetLightBlock = true;
            } else {
                targetPos = trace.getBlockPos().relative(trace.getDirection());
            }
        }

        if (targetPos == null) {
            var pt = AnimationTickHolder.getPartialTicks();
            var targetVec = player.getEyePosition(pt).add(player.getLookAngle().scale(4.5));
            targetPos = BlockPos.containing(targetVec);
        }

        Outliner.getInstance().chaseAABB(outlineSlot, new AABB(targetPos)).colored(isTargetLightBlock ? 0xfb2c36 :
                0xffdf20).lineWidth(1 / 16f);
    }

    public static EventResult onScroll(Minecraft client, double amount) {
        if (isInactive()) {
            return EventResult.pass();
        }

        var player = client.player;
        if (player == null) {
            return EventResult.pass();
        }

        if (!player.isShiftKeyDown()) {
            return EventResult.pass();
        }

        var shouldIgnoreInput = (lightLevel == 1 && amount < 0) || (lightLevel == 15 && amount > 0);
        if (shouldIgnoreInput) {
            return EventResult.interruptFalse();
        }

        lightLevel = Mth.clamp((int) Math.round(lightLevel + amount), 1, 15);
        player.displayClientMessage(Component.literal("§7Light level: §e" + lightLevel), true);
        AllSoundEvents.SCROLL_VALUE.play(client.level, client.player, client.player.blockPosition());

        CreateKineticIlluminatorClient.KINETIC_ILLUMINATOR_RENDER_HANDLER.triggerScrollAnimation((float) amount);

        return EventResult.interruptFalse();
    }

    private static BlockHitResult rayTraceRange(Level level, Player player,
                                                @SuppressWarnings("SameParameterValue") double range) {
        var origin = player.getEyePosition();
        var target = RaycastHelper.getTraceTarget(player, range, origin);
        var context = new ClipContext(origin, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        return level.clip(context);
    }

    private static boolean isInactive() {
        var client = Minecraft.getInstance();

        var player = client.player;
        if (player == null) {
            return true;
        }

        if (client.level == null || client.screen != null) {
            return true;
        }

        if (!player.getMainHandItem().is(ModItems.KINETIC_ILLUMINATOR.get())) {
            return true;
        }

        var pov = client.options.getCameraType();
        return pov == CameraType.THIRD_PERSON_FRONT;
    }
}
