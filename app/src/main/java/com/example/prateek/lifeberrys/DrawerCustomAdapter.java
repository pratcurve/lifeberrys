package com.example.prateek.lifeberrys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by prateek on 1/9/16.
 */
public class DrawerCustomAdapter extends ArrayAdapter {

    private String[] menuOptions;
    private TextView drawerMenuText;
    private ImageView drawerMenuIcon;
    public DrawerCustomAdapter(Context context, String[] Options) {
        super(context, R.layout.drawer_list_view, Options);
        menuOptions = Options;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater customInflater = LayoutInflater.from(getContext());
        View customView = customInflater.inflate(R.layout.drawer_list_view, parent, false);

        drawerMenuText = (TextView) customView.findViewById(R.id.drawer_text_view);
        drawerMenuIcon = (ImageView) customView.findViewById(R.id.fab_icon);

        switch (position) {

            case 0:
                drawerMenuText.setText(menuOptions[0]);
                Picasso.with(getContext()).load(R.drawable.fashion).into(drawerMenuIcon);
                break;
            case 1:
                drawerMenuText.setText(menuOptions[1]);
                Picasso.with(getContext()).load(R.drawable.beauty).into(drawerMenuIcon);
                break;
            case 2:
                drawerMenuText.setText(menuOptions[2]);
                Picasso.with(getContext()).load(R.drawable.health).into(drawerMenuIcon);
                break;
            case 3:
                drawerMenuText.setText(menuOptions[3]);
                Picasso.with(getContext()).load(R.drawable.relationship).into(drawerMenuIcon);
                break;
            case 4:
                drawerMenuText.setText(menuOptions[4]);
                Picasso.with(getContext()).load(R.drawable.household).into(drawerMenuIcon);
                break;
            case 5:
                drawerMenuText.setText(menuOptions[5]);
                Picasso.with(getContext()).load(R.drawable.food).into(drawerMenuIcon);
                break;
            case 6:
                drawerMenuText.setText(menuOptions[6]);
                Picasso.with(getContext()).load(R.drawable.holidays).into(drawerMenuIcon);
                break;
            case 7:
                drawerMenuText.setText(menuOptions[7]);
                Picasso.with(getContext()).load(R.drawable.entertainment).into(drawerMenuIcon);
                break;
            case 8:
                drawerMenuText.setText(menuOptions[8]);
                Picasso.with(getContext()).load(R.drawable.astrology).into(drawerMenuIcon);
                break;
            case 9:
                drawerMenuText.setText(menuOptions[9]);
                Picasso.with(getContext()).load(R.drawable.all_articles).into(drawerMenuIcon);
                break;
        }
        return customView;
    }
}
