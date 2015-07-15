package com.ronit.flink;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import info.ronit.db.*;
/**
 * Created by Ronit on 4/18/2015.
 */
public class ImageCursorAdapter extends SimpleCursorAdapter {

    private Cursor c;
    private Context context;

    public ImageCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.c = c;
        this.context = context;
    }

    public View getView(int pos, View inView, ViewGroup parent) {
        View v = inView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }
        this.c.moveToPosition(pos);
        String name1 = this.c.getString(this.c.getColumnIndex(DbHandler.KEY_NAME));
        String relation1 = this.c.getString(this.c.getColumnIndex(DbHandler.KEY_FLINK));
        String age_detail1 = this.c.getString(this.c.getColumnIndex(DbHandler.KEY_AGE));
        byte[] image = this.c.getBlob(this.c.getColumnIndex(DbHandler.KEY_IMAGE));
        ImageView iv = (ImageView) v.findViewById(R.id.displaypic);
        if (image != null) {
            // If there is no image in the database "NA" is stored instead of a blob
            // test if there more than 3 chars "NA" + a terminating char if more than
            // there is an image otherwise load the default
            if (image.length > 3) {
                iv.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            } else {
                iv.setImageResource(R.drawable.ic_launcher);
            }
        }
        TextView name = (TextView) v.findViewById(R.id.name);
        name.setText(name1);

        TextView relation = (TextView) v.findViewById(R.id.relation);
        relation.setText(relation1);

        TextView age_detail = (TextView) v.findViewById(R.id.age_detail);
        age_detail.setText(age_detail1);
        return (v);
    }
}