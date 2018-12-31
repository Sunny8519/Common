package top.sunny8519.common;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import top.sunny8519.common.databinding.ActivityMainBinding;

/**
 * @author niyang
 * @date 2018/12/15
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        final String[] featureNames = getResources().getStringArray(R.array.feature_list);
        final String[] featureClazz = getResources().getStringArray(R.array.feature_clazz);
        final List<ItemInfo> itemInfos = new ArrayList<>();
        for (int i = 0; i < featureNames.length; i++) {
            final ItemInfo itemInfo = new ItemInfo(featureNames[i], featureClazz[i]);
            itemInfos.add(itemInfo);
        }
        final RecyclerView recyclerView = binding.recyclerView;
        final FeatureDemoAdapter adapter = new FeatureDemoAdapter(R.layout.feature_list_item_layout, itemInfos);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener(){

            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final List<ItemInfo> itemInfoList = adapter.getData();
                try {
                    Class clazz = Class.forName(itemInfoList.get(position).getClazzName());
                    Intent intent = new Intent(MainActivity.this, clazz);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        final DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);
    }

    @RequiredArgsConstructor
    static class ItemInfo {
        @NonNull
        @Getter
        private String featureName;

        @NonNull
        @Getter
        private String clazzName;
    }

    static class FeatureDemoAdapter extends BaseQuickAdapter<ItemInfo, BaseViewHolder> {

        FeatureDemoAdapter(int layoutResId, @Nullable List<ItemInfo> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ItemInfo item) {
            helper.setText(R.id.tv_feature_name, item.getFeatureName());
        }
    }
}
