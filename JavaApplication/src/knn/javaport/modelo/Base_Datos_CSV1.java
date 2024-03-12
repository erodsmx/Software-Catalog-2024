package knn.javaport.modelo;

import java.io.*;
import java.util.*;

public class Base_Datos_CSV1{
	
	//Definicion de una constante de tipo cadena que guarda el nombre del archivo
	private static final String fileName = "temp.csv";
	//Instancia para crear un archivo (Apenas declarado pero aun no creado)
	private File file = new File(fileName);
	
	//Constructor de la clase
	public Base_Datos_CSV1(){
		//Manejo de excepción
    	try {
    		/*El constructor trata de crear un nuevo archivo, de no existir el archivo lo crea
    		exitosamente de lo contrario usa el archivo existente*/
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	//Metodo que lee el archivo completo y regresa una lista ligada de localidades.
	public LinkedList<Localidad> readall() {
			String line = null;
			String[] temp;
                        LinkedList<Localidad> listLoc = new LinkedList<Localidad>();
	        try {
	            FileReader fileReader = new FileReader(fileName);
	            BufferedReader bufferedReader =  new BufferedReader(fileReader);
	            while((line = bufferedReader.readLine()) != null) {
	            	temp = line.split(",");
                        listLoc.add(new Localidad(Float.parseFloat(temp[0]),Float.parseFloat(temp[1]),temp[2].charAt(0)  ));
	            }    
	            bufferedReader.close();            
	        }
	        catch(FileNotFoundException ex) {
	            System.out.println("Unable to open file '" + fileName + "'");                
	        }
	        catch(IOException ex) {
	            System.out.println("Error reading file '" + fileName + "'");                   
	           ex.printStackTrace();
	        }
	        return listLoc;
        }
        
}
