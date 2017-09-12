package br.com.renato.arenatest;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.paging.listview.PagingListView;
import com.studioidan.httpagent.HttpAgent;
import com.studioidan.httpagent.JsonCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import br.com.renato.arenatest.adapters.PullAdapter;
import br.com.renato.arenatest.adapters.RepoAdapter;
import br.com.renato.arenatest.pojo.Item;
import br.com.renato.arenatest.pojo.Owner;
import br.com.renato.arenatest.utils.Server;

@EActivity(resName = "activity_main")

public class MainActivity extends AppCompatActivity {

    int pageCount;
    ArrayList <Item> items;
    ArrayList <Owner> owners;

    @ViewById
    ProgressBar progressBar;
    PagingListView listView;

    @AfterViews
    protected void afterViews() {
        listView = (PagingListView)findViewById(R.id.listView);
        listView.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                getData();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, PullActivity_.class);
                // Passing necessary data to use in next request
                Item item = items.get(i);
                Owner owner = owners.get(i);
                String ownerName = owner.getLogin();
                String itemName = item.getName();
                intent.putExtra("itemName", itemName);
                intent.putExtra("ownerName", ownerName);
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pageCount = 1;
        items = new ArrayList();
        owners = new ArrayList();

        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getData (){

        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }

        // Show main progress bar if first request
        if (pageCount == 1) {
            progressBar.setVisibility(View.VISIBLE);
        }

        String newURL = Server.URL_GET_DATA;
        HttpAgent.get(newURL)
                .queryParams("sort", "stars", "page" , String.valueOf(pageCount), "q", "language:Java")
                .goJson(new JsonCallback() {
                    @Override
                    protected void onDone(boolean success, JSONObject jsonResults) {
                        if (success) {

                            try {
                                JSONArray jsonArray = jsonResults.getJSONArray("items");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject json = jsonArray.getJSONObject(i);
                                    Item item = new Item();
                                    item.setId(json.getInt("id"));
                                    item.setName(json.getString("name"));
                                    item.setDescription(json.getString("description"));
                                    item.setForks(json.getInt("forks"));
                                    item.setStargazersCount(json.getInt("stargazers_count"));
                                    items.add(item);
                                    Owner owner = new Owner();
                                    JSONObject jsonOwner = json.getJSONObject("owner");
                                    owner.setAvatarUrl(jsonOwner.getString("avatar_url"));
                                    owner.setLogin(jsonOwner.getString("login"));
                                    owners.add(owner);
                                }

                                // save index and top position
                                int index = listView.getFirstVisiblePosition();
                                View v = listView.getChildAt(0);
                                int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

                                RepoAdapter repoAdapter = new RepoAdapter(MainActivity.this, R.layout.repo_adapter_row, items, owners);
                                listView.setAdapter(repoAdapter);

                                // restore index and position
                                listView.setSelectionFromTop(index, top);

                                int totalCounts = jsonResults.getInt("total_count");
                                if (totalCounts > items.size()) {
                                    listView.setHasMoreItems(true);
                                    pageCount ++;
                                } else {
                                    listView.setHasMoreItems(false);
                                }

                                listView.setVisibility(View.VISIBLE);
                                listView.onFinishLoading(true, null);

                            } catch (JSONException e) {
                                Toast.makeText(MainActivity.this, e.toString(),Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, R.string.request_error,Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}
