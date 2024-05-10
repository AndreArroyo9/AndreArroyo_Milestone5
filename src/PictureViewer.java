import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.List;


import org.jdesktop.swingx.JXDatePicker;


public class PictureViewer extends JFrame implements ActionListener {
    PicturesManager picturesManager = new PicturesManager();
    JComboBox<Photographer> photographerJComboBox;
    JXDatePicker datePicker;
    DefaultListModel<Object> model = new DefaultListModel<>();
    JList<Object> pictureList;
    JLabel photoLabel;
    JPanel area1;
    JPanel area2;
    JPanel area3;
    JPanel area4;
    JPanel areaButtonAward;
    JPanel areaButtonRemove;
    public PictureViewer(){

        // initialize
        this.setVisible(true);
        this.setLayout(new GridLayout(3,2));
        this.setPreferredSize(new Dimension(800,400));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //
        createButtonAwardArea();

        //
        createButtonRemoveArea();

        // Area 1
        createArea1();

        // Area 2
        createArea2();

        // Area 3
        createArea3();

        // Area 4
        createArea4();

        // Close database and application
        finish();
    }
    public void createButtonAwardArea(){
        areaButtonAward = new JPanel();
        areaButtonAward.setLayout(new BorderLayout());
        JButton awardButton = new JButton("AWARD");
        awardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == awardButton){
                    try {
                        int minVisits = Integer.parseInt(JOptionPane.showInputDialog("Minimum no of visist for getting a prize:"));
                        picturesManager.awardPhotographers(minVisits);
                    }catch(NumberFormatException exception){
                        JOptionPane.showMessageDialog(null, "Wrong input, try again.", "Alert", JOptionPane.ERROR_MESSAGE);
                        exception.printStackTrace();
                    }
                }
            }
        });
        areaButtonAward.add(awardButton, BorderLayout.CENTER);
        this.add(areaButtonAward);
    }
    public void createButtonRemoveArea(){
        areaButtonRemove = new JPanel();
        areaButtonRemove.setLayout(new BorderLayout());
        JButton removeButton = new JButton("REMOVE");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == removeButton){
                    for(Picture picture : picturesManager.noVisitsNoAwardedPictures()){
                        int option = JOptionPane.showConfirmDialog(null, "Do you want to delete the picture " + picture.getTitle(), "Delete Pictures" , JOptionPane.YES_NO_OPTION);
                        if (option < 1){
                            picturesManager.deletePictures(picture);
                            JOptionPane.showMessageDialog(null, "Picture" + picture.getTitle() +" deleted!");
                        }
                    }
                }
            }
        });
        areaButtonRemove.add(removeButton, BorderLayout.CENTER);
        this.add(areaButtonRemove);
    }
    public void createArea1(){
        area1 = new JPanel();
        JLabel photographerLabel = new JLabel("Photographer: ");
        Photographer[] photographers = picturesManager.photographers();
        photographerJComboBox = new JComboBox<>(photographers);
        photographerJComboBox.setSelectedItem(null);
        photographerJComboBox.addActionListener(this);
        photographerJComboBox.setPreferredSize(new Dimension(250,30));

        area1.add(photographerLabel);
        area1.add(photographerJComboBox);

        this.add(area1);
    }
    public void createArea2(){
        area2 = new JPanel();
        JLabel dateLabel = new JLabel("Photos after ");
        datePicker = new JXDatePicker();
        datePicker.addActionListener(this);
        datePicker.setPreferredSize(new Dimension(150,30));
        area2.add(dateLabel);
        area2.add(datePicker);

        this.add(area2);
    }
    public void createArea3(){
        area3 = new JPanel();

        pictureList = new JList<>();
        pictureList.setModel(model);
        pictureList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    changeImage();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(pictureList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(220,100));
        area3.add(scrollPane);

        this.add(area3);
    }

    public void changeImage(){
        Picture selectedPicture = (Picture) pictureList.getSelectedValue();
        ImageIcon imageIcon = new ImageIcon(selectedPicture.getFile());
        Image image = imageIcon.getImage().getScaledInstance(200,80,Image.SCALE_SMOOTH);
        photoLabel.setIcon(new ImageIcon(image));
        picturesManager.incrementVisits(selectedPicture);
    }

    public void createArea4(){
        area4 = new JPanel();
        area4.setLayout(new FlowLayout());
        photoLabel = new JLabel();
        area4.add(photoLabel);

        this.add(area4);
    }
    public void finish(){
        this.pack();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                picturesManager.close();
            }
        });
    }

    public static void main(String[] args) {
        PictureViewer pictureViewer = new PictureViewer();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == photographerJComboBox || e.getSource() == datePicker) {
            model.clear();
            Photographer photographer = (Photographer) photographerJComboBox.getSelectedItem();
            Date dateUtil = datePicker.getDate();
            List<Picture> picturesList;
            if (dateUtil == null){
                // All the pictures
                picturesList = picturesManager.pictures(photographer, null);
            }else {
                // Casting from util.Date to sql.Date
                java.sql.Date dateSQL = new java.sql.Date(dateUtil.getTime());
                // Only pictures since selected date
                picturesList = picturesManager.pictures(photographer, dateSQL);
            }
            model.addAll(picturesList);
        }
    }
}
