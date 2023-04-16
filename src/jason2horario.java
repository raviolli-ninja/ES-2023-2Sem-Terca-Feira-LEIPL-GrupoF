package src;

import java.io.FileReader;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class jason2horario {
    public static ArrayList<Bloco> lerHorarios(String caminhoArquivo) {
        ArrayList<Bloco> horarios = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(caminhoArquivo));
            JSONArray jsonArray = (JSONArray) obj;
            for (Object o : jsonArray) {
                System.out.println("ola" + o);
                JSONObject jsonHorario = (JSONObject) o;
                String uc = (String) jsonHorario.get("uc");
                String turma = (String) jsonHorario.get("turma");
                String diaSem = (String) jsonHorario.get("dia_sem");
                String sala = (String) jsonHorario.get("sala");
                String curso = (String) jsonHorario.get("curso");
                String turno = (String) jsonHorario.get("turno");
                String horaInicioUC = (String) jsonHorario.get("horaInicioUC");
                String horaFimUC = (String) jsonHorario.get("horaFimUC");
                String dataAula = (String) jsonHorario.get("dataAula");
                int maxSala = ((Long) jsonHorario.get("maxSala")).intValue();
                int nInscritos = ((Long) jsonHorario.get("nInscritos")).intValue();
                horarios.add(new Bloco( uc,  turma,  diaSem, sala, maxSala, nInscritos,  horaInicioUC, horaFimUC, dataAula));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return horarios;
    }

    public static void main(String [] main){
        lerHorarios("C:\\Users\\Franc\\Downloads\\3ºProjecto\\ES-2023-2Sem-Terca-Feira-LEIPL-GrupoF\\json example.json");
    }
}
