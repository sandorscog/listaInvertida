import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;

class Invertida{

  RandomAccessFile termos;
  RandomAccessFile IDs;

  public static void main(String[] args){
    try{
      Invertida lista = new Invertida();
      lista.create(1,"José Guilherme");
      lista.create(2,"José Pedro");
      lista.create(3,"José Oliveira");
      lista.create(4,"José Oliveira");
      lista.create(5,"José Oliveira");
      lista.create(6,"José Oliveira");
      lista.create(7,"José Oliveira");
      lista.create(8,"José Oliveira");
      lista.create(9,"José Oliveira");
      lista.create(10,"José Oliveira");
      lista.create(11,"José Oliveira");
      lista.create(12,"José Oliveira");
      lista.create(13,"José Oliveira");
      lista.create(14,"José Oliveira");
    }catch (Exception e) {
      e.printStackTrace();
    }

  }

  public Invertida(){
    try{
      termos = new RandomAccessFile("termoEndereco.db", "rw");
      IDs = new RandomAccessFile("IDs.db", "rw");
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public boolean create(int id, String nome)throws Exception{

    System.out.println("oi");
    createTermo(id,nome);

    return true;
  }

  public void createTermo(int id, String nome)throws Exception{

    ArrayList<String> listaTermos = limpa(nome);
    long addr = 0;

    for(int i = 0; i < listaTermos.size(); ++i){
      termos.seek(0);
      boolean existe = false;
      while(termos.getFilePointer() < termos.length() && !existe){
        if(termos.readUTF().equals(listaTermos.get(i))) existe = true;
        addr = termos.readLong();
      }

      if(existe){
        addr = createID(id, addr);
      }else if(termos.getFilePointer() == termos.length()){
        termos.writeUTF(listaTermos.get(i));
        addr = createID(id, -1);
        termos.writeLong(addr);
      }
    }


  }

  public long createID(int id, long addr)throws Exception{

    if(addr == -1){
      IDs.seek(IDs.length());
      addr = IDs.getFilePointer();
      IDs.writeInt(1);            //inicializacao do bloco, vamos ate o final
      IDs.writeInt(id);           //entao escrevemos 1 para o n de elementos
      for(int i = 0; i < 9; ++i){ //no novo bloco e colocamos 9 posicoes vazias
        IDs.writeInt(-1);         //no final colocamos um ponteiro vazio
      }
      IDs.writeLong(-1);

    }else{
      IDs.seek(addr);
      int n = IDs.readInt();
      if(n < 10){
        IDs.seek(IDs.getFilePointer()-4);
        IDs.writeInt(n+1);
        int i = 0;
        n = IDs.readInt();
        while(i < 9 && n != -1){
          n = IDs.readInt();
          i++;
        }
        IDs.seek(IDs.getFilePointer()-4);
        IDs.writeInt(id);

      } else {
        for(int i = 0; i < 10; i++){
          n = IDs.readInt();
        }
        long posPonteiro = IDs.getFilePointer();
        long ponteiro = IDs.readLong();

        if(ponteiro == -1){
          addr = createID(id, ponteiro);
          IDs.seek(posPonteiro);
          IDs.writeLong(addr);
        }else{
          addr = createID(id, ponteiro);
        }

      }
    }

    return addr;
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
