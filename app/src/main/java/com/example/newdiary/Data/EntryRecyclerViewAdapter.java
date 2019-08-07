package com.example.newdiary.Data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newdiary.Activities.EntryDetailActivity;
import com.example.newdiary.Models.Entry;
import com.example.newdiary.R;

import java.util.ArrayList;

public class EntryRecyclerViewAdapter extends RecyclerView.Adapter<EntryRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Entry> entryList;

    public EntryRecyclerViewAdapter(Context context, ArrayList<Entry> listEntries) {
        this.context = context;
        entryList = listEntries;
    }

    @NonNull
    @Override
    public EntryRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.entry_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryRecyclerViewAdapter.ViewHolder viewHolder, int position) {

        Entry entry = entryList.get(position);

        if ((entry.getDate() != null) && (entry.getTitle() != null) && (entry.getText() != null)) {

            String entryTitle = entry.getTitle();
            viewHolder.title.setText(entryTitle);

            String entryDate = entry.getDate();
            viewHolder.date.setText(entryDate);

            String entryText = entry.getText();
            viewHolder.text.setText(entryText);

        }

    }

    @Override
    public int getItemCount() {
        if (entryList != null) {
            return entryList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView date;
        TextView text;

        public ViewHolder(@NonNull View itemView, final Context ctx) {
            super(itemView);
            context = ctx;

            // connecting views
            title = itemView.findViewById(R.id.entryTitleId);
            date = itemView.findViewById(R.id.entryRowDateId);
            text = itemView.findViewById(R.id.entryTextId);

            // Go to detailed entry page when an entry is clicked on
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Entry clickedEntry = entryList.get(getAdapterPosition());

                    Intent intent = new Intent(context, EntryDetailActivity.class);
                    intent.putExtra("clickedEntry", clickedEntry);
                    ctx.startActivity(intent);

                }
            });
        }

        @Override
        public void onClick(View v) {


        }
    }
}
