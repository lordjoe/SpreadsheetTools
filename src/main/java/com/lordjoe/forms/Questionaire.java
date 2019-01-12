package com.lordjoe.forms;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;

import javax.swing.*;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.lordjoe.sandhurst.Person;
import com.lordjoe.sandhurst.Sandhurst;
import com.lordjoe.sandhurst.SandhurstStreets;
import com.lordjoe.votebuilder.Address;
import com.lordjoe.votebuilder.Household;

/**
 * com.lordjoe.forms.Questionaire
 * User: Steve
 * Date: 8/29/2018
 */
public class Questionaire extends JPanel implements  ActionListener {

    public final File saveFile = new File("Sandhurst.tsv");
    public final Sandhurst neighborhood;
    public  Household selectedHousehold;
    public final JTextField firstName = new  JTextField();
    public final JTextField lastName = new  JTextField();
    public final JTextField phone = new  JTextField();
    public final JTextField email = new  JTextField();
    public final  JComboBox<SandhurstStreets> street;
    public final JComboBox<Household> address;
    public final JCheckBox emailOK;
    public final JCheckBox phoneOK;
    public final ImagePanel houseImage;
    public final FrameGrabberPanel camera;



    public Questionaire(Sandhurst snd) {
        super(new BorderLayout());
        neighborhood = snd;
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(""));
        builder.getPanel().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
          builder.appendColumn("right:pref");
        builder.appendColumn("14dlu");
        builder.appendColumn("fill:max(pref; 400px)");
        builder.appendColumn("5dlu");
        builder.appendColumn("right:pref");
        builder.appendColumn("3dlu");
        builder.appendColumn("fill:max(pref; 400px)");

        builder.append("First:", firstName);
        builder.nextLine();

        builder.append("Last:", lastName);
        builder.nextLine();



         street = new JComboBox<>(SandhurstStreets.values());

         address = new JComboBox<>( );

        SandhurstStreets selectedStreet = SandhurstStreets.values()[0];
        setStreet(selectedStreet);


        street.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String actionCommand = e.getActionCommand();
            }
        });
        street.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                   if(e.getStateChange() ==   ItemEvent.SELECTED) {
                    setStreet((SandhurstStreets) e.getItem());
                     return;
                }
            }
        });

        address.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() ==   ItemEvent.SELECTED) {
                    setHousehold((Household) e.getItem());
                    return;
                }
            }
        });


        builder.append("Street:", street);
        builder.nextLine();

        builder.append("Address:", address);
        builder.nextLine();

        builder.append("Phone:", phone);
        builder.nextLine();


        builder.append("Email:", email);
        builder.nextLine();


       emailOK = new JCheckBox("OK for Sandpoint Residents to see email") ;
        emailOK.setSelected(true);
        builder.append("",emailOK);
        builder.nextLine();

         phoneOK = new JCheckBox("OK for Sandpoint Residents to see phone number") ;
        phoneOK.setSelected(true);
        builder.append("",phoneOK);
        builder.nextLine();

        JButton submit = new JButton("Print NameTag");
        submit.addActionListener(this);
        builder.append("",submit);
        builder.nextLine();

        add(builder.getPanel(),BorderLayout.CENTER);

        JPanel pictures = new JPanel();
        pictures.setLayout(new BorderLayout());
        File house_pictures = new File("House Pictures");
        houseImage = new ImagePanel(house_pictures,((Household)address.getSelectedItem()).address);
        pictures.add(houseImage,BorderLayout.SOUTH);

        camera = new FrameGrabberPanel();
        pictures.add(houseImage,BorderLayout.SOUTH);

         add(pictures,BorderLayout.EAST);


    }

    private void setHousehold(Household item) {
        if(item == selectedHousehold )
            return;
        SandhurstStreets str =  SandhurstStreets.findStreet(item.address) ;
        SandhurstStreets[] values = SandhurstStreets.values();
        for (int i = 0; i < values.length; i++) {
            if(str == values[i]) {
                street.setSelectedIndex(i);
                break;
            }
        }
        houseImage.setAddress(item.address);
        selectedHousehold = item;
    }

    public Household getSelectedHousehold() {
        return selectedHousehold;
    }

    public void setSelectedHousehold(Household selectedHousehold) {
        this.selectedHousehold = selectedHousehold;
    }

    public void setStreet(SandhurstStreets str)   {
        int index = 0;
        Household selectedHousehold = getSelectedHousehold();
        if(selectedHousehold != null && SandhurstStreets.findStreet(selectedHousehold.address) == str)
            return;
        SandhurstStreets[] values = SandhurstStreets.values();
        for (int i = 0; i < values.length; i++) {
            if(str == values[i]) {
                street.setSelectedIndex(i);
                break;
            }
         }

        address.removeAllItems();
        List<Household> households = neighborhood.getHouseholds((SandhurstStreets) street.getSelectedItem());
        for (Household household : households) {
            address.addItem(household);
        }
        setSelectedHousehold(households.get(0));
     }



    @Override
    public void actionPerformed(ActionEvent e) {
        String fName = firstName.getText();
        String lName = lastName.getText();
        String phoneNumber = phone.getText();
        String emailAddress = email.getText();
        SandhurstStreets str = (SandhurstStreets)street.getSelectedItem();
        Household hld = (Household)address.getSelectedItem();
        boolean OKemailOK = emailOK.isSelected();
        boolean OKphone = phoneOK.isSelected();
        String name = lName + ", "  + fName;
        Person p = Person.getPerson(name,hld.address);
        if(phoneNumber.length() > 0)
            p.setPhone(phoneNumber);
        if(emailAddress.length() > 0)
            p.setEmail(emailAddress);
        p.setEmailVisabilityOK(OKemailOK);
        p.setPhoneVisabilityOK(OKphone);
        neighborhood.save(saveFile);

        clear();
    }

    public void clear() {
        firstName.setText("");
        lastName.setText("");
        phone.setText("");
        email.setText("");
        setStreet(SandhurstStreets.values()[0]);
        emailOK.setSelected(true);
        phoneOK.setSelected(true);
    }

    public static void main(String[] args) {

        File f1 = new File(args[0]);
        File f2 = new File(args[1]);

        Sandhurst snd = Household.getSandhurst( f1,f2);

        snd.save(new File("SandhurstPeople.tsv"));
        Questionaire comp = new Questionaire(snd);

        JFrame f = new JFrame("Sandhurst Questionaire");
        f.setBounds(0,0,500,500);
        f.setDefaultCloseOperation(2);
        f.add(comp);
        f.pack();
        f.setVisible(true);
    }

}
