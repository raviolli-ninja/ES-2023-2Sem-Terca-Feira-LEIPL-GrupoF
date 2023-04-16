import src.Bloco;
import src.Horario;

import java.io.FileReader;
import java.util.ArrayList;

public class Utils {

    public static Horario parseJson(String filename) {
        Gson gson = new Gson();
        Horario horario = new Horario();
        try {
            // Lê o arquivo JSON e converte em um objeto JsonObject
            FileReader fileReader = new FileReader(filename);
            JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);

            // Extrai a lista de blocos do objeto JsonObject
            JsonArray jsonArray = jsonObject.get("horarios").getAsJsonArray();

            // Itera sobre cada elemento do JsonArray e cria um objeto Bloco para cada um
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
}
