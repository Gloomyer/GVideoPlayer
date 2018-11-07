package com.gloomyer.demo;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.gloomyer.gvideoplayer.GVideoManager;
import com.gloomyer.gvideoplayer.constants.GPlayState;
import com.gloomyer.gvideoplayer.interfaces.GOnPlayStateChangeListener;
import com.gloomyer.gvideoplayer.utils.GPlayRecyclerViewAutoPlayHelper;
import com.gloomyer.gvideoplayer.view.GVideoView;


public class MainActivity extends AppCompatActivity {

    ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp = findViewById(R.id.vp);
        final String[] tags = new String[3];
        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                MyFragment mf = new MyFragment();
                mf.tag = "MyFragment" + i;
                tags[i] = mf.tag;
                return mf;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                for (int i1 = 0; i1 < 3; i1++) {
                    if (i1 == i) {
                        GVideoManager.get().onResume(tags[i]);
                    } else {
                        GVideoManager.get().onPause(tags[i]);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @SuppressLint("ValidFragment")
    public static class MyFragment extends Fragment {

        private String tag;
        RecyclerView rvVideos;

        public MyFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment, container, false);
            rvVideos = v.findViewById(R.id.rv_videos);
            rvVideos.setLayoutManager(new LinearLayoutManager(getContext()));
            rvVideos.setAdapter(new MyAdapter(tag));
            GPlayRecyclerViewAutoPlayHelper.get().bind(rvVideos, R.id.gvv_video);
            return v;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
        }
    }

    private static class MyHolder extends RecyclerView.ViewHolder {

        GVideoView gvvVideo;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            gvvVideo = itemView.findViewById(R.id.gvv_video);
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        private final String tag;

        public MyAdapter(String tag) {
            this.tag = tag;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item, viewGroup, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, int i) {
            holder.gvvVideo.setTAG(tag);
            holder.gvvVideo.setTitle("我是一个大标题~");
//            holder.gvvVideo.setVideoUrl("https://gloomyer.com/1.mp4");
            //holder.gvvVideo.setVideoUrl("");
            holder.gvvVideo.setVideoUrl("https://f.us.sinaimg.cn//000s19eRlx07oXbpXFy0010402008Ibi0k010.mp4?label=mp4_ld&template=640x360.28.0&Expires=1541577266&ssig=vCzXwTPvVw&KID=unistore,video");
            holder.gvvVideo.setOnPlayStateChangeListener(new GOnPlayStateChangeListener() {
                @Override
                public void onChanged(GPlayState state) {
                    Log.e("onBindViewHolder", tag + state);
                }
            });
            Glide.with(holder.itemView.getContext())
                    .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541484490117&di=268cc50b945c2c0162e968f4a6ecf545&imgtype=0&src=http%3A%2F%2Fpic4.1010pic.com%2Fpic10%2Fallimg%2F201607%2F3956-160GZT925T9.jpg")
                    .into(holder.gvvVideo.getCoverIv());
//            });
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        GVideoManager.get().onDestory();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        GVideoManager.get().onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        GVideoManager.get().onResume();
//    }
//
//    @Override
//    public void onBackPressed() {
//        GVideoManager.get().onBackPressed(this);
//    }
}
