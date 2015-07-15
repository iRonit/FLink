package com.ronit.flink;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import info.ronit.navdrawer.adapter.NavDrawerListAdapter;
import info.ronit.navdrawer.model.NavDrawerItem;
import info.ronit.db.*;
import info.ronit.db.model.*;

public class HomeActivity extends BaseActivity{

    DbManager dbManager;
    ImageButton fabImageButton;
    ListView listView;
    SimpleCursorAdapter adapter;
    ImageView displaypic;
    MemberDetail member = new MemberDetail();

    //Drawer
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter navadapter;

    String[] from = new String[] { DbHandler.KEY_NAME, DbHandler.KEY_AGE , DbHandler.KEY_FLINK, DbHandler.KEY_IMAGE};
    int[] to = new int[] { R.id.name, R.id.age_detail, R.id.relation, R.id.displaypic };


    private static final int PICKFILE_RESULT_CODE = 1;
    private String[] mRelations;
    SharedPreferences pref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.ic_ab_drawer);

        mRelations = getResources().getStringArray(R.array.relation);
        pref = getApplicationContext().getSharedPreferences("MemberCount", MODE_PRIVATE);


        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.empty));

        //Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        //FAB
        fabImageButton = (ImageButton) findViewById(R.id.fab_image_button);
        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getCount()==0 && dbManager.isEmpty())
                {
                    Intent intent = new Intent(HomeActivity.this,AddYourself.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(HomeActivity.this,AddMember.class);
                    startActivity(intent);
                }

            }
        });


        //Display pic
        displaypic = (ImageView) findViewById(R.id.displaypic);

        //Setting up adapter for ListView
        dbManager = new DbManager(this);
        dbManager.open();
        setHomeListView("null");

        //cursor.close();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {

                View parent = (View)arg1.getParent();
                TextView name_home = (TextView) arg1.findViewById(R.id.name);
                TextView age_home = (TextView) arg1.findViewById(R.id.age_detail);
                TextView display_link1 = (TextView) findViewById(R.id.display_link1);
                TextView display_link2 = (TextView) findViewById(R.id.display_link2);
                FlinkMagic fm = new FlinkMagic();
                String nameHome = name_home.getText().toString();
                String ageHome = age_home.getText().toString();

                //Identifying th Member
                member = dbManager.getMember(nameHome, ageHome);

                //Display in FLink!
                String relname = member.getRname();
                String finalFlink = fm.getFinalFlink(member);
                if(member.getFlink().compareToIgnoreCase("Me!")==0)
                    finalFlink = "It's Me!!";
                display_link1.setText(finalFlink);


                if(relname.endsWith("(Me)"))
                    relname = "My "+member.getRelation();
                else if(relname.compareToIgnoreCase("NULL")==0)
                    relname = "How are you related to yourself?";
                else
                    relname += "'s "+member.getRelation();
                display_link2.setText(relname);

            }

        });


        //GoFlink
        LinearLayout goFlink = (LinearLayout) findViewById(R.id.display);
        goFlink.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(HomeActivity.this, Flink.class);
                startActivity(i);
                return true;
            }

        });



        //Drawer
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        Intent i = getIntent();
        if(i.getBooleanExtra("hasImported",false))
        {update_count(); showMessage("Count Updated");}
        setupDrawer();

        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        navadapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(navadapter);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

   }

    public void setHomeListView(String flink) {
        Cursor cursor = flink.equals("null")? dbManager.fetch() : dbManager.fetch(flink);
        if(cursor.getCount()==0 && !flink.equals("null"))
        {
            TextView tv = (TextView) findViewById(R.id.empty);
            tv.setText("Empty");
        }
        adapter = new ImageCursorAdapter(this, R.layout.list_item, cursor, from, to);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_goflink:
                Intent i = new Intent(HomeActivity.this, Flink.class);
                startActivity(i);
                return true;

            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;

            case R.id.action_import:
                import_file();
                return true;

            case R.id.action_export:
                export_file();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickingDisplay(View v) {
        View parent = (View)v.getParent();
        if (parent != null) {
            Intent i = new Intent(HomeActivity.this, DetailActivity.class);
            TextView name = (TextView) parent.findViewById(R.id.name);
            TextView age = (TextView) parent.findViewById(R.id.age_detail);

            member = dbManager.getMember(name.getText().toString(), age.getText().toString());

            i.putExtra("member", member);
            startActivity(i);
        }
    }

    public void setupDrawer() {

        navDrawerItems = new ArrayList<NavDrawerItem>();
        MemberCount mc = new MemberCount();


        ImageView dp = (ImageView) findViewById(R.id.dp);
        Cursor cursor = dbManager.fetch("Me!");

        if(cursor.getCount()!=0)
        {
            byte[] img = cursor.getBlob(cursor.getColumnIndex("image"));
            Bitmap b1= BitmapFactory.decodeByteArray(img, 0, img.length);
            dp.setImageBitmap(b1);
        }
        else
        {
            dp.setImageResource(R.drawable.ic_action_person);
        }

        // adding nav drawer items to array
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(2, -1), true, mc.get_total(pref)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(9, -1), true, mc.get_child(pref)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(9, -1), true, mc.get_cousin(pref)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(9, -1), true, mc.get_gchild(pref)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(9, -1), true, mc.get_gparent(pref)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(9, -1), true, mc.get_parent(pref)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(9, -1), true, mc.get_psibling(pref)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(9, -1), true, mc.get_sibling(pref)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(9, -1), true, mc.get_schild(pref)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1), true, mc.get_spouse(pref)));
        // Help and Feedback
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], navMenuIcons.getResourceId(0, -1)));
        // About FLink!
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[11], navMenuIcons.getResourceId(1, -1)));
    }

    //Slide menu item click listener
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }


   // Diplaying fragment view for selected nav drawer list item
    private void displayView(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                setHomeListView("null");
                break;
            case 1:
                setHomeListView("Child");
                break;
            case 2:
                setHomeListView("Cousin");
                break;
            case 3:
                setHomeListView("Grand-Child");
                break;
            case 4:
                setHomeListView("Grand-Parent");
                break;
            case 5:
                setHomeListView("Parent");
                break;
            case 6:
                setHomeListView("Parent's Sibling");
                break;
            case 7:
                setHomeListView("Sibling");
                break;
            case 8:
                setHomeListView("Sibling's Child");
                break;
            case 9:
                setHomeListView("Spouse");
                break;
            default:
                break;
        }


    }




    public void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }


    public void import_file() {
        try{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICKFILE_RESULT_CODE);

        }
        catch(ActivityNotFoundException exp){
            showMessage("No File Manager Found In Your Device!");
        }
    }

    public void update_count() {

        MemberCount mc = new MemberCount();
        Cursor cursor=null;
        for(int i=0; i<mRelations.length; i++)
        {
            cursor = dbManager.fetch(mRelations[i]);
            mc.refresh(mRelations[i],cursor.getCount(),pref);
        }
        cursor = dbManager.fetch();
        mc.refresh("Total",cursor.getCount(),pref);
        cursor.close();

    }

    public void export_file(){
        try { dbManager.export_db();
                showMessage("Database Exported!");}
        catch (IOException e) { e.getMessage(); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode==RESULT_OK){
                    String FilePath = data.getData().getPath();
                    try{
                        showMessage(dbManager.import_db(FilePath));
                        this.returnHome();
                    }
                    catch(IOException e){ e.getMessage(); }
                }
                break;
        }
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home_intent.putExtra("hasImported",true);
        startActivity(home_intent);
    }
}




