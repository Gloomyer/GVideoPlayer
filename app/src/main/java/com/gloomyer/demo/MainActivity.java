package com.gloomyer.demo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.gloomyer.demo.R;
import com.gloomyer.gvideoplayer.GVideoManager;
import com.gloomyer.gvideoplayer.utils.GPlayRecyclerViewAutoPlayHelper;
import com.gloomyer.gvideoplayer.view.GVideoView;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvVideos = findViewById(R.id.rv_videos);
        rvVideos.setLayoutManager(new LinearLayoutManager(this));
        rvVideos.setAdapter(new MyAdapter());
        GPlayRecyclerViewAutoPlayHelper.get().bind(rvVideos, R.id.gvv_video);
    }

    private class MyHolder extends RecyclerView.ViewHolder {

        GVideoView gvvVideo;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            gvvVideo = itemView.findViewById(R.id.gvv_video);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item, viewGroup, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, int i) {
            holder.gvvVideo.setTitle("我是一个大标题~");
//            holder.gvvVideo.setVideoUrl("https://gloomyer.com/1.mp4");
            //holder.gvvVideo.setVideoUrl("");
            holder.gvvVideo.setVideoUrl("https://f.us.sinaimg.cn//000s19eRlx07oXbpXFy0010402008Ibi0k010.mp4?label=mp4_ld&template=640x360.28.0&Expires=1541565653&ssig=3HRjpJvk9H&KID=unistore,video");
            Glide.with(MainActivity.this)
                    .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541484490117&di=268cc50b945c2c0162e968f4a6ecf545&imgtype=0&src=http%3A%2F%2Fpic4.1010pic.com%2Fpic10%2Fallimg%2F201607%2F3956-160GZT925T9.jpg")
                    .into(holder.gvvVideo.getCoverIv());
//            });
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GVideoManager.get().onDestory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GVideoManager.get().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GVideoManager.get().onResume();
    }

    @Override
    public void onBackPressed() {
        GVideoManager.get().onBackPressed(this);
    }
}
