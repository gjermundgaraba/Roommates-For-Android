package com.gjermundbjaanes.apps.roommates2.helpers.adapteritems;

public class EntryItem implements Item {

    private final String title;
    private final String subtitle;

    EntryItem(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public boolean isSection() {
        return false;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

}
