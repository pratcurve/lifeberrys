package com.example.prateek.lifeberrys;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prateek on 19/7/16.
 */
public class CustomAdapter extends ArrayAdapter {
    List<LifeBerrysXmlParser.Item> items;
    private ImageView articleMainImage;
    private TextView descriptionHint;
    public CustomAdapter(Context context, List<LifeBerrysXmlParser.Item> itemsList) {
        super(context, R.layout.article_list_view, itemsList);
        items = itemsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater customInflater = LayoutInflater.from(getContext());
        View customView = customInflater.inflate(R.layout.article_list_view, parent, false);
        TextView articleHeading = (TextView) customView.findViewById(R.id.articleHeading);
        float desiredSpHeading = getContext().getResources().getDimension(R.dimen.desired_sp_heading);
        float density = getContext().getResources().getDisplayMetrics().density;
        articleHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP, desiredSpHeading/density);
        float desiredsp = getContext().getResources().getDimension(R.dimen.desired_sp_article);
        articleMainImage = (ImageView) customView.findViewById(R.id.articleMainImage);
        descriptionHint = (TextView) customView.findViewById(R.id.description_hint);
        LifeBerrysXmlParser.Item item = items.get(position);
        descriptionHint.setText(item.description.get(0));
        descriptionHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, desiredsp/density);
        Drawable gray_card = ContextCompat.getDrawable(getContext(), R.drawable.gray_card);
        articleMainImage.setImageDrawable(gray_card);
        String Url = item.mainImage;
        String heading = item.mainHeading;
        articleHeading.setText(heading);
        if (!Url.isEmpty()) {
            Picasso.with(getContext()).load(Url).into(articleMainImage);
        }
        return  customView;
    }
}
