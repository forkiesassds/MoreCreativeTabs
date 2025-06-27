package me.hypherionmc.morecreativetabs.neoforge.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CreativeModeTabRegistry.class)
public abstract class CreativeModeTabRegistryMixin {

    @Shadow
    public static List<CreativeModeTab> getDefaultTabs() {
        return null;
    }

    @Inject(method = "getSortedCreativeModeTabs", at = @At("RETURN"), cancellable = true)
    private static void injectCustomTabs(CallbackInfoReturnable<List<CreativeModeTab>> cir) {
        cir.setReturnValue(CustomCreativeTabRegistry.INSTANCE.sortedTabs().stream().filter(t -> !getDefaultTabs().contains(t)).toList());
    }

}
