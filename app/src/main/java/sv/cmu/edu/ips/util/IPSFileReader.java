package sv.cmu.edu.ips.util;

import android.os.Environment;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import sv.cmu.edu.ips.data.ClassificationData;
import sv.cmu.edu.ips.data.LabelData;
import sv.cmu.edu.ips.data.WiFiData;

/**
 * Created by sumeet on 11/30/14.
 */
public class IPSFileReader {

    public static int getCountOfDataCollected(){
        File root = new File(Environment.getExternalStorageDirectory(), "IPS");
        File[] files = null;
        int count = 0;
        if (root.exists()) {
            FileFilter labelFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    boolean isOfInterest = false;
                    if (pathname.isDirectory() && pathname.getAbsolutePath().contains("room")) {
                        isOfInterest = true;
                    }
                    return isOfInterest;
                }
            };

            files = getFilesOfInterest(root, labelFilter);
        }

        if(files != null){
            count = files.length;
        }

        return count;
    }

    public static List<LabelData> getLabelData(){
        List<LabelData> labels = new ArrayList<LabelData>();

        File root = new File(Environment.getExternalStorageDirectory(), "IPS");
        if (root.exists())
        {
            FileFilter labelFilter= new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    boolean isOfInterest = false;
                    if(pathname.isDirectory() && pathname.getAbsolutePath().contains("room") ){
                        isOfInterest = true;
                    }
                    return isOfInterest;
                }
            };

            File[] files = getFilesOfInterest(root, labelFilter);

            for(File file:files){
                readLabeledDataJson(labels, file);
            }
        }
        return  labels;
    }

    private static File[] getFilesOfInterest(File root, FileFilter filter) {
        return root.listFiles(filter);
    }

    private static void readLabeledDataJson(List<LabelData> labels, File file) {
        if(file.isFile() && file.canRead()){
            labels.add(getLabelDataFromFile(file));
        }else if(file.isDirectory()){
            FileFilter labelFilter= new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    boolean isOfInterest = false;
                    if(pathname.isFile() && pathname.getAbsolutePath().contains("label.json") ){
                        isOfInterest = true;
                    }
                    return isOfInterest;
                }
            };

            File[] files = getFilesOfInterest(file,labelFilter);
            for(File fl:files){
                readLabeledDataJson(labels, fl);
            }
        }
    }

    private static LabelData getLabelDataFromFile( File file) {
        LabelData label= null;
        try {
            String text = getStringFromFile(file);
            label = LabelData.getLabelDataFromJsonString(text);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return label;
    }

    public static List<ClassificationData> getClassificationData() {

        List<ClassificationData> classificationDatas = new ArrayList<ClassificationData>();
        File root = new File(Environment.getExternalStorageDirectory(), "IPS");
        if (root.exists())
        {
            FileFilter labelFilter= new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    boolean isOfInterest = false;
                    if(pathname.isDirectory() && pathname.getAbsolutePath().contains("room") ){
                        isOfInterest = true;
                    }
                    return isOfInterest;
                }
            };

            File[] folders = getFilesOfInterest(root, labelFilter);

            for(File folder:folders){
               ClassificationData data = new ClassificationData();
               File[] files = folder.listFiles();
                for(File file:files){
                    if(file.getAbsolutePath().contains("WiFiData.json")){
                        data.setWifiData(readWifiDataJson(file));
                    }else if(file.getAbsolutePath().contains("label.json")){
                        data.setLabelData(getLabelDataFromFile(file));
                    }
                }
                classificationDatas.add(data);
            }
        }

        return classificationDatas;
    }

    private static List<WiFiData> readWifiDataJson( File file) {
        List<WiFiData> wiFiDatas= null;
        if(file.isFile() && file.canRead()){
            try {
                String text = getStringFromFile(file);
                wiFiDatas = WiFiData.getWiFiDataCollectionFromJsonString(text);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        return wiFiDatas;
    }

    public static String getStringFromFile (File fl) throws Exception {
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static String getStringFromFilePath (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static Dataset loadIris(){
        Dataset data = null;
        try {

            File root = new File(Environment.getExternalStorageDirectory(), "IPS");
            String path = root.getAbsolutePath();
            data = FileHandler.loadDataset(new File(path + "/iris.data"), 4,
                    ",");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  data;
    }

}


