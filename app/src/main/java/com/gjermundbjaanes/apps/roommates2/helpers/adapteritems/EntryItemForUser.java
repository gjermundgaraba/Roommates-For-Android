package com.gjermundbjaanes.apps.roommates2.helpers.adapteritems;

import com.gjermundbjaanes.apps.roommates2.parsesubclasses.User;

public class EntryItemForUser extends EntryItem {
    private final User user;

    public EntryItemForUser(String title, String subtitle, User element) {
        super(title, subtitle);
        this.user = element;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
