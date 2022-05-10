/**
 * @author Klik Konrad S22885
 */


package zad1;



public class Main  {
    public static void main(String[] args) {
        Service s = new Service("Italy");
        String weatherJson = s.getWeather("Rome");
        Double rate1 = s.getRateFor("USD");
        Double rate2 = s.getNBPRate();
        // ...
        // część uruchamiająca GUI

        App app=new App(s,weatherJson,rate1,rate2);


    }


}
