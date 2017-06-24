package com.tpanpm.wwsis.placzabaw;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by KacperS on 2017-05-27.
 */

public class ArrayAdapter extends android.widget.ArrayAdapter<Playground> {
    private final Activity context;


    private final ArrayList<Playground> plac;

    static class ViewHolder {
        public TextView name;
        public TextView desc;
        public RatingBar rate;
        public ImageView image;

    }

    public ArrayAdapter(Activity context, ArrayList<Playground> plac) {
        super(context, R.layout.rowlayout, plac);
        this.context = context;
        this.plac = plac;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.rowlayout, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.name = (TextView) rowView.findViewById(R.id.title_test_on_list);
            viewHolder.desc = (TextView) rowView.findViewById(R.id.snippet_on_list);
            viewHolder.rate = (RatingBar) rowView.findViewById(R.id.rating_bar);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.avatar);


            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        String name = plac.get(position).getNAME();
        String desc = plac.get(position).getDESC();
        float rate = plac.get(position).getRATE();
        String url = plac.get(position).getIMAGE();

        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        final ProgressBar progressBar = (ProgressBar) rowView.findViewById(R.id.progress);
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(httpsReference)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.image);

        holder.name.setText(name);
        holder.desc.setText(desc);
        holder.rate.setRating(rate);

        return rowView;


    }

}