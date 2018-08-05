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

import java.util.List;

import leotik.labs.gesturemessenger.Activities.DrawActivity;
import leotik.labs.gesturemessenger.POJO.UserPOJO;
import leotik.labs.gesturemessenger.R;
import leotik.labs.gesturemessenger.Util.DatabaseHelper;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private static List<UserPOJO> mUsers;
    private static Context mcontext;
    private static Intent launchDrawActivity;
    private static DatabaseHelper databaseHelper;


    public ContactsAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context);
        mUsers = databaseHelper.getAllUsers();
        launchDrawActivity = new Intent(context, DrawActivity.class);
        mcontext = context;
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
        if (mUsers.get(position).getU() != null || mUsers.get(position).getU().equals(""))
            holder.profilePhoto.setImageURI(Uri.parse("http://flathash.com/" + mUsers.get(position).getE() + ".png"));
        else
            holder.profilePhoto.setImageURI(Uri.parse(mUsers.get(position).getU()));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void updateContacts() {
        mUsers = databaseHelper.getAllUsers();
        notifyDataSetChanged();
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
            launchDrawActivity.putExtra("email", mUsers.get(getAdapterPosition()).getE());
            mcontext.startActivity(launchDrawActivity);


        }

    }

}