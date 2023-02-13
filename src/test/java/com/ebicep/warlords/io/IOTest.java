//package com.ebicep.warlords.io;
//
//import org.apache.commons.io.IOUtils;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONValue;
//import org.json.simple.parser.ParseException;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.net.URL;
//
//public class IOTest {
//
//    @Test
//    public void test() {
//        long start = System.nanoTime();
//        String url = "https://sessionserver.mojang.com/session/minecraft/profile/9f2b22303b2c4b0fa141d7b598e236c7";
//        try {
//            String nameJson = IOUtils.toString(new URL(url));
//            JSONObject nameValue = (JSONObject) JSONValue.parseWithException(nameJson);
//            JSONArray properties = (JSONArray) nameValue.get("properties");
//            JSONObject property = (JSONObject) properties.get(0);
//            System.out.println(properties);
//            System.out.println(property);
//            System.out.println(property.get("value"));
//
//            long end = System.nanoTime();
//            System.out.println("Time: " + (end - start) / 1000000 + "ms");
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//        }
//    }
//}
