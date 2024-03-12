/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knn.javaport.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import knn.javaport.modelo.Base_Datos_CSV1;
import knn.javaport.modelo.Localidad;
import knn.javaport.modelo.Permutation;
/**
 *
 * @author delta9
 */
public class JavaApplication2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{ 
        int i,posAux;
        LinkedList<Localidad> lista = new LinkedList<>(); //Lugares no visitados
        LinkedList<Localidad> temp = new LinkedList<>();
        ArrayList <LinkedList> sector = new ArrayList<>(); 
        String ruta="";
        String completa="";
        float distancia=0;
        /*Cargar informacion*/
        Base_Datos_CSV1 db = new Base_Datos_CSV1();//db lee los datos del archivo.
        lista = db.readall(); //Carga ciudades 
        
        /*Entrada de datos de usuario */
        char inputCh = (char) System.in.read(); //Elegir ciudad inicial
        
        /*Buscar ciudad seleccionada*/
//        int posicion = findIterator(inputCh, lista ); //Obtiene la posicion de la ciudad elegida de la lista
        

        
        /*Visita ciudades*/
 organizarInfo(sector,lista);
 definirPtCritico(sector);
 validarPtCritico(sector);  
 
 //En base a los resultados formatea la ruta mas optima
 for(i=0;i< sector.size();i++){
        ruta= crearTrayectoriaCuadrante(sector,i);
        distancia+= Float.parseFloat(ruta.substring(ruta.indexOf(',')+1));
        ruta= ruta.substring(0, ruta.indexOf(','));
        if(i== 0 || i==2){
            StringBuilder sb= new StringBuilder(ruta);
            sb.reverse();
            completa= completa.concat(sb.toString());
        }else{
            completa= completa.concat(ruta);            
        }
//Suma las partes restantes


    
    }
    System.out.println("La ruta es: "+completa.substring(completa.indexOf(inputCh)) + completa.substring(0, completa.indexOf(inputCh)) );  

        
        
}
    /*
       medirDistRespectoY() calcula la distancia que hay entre las coordenadas Y (ordenadas)
       del vertice y los puntos del cuadrante.
    */
    public static void medirDistRespectoY(LinkedList<Localidad> lista){
        final int limiteY = 50; //recta Y = 50 que divide a los datos en dos grupos
        float delta = 0;//Distancia de la coordenada Y con respecto a la recta Y = 50
        Localidad coordPtoLim = lista.getFirst(); //apunta a los valores de la lista.
        boolean primerValor=true;   
        for (Localidad l : lista) { //recorre la lista
            if(primerValor){ //Identificar el primer elemento de la lista
                delta = (float) Math.abs(l.getY()-limiteY);
                primerValor = false;
                coordPtoLim = l;
            }
            if((float) Math.abs(l.getY()-limiteY) < delta){
                delta = (float) Math.abs(l.getY()-limiteY);
                coordPtoLim = l;
            }
        }
//                System.out.println(coordPtoLim.getNombre()); 
                coordPtoLim.setPtoLimiteY(true);
              
    }
    /*
       medirDistRespectoX()calcula la distancia que hay entre las coordenadas X (abcisas)
    */
    public static void medirDistRespectoX(LinkedList<Localidad> lista){
        final int limiteX = 50; //recta X = 50 que divide a los datos en dos grupos
        float delta = 0;//Distancia perpendicular del punto a la recta X = 50
        Localidad coordPtoLim = lista.getFirst(); //inicializa a cualquier valor de la lista para poder apuntar a sus valores
        boolean primerValor=true;   
        for (Localidad l : lista) { //recorre la lista
            if(primerValor){ //Identificar el primer elemento de la lista
                delta = (float)Math.abs(l.getX()-limiteX);
                primerValor = false;
                coordPtoLim = l;
            }
            if((float) Math.abs(l.getX()-limiteX) < delta){ 
                delta = (float) Math.abs(l.getX()-limiteX);//Guarda el dato de la diferencia de las X mas pequeño de todos
                coordPtoLim = l; //Actualiza nuevo punto mas cercano a la recta.
            }
        }
//                System.out.println(coordPtoLim.getNombre());
              
            
                coordPtoLim.setPtoLimiteX(true);
    }
            
    /*  OrganizarInfo():
        La lista de ciudades se organiza por cuadrantes en un arrayList,
        cada casilla del arreglo representa un cuadrante,
        cada cuadrante tiene un conjunto de puntos.
    */
    public static void organizarInfo(ArrayList <LinkedList> sector, LinkedList<Localidad> lista ){
        int NUM_CUADRANTES=4;
        LinkedList<Localidad> temp;
        for(int i=0;i< NUM_CUADRANTES;i++){
            temp = extraerPorCuadrante(i+1,lista); //Extrae los puntos del cuadrante i
            sector.add(temp);//Construye arrayList
        }
    }
    /*
        DefinirPtoCritico hace la primera validacion de los puntos que son cercanos a las rectas X=50 y Y=50.
    */
    public static void definirPtCritico(ArrayList <LinkedList> sector){ 
        //Ubicar elementos cercanos a la frontera
        for(LinkedList<Localidad> cuadrante : sector ) { 
            
            medirDistRespectoX(cuadrante); //En cada cuadrante ubica dos puntos a considerar
            medirDistRespectoY(cuadrante);            
        } 
System.out.println("Los puntos limite cercanos a las rectas y=50, X=50 son:");     
        for(LinkedList<Localidad> l: sector){
            for(Localidad k: l){
                if(k.isPtoLimiteX() || k.isPtoLimiteY()){
                    System.out.print(k.getNombre()+",");                                                
                }
            }
        } 
System.out.println();        
    }
    
    //Segunda validacion para encontrar los puntos que conectan con otros cuadrantes
    public static void validarPtCritico(ArrayList <LinkedList> sector){
    int i;
        final int PRIMER_CUADRANTE =0;
        final int SEGUNDO_CUADRANTE =1;
        final int TERCER_CUADRANTE =2;
        final int CUARTO_CUADRANTE =3;
        Localidad auxX1, auxY1, auxX2, auxY2, auxX3, auxY3,auxX4,auxY4;
        Localidad auxDistMin;
        LinkedList<Localidad> pre;
        LinkedList<Localidad> temp; //Guarda temporalmente los puntos de un cuadrante.
        pre = sector.get(PRIMER_CUADRANTE);
        auxX1 = new Localidad(0,0,' ');//Inicializar a cualquier valor de la lista        
        auxY1 = auxX1;//Inicializar a cualquier valor de la lista
        auxX2= auxY2=auxX1;
        auxX3= auxY3 =auxX4 = auxY4 = auxX1;
        auxDistMin = auxX1;
        //Obtiene los dos puntos limite del cuadrante 1
        for(Localidad lim: (LinkedList<Localidad>) sector.get(PRIMER_CUADRANTE)){
            if(lim.isPtoLimiteX()){
                auxX1= lim; //Ubica al primer punto definido como limite X
            }else if(lim.isPtoLimiteY()){
                auxY1 = lim; //Ubica al segundo punto definido como limite Y
            }
        }
        //Obtiene los dos puntos limite del cuadrante 2
        for(Localidad l: (LinkedList<Localidad>) sector.get(SEGUNDO_CUADRANTE)){
            if(l.isPtoLimiteX()){
                auxX2= l; //Ubica al primer punto definido como limite X2
            }else if(l.isPtoLimiteY()){
                auxY2 = l; //Ubica al segundo punto definido como limite Y2
            }
        }

                //Obtiene los dos puntos limite del cuadrante 3
        for(Localidad l: (LinkedList<Localidad>) sector.get(TERCER_CUADRANTE) ){
            if(l.isPtoLimiteX()){
                auxX3= l; //Ubica al primer punto definido como limite X3
            }else if(l.isPtoLimiteY()){
                auxY3 = l; //Ubica al segundo punto definido como limite Y3
            }
        }
                //Obtiene los dos puntos limite del cuadrante 4
        for(Localidad l: (LinkedList<Localidad>) sector.get(CUARTO_CUADRANTE) ){
            if(l.isPtoLimiteX()){
                auxX4= l; //Ubica al primer punto definido como limite X4
            }else if(l.isPtoLimiteY()){
                auxY4 = l; //Ubica al segundo punto definido como limite Y4
            }
        }       
        
        //Analizar para X de cuadrante 1
        auxDistMin = buscaPuntoCercano(auxX1,sector.get(SEGUNDO_CUADRANTE));//Obtiene ciudad mas cercana a punto definido como limite
        if(auxDistMin.getNombre()!= auxX2.getNombre()){ //Si el mas cercano no esta definido como limite
            auxDistMin.setPtoLimiteX(true);//reasigna etiquetas
            auxX2.setPtoLimiteX(false);
        }
        //Analizar para Y de cuadrante 1
        auxDistMin = buscaPuntoCercano(auxY1,sector.get(CUARTO_CUADRANTE));//Obtiene ciudad mas cercana a punto definido como limite
        if(auxDistMin.getNombre()!= auxY4.getNombre()){ //Si el mas cercano no esta definido como limite
            auxDistMin.setPtoLimiteY(true);
            auxY4.setPtoLimiteY(false);
        } 
        //Analizar para X de cuadrante 2
        auxDistMin = buscaPuntoCercano(auxX2,sector.get(PRIMER_CUADRANTE));//Obtiene ciudad mas cercana a punto definido como limite
        if(auxDistMin.getNombre()!= auxX1.getNombre()){ //Si el mas cercano no esta definido como limite
            auxDistMin.setPtoLimiteX(true);
            auxX1.setPtoLimiteX(false);
        }
        //Del siguiente caso puede partir la generalizacion
        //Analizar para Y de cuadrante 2
        auxDistMin = buscaPuntoCercano(auxY2,sector.get(TERCER_CUADRANTE));//Obtiene ciudad mas cercana a punto definido como limite
//        System.out.println("Y2:"+ auxY2.getNombre()+" auxDistMin:"+auxDistMin.getNombre()+" auxY3:"+auxY3.getNombre());
        if(auxDistMin.getNombre()!= auxY3.getNombre()){ //Si el mas cercano no esta definido como limite
            if( buscaPuntoCercano(auxY3,sector.get(SEGUNDO_CUADRANTE)).getNombre() != auxY2.getNombre()){ //Si se busca al reves y no coincide
                auxY2.setPtoLimiteY(false);//elpunto actual no es limite
                //buscar nueva distancia minima y actualizar nuevo valor limite
                buscaPuntoCercano(auxY3,sector.get(SEGUNDO_CUADRANTE)).setPtoLimiteY(true);
                
                
            }else{
                auxDistMin.setPtoLimiteY(true);
                auxY3.setPtoLimiteY(false); 
            }
        }    
        //Analizar para X de cuadrante 3
        auxDistMin = buscaPuntoCercano(auxX3,sector.get(CUARTO_CUADRANTE));//Obtiene ciudad mas cercana a punto definido como limite
        if(auxDistMin.getNombre()!= auxX4.getNombre()){ //Si el mas cercano no esta definido como limite
            auxDistMin.setPtoLimiteX(true);
            auxX4.setPtoLimiteX(false);
        }
        //Analizar para Y de cuadrante 3
        auxDistMin = buscaPuntoCercano(auxY3,sector.get(SEGUNDO_CUADRANTE));//Obtiene ciudad mas cercana a punto definido como limite
        if(auxDistMin.getNombre()!= auxY2.getNombre()){ //Si el mas cercano no esta definido como limite
            auxDistMin.setPtoLimiteY(true);
            auxY2.setPtoLimiteY(false);
        } 

        //Analizar para X de cuadrante 4
        auxDistMin = buscaPuntoCercano(auxX4,sector.get(TERCER_CUADRANTE));//Obtiene ciudad mas cercana a punto definido como limite
        if(auxDistMin.getNombre()!= auxX3.getNombre()){ //Si el mas cercano no esta definido como limite
            auxDistMin.setPtoLimiteX(true);
            auxX3.setPtoLimiteX(false);
        }
        //Analizar para Y de cuadrante 4
        auxDistMin = buscaPuntoCercano(auxY4,sector.get(PRIMER_CUADRANTE));//Obtiene ciudad mas cercana a punto definido como limite
        if(auxDistMin.getNombre()!= auxY1.getNombre()){ //Si el mas cercano no esta definido como limite
            auxDistMin.setPtoLimiteY(true);
            auxY1.setPtoLimiteY(false);
        } 

System.out.println("Los puntos limite que conectan a los cuadrantes entre si son:");     
        for(LinkedList<Localidad> l: sector){
            for(Localidad k: l){
                if(k.isPtoLimiteX() || k.isPtoLimiteY()){
                    System.out.print(k.getNombre()+",");                                                
                }
            }
        }
System.out.println();        

    }

    public static String crearTrayectoriaCuadrante(ArrayList <LinkedList> sector, int x){
        LinkedList<Localidad> listaCuadrante = new LinkedList<>();
        Localidad ptoLimX =new Localidad(0,0,' ');
        Localidad ptoLimY = new Localidad(0,0,' ');
        Localidad loc =new Localidad(0,0,' ');
        LinkedList<Localidad> rutaTemporal = new LinkedList<>();
        LinkedList<String> strTemporal = new LinkedList<>();
        float distancia;
        String str = "";
    StringBuilder sb = new StringBuilder(6);//maximo tamaño del sb es 6
        listaCuadrante = (LinkedList<Localidad>) sector.get(x).clone(); //Copia lista
        for(Localidad l: listaCuadrante){
            if(l.isPtoLimiteX()){
                ptoLimX= l; //Ubica al primer punto definido como limite 
            }else if(l.isPtoLimiteY()){
                ptoLimY = l; //Ubica al segundo punto definido como limite
            }else{
               str = str.concat(String.valueOf(l.getNombre()));
            }
        }
    /*Obtener todas las trayectorias posibles*/
    Permutation permutador = new Permutation();
    permutador.permute(str, 0, str.length()-1);     
    for(String s: permutador.getPermutaciones()){
    s= sb.append(ptoLimX.getNombre()).append(s).append(ptoLimY.getNombre()).toString();
    strTemporal.add(s);
    sb.setLength(0);
    }
    return conectarPuntos(listaCuadrante,strTemporal);
    }
    
    public static String conectarPuntos(LinkedList<Localidad> cuadrante,LinkedList<String>  permutacion){
        int i=0,j=1;
        float distancia = 0,delta=0;
        Localidad A = new Localidad(0,0,' ');
        Localidad B = new Localidad(0,0,' ');
        String str="";
        String rutaOptima="";
        boolean primerValor=true;

        LinkedList<Localidad> temp= new LinkedList<>();
        
        for(i=0;i< permutacion.size();i++){ //Recorre todas las permutaciones generadas
            str=permutacion.get(i);
            while(str.length() > 1){ //Mientras haya al menos dos caracteres en la cadena
                for(Localidad l : cuadrante){ //para cada punto del cuadrante
                    if( str.charAt(0) == l.getNombre() ){ //si es el punto buscado
                        A=l;
                    }else if (str.charAt(1) == l.getNombre()){
                        B=l;
                    }
                }
                distancia+= calcularDistancia(A,B);
                str = str.substring(1);//Quita el primer caracter primeros puntos
            }
//System.out.println(permutacion.get(i)+": "+ distancia );
            
            if(primerValor){//Solo la primera vez no se necesita comparar la distancia por ser el primer dato.
                    rutaOptima = permutacion.get(i);
                    delta = distancia;
                    primerValor=false;
                }
            if(distancia < delta){
                delta = distancia;
                rutaOptima = permutacion.get(i);
            }        
        distancia=0;
        }
         return (rutaOptima + ","+delta);

    }

    
            

    
    /* Dada una lista, extrae los elementos del cuadrante especificado en parametro cuad. */
    public static LinkedList<Localidad> extraerPorCuadrante(int cuad, LinkedList<Localidad> lista) {   
        LinkedList<Localidad> temp= new LinkedList<>();
        for(Localidad l: lista){
            if(l.getCuadrante() == cuad){
                temp.add(l);
            }  
        }   
    return temp; //Regresa todos los puntos de un cuadrante.
}


