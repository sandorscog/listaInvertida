import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;

class Invertida{

  RandomAccessFile termos;
  RandomAccessFile IDs;

  public static void main(String[] args){
    try{
      Invertida lista = new Invertida();
      lista.create(1,"Marcos Antônio de Oliveira");
      lista.create(2,"José Marcos Resende");
      lista.create(3,"Paula Oliveira");
      lista.create(4,"Carlos José Antônio Souza");
      lista.create(5,"José Carlos de Paula");
      //lista.create(6,"José Oliveira santana");
      lista.create(6,"Sandor Borges Scoggin");

      ArrayList<Integer> i = lista.read("José de Paula");
      
      for(int j = 0; j < i.size(); j++){
        System.out.print(i.get(j) + " - ");
      }
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

    createTermo(id,nome);

    return true;
  }

  public ArrayList<Integer> read(String nome){
    ArrayList<String> listaTermos = limpa(nome); //retira sinais graficos e passa para LowerCase
    long addr = 0;                               //alem disso retira as stop words
    ArrayList<Integer> intersecao = new ArrayList<>();

    try{
      for(int i = 0; i < listaTermos.size(); ++i){ //executa a rotuna para cada termo individual
        termos.seek(0);
        boolean existe = false;
        while(termos.getFilePointer() < termos.length() && !existe){      //avanca no arq em busca do termo
          if(termos.readUTF().equals(listaTermos.get(i))) existe = true;  //caso exista o loop quebra
          addr = termos.readLong();
        }
        ArrayList<Integer> novo = readID(addr);

        if(i > 0)
          intersecao = intersecciona(novo, intersecao);
        else
          intersecao = intersecciona(novo, novo);
      }
    }catch (Exception e){
      e.printStackTrace();
    }

    return intersecao;
  }

  public ArrayList<Integer> readID(long addr){
    ArrayList<Integer> ids = new ArrayList<>();

    try{
      IDs.seek(addr);
      int n = IDs.readInt();
      if(n < 10){
        for(int i = 0; i < n; i++)
          ids.add(IDs.readInt());

      } else {
        for(int i = 0; i < n; i++)
          ids.add(IDs.readInt());

        addr = IDs.readLong();
        if(addr > -1){
          ids.addAll(readID(addr));
          System.out.println("rec");
        }
      }
    }catch (Exception e){
      e.printStackTrace();
    }

    return ids;
  }

  public ArrayList<Integer> intersecciona(ArrayList<Integer> novo, ArrayList<Integer> intersecao){

    ArrayList<Integer> atualizada = new ArrayList<>();
    for(int i = 0; i < novo.size(); i++){
      if( intersecao.contains(novo.get(i)) ) atualizada.add(novo.get(i));
    }
    return atualizada;
  }

  public void createTermo(int id, String nome)throws Exception{

    ArrayList<String> listaTermos = limpa(nome); //retira sinais graficos e em LowerCase
    long addr = 0;                               //alem disso retira as stop words

    for(int i = 0; i < listaTermos.size(); ++i){ //executa a rotuna para cada termo individual
      termos.seek(0);
      boolean existe = false;
      while(termos.getFilePointer() < termos.length() && !existe){      //avanca no arq em busca do termo
        if(termos.readUTF().equals(listaTermos.get(i))) existe = true;  //caso exista o loop quebra
        addr = termos.readLong();
      }

      if(existe){
        addr = createID(id, addr);
      }else if(termos.getFilePointer() == termos.length()){  //caso exista = true passamos o endereco
        termos.writeUTF(listaTermos.get(i));                 //do bloco, caso contrario passamos -1
        addr = createID(id, -1);                             //isso ativa a criacao de um novo bloco
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
      IDs.seek(addr);                       //Aqui o bloco ja existe, vamos ate ele
      int n = IDs.readInt();                //lemos o numero de elementos presentes
      if(n < 10){                           //caso nao esteja lotado nos adicionamos
        IDs.seek(IDs.getFilePointer()-4);   //a ID e atualizamos o header
        IDs.writeInt(n+1);
        int i = 0;
        n = IDs.readInt();
        while(i < 9 && n != -1){
          n = IDs.readInt();
          i++;
        }
        IDs.seek(IDs.getFilePointer()-4);
        IDs.writeInt(id);

      } else {                            //caso o bloco esteja cheio precisamos de um novo
        for(int i = 0; i < 10; i++){  //vamos ate o ponteiro para proseguir ao proximo bloco
          n = IDs.readInt();
        }
        long posPonteiro = IDs.getFilePointer();  //salvamos a posicao atual para retornar
        long ponteiro = IDs.readLong();

        if(ponteiro == -1){               //  Se o ponteiro conter -1 entao precisamos de um
          addr = createID(id, ponteiro);  //bloco novo para acomodar a ID. Entao passamos a ID
          IDs.seek(posPonteiro);          //e o -1 em uma chamada recursiva para esse mesma
          IDs.writeLong(addr);            //funcao, assim voltamos ao caso base como se o
        }else{                            //termo ainda nao estivesse presente.
          addr = createID(id, ponteiro);  //  No caso de ja existir um bloco mais para frente
        }                                 //chamamos a recursao mas avancando para a posicao
      }                                   //que foi lida no ponteiro
    }

    return addr;
  }

  public static ArrayList<String> limpa(String str){

    //o metodo retira sinais graficos, passa para lower case e descarta stop word da String
    //retorna um array com os temros validos limpos
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
