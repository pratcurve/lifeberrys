package com.example.prateek.lifeberrys;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by prateek on 20/6/16.
 */
public class fragmentScreenSlidePage extends Fragment {
    ImageView articleImage;
    FloatingActionButton fab;
    List<LifeBerrysXmlParser.Item> items;
    private int position;
    private String articleMainHeading;
    private ImageView mainImage;
    private TextView mainHeading;
    private String link;

    public static fragmentScreenSlidePage newInstance(int position, List<LifeBerrysXmlParser.Item> List) {
        fragmentScreenSlidePage fragment = new fragmentScreenSlidePage();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putSerializable("List", (Serializable) List);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        items = (ArrayList<LifeBerrysXmlParser.Item>) getArguments().getSerializable("List");

        mainHeading = (TextView) rootView.findViewById(R.id.mainHeading);
        mainImage = (ImageView) rootView.findViewById(R.id.articleMainImage);
        LinearLayout articleLayout = (LinearLayout) rootView.findViewById(R.id.articleLayout);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        position = getArguments().getInt("position");
        LifeBerrysXmlParser.Item item = items.get(position);
        link = item.link;
        List<String> description = item.description;;
        articleMainHeading = item.mainHeading;
        final String articlemainImage = item.mainImage;


        //creating articles
        mainHeading.setText(articleMainHeading);
        if (!articlemainImage.isEmpty()) {
            Picasso.with(getContext()).load(articlemainImage).into(mainImage);
        }
        for (int i = 0; i< description.size(); i++){
            if (!item.mainHeading.equals(item.heading.get(i))) {
                Resources r = getResources();
                TextView articleHeading = new TextView(getActivity());

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, (int) getResources().getDimension(R.dimen.heading_margin), 0, 0);
                int textPadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, r.getDisplayMetrics());
                articleHeading.setPadding(textPadding, 0, 0, textPadding);
                float desiredSp = getResources().getDimension(R.dimen.desired_sp_heading);
                float density = getResources().getDisplayMetrics().density;
                articleHeading.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                articleHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP, desiredSp / density);
                articleHeading.setTextColor(Color.parseColor("#000000"));
                articleHeading.setText(item.heading.get(i));
                articleHeading.setLayoutParams(layoutParams);
                articleLayout.addView(articleHeading);
            }
            if (!item.mainImage.equals(item.image.get(i))) {
                articleImage = new ImageView(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        (int)getResources().getDimension(R.dimen.image_height));
                Resources r = getResources();

                //image layout in article

                int margin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, r.getDisplayMetrics());
                layoutParams.setMargins(margin, 0, margin, 0);
                articleImage.setMaxHeight(R.dimen.image_height);
                articleImage.setLayoutParams(layoutParams);
                articleImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                articleImage.setBackgroundResource(R.drawable.gray_card);

                //loading Image using Picasso
                Picasso.with(getContext()).load(item.image.get(i)).into(articleImage);
                articleLayout.addView(articleImage);
            }
            TextView articleDescription = new TextView(getActivity());
            articleDescription.setText(item.description.get(i));
            Resources r = getResources();
            int textPadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, r.getDisplayMetrics());
            float desiredSp = getResources().getDimension(R.dimen.desired_sp_article);
            float density = getResources().getDisplayMetrics().density;
            articleDescription.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            articleDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, desiredSp / density);
            articleDescription.setPadding(textPadding,textPadding,textPadding,0);
            articleLayout.addView(articleDescription);
        }
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareArticle();
                    }
                }
        );
        return rootView;
    }

    public void shareArticle() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        Drawable mDrawable = mainImage.getDrawable();
        Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
        String shareBody, path, url = link;
        Uri screenshotUri;

        shareBody = articleMainHeading + "\n \n" + url;
        if (Build.VERSION.SDK_INT >= 23) {
            if (isStoragePermissionGranted()) {
                path = Media.insertImage(getContext().getContentResolver(), mBitmap, "image", null);
                screenshotUri = Uri.parse(path);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "LifeBerrys.com");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                sharingIntent.setType("image/*");
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        } else {
            path = Media.insertImage(getContext().getContentResolver(), mBitmap, "image", null);
            screenshotUri = Uri.parse(path);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "LifeBerrys.com");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

            sharingIntent.setType("image/*");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
    }

    public boolean isStoragePermissionGranted() {
            if (checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
        }

    }
}
