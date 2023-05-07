package src;
import com.google.gson.*;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


public class Utils {

    /**
     *
     * este metodo converte um objecto do tipo horario para um arquivo json
     * @param horario
     * @param nomeArquivo
     * @throws IOException
     */
    public static void horarioTOJSON(Horario horario, String nomeArquivo) throws IOException {
        // Cria o objeto que irá representar o horário em JSON
        HorarioJSON horarioJSON = new HorarioJSON();
        horarioJSON.setHorarios(horario.horario);

        // Configura o Gson para imprimir o JSON formatado
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Converte o objeto para JSON
        String json = gson.toJson(horarioJSON);

        // Salva o JSON no arquivo
        try (FileWriter file = new FileWriter(nomeArquivo+".json")) {
            file.write(json);
        }
    }

    /**
     * classe auxiliar
     */
    private static class HorarioJSON {
        private ArrayList<Bloco> horarios;

        public ArrayList<Bloco> getHorarios() {
            return horarios;
        }

        public void setHorarios(ArrayList<Bloco> horarios) {
            this.horarios = horarios;
        }
    }

    /**
     * este metodo converte um objeto do tipo horario para um ficheiro csv
     * @param horario
     * @param nomeArquivo
     * @throws IOException
     */


    public static void horarioToCSV(Horario horario, String nomeArquivo) throws IOException {
        // Cria o FileWriter para escrever no arquivo
        FileWriter writer = new FileWriter(nomeArquivo+".csv");

        // Escreve o cabeçalho no arquivo
        writer.append("UC,Turma,Dia,Sala,Curso,Turno,Inicio,Fim,DataAula,MaxSala,Inscritos\n");

        // Percorre todos os blocos do horário e os escreve no arquivo
        for (Bloco bloco : horario.horario) {
            writer.append(bloco.getUc());
            writer.append(",");
            writer.append(bloco.getTurma());
            writer.append(",");
            writer.append(bloco.getDia_sem());
            writer.append(",");
            writer.append(bloco.getSala());
            writer.append(",");
            writer.append(bloco.getCurso());
            writer.append(",");
            writer.append(bloco.getTurno());
            writer.append(",");
            writer.append(bloco.getHoraInicioUC());
            writer.append(",");
            writer.append(bloco.getHoraFimUC());
            writer.append(",");
            writer.append(bloco.getDataAula());
            writer.append(",");
            writer.append(Integer.toString(bloco.getMaxSala()));
            writer.append(",");
            writer.append(Integer.toString(bloco.getnInscritos()));
            writer.append("\n");
        }

        // Fecha o FileWriter
        writer.close();
    }

    /**
     * este metodo converte um arquivo csv para um objecto do tipo horario
     * @param arquivoCSV
     * @return Horario
     * @throws IOException
     */
    public static Horario csvToHorario(String arquivoCSV) throws IOException {
        Horario horario = new Horario();
        BufferedReader br = new BufferedReader(new FileReader(arquivoCSV));
        String linha = br.readLine(); // pula a linha de cabeçalho do arquivo
         linha = br.readLine(); // pula a linha de cabeçalho do arquivo
        while ((linha = br.readLine()) != null) {
            String[] campos = linha.split(";");
            if (campos.length == 11){
                String curso = campos[0].isEmpty() ? "NA" : campos[0];
                String uc = campos[1].isEmpty() ? "NA" : campos[1];
                String turma = campos[3].isEmpty() ? "NA" : campos[3];
                String diaSem = campos[5].isEmpty() ? "NA" : campos[5];
                String sala = campos[9].isEmpty() ? "NA" : campos[9];
                int maxSala = campos[10].isEmpty() ? 0 : Integer.parseInt(campos[10]);
                int nInscritos = campos[4].isEmpty() ? 0 : Integer.parseInt(campos[4]);
                String horaInicioUC = campos[6].isEmpty() ? "NA" : campos[6];
                String horaFimUC = campos[7].isEmpty() ? "NA" : campos[7];
                String dataAula = campos[8].isEmpty() ? "NA" : campos[8];
                String turno = campos[2].isEmpty() ? "NA" : campos[2];

                Bloco bloco = new Bloco(curso,turno, uc,  turma,  diaSem, sala,  maxSala,nInscritos, horaInicioUC, horaFimUC, dataAula);
                horario.addToHor(bloco);
            }

        }

        br.close();

        return horario;
    }


