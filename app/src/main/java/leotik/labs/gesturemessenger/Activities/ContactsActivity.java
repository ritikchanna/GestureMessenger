package leotik.labs.gesturemessenger.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import leotik.labs.gesturemessenger.Adapters.ContactsAdapter;
import leotik.labs.gesturemessenger.Interface.DownloadListner;
import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.RealtimeDB;

public class ContactsActivity extends AppCompatActivity implements DownloadListner {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ContactsAdapter mAdapter;
    private FloatingActionButton fab_add;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        swipeContainer = findViewById(R.id.swiperefresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RealtimeDB.getInstance(ContactsActivity.this).attachFriendListListner(ContactsActivity.this);
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
        mAdapter = new ContactsAdapter(ContactsActivity.this);
        mRecyclerView.setAdapter(mAdapter);


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
        mAdapter.updateContacts();
        swipeContainer.setRefreshing(false);

    }

    @Override
    public void OnErrorDownloadResult(int ResponseCode) {
        swipeContainer.setRefreshing(false);
    }
}
