package top.sunny8519.common;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import top.sunny8519.common.databinding.ActivityBannerDemoBinding;
import top.sunny8519.supportviews.banners.banner.displayer.ImageViewDisplayer;

import java.util.Arrays;

/**
 * @author niyang
 * @date 2018/12/15
 */
public class BannerDemoActivity extends AppCompatActivity {

    private ActivityBannerDemoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_banner_demo);

        ImageViewDisplayer displayer = new ImageViewDisplayer() {
            @Override
            public void displayImage(Context context, Object path, ImageView view) {
                Glide.with(view).load(path).into(view);
            }
        };
        final String[] urls = getResources().getStringArray(R.array.banner_urls);
        binding.banner.setViewDisplayer(displayer)
                .setOffscreenPageLimit(3)
                .setDelayTime(3000)
                .setImages(Arrays.asList(urls))
                .start();
    }

    @Override
    protected void onDestroy() {
        binding.banner.releaseBanner();
        super.onDestroy();
    }
}
