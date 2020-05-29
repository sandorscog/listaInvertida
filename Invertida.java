import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;

class Invertida{

  RandomAccessFile termos;
  RandomAccessFile IDs;

  public static void main(String[] args){
    Invertida lista = new Invertida();
    try{
      lista.create(1,"José Guilherme");
    }catch (Exception e) {
      e.printStackTrace();
    }

  }

  public Invertida(){

  }

  public boolean create(int id, String nome)throws Exception{


    createTermo(id,nome);

    return true;
  }

  public void createTermo(int id, String nome)throws Exception{
    termos = new RandomAccessFile("termoEndereco.db", "rw");
    ArrayList<String> listaTermos = limpa(nome);
    long addr;

    for(int i = 0; i < listaTermos.size(); ++i){
      termos.seek(0);
      boolean flag = false;
      while(termos.getFilePointer() < termos.length() && !flag){
        if(termos.readUTF().equals(listaTermos.get(i))) flag = true;
        addr = termos.readLong();
      }

      if(flag == false){
        //create
      }else if(termos.getFilePointer() == termos.length()){
        termos.writeUTF(listaTermos.get(i));
        //addr = create do bloco - retorna o endereco do long debaixo
        termos.writeLong();
      }
    }


  }

  public long createID(int id, long addr){
    IDs = new RandomAccessFile("IDs.db", "rw");

    if(addr == -1){
      IDs.seek(IDs.length());
      IDs.writeInt(1);
      IDs.writeInt(id);
      for(int i = 9; i < 9; ++i){
        IDs.writeInt(-1);
      }

    }else{
      
    }

  }

  public static ArrayList<String> limpa(String str){
    ArrayList<String> listaTermos = new ArrayList<>();
    str = str.toLowerCase();
    str = Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

    ArrayList<String> stopWords = new ArrayList<>();
    stopWords.add("do");
    stopWords.add("da");
    stopWords.add("e");
    stopWords.add("dos");
    stopWords.add("de");

    String partes[] = str.split(" ");
    for(String i : partes){
      if( !stopWords.contains(i) )
          listaTermos.add(i);
    }

    return listaTermos;
  }
}






//
