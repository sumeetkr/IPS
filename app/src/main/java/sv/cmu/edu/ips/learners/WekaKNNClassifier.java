package sv.cmu.edu.ips.learners;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

import sv.cmu.edu.ips.util.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

/**
 * Created by sumeet on 12/2/14.
 */
public class WekaKNNClassifier {
    public static void classify(){

        File root = new File(Environment.getExternalStorageDirectory(), "IPS");
        String path = root.getAbsolutePath();

        Instances insts = null;
        int percentSplit = 80;
        try {
            insts = new Instances(new java.io.FileReader(path+"/iris.arff"));
        insts.setClassIndex(insts.numAttributes() - 1);
        int trainSize = insts.numInstances() * percentSplit / 100;
        int testSize = insts.numInstances() - trainSize;
        weka.core.Instances train = new weka.core.Instances(insts, 0, trainSize);

//        weka.classifiers.Classifier cl = new weka.classifiers.trees.J48();
//        cl.buildClassifier(train);

        Classifier cl = new IBk();
        cl.buildClassifier(train);
        Logger.log("Performing " + percentSplit + "% split evaluation.");

        //randomize the order of the instances in the dataset.
//        weka.filters.Filter myRandom = new weka.filters.unsupervised.instance.Randomize();
//        myRandom.setInputFormat(insts);
//        insts = weka.filters.Filter.useFilter(insts, myRandom);

        int numCorrect = 0;
        for (int i = trainSize; i < insts.numInstances(); i++)
        {
            weka.core.Instance currentInst = insts.instance(i);
            double predictedClass = cl.classifyInstance(currentInst);
            if (predictedClass == insts.instance(i).classValue())
                numCorrect++;
        }
        Logger.log(numCorrect + " out of " + testSize + " correct (" +
                (double)((double)numCorrect / (double)testSize * 100.0) + "%)");


        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception ex){
            Logger.log(ex.getMessage());
        }
    }
}