//Calcula la distancia de la ruta desde que sale de la primer ciudad y termina en la misma.    
public static void calcularDistanciaTotal(LinkedList<Localidad> ruta){
    float distancia=0;
    float a;
    for(Localidad l: ruta){
        distancia+= l.getDistancia();
    }
    a = (float) Math.hypot( ruta.getFirst().getX() - ruta.getLast().getX(), ruta.getFirst().getY() - ruta.getLast().getY());
    System.out.println("Distancia recorrida:" + distancia + ", " + (distancia + a) );

}

//Dado un vertice, encuentra al punto mas cercano de la lista.
 public static Localidad buscaPuntoCercano(Localidad vertice,LinkedList<Localidad> lista){
        int i=0,posicion=0;
        boolean primerValor=true;
        float distTemp=0;
        LinkedList<Localidad> temp= new LinkedList<>();
        Iterator<Localidad> iterator = lista.iterator();
        /*  Durante el ciclo se calcula la distancia del vertice a cada uno de los puntos,
            El iterador carga la lista de ciudades y va recorriendo una por una, se calcula
            la distancia del vertice al punto K-esimo.
            distTemp almacena la distancia minima del vertice al punto K, a medida que el iterador
            avanza distTemp se actualiza valor minimo si es que lo hay.
        */
        while (iterator.hasNext()) {
            Localidad sitioKesimo = iterator.next(); //Carga nuevo valor de la lista
            sitioKesimo.setDistancia(calcularDistancia(vertice,sitioKesimo)); //Calcula distancia
                if(primerValor){//Solo la primera vez no se necesita comparar la distancia por ser el primer dato.
                    distTemp= sitioKesimo.getDistancia();
                    primerValor=false;
                    posicion = i;
                }                
                if(sitioKesimo.getDistancia()< distTemp){ //Si la nueva distancia calculada es la mas chica de todas
                    distTemp = sitioKesimo.getDistancia(); //Actualiza valor a nueva distancia minima
                    temp.addFirst(sitioKesimo);//Siempre agrega el dato mas chico al inicio
                    posicion=i;
                }
                i++; 
//                            System.out.println("distTemp: "+ distTemp +" Distancia de " + vertice.getNombre() + " a " + sitioKesimo.getNombre()+ ": " + sitioKesimo.getDistancia());
  //                          System.out.println("DX:" + (int)(sitioKesimo.getX()-vertice.getX()) + ", DY:" + (int)(sitioKesimo.getY()-vertice.getY())  );
        }
        vertice = lista.get(posicion);
//        lista.remove(posicion); //Elimina de sitios no visitados.
        return vertice;
}
 
    // findIterator() busca el elemento que tenga como nombre ch en la lista de ciudades.
    public static int findIterator(char ch, LinkedList<Localidad> lista) {
        int i = 0;
    Iterator<Localidad> iterator = lista.iterator();
    while (iterator.hasNext()) { //Recorre cada uno de los elementos de la lista
        Localidad sitioKesimo = iterator.next(); //Obtiene ciudad de la lista
        if (sitioKesimo.getNombre() == ch ) {
            return i; //Regresa la posicion en que se encuentra el elemento en la lista
        }
    i++; //Va incrementando la posicion   
    }
    return i;
}
/**
 *      calcularDistancia(): como no son muchos datos podemos calcular la raiz cuadrada
 *       y obtener la magnitud de la distancia, si el conjunto de datos fuese muy grande 
 *      basta con calcular la distancia cuadrada.
 *      @return distancia euclideana
 */    
     public static float calcularDistancia(Localidad vertice, Localidad kesimo) {
            return (float) Math.hypot((double) (kesimo.getX()- vertice.getX()), (double) (kesimo.getY()- vertice.getY()));

}

  
    
 
}

