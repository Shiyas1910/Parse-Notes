package com.example.mohamedshiyas.parsenoteapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mohamedshiyas on 08/08/17.
 */
public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    private List<Notes> notes;
    private Context contextt;

    public NotesRecyclerAdapter(List<Notes> notesList, Context context1) {
        this.notes = notesList;
        this.contextt = context1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleTextView;
        TextView descriptionTextView;
        private Notes note;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.note_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.note_description);
            itemView.setOnClickListener(this);
        }

        public void bind(Notes notes) {
            this.note = notes;
            titleTextView.setText(note.Title());
            descriptionTextView.setText(note.Description());
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            context.startActivity(NoteActivity.newInstance(context, note));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void updateList(List<Notes> notes) {
        if (notes.size() != this.notes.size() || !this.notes.containsAll(notes)) {
            this.notes = notes;
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
    }

    public Notes getItem(int position) {
        return notes.get(position);
    }
}