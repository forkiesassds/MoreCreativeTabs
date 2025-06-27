package me.hypherionmc.morecreativetabs.client.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record CustomCreativeTabJsonHelper(boolean tabEnabled, String tabName, ItemStack tabIcon, ResourceLocation tabBackground, boolean replace, List<TabItem> tabItems) {
    public static final Codec<CustomCreativeTabJsonHelper> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.fieldOf("tab_enabled").forGetter(CustomCreativeTabJsonHelper::tabEnabled),
            Codec.STRING.fieldOf("tab_name").forGetter(CustomCreativeTabJsonHelper::tabName),
            ItemStack.SINGLE_ITEM_CODEC.fieldOf("tab_stack").forGetter(CustomCreativeTabJsonHelper::tabIcon),
            ResourceLocation.CODEC.optionalFieldOf("tab_background", null).forGetter(CustomCreativeTabJsonHelper::tabBackground),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(CustomCreativeTabJsonHelper::replace),
            Codec.list(TabItem.CODEC).fieldOf("tab_items").forGetter(CustomCreativeTabJsonHelper::tabItems)
    ).apply(i, CustomCreativeTabJsonHelper::new));

    public record TabItem(ItemStack itemStack, boolean hideOldTab, boolean existing) {
        public static final Codec<TabItem> CODEC = RecordCodecBuilder.create(i -> i.group(
                ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(TabItem::itemStack),
                Codec.BOOL.optionalFieldOf("hide_old_tab", false).forGetter(TabItem::hideOldTab),
                Codec.BOOL.optionalFieldOf("existing", false).forGetter(TabItem::existing)
        ).apply(i, TabItem::new));
    }
}
