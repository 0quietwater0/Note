package com.practise.note;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.practise.note.db.Note;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private List<Note> allNote = new ArrayList<>();
    private List<Integer> deleteList = new ArrayList<>();//存储将要删除的子项们的位置
    private CheckBox selectAllCheckbox;
    private static final String TAG = "MainActivity";
    private static final int REQUESTCODE = 1;//新建
    private static final int REQUESTCODE_1 = 2;//展示

    private boolean isSelecting = false;//是否正在选择
    boolean selectAll = false;//选择全部
    int choose = -1;//第一次长按的位置，让它被选定
    boolean isClosing = false;//关闭选择的一瞬间
    NoteListAdapter noteAdapter;
    ListView listView;
    private RelativeLayout relativeLayout;//长按选择删除的布局
    Button cancelBt;
    Button deleteBt;
    TextView textView;
    Button btn_search;//搜索的button
    private RelativeLayout searchLayout;//搜索的布局
    EditText searchText;
    Button searchOk;
    Button searchCancel;
    boolean isSearch = false;//搜索button是否被点击

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        //获取布局控件对象
        relativeLayout = (RelativeLayout) findViewById(R.id.navigation_main);
        deleteBt = (Button) findViewById(R.id.delete_bt);
        cancelBt = (Button) findViewById(R.id.cancel_bt);
        selectAllCheckbox = (CheckBox) findViewById(R.id.select_all_checkbox);
        textView = (TextView) findViewById(R.id.selected_num);
        //获取搜索布局控件对象
        searchLayout = (RelativeLayout) findViewById(R.id.navigation_search);
        searchText = (EditText) findViewById(R.id.search_Text);
        searchOk = (Button) findViewById(R.id.search_ok);
        searchCancel = (Button) findViewById(R.id.search_cancel);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        //初始化数据
        allNote = DataSupport.findAll(Note.class);
        noteAdapter = new NoteListAdapter(
                NoteListActivity.this, R.layout.note_list_item, allNote);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(noteAdapter);
        //查询笔记
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearch) {
                    searchLayout.setVisibility(View.GONE);

                } else {//搜索
                    searchLayout.setVisibility(View.VISIBLE);
                    searchText.getText().clear();
                    searchText.setFocusable(true);
                    searchText.setFocusableInTouchMode(true);
                    searchText.requestFocus();
                    noteAdapter = new NoteListAdapter(
                            NoteListActivity.this, R.layout.note_list_item, allNote);
                    listView = (ListView) findViewById(R.id.list_view);
                    listView.setAdapter(noteAdapter);
                }
                isSearch = !isSearch;
            }
        });
        searchOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchName = searchText.getText().toString();
                if (searchName != null && searchName.length() != 0) {
                    List<Note> noteSearch = DataSupport.where("notename==?", searchName).find(Note.class);
                    int name_exsited = noteSearch.size();
                    if (name_exsited == 1) {
                        noteAdapter = new NoteListAdapter(
                                NoteListActivity.this, R.layout.note_list_item, noteSearch);
                        listView = (ListView) findViewById(R.id.list_view);
                        listView.setAdapter(noteAdapter);
                        searchLayout.setVisibility(View.GONE);
                        isSearch = false;
                    } else {
                        Toast.makeText(NoteListActivity.this, "无此笔记",
                                Toast.LENGTH_SHORT).show();
                        searchText.getText().clear();
                        searchText.setFocusable(true);
                        searchText.setFocusableInTouchMode(true);
                        searchText.requestFocus();
                        noteAdapter = new NoteListAdapter(
                                NoteListActivity.this, R.layout.note_list_item, allNote);
                        listView = (ListView) findViewById(R.id.list_view);
                        listView.setAdapter(noteAdapter);
                    }
                } else {
                    Toast.makeText(NoteListActivity.this, "请输入笔记全称",
                            Toast.LENGTH_SHORT).show();
                    searchText.setFocusable(true);
                    searchText.setFocusableInTouchMode(true);
                    searchText.requestFocus();
                }
            }
        });
        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCancel();

            }
        });
        //新建笔记
        Button btn_newFile = (Button) findViewById(R.id.btn_newFile);
        btn_newFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 给btn_settings添加点击响应事件
                Intent intent = new Intent(NoteListActivity.this, NoteNew.class);
                //启动
                startActivityForResult(intent, REQUESTCODE);
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position 点击的Item位置，从0开始算
                if (isSelecting) {
                    CheckBox checkBox = view.findViewById(R.id.item_checkbox);
                    checkBox.setChecked(!checkBox.isChecked());
                    if (checkBox.isChecked() && !deleteList.contains(new Integer(position))) {
                        deleteList.add(new Integer(position));
                    } else if (!checkBox.isChecked() && deleteList.contains(new Integer(position))) {
                        for (int i = 0; i < deleteList.size(); i++) {
                            if (deleteList.get(i).equals(new Integer(position))) {
                                deleteList.remove(i);
                            }
                        }
                    }
                    textView.setText("（已选：" + deleteList.size() + " 项）");

                } else {
                    Intent intent = new Intent(NoteListActivity.this, NoteShow.class);
                    Note note_item = allNote.get(position);
                    intent.putExtra("data", note_item);//传递给下一个Activity的值
                    startActivityForResult(intent, REQUESTCODE_1);//启动NoteShow
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                textView.setText("（已选：" + 1 + " 项）");
                if (isSelecting) {
                    CheckBox checkBox = view.findViewById(R.id.item_checkbox);
                    checkBox.setChecked(!checkBox.isChecked());
                    if (checkBox.isChecked() && !deleteList.contains(new Integer(position))) {
                        deleteList.add(new Integer(position));
                    } else if (!checkBox.isChecked() && deleteList.contains(new Integer(position))) {
                        for (int i = 0; i < deleteList.size(); i++) {
                            if (deleteList.get(i).equals(new Integer(position))) {
                                deleteList.remove(i);
                            }
                        }
                    }

                } else {
                    choose = position;
                    deleteList.add(new Integer(position));
                    isSelecting = true;
                    noteAdapter.notifyDataSetInvalidated();
                    relativeLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        //按钮：取消选择
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        //按钮：删除选择项
        deleteBt.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteListActivity.this);
                builder.setTitle("");
                builder.setMessage("确定删除所选项目？");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int ia) {
                                delete();
                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancel();

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        //checkBox：全选
        selectAllCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked && !selectAll) {
                    deleteList.clear();
                    for (int i = 0; i < allNote.size(); i++) {
                        deleteList.add(new Integer(i));
                    }
                    selectAll = true;
                    textView.setText("（已选：" + deleteList.size() + " 项）");
                    noteAdapter.notifyDataSetInvalidated();
                } else if (!isChecked && selectAll) {
                    deleteList.clear();
                    selectAll = false;
                    textView.setText(" ");
                    noteAdapter.notifyDataSetInvalidated();

                } else {
                    deleteList.clear();
                    selectAll = false;
                    textView.setText(" ");
                    noteAdapter.notifyDataSetInvalidated();
                }
            }
        });
    }

    //点击删除键的处理逻辑
    private void delete() {
        // Collections.reverse(deleteList);
        allNote = DataSupport.findAll(Note.class);
        noteAdapter = new NoteListAdapter(
                NoteListActivity.this, R.layout.note_list_item, allNote);
        Log.d(TAG, "deleteSize删除前: " + deleteList.size());
        for (int i = 0; i < deleteList.size(); i++) {

            Log.d(TAG, "deleteList: " + deleteList.get(i));
            Note delete_note = noteAdapter.getItem(deleteList.get(i));
            Log.d(TAG, "delete_note: " + delete_note.getNoteName());
            //           if(delete_note!=null&&deleteList.size()>0){
            delete_note.delete();
        }

        allNote = DataSupport.findAll(Note.class);
        noteAdapter = new NoteListAdapter(
                NoteListActivity.this, R.layout.note_list_item, allNote);
        listView.setAdapter(noteAdapter);
        cancel();
    }

    private void searchCancel() {
        searchLayout.setVisibility(View.GONE);
        isSearch = false;
        noteAdapter.notifyDataSetInvalidated();
    }

    //点击取消或返回键的处理逻辑
    private void cancel() {
        selectAll = false;
        selectAllCheckbox.setChecked(false);
        relativeLayout.setVisibility(View.GONE);
        deleteList.clear();
        Log.d(TAG, "delete: " + deleteList.size());
        isClosing = true;
        noteAdapter.notifyDataSetInvalidated();
        isSelecting = false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0 && isSelecting) {
                cancel();
                searchCancel();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            try {
                Note note = (Note) data.getSerializableExtra("data");
                //noteList.add(note);
                noteAdapter.add(note);
                noteAdapter.notifyDataSetChanged();
                listView.setAdapter(noteAdapter);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else if (resultCode == 2) {

            allNote = DataSupport.findAll(Note.class);
            noteAdapter = new NoteListAdapter(
                    NoteListActivity.this, R.layout.note_list_item, allNote);
            listView = (ListView) findViewById(R.id.list_view);
            listView.setAdapter(noteAdapter);

        }


    }

    public class NoteListAdapter extends ArrayAdapter<Note> {
        private int resourceId;

        public NoteListAdapter(Context context, int textViewResourceId, List<Note> objects) {
            super(context, textViewResourceId, objects);
            this.resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //获取当前项的Notes实例
            Note note = getItem(position);
            //加载布局
            View view;
            ViewHolder viewHolder;

            if (isClosing || convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                //引入ViewHolder提升ListView的效率
                viewHolder = new ViewHolder();

                viewHolder.checkBox = (CheckBox) view.findViewById(R.id.item_checkbox);
                viewHolder.textView_name = (TextView) view.findViewById(R.id.note_name);
                viewHolder.textView_time = (TextView) view.findViewById(R.id.modify_time);
                view.setTag(viewHolder);
                if (position == allNote.size()) {
                    isClosing = false;//最后一项加载完成后变为false
                }
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();

            }
            viewHolder.textView_name.setText(note.getNoteName());
            viewHolder.textView_time.setText(note.getModifyTime());

            if (isSelecting) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkBox.setVisibility(View.GONE);

            }

            if (selectAll)
                viewHolder.checkBox.setChecked(true);
            if (choose > -1 && position == choose) {
                viewHolder.checkBox.setChecked(true);
                choose = -1;
            }
            return view;
        }
    }

    class ViewHolder {

        CheckBox checkBox;
        TextView textView_name;
        TextView textView_time;
    }


}
