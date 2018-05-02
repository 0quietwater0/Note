package com.practise.note.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Pepper
 * Date :  2018/4/30
 * Summary:${实体}.
 */

public class Note extends DataSupport implements Serializable {
    private String noteName;
    private String createTime;
    private int isDelete;
    private String modifyTime;
    private String noteContent;

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }



    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

}
