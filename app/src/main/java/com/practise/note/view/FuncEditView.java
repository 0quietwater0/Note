package com.practise.note.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.practise.note.R;
import com.practise.note.util.DialogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static android.text.Html.fromHtml;
import static android.text.Html.toHtml;

/**
 * Author: Pepper
 * Date :  2018/5/16
 * Summary:${ToDo}.
 */

public class FuncEditView extends LinearLayout implements View.OnClickListener {
    private Integer color[] = {R.color.tp_black, R.color.tp_red, R.color.tp_orange,
            R.color.tp_green, R.color.tp_blues, R.color.tp_purple};
    private Integer color_[] = {0xFF000000, 0xFFed2e2e, 0xFFed9a2e,
            0xFF42d153, 0xFF2da4e8, 0xFFd02de8};
    public byte IMG_CODE = 127;//相册
    public byte CAPTURE_CODE = 126;//打开相机拍照
    private int currentColorPosition = 0;
    private Activity mContext;
    private EditText funcEdit;
    private ImageView mBold, mLean, mImage, mLight, mBoldITALIC, mUnderLine;
    private GridView mGridView;
    private int startPosition;
    private int startCount;
    private Editable content;
    private boolean isBold = false; //是否加粗
    private boolean isClickBold = false;

    private boolean isLean = false; //是否倾斜
    private boolean isClickLean = false;

    private boolean isUnderLine = false; //是否下划线
    private boolean isClickUnderLine = false;

    private boolean isLight = false;  //是否Light选择字色
    // private boolean isClickLight = false;

    private boolean isBoldITALIC = false; //是否粗体加斜体
    private boolean isClickBoldITALIC = false;
    private List<Integer> imgPosition = new ArrayList<>();  //插入图片位置
    private List<String> imgUrl = new ArrayList<>(); //插入图片路径

    public FuncEditView(Context context) {
        super(context, null);
        initView((Activity) context);
    }

