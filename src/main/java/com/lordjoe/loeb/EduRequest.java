package com.lordjoe.loeb;

import java.util.HashMap;
import java.util.Map;

/**
 * com.lordjoe.loeb.EduRequest
 * User: Steve
 * Date: 4/4/2018
 */
public class EduRequest {

    public static final Map<String,EduRequest> byEmail = new HashMap<>();

    public static EduRequest getByEMail(String email)  {
        return byEmail.get(email);
    }

    public final String Timestamp;
    public final String book1;
    public final String book2;
    public final String Full_Name;
    public final String Professional_Title;
    public final String School_Name;
    public final String Department;
    public final String SchoolPostal_Address1;
    public final String SchoolPostal_Address2;
    public final String City;
    public final State state;
    public final String ZipCode;
    public final String Email;



    public EduRequest(String[] data) {
        int index = 0;
        Timestamp = data[index++];
        book1 = data[index++];;
        book2 = data[index++];;
        Full_Name = data[index++];
        Professional_Title = data[index++];
        School_Name = data[index++];
        Department = data[index++];
        SchoolPostal_Address1 =data[index++];
        SchoolPostal_Address2 = data[index++];
        City = data[index++];
        this.state = State.fromString(data[index++]);
        ZipCode = data[index++];
        Email = data[index++].toLowerCase();
        byEmail.put(Email,this) ;
        Institution.registerInstitution(this);
    }

    @Override
    public String toString() {
        return   Timestamp + "\t" +
                book1 +  "\t" +
                book2 +  "\t" +
                Full_Name +  "\t" +
                Professional_Title +  "\t" +
                School_Name +  "\t" +
                Department +  "\t" +
                SchoolPostal_Address1 +  "\t" +
                SchoolPostal_Address2 +  "\t" +
                City +  "\t" +
                state +  "\t" +
                ZipCode +  "\t" +
                Email;
    }


}
