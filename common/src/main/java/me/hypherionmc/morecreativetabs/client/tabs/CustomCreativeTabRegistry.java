package me.hypherionmc.morecreativetabs.client.tabs;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.data.CustomCreativeTabJsonHelper;
import me.hypherionmc.morecreativetabs.client.data.TabConfigJsonHelper;
import me.hypherionmc.morecreativetabs.mixin.accessor.CreativeModeTabAccessor;
import me.hypherionmc.morecreativetabs.mixin.accessor.CreativeModeTabsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static me.hypherionmc.morecreativetabs.utils.CreativeTabUtils.*;

public class CustomCreativeTabRegistry {

    public static final CustomCreativeTabRegistry INSTANCE = new CustomCreativeTabRegistry();
    private final Gson GSON = new Gson();

    private final List<CreativeModeTab> vanillaTabs = new ArrayList<>();
    public final LinkedHashSet<CreativeModeTab> customTabs = new LinkedHashSet<>();
    private final Set<String> disabledTabs = new HashSet<>();
    private final LinkedHashSet<String> tabOrder = new LinkedHashSet<>();
    public final LinkedList<CreativeModeTab> currentTabs = new LinkedList<>();
    public final HashMap<String, Pair<CustomCreativeTabJsonHelper, List<ItemStack>>> replacedTabs = new HashMap<>();

    public final HashMap<CreativeModeTab, List<ItemStack>> tabItems = new HashMap<>();
    public final Set<Item> hiddenItems = new HashSet<>();

    public boolean showTabNames = false;
    public boolean wasReloaded = false;

    private final CreativeModeTab OP_TAB = BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabsAccessor.getOpBlockTab());

    public void processEntries(/*HolderLookup.Provider lookupProvider, */Map<ResourceLocation, Resource> entries) {
        for (Map.Entry<ResourceLocation, Resource> entry : entries.entrySet()) {
            ResourceLocation location = entry.getKey();
            Resource resource = entry.getValue();

            ModConstants.logger.info("Processing {}", location.toString());

            try (InputStream stream = resource.open()) {
                DataResult<CustomCreativeTabJsonHelper> result = CustomCreativeTabJsonHelper.CODEC.decode(/*lookupProvider.createSerializationContext(*/JsonOps.INSTANCE/*)*/,
                        JsonParser.parseReader(new InputStreamReader(stream))).map(com.mojang.datafixers.util.Pair::getFirst);
                CustomCreativeTabJsonHelper json = result.result().orElseThrow();
                ArrayList<ItemStack> stacks = new ArrayList<>();

                if (!json.tabEnabled())
                    continue;

                for (CustomCreativeTabJsonHelper.TabItem item : json.tabItems()) {
                    ItemStack stack = item.itemStack();
                    if (stack.isEmpty())
                        continue;

                    if (item.hideOldTab())
                        hiddenItems.add(stack.getItem());

                    stacks.add(stack);
                }

                if (json.replace()) {
                    replacedTabs.put(fileToTab(location.getPath()).toLowerCase(), Pair.of(json, stacks));
                } else {
                    CreativeModeTab.Builder builder = new CreativeModeTab.Builder(null, -1);
                    builder.title(Component.translatable(prefix(json.tabName())));
                    builder.icon(makeTabIcon(json));

                    if (json.tabBackground() != null)
                        builder.backgroundTexture(json.tabBackground());

                    CreativeModeTab tab = builder.build();
                    customTabs.add(tab);
                    tabItems.put(tab, stacks);
                }
            } catch (Exception e) {
                ModConstants.logger.error("Failed to process creative tab", e);
            }
        }

        reorderTabs();
    }

    public void loadTabConfig(Map<ResourceLocation, Resource> entries) {
        entries.forEach((location, resource) -> {
            ModConstants.logger.info("Processing {}", location.toString());
            try (InputStream stream = resource.open()) {
                DataResult<TabConfigJsonHelper> result = TabConfigJsonHelper.CODEC.decode(JsonOps.INSTANCE,
                        JsonParser.parseReader(new InputStreamReader(stream))).map(com.mojang.datafixers.util.Pair::getFirst);
                TabConfigJsonHelper json = result.result().orElseThrow();
                disabledTabs.addAll(json.disabledTabs());
                tabOrder.addAll(json.tabOrder());
            } catch (Exception e) {
                ModConstants.logger.error("Failed to process tab config for {}", location, e);
            }
        });
    }

    private void reorderTabs() {
        List<CreativeModeTab> oldTabs = new ArrayList<>();
        oldTabs.addAll(vanillaTabs);
        oldTabs.addAll(customTabs);

        LinkedHashSet<CreativeModeTab> filteredTabs = new LinkedHashSet<>();
        boolean addExisting = false;

        if (!tabOrder.isEmpty()) {
            for (String orderedTab : tabOrder) {
                if (!orderedTab.equalsIgnoreCase("existing")) {
                    oldTabs.stream()
                            .filter(tab -> getTabKey(((CreativeModeTabAccessor)tab).getInternalDisplayName()).equals(orderedTab))
                            .findFirst().ifPresent(pTab -> processTab(pTab, filteredTabs));
                } else {
                    addExisting = true;
                }
            }
        } else {
            addExisting = true;
        }


        if (addExisting) {
            for (CreativeModeTab tab : oldTabs) {
                processTab(tab, filteredTabs);
            }
        }

        // Don't disable the Survival Inventory, Search and Hotbar
        filteredTabs.add(BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabsAccessor.getSearchTab()));
        filteredTabs.add(BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabsAccessor.getHotbarTab()));
        filteredTabs.add(BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabsAccessor.getInventoryTab()));

        // Don't disable Custom Tabs
        filteredTabs.addAll(customTabs);

        CreativeModeTabAccessor searchTab = (CreativeModeTabAccessor)BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabsAccessor.getSearchTab());
        searchTab.setDisplayItemsGenerator((itemDisplayParameters, output) -> {
            Set<ItemStack> stacks = ItemStackLinkedSet.createTypeAndComponentsSet();

            for (CreativeModeTab tab : currentTabs) {
                if (tab.getType() == CreativeModeTab.Type.SEARCH)
                    continue;

                stacks.addAll(tab.getSearchTabDisplayItems());
            }

            output.acceptAll(stacks);
        });

        currentTabs.clear();
        currentTabs.addAll(filteredTabs.stream().toList());

        CreativeModeTabs.validate();
    }

    // Just used to remove duplicate code
    private void processTab(CreativeModeTab tab, LinkedHashSet<CreativeModeTab> filteredTabs) {
        if (!disabledTabs.contains(getTabKey(((CreativeModeTabAccessor)tab).getInternalDisplayName()))) {
            filteredTabs.add(tab);
        }
    }

    /**
     * Clear all cached data for reloading
     */
    public void clearTabs() {
        wasReloaded = true;

        customTabs.clear();
        hiddenItems.clear();
        disabledTabs.clear();
        tabItems.clear();
        tabOrder.clear();
        currentTabs.clear();
        replacedTabs.clear();
    }

    public List<CreativeModeTab> sortedTabs() {
        return this.currentTabs;
    }

    public List<CreativeModeTab> displayedTabs() {
        return this.currentTabs.stream().filter(t -> {
            if (t == OP_TAB && !Minecraft.getInstance().options.operatorItemsTab().get())
                return false;

            return t.shouldDisplay();
        }).toList();
    }

    public void setVanillaTabs(List<CreativeModeTab> tabs) {
        this.vanillaTabs.clear();
        this.vanillaTabs.addAll(tabs);
    }

}
