package sv.cmu.edu.ips.data;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sumeet on 11/30/14.
 */
public class ClassificationData {
    private List<WiFiData> wifiData;
    private HashMap<String, Double> mapOfWifiData;
    private LabelData labelData;

    public List<WiFiData> getWifiData() {
        return wifiData;
    }

    public void setWifiData(List<WiFiData> wifiData) {
        this.wifiData = wifiData;
    }

    public LabelData getLabelData() {
        return labelData;
    }

    public void setLabelData(LabelData labelData) {
        this.labelData = labelData;
    }

    public HashMap<String, Double> getWifiMap(){
        if(mapOfWifiData == null){
            mapOfWifiData = new HashMap<String, Double>();
            for(WiFiData data: wifiData){
                mapOfWifiData.put(data.getBSSID(), data.getLevel());
            }
        }
        return  mapOfWifiData;
    }

    public double compareMap(HashMap<String, Double> mapToCompare){
        double comparison = 0;

        HashMap<String, Double> ownMap = getWifiMap();
        for(String key:ownMap.keySet()){
            if(mapToCompare.containsKey(key)){
                comparison = comparison +5; //5 points if found

                double otherVal = mapToCompare.get(key);
                double val= ownMap.get(key);
                double diff = Math.abs(val-otherVal);

                comparison = comparison + Math.abs((5 + diff)/(1+diff));
            }
        }
        return comparison;
    }


}
