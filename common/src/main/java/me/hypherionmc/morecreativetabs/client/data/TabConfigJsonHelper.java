package me.hypherionmc.morecreativetabs.client.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record TabConfigJsonHelper(List<String> tabOrder, List<String> disabledTabs) {
    private static final List<String> EMPTY_LIST = List.of();
    public static final Codec<TabConfigJsonHelper> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.list(Codec.STRING).optionalFieldOf("tab_order", EMPTY_LIST).forGetter(TabConfigJsonHelper::tabOrder),
            Codec.list(Codec.STRING).optionalFieldOf("disabled_tabs", EMPTY_LIST).forGetter(TabConfigJsonHelper::disabledTabs)
    ).apply(i, TabConfigJsonHelper::new));
}
