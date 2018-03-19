package io.zipcoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemParser {

    private Pattern pattern;

    private Matcher matcher;

    public int counter = 0;



    private Map<String ,ArrayList<Item>> groceryListMap = new HashMap<String, ArrayList<Item>>();
    // Splits string based on ##

    public ArrayList<String> parseRawDataIntoStringArray(String rawData){
        String stringPattern = "##";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern , rawData);
        return response;
    }
    public Item parseStringIntoItem(String rawItem) throws ItemParseException {
        if (findName(rawItem) == null || findPrice(rawItem) == null) {
            throw new ItemParseException();

        }
        String Name = findName(rawItem);
        Double Price = Double.parseDouble(findPrice(rawItem));
        String Type = findType(rawItem);
        String Expiration = findExpiration(rawItem);

        return new Item(Name, Price, Type, Expiration);
    }

    public ArrayList<String> findKeyValuePairsInRawItemData(String rawItem){
        String stringPattern = "[^|;]";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern , rawItem);
        return response;
    }
    private ArrayList<String> splitStringWithRegexPattern(String stringPattern, String inputString) {
        return new ArrayList<String>(Arrays.asList(inputString.split(stringPattern)));
    }






    public String findName(String rawItem){
        String search = "(?<=([Nn][Aa][Mm][Ee][^A-Za-z])).*?(?=[^A-Za-z0])";
        pattern = Pattern.compile(search);
        matcher = pattern.matcher(rawItem);

        if (matcher.find()){
            if (!matcher.group().equals("")){
                String name = matcher.group().replaceAll("\\d","o");
                return name.toLowerCase();
            }
        }
        return null;
    }
    public String findPrice(String rawItem){
        pattern =Pattern.compile("(?<=([Pp][Rr][Ii][Cc][Ee][^A-Za-z])).*?(?=[^0-9.])");
        matcher = pattern.matcher(rawItem);

        if (matcher.find()){
            if (!matcher.group().equals("")){
                return matcher.group();
            }
        }
        return null;
    }
    public String findType(String rawItem){
        Pattern pattern =Pattern.compile("(?<=([Tt][Yy][Pp][Ee][^A-Za-z])).*?(?=[^A-Za-z0])");
        Matcher regMatcher =pattern.matcher(rawItem);

        if (regMatcher.find()){
            return (regMatcher).group().toLowerCase();

            } else return null;
        }



    public String findExpiration(String rawItem){
        Pattern pattern = Pattern.compile("(?<=([Tt][Yy][Pp][Ee][^A-Za-z])).*?(?=[^A-Za-z0])(.) + [^#]");
        Matcher regMatcher2 = pattern.matcher(rawItem);

        if (regMatcher2.find()){
            return (regMatcher2).group();

        } else return null;
    }
    public Map<String, ArrayList<Item>> getMap() throws Exception{
        Main main = new Main();


        ArrayList<String> listOfItems = parseRawDataIntoStringArray(main.readRawDataToString());

        for (String item : listOfItems){
            try {
                Item newItem = parseStringIntoItem(item);
                if (!groceryListMap.containsKey(newItem.getName())){
                    ArrayList<Item> myItem = new ArrayList<Item>();
                    myItem.add(newItem);
                    groceryListMap.put(newItem.getName(),myItem);
                }else {
                    groceryListMap.get(newItem.getName()).add(newItem);
                }
            }catch (ItemParseException e){
                counter++;
            }
        }
        return groceryListMap;
    }
    public String generateReport() throws Exception {
        groceryListMap = getMap();
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, ArrayList<Item>> groceryItems : groceryListMap.entrySet()) {
            builder.append("\nname: ");
            builder.append(String.format("%8s", captitalizeFirstLetter(groceryItems.getKey())));
            builder.append("\t\t\t\tseen: " + getOccurencesOfItems(groceryItems.getValue()) + " times\n");
            builder.append("===============" + "\t\t\t\t===============\n");
            String priceReport = generatePriceReport(groceryItems);
            builder.append(priceReport);
            builder.append("---------------" + "\t\t\t\t---------------\n");


        }
        builder.append("\nErrors\t\t\t\t\t\tseen: "+counter+" times\n");
        return builder.toString();
    }



    public String display() throws Exception{
        groceryListMap = getMap();
        StringBuilder displayBuild = new StringBuilder();

        for (Map.Entry<String,ArrayList<Item>> item: groceryListMap.entrySet()){
            String upperCase = item.getKey().substring(0,1).toUpperCase() + item.getKey().substring(1);

            displayBuild.append("\n" + String.format("%-5s%10s%15s%2d%5s", "name:", upperCase, "seen: ", item.getValue().size(), "  times"));
            displayBuild.append("\n" + String.format("%15s%3s%5s", "===============", "\t\t\t", "===============") + "\n");

            ArrayList<Double> uniquePriceList = getUniquePrices(item);
            for (int i = 0; i < uniquePriceList.size(); i++) {
                displayBuild.append(String.format("%-11s%.2f%15s%2d%5s", "Price:", uniquePriceList.get(i), "seen: ", getPriceOccurences(item.getValue(), uniquePriceList.get(i)), "  times"));
                displayBuild.append("\n" + String.format("%15s%3s%5s", "---------------", "\t\t\t", "---------------") + "\n");
            }

        }
        displayBuild.append("\n" + String.format("%-20s%10s%2d%5s", "Errors", "seen: ", counter, "  times"));

        return displayBuild.toString();

    }
    public int getOccurencesOfItems(ArrayList list) {
        return list.size();
    }

    public int getPriceOccurences(ArrayList<Item> list, Double price) {
        int countPrice =0;
        for (int i =0; i < list.size();i++){
            if (list.get(i).getPrice().equals(price)){
                countPrice++;
            }
        }
        return countPrice;
    }
    public String generatePriceReport(Map.Entry<String,ArrayList<Item>> input) {
        String reportPrice = "";
        ArrayList<Double> nonDuplicatePrices = getUniquePrices(input);
        for (int i = 0; i < nonDuplicatePrices.size(); i++) {
            reportPrice += "Price";
            reportPrice += (String.format("%10s", nonDuplicatePrices.get(i)));
            reportPrice += ("\t\t\t\tseen: " + getPriceOccurences(input.getValue(), nonDuplicatePrices.get(i)) + " times\n");

        }
        return reportPrice;
    }


    private ArrayList<Double> getUniquePrices(Map.Entry<String, ArrayList<Item>> item) {
        ArrayList<Double> uniquePrice = new ArrayList<Double>();
        for (int i = 0; i < item.getValue().size();i++){
            if (!uniquePrice.contains(item.getValue().get(i).getPrice()));
            uniquePrice.add(item.getValue().get(i).getPrice());

        }
        return uniquePrice;

    }
    public String captitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }


}
