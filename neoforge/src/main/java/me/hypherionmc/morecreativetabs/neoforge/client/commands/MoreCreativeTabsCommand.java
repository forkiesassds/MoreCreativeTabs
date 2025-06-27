package me.hypherionmc.morecreativetabs.neoforge.client.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import me.hypherionmc.morecreativetabs.neoforge.MoreCreativeTabs;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

/**
 * @author HypherionSA
 * Register Client Side Commands
 */
@EventBusSubscriber(modid = ModConstants.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class MoreCreativeTabsCommand {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("mct").then(Commands.literal("showTabNames")
                        .then(Commands.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                            CustomCreativeTabRegistry.INSTANCE.showTabNames = enabled;
                            context.getSource().sendSuccess(() -> enabled ? Component.literal("Showing tab registry names") : Component.literal("Showing tab names"), true);
                            return 1;
                        }))).then(Commands.literal("reloadTabs").executes(context -> {
                    MoreCreativeTabs.reloadResources();
                    context.getSource().sendSuccess(() -> Component.literal("Reloaded Custom Tabs"), true);
                    return 1;
                }))
        );
    }

}
