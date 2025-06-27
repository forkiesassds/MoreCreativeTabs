package me.hypherionmc.morecreativetabs.neoforge.mixin;

import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.client.gui.CreativeTabsScreenPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeTabsScreenPage.class)
public class CreativeTabsScreenPageMixin {

    @Inject(method = "isTop", at = @At("RETURN"), cancellable = true)
    private void injectIsTop(CreativeModeTab tab, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(tab.row() == CreativeModeTab.Row.TOP);
    }

    @Inject(method = "getColumn", at = @At("RETURN"), cancellable = true)
    private void injectGetColumn(CreativeModeTab tab, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(tab.column());
    }

}
