package src;



import java.util.ArrayList;
import java.util.List;

/**
 * OBjecto horario
 * objecto responsavel por guarda uma lista de objesctos Bloco
 */
public class Horario {

    String nome,curso;
    int numero;

    public ArrayList<Bloco> horario = new ArrayList<Bloco>();

    public Horario() { }

    public Horario(String nome) {
        this.nome = nome;
    }

    public void addToHor(Bloco bloco){
        horario.add(bloco);
    }

    public boolean isEmpty() {
        return horario.isEmpty();
    }

    public ArrayList<Bloco> getHorario () {
        return this.horario;
    }
}
