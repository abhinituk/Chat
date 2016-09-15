package xyz.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private final String LOG_TAG= getClass().getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mEditText;
    private Button mSendButton;
    private ProgressBar mProgressBar;
    private GoogleApiClient mGoogleApiClient;
    private String mUsername;
    private Context mContext;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<MessageFormat, MessageViewHolder>
            mFirebaseAdapter;

    private Boolean mAppBackground;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;

        //Set the user name by default to anonymous
        mUsername= "Anonymous";

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // If the user is not signed in then launch the Sign In activity
            startActivity(new Intent(this, SignIn.class));
            finish();
            return;
        } else {
            //If the user is signed in then get the user display name
            mUsername = mFirebaseUser.getDisplayName();
        }



        // Build a GoogleApiClient with access to the Google Sign-In API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();



        //Initialize views present in activity_main.xml
        mRecyclerView= (RecyclerView) findViewById(R.id.messageBox);
        mEditText = (EditText) findViewById(R.id.chatBox);
        mSendButton= (Button) findViewById(R.id.send);
        mProgressBar= (ProgressBar) findViewById(R.id.progressBar);

        //Initialize the layout manager
        mLinearLayoutManager= new LinearLayoutManager(this);

        //When stack from end is set to true,
        // the list fills its content starting from the bottom of the view.
        mLinearLayoutManager.setStackFromEnd(true);



        mFirebaseDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter= new FirebaseRecyclerAdapter<MessageFormat, MessageViewHolder>(
                MessageFormat.class,
                R.layout.message_format,
                MessageViewHolder.class,
                //The Firebase location to watch for data changes.
                mFirebaseDatabaseReference.child("messages"))

        {
            @Override
            protected void populateViewHolder(MessageViewHolder messageViewHolder, MessageFormat messageFormat, int i) {
                //Set the visibility of progress bar invisible
                mProgressBar.setVisibility(View.INVISIBLE);

                //Getting the message from the message format
                messageViewHolder.messageTextView.setText(messageFormat.getMessage());
                messageViewHolder.messengerTextView.setText(messageFormat.getName());
            }
        };

        //Register a new observer to listen for data changes.
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mRecyclerView.scrollToPosition(positionStart);
                }
            }
        });


        //Set the layout manager
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //set adapter to recycler view
        mRecyclerView.setAdapter(mFirebaseAdapter);



        //Enabling the send button only when user has typed something otherwise send button is disabled.
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Perform the particular action whenever the send button is clicked.
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Perform the action here
                MessageFormat messageFormat= new MessageFormat(mUsername,mEditText.getText().toString());
                mFirebaseDatabaseReference.child("messages").push().setValue(messageFormat);
                mFirebaseDatabaseReference.keepSynced(true);
                mEditText.setText("");
            }
        });
    }

    //Creating ViewHolder class for recycler view
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        }
    }




    //Adding the sign out button to overflow menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //Handling sign out button
    //When the user signs out then launch the sign in activity
    //and set the user name to "anonymous"
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = "Anonymous";
                startActivity(new Intent(this, SignIn.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(LOG_TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    //Check for new messages whenever the app is in background
    public void checkNewMessages()
    {
        mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //When the app is in background then get the push notification whenever new message arrives
                //if(Utility.isAppIsInBackground(mContext))


            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (mAppBackground)
                {
//                    Log.v(LOG_TAG,s);
//                    MessageFormat messageFormat= dataSnapshot.getValue(MessageFormat.class);
//                    String message = messageFormat.getMessage();
//                    String name = messageFormat.getName();
//                    Log.v(LOG_TAG,message);
//                    Log.v(LOG_TAG,name);
                    Intent intent = new Intent(mContext,MyIntentService.class);
                    intent.putExtra("Message",dataSnapshot.toString());
                    startService(intent);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppBackground=true;
        checkNewMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppBackground=false;
    }
}
