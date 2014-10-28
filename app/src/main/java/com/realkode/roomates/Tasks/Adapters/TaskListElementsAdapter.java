package com.realkode.roomates.Tasks.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.realkode.roomates.Helpers.AdapterItems.EntryItemForTaskListElement;
import com.realkode.roomates.Helpers.AdapterItems.Item;
import com.realkode.roomates.Helpers.AdapterItems.SectionItem;
import com.realkode.roomates.ParseSubclassses.TaskList;
import com.realkode.roomates.ParseSubclassses.TaskListElement;
import com.realkode.roomates.R;

import java.util.ArrayList;
import java.util.List;

public class TaskListElementsAdapter extends BaseAdapter {
    private final Context context;
    private final TaskList taskList;
    private final ArrayList<Item> items = new ArrayList<Item>();
    private ArrayList<TaskListElement> elements = new ArrayList<TaskListElement>();

    public TaskListElementsAdapter(Context context, TaskList taskList) {
        this.context = context;
        this.taskList = taskList;
        loadObjects();
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void reloadElements() {
        if (elements != null) {
            items.clear();

            ArrayList<TaskListElement> unfinishedElements = new ArrayList<TaskListElement>(elements);
            ArrayList<TaskListElement> finishedElements = new ArrayList<TaskListElement>();

            for (TaskListElement element : unfinishedElements) {
                if (element.getDone()) {
                    finishedElements.add(element);
                }
            }
            unfinishedElements.removeAll(finishedElements);


            items.add(new SectionItem(context.getString(R.string.tasks_section_item_title_todo)));
            for (TaskListElement element : unfinishedElements) {
                items.add(new EntryItemForTaskListElement(element.getElementName(),
                        context.getString(R.string.tasks_item_created_by) + element.getCreatedBy().getDisplayName(),
                        element));
            }

            items.add(new SectionItem(context.getString(R.string.tasks_section_item_title_finished)));
            for (TaskListElement element : finishedElements) {
                items.add(new EntryItemForTaskListElement(element.getElementName(),
                        context.getString(R.string.tasks_item_created_by) + element.getFinishedBy().getDisplayName(),
                        element));
            }


            notifyDataSetChanged();
        }

    }

    public void loadObjects() {
        ParseQuery<TaskListElement> taskListElementParseQuery = ParseQuery.getQuery(TaskListElement.class);
        taskListElementParseQuery.include("createdBy");
        taskListElementParseQuery.include("finishedBy");
        taskListElementParseQuery.orderByAscending("createdAt");
        taskListElementParseQuery.whereEqualTo("taskList", this.taskList);
        taskListElementParseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);

        taskListElementParseQuery.findInBackground(new FindCallback<TaskListElement>() {
            @Override
            public void done(List<TaskListElement> taskListElements, ParseException e) {
                if (e == null) {
                    elements = new ArrayList<TaskListElement>(taskListElements);
                    reloadElements();
                }
            }
        });


    }

    @Override
    public int getCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        if (items != null) {
            Item item = items.get(i);
            if (!item.isSection()) {
                EntryItemForTaskListElement entryItemForTaskListElement = (EntryItemForTaskListElement) item;
                System.out.println(entryItemForTaskListElement.getTaskListElement().getElementName());
                return entryItemForTaskListElement.getTaskListElement();
            } else {
                return null;
            }
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
                final EntryItemForTaskListElement entryItemForTaskListElement = (EntryItemForTaskListElement) item;
                view = View.inflate(context, R.layout.list_task_element_layout, null);
                TextView title = (TextView) view.findViewById(R.id.textViewList);
                TextView subTitle = (TextView) view.findViewById(R.id.textViewCreatedBy);
                title.setText(entryItemForTaskListElement.getTitle());
                subTitle.setText(entryItemForTaskListElement.getSubtitle());
            }
        }

        return view;
    }
}
