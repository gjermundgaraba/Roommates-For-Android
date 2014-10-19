package com.realkode.roomates.Helpers.AdapterItems;

import com.realkode.roomates.ParseSubclassses.Expense;

public class EntryItemForExpenses implements Item{
    private final String title;
    private final String subtitle;
    private final Expense expense;

    public EntryItemForExpenses(String title, String subtitle, Expense element) {
        this.title = title;
        this.subtitle = subtitle;
        this.expense = element;
    }

    public Expense getExpense() {
        return expense;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
