package dev.kvnmtz.createkineticilluminator;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.kvnmtz.createkineticilluminator.client.CreateKineticIlluminatorClient;
import dev.kvnmtz.createkineticilluminator.registry.ModItems;
import dev.kvnmtz.createkineticilluminator.registry.ModPackets;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.NotImplementedException;

public final class CreateKineticIlluminator {

    public static final String MOD_ID = "create_kinetic_illuminator";

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.YELLOW)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    public static void init() {
        ModItems.init();
        ModPackets.init();

        registerRegistrate();

        if (Platform.getEnvironment() == Env.CLIENT) {
            CreateKineticIlluminatorClient.init();
        }
    }

    @ExpectPlatform
    private static void registerRegistrate() {
        throw new NotImplementedException();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
