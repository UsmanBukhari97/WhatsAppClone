package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class WhatsAppChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView chatListView;
    //all of the chats
    private ArrayList<String> chatsList;
    private ArrayAdapter adapter;
    //bcoz we r going to get users list from whatsappusers activity
    private String selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_chat);


        selectedUser = getIntent().getStringExtra("selectedUser");
        FancyToast.makeText(this, "Chat with " + selectedUser + " Now!!!", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();

        //means that button is implementing that method.
        //we can call it like this too
        findViewById(R.id.btnSend).setOnClickListener(this);

        chatListView = findViewById(R.id.chatListView);
        chatsList = new ArrayList();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, chatsList);
        //setting the adapter
        //here in oncreate bcoz implementin chat activity
        chatListView.setAdapter(adapter);

        try {
            ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chat");
            ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chat");

            //sender to recipient
            firstUserChatQuery.whereEqualTo("waSender", ParseUser.getCurrentUser().getUsername());
            firstUserChatQuery.whereEqualTo("waTargetRecipient", selectedUser);
            //recipient to sender
            secondUserChatQuery.whereEqualTo("waSender", selectedUser);
            secondUserChatQuery.whereEqualTo("waTargetRecipient", ParseUser.getCurrentUser().getUsername());


            ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
            //adding first and second user chat queries
            allQueries.add(firstUserChatQuery);
            allQueries.add(secondUserChatQuery);

            //or: acepts array list of parse query objects
            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);
            //old message will be at the top. ordering messages. passing in createdAt.
            myQuery.orderByAscending("createdAt");

            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {

                        for (ParseObject chatObject : objects) {

                            String waMessage = chatObject.get("waMessage") + "";

                            //didnt use if else only if if becase we want both these conditions to come true
                            if (chatObject.get("waSender").equals(ParseUser.getCurrentUser().getUsername())) {



                                //Sender: Message. it will show like this
                                waMessage = ParseUser.getCurrentUser().getUsername() + ": " + waMessage;
                            }
                            if (chatObject.get("waSender").equals(selectedUser)) {


                                waMessage = selectedUser + ": " + waMessage;
                            }

                            //assigning message to array list
                            chatsList.add(waMessage);
                        }
                        //calling adapter after forloop.goodd practice
                        adapter.notifyDataSetChanged();


                    }
                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {


        //more memory friendly to create variable here
        final EditText edtMessage = findViewById(R.id.edtSend);

        ParseObject chat = new ParseObject("Chat");
        chat.put("waSender", ParseUser.getCurrentUser().getUsername());
        chat.put("waTargetRecipient", selectedUser);
        chat.put("waMessage", edtMessage.getText().toString());
        chat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    FancyToast.makeText(WhatsAppChatActivity.this, "Message from " + ParseUser.getCurrentUser().getUsername() + " sent to " + selectedUser, Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                    //we are going to add the message from sender to recipient. eg: John: hi!
                    chatsList.add(ParseUser.getCurrentUser().getUsername() + ": " + edtMessage.getText().toString());
                    //update the list view
                    adapter.notifyDataSetChanged();
                    //when we click on send message we want that text to be gone and show the edit text empty
                    edtMessage.setText("");
                }
            }
        });

    }

}
