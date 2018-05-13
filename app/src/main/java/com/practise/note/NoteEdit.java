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
    EditText note_content;
    Button btn_saveNote;
    ImageView insert_photo;
    ImageView take_photo;
    Note note;
    private static final int RESULTCODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("data");
        String noteContent = note.getNoteContent();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(note.getNoteName());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        note_content = findViewById(R.id.note_content);
        btn_saveNote = findViewById(R.id.btn_saveNote);
        insert_photo = (ImageView) findViewById(R.id.insert_photo);
        take_photo = (ImageView) findViewById(R.id.take_photo);

        SpannableString span_str = (SpannableString) analyzeImage(noteContent);
        note_content.setText(span_str);
        insert_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, 0x111);
            }
        });
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0x222);
            }
        });
        btn_saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteName = note.getNoteName();
                String noteContent = note_content.getText().toString();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resolver = getContentResolver();

        if (requestCode == 0x111 && resultCode == RESULT_OK) {
            Uri originalUri = data.getData();
            //Bitmap ori_bitmap = null;
            Bitmap ori_rbitmap = null;
            try {
                ori_rbitmap = BitmapFactory.decodeStream(resolver.openInputStream(originalUri));
                //ori_rbitmap=resizeimg.resizeImage(ori_bitmap, 300, 300);
            } catch (FileNotFoundException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.i("TestFile", "SD card is not avaiable/writeable right now.");
            }
            String name = Calendar.getInstance(Locale.CHINA).getTimeInMillis() + ".jpg";
            File file = new File("/sdcard/myImage/");
            file.mkdirs();// 创建文件夹
            String fileName = "/sdcard/myImage/" + name;
            FileOutputStream FOut = null;
            try {
                FOut = new FileOutputStream(fileName);
                ori_rbitmap.compress(Bitmap.CompressFormat.JPEG, 100, FOut);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    FOut.flush();
                    FOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String myPath = fileName;//接着根据存放的路径获取图片放到note_content中
            Log.w(myPath, "fileName");
            Toast.makeText(this, myPath, Toast.LENGTH_SHORT).show();
            SpannableString span_str = new SpannableString(myPath);
            Bitmap my_rbm = getSmallBitmap(myPath);
            ImageSpan span = new ImageSpan(this, my_rbm);
            span_str.setSpan(span, 0, myPath.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            Editable et = note_content.getText();// 先获取note_content中的内容
            int start = note_content.getSelectionStart();
            et.insert(start, span_str);// 设置ss要添加的位置
            note_content.setText((CharSequence) et);// 把et添加到note_content中
            note_content.setSelection(start + span_str.length());// 设置note_content中光标在最后面显示
        }
        if (requestCode == 0x222 && resultCode == RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.i("TestFile", "SD card is not avaiable/writeable right now.");
            }
            String name = Calendar.getInstance(Locale.CHINA).getTimeInMillis() + ".jpg";//给拍的照片命名，下面进行存储
            Bundle bundle = data.getExtras();
            Bitmap camera_bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
            Bitmap camera_rbitmap = reSize(camera_bitmap, 800, 600);
            FileOutputStream FOut = null;
            File file = new File("/sdcard/myImage/");
            file.mkdirs();// 创建文件夹
            String fileName = "/sdcard/myImage/" + name;
            try {
                FOut = new FileOutputStream(fileName);
                camera_rbitmap.compress(Bitmap.CompressFormat.JPEG, 100, FOut);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    FOut.flush();
                    FOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String myPath = fileName;//下面从文件夹中取出来放到note_content中去
            SpannableString span_str = new SpannableString(myPath);
            Bitmap my_rbm = getSmallBitmap(myPath);
            ImageSpan span = new ImageSpan(this, my_rbm);
            span_str.setSpan(span, 0, myPath.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            Editable et = note_content.getText();// 先获取note_content中的内容
            int start = note_content.getSelectionStart();
            et.insert(start, span_str);// 设置ss要添加的位置
            note_content.setText((CharSequence) et);// 把et添加到note_content中
            note_content.setSelection(start + span_str.length());// 设置note_content中光标在最后面显示
        }

    }

    @Nullable
    private Bitmap getSmallBitmap(String myPath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(myPath, options);
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        int dw = currentDisplay.getWidth();
        int dh = currentDisplay.getHeight();
        options.inSampleSize = calculateInSampleSize(options, dw, dh);
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(myPath, options);
        if (bm == null) {
            return null;
        }
        return bm;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }

        return inSampleSize;
    }

    //正则表达式解析出图片
    public CharSequence analyzeImage(String content) {
        String my_content = content;
        SpannableString span_str = new SpannableString(content);
        Pattern p = Pattern.compile("/sdcard/myImage/[0-9]{13}+.jpg");
        Matcher m = p.matcher(my_content);
        while (m.find()) {
            String mypath = m.group();
            Toast.makeText(this, m.group(), Toast.LENGTH_SHORT);
            Bitmap bitmap = BitmapFactory.decodeFile(mypath);
            Bitmap rbitmap = reSize(bitmap, 800, 600);
            ImageSpan span = new ImageSpan(this, rbitmap);
            span_str.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span_str;
    }

    //调整图片大小
    private Bitmap reSize(Bitmap bitmaporg, int dw, int dh) {
        int widthold = bitmaporg.getWidth();
        int heightold = bitmaporg.getHeight();
        float scaleWidth = ((float) dw) / widthold;
        float scaleHeight = ((float) dh) / heightold;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmapnew = Bitmap.createBitmap(bitmaporg, 0, 0, widthold, heightold, matrix, true);
        return bitmapnew;
    }

    //toolbar返回
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
