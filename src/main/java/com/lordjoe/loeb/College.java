package com.lordjoe.loeb;

import com.lordjoe.loeb.contributer.InstitutionType;

import java.util.*;

/**
 * com.lordjoe.loeb.College
 * User: Steve
 * Date: 11/12/2018
 */
public class College implements Comparable<College>{

    public static final String HEADER_LINE = "Name\tURL\tCity\tState\tAddress\tEnrollment\tType\tPhone\tNotes";
    private static Map<String,College>  byName = new HashMap<>();
    private static Map<String,College>  byUrl = new HashMap<>();

    public static College makeUnregisteredCollege(String name,State state,String city,String url)  {
        College  ret = new College(name) ;
        ret.setCity(city);
        ret.setState(state);
        ret.setUrl(url);
          return ret;
    }


    public List<College> getCollegesInState(State s)   {
        List<College> ret = new ArrayList<>() ;
        for (College value : byName.values()) {
              if(s == value.getState())
                  ret.add(value);

        }
        Collections.sort(ret);
        return ret;
    }


    public static void registerCollege(College c)  {
        if(byName.containsKey(c.name))
            return;
        byName.put(c.name,c);

    }

    public static College getCollege(String name)  {
        College ret = byName.get(name);
        if(ret == null ) {
            ret = new College(name) ;
            byName.put(name,ret);
        }
        return ret;
    }
    public static College getKnownCollege(String name)  {
        College ret = byName.get(name);
        return ret;
    }
    public static College getByUrl(String name)  {
        name = name.toLowerCase();
        College ret = byUrl.get(name );
        if(ret == null)  {
            for (int i = 0; i < name.length() -3; i++) {
                if(name.charAt(i) == '.') {
                    ret = byUrl.get(name.substring(i + 1));
                    if(ret != null)
                        return ret;
                }

            }
         }
        return ret;
    }

    @Override
    public String toString() {
        return name;
    }

    public static College fromTabbedString(String s)
    {
        String[] split = splitTabbedList(s,8) ;
        int index = 0;
        College ret = getCollege(split[index++]);
        ret.setUrl(split[index++]);
        ret.setCity(split[index++]);
        ret.setState(State.fromString(split[index++]));
        ret.setAddress(split[index++]);

        String enrollment = split[index++];

        try {
            if(enrollment != null && enrollment.length() > 0)
               ret.setEnrollment(Integer.parseInt(enrollment));
        } catch (NumberFormatException e) {
            System.out.println(ret.name + " enrollment " + enrollment);

        }
        ret.setType(InstitutionType.fromSting(split[index++]));
        if(split.length < index - 1)
            ret.setPhone(split[index++]);
        if(split.length < index - 1)
            ret.setNotes(split[index++]);
        return ret;
    }


    public static String[] splitTabbedList(String in,int expected) {
        String[] ret = new String[expected];
        StringTokenizer st = new StringTokenizer(in, "\t");
        int index = 0;
        while (index < expected && st.hasMoreTokens()) {
           ret[index++] = st.nextToken();
        }
        return ret;
    }

    public final String name;
    private String url;
    private int enrollment;
     private String address;
    private State state;
    private String City;
    private InstitutionType type;
    private String phone;
    private String notes;

    private College(String name) {
        this.name = name;
    }


    public String toTabbedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append('\t');
        if(url != null)
          sb.append(url);
        sb.append('\t');
        if(City != null)
            sb.append(City);
        sb.append('\t');
        if(state != null)
            sb.append(state.getAbbreviation());
        sb.append('\t');
        if(address != null)
            sb.append(address);
        sb.append('\t');
        sb.append(Integer.toString(enrollment));
        sb.append('\t');
        if(type != null)
            sb.append(type.toString());
          sb.append('\t');
        if(phone != null)
            sb.append(phone);
        sb.append('\t');
        if(notes != null)
            sb.append(notes);

        return sb.toString();

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url.trim().toLowerCase();
        byUrl.put(url,this);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if(this.phone != null &&  !this.phone.equalsIgnoreCase(phone)) {
            System.out.println(name + " phone old " + this.phone + " new " + phone);
        }
        this.phone = phone;
    }

    public int getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(int enrollment) {
        if(this.enrollment > 0 && this.enrollment != enrollment) {
            System.out.println(name + " Enrollment old " + this.enrollment + " new " + enrollment);
        }
        this.enrollment = enrollment;
    }

    public String getAddress() {
       return address;
    }

    public void setAddress(String address) {
        if(this.address != null && this.address.length() > 2 &&  !this.address.equals(address)) {
            System.out.println(name + " address old " + this.address + " new " + address);
        }
        this.address = address;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        if(this.City != null &&  !this.City.equalsIgnoreCase(city)) {
            System.out.println(name + " city old " + this.City + " new " + city);
        }
        City = city;
    }

    public InstitutionType getType() {
        return type;
    }

    public void setType(InstitutionType type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int compareTo(College o) {
        return name.compareTo(o.name);
    }
}
