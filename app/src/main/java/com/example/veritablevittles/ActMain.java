//====================================================================
//
// Application: Veritable Vittles
// Activity:    ActMain
// Description:
//   This Android application allows customers to submit a dinner reservation.
//
//====================================================================
package com.example.veritablevittles;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.widget.Toolbar;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;

//--------------------------------------------------------------------
// class ActMain
//--------------------------------------------------------------------
public class ActMain extends AppCompatActivity implements TextToSpeech.OnInitListener {

    //----------------------------------------------------------------
    // Constants and variables
    //----------------------------------------------------------------
    public static final String APP_NAME = "Veritable Vittles";
    public static final String APP_VERSION = "1.0";

    //Declare other constants
    public static String SEATING_TYPES_FILE = "InternalData.txt";
    public static final String WAITING_MESSAGE = "Waiting ... ";
    public static final String READY_MESSAGE = "Reservation ready!";
    public final String PARTY_NAME_DEFAULT = " ";
    public final String NUMBER_OF_PEOPLE_DEFAULT = " ";
    public final String OCCASION_DEFAULT = " ";
    public final String WAITING_TIME_DEFAULT = " ";
    public final int SEATING_TYPE_DEFAULT = 0;
    public final int MAX_WAITING_TIME = 40;
    public final int MIN_WAITING_TIME = 20;


    //Declare other Variables
    private Toolbar tbrMain;
    private EditText txtPartyName;
    private EditText txtNumberOfPeople;
    private EditText txtSeatingTypes;
    private EditText txtOccasion;
    private TextToSpeech vs;
    private Voice voice;
    private static EditText txtWaitingTime;
    private static TextView txtReservationReady;
    private Spinner spSeatingTypes;
    private static ToggleButton tbtAction;
    private static Timer timer;
    private static Button btnSpeak;
    private ArrayList<String> seatingTypes = new ArrayList<>();
    private AlertDialog.Builder builder;
    private String lastPartyName;
    private String lastNumberOfPeople;
    private String lastSeatingType;
    private String lastOccasion;
    private static boolean submitMode;
    private String message;
    private float pitch = 1.0f;
    private float rate = 1.0f;

    //----------------------------------------------------------------
    // onCreate
    //----------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laymain);

        //Define and connect to toolbar
        tbrMain = findViewById(R.id.tbrMain);
        setSupportActionBar(tbrMain);
        tbrMain.setNavigationIcon(R.mipmap.ic_launcher_new);

        //Connect to controls
        txtPartyName = findViewById(R.id.txtPartyName);
        txtNumberOfPeople = findViewById(R.id.txtNumberOfPeople);
        txtSeatingTypes = findViewById(R.id.txtSeatingTypes);
        txtOccasion = findViewById(R.id.txtOccasion);
        txtWaitingTime = findViewById(R.id.txtWaitingTime);
        txtReservationReady = findViewById(R.id.txtReservationReady);
        spSeatingTypes = findViewById(R.id.spSeatingTypes);
        tbtAction = findViewById(R.id.tbtAction);
        btnSpeak = findViewById(R.id.btnSpeak);
        writeInternalFile(findViewById((android.R.id.content)));
        readInternalFile();

