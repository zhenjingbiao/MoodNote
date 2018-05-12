package com.example.jingbiaozhen.moodnote;
/*
 * Created by jingbiaozhen on 2018/5/12.
 * 心情笔记javabean
 **/

import java.io.Serializable;
import java.util.List;

public class NoteBean implements Serializable
{

    int id;// 每条笔记id

    String decs;// 内容描述

    String mood;

    String time;


    List<String> imagePaths;// 插入图片地址

    List<String> voicePaths;// 插入音频的地址


    @Override
    public String toString() {
        return "NoteBean{" +
                "id=" + id +
                ", decs='" + decs + '\'' +
                ", mood='" + mood + '\'' +
                ", time='" + time + '\'' +
                ", imagePaths=" + imagePaths +
                ", voicePaths=" + voicePaths +
                '}';
    }
}
