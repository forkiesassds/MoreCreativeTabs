package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.impl.FabricCreativeTabUtils;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public class FabricCreativeInventoryScreenMixin {

    @Inject(method = "init", at = @At("HEAD"))
    private void validateTabs(CallbackInfo ci) {
        FabricCreativeTabUtils.validateTabs(CustomCreativeTabRegistry.INSTANCE.displayedTabs());
    }

}
