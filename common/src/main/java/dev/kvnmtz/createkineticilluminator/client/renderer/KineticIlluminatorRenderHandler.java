package dev.kvnmtz.createkineticilluminator.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler;
import dev.kvnmtz.createkineticilluminator.network.KineticIlluminatorShootPacket;
import dev.kvnmtz.createkineticilluminator.registry.ModItems;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class KineticIlluminatorRenderHandler extends ShootableGadgetRenderHandler {

    private static final int SCROLL_ANIMATION_DURATION_TICKS = 10;
    private float scrollAnimationProgress = 0.0f;
    private float scrollDirection = 0.0f;
    private int scrollStartTick = 0;

    @Override
    public void tick() {
        super.tick();

        updateScrollAnimation();

        handleBeams();
    }

    @Override
    protected void playSound(InteractionHand hand, Vec3 position) {
        // no-op, playing the sound is conditional so there is another method for it which needs to be called
        // manually after calling shoot
    }

    public void playSound(Vec3 position, KineticIlluminatorShootPacket.ShootType shootType) {
        var client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }

        var soundPos = BlockPos.containing(position);

        AllSoundEvents.WORLDSHAPER_PLACE.play(client.level, client.player, soundPos, 0.1F, 0.1F);

        if (shootType == KineticIlluminatorShootPacket.ShootType.PLACE_BLOCK) {
            client.level.playSound(client.player, soundPos, SoundEvents.BEACON_ACTIVATE,
                    SoundSource.PLAYERS, 0.3f, 1.5f);
        } else {
            client.level.playSound(client.player, soundPos, SoundEvents.BEACON_DEACTIVATE,
                    SoundSource.PLAYERS, 0.3f, 1.5f);
        }
    }

    @Override
    protected boolean appliesTo(ItemStack stack) {
        return stack.is(ModItems.KINETIC_ILLUMINATOR.get());
    }

    @Override
    protected void transformTool(PoseStack ms, float flip, float equipProgress, float recoil, float pt) {
        ms.translate(flip * -0.1f, 0.1f, -0.4f);
        ms.mulPose(Axis.YP.rotationDegrees(flip * 5.0f));
    }

    @Override
    protected void transformHand(PoseStack ms, float flip, float equipProgress, float recoil, float pt) {
    }

    public void triggerScrollAnimation(float scrollAmount) {
        scrollDirection = scrollAmount > 0 ? 1.0f : -1.0f;
        scrollStartTick = AnimationTickHolder.getTicks();
        scrollAnimationProgress = 1.0f;
    }

    private void updateScrollAnimation() {
        if (scrollAnimationProgress <= 0) {
            return;
        }

        var currentTick = AnimationTickHolder.getTicks();
        var elapsed = currentTick - scrollStartTick;

        if (elapsed >= SCROLL_ANIMATION_DURATION_TICKS) {
            scrollAnimationProgress = 0.0f;
        } else {
            var t = elapsed / SCROLL_ANIMATION_DURATION_TICKS;
            scrollAnimationProgress = (float) Math.pow(2, -10 * t);
        }
    }

    public float getScrollAnimationMultiplier(float partialTicks) {
        if (scrollAnimationProgress <= 0) {
            return 0.0f;
        }

        var currentTick = AnimationTickHolder.getTicks() + partialTicks;
        var elapsed = currentTick - scrollStartTick;

        if (elapsed >= SCROLL_ANIMATION_DURATION_TICKS) {
            return 0.0f;
        }

        var t = elapsed / SCROLL_ANIMATION_DURATION_TICKS;
        var interpolatedProgress = (float) Math.pow(2, -10 * t);

        return interpolatedProgress * scrollDirection;
    }

    public List<PhotonBeam> cachedBeams;

    private void handleBeams() {
        if (cachedBeams == null) {
            cachedBeams = new LinkedList<>();
        }

        cachedBeams.removeIf(beam -> beam.thickness < 0.1f);
        if (cachedBeams.isEmpty()) {
            return;
        }

        cachedBeams.forEach(beam ->
                Outliner.getInstance()
                        .endChasingLine(beam, beam.startPos, beam.endPos, 1 - beam.thickness, false)
                        .disableLineNormals()
                        .colored(0xffff00)
                        .lineWidth(beam.thickness * 1 / 8f)
        );

        cachedBeams.forEach(beam -> beam.thickness *= 0.6f);
    }

    public void addBeam(PhotonBeam beam) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        var random = new Random();

        if (beam.shootType == KineticIlluminatorShootPacket.ShootType.PLACE_BLOCK) {
            var centerX = beam.endPos.x;
            var centerY = beam.endPos.y;
            var centerZ = beam.endPos.z;
            var radius = 1.25f;

            for (var i = 0; i < 10; i++) {
                var theta = random.nextDouble() * 2 * Math.PI;
                var phi = Math.acos(2 * random.nextDouble() - 1);

                var offsetX = radius * Math.sin(phi) * Math.cos(theta);
                var offsetY = radius * Math.sin(phi) * Math.sin(theta);
                var offsetZ = radius * Math.cos(phi);

                var particleX = centerX + offsetX;
                var particleY = centerY + offsetY;
                var particleZ = centerZ + offsetZ;

                var speed = 0.1f + random.nextFloat() * 0.05f;
                var velocityX = -offsetX / radius * speed;
                var velocityY = -offsetY / radius * speed;
                var velocityZ = -offsetZ / radius * speed;

                level.addParticle(ParticleTypes.END_ROD, particleX, particleY, particleZ,
                        velocityX, velocityY, velocityZ);
            }
        } else {
            var x = beam.endPos.x;
            var y = beam.endPos.y;
            var z = beam.endPos.z;

            Supplier<Double> randomSpeed = () -> (random.nextDouble() - 0.5) * 0.2f;
            for (var i = 0; i < 10; i++) {
                level.addParticle(ParticleTypes.END_ROD, x, y, z, randomSpeed.get(), randomSpeed.get(),
                        randomSpeed.get());
            }
        }

        cachedBeams.add(beam);
    }

    public static class PhotonBeam {
        public float thickness;
        public final Vec3 startPos;
        public final Vec3 endPos;
        public final KineticIlluminatorShootPacket.ShootType shootType;

        public PhotonBeam(Vec3 startPos, Vec3 endPos, KineticIlluminatorShootPacket.ShootType shootType) {
            this.thickness = 1.0f;
            this.startPos = startPos;
            this.endPos = endPos;
            this.shootType = shootType;
        }
    }
}
