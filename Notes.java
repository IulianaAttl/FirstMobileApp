package com.example.notesapp;

import static com.example.notesapp.Welcome.MESSAGE_KEY;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


//notes activity
public class Notes extends AppCompatActivity{

    //declare variables
    private ArrayList<Note> notes;
    private RecyclerView recyclerView;
    public MyAdapter adapter;
    DatabaseReference reference;
    String userID;
    private MyAdapter.NoteClickListener listener;
    private String editNoteKey;
    private Note editNote;
    private ArrayList<String> notekeys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        //get the user id
        Intent intent = getIntent();
        userID = intent.getStringExtra(MESSAGE_KEY);

        //get the recycler view object
        recyclerView = (RecyclerView) findViewById(R.id.myrecyclerview);

        //do not change the recycler view size
        recyclerView.setHasFixedSize(true);

        //use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        //create the list
        notes = new ArrayList<>();

        //call the on click listener method
        setOnClickListener();

        //specify the adapter
        adapter = new MyAdapter(notes, getApplicationContext(), listener);

        //set the adapter to the recycler view
        recyclerView.setAdapter(adapter);

        //add the line to divide the items
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //get the database reference to the user's notes
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Notes");
    }

    //method to add a listener for when a user clicks on a note
    private void setOnClickListener() {
        notekeys = new ArrayList<>();
        DatabaseReference newreference;
        newreference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Notes");

        newreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dss : snapshot.getChildren()){
                    notekeys.add(String.valueOf(dss.getKey()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        //get the position of the note the user clicks and save the note info
        listener = new MyAdapter.NoteClickListener() {
            @Override
            public void onNoteClick(View v, int position) {
                editNoteKey = notekeys.get(position);
                editNote = notes.get(position);
            }
        };
    }

    //show options menu on top
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //method if any of the option menu items is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //if the logout option is selected go back to welcome activity
        if(id == R.id.logoutoption){
            Toast.makeText(Notes.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Welcome.class);
            startActivity(intent);
        }
        //if the edit icon is selected
        else if(id == R.id.editnoteoption){
            //if the user did not select a note before clicking the edit icon
            if(editNote == null){
                //create the dialog box
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Notes.this);
                alertDialog.setTitle("Error");

                //layout for displaying the text fields vertically
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                //create a message textview
                TextView message = new TextView(this);

                //set the message the information for the user to first select a note
                message.setText("You must first click on a note and then the edit icon.");

                //add the text fields to the layout
                layout.addView(message);

                //add the layout to the dialog box
                alertDialog.setView(layout);

                //create the ok button on the dialog box and add a listener
                alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //display message
                        Toast.makeText(Notes.this, "Thank You", Toast.LENGTH_SHORT).show();
                    }
                });
                //show the dialog box
                alertDialog.show();
            }
            //if the user does select a note and then clicks on the edit icon
            else {
                //create the dialog box
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Notes.this);
                alertDialog.setTitle("Edit note");
                alertDialog.setIcon(R.drawable.ic_edit);

                //layout for displaying the text fields vertically
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                //create the input text fields
                EditText notetitletext = new EditText(this);
                EditText notetagtext = new EditText(this);
                EditText noteinfotext = new EditText(this);

                //set the value of the textfields to the values from the note the user selected
                notetitletext.setText(editNote.getTitle());
                notetagtext.setText(editNote.getTag());
                noteinfotext.setText(editNote.getInfo());

                //add the text fields to the layout
                layout.addView(notetitletext);
                layout.addView(notetagtext);
                layout.addView(noteinfotext);

                //add the layout to the dialog box
                alertDialog.setView(layout);

                //create the add button on the dialog box and add a listener
                alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //get the information from the text fields
                        String notetitle = notetitletext.getText().toString();
                        String notetag = notetagtext.getText().toString();
                        String noteinfo = noteinfotext.getText().toString();

                        //get the current date and time
                        Calendar calendar;
                        SimpleDateFormat dateFormat;
                        calendar = Calendar.getInstance();

                        //set the current date and time to variables
                        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        String date = "Date: " + String.valueOf(dateFormat.format(calendar.getTime()));
                        dateFormat = new SimpleDateFormat("h:mm a");
                        String time = "Time: " + String.valueOf(dateFormat.format(calendar.getTime()));

                        //create a note with the new information entered by the user
                        Note note = new Note(notetitle, noteinfo, notetag, date, time);

                        //crete a new reference to the database and save all the values
                        DatabaseReference newreference;
                        newreference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Notes").child(editNoteKey);
                        newreference.child("date").setValue(date);
                        newreference.child("info").setValue(noteinfo);
                        newreference.child("tag").setValue(notetag);
                        newreference.child("time").setValue(time);
                        newreference.child("title").setValue(notetitle);

                        editNote = null;
                        //display message
                        Toast.makeText(Notes.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    }
                });

                //create the cancel button on the dialog box and add a listener
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //display message
                        Toast.makeText(Notes.this, "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                //show the dialog box
                alertDialog.show();
            }
        }
        //if the user selects the delete icon
        else if(id == R.id.deleteoption){
            //if the user does not select a note before clicking the icon
            if(editNote == null){
                //create the dialog box
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Notes.this);
                alertDialog.setTitle("Error");

                //layout for displaying the text fields vertically
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                //create a message field informing the user to first select a note
                TextView message = new TextView(this);
                message.setText("You must first click on a note and then the delete icon.");

                //add the text fields to the layout
                layout.addView(message);

                //add the layout to the dialog box
                alertDialog.setView(layout);

                //create the ok button on the dialog box and add a listener
                alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //display message
                        Toast.makeText(Notes.this, "Thank You", Toast.LENGTH_SHORT).show();
                    }
                });
                //show the dialog box
                alertDialog.show();
            }
            //if the user does select a note before clicking the icon
            else {
                //create a new database reference to the key of the note selected
                DatabaseReference newreference;
                newreference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Notes").child(editNoteKey);

                //remove the note at this key
                newreference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dss : snapshot.getChildren()){
                            dss.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

                editNote = null;
                //display message
                Toast.makeText(Notes.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
            }
        }
        //if the user selects the edit profile option
        else if(id == R.id.editprofileoption){
            //create the dialog box
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Notes.this);
            alertDialog.setTitle("Edit profile");
            alertDialog.setIcon(R.drawable.ic_edit);

            //layout for displaying the text fields vertically
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            //create the input text fields
            EditText username = new EditText(this);
            EditText userphone = new EditText(this);

            //create a new database reference
            DatabaseReference newreference;
            newreference = FirebaseDatabase.getInstance().getReference().child("Users");

            //get the name of the user
            newreference.child(userID).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        //display the name of the user on the text box
                        username.setText(String.valueOf(task.getResult().getValue()));
                    }
                    else{
                        //display error
                        Toast.makeText(Notes.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //get the phone number of the user
            newreference.child(userID).child("phone").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        //display the phone number of the user on the text box
                        userphone.setText(String.valueOf(task.getResult().getValue()));
                    }
                    else{
                        //display error
                        Toast.makeText(Notes.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //add the text fields to the layout
            layout.addView(username);
            layout.addView(userphone);

            //add the layout to the dialog box
            alertDialog.setView(layout);

            //create the save button on the dialog box and add a listener
            alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    //get the information from the text fields
                    String name = username.getText().toString();
                    String phone = userphone.getText().toString();

                    //update the values in the database
                    newreference.child(userID).child("phone").setValue(phone);
                    newreference.child(userID).child("name").setValue(name);

                    //display message
                    Toast.makeText(Notes.this, "User details updated successfully", Toast.LENGTH_SHORT).show();
                }
            });

            //create the cancel button on the dialog box and add a listener
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //display message
                    Toast.makeText(Notes.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            });
            //show the dialog box
            alertDialog.show();
        }
        //if the user selects the add note option
        else if(id == R.id.addoption){
            //create the dialog box
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Notes.this);
            alertDialog.setTitle("Add a note");
            alertDialog.setIcon(R.drawable.ic_add);

            //layout for displaying the text fields vertically
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            //create the input text fields
            EditText notetitle = new EditText(this);
            EditText notetag = new EditText(this);
            EditText noteinfo = new EditText(this);

            //add hints to the text fields
            notetitle.setHint("Title");
            notetag.setHint("tag");
            noteinfo.setHint("Contents");

            //add the text fields to the layout
            layout.addView(notetitle);
            layout.addView(notetag);
            layout.addView(noteinfo);

            //add the layout to the dialog box
            alertDialog.setView(layout);

            //create the add button on the dialog box and add a listener
            alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    //get the information from the text fields
                    String title = "Title: " + notetitle.getText().toString();
                    String tag = "Tag: " + notetag.getText().toString();
                    String info = "Contents: " + noteinfo.getText().toString();

                    //if everything is completed
                    //get the current date and time
                    Calendar calendar;
                    SimpleDateFormat dateFormat;
                    calendar = Calendar.getInstance();

                    //set the current date and time to the variables
                    dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String date = "Date: " + String.valueOf(dateFormat.format(calendar.getTime()));
                    dateFormat = new SimpleDateFormat("h:mm a");
                    String time = "Time: " + String.valueOf(dateFormat.format(calendar.getTime()));

                    //generate an id to the note
                    String id = reference.push().getKey();

                    //create a new note with the information provided
                    Note note = new Note(title, info, tag, date, time);

                    //add the note to the database
                    reference.child(id).setValue(note);
                }
            });
            //create the cancel button on the dialog box and add a listener
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //display message
                    Toast.makeText(Notes.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            });
            //show the dialog box
            alertDialog.show();
        }
        return true;
    }

    //on start method
    @Override
    protected void onStart() {
        super.onStart();
        //create new database reference
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Notes");

        //get the values from the database and add it to the arraylist
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notes.clear();
                //snapshots are the data from the database - if there is data in the database
                if(snapshot.exists()){
                    for(DataSnapshot dss: snapshot.getChildren()){
                        //create a note with the information
                        Note note = dss.getValue(Note.class);
                        //add the note to the arraylist
                        notes.add(note);
                    }
                    //notify that the data has changed
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}