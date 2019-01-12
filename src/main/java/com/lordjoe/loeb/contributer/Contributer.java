package com.lordjoe.loeb.contributer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * com.lordjoe.loeb.contributer.Contributer
 * User: Steve
 * Date: 4/13/2018
 *
 * //Find PersonTRIAL	c05ee4dcce524045869365cecbff4ce7
 */
public class Contributer {
    private String id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String suffix;
    private String employment;
    private String employer;
     private String notes;
    private int contribution;
    private final List<EMail> emails = new ArrayList<>();
    private final List<PhoneNumber> phones = new ArrayList<>();
    private final List<ContributorAddress> addresses = new ArrayList<>();

    public Contributer(String[] strings) {

        String address = null;
        String city = null;
        String state = null;
        String zip = null;
        String phone = null;

        String email = null;

        boolean interesting = false;

        for (String string : strings) {
            string = string.trim();
            if(string.contains("Hubbell"))
                interesting = true;
            if(string.contains("Olson"))
                interesting = true;

        }

        if(interesting)
            System.out.println(strings[2]);

        int index = 0;
        if(strings.length > index)
            id = strings[index++] ;                                                         // 0
        if(strings.length > index)              // 1
            notes = strings[index++] ;
        if(strings.length > index) {
            setLastName(strings[index++]);
         }
        if(strings.length > index)
            firstName = strings[index++] ;   // 3
        if(strings.length > index)
            middleName = strings[index++] ;   // 4
        if(strings.length > index)
            suffix = strings[index++] ;      // 5
        if(strings.length > index)
            address = strings[index++] ;      // 6
        if(strings.length > index)
            city = strings[index++] ;       // 7
        if(strings.length > index)
            state = strings[index++] ;      // 8
        if(strings.length > index)
            zip = strings[index++] ;        // 9
        if(strings.length > index)
            phone = strings[index++] ;      // 10

        if(strings.length > index)
            employment = strings[index++] ;      // 11
        if(strings.length > index)
            employer = strings[index++] ;        // 12
          if(strings.length > index) {
              String string = strings[index++];
              contribution = asInt(string);        // 13
          }
        if(strings.length > index)
            email = strings[index++] ;        // 14

        while(strings.length > index  )   {
             String test =  strings[index++].trim() ;
             if(test.startsWith("$"))
                 System.out.println(test);
         }


        maybeAddPhone(phone) ;
        maybeAddEmail(email) ;
        maybeAddAddress(address,city,state,zip) ;

    }

    private int asInt(String string) {
          string =  string.trim();
        if(string.length() == 0)
            return 0;
        string  = string.replace("$","");
        string  = string.replace(",","");
        try {
            return Integer.parseInt(string) ;
        } catch (NumberFormatException e) {
            return 0;

        }
    }

    public void maybeAddAddress( String address,
            String city,
            String state,
            String zip) {
        if(city == null || city.length() ==0 )
            return;
        if(state == null || state.length() ==0 )
            return;

        addresses.add(new ContributorAddress(address,city,state,zip));
    }

    public void maybeAddEmail(String email) {
        if(email == null || email.length() ==0 )
            return;
        email = EMail.conditionEmail(email);
        emails.add(new EMail(email));
    }

    public void maybeAddPhone(String phone) {
        if(phone == null || phone.length() ==0 )
            return;

        try {
            maybeAddPhone(  phone,0);
          } catch (Exception e1) {
            System.out.println("Acnnot Handle Phone " + phone);

        }
    }

    private void maybeAddPhone(String phone, int i) {
        StringBuilder sb = new StringBuilder();

        for( ; i < phone.length(); i++)    {
            char c = phone.charAt(i);
            if(Character.isDigit(c))
                sb.append(c);
            if(sb.length() >= 10)
                break;
         }
         if(sb.length() < 10)
             return;
         phones.add(new PhoneNumber(sb.toString()));
         if(i < phone.length())
             maybeAddPhone(  phone,   i);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if((firstName == null || firstName.isEmpty()) && lastName.contains(","))  {
            String[] names =  lastName.split(",");
            firstName = names[1].trim();
            this.lastName = names[0].trim();
        }
        else {
            this.lastName = lastName.trim();

        }
    }

    public ContributorAddress getAddress()
    {
        return BaseVerifiedEntity.bestMember(addresses);
    }
    public PhoneNumber getPhoneNumber()
    {
        return BaseVerifiedEntity.bestMember(phones);
    }


    public EMail getEMail()
    {
        return BaseVerifiedEntity.bestMember(emails);
    }


    @Override
    public String toString() {
        return getFirstName() + " "  + getLastName();
    }

       public String getContributionString()
       {
           if(contribution <= 0)
               return "";
           return "$" + contribution;
       }

       public String toPhoneList()
       {
           StringBuilder sb = new StringBuilder();

            for (PhoneNumber phone : phones) {
              sb.append(phone.toString());
              sb.append(" ");
           }
           return sb.toString();
       }

    public String toTabbedLine() {
        StringBuilder sb = new StringBuilder();
        BaseVerifiedEntity.appendNotNull(id,sb);
        BaseVerifiedEntity.appendNotNull(notes,sb);
        BaseVerifiedEntity.appendNotNull(lastName,sb);
        BaseVerifiedEntity.appendNotNull(firstName,sb);
        BaseVerifiedEntity.appendNotNull(middleName,sb);
        BaseVerifiedEntity.appendNotNull(suffix,sb);
        ContributorAddress addr = getAddress();
        if(addr == null)
            sb.append("\t\t\t\t");
        else {
            String str = addr.toTabbedString();
            sb.append(str);
        }

        sb.append(toPhoneList());
        sb.append("\t");


        BaseVerifiedEntity.appendNotNull(employment,sb);
        BaseVerifiedEntity.appendNotNull(employer,sb);
         sb.append(getContributionString());

        EMail eMail = getEMail();
        BaseVerifiedEntity.appendNotNull(eMail,sb);


        return sb.toString();
    }


    public boolean isValidName( ) {
        String firstName =  getFirstName();
        if(firstName == null || firstName.isEmpty())
            return false;
        firstName =  getLastName();
        if(firstName == null || firstName.isEmpty())
            return false;
        return true;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getContribution() {
        return contribution;
    }

    public void setContribution(int contribution) {
        this.contribution = contribution;
    }
}
