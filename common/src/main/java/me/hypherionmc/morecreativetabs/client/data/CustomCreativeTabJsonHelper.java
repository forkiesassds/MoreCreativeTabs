package me.hypherionmc.morecreativetabs.client.data;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CustomCreativeTabJsonHelper {

    @SerializedName("tab_enabled")
    private boolean tabEnabled;

    @SerializedName("tab_name")
    private String tabName;

    @SerializedName("tab_stack")
    private TabIcon tabIcon;

    @SerializedName("tab_background")
    private String tabBackground;

    private boolean replace;

    @Setter
    private boolean keepExisting;

    @SerializedName("tab_items")
    private ArrayList<TabItem> tabItems;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class TabItem {
        private String name;

        @SerializedName("hide_old_tab")
        private boolean hideOldTab;

        private String nbt;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class TabIcon {
        private String name;
        private String nbt;
    }
}
