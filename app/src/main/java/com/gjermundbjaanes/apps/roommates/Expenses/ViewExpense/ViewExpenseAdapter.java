package com.gjermundbjaanes.apps.roommates.expenses.viewexpense;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjermundbjaanes.apps.roommates.R;
import com.gjermundbjaanes.apps.roommates.helpers.Constants;
import com.gjermundbjaanes.apps.roommates.helpers.adapteritems.EntryItemForUser;
import com.gjermundbjaanes.apps.roommates.helpers.adapteritems.Item;
import com.gjermundbjaanes.apps.roommates.helpers.adapteritems.SectionItem;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.Expense;
import com.gjermundbjaanes.apps.roommates.parsesubclasses.User;

import java.text.DecimalFormat;
import java.util.ArrayList;

class ViewExpenseAdapter extends BaseAdapter {
    public Expense expense;
    private final Context context;
    private final ArrayList<Item> items = new ArrayList<Item>();

    ViewExpenseAdapter(Context context, Expense expense) {
        this.context = context;
        this.expense = expense;
        loadElements();
    }


    public void loadElements() {
        if (expense != null) {
            items.clear();

            ArrayList<User> notPaidUp = expense.getNotPaidUp();
            ArrayList<User> paidUp = expense.getPaidUp();


            int numUsers = notPaidUp.size() + paidUp.size();
            DecimalFormat df = new DecimalFormat(".00");
            String amountOwed = df.format(expense.getTotalAmount().doubleValue() / numUsers);
            items.add(new SectionItem(context.getString(R.string.not_paid_up)));

            for (User user : notPaidUp) {
                items.add(new EntryItemForUser(user.getDisplayName(), context.getString(R.string.owes) + " " + amountOwed, user));
            }

            items.add(new SectionItem(context.getString(R.string.paid_up)));

            for (User user : paidUp) {
                items.add(new EntryItemForUser(user.getDisplayName(), context.getString(R.string.has_paid_up), user));
            }


            notifyDataSetChanged();
        }

    }


    public void toggleElement(User user) {
        ArrayList<User> notPaid = expense.getNotPaidUp();
        ArrayList<User> paid = expense.getPaidUp();


        if (notPaid.contains(user)) {
            notPaid.remove(user);
            paid.add(user);

            expense.setNotPaidUp(notPaid);
            expense.setPaidUp(paid);
        } else {
            paid.remove(user);
            notPaid.add(user);

            expense.setNotPaidUp(notPaid);
            expense.setPaidUp(paid);
        }
        loadElements();
        expense.saveEventually();

        if (notPaid.size() == 0) {
            Intent intent = new Intent(Constants.EXPENSE_NEED_TO_REFRESH);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        Item item = items.get(i);
        if (!item.isSection()) {
            EntryItemForUser entryItem = (EntryItemForUser) item;

            return entryItem.getUser();
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final Item item = items.get(position);

        if (item != null) {
            if (item.isSection()) {
                SectionItem sectionItem = (SectionItem) item;
                view = View.inflate(context, R.layout.list_element_section, null);
                view.setBackgroundColor(Color.LTGRAY);
                view.setOnClickListener(null);
                view.setOnLongClickListener(null);
                view.setLongClickable(false);
                TextView title = (TextView) view.findViewById(R.id.sectionTitleTextView);

                title.setText(sectionItem.getTitle());
            } else {
                EntryItemForUser entryItem = (EntryItemForUser) item;
                view = View.inflate(context, R.layout.list_task_element_layout, null);
                TextView title = (TextView) view.findViewById(R.id.textViewList);
                TextView subTitle = (TextView) view.findViewById(R.id.textViewCreatedBy);

                title.setText(entryItem.getTitle());
                subTitle.setText(entryItem.getSubtitle());
            }
        }

        return view;
    }

}