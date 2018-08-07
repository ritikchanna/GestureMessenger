package leotik.labs.gesturemessenger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import leotik.labs.gesturemessenger.POJO.ChatPOJO;
import leotik.labs.gesturemessenger.POJO.UserPOJO;
import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Service.OverlayService;
import leotik.labs.gesturemessenger.Util.ChatsDatabaseHelper;
import leotik.labs.gesturemessenger.Util.DatabaseHelper;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private static List<ChatPOJO> mChats;
    private static Context mcontext;
    private static ChatsDatabaseHelper chatsDatabaseHelper;
    private static DatabaseHelper databaseHelper;


    public ChatsAdapter(Context context) {
        chatsDatabaseHelper = new ChatsDatabaseHelper(context);
        databaseHelper = new DatabaseHelper(context);
        mChats = chatsDatabaseHelper.getChats();
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
        UserPOJO user = databaseHelper.getUser(mChats.get(position).getEmail());
        holder.profileName.setText(user.getN());
        if (user.getU() != null || user.getU().equals(""))
            holder.profilePhoto.setImageURI(Uri.parse("http://flathash.com/" + mChats.get(position).getEmail() + ".png"));
        else
            holder.profilePhoto.setImageURI(Uri.parse(user.getU()));
    }

    public void updateChats() {
        mChats = chatsDatabaseHelper.getChats();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mChats.size();
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
            Intent intent = new Intent(mcontext, OverlayService.class);
            intent.putExtra("gesture", mChats.get(getAdapterPosition()).getMessage());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mcontext.startForegroundService(intent);
            } else {
                mcontext.startService(intent);
            }

        }
    }
}