    /**
     * este metodo converte um arquivo json para um objecto do tipo horario
     * @param filename
     * @return Horario
     */
    public static Horario parseJson(String filename) {
        Gson gson = new Gson();
        Horario horario = new Horario();
        try {

            FileReader fileReader = new FileReader(filename);
            JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);


            JsonArray jsonArray = jsonObject.get("horarios").getAsJsonArray();


            ArrayList<Bloco> blocos = new ArrayList<Bloco>();
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonBloco = jsonElement.getAsJsonObject();
                Bloco bloco = new Bloco();
                bloco.setUc(jsonBloco.get("uc").getAsString());
                bloco.setTurma(jsonBloco.get("turma").getAsString());
                bloco.setDia_sem(jsonBloco.get("dia_sem").getAsString());
                bloco.setSala(jsonBloco.get("sala").getAsString());
                bloco.setCurso(jsonBloco.get("curso").getAsString());
                bloco.setTurno(jsonBloco.get("turno").getAsString());
                bloco.setHoraInicioUC(jsonBloco.get("horaInicioUC").getAsString());
                bloco.setHoraFimUC(jsonBloco.get("horaFimUC").getAsString());
                bloco.setDataAula(jsonBloco.get("dataAula").getAsString());
                bloco.setMaxSala(jsonBloco.get("maxSala").getAsInt());
                bloco.setnInscritos(jsonBloco.get("nInscritos").getAsInt());
                horario.addToHor(bloco);
            }


            return horario;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * este metodo le um horario webcall e converte-o para um horarios
     * @param uri
     * @return
     */
    public static Horario fromWebcalToHorario (String uri) {
        Horario horario = new Horario();
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String httpsString = uri.replaceFirst("webcal://", "https://");
        try {
            CalendarBuilder builder = new CalendarBuilder();
            net.fortuna.ical4j.model.Calendar calendar = builder.build(new URL(httpsString).openStream());
            List<String[]> data = new ArrayList<>();
            for (Object event : calendar.getComponents(Component.VEVENT)) {
                String nome = ((VEvent) event).getSummary().getValue();
                String curso = "N/A";
                net.fortuna.ical4j.model.Date dataInicioCompleta = ((VEvent) event).getStartDate().getDate();
                Date dataFimCompleta = ((VEvent) event).getEndDate().getDate();
                String dataAula = outputDateFormat.format(dataInicioCompleta);
                String horaInicioUC = outputTimeFormat.format(dataInicioCompleta);
                String horaFimUC = outputTimeFormat.format(dataFimCompleta);
                String diaSem = "N/A";
                String salaCompleta = ((VEvent) event).getLocation().getValue();
                String[] salaArray = nome.split(",");
                String sala = salaArray[0].trim();
                int maxSala = 0;
                int nInscritos = 0;
                String turno = "N/A";
                String[] nomeArray = nome.split("-");
                String uc = nomeArray[0].trim();
                String turma = "Turma: N/A";
                Bloco bloco = new Bloco(curso, turno, uc, turma, diaSem, sala, maxSala, nInscritos, horaInicioUC, horaFimUC, dataAula);
                horario.addToHor(bloco);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException | ParserException e) {
            e.printStackTrace();
        }
        return horario;
    }


    /**
     * este metodo recebe um horario e verifica se ha salas com sobrelotação comparando o numero de inscritos com a lotação maxima da sala
     * caso a sala esteja sobrelotada o index é guradado numa lista
     * @param horario
     * @return
     */
    public static ArrayList getOverCrowded(Horario horario){
        ArrayList<Integer> index = new ArrayList<Integer>();
        for (Bloco bloco : horario.horario) {
            if (bloco.getMaxSala()< bloco.nInscritos){
                index.add(horario.horario.indexOf(bloco));
            }
        }
        int max = horario.horario.size();
        ArrayList<Integer> reversed = new ArrayList<Integer>();

        for(int i =0; i<max; i++){
            if(!index.contains(i)){
                reversed.add(i);
            }
        }
        return reversed;
    }

    /**
     * metodo que recebendo um hhorario e um tabel, recorre ao metodo gerOverCrowded e perante essa lista remove todas as linhas com esse index ficando apenas as linhas de sobrelotação
     * @param horario
     * @param table
     * @return
     */
    public static DefaultTableModel setOverCrowded(Horario horario , JTable table) {

        ArrayList<Integer> indexList = getOverCrowded(horario);


        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i =indexList.size()-1 ; i >= 0; i--) {

            model.removeRow(indexList.get(i));

        }



        return model;
    }


