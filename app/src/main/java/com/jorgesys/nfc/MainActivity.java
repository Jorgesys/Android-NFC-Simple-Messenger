package com.jorgesys.nfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements NfcAdapter.OnNdefPushCompleteCallback,
        NfcAdapter.CreateNdefMessageCallback {

        //The array lists to hold our messages
        private ArrayList<String> messagesToSendArray = new ArrayList<>();
        private ArrayList<String> messagesReceivedArray = new ArrayList<>();

        //Text boxes to add and display our messages
        private EditText txtBoxAddMessage;
        private TextView txtReceivedMessages;
        private TextView txtMessagesToSend;

    private NfcAdapter mNfcAdapter;

    public void addMessage(View view) {
        String newMessage = txtBoxAddMessage.getText().toString();
        messagesToSendArray.add(newMessage);

        txtBoxAddMessage.setText(null);
        updateTextViews();

        Toast.makeText(this, "Added Message", Toast.LENGTH_LONG).show();
    }


    private  void updateTextViews() {
        txtMessagesToSend.setText("Messages To Send:\n");
        //Populate Our list of messages we want to send
        if(messagesToSendArray.size() > 0) {
            for (int i = 0; i < messagesToSendArray.size(); i++) {
                txtMessagesToSend.append(messagesToSendArray.get(i));
                txtMessagesToSend.append("\n");
            }
        }

        txtReceivedMessages.setText("Messages Received:\n");
        //Populate our list of messages we have received
        if (messagesReceivedArray.size() > 0) {
            for (int i = 0; i < messagesReceivedArray.size(); i++) {
                txtReceivedMessages.append(messagesReceivedArray.get(i));
                txtReceivedMessages.append("\n");
            }
        }
    }

    //Save our Array Lists of Messages for if the user navigates away
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("messagesToSend", messagesToSendArray);
        savedInstanceState.putStringArrayList("lastMessagesReceived",messagesReceivedArray);
    }

    //Load our Array Lists of Messages for when the user navigates back
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messagesToSendArray = savedInstanceState.getStringArrayList("messagesToSend");
        messagesReceivedArray = savedInstanceState.getStringArrayList("lastMessagesReceived");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtBoxAddMessage = (EditText) findViewById(R.id.txtBoxAddMessage);
        txtMessagesToSend = (TextView) findViewById(R.id.txtMessageToSend);
        txtReceivedMessages = (TextView) findViewById(R.id.txtMessagesReceived);
        Button btnAddMessage = (Button) findViewById(R.id.buttonAddMessage);

        btnAddMessage.setText("Add Message");
        updateTextViews();

        //Check if NFC is available on device
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            //Handle some NFC initialization here
        }
        else {
            Toast.makeText(this, "NFC not available on this device",
                    Toast.LENGTH_SHORT).show();
        }


        //Check if NFC is available on device
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            //This will refer back to createNdefMessage for what it will send
            mNfcAdapter.setNdefPushMessageCallback(this, this);

            //This will be called if the message is sent successfully
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }

    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        //This will be called when another NFC capable device is detected.
        if (messagesToSendArray.size() == 0) {
            return null;
        }
        //We'll write the createRecords() method in just a moment
        NdefRecord[] recordsToAttach = createRecords();
        //When creating an NdefMessage we need to provide an NdefRecord[]
        return new NdefMessage(recordsToAttach);
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        //This is called when the system detects that our NdefMessage was
        //Successfully sent.
        messagesToSendArray.clear();
    }

    public NdefRecord[] createRecords() {

        NdefRecord[] records = new NdefRecord[messagesToSendArray.size()];

        for (int i = 0; i < messagesToSendArray.size(); i++){

            byte[] payload = messagesToSendArray.get(i).
                    getBytes(Charset.forName("UTF-8"));

            NdefRecord record = new NdefRecord(
                    NdefRecord.TNF_WELL_KNOWN,  //Our 3-bit Type name format
                    NdefRecord.RTD_TEXT,        //Description of our payload
                    new byte[0],                //The optional id for our Record
                    payload);                   //Our payload for the Record

            records[i] = record;
        }
        return records;
    }
}