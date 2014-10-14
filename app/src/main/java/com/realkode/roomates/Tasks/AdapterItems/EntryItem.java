package com.realkode.roomates.Tasks.AdapterItems;

public class EntryItem implements Item{

    private final String title;
    private final String subtitle;

    public EntryItem(String title, String subtitle) {
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
