package leotik.labs.gesturemessenger.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import leotik.labs.gesturemessenger.Adapters.ContactsAdapter;
import leotik.labs.gesturemessenger.Interface.DownloadListner;
import leotik.labs.gesturemessenger.POJO.UserPOJO;
import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.RealtimeDB;

public class ContactsActivity extends AppCompatActivity implements DownloadListner {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ContactsAdapter mAdapter;
    private FloatingActionButton fab_add;
    private SwipeRefreshLayout swipeContainer;
    private List<UserPOJO> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        swipeContainer = findViewById(R.id.swiperefresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RealtimeDB.getInstance(ContactsActivity.this).getFriends(ContactsActivity.this);
                //  RealtimeDB.getInstance(ContactsActivity.this).attachFriendListListner(ContactsActivity.this);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRecyclerView = findViewById(R.id.contacts_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mUsers = new ArrayList<>();
        mAdapter = new ContactsAdapter(ContactsActivity.this, mUsers);
        mRecyclerView.setAdapter(mAdapter);
        RealtimeDB.getInstance(ContactsActivity.this).getFriends(ContactsActivity.this);
        swipeContainer.setRefreshing(true);
        fab_add = findViewById(R.id.fab_new_contact);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showaddDialog();
            }
        });


    }

    public void showaddDialog() {
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add a new Contact")
                .setMessage("Please Email of the user")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = String.valueOf(taskEditText.getText());
                        RealtimeDB.getInstance(ContactsActivity.this).addFriend(email, ContactsActivity.this);
                        dialog.dismiss();
                        swipeContainer.setRefreshing(true);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }

    @Override
    public void OnDownloadResult(int ResponseCode, Object Response) {
        mUsers.clear();
        mUsers.addAll((ArrayList<UserPOJO>) Response);
        mAdapter.notifyDataSetChanged();
        swipeContainer.setRefreshing(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnErrorDownloadResult(int ResponseCode) {
        swipeContainer.setRefreshing(false);
    }
}
