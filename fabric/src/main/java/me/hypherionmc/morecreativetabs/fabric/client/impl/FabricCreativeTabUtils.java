package me.hypherionmc.morecreativetabs.fabric.client.impl;

import me.hypherionmc.morecreativetabs.mixin.accessor.CreativeModeTabsAccessor;
import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupImpl;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

import java.util.HashMap;
import java.util.List;

public class FabricCreativeTabUtils {

    public static void validateTabs(List<CreativeModeTab> tabs) {
        int TABS_PER_PAGE = 10;
        int count = 0;

        CreativeModeTab OP_TAB = BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabsAccessor.getOpBlockTab());

        for (CreativeModeTab tab : tabs) {
            final FabricItemGroupImpl fabricItemGroup = (FabricItemGroupImpl) tab;

            if (FabricCreativeGuiComponents.COMMON_GROUPS.contains(tab) || tab == OP_TAB) {
                fabricItemGroup.fabric_setPage(0);
                continue;
            }

            final ItemGroupAccessor itemGroupAccessor = (ItemGroupAccessor) tab;
            fabricItemGroup.fabric_setPage((count / TABS_PER_PAGE));
            int pageIndex = count % TABS_PER_PAGE;
            CreativeModeTab.Row row = pageIndex < (TABS_PER_PAGE / 2) ? CreativeModeTab.Row.TOP : CreativeModeTab.Row.BOTTOM;
            itemGroupAccessor.setRow(row);
            itemGroupAccessor.setColumn(row == CreativeModeTab.Row.TOP ? pageIndex % TABS_PER_PAGE : (pageIndex - TABS_PER_PAGE / 2) % (TABS_PER_PAGE));

            count++;
        }

        // Overlapping group detection logic, with support for pages.
        record ItemGroupPosition(CreativeModeTab.Row row, int column, int page) { }
        var map = new HashMap<ItemGroupPosition, String>();

        for (CreativeModeTab tab : tabs) {
            final FabricItemGroupImpl FabricItemGroupImpl = (FabricItemGroupImpl) tab;
            final String displayName = tab.getDisplayName().getString();
            final var position = new ItemGroupPosition(tab.row(), tab.column(), FabricItemGroupImpl.fabric_getPage());
            final String existingName = map.put(position, displayName);

            if (existingName != null) {
                throw new IllegalArgumentException("Duplicate position: (%s) for item groups %s vs %s".formatted(position, displayName, existingName));
            }
        }
    }

}
