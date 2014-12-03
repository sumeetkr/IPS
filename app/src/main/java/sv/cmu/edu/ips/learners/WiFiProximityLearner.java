package sv.cmu.edu.ips.learners;

import java.util.List;

import sv.cmu.edu.ips.data.ClassificationData;
import sv.cmu.edu.ips.data.LabelData;

/**
 * Created by sumeet on 11/30/14.
 */
public class WiFiProximityLearner {
    public static LabelData findNearestLabel(List<ClassificationData> oldData, ClassificationData newData) {
        LabelData bestLabel = new LabelData();
        double maxClose = 0;

        if(oldData.size()>0){
            bestLabel = oldData.get(0).getLabelData();
            for(ClassificationData clssData:oldData){
                double closeness = clssData.compareMap(newData.getWifiMap());
                if(closeness>maxClose){
                    bestLabel = clssData.getLabelData();
                    maxClose = closeness;
                }
            }
        }
        return bestLabel;
    }
}
