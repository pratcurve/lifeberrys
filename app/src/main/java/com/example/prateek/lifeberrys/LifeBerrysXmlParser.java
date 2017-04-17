package com.example.prateek.lifeberrys;

import android.support.annotation.XmlRes;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prateek on 2/7/16.
 */
public class LifeBerrysXmlParser {

    private static final String namespace = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            System.out.println("Parsing");
            parser.setFeature(Xml.FEATURE_RELAXED, true);
            parser.setInput(in, "UTF-8");
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Item> items = new ArrayList<Item>();
        List<HashMap<String, String>> secondary = new ArrayList<HashMap<String, String>>();
        parser.require(XmlPullParser.START_TAG, namespace, "rss");
//        Log.i("Tag", name);
        while(parser.getEventType() != XmlPullParser.END_DOCUMENT) {
             if (parser.getEventType() == XmlPullParser.START_TAG) {
//                System.out.println("Start TAg " + parser.getName());
                if (parser.getName().equals("item")) {
                    items.add(readItem(parser));
                }
            } else if (parser.getEventType() == XmlPullParser.END_TAG) {
//                System.out.println("End Tag " + parser.getName());
            }
            parser.next();
        }
        return items;
    }


    public static class Item implements Serializable {
        public final  String catid, category, mainHeading, mainImage, guID, link;
        public List<String> image = new ArrayList<>();
        public List<String> description = new ArrayList<>();
        public List<String> heading = new ArrayList<>();
        private Item(String catid, String link,String category, String mainHeading, String mainImage, String guID, List<String> image, List<String> description, List<String> heading) {
            this.catid = catid;
            this.link = link;
            this.category = category;
            this.mainHeading = mainHeading;
            this.mainImage = mainImage;
            this.guID = guID;
            this.image = image;
            this.description = description;
            this.heading = heading;
        }
    }

    private Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "item");
        String section[];
        List<String> image = new ArrayList<>();
        List<String> description = new ArrayList<>();
        List<String> heading = new ArrayList<>();
        String catid = "", category = "", mainHeading = "", mainImage = "", guID = "", link = "";
        while (parser.next() != XmlPullParser.END_TAG) {
//            System.out.println("Inside Item Tag " + parser.getName());
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("catid")) {
                catid = readCatid(parser);
            } else if (name.equals("url")) {
                link = readLink(parser);
            } else if (name.equals("category")) {
                category = readCategory(parser);
            } else if (name.equals("heading")) {
                mainHeading = readHeading(parser);
            } else if (name.equals("mainimage")) {
                mainImage = readMainImage(parser);
            } else if (name.equals("guID")) {
                guID = readguID(parser);
            } else if (name.equals("section")) {
                section = readSection(parser);
                image.add(section[0]);
                description.add(section[1]);
                heading.add(section[2]);
            } else {
                skip(parser);
            }
        }
        return new Item(catid, link, category, mainHeading, mainImage, guID, image, description, heading);
    }

    private String readCatid(XmlPullParser parser)  throws XmlPullParserException, IOException {
        String catId = "";
        parser.require(XmlPullParser.START_TAG, namespace, "catid");
        catId = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "catid");
        return catId;
    }

    private String readLink(XmlPullParser parser)  throws XmlPullParserException, IOException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, namespace, "url");
        link = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "url");
        System.out.println(link);
        return link;
    }

    private String readCategory(XmlPullParser parser) throws XmlPullParserException, IOException {
        String category = "";
        parser.require(XmlPullParser.START_TAG, namespace, "category");
        category = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "category");
        return category;
    }

    private String readMainImage(XmlPullParser parser) throws XmlPullParserException, IOException {
        String mainImage = "";
        parser.require(XmlPullParser.START_TAG, namespace, "mainimage");
        mainImage = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "mainimage");
        return mainImage;
    }

    private String readguID(XmlPullParser parser) throws XmlPullParserException, IOException {
        String guID = "";
        parser.require(XmlPullParser.START_TAG, namespace, "guID");
        guID = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "guID");
        return guID;
    }


    private String[] readSection(XmlPullParser parser) throws XmlPullParserException, IOException {
        String[] section = new String[3];
        parser.require(XmlPullParser.START_TAG, namespace, "section");
//        System.out.println("Inside Section Tag " + parser.getName());
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            else if (parser.getName().equals("image")) {
                section[0] = readImg(parser);
//                System.out.println(section[0]);
            } else if (parser.getName().equals("description")) {
                section[1] = readDesc(parser);
//                System.out.println(section[1]);
            } else if (parser.getName().equals("heading")) {
                section[2] = readHeading(parser);
//                System.out.println(section[2]);
            } else {
                skip(parser);
            }
        }
        return section;
    }

    private String readDesc(XmlPullParser parser) throws XmlPullParserException, IOException {
        String description = "";
//        System.out.println("Inside readDesc method");
        parser.require(XmlPullParser.START_TAG, namespace, "description");
        description = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "description");
        return description;
    }

    private String readImg(XmlPullParser parser) throws XmlPullParserException, IOException {
        String img = "";
        parser.require(XmlPullParser.START_TAG, namespace, "image");
//        System.out.println("inside image Tag " + parser.getName());
        img = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "image");
        return img;
    }

    private String readHeading(XmlPullParser parser) throws XmlPullParserException, IOException {
        String heading = "";
        parser.require(XmlPullParser.START_TAG, namespace, "heading");
        heading = readText(parser);
        parser.require(XmlPullParser.END_TAG, namespace, "heading");
        return  heading;
    }

    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
//            System.out.println("here");
            result = parser.getText();
            parser.nextTag();
        }
//        System.out.println(result);
        return  result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}



