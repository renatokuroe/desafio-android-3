package br.com.renato.arenatest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import br.com.renato.arenatest.R;
import br.com.renato.arenatest.pojo.Owner;
import br.com.renato.arenatest.pojo.Pull;

/**
 * Created by renato on 10/09/17.
 */

public class PullAdapter extends ArrayAdapter {

    ArrayList<Pull> mItems;
    ArrayList<Owner> mOwners;
    Context mContext;
    int PAGE_COUNT;
    int mResource;

    public PullAdapter(Context context, int resource, ArrayList<Pull> items, ArrayList<Owner> owners) {
        super(context, resource, items);
        mItems = items;
        mResource = resource;
        mOwners = owners;
        mContext = context;
        PAGE_COUNT = items.size();
    }

    @Override
    public int getCount(){
        return PAGE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(mResource, null);
        }

        TextView tvPullName = (TextView) v.findViewById(R.id.tvPullName);
        TextView tvPullDesc = (TextView) v.findViewById(R.id.tvPullDesc);
        ImageView ivUsername = (ImageView) v.findViewById(R.id.ivUsername);
        TextView tvUsername = (TextView) v.findViewById(R.id.tvUsername);

        Pull pullItem = mItems.get(position);
        tvPullName.setText(pullItem.getTitle());
        tvPullDesc.setText(pullItem.getBody());

        Owner owner = mOwners.get(position);
        tvUsername.setText(owner.getLogin());
        Glide.with(mContext).load(owner.getAvatarUrl()).into(ivUsername);

        return v;
    }
}

