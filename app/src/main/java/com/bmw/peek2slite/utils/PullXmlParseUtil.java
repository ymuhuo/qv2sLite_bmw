package com.bmw.peek2slite.utils;

import android.text.TextUtils;
import android.util.Xml;

import com.bmw.peek2slite.model.QueXianInfo;
import com.bmw.peek2slite.model.QueXian_GradeInfo;
import com.bmw.peek2slite.model.QueXian_StyleInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/3/6.
 */

public class PullXmlParseUtil {

    public static final String PipeSection = "PipeSection";
    public static final String Distance = "Distance";
    public static final String DefectType = "DefectType";
    public static final String DefectCode = "DefectCode";
    public static final String DefectLevel = "DefectLevel";
    public static final String ClockExpression = "ClockExpression";
    public static final String DefectLength = "DefectLength";
    public static final String DefectDescription = "DefectDescription";

    public static List<QueXianInfo> parseQueXianXml(InputStream inputStream) {

        if (inputStream == null)
            return null;
        XmlPullParser xmlPullParser = Xml.newPullParser();
        List<QueXianInfo> list = null;
        List<QueXian_StyleInfo> styleList = null;
        List<QueXian_GradeInfo> gradeList = null;
        QueXianInfo queXianInfo = null;
        QueXian_StyleInfo queXian_styleInfo = null;
        QueXian_GradeInfo queXian_gradeInfo = null;


        try {
            xmlPullParser.setInput(inputStream, "utf-8");

            int type = xmlPullParser.getEventType();

            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("ArrayOfDefectType".equals(xmlPullParser.getName())) {
                            list = new ArrayList<>();
                        } else if ("DefectType".equals(xmlPullParser.getName())) {
                            queXianInfo = new QueXianInfo();
                            styleList = new ArrayList<>();
                        } else if ("Index".equals(xmlPullParser.getName())) {
                            if (queXian_styleInfo == null)
                                queXianInfo.setIndex(Integer.parseInt(xmlPullParser.nextText()));
                            else
                                queXian_styleInfo.setIndex(Integer.parseInt(xmlPullParser.nextText()));
                        } else if ("Name".equals(xmlPullParser.getName())) {
                            if (queXian_styleInfo == null)
                                queXianInfo.setName(xmlPullParser.nextText());
                            else
                                queXian_styleInfo.setName(xmlPullParser.nextText());
                        } else if ("Code".equals(xmlPullParser.getName())) {
                            if (queXian_styleInfo == null)
                                queXianInfo.setCode(xmlPullParser.nextText());
                            else
                                queXian_styleInfo.setCode(xmlPullParser.nextText());
                        } else if ("Defect".equals(xmlPullParser.getName())) {
                            queXian_styleInfo = new QueXian_StyleInfo();
                            gradeList = new ArrayList<>();
                        } else if ("DefectDescribe".equals(xmlPullParser.getName())) {
                            queXian_gradeInfo = new QueXian_GradeInfo();
                        } else if ("Level".equals(xmlPullParser.getName())) {
                            queXian_gradeInfo.setLevel(Integer.parseInt(xmlPullParser.nextText()));
                        } else if ("Content".equals(xmlPullParser.getName())) {
                            queXian_gradeInfo.setContent(xmlPullParser.nextText());
                        }else if ("Define".equals(xmlPullParser.getName())) {
                            queXian_styleInfo.setDefine(xmlPullParser.nextText());
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        if ("DefectType".equals(xmlPullParser.getName())) {

                            queXianInfo.setStyleList(styleList);
                            list.add(queXianInfo);
                            queXianInfo = null;
                            styleList = null;
                        } else if ("DefectDescribe".equals(xmlPullParser.getName())) {
                            gradeList.add(queXian_gradeInfo);
                            queXian_gradeInfo = null;
                        } else if ("Defect".equals(xmlPullParser.getName())) {
                            queXian_styleInfo.setGradeList(gradeList);
                            styleList.add(queXian_styleInfo);
                            queXian_styleInfo = null;
                            gradeList = null;
                        }


                        break;
                    default:
                        break;
                }
                type = xmlPullParser.next();
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputStream != null)
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return list;
    }


    public static void writeXml(File file,String fileName,String pipeId,String[] keys,Map<String,String> map){

        // XmlSerializer用于写xml数据
        XmlSerializer serializer = Xml.newSerializer();
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            serializer.setOutput(outputStream,"utf-8");

            serializer.startDocument("UTF-8", null);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.setPrefix("xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.startTag("","PipeDefectImage");

            if(pipeId != null && !TextUtils.isEmpty(pipeId)){
                serializer.startTag(null, "PipeSection");
                serializer.text(pipeId);
                serializer.endTag(null,  "PipeSection");
            }

            if(keys != null && map != null) {
                serializer.startTag(null, "Defect");

                for (int i = 0; i < keys.length; i++) {
                    if(!TextUtils.isEmpty(map.get(keys[i]))) {
                        serializer.startTag(null, keys[i]);
                        serializer.text(map.get(keys[i]));
                        serializer.endTag(null, keys[i]);
                    }
                }

                serializer.endTag(null, "Defect");
            }

            serializer.startTag(null,"Filename");
            serializer.text(fileName+".jpg");
            serializer.endTag(null,"Filename");

            serializer.endTag("","PipeDefectImage");
            serializer.endDocument();

            serializer.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String ,String> parsePicXml(File file){

        XmlPullParser pullParser = Xml.newPullParser();
        Map<String,String> map = null;

        try {
            InputStream inputStream = new FileInputStream(file);
            pullParser.setInput(inputStream,"utf-8");
            int type = pullParser.getEventType();

            while (type != XmlPullParser.END_DOCUMENT){
                switch (type){
                    case XmlPullParser.START_TAG:
                        if(pullParser.getName().equals("PipeDefectImage")){
                            map = new HashMap<>();
                        }else if(pullParser.getName().equals(Distance)){
                            map.put(Distance,pullParser.nextText());
                        }else if(pullParser.getName().equals(DefectType)){
                            map.put(DefectType,pullParser.nextText());
                        }else if(pullParser.getName().equals(DefectCode)){
                            map.put(DefectCode,pullParser.nextText());
                        }else if(pullParser.getName().equals(DefectLevel)){
                            map.put(DefectLevel,pullParser.nextText());
                        }else if(pullParser.getName().equals(ClockExpression)){
                            map.put(ClockExpression,pullParser.nextText());
                        }else if(pullParser.getName().equals(DefectLength)){
                            map.put(DefectLength,pullParser.nextText());
                        }else if(pullParser.getName().equals(DefectDescription)){
                            map.put(DefectDescription,pullParser.nextText());
                        }else if(pullParser.getName().equals(PipeSection)){
                            map.put(PipeSection,pullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                type = pullParser.next();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return map;
    }
}
