package com.ronit.flink;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import info.ronit.db.model.*;
import info.ronit.db.*;

public class EditActivity extends BaseActivity {

    MemberDetail member = new MemberDetail();
    EditText name, age;
    RadioGroup gender;
    DbManager dbManager = new DbManager(this);
    ImageView displaypic;
    int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();

        member = (MemberDetail)i.getSerializableExtra("member");

        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        gender = (RadioGroup) findViewById(R.id.gender);
        displaypic = (ImageView) findViewById(R.id.displaypic);

        name.setText(member.getName());
        age.setText(member.getAge());
        if(member.getGender().equalsIgnoreCase("Male"))
            gender.check(R.id.radioMale);
        else
            gender.check(R.id.radioFemale);

        byte[] img = member.getImage();
        Bitmap b1= BitmapFactory.decodeByteArray(img, 0, img.length);
        displaypic.setImageBitmap(b1);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.action_edit_done:
                //Name
                member.setName(name.getText().toString());

                //Gender
                int index = gender.indexOfChild(findViewById(gender.getCheckedRadioButtonId()));
                if (index == 0)
                    member.setGender("Male");
                else
                    member.setGender("Female");

                //Age
                member.setAge(age.getText().toString());

                //Adding Image
                displaypic.buildDrawingCache();
                Bitmap bmap = displaypic.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                member.setImage(byteArray);


                dbManager.open();
                int a = dbManager.update(member);
                dbManager.close();


                showMessage("Successfully Edited!");


                this.returnHome();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_edit;
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), DetailActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home_intent.putExtra("member",member);
        startActivity(home_intent);
    }

    public void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    //For IMAGE
    public void onClickEditDP(View v) {
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
