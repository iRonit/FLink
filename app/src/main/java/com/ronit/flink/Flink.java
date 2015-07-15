package com.ronit.flink;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import info.ronit.db.model.*;
import info.ronit.db.*;

public class Flink extends BaseActivity implements View.OnClickListener{

    AutoCompleteTextView rname1,rname2;
    ArrayAdapter<String> rnameAdapter;
    String[] item = new String[] {"Please search..."};
    DbManager dbManager;
    TextView disp1, disp2;
    ImageButton done;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rname1 = (AutoCompleteTextView) findViewById(R.id.rname1);
        rname2 = (AutoCompleteTextView) findViewById(R.id.rname2);
        disp1 = (TextView) findViewById(R.id.display_link1);
        disp2 = (TextView) findViewById(R.id.display_link2);


        done = (ImageButton) findViewById(R.id.fab_image_button_done);
        done.setOnClickListener(this);

        dbManager = new DbManager(this);
        dbManager.open();


        // Populating RName
        // add the listener so it will tries to suggest while the user types
        rname1.addTextChangedListener(new AutoCompleteTextChangedListener1(this));
        rname2.addTextChangedListener(new AutoCompleteTextChangedListener1(this));

        // set our adapter
        rnameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
        rname1.setAdapter(rnameAdapter);
        rname2.setAdapter(rnameAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flink, menu);
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
    protected int getLayoutResource() {
        return R.layout.activity_flink;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.fab_image_button_done:
                // Go FLink!

                String rn1 = rname1.getText().toString().trim();
                String rn2 = rname2.getText().toString().trim();
                rn1 = rn1.endsWith("(Me)")? rn1.substring(0,rn1.length()-5) : rn1;
                rn2 = rn2.endsWith("(Me)")? rn2.substring(0,rn2.length()-5) : rn2;
                //Input Handling
                if(TextUtils.isEmpty(rn1) || TextUtils.isEmpty(rn2))
                {
                    showMessage("Please add a relative's name!");
                    break;
                }
                else if(!dbManager.exists(rn1) || !dbManager.exists(rn2))
                {
                    showMessage("Please add a relative that's already been added!");
                    break;
                }
                showMessage("FLinked!");

                String flinkNow = "";
                String finalFlink = "";

                MemberDetail member1 = new MemberDetail();
                MemberDetail member2 = new MemberDetail();
                member1 = dbManager.getMember(rn1,"NULL");
                member2 = dbManager.getMember(rn2,"NULL");

                FlinkMagic fm = new FlinkMagic();
                if(rn1.equalsIgnoreCase(rn2))
                {
                    flinkNow = "Seriously?";
                    finalFlink = "O.o";
                }
                else if(member1.getFlink().equalsIgnoreCase("Me!"))
                {
                    flinkNow = fm.getFlinkNow("Spouse", member2.getFlink());
                    finalFlink = fm.getFinalFlink(member1, flinkNow);

                }
                else if(member2.getFlink().equalsIgnoreCase("Me!"))
                {
                    flinkNow = member1.getFlink();
                    finalFlink = fm.getFinalFlink(member1);
                }
                else
                {
                    flinkNow = fm.getFlinkNow(member1.getFlink(), member2.getFlink());
                    finalFlink = fm.getFinalFlink(member1,flinkNow);
                }

                if(finalFlink.contains("/"))
                    finalFlink = "It could be...";
                disp1.setText(finalFlink);
                disp2.setText(flinkNow);

                break;
        }
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


    public void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}

class AutoCompleteTextChangedListener1 implements TextWatcher {

    public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
    Context context;

    public AutoCompleteTextChangedListener1(Context context){
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

        Flink mainActivity = ((Flink) context);

        // query the database based on the user input
        mainActivity.item = mainActivity.getItemsFromDb(userInput.toString());

        // update the adapter
        mainActivity.rnameAdapter.notifyDataSetChanged();
        mainActivity.rnameAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, mainActivity.item);
        mainActivity.rname1.setAdapter(mainActivity.rnameAdapter);
        mainActivity.rname2.setAdapter(mainActivity.rnameAdapter);
    }

}
