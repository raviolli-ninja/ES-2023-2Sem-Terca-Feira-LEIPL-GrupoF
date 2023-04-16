package src;



import java.util.ArrayList;
import java.util.List;

public class Horario {

    String nome,curso;
    int numero;

    List<Bloco> horario = new ArrayList<Bloco>();

    public Horario() {

    }

    public Horario(String nome) {
        this.nome = nome;
    }

    public void addToHor(Bloco bloco){
        horario.add(bloco);
    }
}
