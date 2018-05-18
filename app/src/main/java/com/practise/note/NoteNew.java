package com.practise.note;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.practise.note.db.Note;
import com.practise.note.util.BitmapUtil;
import com.practise.note.util.UpLoadPicSaveUtil;
import com.practise.note.util.UriUtil;
import com.practise.note.view.FuncEditView;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;

public class NoteNew extends AppCompatActivity {
    EditText note_name;
    Button btn_saveNote;
    //ImageView insert_photo;
    //ImageView take_photo;
    private static final int RESULTCODE = 1;
    private static int screenWidth;
    private static int screenHeigh;
    private FuncEditView mFuncView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_new);
        note_name = findViewById(R.id.note_name);
        mFuncView = (FuncEditView) findViewById(R.id.func_view);
        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeigh = dm.heightPixels;
        btn_saveNote = findViewById(R.id.btn_saveNote);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btn_saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteName = note_name.getText().toString();
                List<Note> noteCheck = DataSupport.where("notename==?", noteName).find(Note.class);
                int name_exsited = noteCheck.size();
                //Log.d("NoteNew","checkNum:"+noteCheck.size());
               // String noteContent = note_content.getHtml();
                if (noteName != null && noteName.length() != 0) {
                    //判断是否存在同名的笔记
                    if (name_exsited == 0) {
                        if (!mFuncView.isEmpty()) {
                            //将数据封装到intent中
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");// HH:mm:ss
                            //获取当前时间
                            Date date = new Date(System.currentTimeMillis());
                            String temp_createTime = simpleDateFormat.format(date);
                            Note note = new Note();
                            note.setNoteName(noteName);
                            note.setNoteContent(mFuncView.getContent());
                            note.setCreateTime(temp_createTime);
                            note.setModifyTime(temp_createTime);
                            note.save();
                            Intent intent = new Intent();
                            intent.putExtra("data", note);
                            //添加返回值
                            setResult(RESULTCODE, intent);
                            //销毁当前的activity
                            finish();
                        }
                        else{
                            Toast.makeText(NoteNew.this, "笔记内容不能为空",
                                    Toast.LENGTH_SHORT).show();
                            mFuncView.toFocus();
                        }

                    } else {
                        Toast.makeText(NoteNew.this, "笔记名称重复，无法保存",
                                Toast.LENGTH_SHORT).show();
                        note_name.setFocusable(true);
                        note_name.setFocusableInTouchMode(true);
                        note_name.requestFocus();
                        // InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.showSoftInput(note_name,0);
                    }

                } else {

                    Toast.makeText(NoteNew.this, "笔记名称为空，无法保存",
                            Toast.LENGTH_SHORT).show();
                    note_name.setFocusable(true);
                    note_name.setFocusableInTouchMode(true);
                    note_name.requestFocus();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String path = "";
            if (requestCode == mFuncView.CAPTURE_CODE) {  //拍照
                path = getCapturePath(data);
                Log.e("IMG", path + "!!!!!");
            } else {                            //相册
                // path = getPathFromResult((byte) requestCode, data);
                path = UriUtil.getAbsoluteFilePath(this, data.getData());
                Log.e("IMG", path + "@@@@");
            }
            mFuncView.handleResult(path);
        }

    }
    protected String getCapturePath(Intent data) {
        Bitmap bitmap = null;
        if (!data.hasExtra("data")) {
            Uri uri = data.getData();
            try {
                bitmap = BitmapUtil.getBitmapFormUri(this, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bundle bundle = data.getExtras();
            Bitmap b = (Bitmap) bundle.get("data");
            bitmap = BitmapUtil.compressImage(b);
        }
        return UpLoadPicSaveUtil.saveFile(this, bitmap);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    //    @Nullable
    //    private Bitmap getSmallBitmap(String myPath) {
    //        final BitmapFactory.Options options = new BitmapFactory.Options();
    //        options.inJustDecodeBounds = true;
    //        BitmapFactory.decodeFile(myPath, options);
    //        Display currentDisplay = getWindowManager().getDefaultDisplay();
    //        int dw = currentDisplay.getWidth();
    //        int dh = currentDisplay.getHeight();
    //        options.inSampleSize = calculateInSampleSize(options, dw, dh);
    //        options.inJustDecodeBounds = false;
    //        Bitmap bm = BitmapFactory.decodeFile(myPath, options);
    //        int dwnew = bm.getWidth();
    //        int dhnew = bm.getHeight();
    //        if (bm == null) {
    //            return null;
    //        }
    //        return bm;
    //    }

    //    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    //        final int height = options.outHeight*3;
    //        final int width = options.outWidth*3;
    //        Log.d("calculateInSampleSize","calculateInSampleSizewidth:"+width);
    //        Log.d("calculateInSampleSize","calculateInSampleSizeheight:"+height);
    //        int inSampleSize = 1;
    //
    //        if (height > reqHeight || width > reqWidth) {
    //
    //            // Calculate ratios of height and width to requested height and
    //            // width
    //            final int heightRatio = Math.round((float) height
    //                    / (float) reqHeight);
    //            final int widthRatio = Math.round((float) width / (float) reqWidth);
    //
    //            // Choose the smallest ratio as inSampleSize value, this will
    //            // guarantee
    //            // a final image with both dimensions larger than or equal to the
    //            // requested height and width.
    //            inSampleSize += heightRatio < widthRatio ? widthRatio : heightRatio;
    //        }
    //        Log.d("calculateInSampleSize","calculateInSampleSizeinSampleSize:"+inSampleSize);
    //        return inSampleSize;
    //    }
}
