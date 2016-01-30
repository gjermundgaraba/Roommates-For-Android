package com.gjermundbjaanes.apps.roommates2.helpers.adapteritems;

import com.gjermundbjaanes.apps.roommates2.parsesubclasses.Expense;

public class EntryItemForExpenses extends EntryItem {
    private final Expense expense;

    public EntryItemForExpenses(String title, String subtitle, Expense element) {
        super(title, subtitle);
        this.expense = element;
    }

    public Expense getExpense() {
        return expense;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
