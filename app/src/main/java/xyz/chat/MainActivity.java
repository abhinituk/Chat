package xyz.chat;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private final String LOG_TAG= getClass().getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mEditText;
    private Button mSendButton;
    private ProgressBar mProgressBar;
    private GoogleApiClient mGoogleApiClient;
    private String mUsername;
    private String mPhotoUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the user name by default to anonymous
        mUsername= "Anonymous";


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

        //Set layout manager with recycler view
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //Set the visibility of progress bar invisible
        mProgressBar.setVisibility(View.INVISIBLE);



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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Adding the sign out button to overflow menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(LOG_TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
