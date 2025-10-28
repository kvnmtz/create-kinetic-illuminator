package dev.kvnmtz.createkineticilluminator.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.kvnmtz.createkineticilluminator.item.KineticIlluminatorItem;
import org.apache.commons.lang3.NotImplementedException;

public class ModItems {

    public static ItemEntry<KineticIlluminatorItem> KINETIC_ILLUMINATOR = null;

    public static void init() {
        platformAgnosticInit();
    }

    @ExpectPlatform
    private static void platformAgnosticInit() {
        throw new NotImplementedException();
    }
}