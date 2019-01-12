package com.lordjoe.whitepages;

import com.lordjoe.loeb.State;
import com.lordjoe.loeb.contributer.*;
import com.lordjoe.utilities.EquivalentNames;
import com.lordjoe.utilities.StringUtilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.lordjoe.utilities.StringUtilities.getPropertyString;

/**
 * com.lordjoe.whitepages.WhitePagesPerson
 * User: Steve
 * Date: 4/23/2018
 */
public class WhitePagesPerson {

    public final Contributer contributer;
    private String id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String suffix;
    private String name;
    private String age;
    private String employer;
    private Gender gender;

    private ContributorAddress address;
    private final List<WhitePagesPhone> phones = new ArrayList<>();

    public WhitePagesPerson(JSONObject person, Contributer c) {
        contributer = c;
        String replace = c.getId().replace("RawPerson", "Person");
        id = replace;
        person.put("loeb_id", replace);
            String employer = c.getEmployer();
        if (employer != null)
            person.put("employer", employer);
        name = (String) person.get("name");
        firstName = StringUtilities.getPropertyString(person, "firstname");
        lastName = StringUtilities.getPropertyString(person, "lastname");
        middleName = StringUtilities.getPropertyString(person, "middlename");
        suffix = StringUtilities.getPropertyString(person, "suffix");

        String gender = StringUtilities.getPropertyString(person, "gender");
        this.gender = Gender.fromString(gender);
        JSONArray addr = (JSONArray) person.get("current_addresses");
        if (addr != null && addr.length() > 0)
            address = buildAddress((JSONObject) addr.get(0));
        JSONArray phonesArr = (JSONArray) person.get("phones");
        if (phonesArr != null && phonesArr.length() > 0) {
            for (int i = 0; i < phonesArr.length(); i++) {
                PhoneNumber ph = null;
                JSONObject phoneObject = (JSONObject) phonesArr.get(i);
                try {
                    PhoneNumber.conditionNumber((String)phoneObject.get("phone_number"));
                    phones.add(new WhitePagesPhone(phoneObject));
                } catch ( Exception e) {

                }
                 
          }
        }


        //   throw new UnsupportedOperationException("Fix This"); // ToDo
    }

    private ContributorAddress buildAddress(JSONObject addr) {
        ContributorAddress ret = new ContributorAddress();
        ret.setStreet((String) addr.get("street_line_1"));
        ret.setCity((String) addr.get("city"));
        ret.setZip(new ZipCode((String) addr.get("postal_code")));
        ret.setState(State.fromString((String) addr.get("state_code")));

        return ret;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        this.lastName = lastName;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getContribution() {
        return contributer.getContribution();
    }


    public ContributorAddress getAddress() {
        return address;
    }

    public void setAddress(ContributorAddress address) {
        this.address = address;
    }

    public EMail getEmail() {
        return contributer.getEMail();
    }

    public List<WhitePagesPhone> getPhones() {
        return phones;
    }

    public int score() {
        int ret = suitabilityScore();

        if (getAge() != null)
            ret += 5;

        ret += phones.size();
        return ret;
    }

    public int adequateScore() {
        String street = contributer.getAddress().getStreet();
        if (!StringUtilities.isEmpty(street) && getAddress() != null) {
            String street1 = getAddress().getStreet();
            if (street.equalsIgnoreCase(street1))
                return 10;
        }
        return 0;
    }

    public static int score(ContributorAddress a1, ContributorAddress a2) {
        int ret = 0;
        if (a1 == null)
            return ret;
        State state = a1.getState();
        if (state == null)
            return 0;
        if (state.equals(a2.getState()))
            ret += 10;
        else
            return ret;

        String city = a1.getCity();
        if (city == null)
            return 0;

        if (city.equalsIgnoreCase(a2.getCity()))
            ret += 10;
        else
            return ret;
        if (a1.getStreet().equalsIgnoreCase(a2.getStreet()))
            ret += 10;
        ZipCode zip = a1.getZip();
        if (zip.equals(a2.getZip()))
            ret += 10;

        return ret;

    }

    public int suitabilityScore() {
        int ret = 0;
        if (getLastName().equalsIgnoreCase(contributer.getLastName()))
            ret += 10;
        else
            return ret;
        if (EquivalentNames.isEquivalent(contributer.getFirstName(),getFirstName()) )
            ret += 10;
        else
            return ret;
        ret += score(getAddress(), contributer.getAddress());

        return ret;
    }

    public String toTabbedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id);
        sb.append("\t");

        sb.append(name);
        sb.append("\t");

        sb.append(lastName);
        sb.append("\t");

        sb.append(firstName);
        sb.append("\t");

        if (middleName != null && !"null".equalsIgnoreCase(middleName))
            sb.append(middleName);
        sb.append("\t");


        if (address != null) {
            sb.append(address.getStreet());
            sb.append("\t");

            sb.append(address.getCity());
            sb.append("\t");

            sb.append(address.getState());
            sb.append("\t");

            sb.append(address.getZip());
            sb.append("\t");
        } else {
            sb.append("\t\t\t\t");

        }

        int phonesWritten = 0;
        for (WhitePagesPhone phone : phones) {
            sb.append(phone.toString());
            sb.append("\t");
            sb.append(phone.getLineType());
            sb.append("\t");
            phonesWritten++;
            if(phonesWritten >= 2)
                break;

        }
        for (int i = phonesWritten; i < 2; i++) {
            sb.append("\t");
            sb.append("\t");

        }


        if (age != null && !"null".equalsIgnoreCase(age))
            sb.append(age);
        sb.append("\t");

        sb.append(getContribution());
        sb.append("\t");

        String prop = contributer.getNotes();
        if (prop != null)
            sb.append(prop);


        return sb.toString();
    }
}