    /**
     * metodo que no horario procura aulas subrepostas comparando o dia a hora inicial e a sala
     * devolve uma lista de index das linhas da tabela respectivas as aulas sobrepostas
     * @param horario
     * @return
     */
    public static ArrayList getOverlap(Horario horario){
        ArrayList<Integer> index = new ArrayList<Integer>() ;
        ArrayList<Bloco> blocos = horario.horario;

        for (int i = 0; i < blocos.size(); i++) {
            Bloco bloco1 = blocos.get(i);
            for (int j = i + 1; j < blocos.size(); j++) {
                Bloco bloco2 = blocos.get(j);

                if (bloco1.getDataAula().equals(bloco2.getDataAula()) &&
                        bloco1.getHoraInicioUC().equals(bloco2.getHoraInicioUC()) &&
                        bloco1.getSala().equals(bloco2.getSala())) {


                    if(!alreadyExists(index, blocos.indexOf(bloco1))){
                        index.add(blocos.indexOf(bloco1));
                    }

                    if(!alreadyExists(index, blocos.indexOf(bloco2))){
                        index.add(blocos.indexOf(bloco2));
                    }

                }
            }


        }
        int max = horario.horario.size();
        ArrayList<Integer> reversed = new ArrayList<Integer>();

        for(int i =0; i<blocos.size(); i++){
            if(!index.contains(i)){
                reversed.add(i);
            }
        }




        return reversed;
    }

    /**
     * utilizando o metod getOverLap obtem as linhas da tabela em que as aulas estao sobrepostas, e atualiza a tabela para so mostrar as aulas sobrepostas
     * @param horario
     * @param table
     * @return
     */
    public static DefaultTableModel  setOverlap(Horario horario , JTable table) {

        ArrayList<Integer> indexList = getOverlap(horario); //0 1 2 3 5 6

        DefaultTableModel model = (DefaultTableModel) table.getModel();


        for (int i =indexList.size()-1 ; i >= 0; i--) {

            model.removeRow(indexList.get(i));

        }
        return model;

    }

    /**
     * metodo auxiliar na obtenção de index a cima referida, verifica se o index ja se encontra na lista
     * @param Blist
     * @param index
     * @return
     */
    public static boolean alreadyExists(ArrayList<Integer>Blist, int index){
        for (Integer list: Blist ){
            if (list == index){
                return true;
            }
        }
        return false;
    }


    /**
     * metodo que filtra o horario por dia mes e semana
     * recebe um horario, o tipo de filtro e uma data de uma aula existente
     * @param horario
     * @param filter
     * @param date
     * @return
     */
    public static Horario filter(Horario horario, String  filter, String date){
        Horario filtred = new Horario();
        int[] day31 = {1,3,5,7,8,10,12};


        if(checkHor(horario, date)){
            switch (filter){
                case "Dia":
                    for (Bloco bloco : horario.horario) {
                        if (bloco.getDataAula().equals(date)) {
                            filtred.addToHor(bloco);
                        }
                    }

                    break;
                case "Semana":


                    String firstday = getFday(date, horario,day31);


                    String lastday = getLday(firstday,horario,day31);
                    ArrayList<String> validDays = getValidDays(firstday,lastday,day31);
                    for (String day : validDays) {
                        System.out.println(day);
                    }





                    for (Bloco bloco : horario.horario) {
                        String data = bloco.getDataAula();

                        for (String day : validDays) {
                            if(data.equals(day)){
                                filtred.addToHor(bloco);
                            }
                        }

                    }
//


                    break;
                case "Mes":

                    String[] parseData = date.split("/");
                    int month = Integer.parseInt(parseData[1]) ;
                    int year = Integer.parseInt(parseData[2]) ;

                    for (Bloco bloco : horario.horario) {
                        String dateH = bloco.getDataAula();
                        String[] parseH = dateH.split("/");
                        int monthH = Integer.parseInt(parseH[1]) ;
                        int yearH = Integer.parseInt(parseH[2]) ;
                        if(monthH == month && yearH==year){
                            filtred.addToHor(bloco);
                        }

                    }

                    break;


            }
            return  filtred;
        }

        return horario;


    }

