package com.gjermundbjaanes.apps.roommates.helpers.adapteritems;

public class SectionItem implements Item {

    private final String title;

    public SectionItem(String title) {
        this.title = title;
    }

    public boolean isSection() {
        return true;
    }

    public String getTitle() {
        return title;
    }
}