package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.impl.FabricCreativeTabUtils;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(value = CreativeModeTabs.class, priority = 0)
public abstract class FabricCreativeTabsMixin {

    @Shadow
    public static List<CreativeModeTab> allTabs() {
        return null;
    }

    @Shadow @Nullable private static CreativeModeTab.ItemDisplayParameters CACHED_PARAMETERS;

    @Inject(method = "streamAllTabs", at = @At("RETURN"), cancellable = true)
    private static void injectCustomTabs(CallbackInfoReturnable<Stream<CreativeModeTab>> cir) {
        cir.setReturnValue(CustomCreativeTabRegistry.INSTANCE.sortedTabs().stream());
    }

    @Inject(method = "getDefaultTab", at = @At("RETURN"), cancellable = true)
    private static void injectDefaultTab(CallbackInfoReturnable<CreativeModeTab> cir) {
        cir.setReturnValue(allTabs().get(0));
    }

    @Inject(method = "validate", at = @At("HEAD"), cancellable = true)
    private static void injectValidation(CallbackInfo ci) {
        ci.cancel();
        FabricCreativeTabUtils.validateTabs(CustomCreativeTabRegistry.INSTANCE.sortedTabs());
    }

    // Supplementaries crashes the game with our tabs, since they are not registered (they are fake tabs)
    // Work around to return the registered tabs only
    @Inject(method = "tabs", at = @At("RETURN"), cancellable = true)
    private static void injectTabsCompat(CallbackInfoReturnable<List<CreativeModeTab>> cir) {
        String thread = Thread.currentThread().getStackTrace()[3].getClassName();

        if (!thread.isEmpty() && thread.toLowerCase().contains("supplementaries") && thread.toLowerCase().contains("modcreativetabs")) {
            cir.setReturnValue(BuiltInRegistries.CREATIVE_MODE_TAB.stream().filter(CreativeModeTab::shouldDisplay).toList());
        }
    }

    @Inject(method = "tryRebuildTabContents", at = @At("HEAD"))
    private static void injectReload(FeatureFlagSet arg, boolean bl, HolderLookup.Provider arg2, CallbackInfoReturnable<Boolean> cir) {
        if (CustomCreativeTabRegistry.INSTANCE.isWasReloaded()) {
            CACHED_PARAMETERS = null;
            CustomCreativeTabRegistry.INSTANCE.setWasReloaded(false);
        }
    }

}
