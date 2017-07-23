package com.shtibel.truckies.activities.notifications;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Shtibel on 22/08/2016.
 */
public class Notification implements Serializable{
// MESSAGE_ID INTEGER, TITLE TEXT, CONTENT TEXT, TYPE TEXT, IS_READ INTEGER
    long id;
    long userId;
    long messageId;
    String title;
    String content;
    String type;
    boolean isRead;
    boolean isOnStatusBar;
    Date date;

    public Notification(long id,long userId,long messageId,String title,String content,String type,boolean isRead, boolean isOnStatusBar,Date date){
        this.id=id;
        this.userId=userId;
        this.messageId=messageId;
        this.title=title;
        this.content=content;
        this.type=type;
        this.isRead=isRead;
        this.isOnStatusBar=isOnStatusBar;
        this.date=date;
    }
    public Notification(){

    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public void setIsOnStatusBar(boolean isOnStatusBar) {
        this.isOnStatusBar = isOnStatusBar;
    }

    public boolean isOnStatusBar() {
        return isOnStatusBar;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
