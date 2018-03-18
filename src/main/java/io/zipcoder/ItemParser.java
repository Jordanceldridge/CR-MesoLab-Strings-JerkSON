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
    private int counter = 0;
    private Main main = new Main();
    private Map<String ,ArrayList<Item>> groceryListMap = new HashMap<String, ArrayList<Item>>();
    // Splits string based on ##
    public ArrayList<String> parseRawDataIntoStringArray(String rawData){
        String stringPattern = "##";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern , rawData);
        return response;
    }

    public ArrayList<String> findKeyValuePairsInRawItemData(String rawItem){
        String stringPattern = "[^|*|@|!|$|%|&|;]";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern , rawItem);
        return response;
    }
    private ArrayList<String> splitStringWithRegexPattern(String stringPattern, String inputString) {
        return new ArrayList<String>(Arrays.asList(inputString.split(stringPattern)));
    }

    public Item parseStringIntoItem(String rawItem) throws ItemParseException {
        if (findName(rawItem) == null || findPrice(rawItem) == null){
            throw new ItemParseException();

        }
        String itemName = findName(rawItem);
        Double ittemPrice = Double.parseDouble(findPrice(rawItem));
        String itemType = findType(rawItem);
        String itemExpiration = findExpiration(rawItem);

        return new Item(itemName,ittemPrice,itemType,itemExpiration);



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
        pattern =Pattern.compile("(?<=([Tt][Yy][Pp][Ee][^A-Za-z])).*?(?=[^A-Za-z0])");
        matcher.pattern().matcher(rawItem);

        if (matcher.find()){
            return matcher.group().toLowerCase();

            }
        return null;
        }



    public String findExpiration(String rawItem){
        pattern = Pattern.compile("(?<=([Tt][Yy][Pp][Ee][^A-Za-z])).*?(?=[^A-Za-z0])(.) + [^#]");
        matcher = pattern.matcher(rawItem);

        if (matcher.find()){
            return matcher.group();
        }

        return null;
    }
    public Map<String, ArrayList<Item>> getMap() throws Exception{
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

    public String display() throws Exception{
        groceryListMap = getMap();
        StringBuilder displayBuild = new StringBuilder();

        for (Map.Entry<String,ArrayList<Item>> item: groceryListMap.entrySet()){
            String upperCase = item.getKey().substring(0,1).toUpperCase() + item.getKey().substring(1);

            displayBuild.append("\n" + String.format("%-5s%10s%15s%2d%5s", "name:", upperCase, "seen: ", item.getValue().size(), "  times"));
            displayBuild.append("\n" + String.format("%15s%3s%5s", "===============", "\t\t\t", "===============") + "\n");

            ArrayList<Double> uniquePriceList = getUniquePrices(item);
            for (int i = 0; i < uniquePriceList.size(); i++) {
                displayBuild.append(String.format("%-11s%.2f%15s%2d%5s", "Price:", uniquePriceList.get(i), "seen: ", seenPriceOccurences(item.getValue(), uniquePriceList.get(i)), "  times"));
                displayBuild.append("\n" + String.format("%15s%3s%5s", "---------------", "\t\t\t", "---------------") + "\n");
            }

        }
        displayBuild.append("\n" + String.format("%-20s%10s%2d%5s", "Errors", "seen: ", counter, "  times"));

        return displayBuild.toString();

    }
    public int seenPriceOccurences(ArrayList<Item> list, Double price) {
        int countPrice =0;
        for (int i =0; i < list.size();i++){
            if (list.get(i).getPrice().equals(price)){
                countPrice++;
            }
        }
        return countPrice;
    }


    private ArrayList<Double> getUniquePrices(Map.Entry<String, ArrayList<Item>> item) {
        ArrayList<Double> uniquePrice = new ArrayList<Double>();
        for (int i = 0; i < item.getValue().size();i++){
            if (!uniquePrice.contains(item.getValue().get(i).getPrice()));
            uniquePrice.add(item.getValue().get(i).getPrice());

        }
        return uniquePrice;

    }


}
