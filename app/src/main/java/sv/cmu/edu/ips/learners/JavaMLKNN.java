package sv.cmu.edu.ips.learners;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.sampling.Sampling;
import net.sf.javaml.tools.InstanceTools;

import be.abeel.util.Pair;
import libsvm.LibSVM;
import sv.cmu.edu.ips.util.IPSFileReader;
import sv.cmu.edu.ips.util.Logger;

/**
 * Created by sumeet on 12/2/14.
 */
public class JavaMLKNN {

    public enum Classifiers {
        KNearestNeighbors, LibSVM;
    }

    public static void classify(Classifiers classifier){
        try{
            Classifier cfr = null;
            switch (classifier) {
                case LibSVM:
                    cfr = new LibSVM();
                    break;
                case KNearestNeighbors:
                    cfr = new KNearestNeighbors(5);
                    break;
                default:
                    cfr = new KNearestNeighbors(5);
            }

            double trainingDataPercentage = 0.8;
            int correct =0;
            int wrong =0;

            Dataset dataSetCollection = IPSFileReader.loadIris(); //loadData("",20);
            cfr.buildClassifier(loadData("",20));
            Dataset dataForClassification = IPSFileReader.loadIris(); ;//loadData("", 5);

            Sampling sampler = Sampling.SubSampling;
            Pair<Dataset, Dataset> datas;
            datas = sampler
                    .sample(dataSetCollection,
                            (int) (dataSetCollection.size() * trainingDataPercentage),
                            5);

            for (Instance inst : datas.y()) {
                Object predictedClassValue = cfr.classify(inst);
                Object realClassValue = inst.classValue();
                if (predictedClassValue.equals(realClassValue))
                    correct++;
                else
                    wrong++;
            }

//            Map<Object, PerformanceMeasure> pm = EvaluateDataset.testDataset(cfr, dataForClassification);
//            for(Object o:pm.keySet()){
//                Logger.log(o+": "+pm.get(o).getAccuracy());
//            }
//                System.out.println(o+": "+pm.get(o).getAccuracy());

        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
    }

    public static Dataset loadData(String path, int count){
        Dataset data = new DefaultDataset();
        for (int i = 0; i < count; i++) {
            Instance tmpInstance = InstanceTools.randomInstance(count);
            data.add(tmpInstance);
        }
        return data;
    }
}
