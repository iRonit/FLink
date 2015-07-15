package com.ronit.flink;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import info.ronit.db.model.*;
import info.ronit.db.*;

import java.io.ByteArrayOutputStream;

public class AddYourself extends BaseActivity implements View.OnClickListener{

    MemberDetail member;
    TextView myname;
    TextView myage;
    RadioGroup mygender;
    ImageButton done;
    DbManager dbManager;
    ImageView displaypic;
    int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing
        myname = (TextView) findViewById(R.id.myname);
        myage = (TextView) findViewById(R.id.myage);
        mygender = (RadioGroup) findViewById(R.id.mygender);
        done = (ImageButton) findViewById(R.id.fab_image_button_done);
        displaypic = (ImageView) findViewById(R.id.displaypic);
        done.setOnClickListener(this);

        //DB
        dbManager = new DbManager(this);
        dbManager.open();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.fab_image_button_done:

                member = new MemberDetail();

                //Name
                member.setName(myname.getText().toString());

                //Gender
                int index = mygender.indexOfChild(findViewById(mygender.getCheckedRadioButtonId()));
                if(index==0)
                    member.setGender("Male");
                else
                    member.setGender("Female");

                //Age
                member.setAge(myage.getText().toString());

                //Relation and RName
                member.setRelation("null");
                member.setRname("null");

                //Setting up the FLink!
                member.setFlink("Me!");
                member.setInlaw("NO");

                //Adding Image
                displaypic.buildDrawingCache();
                Bitmap bmap = displaypic.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                member.setImage(byteArray);


                dbManager.addMember(member);
                //SharedPreference for counts
                MemberCount mc = new MemberCount();
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MemberCount", MODE_PRIVATE);
                mc.refresh("Total",1,pref);
                showMessage("Successfully Added!");
                dbManager.close();
                Intent intent = new Intent(AddYourself.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                break;
        }
    }
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_yourself;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_yourself, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
    return super.onOptionsItemSelected(item);
    }

    public void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    //For IMAGE
    public void onClickAddDP(View v) {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // String picturePath contains the path of selected Image
            displaypic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

}
