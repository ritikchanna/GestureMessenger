package leotik.labs.gesturemessenger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import leotik.labs.gesturemessenger.POJO.ChatPOJO;
import leotik.labs.gesturemessenger.POJO.UserPOJO;
import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Service.OverlayService;
import leotik.labs.gesturemessenger.Util.ChatsDatabaseHelper;
import leotik.labs.gesturemessenger.Util.DatabaseHelper;

import static leotik.labs.gesturemessenger.Util.Helper.getTime;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private static List<ChatPOJO> mMessages;
    private static Context mcontext;
    private static Intent launchoverlay;
    private static DatabaseHelper databaseHelper;
    private static ChatsDatabaseHelper chatsDatabaseHelper;
    private String mUser;


    public ConversationAdapter(Context context, String user) {
        databaseHelper = new DatabaseHelper(context);
        chatsDatabaseHelper = new ChatsDatabaseHelper(context);
        mUser = user;
        mMessages = chatsDatabaseHelper.getChat(mUser);
        launchoverlay = new Intent(context, OverlayService.class);
        mcontext = context;
    }


    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatPOJO message = mMessages.get(position);
        if (message.getSide().equals(ChatsDatabaseHelper.SIDE_DOWN)) {
            holder.rightMsg.setVisibility(View.GONE);
            holder.leftMsg.setVisibility(View.VISIBLE);
            holder.leftText.setText(getTime(message.getTime()));
        } else {

            holder.leftMsg.setVisibility(View.GONE);
            holder.rightMsg.setVisibility(View.VISIBLE);
            holder.rightText.setText(getTime(message.getTime()));
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public RelativeLayout leftMsg, rightMsg;
        public ImageView leftImage, rightImage;
        public TextView leftText, rightText;


        public ViewHolder(View v) {
            super(v);
            leftMsg = v.findViewById(R.id.leftMsg);
            rightMsg = v.findViewById(R.id.rightMsg);
            leftImage = v.findViewById(R.id.leftMsgPicture);
            rightImage = v.findViewById(R.id.rightMsgPicture);
            leftText = v.findViewById(R.id.leftMsgTime);
            rightText = v.findViewById(R.id.rightMsgTime);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ChatPOJO message = mMessages.get(getAdapterPosition());
            launchoverlay.putExtra("gesture", message.getMessage());
            UserPOJO user = databaseHelper.getUser(message.getUser());
            if (user == null) {
                launchoverlay.putExtra("sender_name", message.getUser());
                launchoverlay.putExtra("sender_picture", "");
            } else {
                launchoverlay.putExtra("sender_name", user.getN());
                launchoverlay.putExtra("sender_picture", user.getU());
            }
            //todo check if should be drawn otherwise drop notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mcontext.startForegroundService(launchoverlay);
            } else {
                mcontext.startService(launchoverlay);
            }


        }


    }

}