package com.gloomyer.gvideoplayer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
            holder.gvvVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.gvvVideo.setVideoUrl("https://gloomyer.com/1.mp4");
                    holder.gvvVideo.start();
                }
            });
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }
}
