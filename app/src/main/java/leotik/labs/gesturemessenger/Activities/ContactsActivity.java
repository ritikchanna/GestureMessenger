package leotik.labs.gesturemessenger.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.TextView;

import java.util.List;

import leotik.labs.gesturemessenger.Adapters.ContactsAdapter;
import leotik.labs.gesturemessenger.Interface.DownloadListner;
import leotik.labs.gesturemessenger.POJO.UserPOJO;
import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.Constants;
import leotik.labs.gesturemessenger.Util.DatabaseHelper;
import leotik.labs.gesturemessenger.Util.Logging;
import leotik.labs.gesturemessenger.Util.RealtimeDB;

public class ContactsActivity extends AppCompatActivity implements DownloadListner {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ContactsAdapter mAdapter;
    private SwipeRefreshLayout swipeContainer;
    private List<UserPOJO> mUsers, mRequests;
    private DatabaseHelper databaseHelper;
    private TextView requestcount, sentRequests;
    private int currentlist;
    private ActionBar actionbar;
    private FloatingActionButton addfriend_fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentlist = 0;
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle(getString(R.string.contacts_activity_title));
        swipeContainer = findViewById(R.id.swiperefresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                RealtimeDB.getInstance(ContactsActivity.this).getFriends(ContactsActivity.this, Constants.REFRESH_CONTACTS);
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
        databaseHelper = new DatabaseHelper(ContactsActivity.this);
        mUsers = databaseHelper.getAllUsers("f");
        mAdapter = new ContactsAdapter(ContactsActivity.this, mUsers, this);
        mRecyclerView.setAdapter(mAdapter);
        sentRequests = findViewById(R.id.sent_requests);
        sentRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadlist(2);
            }
        });
        addfriend_fab = findViewById(R.id.fab_new_contact);
        addfriend_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showaddDialog();
            }
        });


    }

    public void showaddDialog() {
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add a new Contact")
                .setMessage("Please enter phone number of the user")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phone = String.valueOf(taskEditText.getText());
                        RealtimeDB.getInstance(ContactsActivity.this).addFriend(phone, ContactsActivity.this, Constants.ADD_FRIEND);
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
        if (ResponseCode == Constants.REFRESH_CONTACTS) {
            loadlist(currentlist);
            swipeContainer.setRefreshing(false);
        } else if (ResponseCode == Constants.ADD_FRIEND) {
            //do something on friend add
            swipeContainer.setRefreshing(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.friendRequest_button);
        if (currentlist == 0) {
            View actionView = MenuItemCompat.getActionView(menuItem);
            requestcount = actionView.findViewById(R.id.friendRequest_button_tv);
            setupFriendRequestBadge();
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionsItemSelected(menuItem);
                }
            });
            menuItem.setVisible(true);
        } else menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
//            case R.id.addfriend_button:
//                showaddDialog();
//                return true;
            case R.id.friendRequest_button:
                loadlist(1);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void setupFriendRequestBadge() {
        mRequests = databaseHelper.getAllUsers("r");
        if (mRequests != null) {
            if (mRequests.size() == 0) {
                if (requestcount.getVisibility() != View.GONE) {
                    requestcount.setVisibility(View.GONE);
                }
            } else {
                requestcount.setText(String.valueOf(mRequests.size()));
                if (requestcount.getVisibility() != View.VISIBLE) {
                    requestcount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void OnErrorDownloadResult(int ResponseCode) {
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (currentlist != 0)
            loadlist(0);
        else
            super.onBackPressed();
    }

    public void loadlist(int list) {
        Logging.logDebug(ContactsActivity.class, "Load List " + list);
        mUsers.clear();
        if (list == 2) {
            sentRequests.setVisibility(View.GONE);
            currentlist = 2;
            actionbar.setTitle(getString(R.string.friend_request_title));
            mUsers.addAll(databaseHelper.getAllUsers("s"));
        } else if (list == 1) {
            sentRequests.setVisibility(View.VISIBLE);
            currentlist = 1;
            actionbar.setTitle(getString(R.string.friend_request_title));
            mUsers.addAll(databaseHelper.getAllUsers("r"));
        } else {
            sentRequests.setVisibility(View.GONE);
            currentlist = 0;
            actionbar.setTitle(getString(R.string.contacts_activity_title));
            mUsers.addAll(databaseHelper.getAllUsers("f"));
        }
        invalidateOptionsMenu();
        mAdapter.notifyDataSetChanged();
    }


}
