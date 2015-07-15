package info.ronit.db.model;

import java.io.Serializable;

/**
 * Created by Ronit on 3/30/2015.
 */

public class MemberDetail implements Serializable {
    int id;
    String name;
    String gender;
    String age;
    String relation;
    String rname;
    String flink;
    String inlaw;
    byte[] image;

    public MemberDetail(){   }

    public MemberDetail(int id, String name, String gender, String age, String relation, String rname, String flink, String inlaw){
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.relation = relation;
        this.rname=rname;
        this.flink=flink;
        this.inlaw = inlaw;
    }


    public int getID(){
        return this.id;
    }

    public void setID(int id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getGender(){
        return this.gender;
    }


    public void setGender(String gender){
        this.gender = gender;
    }

    public String getAge(){
        return this.age;
    }

    public void setAge(String age){
        this.age = age;
    }

    public String getRelation(){
        return this.relation;
    }

    public void setRelation(String relation){
        this.relation = relation;
    }

    public String getRname(){
        return this.rname;
    }

    public void setRname(String rname){
        this.rname = rname;
    }

    public String getFlink(){
        return this.flink;
    }

    public void setFlink(String flink){
        this.flink = flink;
    }

    public String getInlaw(){
        return this.inlaw;
    }

    public void setInlaw(String inlaw){
        this.inlaw = inlaw;
    }

    public byte[] getImage(){
        return this.image;
    }

    public void setImage(byte[] image){
        this.image = image;
    }
}
