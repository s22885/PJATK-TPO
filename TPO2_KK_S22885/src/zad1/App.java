package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class App {
    private JPanel panel1;
    private JPanel webPanel;
    private JLabel pogoda;
    private JLabel walutaDoKraju;
    private JLabel nbp;

    private Service service;
    private String pogodaVal;
    private double rate;
    private double nbpRate;



    public App(Service service,String pogodaVal,double rate,double nbpRate){
        this.service=service;
        this.pogodaVal=pogodaVal;
        this.rate=rate;
        this.nbpRate=nbpRate;

        setup();

        SwingUtilities.invokeLater(this::createGui);

    }
    private void setup(){
        DecimalFormat df =new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);

        pogoda.setText(pogodaVal);
        walutaDoKraju.setText("kurs krajowej waluty do wybranej to "+df.format(rate));
        nbp.setText("Kurs dla NBP wynosi "+df.format(nbpRate));

        JFXPanel jfxPanel=new JFXPanel();
        webPanel.add(jfxPanel);
        Platform.runLater(()->{
            WebView webView=new WebView();
            jfxPanel.setScene(new Scene(webView));
            webView.getEngine().load("https://en.wikipedia.org/wiki/"+(service.getCity().equals("")?service.getCity():service.getCity()));
        });


    }


    private void createGui(){
        JFrame frame =new JFrame("s22885");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(900,900);
        frame.getContentPane().add(panel1);
        frame.setVisible(true);
    }
}
