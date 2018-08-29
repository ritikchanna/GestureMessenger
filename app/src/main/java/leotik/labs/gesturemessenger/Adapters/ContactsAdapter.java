package leotik.labs.gesturemessenger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import leotik.labs.gesturemessenger.Activities.ContactsActivity;
import leotik.labs.gesturemessenger.Activities.DrawActivity;
import leotik.labs.gesturemessenger.Interface.DownloadListner;
import leotik.labs.gesturemessenger.POJO.UserPOJO;
import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.Constants;
import leotik.labs.gesturemessenger.Util.RealtimeDB;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> implements DownloadListner {
    private static List<UserPOJO> mUsers;
    private static Context mcontext;
    private static Intent launchDrawActivity;
    private static RealtimeDB realtimeDB;


    public ContactsAdapter(Context context, List<UserPOJO> Users, DownloadListner downloadListner) {
        realtimeDB = RealtimeDB.getInstance(context);
        mUsers = Users;
        launchDrawActivity = new Intent(context, DrawActivity.class);
        mcontext = context;


    }

    @Override
    public void OnDownloadResult(int ResponseCode, Object Response) {
        if (ResponseCode == Constants.FRIEND_REQUEST_RESPONSE) {
            mUsers.remove(Response);
            notifyDataSetChanged();

        }
    }

    @Override
    public void OnErrorDownloadResult(int ResponseCode) {
        if (ResponseCode == Constants.FRIEND_REQUEST_RESPONSE) {
            ((ContactsActivity) mcontext).loadCurrentlist();
        }
    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.profileName.setText(mUsers.get(position).getN());

        if (mUsers.get(position).getS().equals("s")) {
            holder.discardRequest.setVisibility(View.VISIBLE);
            holder.acceptRequest.setVisibility(View.GONE);

        } else if (mUsers.get(position).getS().equals("r")) {
            holder.discardRequest.setVisibility(View.VISIBLE);
            holder.acceptRequest.setVisibility(View.VISIBLE);
        } else {
            holder.discardRequest.setVisibility(View.GONE);
            holder.acceptRequest.setVisibility(View.GONE);
        }
        if (mUsers.get(position).getU() == null || mUsers.get(position).getU().equals("") || mUsers.get(position).getU().equals("null"))
            holder.profilePhoto.setImageURI(Uri.parse("http://flathash.com/" + mUsers.get(position).getE() + ".png"));
        else
            holder.profilePhoto.setImageURI(Uri.parse(mUsers.get(position).getU()));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void datasetchanged() {
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        private SimpleDraweeView profilePhoto;
        private TextView profileName;
        private ImageButton discardRequest, acceptRequest;


        public ViewHolder(View v) {
            super(v);
            profilePhoto = v.findViewById(R.id.contact_photo);
            profileName = v.findViewById(R.id.contact_name);
            discardRequest = v.findViewById(R.id.discard_request);
            acceptRequest = v.findViewById(R.id.accept_request);
            acceptRequest.setOnClickListener(this);
            discardRequest.setOnClickListener(this);
            v.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (!(mUsers.get(getAdapterPosition()).getS().equals("f"))) {
                if (view.getId() == R.id.accept_request) {
                    realtimeDB.acceptFriend(mUsers.get(getAdapterPosition()), ContactsAdapter.this, Constants.FRIEND_REQUEST_RESPONSE);
                    view.setVisibility(View.GONE);
                } else if (view.getId() == R.id.discard_request) {
                    realtimeDB.deleteFriend(mUsers.get(getAdapterPosition()), ContactsAdapter.this, Constants.FRIEND_REQUEST_RESPONSE);
                    view.setVisibility(View.GONE);
                }
            } else {
                launchDrawActivity.putExtra("phone", mUsers.get(getAdapterPosition()).getP());
                launchDrawActivity.putExtra("name", mUsers.get(getAdapterPosition()).getN());
                launchDrawActivity.putExtra("photo", mUsers.get(getAdapterPosition()).getU());
                mcontext.startActivity(launchDrawActivity);
                ((AppCompatActivity) mcontext).finish();
            }

        }


    }

}