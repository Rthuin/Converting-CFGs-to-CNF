import javax.swing.tree.DefaultTreeCellEditor;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

 public class cnf {

    private static final int DEFAULT_SIZE = 10; //size of each line of CFG
    //private static String[][] language = new String[DEFAULT_SIZE][DEFAULT_SIZE]; //to store entered grammar
//    private static String[] dpr = new String[DEFAULT_SIZE];
    private static int p; //np-> number of productions
    private static int line;
    private String input;
    private String string;
    private int lineCount;
    private String epsilonFound = "";
    private Map<String, List<String>> mapVariableProduction = new LinkedHashMap<>();

    public static void main(String args[]) throws FileNotFoundException {
        String final_string;

        // Please enter file name.
        String file = "CFG.txt";
        int size;
        int counter = 0;

        size = countLineNumberReader(file);

        int lineCount=size;
        String[] str=new String[lineCount];

        Scanner sc = new Scanner(new File(file));
        try (FileInputStream fis = new FileInputStream(file);
        	       InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        	       BufferedReader reader = new BufferedReader(isr)

        	  ) {
        	      String str2;
        	      /*while ((str2 = reader.readLine()) != null) {
                    str[counter] = str2;
                    counter ++;
        	      }*/
            str2=  sc.nextLine();
            System.out.println("\nCFG Form:");
            System.out.println(str2);// Display Language

                  while (sc.hasNext()) {
                      str2 = sc.nextLine();
                      str[counter] = str2;
                      counter ++;
                  }

            counter = 0;
        	  }
        catch (IOException e) {
        		  System.out.println("An error occurred.");
        	      e.printStackTrace();
        	  }

        final_string=str[0]+"\n";

        for(int i=1;i<lineCount-1;i++)
            final_string+=str[i]+"\n";

        final_string+=str[lineCount-1];
        for (int i = 0; i < str.length-1; i++) {
            System.out.println(str[i]);
        }
        System.out.println("\n###############################################");
        cnf c= new cnf();
        c.setInputAndNumberOfLine(final_string,lineCount);
        c.convertToCNF();
    }
    /*
     * S-A1A
     * A-0B0|€
     * B-A|10
    */
	public static int countLineNumberReader(String fileName) {

	      File file = new File(fileName);

	      int lines = 0;

	      try (LineNumberReader lnr = new LineNumberReader(new FileReader(file))) {

	          while (lnr.readLine() != null) ;

	          lines = lnr.getLineNumber();

	      } catch (IOException e) {
	          e.printStackTrace();
	      }

	      return lines;

	  }

    public void setString(String string) {
        this.string = string;
    }

    public void setInputAndNumberOfLine(String input, int lineCount) {
        this.input = input;

        this.lineCount = lineCount;

    }

    public Map<String, List<String>> getMapVariableProduction() {
        return mapVariableProduction;
    }

    public void convertToCNF() {
        convertToMap();                              // reading grammar
        nullProduction();                           // null production
        eliminateUseless();                        // remove duplicate value
        unitProduction();                         // remove single non-terminal
        twoTerminalandOneVariable();             // eliminate two terminal
        breakStringLongerThanTwo();             // eliminate more than two terminal
    }

     private void nullProduction() {
         System.out.println("Step I. \nEliminate € ...");

         for (int i = 0; i < lineCount; i++) {
             removeEpsilon();
         }
         displayMap();
     }
     private void eliminateUseless() {
         Iterator itr3 = mapVariableProduction.entrySet().iterator();
         while (itr3.hasNext()) {
             Map.Entry entry = (Map.Entry) itr3.next();
             ArrayList<String> productionRow = (ArrayList<String>) entry.getValue();

             for (int i = 0; i < productionRow.size(); i++) {
                 if (productionRow.get(i).contains(entry.getKey().toString())) {
                     productionRow.remove(entry.getKey().toString());
                 }
             }
         }
     }
     private void unitProduction() {

         System.out.println("Step II. \nEliminate unit production ...");

         for (int i = 0; i < lineCount; i++) {
             removeSingleVariable();
         }
         displayMap();
     }
     private void twoTerminalandOneVariable() {
         System.out.println("Step III. \nEliminate terminals ...");

         Iterator itr5 = mapVariableProduction.entrySet().iterator();
         String key = null;
         int asciiBegin = 77; //M
         Map<String, List<String>> tempList = new LinkedHashMap<>();
         while (itr5.hasNext()) {
             Map.Entry entry = (Map.Entry) itr5.next();
             Set set = mapVariableProduction.keySet();

             ArrayList<String> keySet = new ArrayList<String>(set);
             ArrayList<String> productionList = (ArrayList<String>) entry.getValue();
             Boolean found1 = false;
             Boolean found2 = false;
             Boolean found = false;

             for (int i = 0; i < productionList.size(); i++) {
                 String temp = productionList.get(i);
                 for (int j = 0; j < temp.length(); j++) {
                     if (temp.length() == 3) {
                         String newProduction = temp.substring(1, 3);
                         if (checkDuplicateInProductionList(tempList, newProduction) && checkDuplicateInProductionList(mapVariableProduction, newProduction)) {
                             found = true;
                         } else {
                             found = false;
                         }
                         if (found) {
                             ArrayList<String> newVariable = new ArrayList<>();
                             newVariable.add(newProduction);
                             key = Character.toString((char) asciiBegin);

                             tempList.put(key, newVariable);
                             asciiBegin++;
                         }

                     } else if (temp.length() == 2) { // if only two substring
                         for (int k = 0; k < keySet.size(); k++) {
                             if (!keySet.get(k).equals(Character.toString(productionList.get(i).charAt(j)))) { // if substring not equals to keySet
                                 found = false;
                             } else {
                                 found = true;
                                 break;
                             }
                         }
                         if (!found) {
                             String newProduction = Character.toString(productionList.get(i).charAt(j));
                             if (checkDuplicateInProductionList(tempList, newProduction) && checkDuplicateInProductionList(mapVariableProduction, newProduction)) {
                                 ArrayList<String> newVariable = new ArrayList<>();
                                 newVariable.add(newProduction);
                                 key = Character.toString((char) asciiBegin);
                                 tempList.put(key, newVariable);
                                 asciiBegin++;
                             }
                         }
                     } else if (temp.length() == 4) {

                         String newProduction1 = temp.substring(0, 2); // SA
                         String newProduction2 = temp.substring(2, 4); // SA

                         if (checkDuplicateInProductionList(tempList, newProduction1) && checkDuplicateInProductionList(mapVariableProduction, newProduction1)) {
                             found1 = true;
                         } else {
                             found1 = false;
                         }

                         if (checkDuplicateInProductionList(tempList, newProduction2) && checkDuplicateInProductionList(mapVariableProduction, newProduction2)) {
                             found2 = true;
                         } else {
                             found2 = false;
                         }
                         if (found1) {
                             ArrayList<String> newVariable = new ArrayList<>();
                             newVariable.add(newProduction1);
                             key = Character.toString((char) asciiBegin);

                             tempList.put(key, newVariable);
                             asciiBegin++;
                         }

                         if (found2) {
                             ArrayList<String> newVariable = new ArrayList<>();
                             newVariable.add(newProduction2);
                             key = Character.toString((char) asciiBegin);

                             tempList.put(key, newVariable);
                             asciiBegin++;
                         }
                     }
                 }
             }
         }
         mapVariableProduction.putAll(tempList);
         displayMap();
     }
    private void breakStringLongerThanTwo() {

        System.out.println("Step IV. \nBreak variable strings longer than 2 ... ");
        for (int i = 0; i < lineCount; i++) {
            removeThreeTerminal();
        }
        displayMap();

    }
    private String[] splitEnter(String input) {

        String[] tmpArray = new String[lineCount-1];
        for (int i = 0; i < lineCount; i++) {
            tmpArray = input.split("\\n");
        }
        return tmpArray;
    }

    private void displayMap() {

        Iterator it = mapVariableProduction.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " - " + pair.getValue());
        }
        System.out.println("\n###############################################");
    }

    private void  convertToMap() {

        String[] splitedEnterInput = splitEnter(input);
        for (int i = 0; i < splitedEnterInput.length-1; i++) {

            String[] tempString = splitedEnterInput[i].split("-|\\|");
            String variable = tempString[0].trim();

            String[] production = Arrays.copyOfRange(tempString, 1, tempString.length);
            List<String> productionList = new ArrayList<String>();

            // trim the empty space
            for (int k = 0; k < production.length; k++) {
                production[k] = production[k].trim();
            }

            // import array into ArrayList
            for (int j = 0; j < production.length; j++) {
                productionList.add(production[j]);
            }

            //insert element into map
            mapVariableProduction.put(variable, productionList);
        }
    }
    private void removeEpsilon() {

        Iterator itr = mapVariableProduction.entrySet().iterator();
        Iterator itr2 = mapVariableProduction.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            ArrayList<String> productionRow = (ArrayList<String>) entry.getValue();

            if (productionRow.contains("€")) {
                if (productionRow.size() > 1) {
                    productionRow.remove("€");
                    epsilonFound = entry.getKey().toString();
                } else {
                    // remove if less than 1
                    epsilonFound = entry.getKey().toString();
                    mapVariableProduction.remove(epsilonFound);
                }
            }
        }

        // find B and eliminate them
        while (itr2.hasNext()) {

            Map.Entry entry = (Map.Entry) itr2.next();
            ArrayList<String> productionList = (ArrayList<String>) entry.getValue();

            for (int i = 0; i < productionList.size(); i++) {
                String temp = productionList.get(i);
                for (int j = 0; j < temp.length(); j++) {
                    if (epsilonFound.equals(Character.toString(productionList.get(i).charAt(j)))) {

                        if (temp.length() == 2) {
                            // remove specific character in string
                            temp = temp.replace(epsilonFound, "");

                            if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
                                mapVariableProduction.get(entry.getKey().toString()).add(temp);
                            }

                        } else if (temp.length() == 3) {

                            String deletedTemp = new StringBuilder(temp).deleteCharAt(j).toString();

                            if (!mapVariableProduction.get(entry.getKey().toString()).contains(deletedTemp)) {
                                mapVariableProduction.get(entry.getKey().toString()).add(deletedTemp);
                            }

                        } else if (temp.length() == 4) {

                            String deletedTemp = new StringBuilder(temp).deleteCharAt(j).toString();

                            if (!mapVariableProduction.get(entry.getKey().toString()).contains(deletedTemp)) {
                                mapVariableProduction.get(entry.getKey().toString()).add(deletedTemp);
                            }
                        } else {

                            if (!mapVariableProduction.get(entry.getKey().toString()).contains("€")) {
                                mapVariableProduction.get(entry.getKey().toString()).add("€");
                            }
                        }
                    }
                }
            }
        }
    }
    private void removeSingleVariable() {

        Iterator itr4 = mapVariableProduction.entrySet().iterator();
        String key = null;
        String key2 = null;
        String keyHolder = "";

        while (itr4.hasNext()) {

            Map.Entry entry = (Map.Entry) itr4.next();
            Set set = mapVariableProduction.keySet();
            ArrayList<String> keySet = new ArrayList<String>(set);
            ArrayList<String> productionList = (ArrayList<String>) entry.getValue();
          
            for (int i = 0; i < productionList.size(); i++) {
                String temp = productionList.get(i);

                for (int j = 0; j < temp.length(); j++) {

                    for (int k = 0; k < keySet.size(); k++) {
                        if (keySet.get(k).equals(temp)) {

                            key = entry.getKey().toString();
                            List<String> productionValue = mapVariableProduction.get(temp);

                            for (int l = 0; l < productionValue.size(); l++) {
                            	key2 = productionValue.get(l);
                            	if ( productionValue.get(l).contains(key)) {
											
                            		keyHolder = key2.replace(key, temp);
                            		mapVariableProduction.get(temp).add(keyHolder);
								}
                            }
                            productionList.remove(temp);
                        }
                    }
                }
            }
        }
    }

    private Boolean checkDuplicateInProductionList(Map<String, List<String>> map, String key) {

        Boolean notFound = true;

        Iterator itr = map.entrySet().iterator();
        outerloop:

        while (itr.hasNext()) {

            Map.Entry entry = (Map.Entry) itr.next();
            ArrayList<String> productionList = (ArrayList<String>) entry.getValue();

            for (int i = 0; i < productionList.size(); i++) {
                if (productionList.size() < 2) {

                    if (productionList.get(i).equals(key)) {
                        notFound = false;
                        break outerloop;
                    } else {
                        notFound = true;
                    }
                }
            }
        }

        return notFound;
    }

    private void removeThreeTerminal() {

        Iterator itr = mapVariableProduction.entrySet().iterator();
        ArrayList<String> keyList = new ArrayList<>();
        Iterator itr2 = mapVariableProduction.entrySet().iterator();

        // obtain key that use to eliminate two terminal and above
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            ArrayList<String> productionRow = (ArrayList<String>) entry.getValue();

            if (productionRow.size() < 2) {
                keyList.add(entry.getKey().toString());
            }
        }

        // find more than three terminal or combination of variable and terminal to eliminate them
        while (itr2.hasNext()) {

            Map.Entry entry = (Map.Entry) itr2.next();
            ArrayList<String> productionList = (ArrayList<String>) entry.getValue();

            if (productionList.size() > 1) {
                for (int i = 0; i < productionList.size(); i++) {
                    String temp = productionList.get(i);

                    for (int j = 0; j < temp.length(); j++) {

                        if (temp.length() > 2) {
                            String stringToBeReplaced1 = temp.substring(0, temp.length() - j);
                            String stringToBeReplaced2 = temp.substring(j, temp.length());

                            for (String key : keyList) {

                                List<String> keyValues = new ArrayList<>();
                                keyValues = mapVariableProduction.get(key);
                                String[] values = keyValues.toArray(new String[keyValues.size()]);
                                String value = values[0];

                                if (stringToBeReplaced1.equals(value)) {

                                    mapVariableProduction.get(entry.getKey().toString()).remove(temp);
                                    temp = temp.replace(stringToBeReplaced1, key);

                                    if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
                                        mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
                                    }
                                } else if (stringToBeReplaced2.equals(value)) {

                                    mapVariableProduction.get(entry.getKey().toString()).remove(temp);
                                    temp = temp.replace(stringToBeReplaced2, key);

                                    if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
                                        mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
                                    }
                                }
                            }
                        } else if (temp.length() == 2) {

                            for (String key : keyList) {

                                List<String> keyValues = new ArrayList<>();
                                keyValues = mapVariableProduction.get(key);
                                String[] values = keyValues.toArray(new String[keyValues.size()]);
                                String value = values[0];


                                for (int pos = 0; pos < temp.length(); pos++) {
                                    String tempChar = Character.toString(temp.charAt(pos));


                                    if (value.equals(tempChar)) {

                                        mapVariableProduction.get(entry.getKey().toString()).remove(temp);
                                        temp = temp.replace(tempChar, key);

                                        if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
                                            mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            } else if (productionList.size() == 1) {

                for (int i = 0; i < productionList.size(); i++) {
                    String temp = productionList.get(i);

                    if (temp.length() == 2) {

                        for (String key : keyList) {

                            List<String> keyValues = new ArrayList<>();
                            keyValues = mapVariableProduction.get(key);
                            String[] values = keyValues.toArray(new String[keyValues.size()]);
                            String value = values[0];


                            for (int pos = 0; pos < temp.length(); pos++) {
                                String tempChar = Character.toString(temp.charAt(pos));


                                if (value.equals(tempChar)) {

                                    mapVariableProduction.get(entry.getKey().toString()).remove(temp);
                                    temp = temp.replace(tempChar, key);

                                    if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
                                        mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }


}



