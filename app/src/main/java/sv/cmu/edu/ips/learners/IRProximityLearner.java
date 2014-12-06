package sv.cmu.edu.ips.learners;

import java.util.List;

import sv.cmu.edu.ips.data.ClassificationData;
import sv.cmu.edu.ips.data.LabelData;

/**
 * Created by sumeet on 12/5/14.
 */
public class IRProximityLearner {

    public static LabelData findNearestLabel(List<ClassificationData> oldData, String beaconId) {
        //                find which first label has the same beacon id
//                should later be modified to use the mean of those values

        LabelData closestlabel = null;
        for(ClassificationData classificationData:oldData){
            LabelData label = classificationData.getLabelData();
            if(label!= null && label.getBeaconId() != null && !label.getBeaconId().isEmpty()){
             if(label.getBeaconId().compareTo(beaconId)==0){
                 closestlabel = label;
                 break;
             }
            }
        }

        //lets assume IR has 4 meter accuracy
        if(closestlabel != null) {
            closestlabel.setAccuracyInMeter(4);
            closestlabel.setBestSource(LabelData.Source.IR);
        }
        return  closestlabel;
    }
}
