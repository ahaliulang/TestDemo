package me.ahaliulang.testdemo.utils;



import java.security.MessageDigest;

/**
 * author:tdn
 * time:2019/12/10
 * description:
 */
public class Utils {


    public static String md5sum(String src)
    {
        MessageDigest md5;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(src.getBytes());
            return md5ToHexString(md5.digest());
        }
        catch(Exception e)
        {
            return "";
        }
    }



    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String md5ToHexString(byte[] b)
    { // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for(int i = 0; i < b.length; i++)
        {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }



    public static int getRealPixel(int pxSrc)
    {
        int pix = (int)(pxSrc * 3.0 / 2.0);
        if(pxSrc == 1 && pix == 0)
        {
            pix = 1;
        }
        return pix;
    }

    public static int getRealPixel2(int pxSrc)
    {
        return getRealPixel(pxSrc);
    }

    public static int getScreenW() {
        return 1080;
    }

    public static float formatData(float data, int accuracy)
    {
        float seed = 1.0f;
        while(accuracy > 0)
        {
            seed *= 10;
            accuracy--;
        }
        float result = (int)(data * seed) / seed;
        return result;
    }
}
