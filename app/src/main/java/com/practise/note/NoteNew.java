package com.practise.note;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.practise.note.db.Note;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteNew extends AppCompatActivity {
    EditText note_name;
    EditText note_content;
    Button btn_saveNote;
    private static final int RESULTCODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_new);
        note_name=findViewById(R.id.note_name);
        note_content=findViewById(R.id.note_content);
        btn_saveNote=findViewById(R.id.btn_saveNote);
        btn_saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteName=note_name.getText().toString();
                String noteContent=note_content.getText().toString();
                if(noteName!=null && noteName.length()!=0){
                    //将数据封装到intent中
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd ");// HH:mm:ss
                    //获取当前时间
                    Date date = new Date(System.currentTimeMillis());
                    String temp_createTime=simpleDateFormat.format(date);
                    Note note = new Note();
                    note.setNoteName(noteName);
                    note.setNoteContent(noteContent);
                    note.setCreateTime(temp_createTime);
                    note.setIsDelete(0);
                    note.save();
                    Intent intent = new Intent();
                    intent.putExtra("data",note);
                    //添加返回值
                    setResult(RESULTCODE , intent);

                    //销毁当前的activity
                    finish();
                }
                else
                    Toast.makeText(NoteNew.this,"笔记名称为空，无法保存",
                            Toast.LENGTH_SHORT).show();

            }
        });
    }




}
