package com.example.prateek.lifeberrys;

import android.content.Intent;
import android.media.Image;
import  android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by prateek on 28/7/16.
 */
public class fragmentImageSlidePage extends Fragment {

    private ImageView[] circular = new ImageView[5];

    public static Fragment newInstance(int position, List<LifeBerrysXmlParser.Item> items){
        fragmentImageSlidePage fragment = new fragmentImageSlidePage();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putSerializable("List", (Serializable) items);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_image_slide_page, container, false);
        ImageView imageSlide = (ImageView) rootView.findViewById(R.id.imageSlide);
        TextView imageSlideHeading = (TextView) rootView.findViewById(R.id.imageSlideHeading);
        circular[0] = (ImageView) rootView.findViewById(R.id.circular0);
        circular[1] = (ImageView) rootView.findViewById(R.id.circular1);
        circular[2] = (ImageView) rootView.findViewById(R.id.circular2);
        circular[3] = (ImageView) rootView.findViewById(R.id.circular3);
        circular[4] = (ImageView) rootView.findViewById(R.id.circular4);
        int position = getArguments().getInt("position");
        final List<LifeBerrysXmlParser.Item> items = (ArrayList<LifeBerrysXmlParser.Item>) getArguments().getSerializable("List");
        LifeBerrysXmlParser.Item item = items.get(position);
        String url = item.mainImage;
        imageSlideHeading.setText(item.mainHeading);
        Picasso.with(getContext()).load(url).into(imageSlide);
        imageSlide.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openArticle(items, getArguments().getInt("position"));
                    }
                }
        );
        for (int i = 0; i < 5 ; i++) {
            circular[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
        }
        circular[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
        return rootView;

    }

    public void openArticle(List<LifeBerrysXmlParser.Item> items, int position) {
        Intent intent = new Intent(getActivity(), ScreenSlidePagerActivity.class);
        intent.putExtra("itemList", (Serializable) items);
        intent.putExtra("position", position);
        Log.i("Position", String.valueOf(position));
        startActivity(intent);
    }
}
