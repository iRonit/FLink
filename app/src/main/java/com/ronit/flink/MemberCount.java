package com.ronit.flink;

import android.content.SharedPreferences;

import info.ronit.db.model.*;


/**
 * Created by Ronit on 5/25/2015.
 */
public class MemberCount {

    MemberCount() {
    }

    public void addcount(MemberDetail member, SharedPreferences pref) {

        String check_rel = member.getFlink();
        SharedPreferences.Editor editor = pref.edit();

        int c = pref.getInt(check_rel,0);
        editor.putInt(check_rel,c+1);
        editor.putInt("Total",pref.getInt("Total",0)+1);
        editor.commit();

    }

    public void subcount(MemberDetail member, SharedPreferences pref) {

        String check_rel = member.getFlink();
        SharedPreferences.Editor editor = pref.edit();

        int c = pref.getInt(check_rel,0);
        editor.putInt(check_rel,c-1);
        editor.putInt("Total",pref.getInt("Total",-1)-1);
        editor.commit();

    }

    public void refresh(String check_rel, int count, SharedPreferences pref) {

        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(check_rel,count);
        editor.commit();

    }


    public String get_total(SharedPreferences pref) {
        return String.valueOf(pref.getInt("Total",0));
    }

    public String get_child(SharedPreferences pref) {
        return String.valueOf(pref.getInt("Child",0));
    }

    public String get_cousin(SharedPreferences pref) {
        return String.valueOf(pref.getInt("Cousin",0));
    }

    public String get_gchild(SharedPreferences pref) {
        return String.valueOf(pref.getInt("Grand-Child",0));
    }

    public String get_gparent(SharedPreferences pref) {
        return String.valueOf(pref.getInt("Grand-Parent",0));
    }

    public String get_parent(SharedPreferences pref) {
        return String.valueOf(pref.getInt("Parent",0));
    }

    public String get_psibling(SharedPreferences pref) {
        return String.valueOf(pref.getInt("Parent's Sibling",0));
    }

    public String get_sibling(SharedPreferences pref) {
        return String.valueOf(pref.getInt("Sibling",0));
    }

    public String get_schild(SharedPreferences pref) {
        return String.valueOf(pref.getInt("Sibling's Child",0));
    }

    public String get_spouse(SharedPreferences pref) {
        return String.valueOf(pref.getInt("Spouse",0));
    }

}