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
/**
 *
 * @author delta9
 */
public class JavaApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{ 

        LinkedList<Localidad> lista = new LinkedList<Localidad>(); //Lugares no visitados
        LinkedList<Localidad> ruta = new LinkedList<Localidad>(); //Lugares visitados
        LinkedList<Localidad> temp = new LinkedList<Localidad>();
        
        /*Cargar informacion*/
        Base_Datos_CSV1 db = new Base_Datos_CSV1();//db lee los datos del archivo.
        lista = db.readall(); //Carga ciudades 
        
        /*Entrada de datos de usuario */
        char inputCh = (char) System.in.read(); //Elegir ciudad inicial
        
        /*Buscar ciudad seleccionada*/
        int posicion = findIterator(inputCh, lista ); //Obtiene la posicion de la ciudad elegida de la lista
        
        /*Actualiza Posicion*/
        Localidad vertice = new Localidad(lista.get(posicion).getX(),lista.get(posicion).getY(),lista.get(posicion).getNombre()); 
        ruta.add(vertice); //Agregar a lugares visitado
        lista.remove(posicion); //Eliminar de sitios no visitado
        
        /*Visita ciudades*/
    while(!lista.isEmpty()){ //Mientras haya elementos no visitados
        vertice = buscaPuntoCercano(vertice,lista);//Visita nuevo lugar actualizando la posicion del vertice y eliminando de sitios no visitados.
        ruta.add(vertice);//Agrega nueva posicion a sitios visitados
    }
        /*Obtiene Resultados*/
        calcularDistanciaTotal(ruta);
        System.out.println("La Ruta es: ");
        for(Localidad kesima: ruta){ //Recorre para imprimir
            System.out.print(kesima.getNombre()+", ");
        }
        System.out.println("\b\b.");
}
    /*
       medirDistRespectoY() calcula la distancia que hay entre las coordenadas Y (ordenadas)
    */
    public static void medirDistRespectoY(LinkedList<Localidad> lista){
        final int limiteY = 50; //recta Y = 50 que divide a los datos en dos grupos
        float delta = 0;//Distancia de la coordenada Y con respecto a la recta Y = 50
        Localidad coordPtoLim = new Localidad(0,0,' '); //almacena al punto mas cercano a la recta limite
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
                //return coordPtoLim;
    }
    /*
       medirDistRespectoX()calcula la distancia que hay entre las coordenadas X (abcisas)
    */
    public static void medirDistRespectoX(LinkedList<Localidad> lista){
        final int limiteX = 50; //recta X = 50 que divide a los datos en dos grupos
        float delta = 0;//Distancia perpendicular del punto a la recta X = 50
        Localidad coordPtoLim = new Localidad(0,0,' '); //almacena al punto mas cercano a la recta limite
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
    /*
        DefinirPtoCritico hace la primera validacion de los puntos que son cercanos a las rectas X=50 y Y=50.
    */
    public static void definirPtCritico(LinkedList<Localidad> lista){ 
        int i;
        LinkedList<Localidad> temp; //Guarda temporalmente los puntos de un cuadrante.
        ArrayList <LinkedList> sector = new ArrayList<LinkedList>(); 
        
        for(i=0;i<4;i++){//Solo se hace cuatro veces porque es el numero de cuadrantes.
            temp = extraerPorCuadrante(i+1,lista); //Extrae los puntos del cuadrante i
            sector.add(temp);//Construye arrayList
        }
        //Ubicar elementos cercanos a la frontera
        for(LinkedList<Localidad> cuadrante : sector ) { //Ubica en cada lista del cuadrante los puntos mas cercanos a las rectas
            medirDistRespectoY(cuadrante);
            medirDistRespectoX(cuadrante);
        } 
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
    System.out.println("Distancia recorrida:" + (distancia + a) );

}


//Dado una ubicacion, encuentra al punto mas cercano de la lista.
 public static Localidad buscaPuntoCercano(Localidad vertice,LinkedList<Localidad> lista){
        int i=0,posicion=0;
        boolean primerValor=true;
        float distTemp=0;
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
                    posicion=i;
                }
                i++; 
//                            System.out.println("distTemp: "+ distTemp +" Distancia de " + vertice.getNombre() + " a " + sitioKesimo.getNombre()+ ": " + sitioKesimo.getDistancia());
  //                          System.out.println("DX:" + (int)(sitioKesimo.getX()-vertice.getX()) + ", DY:" + (int)(sitioKesimo.getY()-vertice.getY())  );
        }
        vertice = lista.get(posicion);
        lista.remove(posicion); //Elimina de sitios no visitados.
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

