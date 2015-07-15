package com.ronit.flink;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import info.ronit.db.model.*;
import info.ronit.db.*;

public class DetailActivity extends BaseActivity {

    Button delete;
    ImageView displaypic;
    DbManager dbManager;
    TextView name, age, relation;
    MemberDetail member = new MemberDetail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displaypic = (ImageView) findViewById(R.id.displaypic);
        name = (TextView) findViewById(R.id.name);
        age = (TextView) findViewById(R.id.age_detail);
        relation = (TextView) findViewById(R.id.relation);

        dbManager = new DbManager(this);
        dbManager.open();

        Intent i = getIntent();
        member = (MemberDetail)i.getSerializableExtra("member");

        name.setText(member.getName());
        age.setText(member.getAge());
        relation.setText(member.getFlink());
        byte[] img = member.getImage();
        Bitmap b1= BitmapFactory.decodeByteArray(img, 0, img.length);
        displaypic.setImageBitmap(b1);

    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_detail;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_member_view_details, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id)
        {

            case R.id.action_delete:
            {
                if(member.getFlink().compareToIgnoreCase("Me!")==0)
                {
                    Toast.makeText(DetailActivity.this, "Cannot Delete Yourself!", Toast.LENGTH_SHORT).show();
                    break;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                delete();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Are you sure?");
                d.show();
            }break;

            case R.id.action_edit:
                Intent i = new Intent(DetailActivity.this, EditActivity.class);
                i.putExtra("member", member);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void delete(){
        dbManager = new DbManager(this);
        dbManager.open();
            MemberCount mc = new MemberCount();
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MemberCount", MODE_PRIVATE);
            mc.subcount(member, pref);
            dbManager.delete(member);
            showMessage("Successfully Deleted!");
        dbManager.close();
        this.returnHome();
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnHome();
    }

    public void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
