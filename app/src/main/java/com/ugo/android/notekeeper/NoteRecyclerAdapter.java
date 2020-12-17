package com.ugo.android.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ugo.android.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.ugo.android.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    //private final List<NoteInfo> mNotes;
    private Cursor mCursor;
    LayoutInflater layoutInflater;
    int coursePosition;
    int noteTitlePosition;
    int idPosition;

    public NoteRecyclerAdapter(Context mContext, Cursor cursor) {
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
        mCursor = cursor;

        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if (mCursor == null) {
            return;
        }

        coursePosition = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        noteTitlePosition = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        idPosition = mCursor.getColumnIndex(NoteInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_note_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String course = mCursor.getString(coursePosition);
        String noteTitle = mCursor.getString(noteTitlePosition);
        int id = mCursor.getInt(idPosition);
        //NoteInfo note = mNotes.get(position);
        holder.textCourse.setText(course);
        holder.textTitle.setText(noteTitle);
        holder.mId = id;
    }

    @Override
    public int getItemCount() {
        return mCursor == null? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView textCourse;
        public final TextView textTitle;
        public int mId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = itemView.findViewById(R.id.text_course);
            textTitle = itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NoteActivity.class);
                    intent.putExtra(NoteActivity.NOTE_ID, mId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
