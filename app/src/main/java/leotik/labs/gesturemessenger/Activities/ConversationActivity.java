package leotik.labs.gesturemessenger.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import leotik.labs.gesturemessenger.Adapters.ConversationAdapter;
import leotik.labs.gesturemessenger.R;

public class ConversationActivity extends AppCompatActivity {

    private RecyclerView conversationRecyclerView;
    private FloatingActionButton newMsgfab;
    private LinearLayoutManager mLayoutManager;
    private ConversationAdapter mAdapter;
    private String Photo, Phone, Name;
    private TextView nameTitle;
    private SimpleDraweeView photoTitle;
    private Intent launchDrawActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        Phone = getIntent().getStringExtra("phone");
        Name = getIntent().getStringExtra("name");
        Photo = getIntent().getStringExtra("photo");
        nameTitle = findViewById(R.id.contact_title);
        photoTitle = findViewById(R.id.contact_photo);
        nameTitle.setText(Name);
        photoTitle.setImageURI(Uri.parse(Photo));
        //actionbar.setTitle(Name);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayUseLogoEnabled(false);
        conversationRecyclerView = findViewById(R.id.message_list);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        conversationRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ConversationAdapter(ConversationActivity.this, Phone);
        conversationRecyclerView.setAdapter(mAdapter);
        newMsgfab = findViewById(R.id.fab_new_message);
        newMsgfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDrawActivity = new Intent(ConversationActivity.this, DrawActivity.class);
                launchDrawActivity.putExtra("phone", Phone);
                launchDrawActivity.putExtra("name", Name);
                launchDrawActivity.putExtra("photo", Photo);
                startActivity(launchDrawActivity);
            }
        });
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

}
