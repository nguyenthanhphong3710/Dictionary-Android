package com.example.dictionary;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.CursorAdapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Trace;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    SearchView search;
    static DatabaseHelper myDbHelper;
    static Boolean databaseOpened = false;
    androidx.cursoradapter.widget.SimpleCursorAdapter suggestionAdapter;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        search = findViewById(R.id.search_view);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setIconified(false);

            }
        });

        myDbHelper = new DatabaseHelper(this);
        if(myDbHelper.checkDataBase()){
            openDatabase();
        }
        else {
            LoadDatabaseAsync task = new LoadDatabaseAsync(MainActivity.this);
            task.execute();
        }


        final String[] from = new String[] {"en_words"};
        final int[] to = new int[] {R.id.suggestion_text};
        suggestionAdapter = new androidx.cursoradapter.widget.SimpleCursorAdapter(MainActivity.this,
                R.layout.suggestion_row, null, from, to, 0) {
            @Override
            public void changeCursor(Cursor cursor){
                super.swapCursor(cursor);
            }
        };
        search.setSuggestionsAdapter(suggestionAdapter);
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                CursorAdapter ca = search.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(position);
                String clicked_word = cursor.getString(cursor.getColumnIndex("en_words"));
                search.setQuery(clicked_word, false);


                search.clearFocus();
                search.setFocusable(false);
                Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("en_words", clicked_word);
                intent.putExtras(bundle);
                startActivity(intent);

                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                return true;
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String text = search.getQuery().toString();
                Cursor c = myDbHelper.getMeaning(text);
                if(c.getCount()==0)
                {
                    search.setQuery("",false);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                    builder.setTitle("Word Not Found");
                    builder.setMessage("Please search again");

                    String positiveText = getString(android.R.string.ok);
                    builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    String negativeText = getString(android.R.string.cancel);
                    builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            search.clearFocus();;
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    search.clearFocus();
                    search.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_words", text);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                search.setIconifiedByDefault(false);
                Cursor cursorSuggestion = myDbHelper.getSuggestions(s);
                suggestionAdapter.changeCursor(cursorSuggestion);
                return false;
            }
        });





    }

    protected static void openDatabase() {
        try {
            myDbHelper.openDataBase();
            databaseOpened=true;
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
       int id = item.getItemId();
       if(id==R.id.action_settings){
           Intent intent = new Intent(MainActivity.this, SettingActivity.class);
           startActivity(intent);
           return true;
       }
       if(id == R.id.action_exit)
       {
           System.exit(0);
           return true;
       }
        return super.onOptionsItemSelected(item);
    }
}