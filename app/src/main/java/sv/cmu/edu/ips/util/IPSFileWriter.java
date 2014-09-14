package sv.cmu.edu.ips.util;

/**
 * Created by sumeet on 9/10/14.
 */

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class IPSFileWriter {
    private FileWriter writer;
    private String  TAG= "IPSFileWriter";

    public IPSFileWriter(String sFileName){
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "IPS");
            if (!root.exists())
            {
                root.mkdirs();
            }

            long epoch = System.currentTimeMillis()/1000;
            File gpxfile = new File(root, sFileName);
            writer = new FileWriter(gpxfile);
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    public  void appendText( String sBody)
    {
        try
        {
            if(writer != null){
                writer.append(sBody);
//                writer.write('\n');
                writer.flush();
            }
        }
        catch(IOException e)
        {
            Log.d(TAG, "RunningApplications: " + e.toString());
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            writer.flush();
            writer.close();
            writer = null;

        } catch (IOException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        super.finalize();

        if(writer != null){
            writer.close();
            writer = null;
        }
    }
}
