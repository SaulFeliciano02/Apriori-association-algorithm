import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static ArrayList<String> elementos = new ArrayList<>();
    public static float soporte;
    public static float confianza;
    public static ArrayList<String> transacciones = new ArrayList<>();
    public static ArrayList<String> seleccionados = new ArrayList<>();
    public static ArrayList<String> elementosCantidad = new ArrayList<>();

    public static void main(String[] args) {

        ArrayList<String> split = new ArrayList<>();
        ArrayList<String> combinaciones2 = new ArrayList<>();
        float i = 0;
        float laMaxima = 0;
        boolean bandera = false;
        String subConsecuente;
        String subAntecedente;
        char[] chars;
        float cantidadAntecedente;
        float cantidadConsecuente;
        float confianzaCaso;
        String regla;
        StringBuilder antecedenteSB = new StringBuilder();
        StringBuilder consecuenteSB = new StringBuilder();

        System.out.println("Introduzca el path del archivo a analizar\n");

        Scanner scan = new Scanner(System.in);

        String nomDataset = scan.nextLine(); //Tomo la direccion del archivo y lo guardo en la variable

        BufferedReader breader = null;

        try {
            breader = new BufferedReader(new FileReader(nomDataset));
            do {
                transacciones.add(breader.readLine());  //En este Arraylist, guardo cada linea del archivo por separado
            }while (!transacciones.contains(null));
            transacciones.remove(null);     //Remuevo el objeto null generado por el EOF
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\n");

        for (String s: transacciones
             ) {
            System.out.println(s);

            for (String atributo: s.split(" ")
                 ) {
                split.add(atributo);
                if(!elementos.contains(atributo))
                {
                    elementos.add(atributo);    //Guardo cada atributo de las transacciones por separado
                }

            }

        }

        System.out.println("\nIntroduzca el valor del soporte minimo para las reglas de asociacion\n");

        Scanner scan2 = new Scanner(System.in);

        soporte = scan.nextFloat();     //Guardo el valor del soporte minimo

        System.out.println("\nIntroduzca el valor de la confianza minima para las reglas de asociacion\n");

        Scanner scan3 = new Scanner(System.in);

        confianza = scan.nextFloat();   //Guardo el valor de la confianza minima

        System.out.println("\nRepeticiones de 1 elemento");

        for (String elemento: elementos
        ) {
            for (String dato: split
                 ) {
                if (elemento.equals(dato))
                {
                    i++;
                }
            }

            i /= 100;

            //Verifico si el elemento o atributo cumple con el soporte minimo

            if(i < soporte)
            {
                System.out.println(ANSI_RED + elemento + ":" + i + ANSI_RESET);
            }
            else
            {
                System.out.println(elemento + ":" + i);
                elementosCantidad.add(elemento + ":" + i);
                combinaciones2.add(elemento);

                if (laMaxima < i)
                {
                    laMaxima = i;   //Almaceno la cantidad maxima de repeticiones de un elemento
                }
            }
            i = 0;
        }

        seleccionados.addAll(elementosCantidad); //Almaceno los elementos que cumplieron con el soporte minimo establecido
        repeticiones(combinaciones2, 2, laMaxima); //Ejecuto funcion recursiva para encontrar y almacenar las combinaciones que cumplan con el soporte minimo

        System.out.println("\nAsociaciones que cumplen con el soporte minimo");
        laMaxima = 0;

        for (String select: seleccionados
             ) {
            System.out.println(select); //Imprimo todas las asociaciones o combinaciones que cumplen con el soporte minimo

            if (select.length() > laMaxima)
            {
                laMaxima = select.length(); //Verifico la longitud de la asociacion mas grande de elementos
            }
        }

        System.out.println("\nReglas generadas");

        for (String antecedente: seleccionados
             ) {
            for (String consecuente: seleccionados
                 ) {
                    subAntecedente = antecedente.substring(0, antecedente.indexOf(":")); //En estas variables obtenemos la parte del string del antecedente y consecuente donde se encuentran los elementos
                    subConsecuente = consecuente.substring(0, consecuente.indexOf(":"));

                    cantidadAntecedente = Float.parseFloat(antecedente.substring(antecedente.indexOf(":")+1, antecedente.length())); //En estas guardamos la parte del string del antecedente y consecuente donde se encuentra la cantidad de ocurrencias de estos elementos
                    cantidadConsecuente = Float.parseFloat(consecuente.substring(consecuente.indexOf(":")+1, consecuente.length()));
                    chars = subAntecedente.toCharArray(); //Guardamos cada elemento de dicho string en un arreglo de caracteres para validar que el consecuente contenga cada elemento del antecedente
                    antecedenteSB.append(chars);
                    consecuenteSB.append(subConsecuente); //Variable utilizada para imprimir el string del consecuente modificado para no repetir elementos del antecedente

                    for (char c: chars
                    ) {
                        if(consecuenteSB.indexOf(String.valueOf(c)) != -1) //Validamos que el consecuente contenga ese elemento del antecedente
                        {
                            consecuenteSB.deleteCharAt(consecuenteSB.indexOf(String.valueOf(c))); //Eliminamos las variables repetidas entre el antecedente y consecuente
                        }
                    }

                    //Validamos que el consecuente contenga cada elemento del antecedente para clasificarlo como un consecuente valido
                    for (int j = 0; j < chars.length; j++)
                    {
                        if (consecuente.indexOf(chars[j]) != -1)
                        {
                            bandera = true;
                        }
                        else {
                            bandera = false;
                            break;
                        }
                    }

                    if(bandera == true && !consecuenteSB.toString().isEmpty())
                    {
                        confianzaCaso = (cantidadConsecuente/cantidadAntecedente); //Calculamos el porcentaje de confianza de esta posible regla y luego la imprimimos de color rojo si no cumple con la confianza minima establecida,
                        // de lo contrario es impresa en color blanco
                        //System.out.println(consecuenteSB);
                        if(confianzaCaso >= confianza)
                        {
                            System.out.println(subAntecedente + " => " + consecuenteSB + " : conf = " + confianzaCaso);
                        }
                        else {
                            System.out.println(ANSI_RED + subAntecedente + " => " + consecuenteSB + " : conf = " + confianzaCaso + ANSI_RESET);
                        }
                    }

                    consecuenteSB.delete(0, consecuenteSB.length()); //Limpiamos la variable de impresion del consecuente para el proximo caso
            }
        }
    }

    public static ArrayList<String> repeticiones(ArrayList<String> combinaciones, int cuenta, float laMaxima)
    {
        ArrayList<String> combinaciones2 = new ArrayList<>();
        float i = 0;

        if(laMaxima >= soporte)
        {
            laMaxima = 0;
            System.out.println("\nRepeticiones de " + cuenta + " elementos");

            for (String attr1: combinaciones
            ) {
                for (String attr2: elementos
                ) {
                    if (attr1.length() > 1)
                    {
                        String parseado = attr1.substring(0, attr1.indexOf(":")); //Se guarda la parte del string que contiene los elementos
                        if (!parseado.equals(attr2))
                        {
                            for (String transaccion: transacciones
                            ) {
                                if (transaccion.indexOf(parseado)!=-1 && !parseado.contains(attr2) && transaccion.indexOf(attr2)!=-1)
                                {
                                    i++;
                                }
                            }
                            i /= 100;
                            if(!parseado.contains(attr2))
                            {
                                if (i < soporte)
                                {
                                    System.out.println(ANSI_RED + parseado+ " " +attr2 + ":" + i + ANSI_RESET);
                                }
                                else
                                {
                                    System.out.println(parseado+ " " +attr2 + ":" + i);
                                    combinaciones2.add(parseado+ " " +attr2 + ":" + i);

                                    if(laMaxima < i)
                                    {
                                        laMaxima = i;
                                    }
                                }
                            }
                            i = 0;
                        }
                    }
                    else
                    {
                        if (!attr1.equals(attr2))
                        {
                            for (String transaccion: transacciones
                            ) {
                                if (transaccion.indexOf(attr1)!=-1 && transaccion.indexOf(attr2)!=-1)
                                {
                                    i++;
                                }
                            }
                            i /= 100;
                            if (i < soporte)
                            {
                                System.out.println(ANSI_RED + attr1+ " " +attr2 + " " + i + ANSI_RESET);
                            }
                            else
                            {
                                System.out.println(attr1+ " " +attr2 + ":" + i);
                                combinaciones2.add(attr1+ " " +attr2 + ":" + i);

                                if(laMaxima < i)
                                {
                                    laMaxima = i;
                                }
                            }
                            i = 0;
                        }
                    }
                }
            }

            seleccionados.addAll(combinaciones2); //Se almacenan las asociaciones que cumplen con el soporte minimo
            return repeticiones(combinaciones2, cuenta+1, laMaxima);

        }

        //Esto no esta haciendo nada. Es un comodin para que la funcion no explote.
        return combinaciones;

    }

}
