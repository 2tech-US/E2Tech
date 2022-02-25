package com.example.e2tech.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e2tech.Models.BannerModel;
import com.example.e2tech.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class BannerSliderAdapter extends SliderViewAdapter<BannerSliderAdapter.SliderAdapterVH> {

    Context context;
    List<BannerModel> bannerList;

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_banner_item_layout, parent, false);
        return new SliderAdapterVH(inflate);
    }

    public BannerSliderAdapter(Context context, List<BannerModel> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        viewHolder.bannerImgView.setImageResource(bannerList.get(position).getImgId());
    }


    @Override
    public int getCount() {
        return bannerList.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        ImageView bannerImgView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            bannerImgView = itemView.findViewById(R.id.img_banner);

        }
    }
}
