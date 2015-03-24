package jamajav;

class EightSixteen {

    // Audio processing utility routines:
    public static int getSixteenBitSample(int high, int low) {
        return (high << 8) + (low & 0x00ff);            
    }

    public static byte[] getEightBitPair(int sixteenBitSample) {
        byte[] highLow = new byte[2];
        highLow[0] = (byte)(sixteenBitSample & 0xff); 
        highLow[1] = (byte)((sixteenBitSample >> 8) & 0xff);
        return highLow;
    }

   
    public static int[] toSixteen(byte[] bytes) {

        int[] toReturn = new int[bytes.length/2];

        int sampleIndex = 0;
        for (int t = 0; t < bytes.length;) {
            int low = (int) bytes[t];
            t++;
            int high = (int) bytes[t];
            t++;
            int sample = getSixteenBitSample(high, low);
            toReturn[sampleIndex] = sample;
            sampleIndex++;
        }
        return toReturn;
    }
    
    public static byte[] toEight(int[] sixteen) {
        // inverse of previous routine (must think about it!)

        byte[] toReturn = new byte[2*sixteen.length];
        for (int i = 0; i < sixteen.length; i++) {
            byte[] highLow = getEightBitPair(sixteen[i]);
            toReturn[2*i] = highLow[0];
            toReturn[2*i+1] = highLow[1];
        }
        return toReturn;
    }

    public static byte[] addEights(byte[] eight1, double weight1, byte[] eight2, double weight2) {
        int[] sixteen1 = toSixteen(eight1);
        int[] sixteen2 = toSixteen(eight2);

        boolean oneIsLonger = (sixteen1.length > sixteen2.length);

        int[] sixteenCombine = new int[Math.max(sixteen1.length, sixteen2.length)];

        // Add the overlapping frames
        for (int i=0; i < Math.min(sixteen1.length,sixteen2.length); i++)
            sixteenCombine[i] = (int)(weight1*(double)(sixteen1[i])
                    + weight2*(double)(sixteen2[i]));

        // Copy the tail of the longer array
        if (oneIsLonger)
            for (int i=sixteen2.length; i < sixteen1.length; i++)
                sixteenCombine[i] = (int)(weight1*(double)(sixteen1[i]));
        else
            for (int i=sixteen1.length; i < sixteen2.length; i++)
                sixteenCombine[i] = (int)(weight2*(double)(sixteen2[i]));

        // return the combined array as eight byte pairs
        return toEight(sixteenCombine);
    }

}

