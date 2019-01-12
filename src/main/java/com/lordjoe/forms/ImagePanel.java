package com.lordjoe.forms;
import com.lordjoe.votebuilder.Address;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * com.lordjoe.forms.ImagePanel
 * User: Steve
 * Date: 8/31/2018
 * from https://stackoverflow.com/questions/299495/how-to-add-an-image-to-a-jpanel
 */


public class ImagePanel extends JPanel{
     public static  final Dimension DESIRRED_SIZE = new Dimension(400,400) ;

    public static final String DEFAULT_HOUSE_PICTURE = "GenericHouse.png";
    private final File pictureDirectory;
    private Address address;
    private BufferedImage image;
    private final File[] files;

    public ImagePanel(File dir,Address a) {
        if(!dir.exists())
            throw new IllegalArgumentException("bad directory " + dir);
        pictureDirectory = dir;
        files = dir.listFiles();
        setAddress(a);
        setPreferredSize(DESIRRED_SIZE);
     }

    public void setAddress(Address a) {
        if(a.equals(address))
            return;
        address = a;
        File f = buildFileFromAddress();
        try {
            image = ImageIO.read(f);
        } catch (IOException ex) {
            // handle exception...
        }
        this.repaint();

    }

    private File buildFileFromAddress() {
        File test = new File(pictureDirectory,buildFileNameFromAddress(  )) ;
        if(test.exists())
            return test;
        return new File(pictureDirectory,DEFAULT_HOUSE_PICTURE) ;
    }

    private String buildFileNameFromAddress() {
        String s = address.address + ".png";
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if(s.equalsIgnoreCase(file.getName()))
                return file.getName();
        }
        return s;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = this.getSize();
        g.drawImage(image, 0, 0, size.width,size.height,   this); // see javadoc for more info on the parameters
    }

    public static void main(String[] args) {

        File dir = new File(args[0]);
        Address addr = new Address(args[1]);
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(new ImagePanel(dir,addr), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(DESIRRED_SIZE);
        frame.setVisible(true);
    }

}
