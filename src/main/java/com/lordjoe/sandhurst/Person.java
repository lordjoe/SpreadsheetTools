package com.lordjoe.sandhurst;

import com.lordjoe.votebuilder.Address;
import com.lordjoe.votebuilder.Party;

import java.util.*;

/**
 * com.lordjoe.sandhurst.Person
 * User: Steve
 * Date: 8/12/2018
 */
public class Person implements Comparable<Person> {

    public static Map<String,Person>  knownPeople = new HashMap<>();

    public static Person getPerson(String name,Address adress) {
        Person ret = knownPeople.get(name.trim()) ;
        if(ret == null)    {
            ret = new Person(name,adress);
            knownPeople.put(name,ret);
        }
        return ret;
    }
    public final String name;
    public final Address address;
    public   String _email;
    public   String _phone;
    public   Integer _age;
    private Party _party;
    private boolean visabilityOK;
    private boolean emailVisabilityOK;
    private boolean phoneVisabilityOK;
    private Calendar birthday;



    private Person(String name, Address address ) {
        this.name = name.trim();
        this.address = address;
     }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String _email) {
         if(_email == null)
             return;

         this._email = _email;
    }

    public String getPhone() {
        return _phone;
    }

    public void setPhone(String _phone) {
        if(_phone == null)
            return;

        this._phone = _phone;
    }

    public Integer getAge() {
        return _age;
    }

    public void setAge(Integer _age) {
        if(_age == null)
            return;
        this._age = _age;
    }

    public boolean isVisabilityOK() {
        return visabilityOK;
    }

    public void setVisabilityOK(boolean visabilityOK) {
        this.visabilityOK = visabilityOK;
    }

    public boolean isEmailVisabilityOK() {
        return emailVisabilityOK;
    }

    public void setEmailVisabilityOK(boolean emailVisabilityOK) {
        this.emailVisabilityOK = emailVisabilityOK;
    }

    public boolean isPhoneVisabilityOK() {
        return phoneVisabilityOK;
    }

    public void setPhoneVisabilityOK(boolean phoneVisabilityOK) {
        this.phoneVisabilityOK = phoneVisabilityOK;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Voter{" +
                "name='" + name + '\'' +
                ", address=" + address +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                '}';
    }


    public Party getParty() {
        return _party;
    }

    public void setParty(Party _party) {
        if(_party == null)
            return;
        this._party = _party;
    }


    public String toTabbedLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("\t");
        sb.append(address.address);
        sb.append("\t");
        String phone = getPhone();
        if (phone != null)
            sb.append(getPhone());
        sb.append("\t");
        if (getEmail() != null)
            sb.append(getEmail());
        sb.append("\t");
          if (_party != null)
            sb.append(getParty());
        sb.append("\t");
        if (getBirthday() != null)
            sb.append(getBirthday());
        sb.append("\t");
        if (getAge() != null)
            sb.append(getAge());
        sb.append("\t");
        sb.append(Boolean.toString(isVisabilityOK()));
        sb.append("\t");
        sb.append(Boolean.toString(isEmailVisabilityOK()));
        sb.append("\t");
        sb.append(Boolean.toString(isPhoneVisabilityOK()));
        sb.append("\t");

        return sb.toString();
    }



    public static String toTabbedHeadderLine() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name");
        sb.append("\t");
        sb.append("Address");
        sb.append("\t");
        sb.append("Phone");
        sb.append("\t");
        sb.append("Email");
        sb.append("\t");
        sb.append("Party");
        sb.append("\t");
        sb.append("Birthday");
        sb.append("\t");
        sb.append("Age");
        sb.append("\t");
            sb.append("Visibility OK ");
        sb.append("\t");
        sb.append("See eMail OK ");
        sb.append("\t");
        sb.append("See phone OK ");
        sb.append("\t");
         return sb.toString();
    }


    @Override
    public int compareTo(Person o) {
        return name.compareTo(o.name);
    }


}
