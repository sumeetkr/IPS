package sv.cmu.edu.ips.util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sumeet on 9/13/14.
 */
public class SignalAnalyzer {
    private static int noiseThresholdAmplitude = 480;
    private static int clockLengthGuess = 23;
    private static int thresholdNoOfZerosToCutSignal = 200;

    public static short[] lowPassFilter(short[] data) {
        short[] filteredData = data;

        for (int i = 0; i < filteredData.length; i++) {
            if (filteredData[i] < noiseThresholdAmplitude && filteredData[i] > -1 * noiseThresholdAmplitude) {
                filteredData[i] = 0;
            }
        }
        return filteredData;
    }

    public static short[] digitilizeData(short[] data) {
        short[] digitalData = data;
        for (int i = 0; i < digitalData.length; i++) {
            if (digitalData[i] >= 0) {
                digitalData[i] = 1;
            } else {
                digitalData[i] = 0;
            }

        }

        return digitalData;
    }

    public static short[] getSignalSegment(short[] filteredData, short[] rawData) {
        short[] filteredSignalData = filteredData;

        int startIndex = getSignalSegmentStartIndex(filteredSignalData, 0);
        int endIndex = getSignalSegmentEndIndex(filteredSignalData, startIndex);

        //usually the width of signal is more than 2000 data points
        if (endIndex - startIndex < 1500) {
            startIndex = getSignalSegmentStartIndex(filteredSignalData, startIndex);
            endIndex = getSignalSegmentEndIndex(filteredSignalData, startIndex);
        }

        short[] signalSegment = Arrays.copyOfRange(rawData, startIndex, endIndex);
        return signalSegment;

    }

    private static int getSignalSegmentStartIndex(short[] filteredSignalData, int indexToStartSearch) {
        int startIndex = indexToStartSearch + thresholdNoOfZerosToCutSignal;

        for (int i = indexToStartSearch + thresholdNoOfZerosToCutSignal; i < filteredSignalData.length; i++) {
            if (i > thresholdNoOfZerosToCutSignal && filteredSignalData[i] != 0) {

                //if all j are == 0 before i, then i is the starting index
                boolean allJsAreZero = true;
                for (int j = i - thresholdNoOfZerosToCutSignal; j < i; j++) {
                    if (allJsAreZero && filteredSignalData[j] != 0) {
                        allJsAreZero = false;
                    }
                }
                if (allJsAreZero) {
                    startIndex = i;
                    break;
                }
            }
        }
        return startIndex;
    }

    private static int getSignalSegmentEndIndex(short[] filteredSignalData,
                                                int startIndex) {
        int endIndex = 0;
        for (int i = startIndex; i < filteredSignalData.length; i++) {
            if (endIndex == 0 && filteredSignalData[i] == 0) {

                //if all j are == 0 before i, then i is the starting index
                boolean allJsAreZero = true;
                for (int j = i + 1; j < i + thresholdNoOfZerosToCutSignal; j++) {
                    if (allJsAreZero && filteredSignalData[j] != 0 && j < filteredSignalData.length) {
                        allJsAreZero = false;
                    }
                }
                if (allJsAreZero) endIndex = i;
            }
        }
        return endIndex;
    }

    public static short[] getManchesterEncodedValue(short[] data) {
        short[] digitalSignalData = data;
        String codedString = getManchesterEncodedString(digitalSignalData);

        short[] code = new short[codedString.length()];
        char[] chars = codedString.toCharArray();
        for (int i = 0; i < codedString.length(); i++) {
            code[i] = Short.valueOf(String.valueOf(chars[i]));
        }

        return code;
    }

    public static String getManchesterEncodedString(short[] digitalSignalData) {
        String codedValue = "";

        short lastValue = digitalSignalData[0];
        int repeatationCount = 0;
        for (int i = 0; i < digitalSignalData.length; i++) {
            if (lastValue == digitalSignalData[i]) {
                repeatationCount++;
            } else {

                if (repeatationCount < clockLengthGuess) {
                    codedValue = codedValue + String.valueOf(lastValue);
                } else {
                    codedValue = codedValue + String.valueOf(lastValue) + String.valueOf(lastValue);
                }
                repeatationCount = 0;
                lastValue = digitalSignalData[i];
            }
        }

        return codedValue;
    }

    public static String binaryToManchesterEncoding(String binaryString) {
        String encodedString = "";
        ArrayList<Integer> binaryIntegers = new ArrayList<Integer>();
        for (char chr : binaryString.toCharArray()) {
            Integer integer = Integer.decode(String.valueOf(chr));
            binaryIntegers.add(integer);
        }

        for (Integer integer : binaryIntegers) {
            if (integer.intValue() == 0) {
                encodedString = encodedString.concat("10");
            } else {
                encodedString = encodedString.concat("01");
            }
        }
        return encodedString;
    }

    public static String manchesterToBinaryDecoding(String manchesterString) throws Exception {
        String decodedString = "";
        //sometimes there is an extra 0 at the beginning
        if ("00".compareTo(manchesterString.substring(0, 2)) == 0) {
            manchesterString = manchesterString.substring(1);
        }
        if ("0".compareTo(manchesterString.substring(0, 2)) == 0) {
            manchesterString = manchesterString.substring(1);
        }

        ArrayList<Integer> binaryIntegers = new ArrayList<Integer>();
        for (char chr : manchesterString.toCharArray()) {
            Integer integer = Integer.decode(String.valueOf(chr));
            binaryIntegers.add(integer);
        }

        for (int i = 0; i < binaryIntegers.size() - 1; i = i + 2) {
            if (binaryIntegers.get(i).intValue() == 0 && binaryIntegers.get(i + 1).intValue() == 1) {
                decodedString = decodedString.concat("0");
            } else if (binaryIntegers.get(i).intValue() == 1 && binaryIntegers.get(i + 1).intValue() == 0) {
                decodedString = decodedString.concat("1");
            } else {
                throw new Exception("Incorrect format for Manchester decoding at index : " + i + "  " + manchesterString);
            }
        }

        return decodedString;
    }

    public static long getLongFromBinary(String binaryString) {
        Long val = Long.parseLong(binaryString, 2);
        return val;
    }

    public static String getBeconIdFromDecodedString(String decodedString) {
        String beconIdBinaryString = decodedString.substring(8, decodedString.length() - 8);
        String beaconId = String.valueOf(getLongFromBinary(beconIdBinaryString));

        return beaconId;
    }

    public static double doubleFromBinaryString(String binaryString) {
        double j = 0;
        for (int i = 0; i < binaryString.length(); i++) {
            if (binaryString.charAt(i) == '1') {
                j = j + Math.pow(2, binaryString.length() - 1 - i);
            }

        }
        return j;
    }

    public static String getBeaconIdFromRawSignal(short[] data) throws Exception {
        String beacodId = "";
        try {
            short[] dataCopy = data.clone();
            short[] filteredData = lowPassFilter(dataCopy);
            short[] signalData = getSignalSegment(filteredData, data);
            short[] digitalizedData = digitilizeData(signalData);
            String codedData = getManchesterEncodedString(digitalizedData);
            String decodedValue = manchesterToBinaryDecoding(codedData.substring(1));
            beacodId = String.valueOf(getBeconIdFromDecodedString(decodedValue));

        } catch (Exception e) {
            throw e;
        }

        return beacodId;
    }
}