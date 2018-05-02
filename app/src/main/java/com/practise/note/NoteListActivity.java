package com.practise.note;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.practise.note.adapter.NoteListAdapter;
import com.practise.note.db.Note;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private List<Note> allNote=new ArrayList<>();
    private static final int REQUESTCODE = 1;
    NoteListAdapter noteAdapter;
    ListView listView;
    Note note1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        Toolbar mToolbar =  findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        //初始化数据
        initNote();

        //新建笔记
        Button btn_newFile=(Button)findViewById(R.id.btn_newFile);
        btn_newFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 给btn_settings添加点击响应事件
                Intent intent =new Intent(NoteListActivity.this,NoteNew.class);
                //启动
                startActivityForResult(intent,REQUESTCODE);
            }

        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Note note=(Note)data.getSerializableExtra("data");
        //noteList.add(note);
        noteAdapter.add(note);
        noteAdapter.notifyDataSetChanged();
        listView.setAdapter(noteAdapter);
    }
    //初始化数据
    public void initNote(){

        //DataSupport.deleteAll(Note.class);
        //静态添加数据
//        for(int i=0;i<1;i++)
//        {
//            Note note =new Note();
//            note.setNoteName("android");
//            note.setIsDelete(0);
//            note.setNoteContent("today is Wed");
//            note.setCreateTime("2018.05.02");
//            note.save();
//        }
        allNote= DataSupport.findAll(Note.class);
        noteAdapter=new NoteListAdapter(
                NoteListActivity.this,R.layout.note_list_item,allNote);
        listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(noteAdapter);

    }

}
