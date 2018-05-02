package com.practise.note.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.practise.note.R;
import com.practise.note.db.Note;

import java.util.List;

/**
 * Author: Pepper
 * Date :  2018/5/1
 * Summary:${ToDo}.
 */

public class NoteListAdapter extends ArrayAdapter<Note> {
    private int resourceId ;

    public NoteListAdapter(Context context, int textViewResourceId, List<Note> objects) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取当前项的Notes实例
        Note note=getItem(position);
        //加载布局
        //View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        View view;

        if(convertView==null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

        }
        else
            view = convertView;
        //获取TextView实例
        TextView noteName=(TextView)view.findViewById(R.id.note_name);
        //显示文字
        noteName.setText(note.getNoteName());
        TextView createTime=(TextView)view.findViewById(R.id.create_time);
        createTime.setText(note.getCreateTime());
        //返回布局
        return view;
    }
}
