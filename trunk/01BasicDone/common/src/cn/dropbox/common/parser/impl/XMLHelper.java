package cn.dropbox.common.parser.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import cn.dropbox.common.parser.api.XMLConstants;
import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.File;

public class XMLHelper implements XMLConstants {
    
    public static Document getDOMMaker() throws ParserConfigurationException {
        //get an instance of factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //get an instance of builder
        DocumentBuilder db = dbf.newDocumentBuilder();
        //create an instance of DOM
        Document dom = db.newDocument();
        return dom;
    }
    
    public static String constructXMLString(Document dom) throws IOException {
        // Make a string out of the DOM object
        OutputFormat format = new OutputFormat(dom);
        format.setIndenting(true);
        ByteArrayOutputStream byteoutStream = new ByteArrayOutputStream();
        XMLSerializer serializer = new XMLSerializer(byteoutStream, format);
        serializer.serialize(dom);
        String retXMLStr = new String(byteoutStream.toByteArray());
        byteoutStream.close();
        return retXMLStr;
    }
    

    public static Element constructDateElement(Document dom, Date date) {
        Element resDateEle = dom.createElement(RESOURCE_DATE_TAG);
        GregorianCalendar lastModifiedCal = new GregorianCalendar();
        lastModifiedCal.setTime(date);
        
        Element resDateYearEle = dom.createElement(RESOURCE_DATE_YEAR_TAG);
        Text resDateYearText = dom.createTextNode(Integer.toString(lastModifiedCal.get(Calendar.YEAR)));
        resDateYearEle.appendChild(resDateYearText);
        resDateEle.appendChild(resDateYearEle);
        Element resDateMonthEle = dom.createElement(RESOURCE_DATE_MONTH_TAG);
        Text resDateMonthText = dom.createTextNode(Integer.toString(lastModifiedCal.get(Calendar.MONTH)));
        resDateMonthEle.appendChild(resDateMonthText);
        resDateEle.appendChild(resDateMonthEle);
        Element resDateDayEle = dom.createElement(RESOURCE_DATE_DAY_TAG);
        Text resDateDayText = dom.createTextNode(Integer.toString(lastModifiedCal.get(Calendar.DATE)));
        resDateDayEle.appendChild(resDateDayText);
        resDateEle.appendChild(resDateDayEle);
        Element resDateHourEle = dom.createElement(RESOURCE_DATE_HOUR_TAG);
        Text resDateHourText = dom.createTextNode(Integer.toString(lastModifiedCal.get(Calendar.HOUR)));
        resDateHourEle.appendChild(resDateHourText);
        resDateEle.appendChild(resDateHourEle);
        Element resDateMinuteEle = dom.createElement(RESOURCE_DATE_MINUTE_TAG);
        Text resDateMinuteText = dom.createTextNode(Integer.toString(lastModifiedCal.get(Calendar.MINUTE)));
        resDateMinuteEle.appendChild(resDateMinuteText);
        resDateEle.appendChild(resDateMinuteEle);        
        Element resDateSecondEle = dom.createElement(RESOURCE_DATE_SECOND_TAG);
        Text resDateSecondText = dom.createTextNode(Integer.toString(lastModifiedCal.get(Calendar.SECOND)));
        resDateSecondEle.appendChild(resDateSecondText);
        resDateEle.appendChild(resDateSecondEle);
        return resDateEle;
    }    
    
    public static Document getDocumentFromStream(InputStream xmlStream) {
        // get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // Using factory get an instance of document builder
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document dom = db.parse(xmlStream);
            return dom;
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * I take a xml element and the tag name, look for the tag and get
     * the text content 
     * i.e for <employee><name>John</name></employee> xml snippet if
     * the Element points to employee node and tagName is name I will return John  
     * @param ele
     * @param tagName
     * @return
     */
    public static String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
            if (textVal != null) {
                textVal = textVal.trim();
            }
        }

        return textVal;
    }    
    
    /**
     * Calls getTextValue and returns a int value
     * @param ele
     * @param tagName
     * @return
     */
    public static int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele,tagName));
    }

}
