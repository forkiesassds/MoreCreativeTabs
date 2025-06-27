package me.hypherionmc.morecreativetabs.utils;

import me.hypherionmc.morecreativetabs.client.data.CustomCreativeTabJsonHelper;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class CreativeTabUtils {

    public static Supplier<ItemStack> makeTabIcon(CustomCreativeTabJsonHelper json) {
        return json::tabIcon;
    }

    public static ItemStack getItemStack(String jsonItem) {
        Optional<Item> itemOptional = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(jsonItem));
        return itemOptional.map(Item::getDefaultInstance).orElse(ItemStack.EMPTY);
    }

    public static String prefix(String tabName) {
        return String.format("%s.%s", "morecreativetabs", tabName);
    }

    public static String getTabKey(Component component) {
        if (component.getContents() instanceof TranslatableContents contents) {
            return contents.getKey();
        }
        return component.getString();
    }

    public static String fileToTab(String input) {
        input = input.replace("morecreativetabs/", "");
        input = input.replace("morecreativetabs", "");
        input = input.replace(".json", "");

        return input;
    }

    public static Optional<Pair<CustomCreativeTabJsonHelper, List<ItemStack>>> replacementTab(String tabName) {
        if (CustomCreativeTabRegistry.INSTANCE.replacedTabs.containsKey(tabName)) {
            return Optional.of(CustomCreativeTabRegistry.INSTANCE.replacedTabs.get(tabName));
        }
        if (CustomCreativeTabRegistry.INSTANCE.replacedTabs.containsKey(tabName.toLowerCase())) {
            return Optional.of(CustomCreativeTabRegistry.INSTANCE.replacedTabs.get(tabName.toLowerCase()));
        }
        return Optional.empty();
    }
}