        //Define seating type spinner adapter and listener
       ArrayAdapter<String> spAdapterSeatingTypes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,seatingTypes);
       spAdapterSeatingTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       spSeatingTypes.setAdapter(spAdapterSeatingTypes);
       spSeatingTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               txtSeatingTypes.setText(seatingTypes.get(position));
               Shared.Data.sharedSeatingType = seatingTypes.get(position);
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

       //Initialize variables
        submitMode = false;

    }

    //----------------------------------------------------------------
    // writeInternalFile
    //----------------------------------------------------------------
    public void writeInternalFile(View v)
    {
        //Declare variables
        FileOutputStream fileOut = null;
        PrintStream streamOut = null;
        Random rand = new Random();

        // Attempt to write internal file
        try
        {

            // Open output file
            fileOut =
                    openFileOutput(SEATING_TYPES_FILE, MODE_PRIVATE);
            streamOut = new PrintStream(fileOut);

            // Write lines to output file
            streamOut.println("Seating type");
            streamOut.println("Interior booth");
            streamOut.println("Interior table");
            streamOut.println("Patio");
            streamOut.println("Private Room");
            streamOut.println("Rooftop");
            streamOut.println("Sidewalk");
            streamOut.println("Window table");

            // Close output files
            fileOut.close();
            streamOut.close();

        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(),
                    "Error creating or writing to input file '" +
                            SEATING_TYPES_FILE + "'.",
                    Toast.LENGTH_LONG).show();
        }

    }

    //----------------------------------------------------------------
    // readInternalFile
    //----------------------------------------------------------------
    public void readInternalFile()
    {
        //Declare variables
        FileInputStream fileString = null;
        Scanner streamString = null;
        String line = "";

        //Attempt to read internal file
        try
        {
            //open input file
            fileString = openFileInput(SEATING_TYPES_FILE);
            streamString = new Scanner(fileString);

            //loop to read lines from input file
            while(streamString.hasNextLine())
            {
                line = streamString.nextLine();
                seatingTypes.add(line);
            }

            //Close input file
            fileString.close();


        }
        catch (IOException e)
        {

        }
    }

    //----------------------------------------------------------------
    // Change reservation action
    //----------------------------------------------------------------
    public void changeReservationAction(View view)
    {

        submitMode = !submitMode;

        //Set the current mode action
        if(submitMode)
        {
            //Generate a waiting time
            Random rand = new Random();
            int waitingTime = rand.nextInt((MAX_WAITING_TIME-MIN_WAITING_TIME)+1)+MIN_WAITING_TIME;
            Shared.Data.sharedWaitingTime = waitingTime;
            txtWaitingTime.setText(String.valueOf(Shared.Data.sharedWaitingTime));

            //Cancel timer if exists
            if(timer != null)
                timer.cancel();

            //Schedule a time task
            timer = new Timer();
            timer.schedule(new Task(), 0, 1000);

            //Enable speak button
            btnSpeak.setEnabled(true);

            //Save input to shared preference
            Shared.Data.sharedPartyName = txtPartyName.getText().toString();
            Shared.Data.sharedNumberOfPeople = txtNumberOfPeople.getText().toString();
            Shared.Data.sharedOccasion = txtOccasion.getText().toString();
            lastPartyName = Shared.Data.sharedPartyName;
            lastNumberOfPeople = Shared.Data.sharedNumberOfPeople;
            lastSeatingType = Shared.Data.sharedSeatingType;
            lastOccasion = Shared.Data.sharedOccasion;

            //Show toast message that reservation was submitted
            Toast.makeText(getApplicationContext(),"Reservation submitted.",Toast.LENGTH_LONG).show();

        }
        else
        {
            //Cancel timer if exists
            if(timer != null)
                timer.cancel();
            timer = null;

            Shared.Data.sharedWaitingTime = 0;
            txtWaitingTime.setText(WAITING_TIME_DEFAULT);

            //Disable speak button
            btnSpeak.setEnabled(false);

            //Hide "Reservation ready!" label
            txtReservationReady.setVisibility(View.INVISIBLE);
        }
    }

    //----------------------------------------------------------------
    // Recall reservation
    //----------------------------------------------------------------
    public void recallReservation(View view)
    {
        //Create a dialog box
        builder = new AlertDialog.Builder((view.getContext()));
        builder.setTitle(APP_NAME + " ( " + APP_VERSION + " ) " + "Message");
        builder.setMessage("Recall last reservation?");

        //Define "Yes" response
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Recall last reservation
                txtPartyName.setText(lastPartyName);
                txtNumberOfPeople.setText(lastNumberOfPeople);
                spSeatingTypes.setSelection(seatingTypes.indexOf(lastSeatingType));
                txtOccasion.setText(lastOccasion);
                Shared.Data.sharedPartyName = lastPartyName;
                Shared.Data.sharedNumberOfPeople = lastNumberOfPeople;
                Shared.Data.sharedSeatingType = lastSeatingType;
                Shared.Data.sharedOccasion = lastOccasion;
            }
        });

        //Define "No" response
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Last reservation NOT recalled.", Toast.LENGTH_LONG).show();
            }
        });

        //Show dialog box
        builder.show();

    }

    //----------------------------------------------------------------
    // Reset reservation
    //----------------------------------------------------------------
    public void resetReservation(View view)
    {
        //Create a dialog box
        builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(APP_NAME + " ( " + APP_VERSION + " ) " + "Message");
        builder.setMessage("Reset the reservation?");

        //Define "Yes" response
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Reset the screen
                txtPartyName.setText(PARTY_NAME_DEFAULT);
                txtNumberOfPeople.setText(NUMBER_OF_PEOPLE_DEFAULT);
                spSeatingTypes.setSelection(SEATING_TYPE_DEFAULT);
                txtOccasion.setText(OCCASION_DEFAULT);
                txtWaitingTime.setText(WAITING_TIME_DEFAULT);
                txtReservationReady.setVisibility(View.INVISIBLE);

                //Cancel timer if exists
                if(timer != null)
                    timer.cancel();
                timer = null;

                //Change action mode
                tbtAction.setChecked(true);
                submitMode = false;
            }
        });

        //Define "No" response
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Reservation NOT reset.", Toast.LENGTH_LONG).show();
            }
        });

        //Show dialog box
        builder.show();

    }

    //----------------------------------------------------------------
    // timerTaskHandler
    //----------------------------------------------------------------
    public static Handler timerTaskHandler =
            new Handler(Looper.getMainLooper())
    {
        //------------------------------------------------------------
        // handleMessage
        //------------------------------------------------------------
        @Override
        public void handleMessage(Message msg)
        {
            txtWaitingTime.setText(String.valueOf(Shared.Data.sharedWaitingTime));
            if(Shared.Data.sharedWaitingTime == 0)
            {
                //Cancel timer if exists
                if(timer != null)
                    timer.cancel();
                timer = null;

                //Change button action
                tbtAction.setChecked(true);
                submitMode = false;


                //Disable speak button
                btnSpeak.setEnabled(false);

                //Display reservation ready message
                txtReservationReady.setVisibility(View.VISIBLE);

            }
        }
    };

    //----------------------------------------------------------------
    // synthesizeVoice
    //----------------------------------------------------------------
    public void speakWaitingTime(View view)
    {
        message = Shared.Data.sharedWaitingTimeMessage;
        vs = new TextToSpeech(this, this);
    }

    //----------------------------------------------------------------
    // onInit
    // Implemented from interface TextToSpeech.OnInitListener
    //----------------------------------------------------------------
    @Override
    public void onInit(int status)
    {
        if(vs != null)
        {
            vs.setPitch(pitch);
            vs.setSpeechRate(rate);
            vs.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
            vs = null;
        }
    }

}