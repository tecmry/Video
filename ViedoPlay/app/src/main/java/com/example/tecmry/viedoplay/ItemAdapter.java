package com.example.tecmry.viedoplay;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<ItmeNews> itemNewsList;


    /**实现接口为item设置按键*/
    public interface OnItemClickListner{
        void OnItemClickListner(View view,int position);
    }

    private OnItemClickListner listner;
    public void setItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }
    public ItemAdapter(List<ItmeNews> list){
        this.itemNewsList = list;
    }


    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemnews,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemAdapter.ViewHolder holder, int position) {
        final ItmeNews itmeNewsData = itemNewsList.get(position);
        holder.Tv_username.setText(itmeNewsData.getUsername());
        holder.Tv_time.setText(itmeNewsData.getCreate_time());
        holder.Tv_story.setText(itmeNewsData.getStory());
        holder.Tv_like.setText(itmeNewsData.getLove_times());
        holder.Tv_dislike.setText(itmeNewsData.getHate_times());
        /**
        if (itmeNewsData.getProcess()!=0){
            holder.Tv_look.setText("你已经看了"+itmeNewsData.getProcess()/1000+"秒");
        }else {
            holder.Tv_look.setText("你还没看这个");
        }*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                new ImageLoader(holder.Cv_userimage).execute(itmeNewsData.getProfile_url());
            }
        }).start();
        if (listner!=null){
            holder.Tv_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner.OnItemClickListner(view,holder.getAdapterPosition());
                }
            });
            holder.Cv_userimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner.OnItemClickListner(view,holder.getAdapterPosition());
                }
            });
            holder.Iv_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner.OnItemClickListner(view,holder.getAdapterPosition());
                }
            });
        }
        setFirstImage(itmeNewsData.getVideo_url(),holder);
        holder.progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return itemNewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       final TextView Tv_username;
       final TextView Tv_time;
       final TextView Tv_story;
       final TextView Tv_like;
       final TextView Tv_dislike;
       final CircleView Cv_userimage;
       final SeekBar seekBar;
        final ImageView Iv_button;
       final ProgressBar progressBar;
       final SurfaceView surfaceView;
        //final TextView Tv_look;
        public ViewHolder(View itemView) {
            super(itemView);
          //  Tv_look = (TextView)itemView.findViewById(R.id.Tv_look);
            Tv_username = (TextView)itemView.findViewById(R.id.TV_username);
            Tv_time = (TextView)itemView.findViewById(R.id.TV_time);
            Tv_story = (TextView)itemView.findViewById(R.id.Tv_story);
            Tv_like = (TextView)itemView.findViewById(R.id.TV_love);
            Tv_dislike = (TextView)itemView.findViewById(R.id.TV_dislike);
            Cv_userimage = (CircleView)itemView.findViewById(R.id.IV_userImage);
            seekBar = (SeekBar)itemView.findViewById(R.id.seekbar);
            Iv_button = (ImageView)itemView.findViewById(R.id.Iv_Button);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
            surfaceView = (SurfaceView)itemView.findViewById(R.id.surfaceView);
        }
    }
    private void setFirstImage(String firstImage, final ViewHolder holder){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(firstImage, new HashMap<String, String>());
        Bitmap bitmap=  retriever.getFrameAtTime();
        final BitmapDrawable drawable = new BitmapDrawable(bitmap);
        holder.surfaceView.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
            holder.surfaceView.setBackground(drawable);
            }
        });

    }
}
