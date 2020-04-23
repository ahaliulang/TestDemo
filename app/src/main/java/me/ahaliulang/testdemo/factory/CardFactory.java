package me.ahaliulang.testdemo.factory;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * author:tdn
 * time:2020/1/20
 * description:
 */
public class CardFactory {
    public static Bitmap create(Context context, CardStyleInfo cardStyleInfo,Class<? extends ShareCard> clazz,int index) {
        Bitmap bitmap = null;
        try {
            ShareCard shareCard = (ShareCard) Class.forName(clazz.getName()).newInstance();
            bitmap = shareCard.generate(context,cardStyleInfo,index);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    private static Bitmap generateCardNoWorksStyle(Context context, CardStyleInfo cardStyleInfo) {
        return null;
    }

    private static Bitmap generateCardStyleOne(Context context, CardStyleInfo cardStyleInfo) {
        return null;
    }

    private static Bitmap generateCardStyleTwo(Context context, CardStyleInfo cardStyleInfo) {
        return null;
    }

    private static Bitmap generateCardStyleThree(Context context, CardStyleInfo cardStyleInfo) {
        return null;
    }

    private static Bitmap generateCardStyleFour(Context context, CardStyleInfo cardStyleInfo) {
        return null;
    }

    private static Bitmap generateCardStyleShareInterPhoto(Context context, CardStyleInfo cardStyleInfo) {
        return null;
    }

    public interface ShareCard {
        Bitmap generate(Context context, CardStyleInfo cardStyleInfo ,int index);
    }

    private static class MineShareCard implements ShareCard {

        @Override
        public Bitmap generate(Context context, CardStyleInfo cardStyleInfo, int index) {
            return null;
        }
    }


    private static class PublishShareCard implements ShareCard {

        @Override
        public Bitmap generate(Context context, CardStyleInfo cardStyleInfo, int index) {
            return null;
        }
    }


}
