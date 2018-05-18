package com.practise.note;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.practise.note.db.Note;
import com.practise.note.view.FuncEditView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoteEdit extends AppCompatActivity {
    //EditText note_content;
    private FuncEditView mFuncView;
    Button btn_saveNote;
    //ImageView insert_photo;
    //ImageView take_photo;
    Note note;
    private static final int RESULTCODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        mFuncView = (FuncEditView) findViewById(R.id.func_view);
        btn_saveNote = findViewById(R.id.btn_saveNote);
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("data");
        String noteContent = note.getNoteContent();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(note.getNoteName());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mFuncView.setContent(noteContent);
        btn_saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteName = note.getNoteName();
                String noteContent =mFuncView.getContent();
                //将数据封装到intent中
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ");// HH:mm:ss
                //获取当前时间
                Date date = new Date(System.currentTimeMillis());
                String temp_modifyTime = simpleDateFormat.format(date);
                note.setNoteContent(noteContent);
                note.setModifyTime(temp_modifyTime);
                note.updateAll("notename=?",noteName);
                Intent intent = new Intent();
                intent.putExtra("data", note);
                //添加返回值
                setResult(RESULTCODE, intent);
                //销毁当前的activity
                finish();

            }
        });
    }

    //toolbar返回
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
