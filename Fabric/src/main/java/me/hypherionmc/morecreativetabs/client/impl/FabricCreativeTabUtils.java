package me.hypherionmc.morecreativetabs.client.impl;

import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupAccessor;
import net.minecraft.world.item.CreativeModeTab;

import java.util.HashMap;
import java.util.List;

public class FabricCreativeTabUtils {

    public static void validateTabs(List<CreativeModeTab> tabs) {
        int TABS_PER_PAGE = 10;
        int count = 0;

        for (CreativeModeTab tab : tabs) {
            final FabricItemGroup fabricItemGroup = (FabricItemGroup) tab;

            if (FabricCreativeGuiComponents.COMMON_GROUPS.contains(tab)) {
                fabricItemGroup.setPage(0);
                continue;
            }

            final ItemGroupAccessor itemGroupAccessor = (ItemGroupAccessor) tab;
            fabricItemGroup.setPage((count / TABS_PER_PAGE));
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
            final FabricItemGroup fabricItemGroup = (FabricItemGroup) tab;
            final String displayName = tab.getDisplayName().getString();
            final var position = new ItemGroupPosition(tab.row(), tab.column(), fabricItemGroup.getPage());
            final String existingName = map.put(position, displayName);

            if (existingName != null) {
                throw new IllegalArgumentException("Duplicate position: (%s) for item groups %s vs %s".formatted(position, displayName, existingName));
            }
        }
    }

}
