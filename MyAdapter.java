package com.example.notesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

//adapter class for the recycler view
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    //declare variables
    private ArrayList<Note> listOfNotes;
    Context context;
    private NoteClickListener mNoteListener;

    //inner class provides a reference to each item/row
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //text views in row_layout
        public TextView titleview;
        public TextView tagview;
        public TextView dateview;
        public TextView timeview;
        public TextView infoview;

        //constructor to create a view holder/item/row and click listener
        public MyViewHolder(View itemView){
            super(itemView);
            //set a on click listener to the view
            itemView.setOnClickListener(this);
            titleview = (TextView) itemView.findViewById(R.id.titleview);
            tagview = (TextView) itemView.findViewById(R.id.tagview);
            dateview = (TextView) itemView.findViewById(R.id.dateview);
            timeview = (TextView) itemView.findViewById(R.id.timeview);
            infoview = (TextView) itemView.findViewById(R.id.infoview);
        }

        @Override
        public void onClick(View view) {
            mNoteListener.onNoteClick(view, getAdapterPosition());
        }
    }

    //constructor to provide the information to the Adapter
    public MyAdapter(ArrayList<Note> listOfNotes, Context context, NoteClickListener noteClickListener){
        this.listOfNotes = listOfNotes;
        this.context = context;
        this.mNoteListener = noteClickListener;
    }

    //create new views
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.row_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    //get the element at this position and replace the contents of the view with that element
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = listOfNotes.get(position);
        holder.titleview.setText(note.getTitle());
        holder.tagview.setText(note.getTag());
        holder.dateview.setText(note.getDate());
        holder.timeview.setText(note.getTime());
        holder.infoview.setText(note.getInfo());
    }

    //get the size of the list
    @Override
    public int getItemCount() {
        return listOfNotes.size();
    }

    //interface for the click listener - when a user clicks on a note in the recycler view
    public interface NoteClickListener{
        void onNoteClick(View v, int position);
    }
}

