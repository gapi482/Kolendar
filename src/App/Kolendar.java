package App;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;


public class Kolendar extends Frame {

    private static final String[] monthNames = {"Januar", "Februar", "Marec", "April", "Maj", "Junij", "Julij", "August", "September", "Oktober", "November", "December"};
    private static final int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static String[] weekDays = {"Ponedeljek", "Torek", "Sreda", "Četrtek", "Petek", "Sobota", "Nedelja"};
    private static int leto = 2023;
    private static int mesec = 3;
    private static int dni = 30;
    private static JLabel datum;
    private static JTable tabela;
    private static DefaultTableModel model;
    private static int[] praznikvmescu = new int[3];
    private static String[][] Praznik;


    public static void main(String[] args) throws FileNotFoundException {
        JFrame frame = new JFrame("Kolendar");
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        String date = monthNames[mesec] + " " + leto;
        datum = new JLabel(date);
        frame.add(datum);
        JButton button = new JButton("POJDI V LETO");
        JTextField leto1 = new JTextField(16);
        frame.add(button);
        frame.add(leto1);


        JComboBox meseci = new JComboBox(monthNames);
        meseci.setBounds(200, 200, 20, 40);
        frame.add(meseci);


        if (leto % 4 == 0) {
            if (leto % 100 == 0)
                if (leto % 400 == 0)
                    monthDays[1] = 29; //leap year
        }
        String holiday = getHoliday();
        assert holiday != null;
        String[] prazniki = holiday.split(",");

        Praznik = new String[prazniki.length][2];
        for (int i = 0; i < prazniki.length; i++) {
            Praznik[i] = prazniki[i].split("\\.");

        }
        ;
        int dayss = 0;
        praznikvmescu[1] = 0;
        praznikvmescu[0] = 0;
        for (String[] value : Praznik) {
            if (Integer.parseInt(value[1]) - 1 == mesec) {
                praznikvmescu[dayss] = Integer.parseInt(value[0]);
                dayss++;
            }

        }
        praznikvmescu[2] = mesec;

        String[][] dnevi = Tabela(praznikvmescu);
        model = new DefaultTableModel();
        for (String weekDay : weekDays) {
            model.addColumn(weekDay);
        }
        for (String[] strings : dnevi) {
            model.addRow(strings);
        }


        tabela = new JTable(model);
        TableColumn tColumn = tabela.getColumnModel().getColumn(6);
        tColumn.setCellRenderer(new ColumnColorRenderer(Color.GRAY));


        JScrollPane pane = new JScrollPane(tabela);
        frame.add(pane);


        button.addActionListener(e -> {
            leto = Integer.parseInt(leto1.getText());
            mesec = meseci.getSelectedIndex();
            dni = monthDays[mesec];
            datum.setText(monthNames[mesec] + " " + leto);

            if (leto % 4 == 0) {
                if (leto % 100 == 0)
                    if (leto % 400 == 0)
                        monthDays[1] = 29; //leap year
            }


            int days = 0;
            praznikvmescu[1] = 0;
            praznikvmescu[0] = 0;
            for (String[] value : Praznik) {
                if (Integer.parseInt(value[1]) - 1 == mesec) {
                    praznikvmescu[days] = Integer.parseInt(value[0]);
                    days++;
                }

            }

            praznikvmescu[2] = mesec;

            String[][] dnevi1 = Tabela(praznikvmescu);
            model = (DefaultTableModel) tabela.getModel();
            model.setRowCount(0);
            for (String[] strings : dnevi1) {
                model.addRow(strings);
            }
        });

        frame.show();
    }


    public static String[][] Tabela(int[] prostidnevi) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(leto, mesec, 1);

        int danvtednu = calendar.get(Calendar.DAY_OF_WEEK);
        dni = monthDays[mesec];
        if (danvtednu == 1) //reset da je nedelja zadnja v tednu
            danvtednu = 8;
        String[][] dnevi = new String[6][7];
        int days = 1;
        for (int i = 0; i < 6; i++) {
            for (int j = 1; j < 8; j++) {
                if (days > dni) {
                    dnevi[i][j - 1] = "";
                } else {
                    if (j >= danvtednu - 1 && i == 0) {
                        if (mesec == prostidnevi[2] && (days == prostidnevi[0] || days == prostidnevi[1])) {
                            dnevi[i][j - 1] = String.valueOf(days) + " Praznik";
                        } else
                            dnevi[i][j - 1] = String.valueOf(days);
                        days++;
                    } else if (i != 0) {
                        if (mesec == prostidnevi[2] && (days == prostidnevi[0] || days == prostidnevi[1])) {
                            dnevi[i][j - 1] = String.valueOf(days) + " Praznik";
                        } else
                            dnevi[i][j - 1] = String.valueOf(days);
                        days++;
                    } else
                        dnevi[i][j - 1] = "";
                }
            }
        }
        return dnevi;
    }


    public static String getHoliday() {
        try {
            File myObj = new File("praznik.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String holiday = myReader.nextLine();
                //System.out.println(holiday);
                return holiday;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();

        }
        return null;
    }
}

class ColumnColorRenderer extends DefaultTableCellRenderer {
    Color backgroundColor;

    public ColumnColorRenderer(Color backgroundColor) {
        super();
        this.backgroundColor = backgroundColor;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        cell.setBackground(backgroundColor);
        return cell;
    }
}