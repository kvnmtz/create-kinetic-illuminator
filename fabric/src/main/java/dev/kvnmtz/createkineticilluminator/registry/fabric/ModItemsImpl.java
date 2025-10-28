package dev.kvnmtz.createkineticilluminator.registry.fabric;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import dev.kvnmtz.createkineticilluminator.client.renderer.KineticIlluminatorItemRenderer;
import dev.kvnmtz.createkineticilluminator.item.fabric.KineticIlluminatorItemFabric;
import net.minecraft.world.item.Rarity;

import static dev.kvnmtz.createkineticilluminator.CreateKineticIlluminator.REGISTRATE;
import static dev.kvnmtz.createkineticilluminator.registry.ModItems.KINETIC_ILLUMINATOR;

@SuppressWarnings("unused")
public class ModItemsImpl {

    public static void platformAgnosticInit() {
        KINETIC_ILLUMINATOR = REGISTRATE
                .item("kinetic_illuminator", KineticIlluminatorItemFabric::create)
                .tab(AllCreativeModeTabs.BASE_CREATIVE_TAB.key())
                .properties(p -> p.stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON))
                .transform(CreateRegistrate.customRenderedItem(() -> () -> KineticIlluminatorItemRenderer.INSTANCE))
                .lang("Kinetic Illuminator")
                .model(AssetLookup.itemModelWithPartials())
                .register();
    }
}
