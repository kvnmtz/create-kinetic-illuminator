package dev.kvnmtz.createkineticilluminator.registry.forge;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.AssetLookup;
import dev.kvnmtz.createkineticilluminator.item.KineticIlluminatorItem;
import net.minecraft.world.item.Rarity;

import static dev.kvnmtz.createkineticilluminator.CreateKineticIlluminator.REGISTRATE;
import static dev.kvnmtz.createkineticilluminator.registry.ModItems.KINETIC_ILLUMINATOR;

@SuppressWarnings("unused")
public class ModItemsImpl {

    public static void platformAgnosticInit() {
        //noinspection DataFlowIssue
        KINETIC_ILLUMINATOR = REGISTRATE
                .item("kinetic_illuminator", KineticIlluminatorItem::create)
                .tab(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())
                .properties(p -> p.stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON))
                .lang("Kinetic Illuminator")
                .model(AssetLookup.itemModelWithPartials())
                .register();
    }
}