    public FuncEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        initView((Activity) context);
    }

    public FuncEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView((Activity) context);
    }

    private void initView(Activity context) {
        mContext = context;
        inflate(context, R.layout.func_editview, this);
        funcEdit = (EditText) findViewById(R.id.func_edit);
        mBold = (ImageView) findViewById(R.id.btn_bold);
        mLean = (ImageView) findViewById(R.id.btn_lean);
        mImage = (ImageView) findViewById(R.id.btn_image);
        mLight = (ImageView) findViewById(R.id.btn_light);
        mBoldITALIC = (ImageView) findViewById(R.id.btn_font);
        mUnderLine = (ImageView) findViewById(R.id.btn_underline);

        forEditText();
        setOnClick();
    }

    private void forEditText() {
        funcEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = s.toString().substring(start, start + count);
                if (s1.startsWith("<img src")) {  //输入的是图片
                    imgPosition.add(start);
                    String img_url = s1.substring(10, s1.length() - 4);
                    imgUrl.add(img_url);
                } else {
                    if (count > 1) {
                        for (int i = start; i < start + count; i++) {
                            onTextChanged(s, i, before, 1);
                        }
                    }
                }
                startPosition = start;
                startCount = count;
                /*if (before > 0) { //在删除时把所有的字体设置为零
                    isBold = false;
                    mBold.setTextColor(getResources().getColor(R.color.tp_black));
                    isClickBold = false;
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isBold) {  //加粗
                    for (int i = startPosition; i < startPosition + startCount; i++) {
                        s.setSpan(new StyleSpan(Typeface.BOLD), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                }
                if (isLean) {  //倾斜
                    for (int i = startPosition; i < startPosition + startCount; i++) {
                        s.setSpan(new StyleSpan(Typeface.ITALIC), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                if (isLight) { //字体颜色
                    for (int i = startPosition; i < startPosition + startCount; i++) {
                        s.setSpan(new ForegroundColorSpan(color_[currentColorPosition]), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }

                if (isBoldITALIC) { //粗体加斜体
                    for (int i = startPosition; i < startPosition + startCount; i++) {
                        s.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                if (isUnderLine) { //下划线
                    for (int i = startPosition; i < startPosition + startCount; i++) {
                        s.setSpan(new UnderlineSpan(), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                content = s;
            }
        });
    }

    private void setOnClick() {
        mBold.setOnClickListener(this);
        mLean.setOnClickListener(this);
        mLight.setOnClickListener(this);
        mImage.setOnClickListener(this);
        mBoldITALIC.setOnClickListener(this);
        mUnderLine.setOnClickListener(this);

    }

    public String getContent() {
        String text = toHtml(content);
        StringBuffer sb = new StringBuffer(text);
        int start = 0;
        for (int i = 0; i < imgPosition.size(); i++) {
            if (i == 0) {
                start = imgPosition.get(0);
            } else {
                start = imgPosition.get(i);
            }
            int a = sb.indexOf("null", start);
            sb.replace(a, a + 4, imgUrl.get(i));
        }
        String contentHtml = sb.toString();
        return contentHtml;
    }

    public void setContent(String s) {
        funcEdit.setText(Html.fromHtml(s, imageGetter, null));
    }

    private Html.ImageGetter imageGetter = new Html.ImageGetter() {
        //获取屏幕信息

        @Override
        public Drawable getDrawable(String source) {
            Uri tempPath = Uri.parse(source);
            Drawable d = null;
            try {
                d = Drawable.createFromStream(mContext.getContentResolver().openInputStream(tempPath), null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int width = d.getIntrinsicWidth() * 3;
            int height = d.getIntrinsicHeight() * 3;
            int screenWidth = 1440;
            float scanleWidth = 0, scanleHeight = 0;
            if (width > height) {
                //横屏的图片
                if (width > screenWidth / 2) {
                    scanleWidth = (float) (((float) screenWidth / (float) width) - 0.01);
                    scanleHeight = scanleWidth;
                } else {
                    scanleWidth = (float) screenWidth / (float) 2 / (float) width;
                    scanleHeight = scanleWidth;
                }
            }
            if (width <= height) {//刚开始的时候是使用的int类型的来除，后来发现不精确，所以在这里全都转化成了float
                //竖屏的图片
                if (width >= screenWidth / 2) {
                    scanleWidth = (float) (((float) screenWidth / (float) width) - 0.01);
                    scanleHeight = scanleWidth;
                } else {
                    scanleWidth = (float) screenWidth / (float) 2 / (float) width;
                    scanleHeight = scanleWidth;
                }
            }
            //这一行设置了显示时，图片的大小
            d.setBounds(0, 0, (int) (width * scanleWidth), (int) (height * scanleHeight));
            return d;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bold:
                if (isClickBold) {
                    mBold.setImageResource(R.mipmap.bold);
                    isBold = false;
                } else {  //加粗
                    mBold.setImageResource(R.mipmap.bold_);
                    isBold = true;
                }
                isClickBold = !isClickBold;
                break;
            case R.id.btn_lean:
                if (isClickLean) {
                    mLean.setImageResource(R.mipmap.lean);
                    isLean = false;
                } else {  //倾斜
                    mLean.setImageResource(R.mipmap.lean_);
                    isLean = true;
                }
                isClickLean = !isClickLean;
                break;

            case R.id.btn_image:
                repayState();
                chooseImage();
                break;
            case R.id.btn_light:
                DialogUtil.setGravity(Gravity.BOTTOM);
                View view = DialogUtil.show(mContext, R.layout.edit_color);
                mGridView = (GridView) view.findViewById(R.id.gv_color);
                mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
                mGridView.setAdapter(new ColorAdapter());
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        isLight = true;
                        currentColorPosition = position;
                        Log.v("currentColorPosition", "currentColorPosition" + currentColorPosition);

                        setLightColor();
                        DialogUtil.dismisss();
                    }
                });
                break;
            case R.id.btn_font:
                isBoldITALIC = true;
                if (isClickBoldITALIC){
                    mBoldITALIC.setImageResource(R.mipmap. font);
                    isBoldITALIC=false;
                }else{
                    mBoldITALIC.setImageResource(R.mipmap. font_);
                    isBoldITALIC=true;
                }
                isClickBoldITALIC=!isClickBoldITALIC;
                    break;
            case R.id.btn_underline:
                if (isClickUnderLine) {
                    mUnderLine.setImageResource(R.mipmap.underline);
                    isUnderLine = false;
                } else {  //下划线
                    mUnderLine.setImageResource(R.mipmap.underline_);
                    isUnderLine = true;
                }
                isClickUnderLine = !isClickUnderLine;
                break;
        }
    }

    /**
     * 当点击图片时还原
     */
    private void repayState() {
        isBold = false; //加粗
        isClickBold = false;
        mBold.setImageResource(R.mipmap.bold);

        isLean = false; //倾斜
        isClickLean = false;
        mLean.setImageResource(R.mipmap.lean);

        isUnderLine = false; //下划线
        isClickUnderLine = false;
        mUnderLine.setImageResource(R.mipmap.underline);

        isLight = false;  //高亮
        mLight.setImageResource(R.mipmap.light);

        isBoldITALIC = false; //粗斜体
        mBoldITALIC.setImageResource(R.mipmap.font);
    }

    private void chooseImage() {
        CharSequence item[] = {"手机相册", "相机拍摄"};
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("选择图片").setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:  //手机相册
                        searchImg(IMG_CODE);
                        break;
                    case 1:  //相机拍摄
                        searchImg(CAPTURE_CODE);
                        break;
                }
            }
        }).create();
        dialog.show();
    }

    private void searchImg(byte whichWay) {
        if (whichWay == IMG_CODE) {
            Intent getImage = new Intent();
                /* 开启Pictures画面Type设定为image */
            getImage.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
            getImage.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
            mContext.startActivityForResult(getImage, IMG_CODE);
        } else {
            Intent takephoto = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            mContext.startActivityForResult(takephoto, CAPTURE_CODE);
        }
    }

    public void handleResult(String path) {
        Bitmap bitmap = null;
        Log.e("IMG___", path);
        Bitmap bitmapUrl = BitmapFactory.decodeFile(path);
        if (bitmapUrl != null) {
            bitmap = resizeBitMapImage1(path);
            //bitmap = bitmapUrl;
            ImageSpan imageSpan = new ImageSpan(mContext, bitmap);
            String tempUrl = "<img src=\"" + Uri.fromFile(new File(path)) + "\" />";  //拼接结果：<img src="" />
            Log.e("TAG", tempUrl);
            SpannableString spannableString = new SpannableString(tempUrl);
            spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            int index = funcEdit.getSelectionStart(); //获取光标所在位置
            Editable edit_text = funcEdit.getEditableText();
            Log.e("TAG", "index:" + index + "editable:" + edit_text.toString() + "elength" + edit_text.length());
            if (index < 0 || index >= edit_text.length()) {
                edit_text.append(spannableString);
                edit_text.insert(index + spannableString.length(), "\n");
            } else {
                edit_text.insert(index, spannableString);
                edit_text.insert(index + spannableString.length(), "\n");
                funcEdit.setSelection(funcEdit.length());
            }
            toFocus();

        } else {
            Toast.makeText(mContext, "获取图片失败", Toast.LENGTH_SHORT).show();
        }

    }

    public static Bitmap resizeBitMapImage1(String filePath) {
        Bitmap bitMapImage = null;
        // First, get the dimensions of the image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //double sampleSize = 0;
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        Log.e("Pwidth", "originalWidth" + originalWidth);
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        float hh = 800f;//这里设置高度为800f
        float ww = 800f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        float be = 1.0f;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }

        options.inSampleSize = (int) be;//设置缩放比例
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[128];
        while (true) {
            try {
                options.inSampleSize = (int) be;
                if (be == 1) {
                    Bitmap bitMap = BitmapFactory.decodeFile(filePath, options);
                    bitMapImage = reSize(bitMap, 480f, 800f);
                }
                break;
            } catch (Exception ex) {
                try {
                    be = be * 2;
                } catch (Exception ex1) {
                }
            }
        }
        return bitMapImage;
    }

    //调整图片大小
    public static Bitmap reSize(Bitmap bitmaporg, float dw, float dh) {
        int widthold = bitmaporg.getWidth();
        int heightold = bitmaporg.getHeight();
        float scaleWidth = ((float) dw) / widthold;
        // float scaleHeight = ((float) dh) / heightold;
        Log.e("scaleWidth", "be" + scaleWidth);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap bitmapnew = Bitmap.createBitmap(bitmaporg, 0, 0, widthold, heightold, matrix, true);
        return bitmapnew;
    }

    public void toFocus() {
        funcEdit.clearFocus();
        funcEdit.setFocusable(true);
        funcEdit.setFocusableInTouchMode(true);
        funcEdit.requestFocus();
    }

    public boolean isEmpty() {
        if (TextUtils.isEmpty(funcEdit.getText().toString().trim())) {
            return true;
        }
        return false;
    }

    private class ColorAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return color.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.gv_item_color, parent, false);
            ImageView gvItem = (ImageView) view.findViewById(R.id.iv_item_color);
            gvItem.setImageResource(color[position]);
            return view;
        }
    }

    private void setLightColor() {
        //                if (isClickLight) {
        //                    mLight.setImageResource(R.mipmap.light);
        //                    isLight = false;
        //                } else {  //高亮
        //                    mLight.setImageResource(R.mipmap.light_);
        //                    isLight = true;
        //                }
        //        isClickLight = !isClickLight;
        //        if (isLongClick) {
        //            isLongClick = false;
        //            content.setSpan(new ForegroundColorSpan(color_[currentColorPosition]), funcEdit.getSelectionStart(), funcEdit.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //            funcEdit.setSelection(funcEdit.getSelectionEnd());
        //        }
    }


}

