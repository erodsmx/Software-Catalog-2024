package knn.javaport.modelo;

import java.util.LinkedList;

public class Permutation { 
    private String str;
    private int n;
    private LinkedList<String> permutaciones;

    public Permutation() {
        this.str = "";
        this.n = 0;
        this.permutaciones = new LinkedList<>();
    }
    /** 
     * permutation function 
     * @param str string to calculate permutation for 
     * @param l starting index 
     * @param r end index 
     */
    public void permute(String str, int l, int r) 
    { 
        if (l == r) 
            permutaciones.add(str);
//            System.out.println("l=r"+str); 
        else
        { 
            for (int i = l; i <= r; i++) 
            { 
                str = swap(str,l,i);
                permute(str, l+1, r); 
                str = swap(str,l,i); 

            } 
        } 
    } 
  
    /** 
     * Swap Characters at position 
     * @param a string value 
     * @param i position 1 
     * @param j position 2 
     * @return swapped string 
     */
    public String swap(String a, int i, int j) 
    { 
        char temp; 
        char[] charArray = a.toCharArray(); 
        temp = charArray[i] ; 
        charArray[i] = charArray[j]; 
        charArray[j] = temp; 
        return String.valueOf(charArray); 
    } 

    public LinkedList<String> getPermutaciones() {
        return permutaciones;
    }

    public void setPermutaciones(LinkedList<String> permutaciones) {
        this.permutaciones = permutaciones;
    }
  
} 