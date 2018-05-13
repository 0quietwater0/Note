package com.practise.note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.practise.note.db.Note;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoteShow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_show);
        TextView note_content=(TextView)findViewById(R.id.note_content);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent=getIntent();
        Note note =(Note)intent.getSerializableExtra("data");
        String noteContent=note.getNoteContent();
        mToolbar.setTitle(note.getNoteName());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Button btn_edit=findViewById(R.id.btn_edit);
        SpannableString span_str= (SpannableString)analyzeImage(noteContent);
        note_content.setText(span_str);
    }
    //正则表达式解析出图片
    public CharSequence analyzeImage(String content){
        String my_content=content;
        SpannableString span_str=new SpannableString(content);
        Pattern p=Pattern.compile("/sdcard/myImage/[0-9]{13}+.jpg");
        Matcher m=p.matcher(my_content);
        while(m.find()){
            String mypath=m.group();
            Toast.makeText(this, m.group(), Toast.LENGTH_SHORT);
            Bitmap bitmap= BitmapFactory.decodeFile(mypath);
            Bitmap rbitmap=reSize(bitmap, 800, 600);
            ImageSpan span=new ImageSpan(this,rbitmap);
            span_str.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span_str;
    }
    //调整图片大小
    private Bitmap reSize(Bitmap bitmaporg,int dw,int dh) {
        int widthold = bitmaporg.getWidth();
        int heightold = bitmaporg.getHeight();
        float scaleWidth = ((float) dw) / widthold;
        float scaleHeight = ((float) dh) / heightold;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmapnew = Bitmap.createBitmap(bitmaporg, 0, 0, widthold, heightold, matrix, true);
        return bitmapnew;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
