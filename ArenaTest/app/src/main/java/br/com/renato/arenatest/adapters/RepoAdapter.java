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
import br.com.renato.arenatest.pojo.Item;
import br.com.renato.arenatest.pojo.Owner;

/**
 * Created by renato on 10/09/17.
 */

public class RepoAdapter extends ArrayAdapter {

    ArrayList<Item> mItems;
    ArrayList<Owner> mOwners;
    Context mContext;
    int PAGE_COUNT;
    int mResource;

    public RepoAdapter(Context context, int resource, ArrayList<Item> items, ArrayList<Owner> owners) {
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

        TextView tvRepoName = (TextView) v.findViewById(R.id.tvRepoName);
        TextView tvRepoDesc = (TextView) v.findViewById(R.id.tvRepoDesc);
        TextView tvForkCount = (TextView) v.findViewById(R.id.tvForkCount);
        TextView tvStarCount = (TextView) v.findViewById(R.id.tvStarCount);
        ImageView ivUsername = (ImageView) v.findViewById(R.id.ivUsername);
        TextView tvUsername = (TextView) v.findViewById(R.id.tvUsername);

        Item item = mItems.get(position);
        tvRepoName.setText(item.getName());
        tvRepoDesc.setText(item.getDescription());
        tvForkCount.setText(item.getForks().toString());
        tvStarCount.setText(item.getStargazersCount().toString());

        Owner owner = mOwners.get(position);
        tvUsername.setText(owner.getLogin());
        Glide.with(mContext).load(owner.getAvatarUrl()).into(ivUsername);

        return v;
    }
}

