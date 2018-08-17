package leotik.labs.gesturemessenger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import leotik.labs.gesturemessenger.Activities.ConversationActivity;
import leotik.labs.gesturemessenger.POJO.UserPOJO;
import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.ChatsDatabaseHelper;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private static ArrayList<UserPOJO> users;
    private static Context mcontext;
    private static ChatsDatabaseHelper chatsDatabaseHelper;



    public ChatsAdapter(Context context) {
        chatsDatabaseHelper = new ChatsDatabaseHelper(context);
        users = chatsDatabaseHelper.getChatUsers();
        mcontext = context;
    }

    @Override
    public ChatsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserPOJO user = users.get(position);
        holder.profileName.setText(user.getN());
        if (user.getU() == null || user.getU().equals(""))
            holder.profilePhoto.setImageURI(Uri.parse("http://flathash.com/" + user.getN() + ".png"));
        else
            holder.profilePhoto.setImageURI(Uri.parse(user.getU()));
    }

    public void updateChats() {
        users = chatsDatabaseHelper.getChatUsers();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public SimpleDraweeView profilePhoto;
        public TextView profileName;


        public ViewHolder(View v) {
            super(v);
            profilePhoto = v.findViewById(R.id.contact_photo);
            profileName = v.findViewById(R.id.contact_name);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mcontext, ConversationActivity.class);
            intent.putExtra("user", users.get(getAdapterPosition()).getN());
            mcontext.startActivity(intent);
        }
    }
}