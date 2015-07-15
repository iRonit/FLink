package com.ronit.flink;

import java.util.HashMap;

import info.ronit.db.model.*;


/**
 * Created by Ronit on 4/3/2015.
 */
public class FlinkMagic extends HomeActivity {

    public HashMap<String,Integer> mapRelation;
    public HashMap<String,String> mapMale;
    public HashMap<String,String> mapFemale;
    String[] relations, malerel, femalerel;

    public final String[][]FLINK = {{"Grand-Child", "Child", "Great-Grand-Child", "Parent", "Spouse","Sibling", "Child", "Grand-Child", "Child"},
            {"Sibling's Child", "Cousin", "Grand-Child", "Grand-Parent", "Parent's Sibling", "Parent's Sibling", "Cousin", "Child", "Cousin"},
            {"Great-Grand-Child", "Grand-Child", "null", "Sibling", "Child", "Child", "Grand-Child", "Great-Grand-Child", "Grand-Child"},
            {"Parent's Sibling", "Grand-Parent", "Cousin", "null", "Great-Grand-Parent", "Great-Grand-Parent", "Grand-Parent", "Parent's Sibling", "Grand-Parent"},
            {"Sibling", "Parent's Sibling", "Child", "Great-Grand-Parent", "Grand-Parent", "Grand-Parent", "Parent's Sibling", "Cousin", "Parent"},
            {"Cousin", "Parent's Sibling", "Child", "Great-Grand-Parent", "Grand-Parent", "Grand-Parent", "Parent's Sibling", "Cousin", "Parent's Sibling"},
            {"Sibling's Child", "Cousin", "Grand-Child", "Grand-Parent", "Parent's Sibling", "Parent's Sibling", "Sibling", "Child", "Sibling"},
            {"Grand-Child", "Child", "Great-Grand-Child", "Parent's Sibling", "Sibling", "Sibling", "Sibling's Child", "Grand-Child", "Sibling's Child"},
            {"Child", "Cousin", "Grand-Child", "Grand-Parent", "Parent", "Parent's Sibling", "Sibling", "Child", "null"}
    };

    public final String[][]FLINK_NOW = {{"Sibling", "Sibling's Child", "Parent/Parent's Sibling", "Great-Grand-Child", "Grand-Child", "Sibling's Child", "Sibling's Child", "Cousin", "Child"},
            {"Parent's Sibling", "Cousin", "Grand-Parent", "Grand-Child", "Sibling's Child", "Child/Sibling's Child", "Cousin", "Parent/Parent's Sibling", "Cousin"},
            {"Child", "Grand-Child", "Sibling/Cousins", "Great-Great-Grand-Child", "Great-Grand-Child", "Great-Grand-Child", "Grand-Child", "Sibling's Child", "Grand-Child"},
            {"Great-Grand-Parent", "Grand-Parent", "Great-Great-Grand-Parent", "Sibling/Cousin", "Parent/Parent's Sibling", "Parent/Parent's Sibling", "Grand-Parent", "Great-Grand-Parent", "Grand-Parent"},
            {"Grand-Parent", "Parent's Sibling", "Great-Grand-Parent", "Child/Sibling's Child", "Spouse", "Sibling", "Parent", "Grand-Parent", "Parent"},
            {"Great-Grand-Parent", "Parent/Parent's Sibling", "Great-Grand-Parent", "Child/Sibling's Child", "Sibling/Cousin", "Sibling/Cousin", "Parent's Sibling", "Grand-Parent", "Parent's Sibling"},
            {"Parent's Sibling", "Cousin", "Grand-Parent", "Grand-Child	Child", "Sibling's Child", "Sibling", "Parent/Parent's Sibling", "Sibling"},
            {"Cousin", "Sibling's Child", "Parent's Sibling", "Great-Grand-Child", "Grand-Child", "Grand-Child", "Sibling's Child", "Cousin", "Sibling's Child"},
            {"Parent", "Cousin", "Grand-Parent", "Grand-Child", "Child", "Sibling's Child", "Sibling", "Parent's Sibling", "Spouse"}
    };
    public FlinkMagic() {

        //relations = getResources().getStringArray(R.array.relation);

        relations = new String[]
                {"Child","Cousin","Grand-Child","Grand-Parent","Parent","Parent's Sibling","Sibling","Sibling's Child","Spouse"};
        malerel = new String[]
                {"Son","Cousin-Brother","Grand-Son","Grand-Dad","Dad","Uncle","Brother","Nephew","Husband"};
        femalerel = new String[]
                {"Daughter","Cousin-Sister","Grand-Daughter","Grand-Mother","Mother","Aunt","Sister","Niece","Wife"};

        mapRelation=new HashMap<String,Integer>();
        mapMale=new HashMap<String,String>();
        mapFemale=new HashMap<String,String>();

        for(int i=0; i<relations.length; i++)
        {
            mapRelation.put(relations[i],i);
            mapMale.put(relations[i],malerel[i]);
            mapFemale.put(relations[i],femalerel[i]);

        }

    }


    public String getFlink(String r1,String r2) {

        int r1pos = mapRelation.get(r1);
        int r2pos = mapRelation.get(r2);
        return FLINK[r1pos][r2pos];
    }

    public String getFinalFlink(MemberDetail member) {

        String first="", temp=member.getFlink();
        while(temp.contains("Great-"))
        {
            first += "Great-";
            temp = temp.substring(6);

        }
        if(member.getGender().compareToIgnoreCase("Male")==0)
            first += mapMale.get(temp);
        else
            first += mapFemale.get(temp);

        if(member.getInlaw().equalsIgnoreCase("YES") &&
                !member.getFlink().equalsIgnoreCase("Spouse"))
            first += "-in-Law";

        return first;
    }

    public String getFinalFlink(MemberDetail member, String rel) {

        String first="", temp=rel;
        while(temp.contains("Great-"))
        {
            first += "Great-";
            temp = temp.substring(6);

        }
        if(member.getGender().compareToIgnoreCase("Male")==0)
            first += mapMale.get(temp);
        else
            first += mapFemale.get(temp);

        if(member.getInlaw().equalsIgnoreCase("YES") &&
                !member.getFlink().equalsIgnoreCase("Spouse"))
            first += "-in-Law";

        return first;
    }

    public String getFlinkNow(String member1, String member2) {

        String flinkNow ="";
        int mem1 = mapRelation.get(member1);
        int mem2 = mapRelation.get(member2);
        flinkNow = FLINK_NOW[mem1][mem2];
        return flinkNow;
    }
}
