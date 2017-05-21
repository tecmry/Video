package com.example.tecmry.viedoplay;

/**
 * Created by Tecmry on 2017/5/20.
 */

public class ItmeNews {
    private String username;
    private String create_time;
    private String profile_url;
    private String love_times;
    private String hate_times;
    private String video_url;
    private String story;
    private int id;
    private int process;
    public ItmeNews(int id){
        this.id=id;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }
    public void setCreate_time(String create_time){
        this.create_time = create_time;
    }
    public  String getCreate_time(){
        return  create_time;
    }
    public void setProfile_url(String profile_url){
        this.profile_url = profile_url;
    }
    public String getProfile_url(){
        return  profile_url;
    }
    public void setLove_times(String love_times){
        this.love_times = love_times;
    }
    public String getLove_times(){
        return  love_times;
    }
    public  void setHate_times(String hate_times){
        this.hate_times = hate_times;
    }
    public  String getHate_times(){
        return  hate_times;
    }
    public void setVideo_url(String video_url){
        this.video_url = video_url;
    }
    public String  getVideo_url(){
        return  video_url;
    }
    public void setStory(String story){
        this.story = story;
    }
    public String getStory(){
        return story;
    }
    public void setProcess(int process){
        this.process = process;
    }
    public int getProcess(){
        return process;
    }
}
