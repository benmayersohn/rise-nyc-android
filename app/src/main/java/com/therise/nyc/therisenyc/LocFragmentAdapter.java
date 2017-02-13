package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 1/15/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

// Construct custom view for each fragment
public class LocFragmentAdapter extends BaseAdapter{

    private List<LocPage> pages;
    private int count;
    private Context context;
    private static LayoutInflater inflater=null;

    // Construct view
    public LocFragmentAdapter(FragmentActivity act, List<LocPage> pages) {

        this.pages = pages;

        context=act;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        count = pages.size();

        // Set count now
    }
    @Override
    public int getCount() {

        return count;
    }

    // Don't need this
    @Override
    public Object getItem(int position) {
        return pages.get(position);
    }

    // The ID is just the position
    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder
    {
        TextView workoutTitle;
        TextView workoutDay;
        TextView workoutPlace;
        ImageView workoutImg;
        TextView workoutDesc;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder=new ViewHolder();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.loc_single, null);
        }

        holder.workoutTitle = (TextView) convertView.findViewById(R.id.textView1);
        holder.workoutPlace = (TextView) convertView.findViewById(R.id.textView2);
        holder.workoutDay = (TextView) convertView.findViewById(R.id.textView4);
        holder.workoutImg = (ImageView) convertView.findViewById(R.id.imageView1);
        holder.workoutDesc = (TextView) convertView.findViewById(R.id.textView3);

        // Set values
        holder.workoutTitle.setText(pages.get(position).getTitle());

        // Combine day and place to get the DayPlace
        holder.workoutDay.setText(pages.get(position).getDay());
        holder.workoutDay.append("s @ 6:30 AM");
        holder.workoutPlace.setText(pages.get(position).getPlace());

        holder.workoutDesc.setText(pages.get(position).getDesc());

        // get bitmap from image

        // holder.workoutImg.setImageBitmap(pages.get(position).getImg());

        // Load page in background
        // Then push to UI
        new AsyncTask<ViewHolder, Void, Bitmap>() {
            private ViewHolder v;

            @Override
            protected Bitmap doInBackground(ViewHolder... params) {

                // Set v equal to holder
                v = params[0];

                // Load the image
                return LocationFragment.decodeSampledBitmapFromResource(context.getResources(),
                        pages.get(position).getImg(),
                        LocationFragment.IMAGE_WIDTH,LocationFragment.IMAGE_HEIGHT);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                    v.workoutImg.setImageBitmap(result);
                }
            }.execute(holder);

        return convertView;
    }

}
