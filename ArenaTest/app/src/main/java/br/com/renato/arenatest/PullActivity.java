package br.com.renato.arenatest;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.studioidan.httpagent.HttpAgent;
import com.studioidan.httpagent.JsonArrayCallback;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import br.com.renato.arenatest.adapters.PullAdapter;
import br.com.renato.arenatest.pojo.Owner;
import br.com.renato.arenatest.pojo.Pull;
import br.com.renato.arenatest.utils.Server;

/**
 * Created by renato on 10/09/17.
 */

@EActivity(resName = "activity_pull")

public class PullActivity extends AppCompatActivity {

    ArrayList items;
    ArrayList owners;

    @ViewById
    ProgressBar progressBar;
    ListView listView;

    @AfterViews
    protected void afterViews() {
        listView = (ListView)findViewById(R.id.listView);
        items = new ArrayList();
        owners = new ArrayList();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void getData (){

        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }

        String itemName = this.getIntent().getStringExtra("itemName");
        String ownerName = this.getIntent().getStringExtra("ownerName");

        String newURL = Server.URL_GET_PULLS +  "/" + ownerName + "/" + itemName + "/pulls";

        HttpAgent.get(newURL)
                .goJsonArray(new JsonArrayCallback() {
                    @Override
                    protected void onDone(boolean success, JSONArray jsonResults) {
                        if (success) {

                            if (jsonResults.length() == 0) {
                                Toast.makeText(PullActivity.this, "No results.", Toast.LENGTH_LONG).show();

                            } else {

                                ArrayList<Pull> items = new ArrayList<Pull>();
                                ArrayList<Owner> owners = new ArrayList<Owner>();

                                for (int i = 0; i < jsonResults.length(); i++) {
                                    JSONObject json = null;
                                    try {
                                        json = jsonResults.getJSONObject(i);
                                        Pull pullItem = new Pull();
                                        pullItem.setId(json.getInt("id"));
                                        pullItem.setTitle(json.getString("title"));
                                        pullItem.setBody(json.getString("body"));
                                        items.add(pullItem);

                                        Owner owner = new Owner();
                                        JSONObject jsonOwner = json.getJSONObject("user");
                                        owner.setAvatarUrl(jsonOwner.getString("avatar_url"));
                                        owner.setLogin(jsonOwner.getString("login"));
                                        owners.add(owner);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                PullAdapter pullAdapter = new PullAdapter(PullActivity.this, R.layout.pull_adapter_row, items, owners);
                                listView.setAdapter(pullAdapter);
                                listView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(PullActivity.this, R.string.request_error,Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });

    }
}
