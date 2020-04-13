package com.example.pablo.kayakapp.etc.xml;

import android.os.Environment;
import org.w3c.dom.Element;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Created by Pablo on 05/04/2018.
 */

public class XML {

    String file;

    public XML(String filename) { this.file = filename;}


    public String[] getInfoXML(){

        Document document = null;
        String [] info = new String[11];

        try {
            File tarjeta = Environment.getExternalStorageDirectory();
            File uri = new File(tarjeta.getAbsolutePath()+"/Piragua/PreEntrenos/"+file);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(uri);
            document.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        NodeList listnodos = document.getElementsByTagName("Titulo");
        info[0] = listnodos.item(0).getTextContent();

        listnodos = document.getElementsByTagName("Date");
        info[1]=listnodos.item(0).getTextContent();

        listnodos = document.getElementsByTagName("Ritmos");
        info[2]=listnodos.item(0).getTextContent();

        listnodos = document.getElementsByTagName("NumeroBloques");
        info[3]=listnodos.item(0).getTextContent();

        listnodos = document.getElementsByTagName("DescansoBloquesMin");
        info[4]=listnodos.item(0).getTextContent();

        listnodos = document.getElementsByTagName("NumeroSeries");
        info[5]=listnodos.item(0).getTextContent();

        listnodos = document.getElementsByTagName("DescansoSeriesMin");
        info[6]=listnodos.item(0).getTextContent();

        listnodos = document.getElementsByTagName("TiempoSeriesMin");
        info[7]=listnodos.item(0).getTextContent();

        listnodos = document.getElementsByTagName("TiempoSeriesSec");
        info[8]=listnodos.item(0).getTextContent();

        listnodos = document.getElementsByTagName("DescansoBloquesSeg");
        info[9]=listnodos.item(0).getTextContent();

        listnodos = document.getElementsByTagName("DescansoSeriesSeg");
        info[10]=listnodos.item(0).getTextContent();


    return info;

    }




    public boolean writeSessionXML (String tittle, String rit, String numB, String desB, String numS, String desS, String timeMin, String timeSec, String desSB, String desSS){

        File tarjeta = Environment.getExternalStorageDirectory();
        File dir = new File((tarjeta.getAbsolutePath() + "/Piragua/PreEntrenos/"+tittle+".xml"));

        if(dir.exists()) return false;

        Document dom;
        Element e;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            dom = docBuilder.newDocument();
            Element mainRootElement = dom.createElement("Entrenamiento");

            e = dom.createElement("Titulo");
            e.appendChild(dom.createTextNode(tittle));
            mainRootElement.appendChild(e);

            String fileDate = calcDate();

            e = dom.createElement("Date");
            e.appendChild(dom.createTextNode(fileDate));
            mainRootElement.appendChild(e);

            e = dom.createElement("Ritmos");
            e.appendChild(dom.createTextNode(rit));
            mainRootElement.appendChild(e);

            e = dom.createElement("NumeroBloques");
            e.appendChild(dom.createTextNode(numB));
            mainRootElement.appendChild(e);

            e = dom.createElement("DescansoBloquesMin");
            e.appendChild(dom.createTextNode(desB));
            mainRootElement.appendChild(e);

            e = dom.createElement("DescansoBloquesSeg");
            e.appendChild(dom.createTextNode(desSB));
            mainRootElement.appendChild(e);

            e = dom.createElement("NumeroSeries");
            e.appendChild(dom.createTextNode(numS));
            mainRootElement.appendChild(e);

            e = dom.createElement("DescansoSeriesMin");
            e.appendChild(dom.createTextNode(desS));
            mainRootElement.appendChild(e);

            e = dom.createElement("DescansoSeriesSeg");
            e.appendChild(dom.createTextNode(desSS));
            mainRootElement.appendChild(e);

            e = dom.createElement("TiempoSeriesMin");
            e.appendChild(dom.createTextNode(timeMin));
            mainRootElement.appendChild(e);

            e = dom.createElement("TiempoSeriesSec");
            e.appendChild(dom.createTextNode(timeSec));
            mainRootElement.appendChild(e);

            dom.appendChild(mainRootElement);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                DOMSource dSource = new DOMSource(dom);
                StreamResult sResult = new StreamResult(dir);
                tr.transform(dSource, sResult);
            } catch (TransformerException te) {
                te.printStackTrace();
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }

        return true;
    }

    private String calcDate() {
        Calendar fecha = new GregorianCalendar();

        int annio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH)+1;
        int dia = fecha.get(Calendar.DAY_OF_MONTH);

        return ""+dia+"-"+mes+"-"+annio;
    }
}
