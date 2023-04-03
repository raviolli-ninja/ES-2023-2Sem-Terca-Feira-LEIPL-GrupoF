package src;

import jdk.nashorn.internal.ir.Block;

import java.util.ArrayList;
import java.util.List;

public class Horario {

    String nome,curso;
    int numero;

    List<Block> horario = new ArrayList<Block>();

    public Horario(String nome, String curso, int numero) {
        this.nome = nome;
        this.curso=curso;
        this.numero = numero;
    }

    public Horario(String nome) {
        this.nome = nome;
    }

    public void addToHor(Block block){
        horario.add(block);
    }
}