    /**
     * metodo auxiliar para o filtro semanal, recebendo uma data de aula obtem a data da segunda feira anterior a essa data,
     * sendo a semanda, neste casom ordenada de segunda a domingo, obtem o dia da segunda feira do inicio da semana
     * @param date
     * @param horario
     * @param array
     * @return
     */
    public static String getFday(String date, Horario horario, int[] array){
        String[] parsedate = date.split("/");
        int day = Integer.parseInt(parsedate[0]);
        int month = Integer.parseInt(parsedate[1]);

        String weekDay = "null" ;
        for (Bloco bloco : horario.horario) {
            String dataAula =  bloco.getDataAula();
            if (date.equals(dataAula)){
                weekDay = bloco.getDia_sem();
                System.out.println("dia da semana"+weekDay);
                break;
            }

        }


        switch (weekDay) {
            case "segunda":
            case "segunda-feira":
            case "Seg":
            case "seg":

                break;
            case "terça":
            case "terça-feira":
            case "terca":
            case "terca-feira":
            case  "Ter":
            case "ter":
                day--;
                break;
            case "quarta":
            case "quarta-feira":
            case "Qua":
            case "qua":
                day= day-2;
                break;
            case "quinta":
            case "quinta-feira":
            case "Qui":
            case "qui":
                day= day-3;
                break;
            case "sexta":
            case "sexta-feira":
            case "Sex":
            case "sex":
                day= day-4;
                break;
            case "sábado":
            case "sabado":
            case "Sab":
            case "sab":
                day= day-5;
                break;
            case "domingo":
            case "Dom":
            case "dom":
                day= day-6;
                break;
            default:

                break;
        }

        if (day<1){
            month--;
            if(month <1){
                month= 12;
            }
            if(month == 2){
                day = 28 -4;
            }else{
                if(check31(month,array)){
                    day= 31-4;
                }else {
                    day = 30-4;
                }
            }

        }
        String formatM = String.format("%02d", month);
        String formatD =String.format("%02d", day);
        return (formatD +"/" + formatM+ "/"+parsedate[2]);




    }

    /**
     * metodo auxiliar ao filtro por semana,
     * recebendo a data da segunda feira obtem a data de domingo.
     * @param date
     * @param horario
     * @param array
     * @return
     */
    public static String getLday(String date, Horario horario, int[] array){
        String[] parsedate = date.split("/");
        int day = Integer.parseInt(parsedate[0]);
        int month = Integer.parseInt(parsedate[1]);
        day += 6;
        if(check31(month,array)){
            if (day>31){
                month++;
                day = day-31;
            }
        }else {
            if (day>30){
                month++;
                day = day-30;
            }
        }
        String formatM = String.format("%02d", month);
        String formatD =String.format("%02d", day);
        return (formatD +"/" + formatM+ "/"+parsedate[2]);

    }

    /**
     *  metodo auxiliar no calculo da data, tanto da segunda feira como do domingo
     *  recebendo um mes e um array verifica se esse mes esta dentro do array
     *  esse array e um array dos meses que têm 31 dias
     * @param month
     * @param array
     * @return
     */
    public static boolean check31(int month, int[] array){
        for (int i = 0; i < array.length; i++) {
            if (array[i] == month) {
                return true;
            }
        }
        return false;
    }


    /**
     * metodo auxiliar ao filtro semanal,
     * recebendo uma segunda feira e um domingo calcula e guarda numa lista todas as datas dessa semana
     * @param firstday
     * @param lastday
     * @param day31
     * @return
     */
    public static ArrayList<String> getValidDays(String firstday, String lastday, int[] day31){
        ArrayList<String>days =new ArrayList<String>();
        String[] parseF = firstday.split("/");
        String[] parseL = lastday.split("/");
        int dayS = Integer.parseInt(parseF[0]);
        int monthS = Integer.parseInt(parseF[1]);
        int yearS = Integer.parseInt(parseF[2]);

            if(check31(monthS,day31)){
                //criar dias sabendo que o mes inicial da semana tem 31 dias
                int day = dayS;
                int month = monthS;
                int year = yearS;
                for (int i = 0; i < 7; i++) {
                    if(day==32){
                        day=1;
                        month++;
                    }
                    String formatD =String.format("%02d", day);
                    String formatM = String.format("%02d", month);
                    String date = formatD+"/"+formatM+"/"+year;
                    days.add(date);
                    day++;

                }
            }
            else {
                int day = dayS;
                int month = monthS;
                int year = yearS;
                for (int i = 0; i < 7; i++) {
                    if(day==31){
                        day=1;
                        month++;
                    }
                    String formatD =String.format("%02d", day);
                    String formatM = String.format("%02d", month);
                    String date = formatD+"/"+formatM+"/"+year;
                    days.add(date);
                    day++;

                }
            }

        return days;
    }

    /**
     * metodo que verifica se a data passada existe no horario passado
     * @param horario
     * @param date
     * @return
     */
    static boolean checkHor(Horario horario, String date){
        for (Bloco bloco : horario.horario) {
            String dataAula = bloco.getDataAula();
            if (date.equals(dataAula)) {
                return true;
            }
        }
        return false;
    }

    /**
     * metodo auxiliar a classe testes, usado para ler ficheiros
     * @param filePath
     * @return
     * @throws IOException
     */

    public static String readFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();
        return content.toString();
    }

    /**
     * metodo auxiliar a classe de testes, usado para apagar ficheiros
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }

}
