package com.ugo.android.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.ugo.android.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.ugo.android.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.ugo.android.notekeeper.R.string.close_navigation_drawer;
import static com.ugo.android.notekeeper.R.string.open_navigation_drawer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView recyclerItems;
    private LinearLayoutManager notesLayoutManager;
    private GridLayoutManager coursesLayoutManager;
    private List<NoteInfo> notes;
    private List<CourseInfo> courses;
    private NoteRecyclerAdapter noteRecyclerAdapter;
    private CourseRecyclerAdapter courseRecyclerAdapter;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Menu menu;
    private final int SPAN_COUNT = 2;
    private NoteKeeperOpenHelper mDbOpenHelper;
    private static final int LOADER_NOTES = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDbOpenHelper = new NoteKeeperOpenHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.general_preferences, Boolean.FALSE);
        PreferenceManager.setDefaultValues(this, R.xml.messages_preferences, Boolean.FALSE);
        PreferenceManager.setDefaultValues(this, R.xml.sync_preferences, Boolean.FALSE);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, open_navigation_drawer,
                close_navigation_drawer);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoaderManager.getInstance(this).restartLoader(LOADER_NOTES, null, this);
        updateNavHeader();
    }

    private void loadNotes() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        final String[] noteColumns = {
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry._ID};
        String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + "," + NoteInfoEntry.COLUMN_NOTE_TITLE;
        final Cursor noteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns, null,
                null, null, null, noteOrderBy);

        noteRecyclerAdapter.changeCursor(noteCursor);
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userNameText = headerView.findViewById(R.id.text_user_name);
        TextView emailText = headerView.findViewById(R.id.text_email_address);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String username = pref.getString("displayName", "");
        String email = pref.getString("email", "");

        userNameText.setText(username);
        emailText.setText(email);
    }

    private void initializeDisplayContent() {
        DataManager.loadFromDatabase(mDbOpenHelper);
        recyclerItems = findViewById(R.id.list_items);
        notesLayoutManager = new LinearLayoutManager(this);
        coursesLayoutManager = new GridLayoutManager(this, SPAN_COUNT);

        notes = DataManager.getInstance().getNotes();
        noteRecyclerAdapter = new NoteRecyclerAdapter(this, null);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        courseRecyclerAdapter = new CourseRecyclerAdapter(this, courses);
        displayNotes();
    }

    private void displayNotes() {
        recyclerItems.setLayoutManager(notesLayoutManager);
        recyclerItems.setAdapter(noteRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_notes, R.id.nav_courses);
    }

    private void displayCourses() {
        recyclerItems.setLayoutManager(coursesLayoutManager);
        recyclerItems.setAdapter(courseRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_courses, R.id.nav_notes);
    }

    private void selectNavigationMenuItem(int idActive, int idInactive) {
        menu = navigationView.getMenu();
        menu.findItem(idActive).setChecked(Boolean.TRUE);
        menu.findItem(idInactive).setChecked(Boolean.FALSE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_notes:
                //Toast.makeText(this, "Notes", Toast.LENGTH_LONG).show();
                displayNotes();
                //drawer.closeDrawer(Gravity.LEFT);
                break;
            case R.id.nav_courses:
                displayCourses();
                //drawer.closeDrawer(Gravity.LEFT);
                break;
            case R.id.nav_share:
                handleShare();
                //drawer.closeDrawer(Gravity.LEFT);
                break;
            case R.id.nav_send:
                handleSelection("Send");
                //drawer.closeDrawer(Gravity.LEFT);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return Boolean.TRUE;
    }

    private void handleShare() {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, "Share to - " +
                PreferenceManager.getDefaultSharedPreferences(this).getString("socialNetwork", ""),
                Snackbar.LENGTH_LONG).show();
    }

    private void handleSelection(String message) {
        //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_NOTES) {
            loader = new CursorLoader(this) {
                @Override
                public Cursor loadInBackground() {
                    SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                    final String[] noteColumns = {
                            NoteInfoEntry.getQName(NoteInfoEntry._ID),
                            NoteInfoEntry.COLUMN_NOTE_TITLE,
                            CourseInfoEntry.COLUMN_COURSE_TITLE
                    };

                    final String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE +
                            "," + NoteInfoEntry.COLUMN_NOTE_TITLE;

                    //note_info JOIN course_info ON note_info.course_id = course_info.course_id
                    String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " +
                            CourseInfoEntry.TABLE_NAME + " ON " +
                            NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
                            CourseInfoEntry.getQName( CourseInfoEntry.COLUMN_COURSE_ID);
                    return db.query(tablesWithJoin, noteColumns,
                            null, null, null, null, noteOrderBy);
                }
            };
        }
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_NOTES)  {
            noteRecyclerAdapter.changeCursor(data);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTES)  {
            noteRecyclerAdapter.changeCursor(null);
        }

    }
}