import java.io.*;
import java.net.*;
import java.util.*;
import net.fortuna.ical4j.data.*;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.*;
import net.fortuna.ical4j.model.property.*;
import com.opencsv.*;

public class WebcalToCsv {

    public static void main(String[] args) throws IOException, ParserException {

        // Define the webcal URL and CSV file path
        String webcalUrl = "https://example.com/calendar.ics";
        String csvFilePath = "calendar.csv";

        // Read the webcal URL and parse the iCalendar data
        URL url = new URL(webcalUrl);
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(url.openStream());

        // Extract the events from the iCalendar data and convert them to CSV format
        List<String[]> rows = new ArrayList<>();
        for (Component component : calendar.getComponents(Component.VEVENT)) {
            String[] row = new String[4];
            row[0] = component.getProperty(Property.SUMMARY).getValue();
            row[1] = component.getProperty(Property.DESCRIPTION).getValue();
            row[2] = component.getProperty(Property.DTSTART).getValue();
            row[3] = component.getProperty(Property.DTEND).getValue();
            rows.add(row);
        }

        // Write the CSV data to a file
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            writer.writeNext(new String[] { "Summary", "Description", "Start Time", "End Time" });
            writer.writeAll(rows);
        }

        System.out.println("Data exported to " + csvFilePath);
    }

}
