package io.zipcoder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ItemParserTest {

    private String rawSingleItem =    "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##";

    private String rawSingleItemIrregularSeperatorSample = "naMe:MiLK;price:3.23;type:Food^expiration:1/11/2016##";

    private String rawBrokenSingleItem =    "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##";

    private String rawMultipleItems = "naMe:Milk;price:3.23;type:Food;expiration:1/25/2016##"
                                      +"naME:BreaD;price:1.23;type:Food;expiration:1/02/2016##"
                                      +"NAMe:BrEAD;price:1.23;type:Food;expiration:2/25/2016##";
    private ItemParser itemParser;

    @Before
    public void setUp(){
        itemParser = new ItemParser();
    }

    @Test
    public void parseRawDataIntoStringArrayTest(){
        Integer expectedArraySize = 3;
        ArrayList<String> items = itemParser.parseRawDataIntoStringArray(rawMultipleItems);
        Integer actualArraySize = items.size();
        assertEquals(expectedArraySize, actualArraySize);
    }

    @Test
    public void parseStringIntoItemTest() throws ItemParseException{
        Item expected = new Item("milk", 3.23, "food","1/25/2016");
        Item actual = itemParser.parseStringIntoItem(rawSingleItem);
        assertEquals(expected.toString(), actual.toString());
    }

    @Test(expected = ItemParseException.class)
    public void parseBrokenStringIntoItemTest() throws ItemParseException{
        itemParser.parseStringIntoItem(rawBrokenSingleItem);
    }

    @Test
    public void findKeyValuePairsInRawItemDataTest(){
        Integer expected = 4;
        Integer actual = itemParser.findKeyValuePairsInRawItemData(rawSingleItem).size();
        assertEquals(expected, actual);
    }

    @Test
    public void findKeyValuePairsInRawItemDataTestIrregular(){
        Integer expected = 4;
        Integer actual = itemParser.findKeyValuePairsInRawItemData(rawSingleItemIrregularSeperatorSample).size();
        assertEquals(expected, actual);
    }
    @Test
    public void findNameTest(){
        String expected = "milk";
        String actual = itemParser.findName(rawSingleItem);
        Assert.assertEquals(expected,actual);

    }
    @Test
    public void findPriceTest() {
        String expected = "3.23";
        String actual = itemParser.findPrice(rawSingleItem);
        Assert.assertEquals(expected, actual);
    }
    @Test
    public void findExpirationTest() {
        String expected = "1/25/2016";
        String actual = itemParser.findExpiration(rawSingleItem);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void findTypeTest() {
        String expected = "food";
        String actual = itemParser.findType(rawSingleItem);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void display() throws Exception{
        String expected = "\n" + "name:     Bread         seen:  6  times\n" + "===============\t\t\t===============\n" +"Price:     1.23         seen:  6  times\n" + "---------------\t\t\t---------------\n" + "\n" + "name:      Milk         seen:  6  times\n" + "===============\t\t\t===============\n" + "Price:     3.23         seen:  5  times\n" + "---------------\t\t\t---------------\n" + "Price:     1.23         seen:  1  times\n" + "---------------\t\t\t---------------\n" + "\n" + "name:    Apples         seen:  4  times\n" + "===============\t\t\t===============\n" +
                "Price:     0.25         seen:  2  times\n" + "---------------\t\t\t---------------\n" + "Price:     0.23         seen:  2  times\n" + "---------------\t\t\t---------------\n" + "\n" + "name:   Cookies         seen:  8  times\n" + "===============\t\t\t===============\n" + "Price:     2.25         seen:  8  times\n" + "---------------\t\t\t---------------\n" + "\n" + "Errors                  seen:  4  times";
        String actual = itemParser.display();
        Assert.assertEquals(expected,actual);


    }
    @Test
    public void getPriceOccurencesTest() throws ItemParseException {
        ArrayList<Item> test = new ArrayList<Item>();
        Item item1 = itemParser.parseStringIntoItem(rawSingleItem);
        String rawItem2 = ("naME:BreaD;price:1.23;type:Food;expiration:1/02/2016##");
        String rawItem3 = "naMe:Bread;price:1.23;type:Food^expiration:1/11/2016##";
        Item item2 = itemParser.parseStringIntoItem(rawItem2);
        Item item3 = itemParser.parseStringIntoItem(rawItem3);
        test.add(item1);
        test.add(item2);
        test.add(item3);
        int expected =2;
        int actual = itemParser.getPriceOccurences(test,1.23);

        Assert.assertEquals(expected,actual);
    }

    }
