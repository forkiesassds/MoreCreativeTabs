package me.hypherionmc.morecreativetabs.neoforge;

import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author HypherionSA
 */
@Mod(ModConstants.MOD_ID)
public class MoreCreativeTabs {

    private static boolean hasRun = false;

    public MoreCreativeTabs() {
       // CustomCreativeTabRegistry.INSTANCE.setForge(true);
    }

    public static void reloadResources() {
        if (!hasRun) {
            CustomCreativeTabRegistry.INSTANCE.setVanillaTabs(new ArrayList<>(BuiltInRegistries.CREATIVE_MODE_TAB.stream().toList()));
            reloadTabs();
            hasRun = true;
        } else {
            reloadTabs();
        }
    }

    /**
     * Called to reload all creative tabs
     */
    private static void reloadTabs() {
        ModConstants.logger.info("Checking for custom creative tabs");
        CustomCreativeTabRegistry.INSTANCE.clearTabs();

        if (FMLLoader.getDist() == Dist.CLIENT) {
            ResourceManager manager = Minecraft.getInstance().getResourceManager();
            Map<ResourceLocation, Resource> customTabs = manager.listResources("morecreativetabs",
                    path -> path.getPath().endsWith(".json") && !path.getPath().contains("disabled_tabs")
                            && !path.getPath().contains("ordered_tabs") && !path.getPath().contains("tab_config"));

            Map<ResourceLocation, Resource> tabConfig = manager.listResources("morecreativetabs", path -> path.getPath().contains("tab_config.json"));

            if (!tabConfig.isEmpty()) {
                CustomCreativeTabRegistry.INSTANCE.loadTabConfig(tabConfig);
            }

            CustomCreativeTabRegistry.INSTANCE.processEntries(customTabs);
        };
    }
}
