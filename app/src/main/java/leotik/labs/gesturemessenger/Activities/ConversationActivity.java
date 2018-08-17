package leotik.labs.gesturemessenger.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import leotik.labs.gesturemessenger.Adapters.ConversationAdapter;
import leotik.labs.gesturemessenger.R;

public class ConversationActivity extends AppCompatActivity {

    private RecyclerView conversationRecyclerView;
    private FloatingActionButton newMsgfab;
    private RecyclerView.LayoutManager mLayoutManager;
    private ConversationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        conversationRecyclerView = findViewById(R.id.message_list);
        mLayoutManager = new LinearLayoutManager(this);
        conversationRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ConversationAdapter(ConversationActivity.this, "+919013660088");
        conversationRecyclerView.setAdapter(mAdapter);
        newMsgfab = findViewById(R.id.fab_new_message);
        newMsgfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //todo send intent to draw activity
            }
        });
    }


}
