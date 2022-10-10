import model.Participant;
import service.FileService;
import service.ParticipantService;
import sign.Sign;
import sign.factory.SignFactory;
import service.SignService;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private static final String USER_DIR = System.getProperty("user.dir");

    public static void main(String[] args) {

        FileService fileService = new FileService();
        SignService signService = new SignService();
        ParticipantService participantService = new ParticipantService();

        Path groupFile = Path.of(USER_DIR, "files", "azerbaijao.txt");

        fileService.write(groupFile, participantService.findAll());

        List<String> fileRead = fileService.readLines(groupFile);
        fileRead.parallelStream().forEach(s -> {
            List<String> list = Arrays.asList(s.split(";"));

            Participant participant = new Participant(list.get(0), list.get(1), LocalDateTime.parse(list.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            LocalDateTime localDateTime = participant.getDate();

            String name = participant.getName();
            String zone = participant.getZone();
            String timeZone = String.valueOf(signService.timeZone(localDateTime, participant.getZone()));
            int age = signService.getAge(localDateTime);
            boolean isLeapYear = signService.isLeapYear(localDateTime);
            String formattedDate = signService.format(localDateTime);
            Sign sign = new SignFactory().create(MonthDay.of(localDateTime.getMonth(), localDateTime.getDayOfMonth()));
            String risingSign = sign.getRisingSign(localDateTime.toLocalTime());

            Path participantFile = Path.of(USER_DIR, "files", participant.getName().replaceAll(" ", "_") + ".txt");
            String info = "Name: " + name + "\n" +
                    "Zone: " + zone + "\n" +
                    "TimeZone: " + timeZone + "\n" +
                    "Age: " + age + "\n" +
                    "Leap Year: " + isLeapYear + "\n" +
                    "Birth Date: " + formattedDate + "\n" +
                    "Sign: " + sign + "\n" +
                    "Rising Sign: " + risingSign + "\n";

            fileService.write(participantFile, info);
        });

    }
}