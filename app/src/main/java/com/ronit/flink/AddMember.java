package com.ronit.flink;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;

import info.ronit.db.model.*;
import info.ronit.db.*;

public class AddMember extends BaseActivity implements View.OnClickListener{

    EditText name, age;
    RadioGroup gender;
    ImageView displaypic;
    ImageButton done; //FAB
    Spinner rel;
    AutoCompleteTextView rname;
    MemberDetail member;  //Member Object
    String[] item = new String[] {"Search..."};
    ArrayAdapter<String> rnameAdapter;
    byte[] img=null;
    int RESULT_LOAD_IMAGE = 1;


    //DB Manager
    DbManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        gender = (RadioGroup) findViewById(R.id.gender);
        rel = (Spinner) findViewById(R.id.rel);
        rname = (AutoCompleteTextView) findViewById(R.id.rname);
        displaypic = (ImageView) findViewById(R.id.displaypic);


        done = (ImageButton) findViewById(R.id.fab_image_button_done);
        done.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.relation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rel.setAdapter(adapter);


        dbManager = new DbManager(this);
        dbManager.open();

        // Populating RName
        // add the listener so it will tries to suggest while the user types
        rname.addTextChangedListener(new AutoCompleteTextChangedListener(this));

        // set our adapter
        rnameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
        rname.setAdapter(rnameAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_member, menu);
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

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.fab_image_button_done:
                // Input Error Handling
                String rn = rname.getText().toString().trim();
                rn = rn.endsWith("(Me)")? rn.substring(0,rn.length()-5) : rn ;
                MemberDetail member2 = new MemberDetail();                  //member2 contains details of the related name
                member2 = dbManager.getMember(rn,"NULL");
                String relation = rel.getSelectedItem().toString();
                if(TextUtils.isEmpty(name.getText().toString()))
                {
                    showMessage("Please add a name!");
                    break;
                }
                else if(TextUtils.isEmpty(age.getText().toString()))
                {
                    showMessage("Please add an age!");
                    break;
                }
                else if(TextUtils.isEmpty(rn))
                {
                    showMessage("Please add a relative's name!");
                    break;
                }
                else if(!dbManager.exists(rn))
                {
                    showMessage("Please add a relative that's already been added!");
                    break;
                }
                else if(member2.getFlink().contains("Grand") && relation.contains("Child"))
                {
                    showMessage("Please live in the present!\nLimit Exceeded.");
                    break;
                }
                else
                {
                    member = new MemberDetail();

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

                    //Relation to person
                    member.setRelation(relation);
                    member.setRname(rname.getText().toString().trim());

                    //Setting if inlaw

                    if (member2.getFlink().equalsIgnoreCase("Spouse")
                            || member2.getInlaw().equalsIgnoreCase("YES")
                            || (member.getRelation().equalsIgnoreCase("Spouse")
                                && member2.getInlaw().equalsIgnoreCase("NO")
                                && !member2.getFlink().equalsIgnoreCase("Parent")))
                        member.setInlaw("YES");
                    else
                        member.setInlaw("NO");



                    //Setting up the FLink!
                    String newflink = "";
                    if (member2.getFlink().equalsIgnoreCase("Me!"))
                        newflink = member.getRelation();
                    else {
                        String gotflink = dbManager.getFlinkfromDb(member.getRname());
                        FlinkMagic fm = new FlinkMagic();
                        newflink = fm.getFlink(gotflink, member.getRelation());
                    }
                    member.setFlink(newflink);


                    //Adding Image
                    displaypic.buildDrawingCache();
                    Bitmap bmap = displaypic.getDrawingCache();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    member.setImage(byteArray);


                    dbManager.addMember(member);
                    showMessage("Successfully Added!");
                    //dbManager.close();

                    //SharedPreference for counts
                    MemberCount mc = new MemberCount();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MemberCount", MODE_PRIVATE);
                    mc.addcount(member,pref);


                    Intent intent = new Intent(AddMember.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_member;
    }

    public void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    // this function is used in AutoCompleteTextChangedListener.java
    public String[] getItemsFromDb(String searchTerm){

        // add items on the array dynamically
        List<String> products = dbManager.read(searchTerm);
        int rowCount = products.size();
        String[] item = new String[rowCount];
        int x = 0;
        for (String record : products) {

            item[x] = record;
            x++;
        }
        return item;
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


class AutoCompleteTextChangedListener implements TextWatcher {

    public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
    Context context;

    public AutoCompleteTextChangedListener(Context context){
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        AddMember mainActivity = ((AddMember) context);

        // query the database based on the user input
        mainActivity.item = mainActivity.getItemsFromDb(userInput.toString().trim());

        // update the adapter
        mainActivity.rnameAdapter.notifyDataSetChanged();
        mainActivity.rnameAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, mainActivity.item);
        mainActivity.rname.setAdapter(mainActivity.rnameAdapter);
    }

}