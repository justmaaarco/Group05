package it.polito.group05.group05;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AestheticActivity;
import com.afollestad.aesthetic.NavigationViewMode;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mvc.imagepicker.ImagePicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import it.polito.group05.group05.Utility.BaseClasses.ColorItem;
import it.polito.group05.group05.Utility.BaseClasses.CurrentUser;
import it.polito.group05.group05.Utility.BaseClasses.GroupDatabase;
import it.polito.group05.group05.Utility.BaseClasses.Singleton;
import it.polito.group05.group05.Utility.Event.ReadyEvent;
import it.polito.group05.group05.Utility.HelperClasses.AnimUtils;
import it.polito.group05.group05.Utility.HelperClasses.DB_Manager;
import it.polito.group05.group05.Utility.HelperClasses.ImageUtils;
import it.polito.group05.group05.Utility.Holder.GroupHolder;

public class MainActivity extends AestheticActivity
implements NavigationView.OnNavigationItemSelectedListener {


    private static final int COMING_FROM_BALANCE_ACTIVITY = 123;
    private static int CUSTOM_THEME_OPTION = 0;
    private static int PREDEFINED_THEME_OPTION = 0;
    public  static int REQUEST_FROM_NEW_USER;
    private static String THEME_HELPER = "Primary";
    private static final String PRIMARY = "Primary";
    private static final String ACCENT = "Accent";
    DrawerLayout drawer;
    CircleImageView cv_user_drawer;
    Activity activity;
    Context context;
    ImageView iv_no_groups;
    TextView tv_no_groups;
    FirebaseIndexRecyclerAdapter mAdapter;
    RecyclerView rv;
    ImageView iv_nav_header;
    int colors[] = new int[2];

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        Intent intent = ImagePicker.getPickImageIntent(this, "Select Image:");
        if (requestCode == COMING_FROM_BALANCE_ACTIVITY) {
            drawer.openDrawer(Gravity.START);
        }
        if(bitmap != null && REQUEST_FROM_NEW_USER == requestCode) {
            cv_user_drawer.setImageBitmap(bitmap);
            //currentUser.setProfile_image(bitmap);
            //DB_Manager.getInstance().photoMemoryUpload(1, currentUser.getId(), bitmap);
            String uuid = UUID.randomUUID().toString();
            Singleton.getInstance().getCurrentUser().setiProfile(uuid);
            DB_Manager.getInstance().setContext(context)
                    .imageProfileUpload(1, Singleton.getInstance().getCurrentUser().getId(), uuid,  bitmap);
            FirebaseDatabase.getInstance().getReference("users").child(Singleton.getInstance().getCurrentUser().getId()).child("userInfo").child("iProfile").setValue(uuid);
            drawer.closeDrawers();
            REQUEST_FROM_NEW_USER = -1;
        }
    }


    @Subscribe
    public void groupStart(ReadyEvent cu) {
        Singleton.getInstance().setmCurrentGroup(cu.getGroupDatabase());
        Singleton.getInstance().setIdCurrentGroup(cu.getGroupDatabase().getId());
        Intent i = new Intent(context, GroupActivity.class);
        getIntent().getStringExtra("message");
        i.putExtra("message", getIntent().getStringExtra("message"));
        i.putExtra("expenseId", getIntent().getStringExtra("expenseId"));
        i.putExtra("groupId", getIntent().getStringExtra("groupId"));
        context.startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            finish();
            return;
        }

        String tkn = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase.getInstance().getReference("users").child(Singleton.getInstance().getCurrentUser().getId()).child("fcmToken").setValue(tkn);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_no_groups = (ImageView)findViewById(R.id.iv_no_groups);
        tv_no_groups = (TextView)findViewById(R.id.tv_no_groups);
        rv = (RecyclerView) findViewById(R.id.groups_rv);
        final ProgressBar pb = (ProgressBar)findViewById(R.id.pb_loading_groups);
        setSupportActionBar(toolbar);

        activity = this;
        FirebaseDatabase.getInstance().getReference("users").child(Singleton.getInstance().getCurrentUser().getId()).child("fcmToken").setValue(FirebaseInstanceId.getInstance().getToken());
        /**DEBUGG**/
        Singleton.getInstance().setCurrContext(getApplicationContext());
        context = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    startActivity(new Intent(MainActivity.this, NewGroupActivity.class));
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(final View drawerView, float slideOffset) {
                cv_user_drawer = (CircleImageView)findViewById(R.id.drawer_header_image);
                iv_nav_header = (ImageView) findViewById(R.id.background_nav_header);
                ImageUtils.LoadMyImageProfile(cv_user_drawer, context);
                ImageUtils.LoadMyImageProfile(iv_nav_header, context);
                final TextView tv_username = (TextView)findViewById(R.id.drawer_username);
                tv_username.setText(Singleton.getInstance().getCurrentUser().getName());
                final TextView tv_email = (TextView)findViewById(R.id.drawer_email);
                tv_email.setText(Singleton.getInstance().getCurrentUser().getEmail());
                cv_user_drawer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePicker.pickImage(activity, "Select Image:");
                        REQUEST_FROM_NEW_USER = ImagePicker.PICK_IMAGE_REQUEST_CODE;
                    }
                });
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Query ref = FirebaseDatabase.getInstance().getReference("groups").orderByChild("lmTime");
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("users").child(Singleton.getInstance().getCurrentUser().getId()).child("userGroups");
        mAdapter = new FirebaseIndexRecyclerAdapter( GroupDatabase.class,
                                                            R.layout.group_item_sample,
                                                            GroupHolder.class, groupRef, ref){


            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
                ((GroupHolder)viewHolder).setData(model,context);
            }

            @Override
            protected void onChildChanged(ChangeEventListener.EventType type, int index, int oldIndex) {
                super.onChildChanged(type, index, oldIndex);
                tv_no_groups.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                iv_no_groups.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                pb.setVisibility(View.GONE);

            }

            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                tv_no_groups.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                iv_no_groups.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };

        rv.setAdapter(mAdapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String groupId = getIntent().getStringExtra("groupId");
        if (groupId != null) {

            FirebaseDatabase.getInstance().getReference("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) return;
                    //    if(!(dataSnapshot.getValue() instanceof GroupDatabase)) return ;
                    GroupDatabase g = dataSnapshot.getValue(GroupDatabase.class);
                    EventBus.getDefault().post(new ReadyEvent(g));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        if (id == R.id.nav_balance) {
            Pair<View, String> p = new Pair<>((View) cv_user_drawer, getResources().getString(R.string.transition_group_image));
            AnimUtils.startActivityForResultWithAnimation(this, new Intent(this, UserBalanceActivity.class), COMING_FROM_BALANCE_ACTIVITY, p);

        } else if (id == R.id.nav_manage) {
            Snackbar.make(findViewById(R.id.parent_layout), "To be implemented...", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .show();

        } else if (id == R.id.nav_share) {
            Intent intent = new AppInviteInvitation.IntentBuilder("ciao")
                    .setMessage("ciao ciao ciao")
                    .build();
            startActivityForResult(intent, 1);
        } else if (id == R.id.nav_logout) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                    finish();
                }
            });


        } else if (id == R.id.nav_themes) {

            if(CUSTOM_THEME_OPTION == 0 && PREDEFINED_THEME_OPTION == 0) {
                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title("Theming Options")
                        .positiveText("Predefined Themes")
                        .negativeText("Custom Themes")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                PREDEFINED_THEME_OPTION = 1;
                                CUSTOM_THEME_OPTION = 0;
                                onNavigationItemSelected(item);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                CUSTOM_THEME_OPTION = 1;
                                PREDEFINED_THEME_OPTION = 0;
                                onNavigationItemSelected(item);
                            }
                        })
                        .show();
            }
            if(PREDEFINED_THEME_OPTION == 1) {
                FastItemAdapter adapter = new FastItemAdapter<ColorItem>();
                adapter.withSelectable(true);
                adapter.add(generateThemes());
                final MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title("Select one Theme")
                        .titleColor(getResources().getColor(R.color.colorAccent))
                        .adapter(adapter, new LinearLayoutManager(context))
                        .show();
                adapter.withOnClickListener(new FastAdapter.OnClickListener() {
                    @Override
                    public boolean onClick(View view, IAdapter iAdapter, IItem item, int i) {
                        colors[0] = ((ColorItem)item).getAccentColor();
                        colors[1] = ((ColorItem)item).getPrimaryColor();
                        dialog.dismiss();
                        setupTheme(colors);
                        return true;
                    }
                });
            } else if(CUSTOM_THEME_OPTION == 1)
            ColorPickerDialogBuilder
                    .with(context)
                    .setTitle("Choose " + THEME_HELPER + " color")
                    .initialColor(THEME_HELPER.equals(PRIMARY) ? getResources().getColor(R.color.colorPrimary) :
                                                                getResources().getColor(R.color.colorAccent))
                    .showColorPreview(true)
                    .lightnessSliderOnly()
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton(THEME_HELPER.equals(PRIMARY) ? "Select Accent" : "Let's Theme it!", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            if(THEME_HELPER.equals(PRIMARY)) {
                                THEME_HELPER = ACCENT;
                                colors[1] = selectedColor;
                                onNavigationItemSelected(item);
                            } else {
                                colors[0] = selectedColor;
                                THEME_HELPER = PRIMARY;
                                setupTheme(colors);
                            }

                        }
                    })
                    .setNegativeButton("Restore Default", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            colors[1] = getResources().getColor(R.color.colorPrimary);
                            colors[0] = getResources().getColor(R.color.colorAccent);
                            setupTheme(colors);
                        }
                    })
                    .build()
                    .show();

        } else if (id == R.id.nav_contacts) {
            Intent i = new Intent();
            i.setComponent(new ComponentName("com.android.contacts", "com.android.contacts.DialtactsContactsEntryActivity"));
            i.setAction("android.intent.action.MAIN");
            i.addCategory("android.intent.category.LAUNCHER");
            i.addCategory("android.intent.category.DEFAULT");
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    public void setupTheme(int[] colors) { //0 accent, 1 primary
        int accent = colors[0];
        int primary = colors[1];
        Singleton.getInstance().setColors(colors);
        Aesthetic.get()
                .colorPrimary(primary)
                .colorStatusBarAuto()
                .colorNavigationBarAuto()
                .colorAccent(accent)
                .navigationViewMode(
                        NavigationViewMode.SELECTED_ACCENT
                )
                .apply();
        CUSTOM_THEME_OPTION = 0;
        PREDEFINED_THEME_OPTION = 0;
    }

    private ColorItem[] generateThemes() {
        ColorItem themes[] = new ColorItem[3];
        themes[0] = new ColorItem(Color.parseColor("#ffd740"), Color.parseColor("#4a148c"), "Lakers Theme");
        themes[1] = new ColorItem(Color.parseColor("#607d8b"), Color.parseColor("#ff8f00"), "Robin Hood");
        themes[2] = new ColorItem(Color.parseColor("#e91e63"), Color.parseColor("#ffd740"), "Cake Piece");
        return themes;
    }
}
