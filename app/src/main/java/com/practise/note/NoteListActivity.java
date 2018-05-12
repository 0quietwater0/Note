package com.practise.note;

import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.practise.note.adapter.NoteListAdapter;
import com.practise.note.db.Note;

import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private List<Note> allNote=new ArrayList<>();
    private static final int REQUESTCODE = 1;
    NoteListAdapter noteAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        Toolbar mToolbar =  findViewById(R.id.toolbar);
        mToolbar.setTitle(" ");
        setSupportActionBar(mToolbar);
        //初始化数据
        allNote= DataSupport.findAll(Note.class);
        noteAdapter=new NoteListAdapter(
                NoteListActivity.this,R.layout.note_list_item,allNote);
        listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(noteAdapter);

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

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position 点击的Item位置，从0开始算
                Intent intent=new Intent(NoteListActivity.this,NoteShow.class);
                Note note_item=allNote.get(position);
                intent.putExtra("data",note_item);//传递给下一个Activity的值
                startActivity(intent);//启动Activity
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            Note note=(Note)data.getSerializableExtra("data");
            //noteList.add(note);
            noteAdapter.add(note);
            noteAdapter.notifyDataSetChanged();
            listView.setAdapter(noteAdapter);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }





}
