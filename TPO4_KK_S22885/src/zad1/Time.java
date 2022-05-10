/**
 * @author Klik Konrad S22885
 */

package zad1;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {
    private static final String timeZone = "Europe/Warsaw";
    private static final Locale loc = Locale.forLanguageTag("pl");

    public static String passed(String from, String to) {
        StringBuilder res = new StringBuilder();
        if (from.contains("T") && to.contains("T")) {
            try {
                ZonedDateTime fromDate = ZonedDateTime.of(LocalDateTime.parse(from, DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneId.of(timeZone));
                ZonedDateTime toDate = ZonedDateTime.of(LocalDateTime.parse(to, DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneId.of(timeZone));

                Period period = Period.between(fromDate.toLocalDate(), toDate.toLocalDate());
                int d = period.getDays();
                int m = period.getMonths();
                int y = period.getYears();

                res.append("Od ")
                        .append(fromDate.getDayOfMonth())
                        .append(" ")
                        .append(fromDate.getMonth().getDisplayName(TextStyle.FULL, loc))
                        .append(" ")
                        .append(fromDate.getYear())
                        .append(" (")
                        .append(fromDate.getDayOfWeek().getDisplayName(TextStyle.FULL, loc))
                        .append(") godz. "+fromDate.getHour()+":"+(fromDate.getMinute()==0?"00":fromDate.getMinute())+" do ")
                        .append(toDate.getDayOfMonth())
                        .append(" ")
                        .append(toDate.getMonth().getDisplayName(TextStyle.FULL, loc))
                        .append(" ")
                        .append(toDate.getYear())
                        .append(" (")
                        .append(toDate.getDayOfWeek().getDisplayName(TextStyle.FULL, loc))
                        .append(") godz. "+toDate.getHour()+":"+(toDate.getMinute()==0?"00":toDate.getMinute())+"\n");
                long days = ChronoUnit.DAYS.between(fromDate.toLocalDate(), toDate.toLocalDate());
                res.append(" - mija: ")
                        .append(days)
                        .append(" "+(days==1?"dzień":"dni")+", tygodni ")
                        .append(roundTo2DecimalPlace(((double)days)/7))
                        .append("\n");
                res.append(" - godzin: ")
                        .append(ChronoUnit.HOURS.between(fromDate, toDate))
                        .append(", minut: ")
                        .append(ChronoUnit.MINUTES.between(fromDate, toDate));

                kalendarz(res, d, m, y);
            } catch (DateTimeParseException e) {
                return "*** " + e;
            }
        } else {
            try {
                {
                    LocalDate fromDate = LocalDate.parse(from, DateTimeFormatter.ISO_LOCAL_DATE);
                    LocalDate toDate = LocalDate.parse(to, DateTimeFormatter.ISO_LOCAL_DATE);

                    Period period = Period.between(fromDate, toDate);
                    int d = period.getDays();
                    int m = period.getMonths();
                    int y = period.getYears();

                    res.append("Od ")
                            .append(fromDate.getDayOfMonth())
                            .append(" ")
                            .append(fromDate.getMonth().getDisplayName(TextStyle.FULL, loc))
                            .append(" ")
                            .append(fromDate.getYear())
                            .append(" (")
                            .append(fromDate.getDayOfWeek().getDisplayName(TextStyle.FULL, loc))
                            .append(") do ")
                            .append(toDate.getDayOfMonth())
                            .append(" ")
                            .append(toDate.getMonth().getDisplayName(TextStyle.FULL, loc))
                            .append(" ")
                            .append(toDate.getYear())
                            .append(" (")
                            .append(toDate.getDayOfWeek().getDisplayName(TextStyle.FULL, loc))
                            .append(")\n");
                    long days = ChronoUnit.DAYS.between(fromDate, toDate);
                    res.append(" - mija: ")
                            .append(days)
                            .append(" "+(days==1?"dzień":"dni")+", tygodni ")
                            .append(roundTo2DecimalPlace(((double)days)/7));


                    kalendarz(res, d, m, y);

                }
            } catch (DateTimeParseException e) {
                return "*** " + e;
            }
        }


        return res.toString();

    }

    private static void kalendarz(StringBuilder res, int d, int m, int y) {
        if (d != 0 || m != 0 || y != 0) {
            boolean was = false;
            res.append("\n")
                    .append(" - kalendarzowo: ");
            if (y != 0) {
                was = true;
                res.append(y)
                        .append(" ");
                if (y == 1) {
                    res.append("rok");
                } else if (y < 10) {
                    res.append("lata");
                } else {
                    res.append("lat");
                }
            }
            if (m != 0) {
                if (was) {

                    res.append(", ");
                }
                was = true;
                res.append(m).append(" ");

                if (m == 1) {
                    res.append("miesiąc");
                } else if (m < 10) {
                    res.append("miesiące");
                } else {
                    res.append("miesięcy");
                }

            }
            if (d != 0) {
                if (was) {

                    res.append(", ");
                }
                was = true;
                res.append(d).append(" ");
                if (d == 1) {
                    res.append("dzień");
                } else {
                    res.append("dni");
                }

            }
        }
    }

    public static double roundTo2DecimalPlace(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
